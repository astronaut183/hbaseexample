package com.imooc.bigdata.hos.core.authmgr;

import com.imooc.bigdata.hos.core.authmgr.model.ServiceAuth;
import com.imooc.bigdata.hos.core.authmgr.model.TokenInfo;

import java.util.List;

public interface IAuthService {
    public boolean addAuth(ServiceAuth serviceAuth);

    public boolean deleteAuth(String bucket, String token);

    public boolean deleteAuthByToken(String token);

    public boolean deleteAuthByBucket(String bucket);

    public ServiceAuth getServiceAuth(String bucket, String token);




    public boolean addToken(TokenInfo tokenInfo);

    public boolean deleteToken(String token);

    public boolean updateToken(String token, int expireTime, boolean isActive);

    public boolean refreshToken(String token);

    public boolean checkToken(String token);

    public TokenInfo getTokenInfo(String token);

    public List<TokenInfo> getTokenInfos(String creator);
}
