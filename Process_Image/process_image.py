import os
import shutil
import json
import face_recognition
import numpy as np
import piexif
from datetime import datetime
from geopy.geocoders import Nominatim
from transformers import BlipProcessor, BlipForConditionalGeneration
import torch
import spacy
from spacy.cli import download
from fastapi import FastAPI, UploadFile, File
from fastapi.responses import JSONResponse
import uuid
import uvicorn

from typing import List
import gc
import random
import chardet
from PIL import Image, ImageFilter
from moviepy.editor import (
    ImageSequenceClip, AudioFileClip, CompositeVideoClip,
    concatenate_videoclips, VideoFileClip, vfx
)
from fastapi.responses import FileResponse




PHOTO_DIR = "photos/"
SORTED_DIR = "sorted_photos/"
ENCODINGS_FILE = "face_encodings.json"
# 全局变量
processor = None
model = None
nlp = None




# os.makedirs(SORTED_DIR, exist_ok=True)

# ==========================
# 🌟 加载已有的人脸数据（JSON 文件）
# ==========================
def load_encodings():
    global next_label_id
    if not os.path.exists(ENCODINGS_FILE):
        print("📂 没有检测到现有的人脸数据库，初始化为空。")
        return {}, [], []

    with open(ENCODINGS_FILE, 'r') as f:
        data = json.load(f)

    known_face_dict = {}  # {label: encoding (np array)}
    known_face_encodings = []
    known_face_labels = []

    for label, encoding_list in data.items():
        label_int = int(label)
        encoding_array = np.array(encoding_list)
        known_face_dict[label_int] = encoding_array
        known_face_encodings.append(encoding_array)
        known_face_labels.append(label_int)

    next_label_id = max(known_face_dict.keys(), default=0) + 1
    print(f"✅ 成功加载 {len(known_face_dict)} 个已知人物数据。")
    return known_face_dict, known_face_encodings, known_face_labels



# ==========================
# 🌟 保存人脸编码到 JSON
# ==========================
def save_encodings(known_face_dict):
    data_to_save = {}
    for label, encoding_array in known_face_dict.items():
        data_to_save[label] = encoding_array.tolist()  # 转成 JSON 可保存的 list

    with open(ENCODINGS_FILE, 'w') as f:
        json.dump(data_to_save, f)

    print(f"💾 已保存 {len(data_to_save)} 个人物的编码数据到 {ENCODINGS_FILE}")


# 初始化函数
def init_blip_and_spacy():
    global processor, model, nlp  # 引用全局变量
    try:
        print("正在加载 BLIP 模型和处理器...")
        processor = BlipProcessor.from_pretrained("Salesforce/blip-image-captioning-base")
        model = BlipForConditionalGeneration.from_pretrained("Salesforce/blip-image-captioning-base")
        print("BLIP 模型和处理器加载完成！")
    except Exception as e:
        print(f"加载 BLIP 模型时出错: {e}")
        processor, model = None, None

    try:
        print("正在检测和加载 SpaCy 英文模型...")
        download("en_core_web_sm")  # 只下载一次
        nlp = spacy.load("en_core_web_sm")
        print("SpaCy 英文模型加载完成！")
    except Exception as e:
        print(f"加载 SpaCy 模型时出错: {e}")
        nlp = None


