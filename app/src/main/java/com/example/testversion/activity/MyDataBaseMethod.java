package com.example.testversion.activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

public class MyDataBaseMethod {
    MyDataBaseHelper dbhelper;
    SQLiteDatabase SQdb;

    public MyDataBaseMethod(Context context){
        dbhelper = new MyDataBaseHelper(context,"EEG_DataBase",null,1);
        SQdb = dbhelper.getWritableDatabase();

    }
    /**
     * 方法 1：数据库中新建表
     */
    public void createTable(Context context, String tablename ){
        String DBtable;
        dbhelper = new MyDataBaseHelper(context,"EEG_DataBase",null,1);
        SQdb = dbhelper.getWritableDatabase();
        Cursor cursor = SQdb.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while(cursor.moveToNext()){
            //遍历出表名
            String name = cursor.getString(0);
            if(tablename.equals(name)){
                 tablename +="i";
            }
        }

        //在数据库中新建一个表
        DBtable = "create table "
                + tablename
                +" ("
                +"Sign INTEGER,"
                +"Channel1 REAL,"
                +"Channel2 REAL,"
                +"Channel3 REAL,"
                +"Channel4 REAL,"
                +"Channel5 REAL,"
                +"Channel6 REAL,"
                +"Channel7 REAL,"
                +"Channel8 REAL,"
                +"Time TEXT"
                +" )";

        SQdb.execSQL(DBtable);
    }

    /**
     * 方法 2：存入日期信息
     */
    public void getCurrentDate(Context context,String tablename){

        dbhelper = new MyDataBaseHelper(context,"EEG_DataBase",null,1);
        SQdb = dbhelper.getWritableDatabase();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = dateFormat.format(curDate);

        //将收到的数据对象的值存储到 ContentValues 中，ContentValues 存储的键应与数据库中的字段名一致
        ContentValues values = new ContentValues();
        values.put("Sign",str);
        values.put("Channel1",str);
        values.put("Channel2",str);
        values.put("Channel3",str);
        values.put("Channel4",str);
        values.put("Channel5",str);
        values.put("Channel6",str);
        values.put("Channel7",str);
        values.put("Channel8",str);
        SQdb.insert(tablename,null,values);
    }
    /**
     * 方法 3：插入数据
     */
    public void insertData(Context context, String tablename, ArrayList<int[]> array){

        dbhelper = new MyDataBaseHelper(context,"EEG_DataBase",null,1);
        SQdb = dbhelper.getWritableDatabase();
        if(SQdb.isOpen()){
           new Thread(new Runnable() {
               @Override
               public void run() {
                   /**如果对事务的操作缺省，则SQLite会为每一次操作开启一个事务耗费大量时间**/
                   //显示开启事务，以提高插入数据的效率
//                   SQdb.beginTransaction();
                   try{
                       ContentValues values = new ContentValues();
                       for(int i=0;i<array.get(1).length;i++){
                           values.put("Sign",(Integer)array.get(0)[i]);
                           values.put("Channel1",(float)array.get(1)[i]*0.5364);
                           values.put("Channel2",(float)array.get(2)[i]*0.5364);
                           values.put("Channel3",(float)array.get(3)[i]*0.5364);
                           values.put("Channel4",(float)array.get(4)[i]*0.5364);
                           values.put("Channel5",(float)array.get(5)[i]*0.5364);
                           values.put("Channel6",(float)array.get(6)[i]*0.5364);
                           values.put("Channel7",(float)array.get(7)[i]*0.5364);
                           values.put("Channel8",(float)array.get(8)[i]*0.5364);
                           values.put("Time",(float)array.get(8)[i]*0.5364);
                           //加入每一条数据的系统时间
                           SimpleDateFormat dateFormat = new SimpleDateFormat(" HH:mm:ss:S");
                           Date time = new Date(System.currentTimeMillis());
                           String str = dateFormat.format(time);
                           values.put("Time",str);
//                           Log.i("SQLite", "insertData/ channel1: "+(float)array.get(1)[i]*0.5364);
                           Log.i("Receive", "SQLite: 存入数据库！ ");
                           //插入数据库表中
                           SQdb.insert(tablename,null,values);
                       }
                   }catch (Exception e) {
                       // TODO Auto-generated catch block
                   }

//                   finally {
//                       // 结束事务
//                       if(SQdb != null && SQdb.inTransaction()){
//                           SQdb.endTransaction();
//                       }
//
//                   }
               }
           }).start();
        }

    }

