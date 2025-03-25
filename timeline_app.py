import streamlit as st
import json
from datetime import datetime
from PIL import Image

# 页面设置
st.set_page_config(page_title="📅 Life Event Timeline", layout="wide")
st.title("📸 Life Event Timeline")
st.markdown("Welcome to your personal event timeline. Scroll through your life moments! ✨")


# 读取事件日志
def load_events(json_file):
    with open(json_file, 'r', encoding='utf-8') as f:
        events = json.load(f)
    return events


# 排序事件（按时间）
def sort_events(events):
    def parse_date(event):
        try:
            return datetime.strptime(event['datetime'], "%Y:%m:%d %H:%M:%S")
        except:
            return datetime.min

    return sorted(events, key=parse_date)


# 显示事件时间轴
def display_timeline(events):
    for event in events:
        # 时间和地点
        st.subheader(event['datetime'])
        st.markdown(f"📍 **Location**: {event['location']}")
        # 图片和描述
        cols = st.columns([1, 2])
        with cols[0]:
            try:
                img = Image.open(event['image'])
                st.image(img, width=300)
            except Exception as e:
                st.warning(f"Image not found: {event['image']}")
        with cols[1]:
            st.markdown(f"📝 **Description**: {event['caption']}")

        st.markdown("---")


# 主逻辑
events = load_events("event_log.json")
events_sorted = sort_events(events)

display_timeline(events_sorted)
