package com.mvp.op.repository;

import com.mvp.op.model.DraftItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DraftItemRepository extends JpaRepository<DraftItem, Long> {
    List<DraftItem> findByUserId(Long userId);
}