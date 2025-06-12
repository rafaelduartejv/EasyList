package com.mvp.op.controller;

import com.mvp.op.model.FiscalInvoice;
import com.mvp.op.service.FiscalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fiscal")
@Tag(name = "Fiscal Invoices", description = "Endpoints for Mercado Livre invoice management")
public class FiscalController {

    @Autowired
    private FiscalService fiscalService;

    @PostMapping("/emit")
    @Operation(summary = "Emit invoice(s)", description = "Emit fiscal invoices for given order or pack IDs")
    @ApiResponse(responseCode = "200", description = "Invoice(s) emitted successfully")
    public ResponseEntity<?> emitInvoices(@RequestParam Long userId, @RequestBody List<String> orderOrPackIds) {
        return ResponseEntity.ok(fiscalService.emitInvoices(userId, orderOrPackIds));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "List invoices", description = "Lists all invoices for a user")
    public ResponseEntity<List<FiscalInvoice>> list(@PathVariable Long userId) {
        return ResponseEntity.ok(fiscalService.listInvoices(userId));
    }

    @GetMapping("/{userId}/download/{invoiceId}")
    @Operation(summary = "Download invoice document", description = "Returns metadata or link to download PDF/XML")
    public ResponseEntity<?> download(@PathVariable Long userId, @PathVariable String invoiceId) {
        return ResponseEntity.ok(fiscalService.downloadInvoice(userId, invoiceId));
    }
}