# 使用 Java 17 基础镜像（兼容 Java 12 编译的 jar）
FROM eclipse-temurin:17-jre-alpine

# 设置工作目录
WORKDIR /app

# 复制 Maven 构建后的 JAR 文件
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8090

# 设置环境变量（可通过 docker-compose 覆盖）
ENV SPRING_PROFILES_ACTIVE=prod

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
