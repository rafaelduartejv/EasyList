package com.mvp.op.controller;

import com.mvp.op.service.MercadoLivreItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mercadolivre")
public class MercadoLivreItemController {

    @Autowired
    private MercadoLivreItemService itemService;

    // Recebe userId e lista de itemIds para copiar anúncios em lote
    @PostMapping("/copiar-anuncios/{userId}")
    public ResponseEntity<?> copiarAnunciosParaUsuario(
            @PathVariable Long userId,
            @RequestBody List<String> itemIds
    ) {
        try {
            List<Map<String, Object>> resultados = itemService.copiarAnuncios(userId, itemIds);
            return ResponseEntity.ok(resultados);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }
}
