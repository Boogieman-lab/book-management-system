package com.example.bookmanagementsystembo.admin.controller;

import com.example.bookmanagementsystembo.admin.dto.AdminStatsOverviewResponse;
import com.example.bookmanagementsystembo.admin.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자 통계 API.
 *
 * GET /api/v1/admin/stats/overview — 대시보드 통계 개요
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/stats")
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    /** 대시보드 통계 개요 조회 */
    @GetMapping("/overview")
    public ResponseEntity<AdminStatsOverviewResponse> getOverview() {
        return ResponseEntity.ok(adminStatsService.getOverview());
    }
}
