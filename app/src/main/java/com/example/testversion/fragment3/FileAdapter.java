package com.example.testversion.fragment3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.testversion.R;
import com.example.testversion.activity.MyDataBaseHelper;
import com.example.testversion.activity.MyDataBaseMethod;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FileAdapter extends BaseAdapter {

    //创建view时必须要提供context
    private Activity activity;
    private String TAG = "FileAdapter";
    private Context context;
    private LayoutInflater layoutInflater;
    private MyDataBaseHelper dbhelper;
    private MyDataBaseMethod myDBMethod;
    private SQLiteDatabase db;
    private Map viewMap;
    //提供数据源，文件列表
    public List<File> list=new LinkedList<File>();
    public List<String> tablename = new LinkedList<>();
    public List<String> tabledate = new LinkedList<>();
    //当前列表路径
    public FileAdapter(Context context)
    {
//        this.list = list;
        viewMap = new HashMap();
        this.context=context;
        this.layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        Log.i(TAG, "getCount: "+tablename.size());
        return tablename.size();
    }

    @Override
    public Object getItem(int position) {
        return tablename.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        Log.i(TAG, "getView: run!");
        /*很重要：内容和list通过适配器Adapter进行连接显示。
         * 首先将ListView和Adapter进行连接。
         * 内容和Adapter连接是通过list.add()和list.get(position)
         * 获取list中指定位置的元素:将list中的元素显示*/

        //申明一个视图装载listView条目
//        View v=View.inflate(activity, R.layout.filelist_item, null);
        convertView =  layoutInflater.inflate(R.layout.filelist_item,null);
        LinearLayout layout = convertView.findViewById(R.id.layout);
        //在view 视图中查找组件6
        TextView textName =convertView.findViewById(R.id.table_name);
        TextView textDate = new TextView(context);
//        TextView textDate = convertView.findViewById(R.id.data_info);
        ImageView img = convertView.findViewById(R.id.image);
        //获取当前位置
        String name = tablename.get(position);
        String date = tabledate.get(position);
        //显示表名
        textName.setText(name);
        //显示日期
        if(!date.equals(" ")){
            textDate.setText(date);
            textDate.setTextSize(12);
            textDate.setPadding(25,3,0,0);
            layout.addView(textDate);
        }

//        Log.i("SQLite", "getView: "+name);

//        textDate.setText(date);
//        File f=list.get(position);
//        Log.i(TAG, "getView: "+ f.getName() );
//        //获取文件名和文件大小,绑定数据
//        textName.setText(f.getName());
//        textSize.setText(getFileSize(f));
        return convertView;
    }
    //扫描文件夹
    public List<File> scanFiles(String path) {
        list.clear();
        File dir = new File(path);
        File[] subFiles = dir.listFiles();
        //生成文件列表
        if (subFiles != null) {
            for (File f : subFiles) {
                if (f.isFile()) {
                    list.add(f);
                }
//            Log.i("FileAdapter", "scanFiles: "+f.getName());}
            }
            notifyDataSetChanged();
            notifyDataSetInvalidated();
        }
        return list;
    }
    //扫描数据库获取所有表名
    public void scanDataBase(SQLiteDatabase db){
        tablename.clear();
        tabledate.clear();
        Cursor cursor1 = db.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while(cursor1.moveToNext()){
            //遍历出表名
            String name = cursor1.getString(0);
            if(!name.equals("android_metadata")){//每次生成数据都会有一个android_metadata表生成，内容和字段皆为空，需要排除此表
                tablename.add(name);
                Log.i("SQLite","scanDataBase/ tablename: "+ name);
                //查询当前表中"Date"列的数据4
                Cursor cursor2 = db.query(name,new String[]{"Sign"},null,null,null,null,null);
                cursor2.moveToFirst();
                if(cursor2.getCount()==0){
                    tabledate.add(" ");
                }else{
                    String date = cursor2.getString(0);
                    tabledate.add(date);
                }
            }
        }
        notifyDataSetChanged();
        notifyDataSetInvalidated();
    }


    //计算文件大小
    public static String getFileSize(File f) {
        //申明变量
        int sub_index = 0;
        String show = "";
        //计算文件大小
        if (f.isFile()) {
            long length = f.length();

            if (length >= 1073741824) {
                sub_index = String.valueOf((float) length / 1073741824).indexOf(".");
                show = ((float) length / 1073741824 + "000").substring(0, sub_index + 3) + "GB";
            } else if (length >= 1048576) {
                sub_index = (String.valueOf((float) length / 1048576)).indexOf(".");
                show = ((float) length / 1048576 + "000").substring(0, sub_index + 3) + "MB";
            } else if (length >= 1024) {
                sub_index = (String.valueOf((float) length / 1024)).indexOf(".");
                show = ((float) length / 1024 + "000").substring(0, sub_index + 3) + "KB";
            } else if (length < 1024)
                show = String.valueOf(length) + "B";
        }
        return show;
    }


}
