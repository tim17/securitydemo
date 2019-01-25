package com.example.securitydemo.authentication.validate.jwt;

import com.example.securitydemo.authentication.handler.TokenAuthenticationHandler;
import com.example.securitydemo.common.dto.ReturnData;
import com.example.securitydemo.model.User;
import com.example.securitydemo.util.FastJsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {

    public static final String USERNAME_KEY = "username";
    private String usernameParameter = USERNAME_KEY;
    public static final String PWD_KEY = "password";
    private String passwordParameter = PWD_KEY;

    public static final Integer EXPIRATION_DAYS = 365;
    public static final String AUTHENTICATION_TYPE = "Bearer ";
    public static final String SECRETKEY = "THTJwtSecret";

    private boolean postOnly = true;

    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    public JWTLoginFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public JWTLoginFilter(AuthenticationManager authenticationManager,
                          AuthenticationSuccessHandler authenticationSuccessHandler, AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationManager = authenticationManager;
        this.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        this.setAuthenticationFailureHandler(authenticationFailureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        System.out.println("===== JWTLoginFilter attemptAuthentication =====");

        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        String username = request.getParameter(usernameParameter);
        String password = request.getParameter(passwordParameter);

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>())
        );
    }

//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
//
//        System.out.println("===== JWTLoginFilter successfulAuthentication =====");
//        String token = Jwts.builder()
//                .setSubject(((User) authentication.getPrincipal()).getUsername())
//                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 24 * 1000 * EXPIRATION_DAYS))
//                .signWith(SignatureAlgorithm.HS512, "THTJwtSecret")
//                .compact();
//        response.addHeader("Authorization", AUTHENTICATION_TYPE + token);
////        response.addHeader("Authorization", token);
//        logger.info("successfulAuthentication 登录成功");
//        response.setContentType("application/json;charset=UTF-8");
//        ReturnData returnData = new ReturnData("200", "登陆成功");
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("token", AUTHENTICATION_TYPE + token);
//        returnData.setData(map);
//        String result = FastJsonUtils.toJSONString(returnData);
//        System.out.println(result);
//        response.getWriter().write(result);
//    }


}
