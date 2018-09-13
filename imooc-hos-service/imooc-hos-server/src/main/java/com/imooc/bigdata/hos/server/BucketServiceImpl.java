package com.imooc.bigdata.hos.server;

import com.imooc.bigdata.hos.common.BucketModel;
import com.imooc.bigdata.hos.core.authmgr.IAuthService;
import com.imooc.bigdata.hos.core.authmgr.model.ServiceAuth;
import com.imooc.bigdata.hos.core.usermgr.model.UserInfo;
import com.imooc.bigdata.hos.server.dao.BucketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
@Service("bucketServiceImpl")
public class BucketServiceImpl implements IBucketService{

    @Autowired
    BucketMapper bucketMapper;
    @Autowired
    @Qualifier("authServiceImpl")
    IAuthService authService;

    @Override
    public boolean addBucket(UserInfo userInfo, String bucketName, String detail) {
        BucketModel bucketModel = new BucketModel(bucketName, userInfo.getUserName(), detail);
        bucketMapper.addBucket(bucketModel);
        //todo add auth for bucket and user
        ServiceAuth serviceAuth = new ServiceAuth();
        serviceAuth.setTargetToken(userInfo.getUserId());
        serviceAuth.setBucketName(bucketName);
        serviceAuth.setAuthTime(new Date());
        authService.addAuth(serviceAuth);
        return true;
    }

    @Override
    public boolean deleteBucket(String bucketName) {
        bucketMapper.deleteBucket(bucketName);
        //todo delete auth
        authService.deleteAuthByBucket(bucketName);
        return false;
    }

    @Override
    public boolean updateBucket(String bucketName, String detail) {
        bucketMapper.updateBucket(bucketName,detail);
        return false;
    }

    @Override
    public BucketModel getBucketById(String bucketId) {
        return bucketMapper.getBucketById(bucketId);
    }

    @Override
    public BucketModel getBucketByName(String bucketName) {
        return bucketMapper.getBucketByName(bucketName);
    }

    @Override
    public List<BucketModel> getBucketByCreator(String creator) {
        return bucketMapper.getBucketByCreator(creator);
    }

    @Override
    public List<BucketModel> getUserBuckets(String token) {
        return bucketMapper.getUserAuthorizedBuckets(token);
    }
}
