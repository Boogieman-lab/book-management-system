package com.example.bookmanagementsystembo.user.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 마이페이지 뷰 컨트롤러.
 * {@code /user/mypage} 경로를 처리하며, tab 파라미터로 초기 활성 탭을 지정합니다.
 */
@Controller
public class MypageViewController {

    /**
     * 마이페이지 메인 화면을 렌더링합니다.
     *
     * @param tab   활성화할 탭 식별자 (profile | borrows | reserv | noti), 기본값 profile
     * @param model 뷰에 전달할 모델
     * @return 마이페이지 템플릿 경로
     */
    @GetMapping("/user/mypage")
    public String mypage(@RequestParam(defaultValue = "profile") String tab, Model model) {
        model.addAttribute("activeMenu", "mypage");
        model.addAttribute("tab", tab);
        return "user/mypage";
    }
}
