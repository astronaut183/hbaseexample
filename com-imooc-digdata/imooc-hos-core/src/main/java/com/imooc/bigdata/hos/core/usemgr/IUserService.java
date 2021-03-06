package com.imooc.bigdata.hos.core.usemgr;

import com.imooc.bigdata.hos.core.usemgr.model.UserInfo;

public interface IUserService {
    public boolean addUser(UserInfo userInfo);

    public boolean updateUserInfo(String userId, String password, String detail);

    public boolean deleteUser(String userId);

    public UserInfo getUserInfo(String userId);

    public UserInfo getUserInfoByName(String userName);

    public UserInfo checkPassword(String userName, String password);
}
