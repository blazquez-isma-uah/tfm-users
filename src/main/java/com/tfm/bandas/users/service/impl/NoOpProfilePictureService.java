package com.tfm.bandas.users.service.impl;

import com.tfm.bandas.users.exception.FeatureNotAvailableException;
import com.tfm.bandas.users.service.ProfilePictureService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Profile("docker")
public class NoOpProfilePictureService implements ProfilePictureService {

    private static final String MESSAGE =
            "La gestion de fotos de perfil no esta disponible en el entorno local. "
          + "Requiere el bucket S3 desplegado en AWS.";

    @Override
    public String generateUploadUrl(String iamId) {
        throw new FeatureNotAvailableException(MESSAGE);
    }

    @Override
    public void confirmUpload(String iamId) {
        throw new FeatureNotAvailableException(MESSAGE);
    }

    @Override
    public Optional<String> generateDownloadUrl(String iamId) {
        // No es un error: en local se comporta igual que "usuario sin foto".
        return Optional.empty();
    }
}