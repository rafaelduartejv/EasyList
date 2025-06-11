package com.mvp.op.repository;

import com.mvp.op.model.ItemMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ItemMetricsRepository extends JpaRepository<ItemMetrics, Long> {
    List<ItemMetrics> findByUserIdAndItemIdAndDateBetween(Long userId, String itemId, LocalDate start, LocalDate end);
}