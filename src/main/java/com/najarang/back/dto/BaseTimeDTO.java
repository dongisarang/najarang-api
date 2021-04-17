package com.najarang.back.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class BaseTimeDTO {

    private LocalDateTime created;

    private LocalDateTime modified;
}
