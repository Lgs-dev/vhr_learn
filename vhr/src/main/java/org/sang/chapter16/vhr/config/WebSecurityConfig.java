package org.sang.chapter16.vhr.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sang.chapter16.vhr.entity.Hr;
import org.sang.chapter16.vhr.entity.Role;
import org.sang.chapter16.vhr.service.HrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.session.ConcurrentSessionFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lgs
 * @date 2021-03-26 15:48
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    HrService hrService;
    @Autowired
    CustomMetadataSource metadataSource;
    @Autowired
    UrlAccessDecisionManager urlAccessDecisionManager;


    //配置认证功能的 AuthenticationManagerBuilder
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //将创建的 hrService 配置到AuthenticationManagerBuilder 中
        auth.userDetailsService( hrService );
    }

    //配置加密
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //在 WebSecurity 中配置忽略的路径 WebSecurity
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers( "/css/**", "/js/**", "/index.html", "/img/**", "/fonts/**", "/favicon.ico", "/verifyCode" );
    }

    //角色继承  疑问：是否需要基于数据库的角色集成？如何实现？
//    @Bean
//    RoleHierarchy roleHierarchy(){
//        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
//        String hierarchy = "ROLE_dba > Role_admin ROLE_admin > Role_user";
//        roleHierarchy.setHierarchy( hierarchy );
//        return roleHierarchy;
//    }

    @Bean
    LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setAuthenticationSuccessHandler( (request, response, authentication) -> {
            response.setContentType( "application/json;charset=utf-8" );
            PrintWriter out = response.getWriter();
            Hr hr = (Hr) authentication.getPrincipal();
            hr.setPassword( null );
            Map<String, Object> ok = new HashMap<>();
            ok.put( "status", "200" );
            ok.put( "msg", "登录成功！" );
            ok.put( "obj", hr );
            String s = new ObjectMapper().writeValueAsString( ok );
            out.write( s );
            out.flush();
            out.close();
        } );
        loginFilter.setAuthenticationFailureHandler( (request, response, exception) -> {
            response.setContentType( "application/json;charset=utf-8" );
            PrintWriter out = response.getWriter();
            Map<String, Object> ok = new HashMap<>();
            ok.put( "status", "400" );
            if (exception instanceof LockedException) {
                ok.put( "msg", "账户被锁定，请联系管理员!" );
            } else if (exception instanceof CredentialsExpiredException) {
                ok.put( "msg", "密码过期，请联系管理员!" );
            } else if (exception instanceof AccountExpiredException) {
                ok.put( "msg", "账户过期，请联系管理员!" );
            } else if (exception instanceof DisabledException) {
                ok.put( "msg", "账户被禁用，请联系管理员!" );
            } else if (exception instanceof BadCredentialsException) {
                ok.put( "msg", "用户名或者密码输入错误，请重新输入!" );
            } else{
                ok.put( "msg", exception.getMessage() );
            }
            System.out.println( exception.getMessage() );
            out.write( new ObjectMapper().writeValueAsString( ok ) );
            out.flush();
            out.close();
        } );
        loginFilter.setAuthenticationManager( authenticationManagerBean() );
        loginFilter.setFilterProcessesUrl( "/doLogin" );
        ConcurrentSessionControlAuthenticationStrategy sessionStrategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
        sessionStrategy.setMaximumSessions(1);
        loginFilter.setSessionAuthenticationStrategy(sessionStrategy);
        return loginFilter;
    }

    @Bean
    SessionRegistryImpl sessionRegistry() {
        return new SessionRegistryImpl();
    }

    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().withObjectPostProcessor( new ObjectPostProcessor<FilterSecurityInterceptor>() {
            @Override
            public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                object.setSecurityMetadataSource( metadataSource );
                object.setAccessDecisionManager( urlAccessDecisionManager );
                return object;
            }
        } ).and().logout().logoutSuccessHandler( (req, resp, authentication) -> {
            resp.setContentType( "application/json;charset=utf-8" );
            PrintWriter out = resp.getWriter();
            out.write( new ObjectMapper().writeValueAsString(  "注销成功!"  ) );
            out.flush();
            out.close();
        } ).permitAll().and().csrf().disable().exceptionHandling()
                //没有认证时，在这里处理结果，不要重定向
                .authenticationEntryPoint( (req, resp, authException) -> {
                    resp.setContentType( "application/json;charset=utf-8" );
                    resp.setStatus( 401 );
                    PrintWriter out = resp.getWriter();
                    out.write( new ObjectMapper().writeValueAsString( "请求失败，请联系管理员!" ) );
                    out.flush();
                    out.close();
                } );
        //以下这句就可以控制单个用户只能创建一个session，也就只能在服务器登录一次 : 但是没有起作用
