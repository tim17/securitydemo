package com.example.securitydemo.authentication.handler;

import com.alibaba.fastjson.JSONObject;
import com.example.securitydemo.common.dto.ReturnData;
import com.example.securitydemo.model.User;
import com.example.securitydemo.service.UserService;
//import com.example.securitydemo.util.FastJsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 成功回调
 */
@Component("myAuthenctiationSuccessHandler")
public class MyAuthenctiationSuccessHandler implements AuthenticationSuccessHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());


    public static final Integer EXPIRATION_DAYS = 365;
    public static final String AUTHENTICATION_TYPE = "Bearer ";
    public static final String SECRETKEY = "JwtSecret";

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
/*

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(authentication));
  */

        System.out.println("===== MyAuthenctiationSuccessHandler onAuthenticationSuccess =====");

        UserDetails user = userService.loadUserByUsername(((User) authentication.getPrincipal()).getUsername());
        HashMap<String, Object> rolesMap = new HashMap<>();
        rolesMap.put("role", user.getAuthorities());


        String token = Jwts.builder()
                .setClaims(rolesMap)
                .setSubject(((User) authentication.getPrincipal()).getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 24 * 1000 * EXPIRATION_DAYS))
                .signWith(SignatureAlgorithm.HS512, SECRETKEY)
                .compact();

        response.addHeader("Authorization", AUTHENTICATION_TYPE + token);
//        response.addHeader("Authorization", token);
        response.setContentType("application/json;charset=UTF-8");
        ReturnData appReturnData = new ReturnData("200", "");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("token", AUTHENTICATION_TYPE + token);
        appReturnData.setData(map);
//        String result = FastJsonUtils.toJSONString(returnData);
        String result = JSONObject.toJSONString(appReturnData);
        System.out.println(result);
        response.getWriter().write(result);
    }


}
