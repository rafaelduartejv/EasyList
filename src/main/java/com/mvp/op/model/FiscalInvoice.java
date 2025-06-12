package com.mvp.op.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiscalInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String invoiceId;
    private String sourceType; // "order" or "pack"
    private String sourceId;
    private String status; // e.g. "issued", "error"
    private String downloadUrl;
    private LocalDateTime issuedAt;
    private String errorMessage;
}
