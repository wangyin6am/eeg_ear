package com.example.testversion.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.testversion.ShowToast;
import com.example.testversion.activity.DataPackage;
import com.example.testversion.activity.DataProcess;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;

/**
 * Service
 * 为BLE的搜索、连接、通信提供服务
 * Tqq
 */
public class BleService extends Service {
    private final String TAG = "BleService";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private String mAddress;
    private BluetoothGattCharacteristic characteristic;
    private PowerManager.WakeLock wakeLock = null;

    /**
     * 广播动作，接收和发送时使用
     */
    //蓝牙已连接
    public final static String ACTION_GATT_CONNECTED = "action.gatt.connected";
    //蓝牙已断开
    public final static String ACTION_GATT_DISCONNECTED = "action.gatt.disconnected";
    //发现GATT服务
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "action.gatt.services.discovered";
    //收到蓝牙数据
    public final static String ACTION_DATA_AVAILABLE = "action.data.available";

    //服务标识
//    private final UUID SERVICE_UUID = UUID.fromString("f000fff0-0451-4000-b000-000000000000");
    //特征标识
//    private final UUID CHARA_UUID = UUID.fromString("f000fff1-0451-4000-b000-000000000000");

    /**
     * 内部类，本地的Binder
     */
    public class LocalBinder extends Binder {
        public com.example.testversion.service.BleService getService() {
            return com.example.testversion.service.BleService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * 回调方法，在bindService()使用时回调
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: BLE服务绑定!");
        initializeBleAdapter();
        return mBinder;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind: BLE服务解绑！");
        close();
        return super.onUnbind(intent);
    }

    /**
     * 关闭BluetoothGatt
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * 初始化adapter
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void initializeBleAdapter() {
        // For API level 18 and above, get a reference to BluetoothAdapter through  BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }

    }

    /************************** 搜索管理 ***************************************************/
    // 开始搜索设备
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startSearch(ScanCallback scanCallback) {
        BluetoothLeScanner mBleScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBleScanner.startScan(scanCallback);
    }
    // 结束搜索设备
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void stopSearch(ScanCallback scanCallback) {
        BluetoothLeScanner mBleScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBleScanner.stopScan(scanCallback);
    }

    /**
     * **********************************************************************************
     * 连接设备
     * return: true是连接失败，false是连接成功
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean connect(String address) {
        Log.i("SD", "connect: 开始连接！");
        if (mBluetoothAdapter == null){return false;}//如果否，表示设备不支持蓝牙
        if (address == null) {return false;}

        // 以前连接的设备,尝试重新连接。
        if (address.equals(mAddress) && mBluetoothGatt != null) {
            if (mBluetoothGatt.connect()) {
                // 连接完成
                PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,BleService.class.getName());
                return true;
            }
        }
        // connect
        mAddress = address;
        Log.i("SD", "connect: address = "+address);
        // 通过MAC-address得到远程设备
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.i("SD", "connect: device == null!");return false;}
        mBluetoothGatt = device.connectGatt(this, false, gattCallback);
        if(mBluetoothGatt==null){
            Log.i("SD", "connect: mBluetoothGatt==null!");
        }
        return mBluetoothGatt != null;
    }

    @Override
    public void onDestroy() {
        if(wakeLock != null){
            wakeLock.release();
            wakeLock = null;
        }
        super.onDestroy();
    }

    /**
     * 断开连接
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void
    disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        // 直接断开连接，但是需要先排除空指针异常
        mBluetoothGatt.disconnect();
    }

    /**
     * 回调函数
     */
    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {


        /** 连接状态改变时回调 */
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // 已连接
                Log.i(TAG, "onConnectionStateChange: ~~已连接");
                Log.i("SD", "onConnectionStateChange: 已连接");
                broadcastUpdate(ACTION_GATT_CONNECTED);
                // 查看服务
                mBluetoothGatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // 蓝牙已断开
                Log.i(TAG, "onConnectionStateChange: ~~已断开");
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
                Log.e(TAG, "onConnectionStateChange: 蓝牙连接断开！" );
                close();
            }
        }