# ==========================
# 🌟 处理单张图片
# ==========================
def process_new_photo(photo_path):
    global next_label_id
    try:
        # 1. 打开并转换图片
        with Image.open(photo_path) as img:
            img = img.convert("RGB")
            image_array = np.array(img)

        # 2. 人脸识别
        encodings = face_recognition.face_encodings(image_array)

        if not encodings:
            print(f"⚠️ 图片 {photo_path} 不包含人脸，跳过处理。")
            return

        print(f"✅ 识别到 {len(encodings)} 张人脸，开始分类...")

        for encoding in encodings:
            if len(known_face_encodings) == 0:
                # 没有人脸数据，直接新建人物
                person_label = next_label_id
                next_label_id += 1

                known_face_dict[person_label] = encoding
                known_face_encodings.append(encoding)
                known_face_labels.append(person_label)
                print(f"🆕 未检测到已有人物，新建人物 {person_label}")

            else:
                # 比较与已知人脸的相似度
                distances = face_recognition.face_distance(known_face_encodings, encoding)
                min_distance = np.min(distances)
                best_match_index = np.argmin(distances)

                if min_distance < 0.4:  # 阈值可以调整
                    person_label = known_face_labels[best_match_index]
                    # print(f"👌 匹配到人物 {person_label}，距离为 {min_distance:.2f}")
                else:
                    # 新人物
                    person_label = next_label_id
                    next_label_id += 1

                    known_face_dict[person_label] = encoding
                    known_face_encodings.append(encoding)
                    known_face_labels.append(person_label)
                    # print(f"🆕 新建人物 {person_label}，距离为 {min_distance:.2f}")

            # 保存照片到分类目录
            person_folder = os.path.join(SORTED_DIR, f"人物{person_label}")
            os.makedirs(person_folder, exist_ok=True)
            shutil.copy(photo_path, person_folder)

        # 处理完图片，保存最新的 encodings
        save_encodings(known_face_dict)
        return person_label

    except Exception as e:
        print(f"❌ 处理 {photo_path} 时出错: {e}")


def get_decimal_from_dms(dms, ref):
    """Convert GPS coordinates in DMS to decimal format."""
    degrees = dms[0][0] / dms[0][1]
    minutes = dms[1][0] / dms[1][1]
    seconds = dms[2][0] / dms[2][1]

    decimal = degrees + (minutes / 60.0) + (seconds / 3600.0)

    if ref in ['S', 'W']:
        decimal = -decimal
    return decimal


def reverse_geocode(lat, lon):
    """Use geopy to reverse geocode latitude and longitude to address."""
    geolocator = Nominatim(user_agent="photo_metadata_app")
    try:
        location = geolocator.reverse((lat, lon), exactly_one=True, language='en')
        if location:
            return location.address
        else:
            return "Address not found"
    except Exception as e:
        return f"Error retrieving address: {e}"


def extract_exif_data(image_path):
    # 打开图片文件
    img = Image.open(image_path)

    # 获取 EXIF 信息
    exif_data = img._getexif()

    # 提取拍摄时间
    timestamp = None
    if exif_data and 36867 in exif_data:
        timestamp = exif_data[36867]  # DateTimeOriginal
        timestamp = datetime.strptime(timestamp, '%Y:%m:%d %H:%M:%S')

    # 使用 piexif 加载更详细的 EXIF 数据
    exif_dict = piexif.load(img.info['exif']) if 'exif' in img.info else None

    # 提取相机/设备信息
    camera_model = None
    if exif_dict:
        model = exif_dict['0th'].get(piexif.ImageIFD.Model, None)
        make = exif_dict['0th'].get(piexif.ImageIFD.Make, None)

        camera_model = ""
        if make:
            camera_model += make.decode('utf-8') + " "
        if model:
            camera_model += model.decode('utf-8')

    # 提取 GPS 信息
    gps_info = exif_dict.get('GPS', None) if exif_dict else None
    latitude = longitude = None
    address = None
    if gps_info:
        gps_latitude = gps_info.get(piexif.GPSIFD.GPSLatitude)
        gps_latitude_ref = gps_info.get(piexif.GPSIFD.GPSLatitudeRef).decode('utf-8')
        gps_longitude = gps_info.get(piexif.GPSIFD.GPSLongitude)
        gps_longitude_ref = gps_info.get(piexif.GPSIFD.GPSLongitudeRef).decode('utf-8')

        if gps_latitude and gps_latitude_ref and gps_longitude and gps_longitude_ref:
            latitude = get_decimal_from_dms(gps_latitude, gps_latitude_ref)
            longitude = get_decimal_from_dms(gps_longitude, gps_longitude_ref)

            # 反向地理编码获取地址
            address = reverse_geocode(latitude, longitude)

    # 返回提取的信息
    return {
        'Timestamp': timestamp,
        'Latitude': latitude,
        'Longitude': longitude,
        'Address': address,
        'Camera/Device': camera_model
    }


# 生成图片描述
def generate_caption(image_path):
    try:
        raw_image = Image.open(image_path).convert('RGB')
        inputs = processor(raw_image, return_tensors="pt")
        out = model.generate(**inputs)
        caption = processor.decode(out[0], skip_special_tokens=True)
        return caption
    except Exception as e:
        print(f"Caption generation error for {image_path}: {e}")
        return "No description available."


