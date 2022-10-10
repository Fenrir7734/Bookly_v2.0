package com.fenrir.masterdetail.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ErrorMessage {
    private int statusCode;
    private LocalDateTime timestamp;
    private String message;
}
