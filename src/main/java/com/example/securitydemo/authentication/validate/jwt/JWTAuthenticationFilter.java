package com.example.securitydemo.authentication.validate.jwt;

import com.example.securitydemo.authentication.handler.TokenAuthenticationHandler;
import io.jsonwebtoken.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class JWTAuthenticationFilter extends BasicAuthenticationFilter {


    public static final String AUTHENTICATION_TYPE = "Bearer ";
    public static final String SECRETKEY = "JwtSecret";
    public static final String TOKEN_SALT = "261fde32942a6cc4";

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        System.out.println("===== JWTAuthenticationFilter doFilterInternal =====");
        if (header == null || !header.startsWith(AUTHENTICATION_TYPE)) {
            chain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("===== JWTAuthenticationFilter doFilterInternal authentication =====");
        System.out.println(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        try {
            System.out.println("===== JWTAuthenticationFilter getAuthentication =====");
            String token = request.getHeader("Authorization");
            String md5 = request.getHeader("md5");
            //防止重放
//            System.out.println(token + TOKEN_SALT);
//            System.out.println(DigestUtils.md5DigestAsHex((token + TOKEN_SALT).getBytes()));
//            if (!md5.equals(DigestUtils.md5DigestAsHex((token + TOKEN_SALT).getBytes()))) {
//                return null;
//            }

            // parse the token.
            String user = Jwts.parser()
                    .setSigningKey(SECRETKEY)
                    .parseClaimsJws(token.replace(AUTHENTICATION_TYPE, ""))
                    .getBody()
                    .getSubject();

            //通过Token 获取角色
            List<LinkedHashMap> roles = (List<LinkedHashMap>) Jwts.parser()
                    .setSigningKey(SECRETKEY)
                    .parseClaimsJws(token.replace(AUTHENTICATION_TYPE, ""))
                    .getBody().get("role");
            System.out.println("===== JWTAuthenticationFilter getAuthentication roles " + roles + "=====");
            List<GrantedAuthority> authorities = new ArrayList<>();
            Iterator<LinkedHashMap> iterator = roles.iterator();
            while (iterator.hasNext()) {
                LinkedHashMap map = iterator.next();
                authorities.add(new SimpleGrantedAuthority((String) map.get("authority")));
            }
            return new UsernamePasswordAuthenticationToken(user, null, authorities);
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

}
