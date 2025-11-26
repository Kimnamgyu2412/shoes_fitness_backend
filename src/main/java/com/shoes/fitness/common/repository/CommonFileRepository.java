package com.shoes.fitness.common.repository;

import com.shoes.fitness.entity.CommonFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CommonFileRepository extends JpaRepository<CommonFile, String> {

    // 네이티브 쿼리 - 파트너별 파일 조회
    @Query(value = """
        SELECT cf.id, cf.partner_id, cf.file_type, cf.file_name, cf.original_file_name,
               cf.ncp_bucket, cf.ncp_key, cf.file_url, cf.file_size, cf.file_type_mime,
               cf.upload_status, cf.is_verified, cf.uploaded_by, cf.created_at, cf.updated_at
        FROM common_files cf
        WHERE cf.partner_id = :partnerId
        ORDER BY cf.created_at DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findByPartnerIdNative(@Param("partnerId") String partnerId);

    // 네이티브 쿼리 - 파트너별 파일 타입별 조회
    @Query(value = """
        SELECT cf.id, cf.partner_id, cf.file_type, cf.file_name, cf.original_file_name,
               cf.ncp_bucket, cf.ncp_key, cf.file_url, cf.file_size, cf.file_type_mime,
               cf.upload_status, cf.is_verified, cf.uploaded_by, cf.created_at, cf.updated_at
        FROM common_files cf
        WHERE cf.partner_id = :partnerId AND cf.file_type = :fileType
        ORDER BY cf.created_at DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findByPartnerIdAndFileTypeNative(@Param("partnerId") String partnerId,
                                                               @Param("fileType") String fileType);

    // 네이티브 쿼리 - 파트너별 파일 타입별 첫 번째 파일 조회
    @Query(value = """
        SELECT cf.id, cf.partner_id, cf.file_type, cf.file_name, cf.original_file_name,
               cf.ncp_bucket, cf.ncp_key, cf.file_url, cf.file_size, cf.file_type_mime,
               cf.upload_status, cf.is_verified, cf.uploaded_by, cf.created_at, cf.updated_at
        FROM common_files cf
        WHERE cf.partner_id = :partnerId AND cf.file_type = :fileType
        ORDER BY cf.created_at DESC
        LIMIT 1
        """, nativeQuery = true)
    List<Map<String, Object>> findFirstByPartnerIdAndFileTypeNative(@Param("partnerId") String partnerId,
                                                                    @Param("fileType") String fileType);

    // 네이티브 쿼리 - 업로드 상태별 파일 조회
    @Query(value = """
        SELECT cf.id, cf.partner_id, cf.file_type, cf.file_name, cf.original_file_name,
               cf.ncp_bucket, cf.ncp_key, cf.file_url, cf.file_size, cf.file_type_mime,
               cf.upload_status, cf.is_verified, cf.uploaded_by, cf.created_at, cf.updated_at,
               p.company_name, p.partner_login_id
        FROM common_files cf
        INNER JOIN partners p ON cf.partner_id = p.partner_id
        WHERE cf.upload_status = :uploadStatus
        ORDER BY cf.created_at DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findByUploadStatusWithPartnerNative(@Param("uploadStatus") String uploadStatus);

    // 네이티브 쿼리 - 파일 생성
    @Modifying
    @Query(value = """
        INSERT INTO common_files (
            id, partner_id, file_type, file_name, original_file_name,
            ncp_bucket, ncp_key, file_url, file_size, file_type_mime,
            upload_status, is_verified, uploaded_by, created_at, updated_at
        ) VALUES (
            :id, :partnerId, :fileType, :fileName, :originalFileName,
            :ncpBucket, :ncpKey, :fileUrl, :fileSize, :fileTypeMime,
            :uploadStatus, :isVerified, :uploadedBy, NOW(), NOW()
        )
        """, nativeQuery = true)
    int insertCommonFile(@Param("id") String id,
                         @Param("partnerId") String partnerId,
                         @Param("fileType") String fileType,
                         @Param("fileName") String fileName,
                         @Param("originalFileName") String originalFileName,
                         @Param("ncpBucket") String ncpBucket,
                         @Param("ncpKey") String ncpKey,
                         @Param("fileUrl") String fileUrl,
                         @Param("fileSize") Long fileSize,
                         @Param("fileTypeMime") String fileTypeMime,
                         @Param("uploadStatus") String uploadStatus,
                         @Param("isVerified") Boolean isVerified,
                         @Param("uploadedBy") String uploadedBy);

    // 네이티브 쿼리 - 파일 상태 업데이트
    @Modifying
    @Query(value = """
        UPDATE common_files
        SET upload_status = :uploadStatus, is_verified = :isVerified, updated_at = NOW()
        WHERE id = :fileId
        """, nativeQuery = true)
    int updateFileStatus(@Param("fileId") String fileId,
                         @Param("uploadStatus") String uploadStatus,
                         @Param("isVerified") Boolean isVerified);

    // 네이티브 쿼리 - 파트너별 파일 삭제
    @Modifying
    @Query(value = "DELETE FROM common_files WHERE partner_id = :partnerId", nativeQuery = true)
    int deleteByPartnerIdNative(@Param("partnerId") String partnerId);

    // 네이티브 쿼리 - 파일 삭제
    @Modifying
    @Query(value = "DELETE FROM common_files WHERE id = :fileId", nativeQuery = true)
    int deleteByFileIdNative(@Param("fileId") String fileId);

    List<CommonFile> findByPartnerId(String partnerId);
    List<CommonFile> findByPartnerIdAndFileType(String partnerId,CommonFile.FileType fileType);
    List<CommonFile> findFirstByPartnerIdAndFileType(String partnerId,CommonFile.FileType fileType);
}
