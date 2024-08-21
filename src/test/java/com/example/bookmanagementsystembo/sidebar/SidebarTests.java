package com.example.bookmanagementsystembo.sidebar;

import com.example.bookmanagementsystembo.common.menu.MenuDTO;
import com.example.bookmanagementsystembo.common.menu.MenuEntity;
import com.example.bookmanagementsystembo.common.menu.MenuRepository;
import com.example.bookmanagementsystembo.common.sidebar.SidebarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SidebarTests {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private SidebarService sidebarService;

    @DisplayName("관리자 사이드바 조회")
    @Test
    public void testGetSidebarMenus() {
        List<MenuDTO> sidebarDTO = sidebarService.getSidebarMenus();
        System.out.println(sidebarDTO);
        assertEquals(24, sidebarDTO.size());
    }
}
