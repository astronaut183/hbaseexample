package com.imooc.bigdata.hos.server.test;

import com.imooc.bigdata.hos.common.BucketModel;
import com.imooc.bigdata.hos.core.usermgr.IUserService;
import com.imooc.bigdata.hos.core.usermgr.model.UserInfo;
import com.imooc.bigdata.hos.mybatis.test.BaseTest;
import com.imooc.bigdata.hos.server.IBucketService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class BucketServiceTest extends BaseTest {
    @Autowired
    @Qualifier("bucketServiceImpl")
    IBucketService bucketService;

    @Autowired
    @Qualifier("userServiceImpl")
    IUserService userService;


    @Test
    public void addBucket() {
        UserInfo userInfo = userService.getUserInfoByName("Tom");
        bucketService.addBucket(userInfo, "bucket1", "this is a test bust");
    }


    @Test
    public void getBucket(){
        BucketModel bucketModel = bucketService.getBucketByName("bucket1");
        System.out.println(bucketModel.getCreator());
    }
}
