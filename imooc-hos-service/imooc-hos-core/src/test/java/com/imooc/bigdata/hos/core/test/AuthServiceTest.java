package com.imooc.bigdata.hos.core.test;

import com.imooc.bigdata.hos.core.authmgr.IAuthService;
import com.imooc.bigdata.hos.core.authmgr.model.ServiceAuth;
import com.imooc.bigdata.hos.core.authmgr.model.TokenInfo;
import com.imooc.bigdata.hos.mybatis.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;
import java.util.List;

public class AuthServiceTest extends BaseTest {

    @Autowired
    @Qualifier("authServiceImpl")
    IAuthService authService;

    @Test
    public void addToken() {
        TokenInfo tokenInfo = new TokenInfo("Tom");
        authService.addToken(tokenInfo);
    }

    @Test
    public void getTokenByUser() {
        List<TokenInfo> tokenInfos = authService.getTokenInfos("Tom");
        tokenInfos.forEach(tokenInfo -> {
            System.out.println(tokenInfo.getToken());
        });
    }


    @Test
    public void addServiceAuth() {
        ServiceAuth serviceAuth = new ServiceAuth();
        serviceAuth.setBucketName("bucket1");
        serviceAuth.setTargetToken("0d951e355f734544ac7610e18695068d");
        serviceAuth.setAuthTime(new Date());
        authService.addAuth(serviceAuth);
    }

    @Test
    public void getServiceAuth() {
        ServiceAuth serviceAuth = authService
                .getServiceAuth("bucket1", "0d951e355f734544ac7610e18695068d");
        System.out.println(serviceAuth.getBucketName() + " | " + serviceAuth.getTargetToken());
    }
}
