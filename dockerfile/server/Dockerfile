# 使用 OpenJDK 基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 将 jar 文件复制到容器中
COPY BackEnd-1.0-SNAPSHOT.jar app.jar

# 暴露端口（根据你的 Spring Boot 项目设置的端口）
EXPOSE 9090

# 启动 Spring Boot 应用
ENTRYPOINT ["java", "-jar", "app.jar"]
