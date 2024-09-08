package com.example.bookmanagementsystembo.common.menu;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "menu_tbl")
public class MenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private int menuId;

    @Column(name = "menu_name", nullable = false)
    private String menuName;

    @Column(name = "menu_url")
    private String menuUrl;

    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "menu_order", nullable = false)
    private int menuOrder;

    @Column(name = "active_yn", columnDefinition = "char(1) DEFAULT 'Y'")
    private char activeYn;

    @Column(name = "menu_type", columnDefinition = "varchar(10) DEFAULT 'USER'")
    private String menuType;

    @Builder
    public MenuEntity(int menuId, String menuName, String menuUrl, Integer parentId, int menuOrder, char activeYn, String menuType) {
        this.menuId = menuId;
        this.menuName = menuName;
        this.menuUrl = menuUrl;
        this.parentId = parentId;
        this.menuOrder = menuOrder;
        this.activeYn = activeYn;
        this.menuType = menuType;
    }

    // 상태를 반전시키는 메서드
    public void toggleActiveYn() {
        this.activeYn = (this.activeYn == 'Y') ? 'N' : 'Y';
    }
}
