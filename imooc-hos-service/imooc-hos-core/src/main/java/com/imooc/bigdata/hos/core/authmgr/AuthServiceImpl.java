package com.imooc.bigdata.hos.core.authmgr;

import com.imooc.bigdata.hos.core.authmgr.dao.ServiceAuthMapper;
import com.imooc.bigdata.hos.core.authmgr.dao.TokenInfoMapper;
import com.imooc.bigdata.hos.core.authmgr.model.ServiceAuth;
import com.imooc.bigdata.hos.core.authmgr.model.TokenInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service("authServiceImpl")
public class AuthServiceImpl implements IAuthService {

    @Autowired
    TokenInfoMapper tokenInfoMapper;

    @Autowired
    ServiceAuthMapper serviceAuthMapper;

    @Override
    public boolean addAuth(ServiceAuth serviceAuth) {
        serviceAuthMapper.addAuth(serviceAuth);
        return true;
    }

    @Override
    public boolean deleteAuth(String bucket, String token) {
        serviceAuthMapper.deleteAuth( bucket, token);
        return true;
    }

    @Override
    public boolean deleteAuthByToken(String token) {
        return false;
    }

    @Override
    public boolean deleteAuthByBucket(String bucket) {
        serviceAuthMapper.deleteAuthByBucket(bucket);
        return true;
    }

    @Override
    public ServiceAuth getServiceAuth(String bucket, String token) {
        return serviceAuthMapper.getAuth(bucket, token);
    }




    @Override
    public boolean addToken(TokenInfo tokenInfo) {
        tokenInfoMapper.addToken(tokenInfo);
        return true;
    }

    @Override
    public boolean deleteToken(String token) {
        tokenInfoMapper.deleteToken(token);

        //todo delete auth
        serviceAuthMapper.deleteAuthByToken(token);

        return true;
    }

    @Override
    public boolean updateToken(String token, int expireTime, boolean isActive) {
        tokenInfoMapper.updateToken(token, expireTime, isActive?1:0);
        return true;
    }

    @Override
    public boolean refreshToken(String token) {
        tokenInfoMapper.refreshToken(token, new Date());
        return true;
    }

    @Override
    public boolean checkToken(String token) {
        TokenInfo tokenInfo = tokenInfoMapper.getTokenInfo(token);
        if(tokenInfo == null){
            return false;
        }
        if(tokenInfo.isInActive()){
            Date nowDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tokenInfo.getRefreshTime());
            calendar.add(Calendar.DATE, tokenInfo.getExpireTime());
            return nowDate.before(calendar.getTime());
        }
        return false;
    }

    @Override
    public TokenInfo getTokenInfo(String token) {
        return tokenInfoMapper.getTokenInfo(token);
    }

    @Override
    public List<TokenInfo> getTokenInfos(String creator) {
        return tokenInfoMapper.getTokenInfos(creator);
    }
}
