package com.bms.admin.menu;

import com.bms.admin.system.menu.MenuEntity;
import com.bms.admin.system.menu.MenuRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MenuTests {

    @Autowired
    private MenuRepository menuRepository;

    @Test
    void menuFindAllTest() {
        List<MenuEntity> menuList = menuRepository.findAll();
        menuList.forEach(menu -> System.out.println(menu.getMenuName()));
    }
}