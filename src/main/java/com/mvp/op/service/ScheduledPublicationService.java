package com.mvp.op.service;

import com.mvp.op.controller.MercadoLivreItemController;
import com.mvp.op.model.ScheduledPublication;
import com.mvp.op.repository.DraftItemRepository;
import com.mvp.op.repository.ScheduledPublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledPublicationService {

    @Autowired
    private ScheduledPublicationRepository publicationRepository;

    @Autowired
    private DraftItemRepository draftItemRepository;

    @Autowired
    private MercadoLivreItemController itemController;

    @Scheduled(fixedRate = 60000) // a cada 1 minuto
    public void publicarAgendados() {
        List<ScheduledPublication> pendentes = publicationRepository
                .findByPublishedFalseAndScheduledTimeBefore(LocalDateTime.now());

        for (ScheduledPublication pub : pendentes) {
            try {
                itemController.publishDraft(pub.getDraftItemId());
                pub.setPublished(true);
                publicationRepository.save(pub);
            } catch (Exception e) {
                System.err.println("Erro ao publicar agendado: " + e.getMessage());
            }
        }
    }

    public ScheduledPublication agendar(Long draftId, Long userId, LocalDateTime schedule) {
        ScheduledPublication pub = new ScheduledPublication(null, draftId, userId, schedule, false);
        return publicationRepository.save(pub);
    }

    public List<ScheduledPublication> listarAgendados(Long userId) {
        return publicationRepository.findAll().stream()
                .filter(p -> p.getUserId().equals(userId))
                .toList();
    }
}
