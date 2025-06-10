package com.mvp.op.controller;

import com.mvp.op.dto.AuthRequestMercadoLivre;
import com.mvp.op.dto.AuthResponseMercadoLivre;
import com.mvp.op.util.PkceUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/auth")
public class AuthControllerMercadoLivre {

    @Value("${ml.client-id}")
    private String clientId;

    @Value("${ml.client-secret}")
    private String clientSecret;

    @Value("${ml.redirect-uri}")
    private String redirectUri;

    // Simula armazenamento temporário do code_verifier
    private final Map<String, String> verifierStore = new ConcurrentHashMap<>();

    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        String codeVerifier = PkceUtil.generateCodeVerifier();
        String codeChallenge = PkceUtil.generateCodeChallenge(codeVerifier);

        // Salva o code_verifier na memória (por session ID ou user ID real)
        verifierStore.put("session", codeVerifier);

        String url = String.format(
                "https://auth.mercadolivre.com.br/authorization?response_type=code&client_id=%s&redirect_uri=%s&code_challenge=%s&code_challenge_method=S256",
                clientId, redirectUri, codeChallenge
        );

        response.sendRedirect(url);
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam String code) {
        // Salva o code recebido para troca posterior
        return ResponseEntity.ok("Código recebido: " + code + "\nUse este código para fazer a troca no /auth/token");
    }

    @PostMapping("/token")
    public ResponseEntity<?> exchangeToken(@RequestBody AuthRequestMercadoLivre request) {
        RestTemplate restTemplate = new RestTemplate();

        String codeVerifier = verifierStore.get("session"); // ou por ID de usuário real

        String url = "https://api.mercadolibre.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = String.format(
                "grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s&redirect_uri=%s&code_verifier=%s",
                clientId, clientSecret, request.getCode(), redirectUri, codeVerifier
        );

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<AuthResponseMercadoLivre> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, AuthResponseMercadoLivre.class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao trocar token: " + e.getMessage());
        }
    }
}
