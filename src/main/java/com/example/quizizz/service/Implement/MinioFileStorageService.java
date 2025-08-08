package com.example.quizizz.service.Implement;

import com.example.quizizz.service.Interface.IFileStorageService;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioFileStorageService implements IFileStorageService {

    private final MinioClient minioClient;

    @Value("${minio.avatar-bucket}")
    private String avatarBucket;

    @Value("${minio.image-bucket}")
    private String imageBucket;

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

    @Override
    public void deleteFile(String bucketName, String fileName) throws Exception {
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build()
        );
    }

    private void ensureBucketExists(String bucketName) throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    private String getFileUrl(String bucketName, String fileName) {
        return String.format("%s/%s/%s", minioClient.getObjectUrl(bucketName, fileName), bucketName, fileName);
    }

    private String getFileExtension(String fileName) {
        return fileName != null && fileName.contains(".")
            ? fileName.substring(fileName.lastIndexOf("."))
            : "";
    }
}