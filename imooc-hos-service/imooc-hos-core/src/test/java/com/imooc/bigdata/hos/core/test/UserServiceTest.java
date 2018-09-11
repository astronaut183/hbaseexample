package com.imooc.bigdata.hos.core.test;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.imooc.bigdata.hos.core.usermgr.IUserService;
import com.imooc.bigdata.hos.core.usermgr.model.SystemRole;
import com.imooc.bigdata.hos.core.usermgr.model.UserInfo;
import com.imooc.bigdata.hos.mybatis.test.BaseTest;

/**
 * Created by jixin on 18-3-8.
 */
public class UserServiceTest extends BaseTest {

  @Autowired
  @Qualifier("userServiceImpl")
  IUserService userService;

  @Test
  public void addUser() {
    UserInfo userInfo = new UserInfo("Tom", "123456", SystemRole.ADMIN, "no desc");
    userService.addUser(userInfo);
  }


  @Test
  public void getUserTest() {
    UserInfo userInfo = userService.getUserInfoByName("Tom");
    System.out.println(userInfo.getUserName()+ "|" + userInfo.getPassword());
  }
}
