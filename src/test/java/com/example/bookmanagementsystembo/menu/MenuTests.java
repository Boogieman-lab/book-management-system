package com.example.bookmanagementsystembo.menu;

import com.example.bookmanagementsystembo.common.menu.MenuEntity;
import com.example.bookmanagementsystembo.common.menu.MenuRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MenuTests {

    @MockBean
    private MenuRepository menuRepository;

    @DisplayName("메뉴 조회")
    @Test
    void testMenuFindAll() {
        // Mock 데이터를 설정
        List<MenuEntity> mockMenuList = Arrays.asList(
                MenuEntity.builder()
                        .menuId(1)
                        .menuName("메인")
                        .menuUrl("/main")
                        .parentId(null)
                        .menuOrder(1)
                        .activeYn('Y')
                        .menuType("USER")
                        .build(),
                MenuEntity.builder()
                        .menuId(2)
                        .menuName("서브")
                        .menuUrl("/sub")
                        .parentId(1)
                        .menuOrder(2)
                        .activeYn('Y')
                        .menuType("USER")
                        .build()
        );

        // 레포지토리의 findAll 메서드가 호출될 때, mock 데이터를 반환하도록 설정
        Mockito.when(menuRepository.findAll()).thenReturn(mockMenuList);

        // findAll을 호출하여 mock 데이터를 가져옴
        List<MenuEntity> menuList = menuRepository.findAll();

        assertEquals(2, menuList.size());

        MenuEntity me = menuList.get(0);
        assertEquals("메인", me.getMenuName());
    }

    @DisplayName("메뉴 ID로 조회")
    @Test
    void testMenuFindById() {
        // Mock 데이터를 설정
        MenuEntity mockMenu = MenuEntity.builder()
                .menuId(1)
                .menuName("메인")
                .menuUrl("/main")
                .parentId(null)
                .menuOrder(1)
                .activeYn('1')
                .menuType("USER")
                .build();

        // 레포지토리의 findById 메서드가 호출될 때, mock 데이터를 반환하도록 설정
        Mockito.when(menuRepository.findById(1)).thenReturn(Optional.of(mockMenu));

        // findById를 호출하여 mock 데이터를 가져옴
        Optional<MenuEntity> optionalMenu = menuRepository.findById(1);

        if (optionalMenu.isPresent()) {
            MenuEntity me = optionalMenu.get();
            assertEquals("메인", me.getMenuName());
        }
    }

    @DisplayName("메뉴 활성 여부 변경 테스트")
    @Test
    void testMenuUpdateActiveYn() {
        MenuEntity mockMenu = MenuEntity.builder()
                .menuId(1)
                .menuName("메인")
                .menuUrl("/main")
                .parentId(null)
                .menuOrder(1)
                .activeYn('Y')
                .menuType("USER")
                .build();

        Mockito.when(menuRepository.findById(1)).thenReturn(Optional.of(mockMenu));

        // 메뉴 활성화 상태를 'N'으로 업데이트
        mockMenu = MenuEntity.builder()
                .menuId(mockMenu.getMenuId())
                .menuName(mockMenu.getMenuName())
                .menuUrl(mockMenu.getMenuUrl())
                .parentId(mockMenu.getParentId())
                .menuOrder(mockMenu.getMenuOrder())
                .activeYn('N')
                .menuType(mockMenu.getMenuType())
                .build();

        // mock 데이터를 반환하도록 설정 (실제 save 동작을 흉내냄)
        Mockito.when(menuRepository.save(mockMenu)).thenReturn(mockMenu);

        MenuEntity updatedMenu = menuRepository.save(mockMenu);
        assertEquals('N', updatedMenu.getActiveYn());
    }

}
