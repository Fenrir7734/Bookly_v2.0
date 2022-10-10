package com.fenrir.masterdetail.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.ConstraintViolation;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstraintViolationInfo {
    private String propertyName;
    private String invalidValue;
    private String violationMessage;

    public static ConstraintViolationInfo from(ConstraintViolation cv) {
        return new ConstraintViolationInfo(
                cv.getPropertyPath().toString(),
                cv.getInvalidValue().toString(),
                cv.getMessage()
        );
    }
}
