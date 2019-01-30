package com.example.securitydemo.conf;

import com.example.securitydemo.authentication.UserDetailService;
import com.example.securitydemo.authentication.conf.CustomMetadataSource;
import com.example.securitydemo.authentication.conf.UrlAccessDecisionManager;
import com.example.securitydemo.authentication.handler.AuthenticationAccessDeniedHandler;
import com.example.securitydemo.authentication.handler.MyAuthenctiationFailureHandler;
import com.example.securitydemo.authentication.handler.MyAuthenctiationSuccessHandler;
import com.example.securitydemo.authentication.validate.jwt.JWTAuthenticationFilter;
import com.example.securitydemo.authentication.validate.jwt.JWTLoginFilter;
import com.example.securitydemo.authentication.validate.smscode.SmsAuthenticationConfig;
import com.example.securitydemo.authentication.validate.smscode.SmsCodeFilter;
import com.example.securitydemo.authentication.validate.imgcode.ImgageCodeFilter;
import com.example.securitydemo.common.dto.ReturnData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    protected MyAuthenctiationSuccessHandler myAuthenctiationSuccessHandler;

    @Autowired
    protected MyAuthenctiationFailureHandler myAuthenctiationFailureHandler;

    @Autowired
    DataSource dataSource;

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private ImgageCodeFilter validateCodeFilter;


    @Autowired
    private SmsCodeFilter smsCodeFilter;

    @Autowired
    private SmsAuthenticationConfig smsAuthenticationConfig;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    CustomMetadataSource metadataSource;

    @Autowired
    UrlAccessDecisionManager urlAccessDecisionManager;

    @Autowired
    AuthenticationAccessDeniedHandler deniedHandler;

    /**
     * 自定义认证
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    /**
     * 密码加密
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 实现token持久化
     *
     * @return
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
//        jdbcTokenRepository.setCreateTableOnStartup(true);
        return jdbcTokenRepository;
    }

    /**
     * 不需要验证的地址过滤
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/login.html",
                "/logout",
                "/security/code/image",
                "/security/code/sms",
                "/security/code/imageCode",
                "/user/register",
                "/user/change",
                "/authentication/require",
                "/test/**",
                "/swagger-ui.html",
                "/webjars/**",
                "/v2/**",
                "/swagger-resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                        o.setSecurityMetadataSource(metadataSource);
                        o.setAccessDecisionManager(urlAccessDecisionManager);
                        return o;
                    }
                })
                .and()
                .addFilter(new JWTLoginFilter(authenticationManager(), myAuthenctiationSuccessHandler, myAuthenctiationFailureHandler))
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilterBefore(smsCodeFilter, UsernamePasswordAuthenticationFilter.class) // 添加短信验证码校验过滤器
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
//                .addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class) // 添加验证码校验过滤器
                .formLogin()                    //  定义当需要用户登录时候，转到的登录页面。
//                .loginPage("/login.html")           // 设置登录页面
                .loginPage("/authentication/require")           // 设置登录页面（提示登录接口）
                .loginProcessingUrl("/user/login")  // 自定义的登录接口
                .successHandler(myAuthenctiationSuccessHandler) // 自定义登录成功处理
                .failureHandler(myAuthenctiationFailureHandler) // 自定义登录失败处理
                .permitAll()
                .and()
                .apply(smsAuthenticationConfig)// 将短信验证码认证配置加到 Spring Security 中
//                .rememberMe()
//                .tokenRepository(persistentTokenRepository()) // 配置 token 持久化仓库
//                .tokenValiditySeconds(3600) // remember 过期时间，单为秒
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write(objectMapper.writeValueAsString(new ReturnData("200", "注销成功")));
                    }
                })
                .permitAll()
                .and()
                .csrf().disable()         // 关闭csrf防护
                .exceptionHandling().accessDeniedHandler(deniedHandler);
    }


}
