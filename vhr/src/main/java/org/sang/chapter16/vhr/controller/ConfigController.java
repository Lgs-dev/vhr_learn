package org.sang.chapter16.vhr.controller;

import org.sang.chapter16.vhr.entity.Menu;
import org.sang.chapter16.vhr.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lgs
 * @date 2021-03-29 09:45
 */
@RestController
@RequestMapping("/system/config")
public class ConfigController {
    @Autowired
    MenuService menuService;

    @GetMapping("/menu")
    public List<Menu> sysmenu() {
        return menuService.getMenusByHrid();
    }
}