        /** discoverServices()的回调，一旦有服务被发现，就会回调 */
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // 发现服务
            if (status == GATT_SUCCESS) {
                Log.i("SD", "onServicesDiscovered: ~~");
                characteristic = getClientCharacteristic();
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            }
        }

        final DataProcess dataProcess = new DataProcess();
        private int reTimer = 0;
        private byte sign = 0x00; //判断丢包的标志位
        private  int lost = 0; //丢包计数标志位

        /**
         * 收到数据！
         * BLE端有数据传来，数据由characteristic携带
         */

        int count = 0;
        int[] value_group = new int[128];
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // 取出数据
            //byte[] ackData = characteristic.getValue();
            @SuppressLint("SimpleDateFormat")
            //获取当前系统时间
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
            Date date = new Date();
            String time = df.format(date);
            //获取characteristic携带的数据
            byte[] ackData = characteristic.getValue();

            if (ackData == null) {
                Log.i(TAG, "no data!" );
                System.out.println("没有数据！");
                return;
            }
            else if (ackData[0] == (byte) 0x87){
                Log.i("Receive", "BleService: 收到数据！");
                if(ackData.length!=102){
                    Toast.makeText(getBaseContext(),"传输错误！",Toast.LENGTH_SHORT).show();
                    write(DataPackage.getControl(false));
                }else{
                    //处理数据
                    int[] data_int= dataProcess.dataChange_SD(ackData);
                    // 把数据广播给MainActivity
                    Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                    intent.putExtra("ack",data_int);
                    sendBroadcast(intent);
                }

//                count+=4;
//                Log.i(TAG, "onCharacteristicChanlaged: 蓝牙数据包计数："+count);
                //拼接数组，凑够16组数据传一次
//                System.arraycopy(data_int,0,value_group,count*32,32);
//                count++;
//                String str = toStringMethod(value_group);
//                Log.i(TAG, "onCharacteristicChanged--数据组为："+ str);
//                Log.i(TAG, "onCharacteristicChanged--长度为：" + value_group.length);
                //凑够10组数据广播一次
//                if(count==4){

//                    intent.putIntegerArrayListExtra("ack",arr_group);
//                    intent.putExtra("time",time);
                    //intent.putExtra("ack",ackData);

//                    count = 0;
//                }

                    /** 判断是否丢包,并统计丢包个数*/
//                    if (ackData[100] == (byte) sign) {
//                        if (sign != (byte) 0xFF) {
//                            sign++;
//                        } else {
//                            sign = (byte) 0x00;
//                        }
//                    } else {
//                        if (sign > ackData[100]) {
//                            lost = lost + (0xFF - sign + ackData[100]);
//                        } else if (ackData[100] > sign) {
//                            lost = lost + (ackData[100] - sign);
//                        }
//                        sign = ackData[100];
//                        sign++;
//                        if (lost>0){
//                            Log.e(TAG, "丢包" + lost );
//                        }
//                    }
            }