    /**
     * 方法 3-2：插入数据
     */
    public void insertData2(Context context, String tablename, Queue<Queue<Float>> queue){

        dbhelper = new MyDataBaseHelper(context,"EEG_DataBase",null,1);
        SQdb = dbhelper.getWritableDatabase();
        if(SQdb.isOpen()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    /**如果对事务的操作缺省，则SQLite会为每一次操作开启一个事务耗费大量时间**/
                    //显示开启事务，以提高插入数据的效率
//                   SQdb.beginTransaction();
                    try{
                        ContentValues values = new ContentValues();
                        Queue<Float> ch1 = new LinkedList<>();
                        Queue<Float> ch2 = new LinkedList<>();
                        Queue<Float> ch3 = new LinkedList<>();
                        Queue<Float> ch4 = new LinkedList<>();
                        Queue<Float> ch5 = new LinkedList<>();
                        Queue<Float> ch6 = new LinkedList<>();
                        Queue<Float> ch7 = new LinkedList<>();
                        Queue<Float> ch8 = new LinkedList<>();
                        ch1 = queue.poll();
                        ch2 = queue.poll();
                        ch3 = queue.poll();
                        ch4 = queue.poll();
                        ch5 = queue.poll();
                        ch6 = queue.poll();
                        ch7 = queue.poll();
                        ch8 = queue.poll();
                        while(!ch1.isEmpty()){
                            values.put("Channel1",ch1.poll());
                            values.put("Channel2",ch2.poll());
                            values.put("Channel3",ch3.poll());
                            values.put("Channel4",ch4.poll());
                            values.put("Channel5",ch5.poll());
                            values.put("Channel6",ch6.poll());
                            values.put("Channel7",ch7.poll());
                            values.put("Channel8",ch8.poll());
                            SQdb.insert(tablename,null,values);
                        }
                    }catch (Exception e) {
                        // TODO Auto-generated catch block
                    }

//                   finally {
//                       // 结束事务
//                       if(SQdb != null && SQdb.inTransaction()){
//                           SQdb.endTransaction();
//                       }
//
//                   }
                }
            }).start();
        }

    }


    /**
     * 方法 4：删除表
     */
    public void deleteTable(Context context,String tablename){
        dbhelper = new MyDataBaseHelper(context,"EEG_DateBase",null,1);
        SQdb = dbhelper.getWritableDatabase();
        SQdb.execSQL("drop table "+tablename);
    }

    /**
     * 方法 5：获取数据库中所有表名
     */
    public ArrayList<String> getAllTable(Context context){
        ArrayList<String> DBtable = new ArrayList<String>();
        //实例化一个MyDataBaseHelper对象
        dbhelper = new MyDataBaseHelper(context,"EEG_DataBase",null,1);
        SQdb = dbhelper.getWritableDatabase();
        Cursor cursor = SQdb.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while(cursor.moveToNext()){
            //遍历出表名
            String name = cursor.getString(0);
            DBtable.add(name);
            Log.i("SQLite","scanDataBase/ tablename: "+ name);
        }
        return DBtable;
    }

    /**
     * 方法 6：查询数据
     *  @param context
     *  @param tablename 所要查询的表名
     *  @param columns 要查询的列
     *  @return
     * Note:
     *  query语句用法：
     *  query( String table, String[] columns, String selection, String[] selectionArgs,String groupBy, String having, String orderBy, String limit)
     */

    public Queue queryData(Context context, String tablename, String[] columns){

        dbhelper = new MyDataBaseHelper(context,"EEG_DataBase",null,1);
        SQdb = dbhelper.getWritableDatabase();
        //获取查询的列名
        String col = columns[0];
        Log.i("SQLite", "queryData/column: "+col+",filename: "+tablename);
        //创建存储数据的队列
        Queue<Float> que = new LinkedList<>();
        //获得游标
        Cursor cusor = SQdb.query(tablename,columns,null,null,null,null,null);
        int count = cusor.getCount();
        Log.i("SQLite", "queryData/count: "+count);
       cusor.moveToNext();
        while(cusor.moveToNext()){
            Float f = cusor.getFloat(cusor.getColumnIndex(col));
            if(f!=null){
                que.add(f);
            }
//            Log.i("SQLite", "queryData/data: "+f);
        }

        return que;
    }
    /**
     * 方法 7 ：查询信息
     */
    public String queryInfo(Context context, String tablename, String[] columns){

        dbhelper = new MyDataBaseHelper(context,"EEG_DataBase",null,1);
        SQdb = dbhelper.getWritableDatabase();

        Cursor cusor = SQdb.query(tablename,columns,null,null,null,null,null,"1");
        cusor.moveToFirst();
        String str = cusor.getString(0);
        Log.i("SQLite", "queryData: "+str);
        return str;
    }

    /**
     * 方法 8：导出CSV文件
     */
    public boolean Export_CSV(Context context, String tablename){
        boolean export_Ok = true;
        String[] columns = {"Channel1","Channel2","Channel3",
                "Channel4","Channel5","Channel6","Channel7", "Channel8", "Time"};
        dbhelper = new MyDataBaseHelper(context,"EEG_DataBase",null,1);
        SQdb = dbhelper.getWritableDatabase();
        //创建文件夹
        String foldername = context.getExternalFilesDir(null)+"/"+"CSV";
        File folder = new File(foldername);
        if (!folder.exists()){
            folder.mkdir();
        }
        String filename = foldername + "/" + tablename + ".csv";
        File file = new File(filename);
        Cursor cusor = SQdb.query(tablename,columns,null,null,null,null,null);
        int rowCount = 0;
        int colCount = 0;
        FileWriter fw;
        BufferedWriter bfw;
        try {
            rowCount = cusor.getCount();
            colCount = cusor.getColumnCount();
            Log.i("MyDataBaseMethod", "Export_CSV: rowCount = "+rowCount);
            Log.i("MyDataBaseMethod", "Export_CSV: colCount = "+colCount);
            fw = new FileWriter(file);
            bfw = new BufferedWriter(fw);
            if (rowCount > 0) {
                cusor.moveToFirst();
                // 写入表头
                for (int i = 0; i < colCount; i++) {
                    if (i != colCount - 1){
                        bfw.write(cusor.getColumnName(i) + ',');
                    }else{
                        bfw.write(cusor.getColumnName(i));
                    }

                }
                // 写好表头后换行
                bfw.newLine();
                // 写入数据
                for (int i = 0; i < rowCount; i++) {
                    cusor.moveToPosition(i);
                    for (int j = 0; j < colCount; j++) {
                        if (j != colCount - 1) {
                            if(cusor.getString(j)==null){
                                bfw.write("NaN" + ',');
                            }else {
                                bfw.write(cusor.getString(j) + ',');
                            }
                        }else {
                            Log.i("MyDataBaseMethod", "Export_CSV: cusor.getString(j) = "+cusor.getString(j));
                            if(cusor.getString(j)==null){
                                bfw.write("NaN");
                            }else{
                                bfw.write(cusor.getString(j));
                            }


                        }
                    }
                    // 写好每条记录后换行
                    bfw.newLine();
                }
            }
            // 将缓存数据写入文件
            bfw.flush();
            // 释放缓存
            bfw.close();
            // Toast.makeText(mContext, "导出完毕！", Toast.LENGTH_SHORT).show();
            Log.v("导出数据", "导出完毕！");
        } catch (IOException e) {
            export_Ok = false;
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            cusor.close();
        }
        return export_Ok;
    }
    /**
     * 方法 7:关闭数据库
     */
    public void closeDateBase(){
        if (SQdb!=null){
            SQdb.close();
        }
    }
}
