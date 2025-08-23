package com.github.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DepartmentDto {
    private final Integer departmentId; //부서ID
    private final String name; //부서명
}
