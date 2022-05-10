package com.example.testversion.activity;

import android.util.Log;

public class DataPackage {

    private static DataPackage dataPackage;

    public static DataPackage getInstance() {
        if (dataPackage == null) {
            dataPackage = new DataPackage();
        }
        return dataPackage;
    }

    public byte[] getAdsConfirm() {
        byte[] data = new byte[5];
        data[0] = (byte) 0xa6;
        data[1] = 0x42;
        data[2] = 0x01;
        data[3] = 0;
        data[4] = 0;
        return data;
    }

    public byte[] controlSampling(boolean isStart) {
        byte[] data = new byte[5];
        data[0] = (byte) 0xa5;
        data[1] = 0x7e;
        data[2] = 0x01;
        data[3] = 0x00;
        if (isStart) {
            data[4] = 1;//开始采样
            Log.i("SD", "controlSampling: 开始采样！");
        } else {
            data[4] = 0;//停止采样
            Log.i("SD", "controlSampling: 停止采样！");
        }
        return data;
    }
    public byte[] controlBLE(boolean isStart) {
        byte[] data = new byte[5];
        data[0] = (byte) 0xa5;
        data[1] = 0x6d;
        data[2] = 0x01;
        data[3] = 0x00;
        if (isStart) {
            data[4] = 1;//开启蓝牙通道
            Log.i("SD", "controlBLE: 开启蓝牙通道！");
        } else {
            data[4] = 0;//关闭蓝牙通道
            Log.i("SD", "controlBLE: 关闭蓝牙通道！");
        }
        return data;
    }

    public static byte[] getControl(boolean isOpen) {
        byte[] data = new byte[5];
        data[0] = (byte) 0xa5;
        data[1] = 0x44;
        data[2] = 0x01;
        data[3] = 0x00;

        if (isOpen) {
            data[4] = 1;//开始采样并输出
        } else {
            data[4] = 0;//停止输出
        }
        return data;
    }
    public  byte[] setFlag() {
        byte[] data = new byte[8];
        data[0] = (byte) 0xa5;
        data[1] = (byte)0xE4;
        data[2] = 0x04;
        data[3] = 0x00;
        data[4] = 0x46;
        data[5] = 0x4c;
        data[6] = 0x41;
        data[7] = 0x47;

        return data;
    }

    public byte[] toREG(String register) {

        byte[] command = new byte[6];
        switch (register) {
//            case "LOFF"://用来设定测阻抗的寄存器
//                command =new byte[]{(byte) 0XA5, 0x45, 0x02, 0x00, 0x04, 0x0A};
//                break;
//            case "CONFIG4_ON":
//                command =new byte[]{(byte) 0XA5, 0x45, 0x02, 0x00, 0x17, 0x02};
//
//                break;
//            case "CONFIG_OFF":
//                command =new byte[]{(byte) 0XA5, 0x45, 0x02, 0x00, 0x17, 0x00};
//                break;
//            case "LOFF_SENSP_ON"://用来侦测正端电极脱落
//                command =new byte[]{(byte) 0XA5, 0x45, 0x02, 0x00, 0x0F, (byte)0xFF};
//                break;
//            case "LOFF_SENSP_OFF"://用来侦测正端电极脱落
//                command =new byte[]{(byte) 0XA5, 0x45, 0x02, 0x00, 0x0F, (byte)0x00};
//                break;
//            case "LOFF_SENSN"://用来侦测负端电极脱落，这里设为0
//                command =new byte[]{(byte) 0XA5, 0x45, 0x02, 0x00, 0x10, 0x00};
//                break;
            case "Normal":
                command = new byte[]{(byte) 0xA5, 0x71, 0x01, 0x00, 0x00};
                break;
            case "Check":
                command = new byte[]{(byte) 0xA5, 0x71, 0x01, 0x00, 0x01};
                break;
            case "Test":
                command = new byte[]{(byte) 0xA5, 0x71, 0x01, 0x00, 0x02};
                break;
            case "Rate250":
                command = new byte[]{(byte) 0xA5, 0x72, 0x01, 0x00, 0x06};
                break;
            case "Rate500":
                command = new byte[]{(byte) 0xA5, 0x72, 0x01, 0x00, 0x05};
                break;
            case "Rate1000":
                command = new byte[]{(byte) 0xA5, 0x72, 0x01, 0x00, 0x04};
                break;
            case "Gain":
                command = new byte[]{(byte) 0xA5, 0x73, 0x01, 0x00, 0x00};
                break;
            case "Serial_Yes":
                command = new byte[]{(byte) 0xA5, 0x65, 0x01, 0x00, 0x01};
                break;
            case "Serial_No":
                command = new byte[]{(byte) 0xA5, 0x65, 0x01, 0x00, 0x00};
                break;
            case "Ble_Yes":
                command = new byte[]{(byte) 0xA5, 0x6D, 0x01, 0x00, 0x01};
                break;
            case "Ble_No":
                command = new byte[]{(byte) 0xA5, 0x6D, 0x01, 0x00, 0x00};
                break;
            case "SDCard":
                command = new byte[]{(byte) 0xA5, (byte) 0xE4, 0x20, 0x00, 0x00};
                break;
            case "Update":
                command = new byte[]{(byte) 0xA5, (byte) 0xE1, 0x01, 0x00, 0x00};
                break;
            default:
                break;
        }
        return command;
    }
    public byte[] setMode(int i,int t){
        byte[] data = new byte[6];
        data[0] = (byte) 0xA5;
        data[1] = 0x45;
        data[2] = 0x02;
        data[3] = 0x00;
        data[5] = 0x00;//0x00代表正常波，0x05代表方波
        switch (i){//data[4]是对那个通道进行设定
            case 1:
                data[4] = 0x05;
                break;
            case 2:
                data[4] = 0x06;
                break;
            case 3:
                data[4] = 0x07;
                break;
            case 4:
                data[4] = 0x08;
                break;
            case 5:
                data[4] = 0x09;
                break;
            case 6:
                data[4] = 0x0A;
                break;
            case 7:
                data[4] = 0x0B;
                break;
            case 8:
                data[4] = 0x0C;
                break;
        }
        switch (t){
            case 0:
                data[5] = 0x00;//0x00代表正常波，0x05代表方波
                break;
            case 1:
                data[5] = 0x05;//0x00代表正常波，0x05代表方波
                break;
        }
        return data;
    }

}
