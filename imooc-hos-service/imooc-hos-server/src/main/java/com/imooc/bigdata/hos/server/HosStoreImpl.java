package com.imooc.bigdata.hos.server;

import com.google.common.base.Strings;
import com.imooc.bigdata.hos.common.HosObject;
import com.imooc.bigdata.hos.common.HosObjectSummary;
import com.imooc.bigdata.hos.common.ObjectListResult;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ByteBufferInputStream;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
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
    public void createBucketStore(String bucket) throws IOException {
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
    public void put(String bucket, String key, ByteBuffer content, long length, String mediaType,
                    Map<String, String> properties) throws Exception {
        //1 if create dir
        InterProcessMutex lock = null;
        if (key.endsWith("/")) {
            putDir(bucket, key);
            return;
        }

        //get seqid
        String dir = key.substring(0, key.lastIndexOf("/") + 1);
        String hash = null;
        while (hash == null) {
            if (!dirExist(bucket, dir)) {
                hash = putDir(bucket, dir);
            } else {
                hash = getDirSeqId(bucket, dir);
            }
        }
        //2 upload file to obj table

        //get lock
        String lockey = hash +"_"+key.substring(key.lastIndexOf("/")+1);
        lock = new InterProcessMutex(zkClient, "/hos/"+bucket+"/"+lockey);
        lock.acquire();
        //upload file
        String fileKey = hash+"_"+key.substring(key.lastIndexOf("/")+1);
        Put contentPut = new Put(fileKey.getBytes());

        if(!Strings.isNullOrEmpty(mediaType)){
            contentPut.addColumn(HosUtil.OBJ_META_CF_BYTES, HosUtil.OBJ_MEDIATYPE_QUALIFIER,
                    mediaType.getBytes());
        }
        //todo add props lenth

        //if object less than 20m, put hbase : put hdfs;
        if(length <=HosUtil.FILE_STORE_THRESHOLD){
            ByteBuffer byteBuffer = ByteBuffer.wrap(HosUtil.OBJ_CONT_QUAILIFIER);
            contentPut.addColumn(HosUtil.OBJ_META_CF_BYTES, HosUtil.OBJ_MEDIATYPE_QUALIFIER,
                    mediaType.getBytes());
            byteBuffer.clear();
        }else{
            String fileDir = HosUtil.FILE_STORE_ROOT+"/"+bucket+"/"+hash;
            String name = key.substring(key.lastIndexOf("/")+1);
            InputStream inputStream = new ByteBufferInputStream(content);
            fileStore.saveFile(fileDir, name, inputStream, length, (short)1);
        }
        HBaseServiceImpl1.putRow(connection, HosUtil.getObjTableName(bucket),contentPut);
        //free lock
        if(lock!= null){
            lock.release();
        }
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


    private boolean dirExist(String bucket, String key) throws IOException {
        return HBaseServiceImpl1.existsRow(connection, HosUtil.getDirTableName(bucket), key);
    }

    private String getDirSeqId(String bucket, String key) throws IOException {
        Result result = HBaseServiceImpl1.getRow(connection, HosUtil.getDirTableName(bucket), key);
        if (result.isEmpty()) {
            return null;
        }
        return Bytes.toString(result.getValue(HosUtil.DIR_META_CF_BYTES, HosUtil.DIR_SEQID_QUALIFIER));
    }

    private String putDir(String bucket, String key) throws Exception {
        if (dirExist(bucket, key)) {
            return null;
        }

        // get lock from zk
        InterProcessMutex lock = null;
        try {
            String lockey = key.replace("/", "_");
            lock = new InterProcessMutex(zkClient, "/hos" + bucket + "/" + lockey);
            lock.acquire();
            String dir1 = key.substring(0, key.lastIndexOf("/"));
            String name = dir1.substring(dir1.lastIndexOf("/"));

            if (name.length() > 0) {
                String parent = dir1.substring(0, dir1.lastIndexOf("/") + 1);
                if (!dirExist(bucket, parent)) {
                    this.putDir(bucket, parent);
                }
                //add sub cf in parent dir     add row
                Put put = new Put(Bytes.toBytes(parent));
                put.addColumn(HosUtil.DIR_SUBDIR_CF_BYTES, Bytes.toBytes(name), Bytes.toBytes("1"));
                HBaseServiceImpl1.putRow(connection, HosUtil.getDirTableName(bucket), put);
            }
            //add to dir table
            String seqId = getDirSeqId(bucket, key);
            String hash = seqId == null ? makeDirSeqId(bucket) : seqId;
            Put dirput = new Put(key.getBytes());
            dirput.addColumn(HosUtil.DIR_META_CF_BYTES, HosUtil.DIR_SEQID_QUALIFIER, Bytes.toBytes(hash));
            HBaseServiceImpl1.putRow(connection, HosUtil.getDirTableName(bucket), dirput);
            return hash;
        } finally {
            if (lock != null) {
                lock.release();
            }
        }
    }

    private String makeDirSeqId(String bucket) throws IOException {
        long v = HBaseServiceImpl.incrementColumnValue(connection, HosUtil.BUCKET_DIR_SEQ_TABLE, bucket,
                HosUtil.BUCKET_DIR_SEQ_CF_BYTES, HosUtil.BUCKET_DIR_SEQ_QUALIFIER, 1);
        return String.format("%da%d", v % 64, v);
    }
}
