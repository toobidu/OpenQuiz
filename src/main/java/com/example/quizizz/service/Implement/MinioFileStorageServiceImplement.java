package com.example.quizizz.service.Implement;

import com.example.quizizz.service.Interface.IFileStorageService;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

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

    /**
     * Upload avatar cho người dùng lên MinIO.
     * @param file File avatar
     * @param userId Id người dùng
     * @return URL file avatar
     * @throws Exception Nếu upload lỗi
     */
    @Override
    public String uploadAvatar(MultipartFile file, Long userId) throws Exception {
        ensureBucketExists(avatarBucket);
        String fileName = "avatar_" + userId + "_" + UUID.randomUUID() + getFileExtension(file.getOriginalFilename());
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(avatarBucket)
                .object(fileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build()
        );
        return getFileUrl(avatarBucket, fileName);
    }

    /**
     * Upload hình ảnh quiz lên MinIO.
     * @param file File hình ảnh
     * @param quizId Id quiz
     * @return URL file hình ảnh
     * @throws Exception Nếu upload lỗi
     */
    @Override
    public String uploadQuizImage(MultipartFile file, Long quizId) throws Exception {
        ensureBucketExists(imageBucket);
        String fileName = "quiz_" + quizId + "_" + UUID.randomUUID() + getFileExtension(file.getOriginalFilename());
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(imageBucket)
                .object(fileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build()
        );
        return getFileUrl(imageBucket, fileName);
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
     * Lấy URL truy cập file từ bucket và tên file.
     * @param bucketName Tên bucket
     * @param fileName Tên file
     * @return Đường dẫn file
     */
    private String getFileUrl(String bucketName, String fileName) {
        return "/" + bucketName + "/" + fileName;
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