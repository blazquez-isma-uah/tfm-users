package com.tfm.bandas.users.controller;

import com.tfm.bandas.users.dto.PictureUrlResponseDTO;
import com.tfm.bandas.users.dto.UploadUrlResponseDTO;
import com.tfm.bandas.users.service.ProfilePictureService;
import com.tfm.bandas.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/picture")
@RequiredArgsConstructor
public class UserPictureController {

    private static final Logger logger = LoggerFactory.getLogger(UserPictureController.class);

    private final ProfilePictureService profilePictureService;
    private final UserService userService;

    @PostMapping("/me/upload-url")
    public ResponseEntity<UploadUrlResponseDTO> getMyUploadUrl(@AuthenticationPrincipal Jwt jwt) {
        String iamId = jwt.getSubject();
        logger.info("Calling getMyUploadUrl for iamId: {}", iamId);
        return ResponseEntity.ok(new UploadUrlResponseDTO(profilePictureService.generateUploadUrl(iamId)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}/upload-url")
    public ResponseEntity<UploadUrlResponseDTO> getUploadUrlForUser(@PathVariable Long userId) {
        String iamId = userService.getUserById(userId).iamId();
        logger.info("Calling getUploadUrlForUser for userId: {}", userId);
        return ResponseEntity.ok(new UploadUrlResponseDTO(profilePictureService.generateUploadUrl(iamId)));
    }

    @PutMapping("/me")
    public ResponseEntity<Void> confirmMyUpload(@AuthenticationPrincipal Jwt jwt) {
        String iamId = jwt.getSubject();
        logger.info("Calling confirmMyUpload for iamId: {}", iamId);
        profilePictureService.confirmUpload(iamId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}")
    public ResponseEntity<Void> confirmUploadForUser(@PathVariable Long userId) {
        String iamId = userService.getUserById(userId).iamId();
        logger.info("Calling confirmUploadForUser for userId: {}", userId);
        profilePictureService.confirmUpload(iamId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<PictureUrlResponseDTO> getMyPictureUrl(@AuthenticationPrincipal Jwt jwt) {
        String iamId = jwt.getSubject();
        logger.info("Calling getMyPictureUrl for iamId: {}", iamId);
        String url = profilePictureService.generateDownloadUrl(iamId).orElse(null);
        return ResponseEntity.ok(new PictureUrlResponseDTO(url));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MUSICIAN')")
    @GetMapping("/{userId}")
    public ResponseEntity<PictureUrlResponseDTO> getPictureUrlForUser(@PathVariable Long userId) {
        String iamId = userService.getUserById(userId).iamId();
        logger.info("Calling getPictureUrlForUser for userId: {}", userId);
        String url = profilePictureService.generateDownloadUrl(iamId).orElse(null);
        return ResponseEntity.ok(new PictureUrlResponseDTO(url));
    }
}