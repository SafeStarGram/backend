package com.github.entity;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostEntity {
    private Long postId;
    private Long subAreaId;
    private Long repoterId;
    private Long actionTakerId;
    private String title;
    private String content;

    private byte[] imageBlob;

    private String repoterRisk;
    private String managerRisk;
    private String isChecked;
    private String isActionTaken;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime isCheckdAt;
    private LocalDateTime isActionTakenAt;
}