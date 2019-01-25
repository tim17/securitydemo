package com.example.securitydemo.service;

import com.example.securitydemo.dto.RoleDto;
import com.example.securitydemo.model.Role;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface RoleService {

    public int add(Role bean) throws Exception;

    public void save(Role bean) throws Exception;

    public RoleDto find(Integer id) throws Exception;

    public PageInfo selectList(Map<String, Object> map, Page page) throws Exception;

    /**
     * 获取用户 角色列表
     * @param userId
     * @param page
     * @return
     * @throws Exception
     */
    public PageInfo selectRoleListByUserId(Integer userId, Page page) throws Exception;

    /**
     * 获取菜单 角色列表
     * @param menuId
     * @param page
     * @return
     * @throws Exception
     */
    public PageInfo selectRoleListByMenuId(Integer menuId, Page page) throws Exception;


}

