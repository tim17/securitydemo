package com.example.securitydemo.mapper;

import com.example.securitydemo.model.User;
import com.example.securitydemo.model.UserRole;
import com.example.securitydemo.persistence.provider.UserSQLProvider;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserMapper {
    @Delete({
            "delete from user",
            "where id = #{id}"
    })
    int deleteByPrimaryKey(Integer id);


    @InsertProvider(type = UserSQLProvider.class, method = "insert")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = Integer.class)
    public int insert(User bean) throws Exception;

    @UpdateProvider(type = UserSQLProvider.class, method = "update")
    public void update(User bean);

    @InsertProvider(type = UserSQLProvider.class, method = "batchInsertUserRole")
    public void batchInsertUserRole(@Param("list") List<UserRole> relations);

    @Select({
            "select * from user where id = #{id}"
    })
    @Results(id = "userMap", value = {
            @Result(column = "id", property = "id", id = true),
            @Result(column = "name", property = "name"),
            @Result(column = "phone", property = "phone"),
            @Result(column = "telephone", property = "telephone"),
            @Result(column = "address", property = "address"),
            @Result(column = "enabled", property = "enabled"),
            @Result(column = "username", property = "username"),
            @Result(column = "password", property = "password"),
            @Result(column = "remark", property = "remark"),
            @Result(column = "userface", property = "userface"),
            @Result(property = "roles", column = "id",
                    many = @Many(select = "com.example.securitydemo.mapper.RoleMapper.selectRoleListByUserId"))
    })
    User selectByPrimaryKey(Integer id);

    @SelectProvider(type = UserSQLProvider.class, method = "select")
    @ResultMap(value = "userMap")
    List<User> selectAll(Map<String, Object> map);

    @Select({
            "select u.* from user u,user_role r where u.id=r.user_id and r.role_id = #{roleId} ",
            "order by u.id desc",
    })
    @ResultMap(value = "userMap")
    List<User> selectUserListByRoleId(Integer roleId);

    @Select({
            "select * from user where username = #{username}"
    })
    @ResultMap(value = "userMap")
    User findByUserName(String username);


}