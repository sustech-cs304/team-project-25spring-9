# 使用 Python 3.9 作为基础镜像
FROM python:3.9

# 安装基本工具
RUN apt-get update && apt-get install -y \
    cmake \
    build-essential \
    ninja-build \
    git \
    && rm -rf /var/lib/apt/lists/*
# 设置工作目录
WORKDIR /app
COPY requirements.txt .
# 安装 Python 依赖
RUN pip install -r requirements.txt
# 复制当前目录下所有文件到容器内
COPY . /app



# 暴露 8123 端口
EXPOSE 8123

# 运行 Python 应用
CMD ["python", "process_image.py"]
