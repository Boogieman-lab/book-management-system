package com.example.bookmanagementsystembo.sidebar;

import com.example.bookmanagementsystembo.common.menu.MenuDTO;
import com.example.bookmanagementsystembo.common.menu.MenuEntity;
import com.example.bookmanagementsystembo.common.menu.MenuRepository;
import com.example.bookmanagementsystembo.common.sidebar.SidebarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SidebarTests {

    @MockBean
    private MenuRepository menuRepository;

    @Autowired
    private SidebarService sidebarService;

    @DisplayName("사이드바 상위 및 하위 조회")
    @Test
    public void testGetSidebarMenu() {
        // Given
        MenuEntity parentMenu = MenuEntity.builder()
                .menuId(1)
                .menuName("Main")
                .menuUrl("/")
                .parentId(null)
                .menuOrder(1)
                .activeYn('Y')
                .menuType("ADMIN")
                .build();

        MenuEntity childMenu = MenuEntity.builder()
                .menuId(2)
                .menuName("Books")
                .menuUrl("/books")
                .parentId(1)
                .menuOrder(1)
                .activeYn('Y')
                .menuType("ADMIN")
                .build();
        Mockito.when(menuRepository.findByParentIdIsNullAndMenuType("ADMIN")).thenReturn(Arrays.asList(parentMenu));
        Mockito.when(menuRepository.findByParentId(1)).thenReturn(Arrays.asList(childMenu));

        List<MenuDTO> sidebarDTO = sidebarService.getSidebarMenu();
        // Create expected DTOs using the fromSidebarMenu method
        List<MenuDTO> expectedSidebarDTO = Arrays.asList(
                MenuDTO.toSidebarMenu(1, "Main", "/", null, 1),
                MenuDTO.toSidebarMenu(2, "Books", "/books", 1, 1)
        );

        // Assertions
        assertEquals(expectedSidebarDTO.size(), sidebarDTO.size());
        for (int i = 0; i < expectedSidebarDTO.size(); i++) {
            assertEquals(expectedSidebarDTO.get(i), sidebarDTO.get(i));
        }
    }
}
