package com.example.testversion.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.test.espresso.core.internal.deps.guava.util.concurrent.ThreadFactoryBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FileService extends Service  {
    private final String TAG = "FileService";
    private Calendar calendar;
    private String year,month,day,hour,minute,second;
    public final static String ACTION_DATA_AVAILABLE = "action.data.available";
    PowerManager.WakeLock wakeLock = null;
    /**
     * 自定义MyBinder类
     */
    public class MyBinder extends Binder{

        public boolean createFile(String filename){
            //获取新建文件的时间
            year  = String.valueOf(calendar.get(Calendar.YEAR));//获取年
            month = String.valueOf(calendar.get(Calendar.MONTH)+1);//获取月
            day   = String.valueOf(calendar.get(Calendar.DATE));//获取日
            if(calendar.get(Calendar.AM_PM)==0){//时间为上午
                hour  = String.valueOf(calendar.get(Calendar.HOUR));//获取时
            }else {//时间为下午
                hour  = String.valueOf(calendar.get(Calendar.HOUR)+12);//获取时
            }
            minute = String.valueOf(calendar.get(Calendar.MINUTE));//获取分
            second = String.valueOf(calendar.get(Calendar.SECOND));//获取秒

            filename = filename +"_"+ year+"-"+month+"-"+day+"_"+hour+":"+minute+":"+second+".txt";
            try {
                Log.i(TAG, "createFile: 新建了文件："+filename);
                datafile = new File(getBaseContext().getExternalFilesDir(null) + "/" + filename);
                System.out.println("文件路径是：" + datafile.getAbsoluteFile().toString());
                if (!datafile.exists()) {
                    datafile.getParentFile().mkdirs();
                    datafile.createNewFile();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        };
        // 开启子线程，开始解析并存储到文件
        public void startThread(boolean isReceive) {
            Log.i(TAG, "startThread: "+isReceive);
            startSaveThread(isReceive);
        }

    }

    private final  MyBinder myBinder= new MyBinder();


    //创建服务，执行耗时操作
    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {
        //广播注册
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DATA_AVAILABLE);//添加动作
        registerReceiver(broadcastReceiver,intentFilter);
        calendar = Calendar.getInstance();//获取一个实例
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));//实例设定时区（中国的时区为GMT+8:00）
        Log.i(TAG, "onCreate: 文件服务已创建！");

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"Tag" );
        wakeLock.acquire();

        super.onCreate();

    }

