package com.mvp.op.controller;

import com.mvp.op.service.ScheduledPublicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/schedule")
@Tag(name = "Scheduled Publications", description = "Endpoints to schedule Mercado Livre item publications")
public class ScheduledPublicationController {

    @Autowired
    private ScheduledPublicationService publicationService;

    @PostMapping
    @Operation(summary = "Schedule item publication", description = "Schedule a future post for a draft")
    public ResponseEntity<?> agendar(
            @RequestParam Long draftId,
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduleTime) {
        return ResponseEntity.ok(publicationService.agendar(draftId, userId, scheduleTime));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "List appointments for a user")
    public ResponseEntity<?> listar(@PathVariable Long userId) {
        return ResponseEntity.ok(publicationService.listarAgendados(userId));
    }
}
