package com.mvp.op.repository;

import com.mvp.op.model.FiscalInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FiscalInvoiceRepository extends JpaRepository<FiscalInvoice, Long> {
    List<FiscalInvoice> findByUserId(Long userId);
}