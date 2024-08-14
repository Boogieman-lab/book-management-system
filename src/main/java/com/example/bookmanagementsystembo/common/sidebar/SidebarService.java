package com.example.bookmanagementsystembo.common.sidebar;

import com.example.bookmanagementsystembo.common.menu.MenuDTO;
import com.example.bookmanagementsystembo.common.menu.MenuEntity;
import com.example.bookmanagementsystembo.common.menu.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SidebarService {

    private final MenuRepository menuRepository;


    public List<MenuDTO> getSidebarMenu() {
        // 상위 메뉴 조회
        List<MenuEntity> parentMenus = menuRepository.findByParentIdIsNullAndMenuType("ADMIN");
        List<MenuDTO> sidebarDTOs = new ArrayList<>();

        for (MenuEntity parentMenu : parentMenus) {
            // 상위 메뉴를 DTO로 변환하여 추가
            MenuDTO parentDTO = MenuDTO.toSidebarMenu(
                    parentMenu.getMenuId(),
                    parentMenu.getMenuName(),
                    parentMenu.getMenuUrl(),
                    parentMenu.getParentId(),
                    parentMenu.getMenuOrder()
            );
            sidebarDTOs.add(parentDTO);

            // 하위 메뉴 조회 및 DTO로 변환하여 추가
            List<MenuEntity> childMenus = menuRepository.findByParentId(parentMenu.getMenuId());
            List<MenuDTO> childDTOs = childMenus.stream()
                    .map(childMenu -> MenuDTO.toSidebarMenu(
                            childMenu.getMenuId(),
                            childMenu.getMenuName(),
                            childMenu.getMenuUrl(),
                            childMenu.getParentId(),
                            childMenu.getMenuOrder()
                    ))
                    .collect(Collectors.toList());

            sidebarDTOs.addAll(childDTOs);
        }

        return sidebarDTOs;
    }
}
