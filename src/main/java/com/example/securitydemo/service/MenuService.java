package com.example.securitydemo.service;

import com.example.securitydemo.model.Menu;
import com.example.securitydemo.model.MenuRole;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface MenuService {

    public int add(Menu bean) throws Exception;

    public void save(Menu bean) throws Exception;

    public Menu find(Integer id) throws Exception;

    public List<Menu> findAll() throws Exception;


    public PageInfo selectList(Map<String, Object> map, Page page) throws Exception;

    public PageInfo selectMenuListByRoleId(Integer roleId, Page page) throws Exception;

    public PageInfo selectMenuListByParentId(Integer parentId, Page page) throws Exception;

    public void batchInsertMenuRole(List<MenuRole> relations) throws Exception;


}

