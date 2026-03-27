package com.example.bookmanagementsystembo.admin.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 전용 뷰 컨트롤러.
 * 모든 /admin/** 경로는 SecurityConfig에 의해 ROLE_ADMIN 권한이 요구됩니다.
 */
@Controller
@RequestMapping("/admin")
public class AdminViewController {

    /** 통계 대시보드 메인 화면 */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    /** 도서 센터 (신규 등록 및 실물 추가) */
    @GetMapping("/books")
    public String books() {
        return "admin/books";
    }

    /** 대출/반납 관제 */
    @GetMapping("/borrows")
    public String borrows() {
        return "admin/borrows";
    }

    /** 희망도서 결재함 (승인/거절) */
    @GetMapping("/requests")
    public String requests() {
        return "admin/requests";
    }

    /** 임직원 권한 롤 관리 */
    @GetMapping("/users")
    public String users() {
        return "admin/users";
    }
}
