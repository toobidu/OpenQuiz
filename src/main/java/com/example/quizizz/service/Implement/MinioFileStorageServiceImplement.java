package com.example.quizizz.service.Implement;

import com.example.quizizz.service.Interface.IFileStorageService;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Service lưu trữ file sử dụng MinIO (avatar, hình ảnh quiz).
 * Đảm bảo bucket tồn tại, upload và xóa file, trả về URL file.
 */
@Service
@RequiredArgsConstructor
public class MinioFileStorageServiceImplement implements IFileStorageService {

    private final MinioClient minioClient;

    @Value("${minio.avatar-bucket}")
    private String avatarBucket;

    @Value("${minio.image-bucket}")
    private String imageBucket;

    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * Upload avatar cho người dùng lên MinIO.
     * @param file File avatar
     * @param userId Id người dùng
     * @return Tên file (không phải presigned URL)
     * @throws Exception Nếu upload lỗi
     */
    @Override
    public String uploadAvatar(MultipartFile file, Long userId) throws Exception {
        ensureBucketExists(avatarBucket);
        String fileName = "avatar_" + userId + "_" + UUID.randomUUID().toString().substring(0, 8) + getFileExtension(file.getOriginalFilename());
        
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(avatarBucket)
                .object(fileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build()
        );
        
        return fileName; // Trả về tên file thay vì presigned URL
    }

    /**
     * Upload hình ảnh quiz lên MinIO.
     * @param file File hình ảnh
     * @param quizId Id quiz
     * @return Presigned URL của file
     * @throws Exception Nếu upload lỗi
     */
    @Override
    public String uploadQuizImage(MultipartFile file, Long quizId) throws Exception {
        ensureBucketExists(imageBucket);
        String fileName = "quiz_" + quizId + "_" + UUID.randomUUID().toString().substring(0, 8) + getFileExtension(file.getOriginalFilename());
        
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(imageBucket)
                .object(fileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build()
        );
        
        return getPresignedUrl(imageBucket, fileName);
    }

    /**
     * Xóa file khỏi MinIO.
     * @param bucketName Tên bucket
     * @param fileName Tên file
     * @throws Exception Nếu xóa lỗi
     */
    @Override
    public void deleteFile(String bucketName, String fileName) throws Exception {
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build()
        );
    }

    /**
     * Lấy presigned URL để truy cập avatar.
     * @param fileName Tên file avatar
     * @return Presigned URL
     */
    @Override
    public String getAvatarUrl(String fileName) throws Exception {
        return getPresignedUrl(avatarBucket, fileName);
    }

    /**
     * Đảm bảo bucket tồn tại, nếu chưa thì tạo mới.
     * @param bucketName Tên bucket
     * @throws Exception Nếu lỗi MinIO
     */
    private void ensureBucketExists(String bucketName) throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * Tạo presigned URL có thời hạn để truy cập file an toàn.
     * @param bucketName Tên bucket
     * @param fileName Tên file
     * @return Presigned URL có thời hạn 1 giờ
     */
    private String getPresignedUrl(String bucketName, String fileName) throws Exception {
        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(fileName)
                .expiry(1, TimeUnit.HOURS)
                .build()
        );
    }

    /**
     * Lấy phần mở rộng của file.
     * @param fileName Tên file
     * @return Phần mở rộng (ví dụ: .jpg)
     */
    private String getFileExtension(String fileName) {
        return fileName != null && fileName.contains(".")
            ? fileName.substring(fileName.lastIndexOf("."))
            : "";
    }
}
