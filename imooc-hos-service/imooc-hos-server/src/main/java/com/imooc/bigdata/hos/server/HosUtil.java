package com.imooc.bigdata.hos.server;

import org.apache.hadoop.hbase.util.Bytes;

public class HosUtil {
    static final String DIR_TABLE_PREFIX = "hos_dir_";
    static final String OBJ_TABLE_PREFIX = "hos_obj_";

    static final String DIR_META_CF = "cf";
    static final byte[] DIR_META_CF_BYTES = "cf".getBytes();
    static final String DIR_SUBDIR_CF = "sub";
    static final byte[] DIR_SUBDIR_CF_BYTES = "sub".getBytes();

    static final String OBJ_META_CF = "cf";
    static final byte[] OBJ_META_CF_BYTES = "cf".getBytes();
    static final String OBJ_CONT_CF = "c";
    static final byte[] OBJ_CONT_CF_BYTES = "c".getBytes();

    static final byte[] DIR_SEQID_QUALIFIER = "u".getBytes();
    static final byte[] OBJ_CONT_QUAILIFIER = "c".getBytes();
    static final byte[] OBJ_LEN_QUAILIFIER = "l".getBytes();
    static final byte[] OBJ_PROPS_QUALIFIER = "p".getBytes();
    static final byte[] OBJ_MEDIATYPE_QUALIFIER = "m".getBytes();

    static final String FILE_STORE_ROOT = "/hos";
    static final int FILE_STORE_THRESHOLD = 20 * 1024 * 1024;

    static final String BUCKET_DIR_SEQ_TABLE = "hos_dir_seq";
    static final String BUCKET_DIR_SEQ_CF = "s";
    static final byte[] BUCKET_DIR_SEQ_CF_BYTES = BUCKET_DIR_SEQ_CF.getBytes();
    static final byte[] BUCKET_DIR_SEQ_QUALIFIER = "s".getBytes();

    static final byte[][] OBJ_REGIONS = new byte[][]{
            Bytes.toBytes("1"),
            Bytes.toBytes("4"),
            Bytes.toBytes("7")
    };

    static String getDirTableName(String bucketName) {
        return DIR_TABLE_PREFIX + bucketName;
    }

    static String getObjTableName(String bucketName) {
        return OBJ_TABLE_PREFIX + bucketName;
    }

    static String[] getDirColumnFamily() {
        return new String[]{DIR_META_CF, DIR_SUBDIR_CF};
    }

    static String[] getObjColumnFamily() {
        return new String[]{OBJ_CONT_CF, OBJ_META_CF};
    }
}
