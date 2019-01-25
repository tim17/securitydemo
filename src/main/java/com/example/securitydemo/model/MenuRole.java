package com.example.securitydemo.model;

import java.io.Serializable;

public class MenuRole implements Serializable {
    private static final long serialVersionUID = -3443654781274164247L;
    private Integer id;
    private Integer menuId;
    private Integer roleId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}
