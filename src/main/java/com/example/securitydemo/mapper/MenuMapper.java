package com.example.securitydemo.mapper;

import com.example.securitydemo.model.Menu;
import com.example.securitydemo.model.MenuRole;
import com.example.securitydemo.persistence.provider.MenuSQLProvider;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface MenuMapper {
    @Delete({
            "delete from menu",
            "where id = #{id}"
    })
    int deleteByPrimaryKey(Integer id);


    @InsertProvider(type = MenuSQLProvider.class, method = "insert")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Integer.class)
    public int insert(Menu bean) throws Exception;

    @UpdateProvider(type = MenuSQLProvider.class, method = "update")
    public void update(Menu bean);

    @InsertProvider(type = MenuSQLProvider.class, method = "batchInsertMenuRole")
    public void batchInsertMenuRole(@Param("list") List<MenuRole> relations);

    @Select({
            "select * from menu where id = #{id}"
    })
    @Results(id = "menuMap", value = {
            @Result(column = "id", property = "id", id = true),
            @Result(column = "url", property = "url"),
            @Result(column = "path", property = "path"),
            @Result(column = "component", property = "component"),
            @Result(column = "name", property = "name"),
            @Result(column = "iconCls", property = "iconCls"),
            @Result(column = "parentId", property = "parentId"),
            @Result(column = "enabled", property = "enabled"),
            @Result(column = "keepAlive", property = "meta.keepAlive"),
            @Result(column = "requireAuth", property = "meta.requireAuth"),
            @Result(property = "roles", column = "id",
                    many = @Many(select = "com.example.securitydemo.mapper.RoleMapper.selectRoleListByMenuId")),
            @Result(property = "children", column = "id",
                    many = @Many(select = "com.example.securitydemo.mapper.MenuMapper.selectMenuListByParentId"))
    })
    Menu selectByPrimaryKey(Integer id);

    @SelectProvider(type = MenuSQLProvider.class, method = "select")
    @ResultMap(value = "menuMap")
    List<Menu> selectAll(Map<String, Object> map);

    @Select({
            "select m.* from menu m,menu_role r where m.id=r.menu_id and r.role_id = #{roleId} and m.enabled=true ",
    })
    @ResultMap(value = "menuMap")
    List<Menu> selectMenuListByRoleId(Integer roleId);

    @Select({
            "select m.* from menu m where m.parentId = #{parentId} and m.enabled=true ",
    })
    @ResultMap(value = "menuMap")
    List<Menu> selectMenuListByParentId(Integer parentId);


}