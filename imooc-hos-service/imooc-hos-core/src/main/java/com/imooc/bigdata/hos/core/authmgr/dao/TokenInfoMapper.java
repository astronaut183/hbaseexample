package com.imooc.bigdata.hos.core.authmgr.dao;

import com.imooc.bigdata.hos.core.authmgr.model.TokenInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;

import java.util.Date;
import java.util.List;

@Mapper
public interface TokenInfoMapper {

    public void addToken(@Param("token") TokenInfo tokenInfo);

    public void deleteToken(@Param("token") String token);

    public void updateToken(@Param("token") String token, @Param("expireTime") int expireTime,
                            @Param("isActive") int isActive);

    public void refreshToken(@Param("token") String token, @Param("refreshTime") Date refreshTime);

    @ResultMap("TokenInfoResultMap")
    public TokenInfo getTokenInfo(@Param("token") String token);

    @ResultMap("TokenInfoResultMap")
    public List<TokenInfo> getTokenInfos(@Param("creator") String creator);
}
