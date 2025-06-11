package com.mvp.op.controller;

import com.mvp.op.model.ItemMetrics;
import com.mvp.op.service.ItemMetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Item Metrics", description = "Endpoints for querying and storing Mercado Livre item metrics")
@RestController
@RequestMapping("/metrics")
public class ItemMetricsController {

    @Autowired
    private ItemMetricsService itemMetricsService;

    @Operation(summary = "Fetch and store item metrics by time window", description = "Retrieves item visits for the last N units of time and stores them in the database")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Metrics successfully saved"),
            @ApiResponse(responseCode = "404", description = "Token not found or no visit data available"),
            @ApiResponse(responseCode = "400", description = "Error during metrics retrieval")
    })
    @GetMapping("/fetch/time-window")
    public ResponseEntity<?> fetchMetricsTimeWindow(
            @RequestParam Long userId,
            @RequestParam String itemId,
            @RequestParam int last,
            @RequestParam String unit) {
        return itemMetricsService.fetchMetricsTimeWindow(userId, itemId, last, unit);
    }

    @Operation(summary = "Fetch and store item metrics by date range", description = "Retrieves item visits between a start and end date and stores them in the database")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Metrics successfully saved"),
            @ApiResponse(responseCode = "404", description = "Token not found or no visit data available"),
            @ApiResponse(responseCode = "400", description = "Error during metrics retrieval")
    })
    @GetMapping("/fetch/date-range")
    public ResponseEntity<?> fetchMetricsDateRange(
            @RequestParam Long userId,
            @RequestParam String itemId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return itemMetricsService.fetchMetricsDateRange(userId, itemId, start, end);
    }

    @Operation(summary = "Retrieve saved item metrics by date range", description = "Fetches stored item visits from the database between the given dates")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Metrics successfully retrieved")
    })
    @GetMapping("/history")
    public ResponseEntity<List<ItemMetrics>> getMetricsHistory(
            @RequestParam Long userId,
            @RequestParam String itemId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return itemMetricsService.getSavedMetrics(userId, itemId, start, end);
    }
}