def extract_noun_tags(text):
    """
    提取英文文本中的关键词（名词和专有名词）

    参数:
        text (str): 输入的英文文本

    返回:
        tags (list): 去重且小写处理后的关键词列表
    """
    if not text:
        return []

    # 分词 & 词性标注
    doc = nlp(text)

    # 只保留名词（NOUN）和专有名词（PROPN）
    tags = [token.text for token in doc if token.pos_ in ['NOUN', 'PROPN']]

    # 去重 & 小写（可根据需求取消小写处理）
    tags = list(set(tag.lower() for tag in tags))

    print(f"Image event tags: {tags}")

    return tags


# 处理图片
def process_images(img_path):
    # 提取元信息
    metadata = extract_exif_data(img_path)

    # 生成描述
    caption = generate_caption(img_path)

    # Aoto-tagging
    tags = extract_noun_tags(caption)

    # 人脸识别
    person_label = process_new_photo(img_path)

    # ✅ 平铺 JSON 格式
    photo_info = {
        'Timestamp': metadata.get('Timestamp').strftime('%Y-%m-%d %H:%M:%S') if isinstance(metadata.get('Timestamp'),
                                                                                           datetime) else metadata.get(
            'Timestamp'),
        'Latitude': metadata.get('Latitude'),
        'Longitude': metadata.get('Longitude'),
        'Address': metadata.get('Address'),
        'Camera/Device': metadata.get('Camera/Device'),
        'Caption': caption,
        'AutoTags': tags,
        'PersonLabel': person_label
    }

    print(f"Processed {img_path}: {caption} @ {photo_info.get('Address')} on {photo_info.get('Timestamp')}")

    return photo_info

def get_config():
    config_file = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'config.json')
    with open(config_file, 'rb') as f:
        encoding_result = chardet.detect(raw_data := f.read())
        encoding = encoding_result['encoding']
    return json.loads(raw_data.decode(encoding))

def transform_image(img, t, x_speed, y_speed, move_on_x, move_positive):
    original_size = img.size
    crop_width = img.width * 0.8
    crop_height = img.height * 0.8
    if move_on_x:
        left = min(x_speed * t, img.width - crop_width) if move_positive else max(img.width - crop_width - x_speed * t, 0)
        upper = (img.height - crop_height) / 2
    else:
        upper = min(y_speed * t, img.height - crop_height) if move_positive else max(img.height - crop_height - y_speed * t, 0)
        left = (img.width - crop_width) / 2
    cropped_img = img.crop((left, upper, left + crop_width, upper + crop_height))
    return cropped_img.resize(original_size)

def generate_video(image_paths: list[str]) -> str:
    current_dir = os.path.dirname(os.path.abspath(__file__))
    config = get_config()

    temp_dir = os.path.join(current_dir, 'temp')
    video_dir = os.path.join(current_dir, 'video')
    voice_path = os.path.join(current_dir, 'bgm', 'bgm.mp3')

    os.makedirs(temp_dir, exist_ok=True)
    os.makedirs(video_dir, exist_ok=True)

    fps = config['fps']
    enlarge_background = config['enlarge_background']
    duration = config['duration']

    bgm_audio = AudioFileClip(voice_path)
    clips = []

    for idx, img_path in enumerate(image_paths):
        im = Image.open(img_path)
        effect_type = random.choice([0, 1])
        if effect_type == 0:
            x_speed = (im.width - im.width * 0.8) / duration
            y_speed = 0
            move_on_x = True
            move_positive = random.choice([True, False])
        else:
            x_speed = 0
            y_speed = (im.height - im.height * 0.8) / duration
            move_on_x = False
            move_positive = random.choice([True, False])

        n_frames = int(fps * duration)
        frames_foreground = [np.array(transform_image(im, t / fps, x_speed, y_speed, move_on_x, move_positive)) for t in range(n_frames)]
        img_foreground = ImageSequenceClip(frames_foreground, fps=fps)

        img_blur = im.filter(ImageFilter.GaussianBlur(radius=30))
        if enlarge_background:
            img_blur = img_blur.resize((int(im.width * 1.1), int(im.height * 1.1)), Image.Resampling.LANCZOS)
        frames_background = [np.array(img_blur)] * n_frames
        img_background = ImageSequenceClip(frames_background, fps=fps)

        final_clip = CompositeVideoClip(
            [img_background.set_position("center"), img_foreground.set_position("center")],
            size=img_blur.size
        )

        final_clip = final_clip.set_duration(duration)
        temp_clip_path = os.path.join(temp_dir, f'temp_{idx}.mp4')
        final_clip.write_videofile(temp_clip_path, logger=None)
        clips.append(VideoFileClip(temp_clip_path))
        gc.collect()

    final_video = concatenate_videoclips(clips, method="compose")
    final_video = final_video.set_audio(bgm_audio.subclip(0, final_video.duration))
    output_path = os.path.join(video_dir, f'output_{datetime.now().strftime("%Y%m%d%H%M%S")}.mp4')
    final_video.write_videofile(output_path, logger=None)
    return output_path


