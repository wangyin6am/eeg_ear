package com.example.testversion.activity;

import java.util.ArrayList;

/**
 * @author: Tian
 * @Date; 2019/9/25 22:29
 */
public class ByteToString {

    /*********************************************************************************
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     * @author 极光舞者，https://blog.csdn.net/qq_34763699/article/details/78650272
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex).append(" ");
        }
        return sb.toString();
    }

    public static String bytesToHex(byte[] bytes, int Len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Len; i++) {
            byte aByte = bytes[i];
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex).append(" ");
        }
        return sb.toString();
    }

    /**
     * byte[]转化为无符号整型的字符串，便于文件存储
     */
    public static String bytesToInt(byte[] bytes){
        if (bytes==null){
            return "";
        }else{
            StringBuilder sb1 = new StringBuilder();
            ArrayList<Byte> arrayList1= new ArrayList<>();
            int i = 0;
            for(i=0;i<bytes.length;i++){
                arrayList1.add(bytes[i]);
            }
//            arrayList1.remove(103);
//            arrayList1.remove(102);
//            arrayList1.remove(101);
            for (byte aByte : arrayList1) {
                String str = String.valueOf(aByte & 0xFF);
                sb1.append(str).append(" ");
            }
            return sb1.toString();
        }
    }



}
