package com.mvp.op.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ml")
public class NotControllerMercadoLivre {

    @PostMapping("/notificacoes")
    public ResponseEntity<Void> receberNotificacao(@RequestBody Map<String, Object> payload) {
        System.out.println("🔔 Notificação recebida do Mercado Livre:");
        payload.forEach((k, v) -> System.out.println(k + ": " + v));
        return ResponseEntity.ok().build();
    }
}
