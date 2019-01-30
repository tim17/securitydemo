package com.example.securitydemo.authentication.conf;

import com.example.securitydemo.model.Menu;
import com.example.securitydemo.model.Role;
import com.example.securitydemo.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;

/**
 * Created by sang on 2017/12/28.
 */
@Component
public class CustomMetadataSource implements FilterInvocationSecurityMetadataSource {
    @Autowired
    MenuService menuService;

    AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) {
        try {
            String requestUrl = ((FilterInvocation) o).getRequestUrl();
            List<Menu> allMenu = menuService.findAll();
            for (Menu menu : allMenu) {
                if (antPathMatcher.match(menu.getUrl(), requestUrl)
                        && menu.getRoles().size() > 0) {
                    List<Role> roles = menu.getRoles();
                    int size = roles.size();
                    String[] values = new String[size];
                    for (int i = 0; i < size; i++) {
                        values[i] = roles.get(i).getName();
                    }
                    return SecurityConfig.createList(values);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //没有匹配上的资源，都是登录访问
        return SecurityConfig.createList("ROLE_LOGIN");
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        System.out.println("===== CustomMetadataSource getAllConfigAttributes ");
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return FilterInvocation.class.isAssignableFrom(aClass);
    }
}
