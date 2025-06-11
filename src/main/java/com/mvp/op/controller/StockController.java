package com.mvp.op.controller;

import com.mvp.op.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock")
@Tag(name = "Stock Management", description = "Endpoints to manage stock levels of draft items")
public class StockController {

    @Autowired
    private StockService stockService;

    @PutMapping("/update")
    @Operation(summary = "Update stock of an item")
    public ResponseEntity<?> atualizar(
            @RequestParam Long userId,
            @RequestParam Long draftId,
            @RequestParam int novoEstoque) {
        return ResponseEntity.ok(stockService.atualizarEstoque(userId, draftId, novoEstoque));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "List a user's inventory")
    public ResponseEntity<?> listar(@PathVariable Long userId) {
        return ResponseEntity.ok(stockService.listarEstoque(userId));
    }
}
