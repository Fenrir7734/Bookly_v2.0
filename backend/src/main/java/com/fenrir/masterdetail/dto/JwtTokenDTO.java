package com.fenrir.masterdetail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JwtTokenDTO {
    private final String accessToken;
    private final String tokenType;

    public JwtTokenDTO(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }
}
