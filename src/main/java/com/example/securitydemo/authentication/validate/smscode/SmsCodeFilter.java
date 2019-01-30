package com.example.securitydemo.authentication.validate.smscode;

import com.example.securitydemo.authentication.controller.ValidateController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
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
public class SmsCodeFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${CACHE_KEY_SMS_CODE}")
    public String CACHE_KEY_SMS_CODE;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if (StringUtils.equalsIgnoreCase("/login/mobile", httpServletRequest.getRequestURI())
                || StringUtils.equalsIgnoreCase("/user/bind_mobile", httpServletRequest.getRequestURI())
                || StringUtils.equalsIgnoreCase("/user/register", httpServletRequest.getRequestURI())
                || StringUtils.equalsIgnoreCase("/user/change", httpServletRequest.getRequestURI())
                && StringUtils.equalsIgnoreCase(httpServletRequest.getMethod(), "post")) {

            System.out.println(" ===== doFilterInternal SmsCodeFilter ===== ");

            try {
                validateCode(new ServletWebRequest(httpServletRequest));
            } catch (ValidateCodeException e) {
                authenticationFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, e);
                return;
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void validateCode(ServletWebRequest servletWebRequest) throws ServletRequestBindingException {
        String smsCodeInRequest = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "smsCode");
        String mobileInRequest = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "mobile");
        SmsCode codeInCache = (SmsCode) redisTemplate.opsForValue().get(CACHE_KEY_SMS_CODE + "_" + mobileInRequest);
        if (StringUtils.isBlank(smsCodeInRequest)) {
            throw new ValidateCodeException("验证码不能为空！");
        }
        if (codeInCache == null) {
            throw new ValidateCodeException("验证码不存在！");
        }
        if (LocalDateTime.now().isAfter(codeInCache.getExpireTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
            redisTemplate.delete(CACHE_KEY_SMS_CODE + "_" + mobileInRequest);
            throw new ValidateCodeException("验证码已过期！");
        }
        if (!StringUtils.equalsIgnoreCase(codeInCache.getCode(), smsCodeInRequest)) {
            throw new ValidateCodeException("验证码不正确！");
        }
        redisTemplate.delete(CACHE_KEY_SMS_CODE + "_" + mobileInRequest);
    }
}