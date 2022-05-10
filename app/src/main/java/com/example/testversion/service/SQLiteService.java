package com.example.testversion.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.example.testversion.activity.MyDataBaseMethod;
import com.example.testversion.fragment1.Fragment1;

import java.util.ArrayList;

public class SQLiteService extends Service {
    String TAG = "SQLiteService";
    String Tablename;
    PowerManager.WakeLock wakeLock = null;
    MyDataBaseMethod myDBMethod;
    public final static String ACTION_DATA_AVAILABLE = "action.data.available";
    public SQLiteService() {
    }

    /**
     * 自定义MyBinder类
     */
    public class IMyBinder extends Binder{
        /* 在数据库中新建一个表 */
        public void createTable(Context context,String tablename){
            Tablename = tablename;
            myDBMethod.createTable(context,tablename);
            //存入文件建立的时间
            myDBMethod.getCurrentDate(context,tablename);
        }

        /* 获取文件的建立时间 */
        public String getDate(Context context,String tablename){
            String date = myDBMethod.queryInfo(context,tablename,new String[]{"Sign"});
            Log.i("SQLite", "onClick/date: "+date);
            return date;
        }




    }

    private final IMyBinder iMyBinder = new IMyBinder();

    @Override
    public void onCreate() {
        //广播注册
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DATA_AVAILABLE);//添加动作
        registerReceiver(broadcastReceiver,intentFilter);
        myDBMethod = new MyDataBaseMethod(getBaseContext());
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,TAG);
        wakeLock.acquire();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.i(TAG, "onBind: SQLite服务已绑定！");
        return iMyBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        wakeLock.release();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: SQLite服务已销毁！");
        // 取消注册广播接收器
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }


    /**
     * 广播接收
     */
    Fragment1 fragment1  = new Fragment1();
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent == null){
                return;
            }
            String action = intent.getAction();
            if(ACTION_DATA_AVAILABLE.equals(action)){
//                Log.i(TAG, "onReceive: 收到了数据!");
                //获取广播的数据
                ArrayList<int[]> arr = (ArrayList<int[]>) intent.getSerializableExtra("ack");
                myDBMethod.insertData(context,Tablename,arr);
                Log.i(TAG, "onReceive: 插入数据库！");
//                Log.i(TAG, "onReceive/tablename =  "+Tablename);

            }
        }
    };


}