package com.tfm.bandas.users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@Profile("aws")
public class S3Config {

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.EU_WEST_1)
                .build();
    }
}