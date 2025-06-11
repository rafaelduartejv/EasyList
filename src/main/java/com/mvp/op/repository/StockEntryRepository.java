package com.mvp.op.repository;

import com.mvp.op.model.StockEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockEntryRepository extends JpaRepository<StockEntry, Long> {
    Optional<StockEntry> findByUserIdAndDraftItemId(Long userId, Long draftItemId);
    List<StockEntry> findByUserId(Long userId);
}
