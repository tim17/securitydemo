package com.example.securitydemo.service;

import com.example.securitydemo.dto.UserDto;
import com.example.securitydemo.model.User;
import com.example.securitydemo.model.UserRole;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;

public interface UserService {

    public int add(User bean) throws Exception;

    public void save(User bean) throws Exception;

    public User find(Integer id) throws Exception;

    public PageInfo selectList(Map<String, Object> map, Page page) throws Exception;

    public PageInfo selectListByRoleId(Integer roleId, Page page) throws Exception;

    public void batchInsertUserRole(List<UserRole> relations) throws Exception;

    /**
     * 通过用户名获取用户
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * 注册用户
     *
     * @param username
     * @param password
     * @return
     */
    public int register(String username, String password, String mobile) throws Exception;

    /**
     * 自定义登录
     * 用户名密码
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    public UserDetails login(String username, String password) throws Exception;


}

