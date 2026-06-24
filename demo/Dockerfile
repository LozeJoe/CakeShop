# ── 阶段1: Maven 构建 (JDK 11 + 阿里云镜像加速) ──
FROM maven:3.9-eclipse-temurin-11-alpine AS builder
WORKDIR /build

# 阿里云 Maven 镜像加速（直接写入，无需额外文件）
RUN echo '<?xml version="1.0" encoding="UTF-8"?><settings><mirrors><mirror><id>aliyun</id><mirrorOf>central</mirrorOf><name>aliyun</name><url>https://maven.aliyun.com/repository/public</url></mirror></mirrors></settings>' > /usr/share/maven/conf/settings.xml

COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
RUN mvn dependency:go-offline -B 2>/dev/null || true
COPY src ./src
RUN mvn package -DskipTests -B

# ── 阶段2: 运行时 ──
FROM eclipse-temurin:11-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8090
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-jar", "app.jar"]
