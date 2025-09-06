package com.github.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {
    private Long userId;
    private Long subAreaId;
    private String title;
    private String content;
    private String reporterRisk;
}