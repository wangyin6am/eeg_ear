package com.example.testversion.fragment1;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.test.espresso.core.internal.deps.guava.util.concurrent.ThreadFactoryBuilder;

import com.example.testversion.R;
import com.example.testversion.activity.MainActivity;
import com.example.testversion.activity.MyDataBaseHelper;
import com.example.testversion.activity.MyDataBaseMethod;
import com.example.testversion.service.FileService;
import com.example.testversion.service.SQLiteService;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.content.Context.VIBRATOR_SERVICE;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.example.testversion.service.BleService.ACTION_GATT_CONNECTED;
import static com.example.testversion.service.BleService.ACTION_GATT_DISCONNECTED;


public class Fragment1 extends Fragment implements MainActivity.SendValue {
    private View view;

    private String[] channel = new String[]{"通道1","通道2","通道3","通道4","通道5","通道6","通道7","通道8"};
    private TextView tv_ch,chText;
            ListView listView;
    private String choose_ch;

    TextView tv_ch1,tv_ch2,tv_ch3,tv_ch4,tv_ch5,tv_ch6,tv_ch7,tv_ch8;
    ArrayList<TextView> CH_R= new ArrayList<TextView>();
    String ACTION_DATA_AVAILABLE = "action.data.available";
    private EditText edit_filename;
    private EditText edit_hour;
    private EditText edit_min;
    private TextView text_timer;
    private TextView filepath;

    private Button btn_file,btn_timer,btn_testR;

    private MyDataBaseHelper dbhelper;
    private SQLiteDatabase db;
    public String filename ="新建数据文件";

    private Context fcontext;
    private long totalTime;
    private boolean isReceive = false;
    private CountDownTimer countDownTimer;
    Vibrator vibrator;
    Handler.Callback callback;
    Handler handler;
    FileService.MyBinder fileBinder;
    SQLiteService.IMyBinder sqliteBinder;
    private MyDataBaseMethod myDBMethod;
    boolean isCountDown = false;//传递给MainActivity的计时开始信息
    boolean isConnect = false;//接受MainActivity传来的连接状态信息
   // private DataInterface mdataInterface;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_1, container, false);
        this.fcontext = getActivity();
        initView(view);
        return view;
    }

    private void initView(View view) {
        edit_filename = view.findViewById(R.id.edit_file);
        edit_hour  = view.findViewById(R.id.edit_hour);
        edit_min   = view.findViewById(R.id.edit_min);
        text_timer = view.findViewById(R.id.text_timer);
//        filepath   = view.findViewById(R.id.filepath);

        tv_ch1 = view.findViewById(R.id.ch1_R);
        tv_ch2 = view.findViewById(R.id.ch2_R);
        tv_ch3 = view.findViewById(R.id.ch3_R);
        tv_ch4 = view.findViewById(R.id.ch4_R);
        tv_ch5 = view.findViewById(R.id.ch5_R);
        tv_ch6 = view.findViewById(R.id.ch6_R);
        tv_ch7 = view.findViewById(R.id.ch7_R);
        tv_ch8 = view.findViewById(R.id.ch8_R);

        CH_R.add(tv_ch1);
        CH_R.add(tv_ch2);
        CH_R.add(tv_ch3);
        CH_R.add(tv_ch4);
        CH_R.add(tv_ch5);
        CH_R.add(tv_ch6);
        CH_R.add(tv_ch7);
        CH_R.add(tv_ch8);

        btn_testR = view.findViewById(R.id.btn_testR);
        btn_file  = view.findViewById(R.id.button_file);
        btn_timer = view.findViewById(R.id.button_timer);
        btn_timer.setEnabled(false);

        vibrator = (Vibrator) fcontext.getSystemService(VIBRATOR_SERVICE);

//        filepath.setText("文件路径为： "+fcontext.getExternalFilesDir(null).toString());
        //广播注册
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        fcontext.registerReceiver(broadcastReceiver,intentFilter);
        //实例化一个MyDataBaseHelper对象
        dbhelper = new MyDataBaseHelper(getContext(),"EEG_DataBase",null,1);
        db = dbhelper.getWritableDatabase();
        //实例化一个MyDataBaseMethod对象
        myDBMethod = new MyDataBaseMethod(getContext());
    }

    /**
     * @name：showChannel()
     * 功  能:自定义弹出对话框
     */
    public int[] CHANNEL;
    private void showChannel(){
        CHANNEL = new int[8];
        AlertDialog builder = new AlertDialog.Builder(this.getActivity())
                .setTitle("选择通道：")
                .setMultiChoiceItems(channel, new boolean[]{false, false, false, false, false, false, false, false}, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str  = "您选择的通道是：";
                        for(int i = 0;i<channel.length;i++){
                            if(listView.getCheckedItemPositions().get(i)){
                                CHANNEL[i]=1;
                               str += (String) listView.getAdapter().getItem(i)+"  ";
                            }else{ CHANNEL[i]=0;}
                        }
                        choose_ch = str;
                        chText.setText(choose_ch);
                    }
                }).create();
        listView = builder.getListView();
        builder.show();
        Handler handler = new Handler();
        Message msg = Message.obtain();
        msg.what = 5;
        msg.obj = CHANNEL;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        getActivity().registerReceiver(broadcastReceiver,intentFilter);
        super.onActivityCreated(savedInstanceState);

        /**
         * 点击事件 1：ch_choose
         * 功   能：对话窗方式选择通道
         */
