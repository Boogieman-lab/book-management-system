package com.example.bookmanagementsystembo.auth.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class Profile {
    private String nickname;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;

}
