package org.sang.chapter16.vhr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;

/**
 * 该类中进行角色信息的比对
 * @author lgs
 * @date 2021-03-26 15:37
 */
@Component
public class UrlAccessDecisionManager implements AccessDecisionManager {

    /**
     * decide ：判决
     *  判断当权用户是否具备请求需要的角色，未抛出异常，则说明请求可以通过。
     * @param authentication 用户的；包含当前登录用户的信息
     * @param object  一个FilterInvocation对象，可以获取当前请求对象
     * @param configAttributes 用户请求的；FilterInvovationSecurity 中的getAttributes方法的返回值，即当前请求URL所 需要的角色
     * @throws AccessDeniedException
     * @throws InsufficientAuthenticationException
     */
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        Iterator<ConfigAttribute> iterator = configAttributes.iterator();
        while (iterator.hasNext()){
            ConfigAttribute ca = iterator.next();
            String needRole = ca.getAttribute();
            // 如果需要的角色是 "ROLE_LOGIN",说明当前请求的URL用户登录后即可访问
            if("ROLE_LOGIN".equals( needRole )){
                if(authentication instanceof AnonymousAuthenticationToken){
                    throw new BadCredentialsException( "未登录" );
                }else {
                    return;
                }
            }
            //判断当前用户请求需要的角色
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for(GrantedAuthority authority : authorities){
                if(authority.getAuthority().equals( needRole )){
                    return;
                }
            }
        }
        throw new AccessDeniedException( "权限不足" );
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