//            if (ackData[0] == 0x86) {
//                reTimer = 0;// 收到了ADS的状态数据Config
//                if (ackData[4] == 1) {//设备状态为OK
//                    write(DataPackage.getInstance().getControl(true));
//                    return;
//                } else if (ackData[4] == 0) {//设备状态为ng
//                    ShowToast.show(getApplicationContext(), "ADS设置不正确，设备无法工作", Toast.LENGTH_SHORT);
//                }
//
//            }

        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Log.i(TAG, "onMtuChanged: ~~" + mtu);
        }
    };


    /**
    * 发送广播
    */
    private void broadcastUpdate(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /**
     * 针对于每个characteristic ,允许接收数据
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        // 设置Characteristic的Notify属性为真
        boolean isNotify = mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        if (isNotify) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(NOTIFY_UUID);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }
    //GATT客户端特性配置
    /**
     * 查找UUID
     */
//    private final String CONFIG_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";
    private final UUID SERVICE_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private final UUID NOTIFY_UUID = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private final UUID WRITE_UUID = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    BluetoothGattCharacteristic gattCharacteristic_notify = null;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private BluetoothGattCharacteristic getClientCharacteristic() {
        Log.i("SD", "getClientCharacteristic: 开始查找UUID！");
        BluetoothGattService gattservice = null;
        BluetoothGattCharacteristic gattCharacteristic = null;
        //获取蓝牙设备的服务列表
        List<BluetoothGattService> services = mBluetoothGatt.getServices();
        for (BluetoothGattService service : services) {//遍历服务列表
            //获取这个Service的UUID
            UUID uuidService = service.getUuid();
            if(SERVICE_UUID.equals(uuidService)){ //找到指定UUID的Service
                gattservice = service;
                Log.i("SD", "getClientCharacteristic: 找到！ServiceUUID = "+uuidService);
                //找到指定Service后，找这个Service下指定UUID的Characteristic
                //获取Service下所有的Characteristic
                List<BluetoothGattCharacteristic> gattCharacteristics = gattservice.getCharacteristics();
                for (BluetoothGattCharacteristic chara : gattCharacteristics) {
                    //获取这个Characteristics的UUID
                    UUID uuidChara = chara.getUuid();
                    //找到指定UUID的Characteristics
                    if(NOTIFY_UUID.equals(uuidChara)){//找到指定UUID的“可通知”的characteristic
                        Log.i("SD", "getClientCharacteristic: 找到！NOTIFY_UUID = "+uuidChara);
                        //启用有读特性的Characteristic
                        gattCharacteristic_notify = chara;
                        boolean isNotify = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic_notify, true);
                        if(isNotify) {
                            List<BluetoothGattDescriptor> descriptorList = gattCharacteristic_notify.getDescriptors();
                            if(descriptorList != null && descriptorList.size() > 0) {
                                for(BluetoothGattDescriptor descriptor : descriptorList) {
                                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                    mBluetoothGatt.writeDescriptor(descriptor);
                                }
                            }
                        }
                    }else if(WRITE_UUID.equals(uuidChara)){//找到指定UUID的“可写”的characteristic
                        Log.i("SD", "getClientCharacteristic: 找到！WRITE_UUID = "+uuidChara);
                        gattCharacteristic = chara;
                        return gattCharacteristic;
                    }

//                int charaProp = gattCharacteristic.getProperties();

                    // 找到属性为：写和通知（无回答）的Characteristic UUID
//                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0
//                        && (charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
//                if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0
//                        && (charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {//获取属性为“可读可写”的Characteristic
//                    //设置Characteristic的Notify属性为真
//                    //启用有读特性的Characteristic
//                    boolean isNotify = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
//                    if (isNotify) {//启用成功
//                        //获取特性UUID属性为“可读可写”,返回此UUID
////                        BluetoothGattDescriptor descriptor= gattCharacteristic.getDescriptor(UUID.fromString(CONFIG_DESCRIPTOR_UUID));
//                        BluetoothGattDescriptor descriptor_write = gattCharacteristic.getDescriptor(UUID.fromString(WRITE_UUID));
//                        BluetoothGattDescriptor descriptor_notify = gattCharacteristic.getDescriptor(UUID.fromString(NOTIFY_UUID));
//
//                        //到修改此描述符的本地存储缓存值
//                        descriptor_write.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                        //将修改的值发送给远程设备
//                        mBluetoothGatt.writeDescriptor(descriptor_write);
//                    }
//                    return gattCharacteristic;
//                }
                }
            }
            // 是否找到了BluetoothGattService
            if (service == null) {
                ShowToast.show(getBaseContext(), "不支持的设备", Toast.LENGTH_SHORT);
                return null;
            }

        }
        return null;
    }

    /**********************************************************************
     * 改变BLE默认的单次发包、收包的最大长度,用于android 5.0及以上版本
     * 必须调用，因为Android默认包大小为25个字节，脑电的有104个字节
     * 一般设置为121即可
     */    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean requestMtu(int mtu) {
        if (mBluetoothGatt != null) {
            return mBluetoothGatt.requestMtu(mtu);
        }
        return false;
    }

    /*********************************************************************
     * 发送数据
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean write(byte[] data) {
        Log.i(TAG, "write: 收到！");
        if (mBluetoothGatt == null ) {
            Log.i(TAG, "mBluetoothGatt == null");
            return false;
        }else if(characteristic == null ){
            Log.i(TAG, "characteristic == null");
            return false;
        }
        //修改此characteristic的本地缓存值
        characteristic.setValue(data);
        //将值发送给远程设备
        Boolean success =mBluetoothGatt.writeCharacteristic(characteristic);
        return success;
    }


}
