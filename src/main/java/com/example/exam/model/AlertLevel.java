package com.example.exam.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlertLevel {
    LEVEL1("초미세먼지 경보"),
    LEVEL2("미세먼지 경보"),
    LEVEL3("초미세먼지 주의보"),
    LEVEL4("미세먼지 주의보");

    private final String description;
}
