package org.sang.chapter16.vhr.utils;

import org.sang.chapter16.vhr.entity.Hr;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *  工具方法，用来返回当前登录
 * @author lgs
 * @date 2021-03-29 09:44
 */
public class HrUtils {
    public static Hr getCurrentHr(){
        return (Hr) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
