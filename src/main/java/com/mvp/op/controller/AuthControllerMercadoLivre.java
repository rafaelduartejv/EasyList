package com.mvp.op.controller;

import com.mvp.op.dto.AuthRequestMercadoLivre;
import com.mvp.op.dto.AuthResponseMercadoLivre;
import com.mvp.op.model.MercadoLivreToken;
import com.mvp.op.repository.MercadoLivreTokenRepository;
import com.mvp.op.util.PkceUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Tag(name = "Mercado Livre Authentication", description = "Endpoints for Mercado Livre OAuth 2.0 authentication")
@RestController
@RequestMapping("/auth")
public class AuthControllerMercadoLivre {

    @Value("${ml.client-id}")
    private String clientId;

    @Value("${ml.client-secret}")
    private String clientSecret;

    @Value("${ml.redirect-uri}")
    private String redirectUri;

    @Autowired
    private MercadoLivreTokenRepository tokenRepository;

    // Simula armazenamento temporário do code_verifier
    private final Map<String, String> verifierStore = new ConcurrentHashMap<>();

    @GetMapping("/login")
    @Operation(summary = "Initiate Mercado Livre OAuth login", description = "Redirects the user to Mercado Livre's authorization page with PKCE")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Redirects to Mercado Livre authorization page"),
            @ApiResponse(responseCode = "500", description = "Internal server error during redirect")
    })
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
    @Operation(summary = "Handle OAuth callback", description = "Receives the authorization code from Mercado Livre and returns it for token exchange")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authorization code received successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or missing authorization code")
    })
    public ResponseEntity<String> callback(@RequestParam String code) {
        // Salva o code recebido para troca posterior
        return ResponseEntity.ok("Código recebido: " + code + "\nUse este código para fazer a troca no /auth/token");
    }

    @PostMapping("/token")
    @Operation(summary = "Exchange authorization code for token", description = "Exchanges the authorization code for an access token and refresh token from Mercado Livre")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token exchanged and saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid code or code_verifier not found"),
            @ApiResponse(responseCode = "500", description = "Error during token exchange")
    })
    public ResponseEntity<?> exchangeToken(@RequestBody AuthRequestMercadoLivre request) {
        RestTemplate restTemplate = new RestTemplate();

        // Recupera o code_verifier da sessão simulada
        String codeVerifier = verifierStore.get("session");
        if (codeVerifier == null) {
            return ResponseEntity.badRequest().body("code_verifier não encontrado (verifique se o /login foi acessado)");
        }

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

            AuthResponseMercadoLivre tokenData = response.getBody();

            // Salvar token no banco
            MercadoLivreToken token = new MercadoLivreToken();
            token.setUserId(tokenData.getUserId());
            token.setAccessToken(tokenData.getAccessToken());
            token.setRefreshToken(tokenData.getRefreshToken());
            token.setExpiresAt(LocalDateTime.now().plusSeconds(tokenData.getExpiresIn()));

            tokenRepository.save(token);

            return ResponseEntity.ok(tokenData);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao trocar token: " + e.getMessage());
        }
    }



    @GetMapping("/user-info/{userId}")
    @Operation(summary = "Get Mercado Livre user info", description = "Retrieves user information from Mercado Livre using the stored access token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User information retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Token not found for the user"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired access token")
    })
    public ResponseEntity<?> getUserInfo(@PathVariable Long userId) {
        MercadoLivreToken token = tokenRepository.findById(userId).orElse(null);
        if (token == null) return ResponseEntity.notFound().build();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.mercadolibre.com/users/me", HttpMethod.GET, entity, String.class
        );

        return ResponseEntity.ok(response.getBody());
    }

}
