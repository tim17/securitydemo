package com.example.securitydemo.service.impl;

import com.example.securitydemo.mapper.MenuMapper;
import com.example.securitydemo.model.Menu;
import com.example.securitydemo.model.MenuRole;
import com.example.securitydemo.service.MenuService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public int add(Menu bean) throws Exception {
        return menuMapper.insert(bean);
    }

    @Override
    public void save(Menu bean) throws Exception {
        menuMapper.update(bean);
    }

    @Override
    public Menu find(Integer id) throws Exception {
        Menu bean = menuMapper.selectByPrimaryKey(id);
        return bean;
    }

    @Override
    public List<Menu> findAll() throws Exception {
        if (redisTemplate.opsForValue().get("TEST_ALL_MENU") != null) {
            List<Menu> allMenu = (List<Menu>) redisTemplate.opsForValue().get("TEST_ALL_MENU");
            return allMenu;
        } else {
            List<Menu> allMenu = menuMapper.selectAllmenu();
            redisTemplate.opsForValue().set("TEST_ALL_MENU", allMenu);
            return allMenu;
        }
//        return menuMapper.selectAllmenu();
    }

    @Override
    public PageInfo selectList(Map<String, Object> map, Page page) throws Exception {

        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<Menu> list = menuMapper.selectAll(map);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;

    }

    @Override
    public PageInfo selectMenuListByRoleId(Integer roleId, Page page) throws Exception {
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<Menu> list = menuMapper.selectMenuListByRoleId(roleId);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    @Override
    public PageInfo selectMenuListByParentId(Integer parentId, Page page) throws Exception {
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<Menu> list = menuMapper.selectMenuListByParentId(parentId);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    @Override
    public void batchInsertMenuRole(List<MenuRole> relations) throws Exception {
        menuMapper.batchInsertMenuRole(relations);
    }

}
