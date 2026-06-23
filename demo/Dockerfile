# ── 阶段1: Maven 构建 ──
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /build
COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
# 先下载依赖（利用 Docker 缓存层）
RUN mvn dependency:go-offline -B 2>/dev/null || true
COPY src ./src
RUN mvn package -DskipTests -B

# ── 阶段2: 运行时 ──
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8090
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-jar", "app.jar"]
