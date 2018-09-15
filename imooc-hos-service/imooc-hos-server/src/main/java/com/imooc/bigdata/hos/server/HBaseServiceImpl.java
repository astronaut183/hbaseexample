package com.imooc.bigdata.hos.server;

import com.imooc.bigdata.hos.core.ErrorCodes;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.FilterList;

import java.util.Arrays;
import java.util.List;

public class HBaseServiceImpl {
    //1 create table
    public static boolean createTable(Connection connection, String tableName, String[] cfs
            , byte[][] splitKeys) {
        try (HBaseAdmin admin = (HBaseAdmin) connection.getAdmin()) {
            if (admin.tableExists(tableName)) {
                return false;
            }
            HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
            Arrays.stream(cfs).forEach(cf -> {
                HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(cf);
                hColumnDescriptor.setMaxVersions(1);
                tableDescriptor.addFamily(hColumnDescriptor);
            });
            admin.createTable(tableDescriptor, splitKeys);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HosServerException(ErrorCodes.ERROR_HBASE, "create table error");
        }
        return true;
    }

    //2 delete table
    public static boolean deleteTable(Connection connection, String tableName) {
        try (HBaseAdmin admin = (HBaseAdmin) connection.getAdmin()) {
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HosServerException(ErrorCodes.ERROR_HBASE, "delete table error");
        }
        return true;
    }
    //3 delete cf
    public static boolean deleteColumnFamily(Connection connection, String tableName, String cf){
        try (HBaseAdmin admin = (HBaseAdmin) connection.getAdmin()) {
            admin.deleteColumn(tableName, cf);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HosServerException(ErrorCodes.ERROR_HBASE, "delete cf error");
        }
        return true;
    }
    //4 delete cal
    public static boolean deleteColumnQualifier(Connection connection, String tableName,
                                                String rowKey, String cf, String column){
        Delete delete = new Delete(rowKey.getBytes());
        delete.addColumn(cf.getBytes(),column.getBytes());
        return deleteRow(connection, tableName, delete);
    }

    public static boolean deleteRow(Connection connection, String tableName, Delete delete){
        try(Table table = connection.getTable(TableName.valueOf(tableName))){
            table.delete(delete);
        }catch(Exception e){
            throw new HosServerException(ErrorCodes.ERROR_HBASE, "delete qualifier error");
        }
        return true;
    }
    //5 delete row
    public static boolean deleteRow(Connection connection, String tableName, String rowKey){
        Delete delete = new Delete(rowKey.getBytes());
        return deleteRow(connection, tableName, delete);
    }
    //6 read row
    public static Result getRow(Connection connection, String tableName, Get get){
        try(Table table = connection.getTable(TableName.valueOf(tableName))){
            return table.get(get);
        }catch(Exception e){
            throw new HosServerException(ErrorCodes.ERROR_HBASE, "delete get error");
        }
    }

    public static Result getRow(Connection connection, String tableName, String rowKey){
        Get get = new Get(rowKey.getBytes());
        return getRow(connection, tableName, get);
    }
    //7 get scanner

    public static ResultScanner getScanner(Connection connection, String tableName, Scan scan){
        try(Table table = connection.getTable(TableName.valueOf(tableName))){
            return table.getScanner(scan);
        }catch(Exception e){
            throw new HosServerException(ErrorCodes.ERROR_HBASE, "delete scanner error");
        }
    }

    public static ResultScanner getScanner(Connection connection, String tableName,
                                           String startKey, String endKey, FilterList filterList){
        Scan scan = new Scan();
        scan.setStartRow(startKey.getBytes());
        scan.setStopRow(endKey.getBytes());
        scan.setFilter(filterList);
        return getScanner(connection, tableName, scan);
    }
    //8 insert row

    public static boolean putRow(Connection connection, String tableName, Put put){
        try(Table table = connection.getTable(TableName.valueOf(tableName))){
            table.put(put);
        }catch(Exception e){
            throw new HosServerException(ErrorCodes.ERROR_HBASE, "put row error");
        }
        return true;
    }
    //9 insert rows
    public static boolean putRow(Connection connection, String tableName, List<Put> puts){
        try(Table table = connection.getTable(TableName.valueOf(tableName))){
            table.put(puts);
        }catch(Exception e){
            throw new HosServerException(ErrorCodes.ERROR_HBASE, "put row error");
        }
        return true;
    }

    //10 incrementColumnValue    create seqId

    public static long incrementColumnValue(Connection connection, String tableName,
                                            String row, String cf, String qual, int num){
        try(Table table = connection.getTable(TableName.valueOf(tableName))){
            return table.incrementColumnValue(row.getBytes(), cf.getBytes(), qual.getBytes(), num);
        }catch(Exception e){
            throw new HosServerException(ErrorCodes.ERROR_HBASE, "create seqId error");
        }
    }
}
