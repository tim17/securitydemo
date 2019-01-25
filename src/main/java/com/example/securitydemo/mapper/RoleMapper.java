package com.example.securitydemo.mapper;

import com.example.securitydemo.model.Role;
import com.example.securitydemo.persistence.provider.RoleSQLProvider;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface RoleMapper {
    @Delete({
            "delete from role",
            "where id = #{id}"
    })
    int deleteByPrimaryKey(Integer id);


    @InsertProvider(type = RoleSQLProvider.class, method = "insert")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Integer.class)
    public int insert(Role bean) throws Exception;

    @UpdateProvider(type = RoleSQLProvider.class, method = "update")
    public void update(Role bean);


    @Select({
            "select * from role where id = #{id}"
    })
    @Results(id = "roleMap", value = {
            @Result(column = "id", property = "id", id = true),
            @Result(column = "name", property = "name"),
            @Result(column = "nameZh", property = "nameZh"),
    })
    Role selectByPrimaryKey(Integer id);

    @SelectProvider(type = RoleSQLProvider.class, method = "select")
    @ResultMap(value = "roleMap")
    List<Role> selectAll(Map<String, Object> map);


    @Select({
            "select ro.* from role ro,user_role r where ro.id=r.role_id and r.user_id = #{userId} ",
            "order by ro.id desc",
    })
    @ResultMap(value = "roleMap")
    List<Role> selectRoleListByUserId(Integer userId);


    @Select({
            "select ro.* from role ro,menu_role r where ro.id=r.role_id and r.menu_id = #{menuId} ",
            "order by ro.id desc",
    })
    @ResultMap(value = "roleMap")
    List<Role> selectRoleListByMenuId(Integer menuId);


}