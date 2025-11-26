# FFmpeg Setup Guide for Docker Deployment

## 📋 Overview
This guide explains how to set up FFmpeg for the Shoes Admin Backend application when deploying with Docker.

## 🐳 Docker Setup

### Option 1: Install FFmpeg in Dockerfile

Add FFmpeg installation to your `Dockerfile`:

```dockerfile
FROM openjdk:17-jdk-slim

# Install FFmpeg
RUN apt-get update && \
    apt-get install -y ffmpeg && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Verify FFmpeg installation
RUN ffmpeg -version

# Copy application
COPY target/*.jar app.jar

# Set environment variable for FFmpeg path
ENV FFMPEG_PATH=/usr/bin/ffmpeg

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Option 2: Using Alpine Linux (Smaller Image)

```dockerfile
FROM openjdk:17-jdk-alpine

# Install FFmpeg
RUN apk update && \
    apk add --no-cache ffmpeg

# Copy application
COPY target/*.jar app.jar

# Set environment variable
ENV FFMPEG_PATH=/usr/bin/ffmpeg

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Option 3: Multi-stage Build with Ubuntu

```dockerfile
# Build stage
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM ubuntu:22.04

# Install OpenJDK and FFmpeg
RUN apt-get update && \
    apt-get install -y \
    openjdk-17-jre-headless \
    ffmpeg \
    && rm -rf /var/lib/apt/lists/*

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Set FFmpeg path
ENV FFMPEG_PATH=/usr/bin/ffmpeg

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🔧 Docker Compose Configuration

Create or update `docker-compose.yml`:

```yaml
version: '3.8'

services:
  shoes-admin-backend:
    build: .
    container_name: shoes-admin-backend
    ports:
      - "8082:8082"
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - FFMPEG_PATH=/usr/bin/ffmpeg
      - TEMP_DIR=/tmp/video-processing
    volumes:
      - ./uploads:/app/uploads
      - ./temp:/tmp/video-processing
    restart: unless-stopped
    networks:
      - shoes-network

networks:
  shoes-network:
    driver: bridge
```

## 📝 Application Configuration

Update your `application-production.yml`:

```yaml
# Video processing configuration
video:
  processing:
    ffmpeg-path: ${FFMPEG_PATH:/usr/bin/ffmpeg}
    thumbnail:
      width: 320
      height: 180
      position: 5
    encoding:
      bitrate: 2000k
      resolution: 1280x720

# File upload configuration
file:
  upload:
    temp-dir: ${TEMP_DIR:/tmp/video-processing}
    max-file-size: 104857600  # 100MB
    allowed-video-types: video/mp4,video/webm,video/quicktime,video/x-msvideo

# Multipart configuration
spring:
  servlet:
    multipart:
      enabled: true
      max-file-size: 100MB
      max-request-size: 100MB
      location: ${TEMP_DIR:/tmp/video-processing}
```

## 🚀 Deployment Commands

### Build and Run with Docker

```bash
# Build Docker image
docker build -t shoes-admin-backend .

# Run container
docker run -d \
  --name shoes-admin-backend \
  -p 8082:8082 \
  -e SPRING_PROFILES_ACTIVE=production \
  -e FFMPEG_PATH=/usr/bin/ffmpeg \
  -v $(pwd)/uploads:/app/uploads \
  -v $(pwd)/temp:/tmp/video-processing \
  shoes-admin-backend
```

### Using Docker Compose

```bash
# Start services
docker-compose up -d

# View logs
docker-compose logs -f shoes-admin-backend

# Stop services
docker-compose down
```

## ✅ Verification

### Check FFmpeg Installation in Container

```bash
# Enter container
docker exec -it shoes-admin-backend /bin/bash

# Check FFmpeg version
ffmpeg -version

# Check FFmpeg path
which ffmpeg

# Test FFmpeg
ffmpeg -f lavfi -i testsrc=duration=5:size=320x240:rate=30 /tmp/test.mp4
```

### Test API Endpoint

```bash
# Health check
curl http://localhost:8082/health

# Test video composition (adjust with actual files)
curl -X POST http://localhost:8082/api/admin/video-editor/compose \
  -F "media=@test-image.jpg" \
  -F "media=@test-video.mp4" \
  -F 'overlayData=[{"id":"1","x":0,"y":0,"width":540,"height":960,"mediaType":"image"},{"id":"2","x":540,"y":0,"width":540,"height":960,"mediaType":"video","hasAudio":false}]' \
  -F "duration=10" \
  -o output.mp4
```

## 🔍 Troubleshooting

### Common Issues

#### 1. FFmpeg Not Found
```bash
# Check if FFmpeg is installed in container
docker exec shoes-admin-backend which ffmpeg

# If not found, rebuild image
docker-compose build --no-cache
```

#### 2. Permission Denied
```bash
# Fix permissions for temp directory
docker exec shoes-admin-backend chmod 777 /tmp/video-processing
```

#### 3. Out of Memory
```bash
# Increase Docker memory limit
docker update --memory=2g shoes-admin-backend

# Or in docker-compose.yml:
services:
  shoes-admin-backend:
    mem_limit: 2g
```

#### 4. Disk Space Issues
```bash
# Check container disk usage
docker exec shoes-admin-backend df -h

# Clean up old files
docker exec shoes-admin-backend find /tmp/video-processing -type f -mtime +1 -delete
```

## 📊 Performance Optimization

### Docker Resource Limits

```yaml
# docker-compose.yml
services:
  shoes-admin-backend:
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 2G
        reservations:
          cpus: '1'
          memory: 1G
```

### FFmpeg Optimization

```yaml
# application-production.yml
video:
  processing:
    ffmpeg-options:
      threads: 2  # Limit CPU threads
      preset: fast  # Faster encoding (medium, fast, faster)
      crf: 28  # Lower quality for smaller files (18-28)
```

## 🔐 Security Considerations

1. **File Size Limits**: Enforce max file size to prevent DoS
2. **File Type Validation**: Only allow specific video formats
3. **Temp File Cleanup**: Implement scheduled cleanup of temporary files
4. **Resource Limits**: Set Docker container resource limits

## 📦 Minimal Docker Image (Production Ready)

```dockerfile
FROM eclipse-temurin:17-jre-alpine

# Install only FFmpeg runtime (no development packages)
RUN apk add --no-cache ffmpeg

# Create non-root user
RUN addgroup -g 1000 spring && \
    adduser -D -u 1000 -G spring spring

# Create necessary directories
RUN mkdir -p /app /tmp/video-processing && \
    chown -R spring:spring /app /tmp/video-processing

USER spring

# Copy application
COPY --chown=spring:spring target/*.jar /app/app.jar

WORKDIR /app

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8082/health || exit 1

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 📚 Additional Resources

- [FFmpeg Official Documentation](https://ffmpeg.org/documentation.html)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)

## 🆘 Support

If you encounter any issues:
1. Check container logs: `docker logs shoes-admin-backend`
2. Verify FFmpeg installation: `docker exec shoes-admin-backend ffmpeg -version`
3. Check application logs for specific error messages
4. Ensure sufficient disk space and memory