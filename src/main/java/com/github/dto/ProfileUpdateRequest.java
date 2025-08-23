package com.github.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProfileUpdateRequest {

    private Integer userId;         // 수정 대상 유저

    private String phoneNumber;     // 핸드폰 번호
    private String radioNumber;     // 무전 번호
    private Integer departmentId;   // 부서
    private Integer positionId;     // 직책
    private String profilePhotoUrl; // 프로필 사진 URL

}
