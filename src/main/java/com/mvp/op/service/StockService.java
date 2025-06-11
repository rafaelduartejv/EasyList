package com.mvp.op.service;

import com.mvp.op.model.DraftItem;
import com.mvp.op.model.StockEntry;
import com.mvp.op.repository.DraftItemRepository;
import com.mvp.op.repository.StockEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockService {

    @Autowired
    private StockEntryRepository stockRepo;

    @Autowired
    private DraftItemRepository draftRepo;

    public StockEntry atualizarEstoque(Long userId, Long draftId, int novoEstoque) {
        DraftItem draft = draftRepo.findById(draftId).orElseThrow();
        draft.setAvailableQuantity(novoEstoque);
        draftRepo.save(draft);

        StockEntry stock = stockRepo.findByUserIdAndDraftItemId(userId, draftId)
                .orElse(new StockEntry(null, userId, draftId, 0, 0, LocalDateTime.now()));

        stock.setCurrentStock(novoEstoque);
        stock.setLastUpdated(LocalDateTime.now());

        return stockRepo.save(stock);
    }

    public List<StockEntry> listarEstoque(Long userId) {
        return stockRepo.findByUserId(userId);
    }
}
