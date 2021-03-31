package org.sang.chapter16.vhr.service;

import org.sang.chapter16.vhr.entity.Hr;
import org.sang.chapter16.vhr.entity.Role;
import org.sang.chapter16.vhr.mapper.HrMapper;
import org.sang.chapter16.vhr.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author lgs
 * @date 2021-03-26 14:58
 */
@Service
public class HrService implements UserDetailsService {
    @Autowired
    HrMapper hrMapper;


    /**
     * 通过用户名去数据库中查找用户，没有抛出异常；找到用户，则继续查找该用户具有的角色信息；
     * 该方法在用户登录的时候自动调用
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Hr hr = hrMapper.loadUserByUsername(username);
        if(hr == null){
            throw new UsernameNotFoundException( "用户名不存在！" );
        }
        hr.setRoles(hrMapper.getHrRolesById(hr.getId()));
        return hr;
    }
}
