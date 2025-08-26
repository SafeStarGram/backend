package com.github.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponse {

    private Integer userId;
    private String  name; //이름
    private String  phoneNumber; //휴대폰
    private String  radioNumber; //무전번호
    private String  profilePhotoUrl; //프로필사진URL


    private Integer departmentId;
    private Integer positionId;

}
