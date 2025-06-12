package com.mvp.op.service;

import com.mvp.op.model.FiscalInvoice;
import com.mvp.op.model.MercadoLivreToken;
import com.mvp.op.repository.FiscalInvoiceRepository;
import com.mvp.op.repository.MercadoLivreTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FiscalService {

    @Autowired
    private FiscalInvoiceRepository invoiceRepository;

    @Autowired
    private MercadoLivreTokenRepository tokenRepository;

    public List<FiscalInvoice> emitInvoices(Long userId, List<String> ids) {
        MercadoLivreToken token = tokenRepository.findById(userId).orElseThrow();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<FiscalInvoice> result = new ArrayList<>();

        for (String id : ids) {
            String url = "https://api.mercadolibre.com/users/" + userId + "/invoices/orders";
            Map<String, Object> body = Map.of("orders", List.of(Map.of("id", id)));

            try {
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
                ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
                Map data = response.getBody();
                FiscalInvoice invoice = new FiscalInvoice(null, userId, (String) data.get("id"), "order", id,
                        "issued", (String) data.get("pdf_url"), LocalDateTime.now(), null);
                invoiceRepository.save(invoice);
                result.add(invoice);
            } catch (Exception e) {
                FiscalInvoice error = new FiscalInvoice(null, userId, null, "order", id,
                        "error", null, LocalDateTime.now(), e.getMessage());
                invoiceRepository.save(error);
                result.add(error);
            }
        }
        return result;
    }

    public List<FiscalInvoice> listInvoices(Long userId) {
        return invoiceRepository.findByUserId(userId);
    }

    public Map<String, ? extends Serializable> downloadInvoice(Long userId, String invoiceId) {
        Optional<FiscalInvoice> opt = invoiceRepository.findByUserId(userId).stream()
                .filter(inv -> invoiceId.equals(inv.getInvoiceId()))
                .findFirst();
        return opt.map(invoice -> Map.of(
                "pdf_url", invoice.getDownloadUrl(),
                "status", invoice.getStatus(),
                "issued_at", invoice.getIssuedAt()
        )).orElse(Map.of("error", "Nota não encontrada"));
    }
}
