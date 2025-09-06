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
    private Long reporterId;
    private Long actionTakerId;
    private String title;
    private String content;

    private byte[] imageBlob;

    private String reporterRisk;
    private String managerRisk;
    private String isChecked;
    private String isActionTaken;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime isCheckdAt;
    private LocalDateTime isActionTakenAt;
}