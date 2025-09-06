package com.github.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileUpdateRequest {

    private String  phoneNumber;
    private String  radioNumber;
    private String  profilePhotoUrl;
}
