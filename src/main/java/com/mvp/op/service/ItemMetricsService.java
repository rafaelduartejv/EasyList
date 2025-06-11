package com.mvp.op.service;

import com.mvp.op.model.ItemMetrics;
import com.mvp.op.model.MercadoLivreToken;
import com.mvp.op.repository.ItemMetricsRepository;
import com.mvp.op.repository.MercadoLivreTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ItemMetricsService {

    @Autowired
    private MercadoLivreTokenRepository tokenRepository;

    @Autowired
    private ItemMetricsRepository metricsRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<?> fetchMetricsTimeWindow(Long userId, String itemId, int last, String unit) {
        try {
            MercadoLivreToken token = tokenRepository.findById(userId).orElse(null);
            if (token == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token não encontrado");

            String url = String.format("https://api.mercadolibre.com/items/%s/visits/time_window?last=%d&unit=%s",
                    itemId, last, unit);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token.getAccessToken());
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(response.getStatusCode()).body("Erro ao buscar métricas");
            }

            List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");
            if (results == null || results.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sem dados de visitas retornados");
            }

            List<ItemMetrics> saved = new ArrayList<>();
            for (Map<String, Object> dayData : results) {
                ItemMetrics metrics = new ItemMetrics();
                metrics.setUserId(userId);
                metrics.setItemId(itemId);
                metrics.setDate(LocalDate.parse((String) dayData.get("date")));
                metrics.setTotalVisits((Integer) dayData.get("value"));
                saved.add(metrics);
            }

            metricsRepository.saveAll(saved);
            return ResponseEntity.ok(saved);
        } catch (RestClientException | ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao consultar métricas: " + e.getMessage());
        }
    }

    public ResponseEntity<?> fetchMetricsDateRange(Long userId, String itemId, LocalDate start, LocalDate end) {
        try {
            MercadoLivreToken token = tokenRepository.findById(userId).orElse(null);
            if (token == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token não encontrado");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String url = String.format(
                    "https://api.mercadolibre.com/items/visits?ids=%s&date_from=%s&date_to=%s",
                    itemId, start.format(formatter), end.format(formatter)
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token.getAccessToken());
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(response.getStatusCode()).body("Erro ao buscar métricas");
            }

            Map<String, List<Map<String, Object>>> body = response.getBody();
            List<Map<String, Object>> visits = body.get(itemId);

            if (visits == null || visits.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sem dados de visitas retornados");
            }

            List<ItemMetrics> saved = new ArrayList<>();
            for (Map<String, Object> day : visits) {
                ItemMetrics metrics = new ItemMetrics();
                metrics.setUserId(userId);
                metrics.setItemId(itemId);
                metrics.setDate(LocalDate.parse((String) day.get("date")));
                metrics.setTotalVisits((Integer) day.get("value"));
                saved.add(metrics);
            }

            metricsRepository.saveAll(saved);
            return ResponseEntity.ok(saved);
        } catch (RestClientException | ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao consultar métricas: " + e.getMessage());
        }
    }

    public ResponseEntity<List<ItemMetrics>> getSavedMetrics(Long userId, String itemId, LocalDate start, LocalDate end) {
        List<ItemMetrics> metrics = metricsRepository.findByUserIdAndItemIdAndDateBetween(userId, itemId, start, end);
        return ResponseEntity.ok(metrics);
    }
}
