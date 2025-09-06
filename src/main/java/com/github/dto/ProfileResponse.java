package com.github.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponse {
    private Integer userId;
    private String  name;
    private String  phoneNumber;
    private String  radioNumber;
    private String  profilePhotoUrl;
}
