package com.tfm.bandas.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UploadUrlResponseDTO(
    @JsonProperty("uploadUrl") String uploadUrl
) {}