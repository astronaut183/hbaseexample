package com.imooc.bigdata.hos.server;

import com.imooc.bigdata.hos.common.HosObject;
import com.imooc.bigdata.hos.common.HosObjectSummary;
import com.imooc.bigdata.hos.common.ObjectListResult;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

public class HosStoreImpl implements IHosStore {
    private static Logger logger = Logger.getLogger(HdfsServiceImpl.class);

    private Connection connection = null;
    private IHdfsService fileStore;
    private String zkUrls;
    private CuratorFramework zkClient;

    public HosStoreImpl(Connection connection, IHdfsService fileStore, String zkUrls) {
        this.connection = connection;
        this.fileStore = fileStore;
        this.zkUrls = zkUrls;
        zkClient = CuratorFrameworkFactory.newClient(zkUrls,
                new ExponentialBackoffRetry(20, 5));
        this.zkClient.start();
    }

    @Override
    public void createbucketStore(String bucket) throws IOException {
        //1 create dir table
        HBaseServiceImpl
                .createTable(connection, HosUtil.getDirTableName(bucket), HosUtil.getDirColumnFamily(),
                        null);
        //2 create object table
        HBaseServiceImpl
                .createTable(connection, HosUtil.getObjTableName(bucket), HosUtil.getObjColumnFamily(),
                        HosUtil.OBJ_REGIONS);
        //3 add to seq table

        Put put = new Put(bucket.getBytes());
        put.addColumn(HosUtil.BUCKET_DIR_SEQ_CF_BYTES, HosUtil.BUCKET_DIR_SEQ_QUALIFIER,
                Bytes.toBytes(0L));
        HBaseServiceImpl.putRow(connection, HosUtil.BUCKET_DIR_SEQ_TABLE, put);
        //4 create hdfs dir
        fileStore.mkDir(HosUtil.FILE_STORE_ROOT + "/" + bucket);
    }

    @Override
    public void deleteBucketStore(String bucket) throws IOException {

        //1 delete dir table and object table
        HBaseServiceImpl.deleteTable(connection, HosUtil.getDirTableName(bucket));
        HBaseServiceImpl.deleteTable(connection, HosUtil.getObjTableName(bucket));
        //2 delete row in seq table
        HBaseServiceImpl.deleteRow(connection, HosUtil.BUCKET_DIR_SEQ_TABLE, bucket);
        //3 delete dir in hdfs
        fileStore.deleteDir(HosUtil.FILE_STORE_ROOT + "/" + bucket);
    }

    @Override
    public void createSeqTable() throws IOException {
        HBaseServiceImpl.createTable(connection, HosUtil.BUCKET_DIR_SEQ_TABLE,
                new String[]{HosUtil.BUCKET_DIR_SEQ_CF}, null);
    }

    @Override
    public void put(String bucket, String key, ByteBuffer content, long length, String mediaType, Map<String, String> properties) throws IOException {

    }

    @Override
    public HosObjectSummary getSummary(String bucket, String key) throws IOException {
        return null;
    }

    @Override
    public List<HosObjectSummary> list(String bucket, String startKey, String endKey) throws IOException {
        return null;
    }

    @Override
    public ObjectListResult listDir(String bucket, String dir, String start, int maxCount) throws IOException {
        return null;
    }

    @Override
    public ObjectListResult listByPrefix(String bucket, String dir, String start, String prefix, int maxCount) throws IOException {
        return null;
    }

    @Override
    public HosObject getObject(String bucket, String key) throws IOException {
        return null;
    }

    @Override
    public void deleteObject(String bucket, String key) throws IOException {

    }
}
