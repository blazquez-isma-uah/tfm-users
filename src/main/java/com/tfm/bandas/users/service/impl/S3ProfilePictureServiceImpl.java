package com.tfm.bandas.users.service.impl;

import com.tfm.bandas.users.exception.NotFoundException;
import com.tfm.bandas.users.model.entity.UserProfileEntity;
import com.tfm.bandas.users.model.repository.UserRepository;
import com.tfm.bandas.users.service.ProfilePictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Optional;

@Service
@Profile("aws")
@RequiredArgsConstructor
public class S3ProfilePictureServiceImpl implements ProfilePictureService {

    @Value("${PROFILE_PICTURE_UPLOAD_URL_TTL_MINUTES:5}")
    private long uploadUrlTtlMinutes;

    @Value("${PROFILE_PICTURE_DOWNLOAD_URL_TTL_MINUTES:10}")
    private long downloadUrlTtlMinutes;

    private final S3Presigner s3Presigner;
    private final UserRepository userRepo;

    @Value("${PROFILE_PICTURES_BUCKET}")
    private String bucketName;

    // Key fija por usuario: cada subida sobrescribe la anterior, sin histórico.
    private String keyFor(String iamId) {
        return iamId + "/profile.jpg";
    }

    @Override
    public String generateUploadUrl(String iamId) {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyFor(iamId))
                .contentType("image/jpeg")
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(uploadUrlTtlMinutes))
                .putObjectRequest(putRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }

    @Override
    @Transactional
    public void confirmUpload(String iamId) {
        UserProfileEntity user = userRepo.findByIamId(iamId)
                .orElseThrow(() -> new NotFoundException("User not found with IAM ID: " + iamId));

        // Referencia estable (no firmada). No es accesible directamente -el bucket
        // es privado- pero identifica sin ambigüedad qué objeto S3 corresponde
        // a este usuario. La URL firmada real se genera bajo demanda en generateDownloadUrl.
        user.setProfilePictureUrl("https://" + bucketName + ".s3.amazonaws.com/" + keyFor(iamId));
        userRepo.saveAndFlush(user);
    }

    @Override
    public Optional<String> generateDownloadUrl(String iamId) {
        UserProfileEntity user = userRepo.findByIamId(iamId)
                .orElseThrow(() -> new NotFoundException("User not found with IAM ID: " + iamId));

        if (user.getProfilePictureUrl() == null) {
            return Optional.empty();
        }

        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyFor(iamId))
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(downloadUrlTtlMinutes))
                .getObjectRequest(getRequest)
                .build();

        return Optional.of(s3Presigner.presignGetObject(presignRequest).url().toString());
    }
}