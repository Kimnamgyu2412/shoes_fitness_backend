package com.shoes.fitness.common.service;

import com.shoes.fitness.common.dto.BusinessException;
import com.shoes.fitness.common.dto.FileUploadResult;
import com.shoes.fitness.common.repository.CommonFileRepository;
import com.shoes.fitness.entity.CommonFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommonFileService {

    private final CommonFileRepository commonFileRepository;
    private final FileUploadService fileUploadService;

    @Value("${ncp.object-storage.bucket}")
    private String bucketName;

    // 허용되는 파일 확장자
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "pdf", "jpg", "jpeg", "png", "gif"
    );

    // 최대 파일 크기 (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Transactional
    public CommonFile uploadFile(String partnerId, CommonFile.FileType fileType,
                                 MultipartFile file, String uploadedBy) {
        try {
            // 파일 유효성 검증
            validateFile(file);

            // NCP 경로 생성
            String directory = generateDirectory(partnerId, fileType);

            // 파일 업로드 (파일 타입에 따라 적절한 메서드 사용)
            FileUploadResult uploadResult;
            if (isImageFileType(fileType)) {
                uploadResult = fileUploadService.uploadImageFile(file, directory);
            } else {
                uploadResult = fileUploadService.uploadVideoFile(file, directory);
            }

            // CommonFile 엔티티 생성
            CommonFile commonFile = CommonFile.builder()
                    .partnerId(partnerId)
                    .fileType(fileType)
                    .fileName(uploadResult.getFileName())
                    .originalFileName(file.getOriginalFilename())
                    .ncpBucket(uploadResult.getBucket())
                    .ncpKey(uploadResult.getKey())
                    .fileUrl(uploadResult.getFileUrl())
                    .fileSize(uploadResult.getFileSize())
                    .fileTypeMime(uploadResult.getContentType())
                    .uploadStatus(CommonFile.UploadStatus.COMPLETED)
                    .uploadedBy(uploadedBy)
                    .build();

            CommonFile savedFile = commonFileRepository.save(commonFile);

            // 임시 파일 정리
            if (uploadResult.getTempFilePath() != null) {
                fileUploadService.cleanupTempFile(uploadResult.getTempFilePath());
            }

            log.info("파일 업로드 완료 - Partner: {}, FileType: {}, FileName: {}",
                    partnerId, fileType, file.getOriginalFilename());

            return savedFile;

        } catch (Exception e) {
            log.error("파일 업로드 실패 - Partner: {}, FileType: {}, Error: {}",
                    partnerId, fileType, e.getMessage(), e);
            throw new BusinessException("파일 업로드에 실패했습니다: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("파일이 선택되지 않았습니다.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("파일 크기는 10MB를 초과할 수 없습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException("올바른 파일 확장자가 없습니다.");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException("허용되지 않는 파일 형식입니다. (허용: PDF, JPG, JPEG, PNG, GIF)");
        }
    }

    private String generateDirectory(String partnerId, CommonFile.FileType fileType) {
        return String.format("common/%s/%s", partnerId, fileType.getDirectoryName());
    }

    public List<CommonFile> getFilesByPartner(String partnerId) {
        return commonFileRepository.findByPartnerId(partnerId);
    }

    public List<CommonFile> getFilesByPartnerAndType(String partnerId, CommonFile.FileType fileType) {
        return commonFileRepository.findByPartnerIdAndFileType(partnerId, fileType);
    }

//    public CommonFile getFileByPartnerAndType(String partnerId, CommonFile.FileType fileType) {
//        return commonFileRepository.findFirstByPartnerIdAndFileType(partnerId, fileType)
//                .orElse(null);
//    }

    @Transactional
    public void deleteFile(String fileId) {
        CommonFile file = commonFileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException("파일을 찾을 수 없습니다."));

        try {
            // NCP에서 파일 삭제
            fileUploadService.deleteFile(file.getNcpBucket(), file.getNcpKey());

            // DB에서 삭제
            commonFileRepository.delete(file);

            log.info("파일 삭제 완료 - FileId: {}, Key: {}", fileId, file.getNcpKey());

        } catch (Exception e) {
            log.error("파일 삭제 실패 - FileId: {}, Error: {}", fileId, e.getMessage(), e);
            throw new BusinessException("파일 삭제에 실패했습니다.");
        }
    }

//    @Transactional
//    public CommonFile markAsVerified(String fileId, String verifiedBy) {
//        CommonFile file = commonFileRepository.findById(fileId)
//                .orElseThrow(() -> new BusinessException("파일을 찾을 수 없습니다."));
//
//        file.markAsVerified(verifiedBy);
//        return commonFileRepository.save(file);
//    }

    private boolean isImageFileType(CommonFile.FileType fileType) {
        return fileType == CommonFile.FileType.BUSINESS_LICENSE ||
               fileType == CommonFile.FileType.PROFILE_IMAGE ||
               fileType == CommonFile.FileType.COMPANY_LOGO;
    }
}
