                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               package com.example.testversion.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.testversion.R;
import com.example.testversion.ShowToast;
import com.example.testversion.fragment1.Fragment1;
import com.example.testversion.fragment2.Fragment2;
import com.example.testversion.fragment3.Fragment3;
import com.example.testversion.fragment4.Fragment4;
import com.example.testversion.fragment5.Fragment5;
import com.example.testversion.fragment6.Fragment6;
import com.example.testversion.fragment6.PSQISubmitFragment;
import com.example.testversion.service.BleService;
import com.example.testversion.service.bleDataViewModel;

import org.pytorch.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.example.testversion.service.BleService.ACTION_DATA_AVAILABLE;
import static com.example.testversion.service.BleService.ACTION_GATT_CONNECTED;
import static com.example.testversion.service.BleService.ACTION_GATT_DISCONNECTED;
import static com.example.testversion.service.BleService.ACTION_GATT_SERVICES_DISCOVERED;
public class MainActivity extends AppCompatActivity implements Handler.Callback ,Fragment4.SendResult,Fragment6.SetScore{

    private ListView mListView;
    private FrameLayout mFrame;
    public BluetoothAdapter bleAdapter;
    public BleService bluetoothService;
    private List<User> mList = new ArrayList<>();
    private List<Fragment> mFragmentList = new ArrayList<>();
    private FragmentManager supportFragmentManager = getSupportFragmentManager();
    private MyListViewApader apader;
    private int channel = 1;
    private boolean measureR = false;
    private MyDataBaseMethod myDBMethod;
    MyDataBaseHelper dbhelper;
    private SQLiteDatabase db;
    public String tablename;

    private static final String TAG = "main";

    private String filename = "新建文件.txt";
    //接口
    SendValue sendValueTo1;
    SendValue sendValueTo2;
    SendValue sendValueTo3;

    Fragment1 fragment1;
    Fragment2 fragment2;
    Fragment3 fragment3;
    Fragment4 fragment4;
    Fragment5 fragment5;
    Fragment6 fragment6;
    PSQISubmitFragment psqiSubmitFragment;
    bleDataViewModel mviewModel;

