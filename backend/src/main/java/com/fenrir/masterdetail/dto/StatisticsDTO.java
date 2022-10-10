package com.fenrir.masterdetail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatisticsDTO {
    private Long id;
    private Long numberOfRates;
    private Long numberOfComments;
    private Double rate;
}
