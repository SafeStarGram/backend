package com.github.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PositionDto {
    private final Integer positionId; //직책ID
    private final String name; //직책명
}