//        http.sessionManagement().maximumSessions( 1 ).expiredUrl( "/doLogin" );
        http.addFilterAt( new ConcurrentSessionFilter( sessionRegistry(), event -> {
            HttpServletResponse resp = event.getResponse();
            resp.setContentType( "application/json;charset=utf-8" );
            resp.setStatus( 401 );
            PrintWriter out = resp.getWriter();
            out.write( new ObjectMapper().writeValueAsString( "您已在另一台设备登录，本次登录已下线!" ) );
            out.flush();
            out.close();
        } ), ConcurrentSessionFilter.class );
        http.addFilterAt( loginFilter(), UsernamePasswordAuthenticationFilter.class );

    }

//    //在 HttpSecurity 配置拦截规则、表单登录、登录成功或失败的相应
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        // authorizeRequests() 方法开启 HttpSecurity 的配置
//        http.authorizeRequests().withObjectPostProcessor( new ObjectPostProcessor<FilterSecurityInterceptor>() {
//            //定义FilterSecurityInterceptor时，加入自定义的实例
//            @Override
//            public <O extends FilterSecurityInterceptor> O postProcess(O object) {
//                object.setSecurityMetadataSource( metadataSource );
//                object.setAccessDecisionManager( urlAccessDecisionManager );
//                return object;
//            }
//        } ).and()
//                /* 设置登录 */.formLogin()//表示开启表单登录配置，即一开始看到的登录页面，同时配置了登录接口"/login"
//                //.loginPage( "/login" ) //设置自定义登录界面
//                .loginProcessingUrl( "/login" ) //表示登录请求处理接口
//                .usernameParameter( "username" )  //设置认证需要的用户名和密码的参数
//                .passwordParameter( "password" ).successHandler( new AuthenticationSuccessHandler() { //登录成功后的处理逻辑
//            @Override
//            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                Object principal = authentication.getPrincipal();
//                response.setContentType( "application/json;charset=utf-8" );
//                PrintWriter out = response.getWriter();
//                response.setStatus( 200 );
//                Map<String, Object> map = new HashMap<>();
//                map.put( "status", 200 );
//                map.put( "msg", principal );
//                ObjectMapper om = new ObjectMapper();
//                out.write( om.writeValueAsString( map ) );
//                out.flush();
//                out.close();
//            }
//        } ).failureHandler( new AuthenticationFailureHandler() { //登录失败后的处理逻辑
//            //AuthenticationException exception 通过这个异常参数可以获取登录失败的原因，给用户一个明确提示
//            @Override
//            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//                response.setContentType( "application/json;charset=utf-8" );
//                PrintWriter out = response.getWriter();
//                response.setStatus( 401 );
//                Map<String, Object> map = new HashMap<>();
//                map.put( "status", 401 );
//                if (exception instanceof CredentialsExpiredException) {
//                    map.put( "msg", "密码已过期，登录失败！" );
//                } else {
//                    map.put( "msg", "登陆失败！" );
//                }
//                ObjectMapper om = new ObjectMapper();
//                out.write( om.writeValueAsString( map ) );
//                out.flush();
//                out.close();
//            }
//        } ).permitAll() //表示和登录相关的接口都不需要认证即可访问
//                .and()
//                /*设置登出*/
//                //表示等处
//                .logout() //表示开启注销登录的配置
//                .logoutUrl( "/logout" ) //表示配置注销登录请求URL为 "/logout",默认也是"/logout"
//                .clearAuthentication( true ) //是否清除身份认证信息, 默认 true
//                .invalidateHttpSession( true ) //是否使session试校，默认 true
//                .addLogoutHandler( new LogoutHandler() { //配置一个LogoutHandler，在这个里面完成一些数据清除工作，例如Cookie 的清除
//                    @Override
//                    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//
//                    }
//                } ).logoutSuccessHandler( new LogoutSuccessHandler() { //登录成功后的处理逻辑，返回一段json 或者 跳转到登录界面
//            @Override
//            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                response.sendRedirect( "/login" ); //重定位到自定义的登录界面
//            }
//        } ).permitAll().and().csrf() //表示关闭 csrf
//                .disable();
//
//    }


}
