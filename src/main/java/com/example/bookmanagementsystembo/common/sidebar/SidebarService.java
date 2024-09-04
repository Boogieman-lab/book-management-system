package com.example.bookmanagementsystembo.common.sidebar;

import com.example.bookmanagementsystembo.common.menu.MenuDTO;
import com.example.bookmanagementsystembo.common.menu.MenuEntity;
import com.example.bookmanagementsystembo.common.menu.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SidebarService {

    private final MenuRepository menuRepository;

    public List<MenuDTO> getSidebarMenus() {
        List<MenuEntity> menus = menuRepository.findByMenuTypeAndActiveYn("ADMIN", 'Y');
        List<MenuDTO> sidebarMenus = new ArrayList<>();
        for (MenuEntity menu : menus) {
            sidebarMenus.add(MenuDTO.toSidebarMenu(
                    menu.getMenuId()
                    , menu.getMenuName()
                    , menu.getMenuUrl()
                    , menu.getParentId()
                    , menu.getMenuOrder()
            ));
        }
        return sidebarMenus;
    }
}
