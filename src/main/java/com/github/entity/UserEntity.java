package com.github.entity;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    private Integer userId;
    private Integer areaId;
    private Integer departmentId;
    private Integer positionId;
    private String name;
    private String email;
    private String password;
    private String profilePhotoUrl;
    private String phoneNumber; //varchar여서 int -> string으로 수정
    private String radioNumber; //varchar여서 int -> string으로 수정
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
