package com.example.bookmanagementsystembo.common.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/menus")
public class MenuApiController {

    private final MenuService menuService;

    // 메뉴 항목 리스트 조회
    @GetMapping
    public ResponseEntity<List<MenuDTO>> list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "searchMenuName", defaultValue = "") String searchMenuName) {
        List<MenuDTO> menuList = menuService.getMenuList(page, searchMenuName);
        return ResponseEntity.ok(menuList);
    }

    @PatchMapping("/modify/{id}")
    public ResponseEntity<Void> modifyActiveYn(@PathVariable("menuId") int menuId, @PathVariable("activeYn") char activeYn) {
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setMenuId(menuId);
        menuDTO.setActiveYn(activeYn);
        menuService.modifyActiveYn(menuDTO);
        return ResponseEntity.ok().build();
    }


}