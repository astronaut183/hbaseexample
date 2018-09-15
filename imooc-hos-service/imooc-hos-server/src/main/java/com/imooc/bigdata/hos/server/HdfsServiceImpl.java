package com.imooc.bigdata.hos.server;

import com.imooc.bigdata.hos.core.ErrorCodes;
import com.imooc.bigdata.hos.core.HosConfiguration;
import org.apache.commons.io.FileExistsException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class HdfsServiceImpl implements IHdfsService {
    private static Logger logger = Logger.getLogger(HdfsServiceImpl.class);

    private FileSystem fileSystem;
    private long defaultBlockSize = 128 * 1024 * 1024;
    private long initBlockSize = defaultBlockSize / 2;

    public HdfsServiceImpl() throws Exception {
        //1.read message about HDFS
        HosConfiguration hosConfiguration = HosConfiguration.getConfiguration();
        String confDir = hosConfiguration.getString("hadoop.conf.dir");
        String hdfsUri = hosConfiguration.getString("hadoop.uri");
        //hdfs://localhost:9000
        //2.get filesystem
        Configuration configuration = new Configuration();
        configuration.addResource(new Path(confDir + "/hdfs-site.xml"));
        configuration.addResource(new Path(confDir + "/core-site.xml"));
        fileSystem = FileSystem.get(new URI(hdfsUri), configuration);
    }

    @Override
    public void saveFile(String dir, String name, InputStream inputStream, long length, short relication) throws IOException {

        //1.dir?true:new
        Path dirPath = new Path(dir);
        try {
            if (!fileSystem.exists(dirPath)) {
                boolean succ = fileSystem.mkdirs(dirPath, FsPermission.getDirDefault());
                logger.info("create dir " + dirPath);
                if (!succ) {
                    throw new HosServerException(ErrorCodes.ERROR_HDFS, "create dir" + dirPath + "error");
                }
            }
        } catch (FileExistsException ex) {
            ex.printStackTrace();
        }
        //2.save
        Path path = new Path(dir + "/" + name);
        long blockSize = length <= initBlockSize ? initBlockSize : defaultBlockSize;
        FSDataOutputStream outputStream = fileSystem
                .create(path, true, 512 * 1024, relication, blockSize);

        try {
            fileSystem.setPermission(path, FsPermission.getFileDefault());
            byte[] buffer = new byte[512 * 1024];
            int len = -1;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
        } finally {
            inputStream.close();
            outputStream.close();
        }
    }

    @Override
    public void deleteFile(String dir, String name) throws IOException {
        fileSystem.delete(new Path(dir + "/" + name), false);
    }

    @Override
    public InputStream openFile(String dir, String name) throws IOException {
        return fileSystem.open(new Path(dir + "/" + name));
    }

    @Override
    public void mkDir(String dir) throws IOException {
        fileSystem.mkdirs(new Path(dir));
    }

    @Override
    public void deleteDir(String dir) throws IOException {
        fileSystem.delete(new Path(dir), true);
    }
}
