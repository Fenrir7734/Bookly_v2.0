package com.fenrir.masterdetail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.Column;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
public class ReviewRequestDTO {
    @Size(max = 1000)
    private String content;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(name = "rate", nullable = false)
    private Integer rate;
}
