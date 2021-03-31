package org.sang.chapter16.vhr.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lgs
 * @date 2021-03-27 10:49
 */
@RestController
public class testController {
    //
    @GetMapping("/employee/basic/hello")
    public String manager() {
        return "hello manager!";
    }

    @GetMapping("/personnel/remove/hello")
    public String personnel(){
        return "hello personnel!";
    }

}
