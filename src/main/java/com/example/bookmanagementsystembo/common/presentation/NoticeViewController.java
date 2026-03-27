package com.example.bookmanagementsystembo.common.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 공지사항 뷰 컨트롤러.
 * <p>이용안내 및 시스템 공지사항 페이지(UI-INF-002)를 제공합니다.</p>
 */
@Controller
public class NoticeViewController {

    /**
     * 공지사항 목록 페이지를 반환합니다.
     *
     * @return notices/list 템플릿
     */
    @GetMapping("/notices")
    public String notices() {
        return "notices/list";
    }
}
