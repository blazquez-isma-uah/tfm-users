package com.tfm.bandas.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PictureUrlResponseDTO(
    @JsonProperty("pictureUrl") String pictureUrl
) {}