    PowerManager.WakeLock wakeLock;
    Module module;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "tag");
        wakeLock.acquire();
        initView();
        initData();

        // 绑定服务
        bindBluetoothService();

        // 实例化蓝牙适配器
        initBluetoothAdapter();

        // 启动时检查权限
        isHaveLocalPermission();
        isHaveStoragePermission();

        mviewModel = ViewModelProviders.of(this).get(bleDataViewModel.class);
        //实例化一个MyDataBaseHelper对象
        dbhelper = new MyDataBaseHelper(MainActivity.this,"EEG_DataBase",null,1);
        myDBMethod = new MyDataBaseMethod(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册广播
        registerBroadcast();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 广播接收取消注册
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 解绑服务
        unbindService(mServiceConnection);
        wakeLock.release();
    }

    private void initView() {
        mListView = findViewById(R.id.mListview);
        mFrame    = findViewById(R.id.mFrame);
    }

    private void initData() {
        //左边listView集合添加数据，适配器适配
        listViewData();
        //添加fragment,复用fragment
        addFragment();
        //默认选中ListView第一条item
        replese(0);
        //ListView第一条item的Select为true
        mList.get(0).setSelect(true);

        //listView点击事件
        mListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            //切换fragment
            replese(position);
            //在bean类里写的一个标记 boolean类型的isSelect是关键，默认无状态， 并设置get set方法
            //集合里所有数据的Select设置为flase,position下标所对应的item的Select为true，刷新适配器。
            for (int i = 0; i < mList.size(); i++) {
                mList.get(i).setSelect(false);
            }
            mList.get(position).setSelect(true);
            //在刷新一下适配器就ok
            apader.notifyDataSetChanged();
            //Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
        });
    }


    /*********************************** 菜单项设置 ————蓝牙连接***************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 创建菜单
        getMenuInflater().inflate(R.menu.menu_connect, menu);
        return true;
    }

    private boolean isConnect = false;
    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String TAG = "BLE";
        int id = item.getItemId();
        switch (id) {
            case R.id.item_connect:
                if (isHaveLocalPermission()) {
                    if (isConnect) {
                        // 断开连接
                        write(DataPackage.getInstance().getControl(false));  // 发送停止命令

                        bluetoothService.disconnect();
                        Log.i(TAG, "onOptionsItemSelected: 断开连接！");

                    } else {
                        // 请求连接设备
                        Log.i(TAG, "onOptionsItemSelected: 请求连接！");
                        startSearch();
                        //showListDialog(this);
                    }
                }
                break;
            case R.id.mode_normal://设置工作模式：正常
                setRegister("Normal");
                break;
            case R.id.mode_check://设置工作模式：测试阻抗
                setRegister("Check");
                break;
            case R.id.mode_test://设置工作模式：测试模式
                setRegister("Test");
                break;
            case R.id.Rate250://设置采样率250
                setRegister("Rate250");
                break;
            case R.id.Rate500://设置采样率500
                setRegister("Rate500");
                break;
            case R.id.Rate1000://设置采样率1000
                setRegister("Rate1000");
                break;
            case R.id.Gain://设置增益
                setRegister("Gain");
                break;
            case R.id.serial_yes://设置串口输出数据
                setRegister("Serial_Yes");
                break;
            case R.id.serial_no://设置串口不输出数据
                setRegister("Serial_No");
                break;
            case R.id.ble_yes://设置蓝牙输出数据
                setRegister("Ble_Yes");
                break;
            case R.id.ble_no://设置蓝牙不输出数据
                setRegister("Ble_No");
                break;
            case R.id.SDCard://设置SD卡
                setRegister("SDCard");
                break;
            case R.id.Update://进入系统固件更新模式
                setRegister("Update");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 方法名：setState()
     * 功  能：根据连接状态，改变UI
     * 参  数：boolean state
     * 返回值：void
     */
    private void setState(boolean state) {
        Log.i(TAG, "setState: " + state);
        isConnect = state;
        TextView text = findViewById(R.id.item_connect);
        if (state) {
            // 已连接
            dialogList.dismiss();
            text.setText(R.string.connect_ed);
        } else {
            // 未连接
            text.setText(R.string.connect_not);

        }
    }


    /*********************************** 左侧导航栏ListView设置  **************************************/

    /**
     * 方法名： listViewData()
     * 功    能：左边listView集合添加数据
     * 参    数：无
     * 返回值：无
     */
    private void listViewData() {

        mList.add(new User("基本设置"));
        mList.add(new User("实时波形"));
        mList.add(new User("本地数据"));
        mList.add(new User("睡眠质量测评"));
        mList.add(new User("睡眠分期"));
        mList.add(new User("特征波检测"));

        //适配器适配
        apader = new MyListViewApader(mList, this);
        mListView.setAdapter(apader);
    }

    /**
     * 方法名：addFragment()
     * 功    能：添加fragment,复用fragment
     * 参    数：无
     * 返回值：无
     */
    private void addFragment() {

        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment4 = new Fragment4();
        fragment5 = new Fragment5();
        fragment6 = new Fragment6();
        psqiSubmitFragment = new PSQISubmitFragment();
        //将fragment添加入布局列表
        mFragmentList.add(fragment1);transaction.add(R.id.mFrame, fragment1,"Fragment1");
        mFragmentList.add(fragment2);transaction.add(R.id.mFrame, fragment2,"Fragment2");
        mFragmentList.add(fragment3);transaction.add(R.id.mFrame, fragment3,"Fragment3");
        mFragmentList.add(fragment6);transaction.add(R.id.mFrame, fragment6,"Fragment6");
        mFragmentList.add(fragment4);transaction.add(R.id.mFrame, fragment4,"Fragment4");
        mFragmentList.add(fragment5);transaction.add(R.id.mFrame, fragment5,"Fragment5");
        mFragmentList.add(psqiSubmitFragment);transaction.add(R.id.mFrame, psqiSubmitFragment,"Fragment7");
        //添加所有的fragment
        Log.i(TAG, "addFragment: list.size="+mFragmentList.size());
        transaction.commit();
    }

    /**
     * 方法名：replese()
     * 功    能：根据点击事件的下标切换fragment页面
     * 参    数：int position
     * 返回值：无
     */
    public void replese(int position) {
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        //所有的fragment隐藏，position对应的fragment显示，提交。
        for (int i = 0; i < mFragmentList.size(); i++) {
            Fragment fragment = mFragmentList.get(i);
            transaction.hide(fragment);
        }
        Log.i(TAG, "replese: mFragmentList.size="+mFragmentList.size());
        transaction.show(mFragmentList.get(position)).commit();
    }


    /**************************************** 权限获取 ***********************************************/


    /**
     * 方法名：isHaveStoragePermission()
     * 功  能：申请储存权限
     * 参  数：无
     * 返回值：boolean
     */
    private final int REQUEST_PERMISSION_STORAGE = 3;

    private boolean isHaveStoragePermission() {
        //Android 6.0 以上版本
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            int check = this.checkSelfPermission(permissions[0]);
            if (check == PackageManager.PERMISSION_DENIED) {
                // 请求权限，在回调方法onRequestPermissionsResult中写下一步
                requestPermissions(permissions, REQUEST_PERMISSION_STORAGE);
                return false;
            }
        }
        return true;
    }


    /**
     * 方法名：isHaveLocalPermission()
     * 功  能：申请位置授权
     * 参  数：无
     * 返回值：boolean
     */
    private final int REQUEST_PERMISSION_LOCATION = 2;

    private boolean isHaveLocalPermission() {
        //Android 6.0 以上版本
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
            int check = ContextCompat.checkSelfPermission(MainActivity.this, permissions[0]);
            // 没有权限就请求 GRANTED---授权  DENIED---拒绝
            if (check == PackageManager.PERMISSION_DENIED) {
                // 请求权限，在回调方法onRequestPermissionsResult中写下一步
                requestPermissions(permissions, REQUEST_PERMISSION_LOCATION);
                return false;
            }
        }
        return true;
    }


    /*************************************** 蓝牙操作服务 *********************************************/

    /**
     * 方法名：bindBluetoothService()
     * 功  能：蓝牙绑定服务
     * 参  数：无
     * 返回值：void
     */

    private void bindBluetoothService() {
        // 绑定服务
        Intent intent = new Intent(this, BleService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    /**
     * 方法名：initBluetoothAdapter()
     * 功  能：实例化蓝牙适配器
     * 参  数：无
     * 返回值：void
     **/

    private void initBluetoothAdapter() {
        if (bleAdapter == null) {
            bleAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bleAdapter == null) {
                ShowToast.show(this, R.string.not_support, Toast.LENGTH_SHORT);
            }
        }
    }

    /**
     * 方法名：registerBroadcast()
     * 功  能：注册广播
     * 参  数：无
     * 返回值：void
     */
    private void registerBroadcast() {
        // 注册全局广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        registerReceiver(broadcastReceiver, intentFilter);
    }


    /**
     * 方法名：startSearch()
     * 功  能：搜索设备、显示、连接
     * 参  数：无
     * 返回值：void
     */

    @SuppressLint("MissingPermission")
    private void startSearch() {
        if (!bleAdapter.isEnabled()) {
            openBluetooth();
            return;
        }
        // 根据Android版本不同不同选择不同的方法开始搜索

        bluetoothService.startSearch(mScanCallback);

        showListDialog(this);
        // 延时10s，对话框取消，同时停止搜索（在dialog的onDismiss中）
        handlerDelay.postDelayed(runnableDelay, 10000);
    }

    /**
     * 方法名：openBluetooth()
     * 功  能：打开蓝牙
     * 参  数：无
     * 返回值：void
     */
    private final int REQUEST_ENABLE_BT = 1;

    private void openBluetooth() {
        /* 以询问的方式，打开蓝牙 */
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }


    // 搜索到的设备列表
    private List<Map<String, String>> listMap = new ArrayList<>();
    private SimpleAdapter adapter;
    private ProgressBar progressBar;
    private AlertDialog dialogList;

    /**
     * 方法名：check ，在Device()
     * 功  能：检查是否为指定设备，如果是，就加入列表————脑电设备名称为HHXX_ECG_A
     * 参  数：BluetoothDevice device
     * 返回值:void
     */
    @SuppressLint("MissingPermission")
    private void checkDevice(BluetoothDevice device) {
        // 判断是否为指定设备
//      String ECG_BLE_NAME = "HHXX_ECG_A";
//      if (!ECG_BLE_NAME.equals(device.getName())) {
//          return;
//      }
        if (device.getName() == null) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("name", device.getName());
        map.put("address", device.getAddress());
        if (listMap.contains(map)) {
            return;
        }
        listMap.add(map);
        adapter.notifyDataSetChanged();
    }


    /**
     * 方法名：showListDialog()
     * 功  能：蓝牙设备对话框。显示搜索到的设备列表，点击可添加
     * 参  数：Context context
     * 返回值：void
     */
    private void showListDialog(Context context) {
        // 设置布局
        final View outerView = LayoutInflater.from(context).inflate(R.layout.dialog_listview, null);
        progressBar = outerView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        ListView listView = outerView.findViewById(R.id.listview);
        adapter = new SimpleAdapter(this, listMap,
                android.R.layout.simple_list_item_2,
                new String[]{"name", "address"},
                new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onItemClickListener);

        // 设置对话框
        AlertDialog.Builder d = new AlertDialog.Builder(context)
                .setView(outerView)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        stopSearch();
                        listMap.clear();
                    }
                });
        dialogList = d.create();
        dialogList.setCancelable(true);
        dialogList.setCanceledOnTouchOutside(true);
        dialogList.show();

        // 设置对话框的宽高
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        //获取对话框当前的参数值
        android.view.WindowManager.LayoutParams p = dialogList.getWindow().getAttributes();
        p.width = (int) (dm.widthPixels * 0.6);
        dialogList.getWindow().setAttributes(p);
        // 设置dialog自带背景为透明
        dialogList.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


    /**
     * 列表点击事件：OnItemClickListener
     * 功  能：对话框列表点击事件，点击连接设备
     */
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 只对连接失败的情况进行提示，连接成功Service会发送广播，在广播接收中提示成功
            String address = listMap.get(position).get("address");
            String name = listMap.get(position).get("name");
            Log.i("SD", "onItemClick: name = "+name);
            if (!bluetoothService.connect(address)) {
                ShowToast.show(MainActivity.this, "连接失败", Toast.LENGTH_SHORT);
            }
        }
    };


    /**
     * 方法名：stopSearch()
     * 功  能：停止搜索
     * 参  数：无
     * 返回值：void
     */
    @SuppressLint("MissingPermission")
    private void stopSearch() {
        // 移除handler设置的延时任务
        handlerDelay.removeCallbacks(runnableDelay);
        if (!bleAdapter.isEnabled()) {
            return;
        }
        // 根据版本不同，停止搜索
        bluetoothService.stopSearch(mScanCallback);
    }


    /**
     * Runnable接口
     * 功  能：用于延时执行操作
     */
    private Handler handlerDelay = new Handler();
    private Runnable runnableDelay = new Runnable() {
        @Override
        public void run() {
            // 停止搜索
            stopSearch();
            // 若搜索为空，则提示没有搜索到设备
            if (listMap.isEmpty()) {
                ShowToast.show(MainActivity.this, "没有搜索到可用设备", Toast.LENGTH_SHORT);
                dialogList.dismiss();
            }
            //获得对话框自定义布局，将ProgressBar隐藏
            progressBar.setVisibility(View.INVISIBLE);
        }
    };


    /**
     * 向蓝牙发送数据
     */
    private void write(byte[] data) {
        if (bluetoothService.write(data)) {
            ShowToast.show(this, "发送成功", Toast.LENGTH_SHORT);
            return;
        } else {
            ShowToast.show(this, "发送失败", Toast.LENGTH_SHORT);
        }

    }

    /**
     * 方法名：onBackPressed()
     * 功  能：想要设置双击退出app
     * 参  数：无
     * 返回值：void
     */
    long time = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - time < 2000) {
            super.onBackPressed();
        }
        time = System.currentTimeMillis();
    }


    /*******************************************  回调方法   **********************************************/


    /**
     * 回调方法 1：onActivityResult
     * 功  能：系统自动回调方法，处理其他activity通信的返回结果
     * 参  数：int requestCode, int resultCode, Intent data
     * 返回值：void
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 接收到了打开蓝牙的请求，但是用户拒绝打开蓝牙
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_CANCELED) {
            ShowToast.show(this, R.string.open_fail, Toast.LENGTH_SHORT);

            // 用户打开蓝牙
        } else if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            ShowToast.show(this, R.string.open_success, Toast.LENGTH_SHORT);
            // 搜索设备
            startSearch();
        }
    }

    /**
     * 回调方法 2：onRequestPermissionsResult()
     * 功  能：获取权限的回调，获取成功就开始蓝牙搜索
     * 参  数：int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults
     * 返回值：void
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 位置权限获取成功，开始搜索
            startSearch();

        } else if (requestCode == REQUEST_PERMISSION_LOCATION && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            // 获取位置权限失败，BLE不能使用 todo：使用对话框弹窗
            ShowToast.show(MainActivity.this, "请打开权限，否则无法启用蓝牙功能", Toast.LENGTH_SHORT);
            // 启动时检查权限
            isHaveLocalPermission();

        } else if (requestCode == REQUEST_PERMISSION_STORAGE && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            // 外部存储权限获取失败
            ShowToast.show(MainActivity.this, "请打开权限，否则无法保存数据", Toast.LENGTH_SHORT);
            // 启动时检查权限
            isHaveStoragePermission();
        }

    }


    /**
     * 回调方法 3：handleMessage
     * 功   能：fragment有message传来时回调,向设备发送是否接收蓝牙数据的命令
     */
    @Override
    public boolean handleMessage(@NonNull Message msg) {

        switch (msg.what) {
            case 0://开始采样
                //Fragment1中开始计时/开始检测。发送接收命令，设备开始采集数据，app接收数据
                //isReceive = true;
//                tablename = (String) msg.obj;
//                Log.i(TAG, "handleMessage/tablename: "+filename);
                //因为开机就开始采样所以必须先停止采样
                write(DataPackage.getInstance().controlSampling(true));
                break;
            case 1://停止采样
                //Fragment1中停时计时/停止检测。发送停止接收命令，设备采集数据，app停止接收数据
                Log.i(TAG, "handleMessage/msg.what=0: 收到！");
                // 停止采样
                write(DataPackage.getInstance().controlSampling(false));
               //isReceive = false;
                //stopFileService();
                break;
            case 2://开启蓝牙传输
                write(DataPackage.getInstance().controlBLE(true));
                break;
            case 3://关闭蓝牙传输
                write(DataPackage.getInstance().controlBLE(false));
                break;
            case 4://发送测阻抗指令,进行相关寄存器的设定
                measureR = true;
                setRegister("LOFF");
                handlerDelay.postDelayed(()->setRegister("CONFIG4_ON"),10);//每一指令都延迟10ms后执行，防止堵塞
                handlerDelay.postDelayed(()->setRegister("LOFF_SENSP_ON"),20);
                handlerDelay.postDelayed(()->setRegister("LOFF_SENSN"),30);
                handlerDelay.postDelayed(() -> {
                    if (bluetoothService.write(DataPackage.getInstance().getControl(true))) {
                        Log.i("measure", "handleMessage: 开始输出！");
                        Handler handler = new Handler();
                        handler.postDelayed(() -> setfrag1(),2000);
                    }
                },40);

//                else {
//                    while (bluetoothService.write(DataPackage.getInstance().getControl(true))){
//                        write(DataPackage.getInstance().getControl(true));
//                    }
//
//                }
//                write(DataPackage.getInstance().getControl(true));//发送开始输出指令
                //延迟2s后停止输出

                break;
            case 5://插入标记
                write(DataPackage.getInstance().setFlag());
        }
        return false;
    }

    public void setfrag1(){
        if(bluetoothService.write(DataPackage.getInstance().getControl(false))){
            Log.i("measure", "handleMessage: 停止输出！");
            fragment1.setResult();
            Toast.makeText(this,"测试完成！",Toast.LENGTH_SHORT).show();
            measureR = false;
        }

    }
    /**
     * @name:setRegister()
     * 函数作用：发送设定寄存器的指令
     */
    private void setRegister(String info){
        boolean bo = bluetoothService. write(DataPackage.getInstance().toREG(info));
        Log.i("measure", "setRegister("+info+"): "+bo);
    }



    /**
     * 回调方法 4：ScanCallback
     * 功   能：搜索到蓝牙设备时回调（API21以上）
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private ScanCallback mScanCallback = new ScanCallback() {
        /* 当有一个设备被发现的时候，回调此方法*/
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            // 检备是否为指定设备
            checkDevice(device);
        }
    };


    /**
     * 回调方法 5：ServiceConnection
     * 功   能：蓝牙设备绑定服务的回调。通过bindService回调，绑定状态改变时会回调此方法
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        /**Service绑定成功的回调，再次获得Service的实例*/
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetoothService = ((BleService.LocalBinder) service).getService();
        }

        /**Service断开*/
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG, "onServiceDisconnected: ~~Service断开");
            bluetoothService = null;
        }
    };


    public boolean isReceive; // 控制接收任务的死循环是否结束
    private int reTimer = 0;
    public int num = 0;

    ConcurrentLinkedQueue<byte[]> queueByte = new ConcurrentLinkedQueue<>();// 存放接收到的数据

    private byte sign = 0x00; //判断丢包的标志位
    private int lost = 0; //丢包计数标志位

    /**
     * 回调方法 6： broadcastReceiver
     * 功  能：广播收到数据时回调
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String action = intent.getAction();
            if (ACTION_GATT_CONNECTED.equals(action)) {
                reTimer = 0;
                // 设备已连接
//                setState(true);
//                ShowToast.show(MainActivity.this, "已连接", Toast.LENGTH_SHORT);

            } else if (ACTION_GATT_DISCONNECTED.equals(action)) {
                reTimer = 0;
                // 连接已断开
                setState(false);
                ShowToast.show(MainActivity.this, "连接已断开", Toast.LENGTH_SHORT);


            } else if (ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                reTimer = 0;
                // 设备已连接，服务已发现，可以正常通信了
                setState(true);
                ShowToast.show(MainActivity.this, "已连接", Toast.LENGTH_SHORT);
                bluetoothService.requestMtu(110);

            } else if (ACTION_DATA_AVAILABLE.equals(action)) {
                reTimer = 0;
                ++num;
                // 收到了蓝牙发来的数据包，准备接收数据
                //广播接收来自BleService的解析后数据
//                ArrayList<int[]> arr = (ArrayList<int[]>)intent.getSerializableExtra("ack");
//                ArrayList<Integer> arr = intent.getIntegerArrayListExtra("ack");
                int[] arr = intent.getIntArrayExtra("ack");
                if(measureR){
                    //传递给Fragment1
                    sendValueTo1.getArray(arr, isReceive);
                    Log.i("measure", "sendValueTo1 ");
                }else{
                        //传递给Fragment2
                        sendValueTo2.getArray(arr, isReceive);
                        Log.i("measure", "sendValueTo2 ");
                        //将数据存入数据库
                        tablename = fragment1.filename;
//                        myDBMethod.insertData(MainActivity.this,tablename,arr);

                }


            }
        }

    };

    /**
     * 继承Fragment4定义的接口
     * @param data C3或C4通道的数据
     * @param psgResult 分期结果
     */
    @Override
    public void getPSG(double[] data, ArrayList<Integer> psgResult) {
        Log.i(TAG, "getPSG: MainActivity 收到Fragment4的数据");
        //通过tag找到fragment5，TAG在MainActivity中的addFragment中添加。
        //注意这里必须通过此方法找到fragment，不然无法幅值
        Fragment5 fragment5 = (Fragment5)supportFragmentManager.findFragmentByTag("Fragment5");
        fragment5.getRawData(data,psgResult);

    }

    /**
     * 继承Fragment6的接口
     * @param info
     * @param score
     */
    @Override
    public void setScore(String[] info, int[] score) {
        replese(6);
        psqiSubmitFragment = (PSQISubmitFragment) supportFragmentManager.findFragmentByTag("Fragment7");
        psqiSubmitFragment.getScore(info,score);

    }

    /**
     * 重新加载布局
     */
    public void reLoadFragView(){
        /*现将该fragment从fragmentList移除*/
        if (mFragmentList.contains(fragment6)){
            mFragmentList.remove(fragment6);
        }
        /*从FragmentManager中移除*/
        getSupportFragmentManager().beginTransaction().remove(fragment6).commit();
        /*重新创建*/
        fragment6=new Fragment6();
        /*添加到fragmentList*/
        mFragmentList.add(3,fragment6);
        getSupportFragmentManager().beginTransaction().add(R.id.mFrame,fragment6,"Fragment6").commit();

    }
    /**
     * 接口名：SendValue
     * 功  能：定义接口——向fragment2中传递数据
     */
    public interface SendValue {
        void getArray(int[] value, boolean running);
    }

    /**
     * Activity父类继承：onAttachFragment
     * 功能：绑定接口
     *
     * @param fragment
     */

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment1);
        super.onAttachFragment(fragment2);
        super.onAttachFragment(fragment3);

        try {
            sendValueTo1 = (SendValue) fragment1;
            sendValueTo2 = (SendValue) fragment2;
            sendValueTo3 = (SendValue) fragment3;
        } catch (Exception e) {
        }

    }


}