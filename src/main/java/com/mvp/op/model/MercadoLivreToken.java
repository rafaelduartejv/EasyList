package com.mvp.op.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class MercadoLivreToken {

    @Id
    private Long userId;

    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiresAt;
}
