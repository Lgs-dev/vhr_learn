package org.sang.chapter16.vhr.service;

import org.sang.chapter16.vhr.entity.Menu;
import org.sang.chapter16.vhr.mapper.MenuMapper;
import org.sang.chapter16.vhr.utils.HrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author lgs
 * @date 2021-03-26 15:05
 */
@Service
@Transactional
@CacheConfig(cacheNames = "menus_cache")
public class MenuService {
    @Autowired
    MenuMapper menuMapper;

    @Cacheable(key = "#root.methodName")
    public List<Menu> getAllMenu() {
        return menuMapper.getAllMenu();
    }

    public List<Menu> getMenusByHrid(){
        return menuMapper.getMenusByHrId( HrUtils.getCurrentHr().getId() );
    }
}