if __name__ == '__main__':
    # ==========================
    # 🌟 初始化已知人脸数据
    # ==========================
    known_face_dict, known_face_encodings, known_face_labels = load_encodings()

    # 调用初始化函数
    # 调用初始化（只执行一次）
    init_blip_and_spacy()
    app = FastAPI()

    @app.post("/process_image")
    async def process_image(file: UploadFile = File(...)):
        try:
            # 创建临时路径，防止文件名冲突
            temp_filename = f"temp_{uuid.uuid4().hex}_{file.filename}"

            # 保存文件到临时路径
            with open(temp_filename, "wb") as buffer:
                shutil.copyfileobj(file.file, buffer)

            # 调用你的图片处理函数（传路径）
            result = process_images(temp_filename)

            # 删除临时文件，避免堆积
            os.remove(temp_filename)

            # 返回结果
            return JSONResponse(content=result)

        except Exception as e:
            return JSONResponse(status_code=500, content={"error": str(e)})


    UPLOAD_FOLDER = os.path.join(os.path.dirname(__file__), "temp")
    # UPLOAD_FOLDER = os.path.join(os.getcwd(), "image")
    os.makedirs(UPLOAD_FOLDER, exist_ok=True)


    @app.post("/extract_exif/")
    async def extract_exif_api(file: UploadFile = File(...)):
        file_path = f"temp_{file.filename}"
        with open(file_path, "wb") as f:
            f.write(await file.read())
        metadata = extract_exif_data(file_path)
        os.remove(file_path)
        return JSONResponse(metadata)


    @app.post("/generate_caption/")
    async def caption_api(file: UploadFile = File(...)):
        file_path = f"temp_{file.filename}"
        with open(file_path, "wb") as f:
            f.write(await file.read())
        caption = generate_caption(file_path)
        os.remove(file_path)
        return {"caption": caption}


    @app.post("/auto_tag/")
    async def auto_tag_api(file: UploadFile = File(...)):
        file_path = f"temp_{file.filename}"
        with open(file_path, "wb") as f:
            f.write(await file.read())
        caption = generate_caption(file_path)
        tags = extract_noun_tags(caption)
        os.remove(file_path)
        return {"tags": tags}


    @app.post("/face_recognition/")
    async def face_recognition_api(file: UploadFile = File(...)):
        file_path = f"temp_{file.filename}"
        with open(file_path, "wb") as f:
            f.write(await file.read())
        person_label = process_new_photo(file_path)
        os.remove(file_path)
        return {"person_label": person_label}


    @app.post("/generate_video/")
    async def generate_video_api(files: List[UploadFile] = File(...)):
        saved_files = []

        for file in files:
            file_path = os.path.join(UPLOAD_FOLDER, file.filename)
            with open(file_path, "wb") as f:
                content = await file.read()
                f.write(content)
            saved_files.append(file_path)

        try:
            output_video_path = generate_video(saved_files)
            return FileResponse(output_video_path, media_type="video/mp4", filename=os.path.basename(output_video_path))
        finally:
            # 清理临时文件
            for path in saved_files:
                os.remove(path)


    uvicorn.run(app, host="0.0.0.0", port=8123)