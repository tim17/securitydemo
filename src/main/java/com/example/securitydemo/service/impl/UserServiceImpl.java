package com.example.securitydemo.service.impl;

import com.example.securitydemo.dto.UserDto;
import com.example.securitydemo.mapper.UserMapper;
import com.example.securitydemo.model.User;
import com.example.securitydemo.model.UserRole;
import com.example.securitydemo.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public int add(User bean) throws Exception {
        return userMapper.insert(bean);
    }

    @Override
    public void save(User bean) throws Exception {
        userMapper.update(bean);
    }

    @Override
    public User find(Integer id) throws Exception {
        User bean = userMapper.selectByPrimaryKey(id);
        return bean;
    }

    @Override
    public PageInfo selectList(Map<String, Object> map, Page page) throws Exception {

        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<User> list = userMapper.selectAll(map);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;

    }

    @Override
    public PageInfo selectListByRoleId(Integer roleId, Page page) throws Exception {
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<User> list = userMapper.selectUserListByRoleId(roleId);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    @Override
    public void batchInsertUserRole(List<UserRole> relations) throws Exception {
        userMapper.batchInsertUserRole(relations);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUserName(username);
//        if (user == null) {
//            throw new UsernameNotFoundException("未找到用户");
//        }
        return user;
    }


    @Override
    public int register(String username, String password,String mobile) throws Exception {

        //如果用户名存在，返回错误
        if (userMapper.findByUserName(username) != null) {
            return -1;
        }
        try {
            User bean = new User();
            bean.setUsername(username);
            bean.setPassword(passwordEncoder.encode(password));
            bean.setPhone(mobile);
            bean.setEnabled(true);
            return userMapper.insert(bean);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    @Override
    public UserDetails login(String username, String password) throws Exception {
        User user = userMapper.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        } else if (passwordEncoder.encode(password).equals(user.getPassword())) {
            return user;
        } else {
            throw new UsernameNotFoundException("用户名或密码不正确");
        }
    }
}
