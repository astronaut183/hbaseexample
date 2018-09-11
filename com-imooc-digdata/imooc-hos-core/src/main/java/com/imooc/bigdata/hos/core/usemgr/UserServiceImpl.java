package com.imooc.bigdata.hos.core.usemgr;

import com.google.common.base.Strings;
import com.imooc.bigdata.hos.core.CoreUtil;
import com.imooc.bigdata.hos.core.usemgr.dao.UserInfoMapper;
import com.imooc.bigdata.hos.core.usemgr.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service("userServiceImpl")
public class UserServiceImpl implements IUserService{

    @Autowired
    UserInfoMapper userInfoMapper;
    @Override
    public boolean addUser(UserInfo userInfo) {
        userInfoMapper.addUser(userInfo);

        //todo add token
        return true;
    }

    @Override
    public boolean updateUserInfo(String userId, String password, String detail) {
        userInfoMapper.updateUserInfo(userId,
                Strings.isNullOrEmpty(password) ? null: CoreUtil.getMd5Password(password), detail);
        return true;
    }

    @Override
    public boolean deleteUser(String userId) {
        userInfoMapper.delete(userId);
        return true;
    }

    @Override
    public UserInfo getUserInfo(String userId) {
        return userInfoMapper.getUserInfo(userId);
    }

    @Override
    public UserInfo getUserInfoByName(String userName) {
        return userInfoMapper.getUserInfoByName(userName);
    }

    @Override
    public UserInfo checkPassword(String userName, String password) {
        return userInfoMapper.checkPassword(userName,CoreUtil.getMd5Password(password));
    }
}
