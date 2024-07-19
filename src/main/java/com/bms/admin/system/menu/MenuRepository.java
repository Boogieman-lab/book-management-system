package com.bms.admin.system.menu;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<MenuEntity, Integer> {
    Page<MenuEntity> findByMenuNameContaining(String searchMenuName, Pageable pageable);
}