//    //开启服务，若未创建则创建，若已创建则不重复创建
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.i(TAG, "onStartCommand: 文件服务已开启！");
//        return super.onStartCommand(intent, flags, startId);
//    }

    //绑定服务，用来实现进度监控
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: 文件服务已绑定！");
        return myBinder;
    }

    //解绑服务
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind: 文件服务已解绑！");
        wakeLock.release();
        return super.onUnbind(intent);
    }

    //销毁服务
    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: 文件服务已销毁！");
        // 取消注册广播接收器
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }




    File datafile ;
    ConcurrentLinkedQueue<Integer> queue_sign =new ConcurrentLinkedQueue<>();//存储每个包的计数标志位
    ConcurrentLinkedQueue<String> queue_time =new ConcurrentLinkedQueue<>();//存储每个数据接收时间
    ConcurrentLinkedQueue<String> queue1 =new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<String> queue2 =new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<String> queue3 =new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<String> queue4 =new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<String> queue5 =new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<String> queue6 =new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<String> queue7 =new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<String> queue8 =new ConcurrentLinkedQueue<>();

    /**
     * 广播接收
     */
    String time ="";
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
                time = intent.getStringExtra("time");
                sendDataToQueue(arr,time);
            }
        }
    };
    double ce = 0.5364;
    private void sendDataToQueue(ArrayList<int[]> arr,String time){
            for(int i= 0;i<4;i++){//由于一个包中包含四个数据，四个数据接收时间相同，所以往队列中添加4次
                queue_time.offer(time);
            }

            for(int v:arr.get(0)){
                queue_sign.offer(v);
            }
            for(int v:arr.get(1)){
                //Log.i(TAG, "sendDataToQueue: "+v);
                String str = String.format("%.3f",v*ce);
//                String str = Integer.toString(v);
                queue1.offer(str);
            }
            for(int v:arr.get(2)){
                String str = String.format("%.3f",v*ce);
                queue2.offer(str);
            }
            for(int v:arr.get(3)){
                String str = String.format("%.3f",v*ce);
                queue3.offer(str);
            }
            for(int v:arr.get(4)){
                String str = String.format("%.3f",v*ce);
                queue4.offer(str);
            }
            for(int v:arr.get(5)){
                String str = String.format("%.3f",v*ce);
                queue5.offer(str);
            }
            for(int v:arr.get(6)){
                String str = String.format("%.3f",v*ce);
                queue6.offer(str);
            }
            for(int v:arr.get(7)){
                String str = String.format("%.3f",v*ce);
                queue7.offer(str);
            }
            for(int v:arr.get(8)){
                String str = String.format("%.3f",v*ce);
                queue8.offer(str);
            }
    }


    // 创建线程池
    ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("thread-call-runner-%d").build();
    ExecutorService executorService = new ThreadPoolExecutor(4, 5, 100,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), namedThreadFactory);



    /**
     * 数据存储进文件的线程
     */


    private void startSaveThread(boolean isReceive) {
        // Runnable中写线程中执行的任务
        final Runnable runnable2 = () -> {
            Log.i(TAG, "startSaveThread: 开启文件存储线程！");

            FileOutputStream fileOutputStream = null;

            try {
                fileOutputStream = new FileOutputStream(datafile);
                //将时间信息存入文件
                String DATE = year+"/"+month+"/"+day+"      "+hour+": "+minute+": "+second+"\n";
                fileOutputStream.write(DATE.getBytes());
                Log.i(TAG, "startSaveThread: DATA~"+DATE);
                while (isReceive) {//死循环不断检测是否有数据传来
                    if (queue1.size() > 255) {//每100个存储一次
                        while (!queue1.isEmpty()){
                            fileOutputStream.write((queue_sign.poll().toString()+" ").getBytes());
                            fileOutputStream.write((queue1.poll()+" ").getBytes());
                            fileOutputStream.write((queue2.poll()+" ").getBytes());
                            fileOutputStream.write((queue3.poll()+" ").getBytes());
                            fileOutputStream.write((queue4.poll()+" ").getBytes());
                            fileOutputStream.write((queue5.poll()+" ").getBytes());
                            fileOutputStream.write((queue6.poll()+" ").getBytes());
                            fileOutputStream.write((queue7.poll()+" ").getBytes());
                            fileOutputStream.write((queue8.poll()+" ").getBytes());
                            fileOutputStream.write((queue_time.poll()+" \n").getBytes());
                            Log.i("Receive", "FileService: 存入文件！");
                        }

                        //fileOutputStream.write("\n".getBytes());
                        //fileOutputStream.write((queueInt1.poll().toString() + "  ").getBytes());

                    }
                }
                while (!isReceive) {
                    while (!queue1.isEmpty()) {//结束存储时，将队列中剩余的不足255个元素存入文件中
                        fileOutputStream.write((queue_sign.poll().toString()+" ").getBytes());
                        fileOutputStream.write((queue1.poll()+" ").getBytes());
                        fileOutputStream.write((queue2.poll()+" ").getBytes());
                        fileOutputStream.write((queue3.poll()+" ").getBytes());
                        fileOutputStream.write((queue4.poll()+" ").getBytes());
                        fileOutputStream.write((queue5.poll()+" ").getBytes());
                        fileOutputStream.write((queue6.poll()+" ").getBytes());
                        fileOutputStream.write((queue7.poll()+" ").getBytes());
                        fileOutputStream.write((queue8.poll()+" ").getBytes());
                        fileOutputStream.write((queue_time.poll()+" \n").getBytes());
                    }
                }

                Log.i(TAG, "run: ~~ rf close");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        executorService.execute(runnable2);
    }


//    private boolean createFile(String fileName) {
//        try {
//            Log.i(TAG, "createFile: 新建了文件："+fileName);
//            datafile = new File(getBaseContext().getExternalFilesDir(null) + "/" + fileName);
//            System.out.println("文件路径是：" + datafile.getAbsoluteFile().toString());
//            if (!datafile.exists()) {
//                datafile.getParentFile().mkdirs();
//                datafile.createNewFile();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }




}
