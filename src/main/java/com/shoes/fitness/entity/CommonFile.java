package com.shoes.fitness.entity;

import com.shoes.fitness.common.util.UuidUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "common_files")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonFile {

    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(32)")
    private String id;

    @Column(name = "partner_id", nullable = false)
    private String partnerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "ncp_bucket", nullable = false)
    private String ncpBucket;

    @Column(name = "ncp_key", nullable = false)
    private String ncpKey;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type_mime")
    private String fileTypeMime;

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_status")
    @Builder.Default
    private UploadStatus uploadStatus = UploadStatus.PENDING;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "uploaded_by")
    private String uploadedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.id = UuidUtil.generateShortUuid();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateUploadStatus(UploadStatus status) {
        this.uploadStatus = status;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsVerified(String verifiedBy) {
        this.isVerified = true;
        this.updatedAt = LocalDateTime.now();
    }

    public enum FileType {
        BUSINESS_LICENSE("사업자등록증"),
        BANK_ACCOUNT_COPY("통장사본"),
        COMPANY_LOGO("회사 로고"),
        PROFILE_IMAGE("프로필 이미지"),
        CONTRACT("계약서"),
        OTHER("기타");

        private final String description;
        FileType(String description) { this.description = description; }

        public String getDescription() { return description; }

        public String getDirectoryName() {
            switch (this) {
                case BUSINESS_LICENSE: return "business";
                case BANK_ACCOUNT_COPY: return "bank";
                case COMPANY_LOGO: return "logo";
                case PROFILE_IMAGE: return "profile";
                case CONTRACT: return "contract";
                default: return "other";
            }
        }
    }

    public enum UploadStatus {
        PENDING("업로드 대기"),
        COMPLETED("업로드 완료"),
        FAILED("업로드 실패");

        private final String description;
        UploadStatus(String description) { this.description = description; }

        public String getDescription() { return description; }
    }
}
