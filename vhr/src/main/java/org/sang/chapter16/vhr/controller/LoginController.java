package org.sang.chapter16.vhr.controller;

import org.sang.chapter16.vhr.config.VerificationCode;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author lgs
 * @date 2021-03-29 11:12
 */
@RestController
public class LoginController {

//    @GetMapping("/login")
//    public RespBean login() {
//        return RespBean.error("尚未登录，请登录！");
//    }

    @GetMapping("/verifyCode")
    public void verifyCode(HttpServletRequest request,HttpServletResponse response) throws IOException{
        //疑问，如何校验 验证码？
        VerificationCode code = new VerificationCode();
        BufferedImage image = code.getImage();
        String text = code.getText();
        //session中保存验证码
        HttpSession session = request.getSession(true);
        System.out.println( request.getSession( true ).getId() );
        System.out.println( request.getSession( true ).getId() );
        System.out.println( request.getSession( true ).getId() );
        session.setAttribute( "verify_code", text );
        VerificationCode.output( image, response.getOutputStream() );
    }


}
