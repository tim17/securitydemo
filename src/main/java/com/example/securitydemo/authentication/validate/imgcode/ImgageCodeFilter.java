package com.example.securitydemo.authentication.validate.imgcode;

import com.example.securitydemo.authentication.controller.ValidateController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class ImgageCodeFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if (StringUtils.equalsIgnoreCase("/login", httpServletRequest.getRequestURI())
                && StringUtils.equalsIgnoreCase(httpServletRequest.getMethod(), "post")) {
            try {
                System.out.println(" ===== doFilterInternal ImgageCodeFilter ===== ");
                validateCode(new ServletWebRequest(httpServletRequest));
            } catch (ValidateCodeException e) {
                logger.error(e);
                authenticationFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, e);
                return;
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void validateCode(ServletWebRequest servletWebRequest) throws ServletRequestBindingException {
        String keyInRequest = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "imageCodeKey");
        String codeInRequest = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "imageCode");
        ImageCode codeInCache = (ImageCode) redisTemplate.opsForValue().get(ValidateController.KEY_IMAGE_CODE + "_" + keyInRequest);

        if (StringUtils.isBlank(codeInRequest)) {
            throw new ValidateCodeException("验证码不能为空！");
        }
        if (codeInCache == null) {
            throw new ValidateCodeException("验证码不存在！");
        }
        if (LocalDateTime.now().isAfter(codeInCache.getExpireTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
            redisTemplate.delete(ValidateController.KEY_SMS_CODE + "_" + keyInRequest);
            throw new ValidateCodeException("验证码已过期！");
        }
        if (!StringUtils.equalsIgnoreCase(codeInCache.getCode(), codeInRequest)) {
            throw new ValidateCodeException("验证码不正确！");
        }
        redisTemplate.delete(ValidateController.KEY_SMS_CODE + "_" + keyInRequest);

    }

}
