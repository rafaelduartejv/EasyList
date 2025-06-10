package com.mvp.op.service;

import com.mvp.op.dto.AuthResponseMercadoLivre;
import com.mvp.op.model.MercadoLivreToken;
import com.mvp.op.repository.MercadoLivreTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TokenRefreshService {

    @Value("${ml.client-id}")
    private String clientId;

    @Value("${ml.client-secret}")
    private String clientSecret;

    private final MercadoLivreTokenRepository repository;

    public TokenRefreshService(MercadoLivreTokenRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedRate = 5 * 60 * 1000) // a cada 5 minutos
    @Transactional
    public void refreshTokens() {
        List<MercadoLivreToken> tokens = repository.findAll();

        for (MercadoLivreToken token : tokens) {
            if (token.getExpiresAt().isBefore(LocalDateTime.now().plusMinutes(10))) {
                refreshToken(token);
            }
        }
    }

    private void refreshToken(MercadoLivreToken token) {
        RestTemplate restTemplate = new RestTemplate();

        String body = String.format(
                "grant_type=refresh_token&client_id=%s&client_secret=%s&refresh_token=%s",
                clientId, clientSecret, token.getRefreshToken()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<AuthResponseMercadoLivre> response = restTemplate.exchange(
                "https://api.mercadolibre.com/oauth/token",
                HttpMethod.POST,
                entity,
                AuthResponseMercadoLivre.class
        );

        AuthResponseMercadoLivre data = response.getBody();
        token.setAccessToken(data.getAccessToken());
        token.setRefreshToken(data.getRefreshToken());
        token.setExpiresAt(LocalDateTime.now().plusSeconds(data.getExpiresIn()));
        repository.save(token);
    }
}
