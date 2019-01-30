package com.example.securitydemo.controller;

import com.example.securitydemo.authentication.UserDetailService;
import com.example.securitydemo.common.dto.ReturnData;
import com.example.securitydemo.dto.UserDto;
import com.example.securitydemo.model.User;
import com.example.securitydemo.model.UserRole;
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

import java.util.ArrayList;
import java.util.List;

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
    public @ResponseBody
    ReturnData register(
            @RequestParam(value = "mobile", required = false) String mobile,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "smsCode", required = false) String smsCode
    ) {
        try {
            User bean = new User();
            bean.setUsername(mobile);
            bean.setPassword(password);
            int resultId = userService.register(mobile, password, mobile);
            if (resultId > 0) {
                //默认注册用户为普通用户 ROLE_USER
                //为新用户添加普通用户身份关联
                User user = userService.findByUsername(mobile);
                UserRole userRole = new UserRole();
                userRole.setRoleId(2);//ROLE_USER
                userRole.setUserId(user.getId());
                List<UserRole> relations = new ArrayList<UserRole>();
                relations.add(userRole);
                userService.batchInsertUserRole(relations);
                return new ReturnData("200", "OK");
            } else {
                return new ReturnData("200", "手机号已存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnData("500", "error message : " + e);
        }
    }


    @PostMapping("/change")
    @ApiOperation(value = "重置密码", notes = "重置密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String", example = "0"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", example = "0"),
            @ApiImplicitParam(name = "smsCode", value = "验证码", required = true, dataType = "String", example = "0")})
    public @ResponseBody
    ReturnData change(
            @RequestParam(value = "mobile", required = false) String mobile,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "smsCode", required = false) String smsCode
    ) {
        try {
            UserDetails user = userService.loadUserByUsername(mobile);
            if (user != null) {
                userService.changePassword(mobile, password);
                return new ReturnData("200", "成功");
            } else {
                return new ReturnData("200", "手机号不存在");
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
    public @ResponseBody
    Object currentUser() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @GetMapping("/me2")
    //    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public @ResponseBody
    Object currentUser(Authentication authentication) {
        return authentication;
    }

    /**
     * @param userDetails
     * @return 只包含了userDetails
     */
    @GetMapping("/me3")
    public @ResponseBody
    Object cuurentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userDetails;
    }

}

