package com.imooc.bigdata;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.ByteRange;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class HBaseUtil {

    /**
     *创建表
     *
     * @param tableName  表名
     * @param cfs  列族的数组
     * @return  是否创建成功
     */
    public static boolean createTable(String tableName,String[] cfs){
        try(HBaseAdmin admin = (HBaseAdmin) HBaseConn.getHBaseConn().getAdmin()){
            if(admin.tableExists((tableName))){
                return false;
            }
            final HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
            Arrays.stream(cfs).forEach(cf ->{
                HColumnDescriptor columnDescriptor = new HColumnDescriptor(cf);
                columnDescriptor.setMaxVersions(1);
                tableDescriptor.addFamily(columnDescriptor);
            });
            admin.createTable(tableDescriptor);
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 删除表
     * @param tableName 表名
     * @return  是否删除成功
     */
    public static boolean deleteTable(String tableName){
        try(HBaseAdmin admin = (HBaseAdmin) HBaseConn.getHBaseConn().getAdmin()) {
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     *
     * @param tableName   表名
     * @param rowKey      唯一标识
     * @param cfName    列族名
     * @param qualifiter    列标识
     * @param data      数据
     * @return  是否插入成功
     */
    public static boolean putRow(String tableName, String rowKey, String cfName, String qualifiter,
                                 String data){
        try(Table table = HBaseConn.getTable(tableName)){
            Put put  = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes(cfName), Bytes.toBytes(qualifiter), Bytes.toBytes(data));
            table.put(put);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }

        return true;
    }

    public static boolean putRows(String tableName, List<Put> puts){
        try(Table table = HBaseConn.getTable(tableName)){
            table.put(puts);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }

        return true;
    }


    public static Result getRow(String tableName, String rowKey){
        try(Table table = HBaseConn.getTable(tableName)){
            Get get = new Get(Bytes.toBytes(rowKey));
            return table.get(get);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }

        return null;
    }


    public static Result getRow(String tableName, String rowKey, FilterList filterList){
        try(Table table = HBaseConn.getTable(tableName)){
            Get get = new Get(Bytes.toBytes(rowKey));
            get.setFilter(filterList);
            return table.get(get);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }

        return null;
    }

    public static ResultScanner getScanner(String tableName){
        try(Table table = HBaseConn.getTable(tableName)){
            Scan scan = new Scan();
            scan.setCaching(1000);
            return table.getScanner(scan);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }

        return null;
    }

    /**
     *
     * @param tableName    表名
     * @param startRowKey   起始RowKey
     * @param endRowKey     终止RowKey
     * @return  resultScanner实例
     */
    public static ResultScanner getScanner(String tableName, String startRowKey, String endRowKey){
        try(Table table = HBaseConn.getTable(tableName)){
            Scan scan = new Scan();
            scan.setStartRow(Bytes.toBytes(startRowKey));
            scan.setStopRow(Bytes.toBytes(endRowKey));
            scan.setCaching(1000);
            return table.getScanner(scan);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }

        return null;
    }


    public static ResultScanner getScanner(String tableName, String startRowKey, String endRowKey,
                                           FilterList filterList){
        try(Table table = HBaseConn.getTable(tableName)){
            Scan scan = new Scan();
            scan.setStartRow(Bytes.toBytes(startRowKey));
            scan.setStopRow(Bytes.toBytes(endRowKey));
            scan.setFilter(filterList);
            scan.setCaching(1000);
            return table.getScanner(scan);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }

        return null;
    }

    /**
     * 删除一行记录
     * @param tableName  表名
     * @param rowKey   唯一标识
     * @return   是否删除成功
     */
    public static boolean deleteRow(String tableName, String rowKey){
        try(Table table = HBaseConn.getTable(tableName)){
            Delete delete = new Delete(Bytes.toBytes(rowKey));
            table.delete(delete);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        return true;
    }



    public static boolean deleteColumnFamily(String tableName, String cfName){

        try(HBaseAdmin admin = (HBaseAdmin) HBaseConn.getHBaseConn().getAdmin()) {
            admin.deleteColumn(tableName, cfName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    public static boolean deleteQualifiter(String tableName, String rowKey, String cfName, String qualifiter){
        try(Table table = HBaseConn.getTable(tableName)){
            Delete delete = new Delete(Bytes.toBytes(rowKey));
            delete.addColumn(Bytes.toBytes(cfName), Bytes.toBytes(qualifiter));
            table.delete(delete);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
        return true;
    }
}
