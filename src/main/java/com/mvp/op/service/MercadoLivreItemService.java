package com.mvp.op.service;

import com.mvp.op.model.MercadoLivreToken;
import com.mvp.op.repository.MercadoLivreTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class MercadoLivreItemService {

    @Autowired
    private MercadoLivreTokenRepository tokenRepository;

    private final String BASE_URL = "https://api.mercadolibre.com";
    private final RestTemplate restTemplate = new RestTemplate();

    public List<Map<String, Object>> copiarAnuncios(Long userId, List<String> listaItemIds) {
        MercadoLivreToken token = tokenRepository.findById(userId).orElse(null);
        if (token == null) {
            throw new RuntimeException("Usuário não autenticado");
        }
        String accessToken = token.getAccessToken();

        List<Map<String, Object>> resultados = new ArrayList<>();

        for (String itemId : listaItemIds) {
            try {
                Map<String, Object> itemOriginal = buscarItemOriginal(itemId, accessToken);
                Map<String, Object> novoItem = prepararNovoItem(itemOriginal);
                Map<String, Object> respostaCriacao = criarNovoItem(novoItem, accessToken);

                resultados.add(Map.of(
                        "original_item_id", itemId,
                        "status", "sucesso",
                        "novo_item", respostaCriacao
                ));
            } catch (Exception e) {
                resultados.add(Map.of(
                        "original_item_id", itemId,
                        "status", "erro",
                        "mensagem", e.getMessage()
                ));
            }
        }

        return resultados;
    }

    private Map<String, Object> buscarItemOriginal(String itemId, String accessToken) {
        String url = BASE_URL + "/items/" + itemId;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Erro ao buscar item " + itemId);
        }

        return response.getBody();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> prepararNovoItem(Map<String, Object> original) {
        // Cria uma cópia do mapa para editar
        Map<String, Object> novoItem = new HashMap<>(original);

        // Remover campos proibidos
        novoItem.remove("id");
        novoItem.remove("seller_id");
        novoItem.remove("site_id");
        novoItem.remove("date_created");
        novoItem.remove("last_updated");
        novoItem.remove("status");
        novoItem.remove("initial_quantity");
        novoItem.remove("sold_quantity");
        novoItem.remove("price"); // Você pode querer alterar o preço manualmente
        novoItem.remove("permalink");
        novoItem.remove("official_store_id");
        novoItem.remove("catalog_product_id");

        // Alterar título para indicar cópia
        String tituloOriginal = (String) novoItem.get("title");
        novoItem.put("title", tituloOriginal + " - Cópia");

        // Ajustar atributos que precisam de atenção
        // Exemplo: remover variações que podem causar erro na criação
        if (novoItem.containsKey("variations")) {
            novoItem.remove("variations");
        }

        // Se houver atributos ou descrições que precisem ajuste, faça aqui

        return novoItem;
    }

    private Map<String, Object> criarNovoItem(Map<String, Object> novoItem, String accessToken) {
        String url = BASE_URL + "/items";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(novoItem, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Erro ao criar novo item: " + response.getBody());
        }

        return response.getBody();
    }
}
