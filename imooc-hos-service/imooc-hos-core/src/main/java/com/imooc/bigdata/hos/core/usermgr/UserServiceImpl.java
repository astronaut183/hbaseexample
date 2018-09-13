package com.imooc.bigdata.hos.core.usermgr;

import java.util.Date;

import com.imooc.bigdata.hos.core.authmgr.IAuthService;
import com.imooc.bigdata.hos.core.authmgr.model.TokenInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.imooc.bigdata.hos.core.usermgr.dao.UserInfoMapper;
import com.imooc.bigdata.hos.core.usermgr.model.UserInfo;

@Transactional
@Service("userServiceImpl")
public class UserServiceImpl implements IUserService{

  private final long LONG_REFRESH_TIME = 4670409600000L;

  @Autowired
  UserInfoMapper userInfoMapper;

  @Autowired
  @Qualifier("authServiceImpl")
  IAuthService authService;
  @Override
  public boolean addUser(UserInfo userInfo) {
    userInfoMapper.addUser(userInfo);

    //todo add token
    TokenInfo tokenInfo = new TokenInfo();
    tokenInfo.setToken(userInfo.getUserId());
    tokenInfo.setInActive(true);
    tokenInfo.setExpireTime(7);
    tokenInfo.setRefreshTime(new Date(LONG_REFRESH_TIME));
    tokenInfo.setCreator(CoreUtil.SYSTEM_USER);
    tokenInfo.setCreateTime(new Date());

    authService.addToken(tokenInfo);
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
    userInfoMapper.deleteUser(userId);

    authService.deleteToken(userId);
    authService.deleteAuthByToken(userId);
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
