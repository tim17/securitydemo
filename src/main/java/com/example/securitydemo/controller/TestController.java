package com.example.securitydemo.controller;

import com.example.securitydemo.common.dto.ReturnData;
import com.example.securitydemo.dto.MenuDto;
import com.example.securitydemo.dto.RoleDto;
import com.example.securitydemo.dto.UserDto;
import com.example.securitydemo.model.*;
import com.example.securitydemo.service.MenuService;
import com.example.securitydemo.service.RoleService;
import com.example.securitydemo.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/test")
@Api(tags = "测试用户及权限管理", value = "测试用户及权限管理", description = "测试用户及权限管理")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MenuService menuService;


    @PostMapping("/addUser")
    @ApiOperation(value = "新增用户", notes = "新增用户")
    ReturnData addUser(UserDto dto) {
        try {
            User bean = new User();
            BeanUtils.copyProperties(dto, bean);
            int id = userService.add(bean);
            return new ReturnData("200", "OK");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error:", e);
            return new ReturnData("500", "error message : " + e);
        }
    }


    @PostMapping("/addMenu")
    @ApiOperation(value = "新增菜单", notes = "新增菜单")
    ReturnData addMenu(MenuDto dto) {
        try {
            Menu bean = new Menu();
            BeanUtils.copyProperties(dto, bean);
            int id = menuService.add(bean);
            return new ReturnData("200", "OK");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error:", e);
            return new ReturnData("500", "error message : " + e);
        }
    }


    @PostMapping("/addRole")
    @ApiOperation(value = "新增角色", notes = "新增角色")
    ReturnData addRole(RoleDto dto) {
        try {
            Role bean = new Role();
            BeanUtils.copyProperties(dto, bean);
            int id = roleService.add(bean);
            return new ReturnData("200", "OK");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error:", e);
            return new ReturnData("500", "error message : " + e);
        }
    }


    @PostMapping("/addUserRole")
    @ApiOperation(value = "新增用户与角色关系", notes = "新增用户与角色关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "String", example = "0"),
            @ApiImplicitParam(name = "roleId", value = "角色ID", required = true, dataType = "String", example = "0")})
    ReturnData batchInsertUserRole(
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "roleId", required = false) Integer roleId
    ) {
        try {
            UserRole relation = new UserRole();
            relation.setUserId(userId);
            relation.setRoleId(roleId);
            List<UserRole> relations = new ArrayList<UserRole>();
            relations.add(relation);
            userService.batchInsertUserRole(relations);
            return new ReturnData("200", "OK");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error:", e);
            return new ReturnData("500", "error message : " + e);
        }
    }


    @PostMapping("/addMenuRole")
    @ApiOperation(value = "新增菜单与角色关系", notes = "新增菜单与角色关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuId", value = "菜单ID", required = true, dataType = "String", example = "0"),
            @ApiImplicitParam(name = "roleId", value = "角色ID", required = true, dataType = "String", example = "0")})
    ReturnData batchInsertMenuRole(
            @RequestParam(value = "menuId", required = false) Integer menuId,
            @RequestParam(value = "roleId", required = false) Integer roleId
    ) {
        try {
            MenuRole relation = new MenuRole();
            relation.setMenuId(menuId);
            relation.setRoleId(roleId);
            List<MenuRole> relations = new ArrayList<MenuRole>();
            relations.add(relation);
            menuService.batchInsertMenuRole(relations);
            return new ReturnData("200", "OK");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error:", e);
            return new ReturnData("500", "error message : " + e);
        }
    }


    @GetMapping(value = "/userList")
    @ApiOperation(value = "User列表", notes = "查询全部User列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页要显示数据数", paramType = "query", required = false, dataType = "Integer")
    })
    public @ResponseBody
    ReturnData userList(@RequestParam(value = "pageNum", required = false) Integer pageNum,
                        @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        try {
            pageNum = pageNum == null ? 1 : pageNum;
            pageSize = pageSize == null ? 10 : pageSize;
            Page page = new Page(pageNum, pageSize);
            UserDto dto = new UserDto();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("dto", dto);
            PageInfo pageInfo = userService.selectList(map, page);
            return new ReturnData("200", "SUCCESS", pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error", e);
            return new ReturnData("500", "ERROR:" + e);
        }
    }


    @GetMapping(value = "/userListByRoleId/{roleId}")
    @ApiOperation(value = "通过角色ID查询User列表", notes = "通过角色ID查询User列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色ID", paramType = "path", required = false, dataType = "String"),
            @ApiImplicitParam(name = "pageNum", value = "页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页要显示数据数", paramType = "query", required = false, dataType = "Integer")
    })
    public @ResponseBody
    ReturnData userListByRoleId(@PathVariable(value = "roleId") Integer roleId,
                                @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        try {
            pageNum = pageNum == null ? 1 : pageNum;
            pageSize = pageSize == null ? 10 : pageSize;
            Page page = new Page(pageNum, pageSize);
            PageInfo pageInfo = userService.selectListByRoleId(roleId, page);
            return new ReturnData("200", "SUCCESS", pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error", e);
            return new ReturnData("500", "ERROR:" + e);
        }
    }


    @GetMapping(value = "/roleList")
    @ApiOperation(value = "角色列表", notes = "查询全部角色列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页要显示数据数", paramType = "query", required = false, dataType = "Integer")
    })
    public @ResponseBody
    ReturnData roleList(@RequestParam(value = "pageNum", required = false) Integer pageNum,
                        @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        try {
            pageNum = pageNum == null ? 1 : pageNum;
            pageSize = pageSize == null ? 10 : pageSize;
            Page page = new Page(pageNum, pageSize);
            RoleDto dto = new RoleDto();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("dto", dto);
            PageInfo pageInfo = roleService.selectList(map, page);
            return new ReturnData("200", "SUCCESS", pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error", e);
            return new ReturnData("500", "ERROR:" + e);
        }
    }


    @GetMapping(value = "/menuList")
    @ApiOperation(value = "菜单列表", notes = "查询全部菜单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页要显示数据数", paramType = "query", required = false, dataType = "Integer")
    })
    public @ResponseBody
    ReturnData menuList(@RequestParam(value = "pageNum", required = false) Integer pageNum,
                        @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        try {
            pageNum = pageNum == null ? 1 : pageNum;
            pageSize = pageSize == null ? 10 : pageSize;
            Page page = new Page(pageNum, pageSize);
            MenuDto dto = new MenuDto();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("dto", dto);
            PageInfo pageInfo = menuService.selectList(map, page);
            return new ReturnData("200", "SUCCESS", pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error", e);
            return new ReturnData("500", "ERROR:" + e);
        }
    }


    @GetMapping(value = "/menuListByRoleId/{roleId}")
    @ApiOperation(value = "通过角色ID查询菜单列表", notes = "通过角色ID查询菜单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色ID", paramType = "path", required = false, dataType = "String"),
            @ApiImplicitParam(name = "pageNum", value = "页码", paramType = "query", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页要显示数据数", paramType = "query", required = false, dataType = "Integer")
    })
    public @ResponseBody
    ReturnData menuListByRoleId(@PathVariable(value = "roleId") Integer roleId,
                                @RequestParam(value = "pageNum", required = false) Integer pageNum,
                                @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        try {
            pageNum = pageNum == null ? 1 : pageNum;
            pageSize = pageSize == null ? 10 : pageSize;
            Page page = new Page(pageNum, pageSize);
            PageInfo pageInfo = menuService.selectMenuListByRoleId(roleId, page);
            return new ReturnData("200", "SUCCESS", pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error", e);
            return new ReturnData("500", "ERROR:" + e);
        }
    }

    @PostMapping("/register")
    @ApiOperation(value = "注册", notes = "注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", paramType = "query", required = false, dataType = "String")
    })
    ReturnData register(@RequestParam(value = "username", required = false) String username,
                        @RequestParam(value = "password", required = false) String password) {
        try {
            if (userService.register(username, password, null) > 0) {
                return new ReturnData("200", "SUCCESS");
            } else {
                return new ReturnData("500", "用户名已存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error:", e);
            return new ReturnData("500", "error message : " + e);
        }
    }

    @PostMapping("/findUser")
    @ApiOperation(value = "通过ID获取用户", notes = "通过ID获取用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户名", paramType = "query", required = false, dataType = "Integer")
    })
    ReturnData findUser(@RequestParam(value = "userId", required = false) Integer userId) {
        try {
            User user = userService.find(userId);
            if (user != null) {
//                System.out.println(user.getAuthorities().size());
                System.out.println(user.getAuthorities());
                return new ReturnData("200", "SUCCESS", user);
            } else {
                return new ReturnData("500", "用户不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error:", e);
            return new ReturnData("500", "error message : " + e);
        }
    }


}

