package com.imooc.bigdata.hos.core.authmgr.dao;

import com.imooc.bigdata.hos.core.authmgr.model.ServiceAuth;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;

public interface ServiceAuthMapper {
    public void addAuth(@Param("auth")ServiceAuth auth);

    public void deleteAuth(@Param("bucket") String bucket, @Param("token") String token);

    public void deleteAuthByToken(@Param("token") String token);

    public void deleteAuthByBucket(@Param("bucket") String bucket);

    @ResultMap("ServiceAuthResultMap")
    public ServiceAuth getAuth(@Param("bucket") String bucket, @Param("token") String token);
}
