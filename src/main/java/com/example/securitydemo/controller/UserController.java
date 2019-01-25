package com.example.securitydemo.controller;

import com.example.securitydemo.authentication.UserDetailService;
import com.example.securitydemo.common.dto.ReturnData;
import com.example.securitydemo.dto.UserDto;
import com.example.securitydemo.model.User;
import com.example.securitydemo.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserDetailService userDetailService;

    @Autowired
    UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    @ApiOperation(value = "注册", notes = "注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String", example = "0"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", example = "0"),
            @ApiImplicitParam(name = "smsCode", value = "验证码", required = true, dataType = "String", example = "0")})
    ReturnData register(
            @RequestParam(value = "mobile", required = false) String mobile,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "smsCode", required = false) String smsCode
    ) {
        try {
            User bean = new User();
            bean.setUsername(mobile);
            bean.setPassword(password);
            if (userService.register(mobile, password, mobile) > 0) {
                return new ReturnData("200", "OK");
            } else {
                return new ReturnData("500", "手机号已存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnData("500", "error message : " + e);
        }
    }

    @GetMapping("/getusers")
    public String getUsers() {
        userDetailService.loadUserByUsername("user");
        return "Hello Spring Security";
    }


    @PostMapping("/bind_mobile")
    public String bindMobile(String mobile, String smsCode) {
        try {
            System.out.println("===== 绑定手机号 ===== ");
            System.out.println(mobile);
            System.out.println(smsCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取当前登录的用户
     *
     * @return 完整的Authentication
     */
    @GetMapping("/me1")
    public Object currentUser() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @GetMapping("/me2")
    public Object currentUser(Authentication authentication) {
        return authentication;
    }

    /**
     * @param userDetails
     * @return 只包含了userDetails
     */
    @GetMapping("/me3")
    public Object cuurentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userDetails;
    }

}

