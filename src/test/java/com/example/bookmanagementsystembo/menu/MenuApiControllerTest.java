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
        int menuId = 1;
        char activeYn = 'Y';

        // Perform PATCH request
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/menus/modify/{menuId}/{activeYn}", menuId, activeYn))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify that the service method was called with correct parameters
        verify(menuService).modifyActiveYn(any(MenuDTO.class));
    }
}