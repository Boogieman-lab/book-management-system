package com.example.bookmanagementsystembo.common.menu;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<MenuEntity, Integer> {
    Page<MenuEntity> findByMenuNameContaining(String searchMenuName, Pageable pageable);

    List<MenuEntity> findByParentIdIsNullAndMenuType(String MenuType); // 상위 메뉴 조회

    List<MenuEntity> findByParentId(Integer parentId); // 하위 메뉴 조회

}
