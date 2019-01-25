package com.example.securitydemo.persistence.provider;

import com.example.securitydemo.dto.RoleDto;
import com.example.securitydemo.model.Role;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.beans.BeanUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RoleSQLProvider {

    /**
     * select
     *
     * @param map
     * @return
     */
    public String select(Map<String, Object> map) {
        RoleDto dto = new RoleDto();
        if (Objects.nonNull(map.get("dto"))) {
            BeanUtils.copyProperties(map.get("dto"), dto);
        }
        return new SQL() {
            {
                SELECT("*");
                FROM("role");
                if (StringUtils.isNotBlank(dto.getName())) {
                    WHERE("name =#{dto.name}");
                }
                if (StringUtils.isNotBlank(dto.getNameZh())) {
                    WHERE("nameZh =#{dto.nameZh}");
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
    public String insert(Role bean) {
        return new SQL() {
            {
                INSERT_INTO("role");
                if (StringUtils.isNotBlank(bean.getName())) {
                    VALUES("name", "#{name}");
                }
                if (StringUtils.isNotBlank(bean.getNameZh())) {
                    VALUES("nameZh", "#{nameZh}");
                }
            }
        }.toString();
    }


    /**
     * update
     *
     * @param bean
     * @return
     */
    public String update(Role bean) {
        return new SQL() {
            {
                UPDATE("role");

                if (StringUtils.isNotBlank(bean.getName())) {
                    SET("name = #{name}");
                }
                if (StringUtils.isNotBlank(bean.getNameZh())) {
                    SET("nameZh = #{nameZh}");
                }
                WHERE("ID = #{id}");
            }
        }.toString();
    }





}
