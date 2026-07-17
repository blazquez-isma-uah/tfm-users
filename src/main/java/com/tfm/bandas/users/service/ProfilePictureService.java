package com.tfm.bandas.users.service;

import java.util.Optional;

public interface ProfilePictureService {

    /** Genera una presigned URL de subida (PUT) para la foto de perfil del usuario. */
    String generateUploadUrl(String iamId);

    /** Confirma que la subida a S3 se completó y actualiza profilePictureUrl en BD. */
    void confirmUpload(String iamId);

    /**
     * Genera una presigned URL de lectura (GET).
     * Optional.empty() si el usuario no tiene foto — no es un error, es el estado
     * normal para la mayoría de usuarios.
     */
    Optional<String> generateDownloadUrl(String iamId);
}