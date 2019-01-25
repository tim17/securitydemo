package com.example.securitydemo.authentication.validate.smscode;

import com.example.securitydemo.authentication.UserDetailService;
import com.example.securitydemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class SmsAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    UserDetailService userDetailService;

    @Autowired
    UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println("===== SmsAuthenticationProvider into ===== ");
        SmsAuthenticationToken authenticationToken = (SmsAuthenticationToken) authentication;
        //此处可改为通过手机号找用户
        UserDetails userDetails = userDetailService.loadUserByUsername((String) authenticationToken.getPrincipal());
        System.out.println("===== SmsAuthenticationProvider authenticationToken.getPrincipal  " + authenticationToken.getPrincipal() + " ===== ");
        if (userDetails == null){
            throw new InternalAuthenticationServiceException("未找到与该手机号对应的用户");
        }

        SmsAuthenticationToken authenticationResult = new SmsAuthenticationToken(userDetails, userDetails.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return SmsAuthenticationToken.class.isAssignableFrom(aClass);
    }

    public UserDetailService getUserDetailService() {
        return userDetailService;
    }

    public void setUserDetailService(UserDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }
}
