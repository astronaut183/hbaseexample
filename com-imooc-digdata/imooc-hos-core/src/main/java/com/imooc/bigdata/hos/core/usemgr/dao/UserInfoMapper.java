package com.imooc.bigdata.hos.core.usemgr.dao;

import com.imooc.bigdata.hos.core.usemgr.model.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;

public interface UserInfoMapper {
    void addUser(@Param("userInfo") UserInfo userInfo);

    int delete(@Param("userId") String userId);

    int updateUserInfo(@Param("userId") String userId,
                       @Param("password") String password, @Param("detail") String detail);

    @ResultMap("UserInfoResultMap")
    UserInfo getUserInfo(@Param("userId") String userId);

    @ResultMap("UserInfoResultMap")
    UserInfo getUserInfoByName(@Param("userName") String userName);

    UserInfo checkPassword(@Param("username") String userName, @Param("password") String password);
}
