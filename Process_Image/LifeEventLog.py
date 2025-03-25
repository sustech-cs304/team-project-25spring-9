import os
import piexif
from PIL import Image
from transformers import BlipProcessor, BlipForConditionalGeneration
from geopy.geocoders import Nominatim
from datetime import datetime
import json

# 初始化模型和地理定位器
processor = BlipProcessor.from_pretrained("Salesforce/blip-image-captioning-base")
model = BlipForConditionalGeneration.from_pretrained("Salesforce/blip-image-captioning-base")
geolocator = Nominatim(user_agent="event_retrieval_system")

# 文件夹路径（图片存放位置）
IMAGE_FOLDER = "your_image_folder"

# 存储事件信息
events = []

# 将GPS坐标转为可读地址
def get_location_name(lat, lon):
    try:
        location = geolocator.reverse((lat, lon), language='en')
        return location.address if location else "Unknown location"
    except Exception as e:
        return "Unknown location"

# 提取EXIF信息（时间和GPS）
def extract_exif(image_path):
    try:
        exif_data = piexif.load(image_path)
        # 时间
        date_time = exif_data["0th"].get(piexif.ImageIFD.DateTime)
        date_time_str = date_time.decode() if date_time else "Unknown time"

        # GPS信息
        gps_data = exif_data.get("GPS", {})
        lat = lon = None
        if gps_data:
            def dms_to_dd(d, m, s, ref):
                dd = d + m / 60 + s / 3600
                if ref in ['S', 'W']:
                    dd *= -1
                return dd

            gps_lat = gps_data.get(piexif.GPSIFD.GPSLatitude)
            gps_lat_ref = gps_data.get(piexif.GPSIFD.GPSLatitudeRef)
            gps_lon = gps_data.get(piexif.GPSIFD.GPSLongitude)
            gps_lon_ref = gps_data.get(piexif.GPSIFD.GPSLongitudeRef)

            if gps_lat and gps_lat_ref and gps_lon and gps_lon_ref:
                lat = dms_to_dd(gps_lat[0][0]/gps_lat[0][1],
                                gps_lat[1][0]/gps_lat[1][1],
                                gps_lat[2][0]/gps_lat[2][1],
                                gps_lat_ref.decode())

                lon = dms_to_dd(gps_lon[0][0]/gps_lon[0][1],
                                gps_lon[1][0]/gps_lon[1][1],
                                gps_lon[2][0]/gps_lon[2][1],
                                gps_lon_ref.decode())

        return date_time_str, lat, lon
    except Exception as e:
        print(f"EXIF extraction error for {image_path}: {e}")
        return "Unknown time", None, None

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

# 处理文件夹下所有图片
def process_images(folder_path):
    for filename in os.listdir(folder_path):
        if filename.lower().endswith(('jpg', 'jpeg', 'png')):
            img_path = os.path.join(folder_path, filename)

            # 提取元信息
            date_time_str, lat, lon = extract_exif(img_path)
            location_name = get_location_name(lat, lon) if lat and lon else "Location Unknown"

            # 生成描述
            caption = generate_caption(img_path)

            # 整理事件数据
            event = {
                "image": filename,
                "datetime": date_time_str,
                "location": location_name,
                "caption": caption
            }
            print(f"Processed {filename}: {caption} @ {location_name} on {date_time_str}")
            events.append(event)

# 日志输出为 JSON 和 Markdown
def output_logs(events, json_file='event_log.json', md_file='event_log.md'):
    # 按时间排序
    def parse_date(date_str):
        try:
            return datetime.strptime(date_str, "%Y:%m:%d %H:%M:%S")
        except:
            return datetime.min

    events_sorted = sorted(events, key=lambda x: parse_date(x["datetime"]))

    # 保存JSON
    with open(json_file, 'w', encoding='utf-8') as f:
        json.dump(events_sorted, f, indent=4, ensure_ascii=False)

    # 保存Markdown
    with open(md_file, 'w', encoding='utf-8') as f:
        f.write("# 📸 Life Event Log\n\n")
        for event in events_sorted:
            f.write(f"## {event['datetime']}\n")
            f.write(f"- **Location**: {event['location']}\n")
            f.write(f"- **Description**: {event['caption']}\n")
            f.write(f"- ![Image]({event['image']})\n\n")

# 主程序
process_images(IMAGE_FOLDER)
output_logs(events)

print("日志生成完毕！查看 event_log.json 和 event_log.md")
