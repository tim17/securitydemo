package com.example.securitydemo.service.impl;

import com.example.securitydemo.dto.RoleDto;
import com.example.securitydemo.mapper.RoleMapper;
import com.example.securitydemo.model.Role;
import com.example.securitydemo.service.RoleService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public int add(Role bean) throws Exception {
        return roleMapper.insert(bean);
    }

    @Override
    public void save(Role bean) throws Exception {
        roleMapper.update(bean);
    }

    @Override
    public RoleDto find(Integer id) throws Exception {
        Role bean = roleMapper.selectByPrimaryKey(id);
        RoleDto dto = new RoleDto();
        BeanUtils.copyProperties(bean, dto);
        return dto;
    }

    @Override
    public PageInfo selectList(Map<String, Object> map, Page page) throws Exception {

        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<Role> list = roleMapper.selectAll(map);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;

    }


    @Override
    public PageInfo selectRoleListByUserId(Integer userId, Page page) throws Exception {
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<Role> list = roleMapper.selectRoleListByUserId(userId);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    @Override
    public PageInfo selectRoleListByMenuId(Integer menuId, Page page) throws Exception {
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<Role> list = roleMapper.selectRoleListByMenuId(menuId);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }



}
