package com.example.bookmanagementsystembo.common.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainViewController {

    /**
     * 기본 루트(/) 접근 시 부기맨 메인 소개 화면으로 안내합니다.
     */
    @GetMapping("/")
    public String index() {
        return "info/about";
    }

    /**
     * /about 명시적 경로로의 접근 역시 허용합니다.
     */
    @GetMapping("/about")
    public String about() {
        return "info/about";
    }
}