//        tv_ch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showChannel();
//            }
//        });


        /**
         * 点击事件 2：btn_testR
         * 功    能：测阻抗
         */
        btn_testR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //向activity发送测阻值指令
                setResult();
                handler = new Handler(callback);
                Message message = Message.obtain();
                message.what=2;
                handler.sendMessage(message);
                Handler handler1 = new Handler();
//              handler1.postDelayed(()->setResult(),2100);
            }
        });


        /**
         * 作用控件：btn_file
         * 功    能：获取输入的文件名
         */
        btn_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TAG = "FileService";
                filename = edit_filename.getText().toString();
                if(filename.equals("")){
                    Toast.makeText(getActivity(),"请输入文件名", LENGTH_SHORT);
                }else{
//                    /* 在数据库中新建一个表 */
//                    myDBMethod.createTable(getContext(),filename);
//                    /* 存入当前date */
//                    myDBMethod.getCurrentDate(getContext(),filename);
//                    /* 查询当前date */
//                    String date = myDBMethod.queryInfo(getContext(),filename,new String[]{"Date"});
//                    Log.i("SQLite", "onClick/date: "+date);
                    edit_filename.clearFocus();
                    // edit_filename.setFocusableInTouchMode(true);
                    edit_filename.setText(filename);
                    btn_timer.setEnabled(true);
                    Log.i(TAG, "Filename: "+filename);
                }

            }
        });

        //设置定时按钮点击事件
        /**
         * 作用控件：btn_timer
         * 功    能：倒计时
         **/
        btn_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = btn_timer.getText().toString();//获取按钮的字符串
                String numH = edit_hour.getText().toString();
                String numM = edit_min.getText().toString();
                int h = 0;
                int min = 0;


                    if (str.equals("开始")) {
                        //绑定文件服务
                        bindFileService();
                        //绑定SQLite服务
                        bindSQLiteService();

                        if(!isConnect){//蓝牙状态为未连接
                            //关闭输入法
                            InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );
                            if ( imm.isActive( ) ) {//如果输入法处于打开状态
                                imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );
                            }
                            Toast.makeText(fcontext, "请连接后重试！", LENGTH_LONG).show();
                        }

                        else if (isConnect){//蓝牙状态为已连接
                            if (numH.equals("") && numM.equals("")) {
                                makeText(fcontext, "请输入时间后开始！", LENGTH_SHORT).show();
                            } else {
                                    if ((!numH.equals("")) && numM.equals("")) {
                                        h = Integer.parseInt(edit_hour.getText().toString());
                                        min = 0;
                                    } else if (numH.equals("") && (!(numM.equals("")))) {
                                        h = 0;
                                        min = Integer.parseInt(edit_min.getText().toString());
                                    } else if ((!numH.equals("")) && (!numM.equals(""))) {
                                        h = Integer.parseInt(edit_hour.getText().toString());
                                        min = Integer.parseInt(edit_min.getText().toString());
                                }
                                    System.out.println("设定时间为：" + h + "小时" + min + "分钟");
                                    totalTime = (h * 60 * 60 + min * 60) * 1000;
                                    System.out.println("总时间为：" + totalTime + "毫秒");
                                if (min > 60 || min == 60) {
                                    makeText(fcontext, "请重新输入！", LENGTH_SHORT).show();
                                    edit_min.setText("");
                                } else {
                                    isCountDown = true;
                                    isReceive = true;
                                    handler = new Handler(callback);
                                    Message message = Message.obtain();
                                    message.what=1;
                                    message.obj = filename;
//                                    handler.sendMessage(message);

                                    //开启倒计时的线程
                                    countDownThread(totalTime, 1000);
                                    edit_hour.clearFocus();
                                    edit_min.clearFocus();
                                    edit_hour.setFocusableInTouchMode(false);
                                    edit_min.setFocusableInTouchMode(false);
                                    edit_filename.setFocusableInTouchMode(false);
                                    btn_timer.setText("取消");
                                    //关闭输入法
                                    InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );
                                    if ( imm.isActive( ) ) {//如果输入法处于打开状态
                                        imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );
                                    }
                                }
                            }
                        }

                    }
                    else if(str.equals("取消")){
                        isReceive = false;
                        Message message = Message.obtain();
                        message.what = 0;
                        handler.sendMessage(message);
                        btn_timer.setText("开始");
                        countDownTimer.cancel();
                        countDownTimer.onFinish();
                        edit_hour.setText("");
                        edit_min.setText("");
                        edit_hour.setFocusableInTouchMode(true);
                        edit_min.setFocusableInTouchMode(true);
                    }

            }
        });

    }

    /**
     * Activity继承函数
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.callback = (Handler.Callback) context;

    }


    /**
     * 广播接收回调
     */
