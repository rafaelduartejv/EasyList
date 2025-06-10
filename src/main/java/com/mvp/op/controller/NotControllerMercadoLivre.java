package com.mvp.op.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Mercado Livre Notifications", description = "Endpoint for receiving Mercado Livre webhook notifications")
@RestController
@RequestMapping("/api/ml")
public class NotControllerMercadoLivre {

    @PostMapping("/notificacoes")
    @Operation(summary = "Receive Mercado Livre webhook notification", description = "Processes notifications sent by Mercado Livre via webhook")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification received and processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid notification payload")
    })    public ResponseEntity<Void> receberNotificacao(@RequestBody Map<String, Object> payload) {
        System.out.println("🔔 Notificação recebida do Mercado Livre:");
        payload.forEach((k, v) -> System.out.println(k + ": " + v));
        return ResponseEntity.ok().build();
    }
}
