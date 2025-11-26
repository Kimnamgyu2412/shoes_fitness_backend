package com.shoes.fitness.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DirectoryConfig {

    private final FileUploadConfig fileUploadConfig;

    /**
     * 애플리케이션 시작 시 필요한 디렉토리 생성
     */
    @Bean
    public ApplicationRunner createDirectories() {
        return args -> {
            createDirectoryIfNotExists(fileUploadConfig.getTempDir());
            log.info("임시 파일 디렉토리 초기화 완료: {}", fileUploadConfig.getTempDir());
        };
    }

    private void createDirectoryIfNotExists(String directoryPath) {
        try {
            Path path = Paths.get(directoryPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("디렉토리 생성: {}", directoryPath);
            } else {
                log.debug("디렉토리 이미 존재: {}", directoryPath);
            }
        } catch (IOException e) {
            log.error("디렉토리 생성 실패: {}", directoryPath, e);
            throw new RuntimeException("디렉토리 생성에 실패했습니다: " + directoryPath, e);
        }
    }
}
