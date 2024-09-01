package com.example.bookmanagementsystembo.menu;

import com.example.bookmanagementsystembo.common.menu.MenuApiController;
import com.example.bookmanagementsystembo.common.menu.MenuDTO;
import com.example.bookmanagementsystembo.common.menu.MenuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;

@WebMvcTest(MenuApiController.class)
public class MenuApiControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuService menuService;

    @Test
    public void testModifyActiveYn() throws Exception {
        // Given: 테스트를 위한 준비 작업
        int menuId = 1;

        // MenuDTO 객체를 생성하고 초기 값을 설정합니다.
        MenuDTO mockMenuDTO = new MenuDTO();
        mockMenuDTO.setMenuId(menuId);
        mockMenuDTO.setActiveYn('Y'); // 초기 상태 설정

        // menuService.getMenu(menuId)가 호출될 때 mockMenuDTO를 반환하도록 설정합니다.
        when(menuService.getMenu(anyInt())).thenReturn(mockMenuDTO);

        // modifyActiveYn 메서드가 호출될 때 아무 작업도 수행하지 않도록 설정합니다.
        doNothing().when(menuService).modifyActiveYn(any(MenuDTO.class));

        // When: PATCH 요청을 수행합니다.
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/menus/modify/{menuId}", menuId))
                .andExpect(MockMvcResultMatchers.status().isOk()); // HTTP 상태 코드가 200 OK인지 확인합니다.

        // Then: 서비스 메서드가 올바르게 호출되었는지 검증합니다.
        // getMenu 메서드가 올바른 menuId로 호출되었는지 확인합니다.
        verify(menuService).getMenu(menuId);

        // modifyActiveYn 메서드가 올바른 MenuDTO 객체로 호출되었는지 검증합니다.
        verify(menuService).modifyActiveYn(argThat(menuDTO ->
                menuDTO.getMenuId() == menuId
        ));
    }
}