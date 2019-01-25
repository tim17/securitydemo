package com.example.securitydemo.authentication.validate.jwt;

import com.example.securitydemo.authentication.handler.TokenAuthenticationHandler;
import io.jsonwebtoken.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

    public static final String AUTHENTICATION_TYPE = "Bearer ";
    public static final String SECRETKEY = "THTJwtSecret";


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
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        try {
            System.out.println("===== JWTAuthenticationFilter getAuthentication =====");
            String token = request.getHeader("Authorization");
            // parse the token.
            String user = Jwts.parser()
                    .setSigningKey(SECRETKEY)
                    .parseClaimsJws(token.replace(AUTHENTICATION_TYPE, ""))
                    .getBody()
                    .getSubject();
            return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

}
