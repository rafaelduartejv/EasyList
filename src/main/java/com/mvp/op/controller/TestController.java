package com.mvp.op.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Test", description = "Endpoints for testing authorization")
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Access admin-only endpoint", description = "Endpoint restricted to users with ROLE_ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Access granted"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public String adminOnly() {
        return "Acessado apenas por administradores";
    }
}