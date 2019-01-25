package com.example.securitydemo.authentication.conf;

import com.example.securitydemo.authentication.UserDetailService;
import com.example.securitydemo.authentication.handler.MyAuthenctiationFailureHandler;
import com.example.securitydemo.authentication.handler.MyAuthenctiationSuccessHandler;
import com.example.securitydemo.authentication.validate.jwt.JWTAuthenticationFilter;
import com.example.securitydemo.authentication.validate.jwt.JWTLoginFilter;
import com.example.securitydemo.authentication.validate.smscode.SmsAuthenticationConfig;
import com.example.securitydemo.authentication.validate.smscode.SmsCodeFilter;
import com.example.securitydemo.authentication.validate.imgcode.ImgageCodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

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

    @Override
    protected void configure(HttpSecurity http) throws Exception {


        http
                .addFilter(new JWTLoginFilter(authenticationManager(), myAuthenctiationSuccessHandler, myAuthenctiationFailureHandler))
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
//                .addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class) // 添加验证码校验过滤器
                .addFilterBefore(smsCodeFilter, UsernamePasswordAuthenticationFilter.class) // 添加短信验证码校验过滤器
                .formLogin()                    //  定义当需要用户登录时候，转到的登录页面。
//                .loginPage("/login.html")           // 设置登录页面
                .loginPage("/authentication/require")           // 设置登录页面
                .loginProcessingUrl("/login")  // 自定义的登录接口
                .successHandler(myAuthenctiationSuccessHandler) // 自定义登录成功处理
                .failureHandler(myAuthenctiationFailureHandler) // 自定义登录失败处理
                .and()
                .rememberMe()
                .tokenRepository(persistentTokenRepository()) // 配置 token 持久化仓库
                .tokenValiditySeconds(3600) // remember 过期时间，单为秒
                .userDetailsService(userDetailService) // 处理自动登录逻辑
                .and()
                .authorizeRequests()        // 授权配置
                .antMatchers("/login.html",
                        "/code/image",
                        "/code/sms",
                        "/code/imageCode",
                        "/user/register",
                        "/authentication/require",
                        "/test/**",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/v2/**",
                        "/swagger-resources/**").permitAll()     // 设置所有人都可以访问登录页面
                .anyRequest()               // 任何请求,登录后可以访问
                .authenticated()
                .and()
                .csrf().disable()         // 关闭csrf防护
                .apply(smsAuthenticationConfig); // 将短信验证码认证配置加到 Spring Security 中
    }


}
