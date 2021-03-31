package org.sang.chapter16.vhr.config;

import org.sang.chapter16.vhr.entity.Menu;
import org.sang.chapter16.vhr.entity.Role;
import org.sang.chapter16.vhr.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;

/**
 * 该类用于获取一个请求需要的角色
 * @author lgs
 * @date 2021-03-26 15:09
 */
@Component
public class CustomMetadataSource implements FilterInvocationSecurityMetadataSource{
    @Autowired
    MenuService menuService;
    //创建一个 AntPathMatcher,主要用来实现ant风格的URL匹配
    AntPathMatcher antPathMatcher = new AntPathMatcher();
    //getAttributes方法 ， 确定一个请求需要哪些角色
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
       //当前请求的URL
        String requestUrl = ((FilterInvocation) object).getRequestUrl();
        //获取数据库中的所有资源信息，返回访问requestUrl 需要的 roles 信息
        List<Menu> allMenu = menuService.getAllMenu();
        for(Menu menu : allMenu){
            if(antPathMatcher.match( menu.getUrl(),requestUrl) && menu.getRoles().size()>0){
                List<Role> roles = menu.getRoles();
                int size = roles.size();
                String[] values = new String[size];
                for(int i=0;i<size;i++){
                    values[i] = roles.get( i ).getName();
                }
                //返回需要的角色列表LIST
                return SecurityConfig.createList( values );
            }
        }
        //"ROLE_LOGIN" 表示该请求登录后即可访问
        return SecurityConfig.createList( "ROLE_LOGIN" );
    }

    //用来返回所有定义好的权限资源 ，spring security在启动的时会校验相关的配置是否正确，如果不需要校验，直接返回null
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom( clazz );
    }
}