//    List<int[]> channel_list = new ArrayList<>();//创建包含8个链表的链表
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent == null){return;}
            String action = intent.getAction();
            if(ACTION_GATT_CONNECTED.equals(action)){
                isConnect = true;
            }else if(ACTION_GATT_DISCONNECTED.equals(action)) {
                isConnect = false;
                if (btn_timer.getText().toString().equals("取消")) {
                    countDownTimer.cancel();
                    countDownTimer.onFinish();
                }
            }
        }
    };


    /**
     * 根据阻抗，设置Textview
     */
    public void setResult(){
        Log.i("measure", "setResult! ");
//        ch_R = measureR(channel_list);
//        if (ch_R==null){
//            return;
//        }
        for(int i=0;i<8;i++){
            CH_R.get(i).setTextColor(Color.parseColor("#2baf2b"));
//            if(ch_R[i]<100000){
////                CH_R.get(i).setBackgroundColor(Color.parseColor("#2baf2b"));
//                CH_R.get(i).setTextColor(Color.parseColor("#2baf2b"));
//            }else {
////                CH_R.get(i).setBackgroundColor(Color.parseColor("#e84e40"));
//                CH_R.get(i).setTextColor(Color.parseColor("#e84e40"));
//            }
        }
        clearList();
    }
    /**
     * 计算电阻
     * @return :float[] ,返回每个通道的阻值
     */
    public float[] measureR(ArrayList[] list){
        float[] max = new float[8];
        float[] min = new float[8];
        float[] R = new float[8];
        if (list[0]!=null){
            for(int j=0;j<8;j++){
                ArrayList<Float> array;
                array = list[j];
                max[j] = array.get(0);
                min[j] = array.get(0);
                for(int i=0;i<array.size();i++){
                   if(max[j]<array.get(i)){
                       max[j] = array.get(i);
                   }
                   if(min[j]>array.get(i)){
                       min[j] = array.get(i);
                   }
                }
                Log.i("measureR", "max_"+j+": "+max[j]);
                Log.i("measureR", "min_"+j+": "+min[j]);
                R[j] = (max[j]-min[j])/6;//计算每个通道的电阻值
                Log.i("measureR", "R: "+j+": "+R[j]);
            }
        }
        return R;
    }


    float[] ch_R = new float[8];


    /**
     * 接口继承函数：getArray
     * 功能：接收来自activity的数据
     * 参数：int[] value, boolean receive
     * 返回值：void
     */

    ArrayList<Float> ch1_R = new ArrayList<>();
    ArrayList<Float> ch2_R = new ArrayList<>();
    ArrayList<Float> ch3_R = new ArrayList<>();
    ArrayList<Float> ch4_R = new ArrayList<>();
    ArrayList<Float> ch5_R = new ArrayList<>();
    ArrayList<Float> ch6_R = new ArrayList<>();
    ArrayList<Float> ch7_R = new ArrayList<>();
    ArrayList<Float> ch8_R = new ArrayList<>();

    ArrayList[] channel_list = new ArrayList[]{ch1_R,ch2_R,ch3_R,ch4_R,ch5_R,ch6_R,ch7_R,ch8_R};

    public void clearList(){
        for(int i=0;i<channel_list.length;i++){
            channel_list[i].clear();
        }
    }

    @Override
    public void getArray(int[] value, boolean receive) {
        if(value==null){return;}
//        for(float v:value.get(1)){
//            v = (float) (v*0.5364);
//            channel_list[0].add(v);
////             Log.i("measure", "ch1_R: "+v);
//        }
        for(int i=0;i<8;i++){
            for(int j=i;j<value.length;j+=8){
                channel_list[i].add((value[i]));
            }
        }
    }
    /**********************************   数据库绑定服务   **********************************/

    /**
     * 创建并绑定SQLite服务
     */
    private void bindSQLiteService(){
        Intent intent = new Intent(this.getActivity(),SQLiteService.class);
        getActivity().bindService(intent,conn_SQlite,Context.BIND_AUTO_CREATE);
    }
    private ServiceConnection conn_SQlite = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i("SQLiteService", "onServiceConnected: " + "SQlite服务已绑定！");
            Log.i("SQLiteService", "onServiceConnected/filename = "+filename);
            sqliteBinder  = (SQLiteService.IMyBinder)iBinder;
            sqliteBinder.createTable(getContext(),filename);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    /**
     * 停止数据库存储服务
     */
    private void unbindSQLiteService() {
        // 解绑前确认当前处在绑定状态，然后再进行解绑，否则会崩溃
        if (sqliteBinder != null && sqliteBinder.isBinderAlive()) {
            // getActivity().unbindService(conn);
            getActivity().unbindService(conn_SQlite);
        }
        isReceive = false;
//        Intent intent = new Intent(this.getActivity(), FileService.class);
//        fcontext.stopService(intent);
    }

    /**********************************  文件存储服务  *************************************/

    /**
     * 创建并绑定文件存储服务
     */
    private void bindFileService() {
        Intent intent = new Intent(this.getActivity(), FileService.class);
        //getActivity().startService(intent);
        getActivity().bindService(intent, conn_file, Context.BIND_AUTO_CREATE);
        isReceive = true;
    }

    /**
     * 文件存储服务绑定成功时回调
     */
    private ServiceConnection conn_file = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder Binder) {
            String TAG = "FileService";
            Log.i(TAG, "onServiceConnected: " + "文件服务已绑定！");
            fileBinder = (FileService.MyBinder) Binder;
            fileBinder.createFile(filename);
            fileBinder.startThread(isReceive);

          //  Log.i(TAG, "isReceive: "+isReceive);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            String TAG = "FileService";
//            Toast.makeText(getActivity(), "定时结束，存储完成！", LENGTH_SHORT).show();
            Log.i(TAG, "onServiceDisconnected: " + "文件服务绑定连接断开！");
        }
    };

    /**
     * 停止文件存储服务
     */
    private void unbindFileService() {
        // 解绑前确认当前处在绑定状态，然后再进行解绑，否则会崩溃
        if (fileBinder != null && fileBinder.isBinderAlive()) {
            // getActivity().unbindService(conn);
            getActivity().unbindService(conn_file);
        }
        isReceive = false;
//        Intent intent = new Intent(this.getActivity(), FileService.class);
//        fcontext.stopService(intent);
    }



    // 创建线程池
    ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("thread-call-runner-%d").build();
    ExecutorService executorService = new ThreadPoolExecutor(3, 3, 100,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), namedThreadFactory);



    /**
     * 线程名：countDownThread
     * 功 能：执行倒计时
     * 参 数：long ；long
     * 返回值：void
     */

    public void countDownThread(final long totaltime, final long unit) {

        countDownTimer = new CountDownTimer(totaltime, unit) {
            @Override
            public void onTick(long millisUntilFinished) {
                text_timer.setText(formatTime(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                //vibrator.vibrate(1000);
                text_timer.setText("00:00:00");
                edit_hour.setText("");
                edit_min.setText("");
                makeText(fcontext, "时间到，采集结束！", LENGTH_LONG).show();
                edit_hour.setFocusableInTouchMode(true);
                edit_min.setFocusableInTouchMode(true);
                edit_filename.setText("");
                edit_filename.setFocusableInTouchMode(true);
                btn_timer.setText("开始");
                Message message = Message.obtain();
                message.what = 0;
                handler.sendMessage(message);
                btn_timer.setEnabled(false);
                //解绑文件服务
                unbindFileService();
                //解绑SQLite服务
                unbindSQLiteService();

            }
        };
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                countDownTimer.start();
                System.out.println("开启countdowntimer");
            }
        };
        executorService.execute(runnable);
    }

    /**
     *  方法名：formatTime
     *  功 能：设置CountDownTimer剩余时间按 时、分、秒显示
     *  参 数：long
     *  返回值：String
     */
    public String formatTime ( long millisecond){
        String str = "00时00分00秒";
        int hour;
        int minute;//分钟
        int second;//秒数
        hour = (int) (((millisecond / 1000) / 60) / 60);
        minute = (int) (((millisecond / 1000) / 60)%60);
        second = (int) ((millisecond / 1000) % 60);

        if (hour < 10) {
            if (minute < 10 && second < 10) {
                str = "0" + hour + ": " + "0" + minute + ": " + "0" + second ;
            } else if (minute >= 10 && second < 10) {
                str = "0" + hour + ": " + minute + ": " + "0" + second;
            } else if (minute >=10 && second >=10) {
                str = "0" + hour + ": " + minute + ": " + second ;
            } else if (minute < 10 && second >= 10) {
                str = "0" + hour + ": " + "0" + minute + ": " + second ;
            }
        } else {
            if (minute < 10 && second < 10) {
                str = hour + ": " + "0" + minute + ": " + "0" + second ;
            } else if (minute >= 10 && second < 10) {
                str = hour + ": " + minute + ": " + "0" + second;
            } else if (minute >= 10 && second >= 10) {
                str = hour + ": " + minute + ": " + second ;
            } else if (minute < 10 && second >= 10) {
                str = hour + ": " + "0" + minute + ": " + second ;
            }
        }
        return str;
    }


}