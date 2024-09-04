package com.example.bookmanagementsystembo.common.menu;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MenuDTO {
    private int menuId;
    private String menuName;
    private String menuUrl;
    private Integer parentId;
    private int menuOrder;
    private char activeYn;
    private String menuType;

    @Builder
    public MenuDTO(int menuId, String menuName, String menuUrl, Integer parentId, int menuOrder, char activeYn, String menuType) {
        this.menuId = menuId;
        this.menuName = menuName;
        this.menuUrl = menuUrl;
        this.parentId = parentId;
        this.menuOrder = menuOrder;
        this.activeYn = activeYn;
        this.menuType = menuType;
    }

    // 엔티티를 DTO로 변환
    public static MenuDTO toMenuDTO(MenuEntity menuEntity) {
        return MenuDTO.builder()
                .menuId(menuEntity.getMenuId())
                .menuName(menuEntity.getMenuName())
                .menuUrl(menuEntity.getMenuUrl())
                .parentId(menuEntity.getParentId())
                .menuOrder(menuEntity.getMenuOrder())
                .activeYn(menuEntity.getActiveYn())
                .menuType(menuEntity.getMenuType())
                .build();
    }

    // 사이드바 메뉴를 위한 생성자
    public static MenuDTO toSidebarMenu(int menuId, String menuName, String menuUrl, Integer parentId, int menuOrder) {
        return MenuDTO.builder()
                .menuId(menuId)
                .menuName(menuName)
                .menuUrl(menuUrl)
                .parentId(parentId)
                .menuOrder(menuOrder)
                .build();
    }
}
