package com.mvp.op.controller;

import com.mvp.op.model.DraftItem;
import com.mvp.op.model.MercadoLivreToken;
import com.mvp.op.repository.DraftItemRepository;
import com.mvp.op.repository.MercadoLivreTokenRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Tag(name = "Mercado Livre Items", description = "Endpoints for managing Mercado Livre item drafts and publishing")
@RestController
@RequestMapping("/items")
public class MercadoLivreItemController {

    @Autowired
    private DraftItemRepository draftItemRepository;

    @Autowired
    private MercadoLivreTokenRepository tokenRepository;

    @PostMapping("/clone/{itemId}")
    @Operation(summary = "Clone a Mercado Livre item", description = "Clones an existing Mercado Livre item and saves it as a draft")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item cloned and saved as draft successfully"),
            @ApiResponse(responseCode = "404", description = "Token not found for the user"),
            @ApiResponse(responseCode = "400", description = "Invalid item ID or API error")
    })    public ResponseEntity<?> cloneItem(@PathVariable String itemId, @RequestParam Long userId) {
        MercadoLivreToken token = tokenRepository.findById(userId).orElse(null);
        if (token == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token não encontrado");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.mercadolibre.com/items/" + itemId,
                HttpMethod.GET, entity, Map.class);

        Map<String, Object> item = response.getBody();

        DraftItem draft = new DraftItem();
        draft.setUserId(userId);
        draft.setTitle((String) item.get("title"));
        draft.setCategoryId((String) item.get("category_id"));
        draft.setCondition((String) item.get("condition"));
        draft.setPrice(Double.valueOf(item.get("price").toString()));
        draft.setAvailableQuantity((Integer) item.get("available_quantity"));
        draft.setListingTypeId((String) item.get("listing_type_id"));
        draft.setOriginalItemId(itemId);

        List<Map<String, String>> pictures = (List<Map<String, String>>) item.get("pictures");
        List<String> pictureUrls = pictures.stream().map(pic -> pic.get("url")).toList();
        draft.setPictures(pictureUrls);

        String descUrl = "https://api.mercadolibre.com/items/" + itemId + "/description";
        ResponseEntity<Map> descResp = restTemplate.exchange(descUrl, HttpMethod.GET, entity, Map.class);
        if (descResp.getStatusCode().is2xxSuccessful()) {
            draft.setDescription((String) descResp.getBody().get("plain_text"));
        }

        draftItemRepository.save(draft);
        return ResponseEntity.ok(draft);
    }

    @PutMapping("/drafts/{id}")
    @Operation(summary = "Edit a draft item", description = "Updates an existing draft item with new details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Draft updated successfully"),
            @ApiResponse(responseCode = "404", description = "Draft not found")
    })    public ResponseEntity<?> editDraft(@PathVariable Long id, @RequestBody DraftItem updatedDraft) {
        DraftItem draft = draftItemRepository.findById(id).orElse(null);
        if (draft == null) return ResponseEntity.notFound().build();

        draft.setTitle(updatedDraft.getTitle());
        draft.setPrice(updatedDraft.getPrice());
        draft.setDescription(updatedDraft.getDescription());
        draft.setAvailableQuantity(updatedDraft.getAvailableQuantity());
        draft.setPictures(updatedDraft.getPictures());

        draftItemRepository.save(draft);
        return ResponseEntity.ok(draft);
    }

    @PostMapping("/publish/{draftId}")
    @Operation(summary = "Publish a draft item", description = "Publishes a draft item as a new listing on Mercado Livre")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Draft published successfully"),
            @ApiResponse(responseCode = "404", description = "Draft or token not found"),
            @ApiResponse(responseCode = "400", description = "Error during publication")
    })    public ResponseEntity<?> publishDraft(@PathVariable Long draftId) {
        DraftItem draft = draftItemRepository.findById(draftId).orElse(null);
        if (draft == null) return ResponseEntity.notFound().build();

        MercadoLivreToken token = tokenRepository.findById(draft.getUserId()).orElse(null);
        if (token == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token não encontrado");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new HashMap<>();
        payload.put("title", draft.getTitle());
        payload.put("category_id", draft.getCategoryId());
        payload.put("price", draft.getPrice());
        payload.put("currency_id", "BRL");
        payload.put("available_quantity", draft.getAvailableQuantity());
        payload.put("condition", draft.getCondition());
        payload.put("listing_type_id", draft.getListingTypeId());
        payload.put("description", Map.of("plain_text", draft.getDescription()));
        payload.put("pictures", draft.getPictures().stream().map(url -> Map.of("source", url)).toList());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.mercadolibre.com/items", entity, Map.class);

        return ResponseEntity.ok(response.getBody());
    }

    @GetMapping("/drafts/user/{userId}")
    @Operation(summary = "List user drafts", description = "Retrieves all draft items for a specific user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Drafts retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No drafts found for the user")
    })    public ResponseEntity<?> listUserDrafts(@PathVariable Long userId) {
        return ResponseEntity.ok(draftItemRepository.findByUserId(userId));
    }
}