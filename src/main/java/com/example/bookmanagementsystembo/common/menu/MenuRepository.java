package com.example.bookmanagementsystembo.common.menu;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<MenuEntity, Integer> {
    Page<MenuEntity> findByMenuNameContaining(String searchMenuName, Pageable pageable);

    List<MenuEntity> findByMenuTypeAndActiveYn(String menuType, char activeYn);

}
