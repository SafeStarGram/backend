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
    private Integer phoneNumber;
    private Integer radioNumber;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
