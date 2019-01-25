package com.example.securitydemo.persistence.provider;

import com.example.securitydemo.dto.MenuDto;
import com.example.securitydemo.model.Menu;
import com.example.securitydemo.model.MenuRole;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.beans.BeanUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MenuSQLProvider {

    /**
     * select
     *
     * @param map
     * @return
     */
    public String select(Map<String, Object> map) {
        MenuDto dto = new MenuDto();
        if (Objects.nonNull(map.get("dto"))) {
            BeanUtils.copyProperties(map.get("dto"), dto);
        }
        return new SQL() {
            {
                SELECT("*");
                FROM("menu");
                if (Objects.nonNull(dto.getParentId())) {
                    WHERE("parentId =#{dto.parentId}");
                }
                if (Objects.nonNull(dto.getEnabled())) {
                    WHERE("enabled =#{dto.enabled}");
                }
                ORDER_BY("id desc");
            }
        }.toString();
    }


    /**
     * insert
     *
     * @param bean
     * @return
     */
    public String insert(Menu bean) {
        return new SQL() {
            {
                INSERT_INTO("menu");
                if (StringUtils.isNotBlank(bean.getUrl())) {
                    VALUES("url", "#{url}");
                }
                if (StringUtils.isNotBlank(bean.getPath())) {
                    VALUES("path", "#{path}");
                }
                if (StringUtils.isNotBlank(bean.getComponent())) {
                    VALUES("component", "#{component}");
                }
                if (StringUtils.isNotBlank(bean.getName())) {
                    VALUES("name", "#{name}");
                }
                if (StringUtils.isNotBlank(bean.getIconCls())) {
                    VALUES("iconCls", "#{iconCls}");
                }
                if (Objects.nonNull(bean.getParentId())) {
                    VALUES("parentId", "#{parentId}");
                }
                VALUES("keepAlive", "#{meta.keepAlive}");
                VALUES("requireAuth", "#{meta.requireAuth}");
                VALUES("enabled", "#{enabled}");
            }
        }.toString();
    }


    /**
     * update
     *
     * @param bean
     * @return
     */
    public String update(Menu bean) {
        return new SQL() {
            {
                UPDATE("menu");

                if (StringUtils.isNotBlank(bean.getUrl())) {
                    SET("url = #{url}");
                }
                if (StringUtils.isNotBlank(bean.getPath())) {
                    SET("path = #{path}");
                }
                if (StringUtils.isNotBlank(bean.getComponent())) {
                    SET("component = #{component}");
                }
                if (StringUtils.isNotBlank(bean.getName())) {
                    SET("name = #{name}");
                }
                if (StringUtils.isNotBlank(bean.getIconCls())) {
                    SET("iconCls = #{iconCls}");
                }
                if (Objects.nonNull(bean.getParentId())) {
                    SET("parentId = #{parentId}");
                }
                SET("keepAlive = #{meta.keepAlive}");
                SET("requireAuth = #{meta.requireAuth}");
                SET("enabled = #{enabled}");
                WHERE("ID = #{id}");
            }
        }.toString();
    }


    /**
     * batch insert menu role relation
     *
     * @param map
     * @return
     */
    public String batchInsertMenuRole(Map map) {
        List<MenuRole> relations = (List<MenuRole>) map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("insert into menu_role ");
        sb.append("(menu_id,role_id) ");
        sb.append("values ");
        MessageFormat mf = new MessageFormat("(#'{'list[{0}].menuId}, #'{'list[{0}].roleId})");
        for (int i = 0; i < relations.size(); i++) {
            sb.append(mf.format(new Object[]{i}));
            if (i < relations.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

}
