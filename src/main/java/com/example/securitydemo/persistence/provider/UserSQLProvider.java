package com.example.securitydemo.persistence.provider;

import com.example.securitydemo.dto.UserDto;
import com.example.securitydemo.model.User;
import com.example.securitydemo.model.UserRole;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.beans.BeanUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserSQLProvider {

    /**
     * select
     *
     * @param map
     * @return
     */
    public String select(Map<String, Object> map) {
        UserDto dto = new UserDto();
        if (Objects.nonNull(map.get("dto"))) {
            BeanUtils.copyProperties(map.get("dto"), dto);
        }
        return new SQL() {
            {
                SELECT("*");
                FROM("user");
                if (StringUtils.isNotBlank(dto.getName())) {
                    WHERE("name =#{dto.name}");
                }
                if (StringUtils.isNotBlank(dto.getPhone())) {
                    WHERE("phone =#{dto.phone}");
                }
                if (StringUtils.isNotBlank(dto.getTelephone())) {
                    WHERE("telephone =#{dto.telephone}");
                }
                if (Objects.nonNull(dto.getEnabled())) {
                    WHERE("enabled =#{dto.enabled}");
                }
                if (StringUtils.isNotBlank(dto.getUsername())) {
                    WHERE("username =#{dto.username}");
                }
                if (StringUtils.isNotBlank(dto.getPassword())) {
                    WHERE("password =#{dto.password}");
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
    public String insert(User bean) {
        return new SQL() {
            {
                INSERT_INTO("user");
                if (StringUtils.isNotBlank(bean.getName())) {
                    VALUES("name", "#{name}");
                }
                if (StringUtils.isNotBlank(bean.getPhone())) {
                    VALUES("phone", "#{phone}");
                }
                if (StringUtils.isNotBlank(bean.getTelephone())) {
                    VALUES("telephone", "#{telephone}");
                }
                if (StringUtils.isNotBlank(bean.getAddress())) {
                    VALUES("address", "#{address}");
                }
                if (StringUtils.isNotBlank(bean.getUserface())) {
                    VALUES("userface", "#{userface}");
                }
                if (StringUtils.isNotBlank(bean.getRemark())) {
                    VALUES("remark", "#{remark}");
                }
                if (StringUtils.isNotBlank(bean.getUsername())) {
                    VALUES("username", "#{username}");
                }
                if (StringUtils.isNotBlank(bean.getPassword())) {
                    VALUES("password", "#{password}");
                }
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
    public String update(User bean) {
        return new SQL() {
            {
                UPDATE("user");

                if (StringUtils.isNotBlank(bean.getName())) {
                    SET("name = #{name}");
                }
                if (StringUtils.isNotBlank(bean.getPhone())) {
                    SET("phone = #{phone}");
                }
                if (StringUtils.isNotBlank(bean.getTelephone())) {
                    SET("telephone = #{telephone}");
                }
                if (StringUtils.isNotBlank(bean.getAddress())) {
                    SET("address = #{address}");
                }
                if (StringUtils.isNotBlank(bean.getUserface())) {
                    SET("userface = #{userface}");
                }
                if (StringUtils.isNotBlank(bean.getRemark())) {
                    SET("remark = #{remark}");
                }
                if (StringUtils.isNotBlank(bean.getUsername())) {
                    SET("username = #{username}");
                }
                if (StringUtils.isNotBlank(bean.getPassword())) {
                    SET("password = #{password}");
                }
                if (StringUtils.isNotBlank(bean.getUserface())) {
                    SET("userface = #{userface}");
                }
                SET("enabled = #{enabled}");
                WHERE("ID = #{id}");
            }
        }.toString();
    }


    /**
     * batch insert user role relation
     * @param map
     * @return
     */
    public String batchInsertUserRole(Map map) {
        List<UserRole> relations = (List<UserRole>) map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("insert into user_role ");
        sb.append("(user_id,role_id) ");
        sb.append("values ");
        MessageFormat mf = new MessageFormat("(#'{'list[{0}].userId}, #'{'list[{0}].roleId})");
        for (int i = 0; i < relations.size(); i++) {
            sb.append(mf.format(new Object[]{i}));
            if (i < relations.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

}
