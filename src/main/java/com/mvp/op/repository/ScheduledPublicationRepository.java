package com.mvp.op.repository;

import com.mvp.op.model.ScheduledPublication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledPublicationRepository extends JpaRepository<ScheduledPublication, Long> {
    List<ScheduledPublication> findByPublishedFalseAndScheduledTimeBefore(LocalDateTime time);
}
