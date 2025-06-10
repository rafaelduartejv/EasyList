package com.mvp.op.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseMercadoLivre {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private int expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("user_id")
    private Long userId;

}