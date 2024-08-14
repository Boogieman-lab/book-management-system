package com.example.bookmanagementsystembo.common.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    // 메뉴 항목 리스트 조회
    public List<MenuDTO> getMenuList(int page, String searchMenuName) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.asc("menuOrder")));

        Page<MenuEntity> menuPage;
        if (searchMenuName == null || searchMenuName.trim().isEmpty()) {
            menuPage = menuRepository.findAll(pageable);
        } else {
            // 검색어가 있을 때
            menuPage = menuRepository.findByMenuNameContaining(searchMenuName, pageable);
        }

        return menuPage.getContent().stream().map(MenuDTO::toMenuDTO).toList();
    }

}
