package com.example.bookmanagementsystembo.menu;

import com.example.bookmanagementsystembo.common.menu.MenuDTO;
import com.example.bookmanagementsystembo.common.menu.MenuEntity;
import com.example.bookmanagementsystembo.common.menu.MenuRepository;
import com.example.bookmanagementsystembo.common.menu.MenuService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private MenuService menuService;

    @Test
    public void testModifyActiveYn() {
        // Given
        int menuId = 1;
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setMenuId(menuId);
        menuDTO.setActiveYn('Y');

        MenuEntity menuEntity = MenuEntity.builder()
                .menuId(menuId)
                .menuName("Sample Menu")
                .menuUrl("/sample")
                .parentId(0)
                .menuOrder(1)
                .activeYn('N')
                .menuType("ADMIN")
                .build();

        when(menuRepository.findById(menuId)).thenReturn(java.util.Optional.of(menuEntity));

        // When
        menuService.modifyActiveYn(menuDTO);

        // Then
        verify(menuRepository).findById(menuId);
        assertEquals('Y', menuEntity.getActiveYn());
    }
}
