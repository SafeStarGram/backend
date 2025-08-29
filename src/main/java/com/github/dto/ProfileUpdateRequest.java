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

    private String  phoneNumber;      // 휴대폰 번호
    private String  radioNumber;      // 무전 번호
    private Integer departmentId;     // 부서 ID
    private Integer positionId;       // 직책 ID
    private String  profilePhotoUrl;  // 프로필 사진 URL

}
