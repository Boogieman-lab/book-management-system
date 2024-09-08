package com.example.bookmanagementsystembo.common.sidebar;

import com.example.bookmanagementsystembo.common.menu.MenuDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sidebar")
public class SidebarApiController {

    private final SidebarService sidebarService;

    @GetMapping
    public ResponseEntity<List<MenuDTO>> list() {
        List<MenuDTO> sidebarList = sidebarService.getSidebarMenus();
        return ResponseEntity.ok(sidebarList);
    }
}
