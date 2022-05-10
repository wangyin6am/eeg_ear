package com.example.testversion.activity;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;


public class DataProcess {

    /*
    数据处理：
    1、去掉包头包尾
    2、拼接
    3、进制转化
    4、乘常数
    5、各通道数据提取
     */

    /*todo:数据保存 MATLAB对照，找出归一化或数据处理问题*/

    int num;

    /**
     * Note: version1 EEG设备
     * @parameter bytes: 为传入的数据包，共104个字节，包含包头4个字节，包尾4个字节，以及中间的4组数据，每组数据为8个通道的数据，一个数据由3个字节表示
     * @return ArrayList{ 丢包标志位，channel1，channel2，channel3，channel4，channel5，channel6，channel7，channel8}
     */
    public ArrayList<int[]> dataChange(byte[] bytes) {


        byte[] bytes_go = new byte[96];//去掉包头包尾的数组
         num = bytes[100];//第100元素为数据包的标志位，从1到255循环
         if(num<0){
             num = 256+num;
         }
        //将数据存入新数组（不包括包头包尾）-- 96个字节元素
        for (int i = 0; i < 96; i++) {
            bytes_go[i] = bytes[i + 4];//从第四字节开始
            //Log.i(TAG, "bytes_go: "+bytes_go[i]);
        }
        //打印

        int[] value = new int[32];
        System.out.println();
        int j = 0;
        int i = 0;
        while (j < 96) {
            // 1个数据
            byte[] byteOne = new byte[3];
            // 1个数据有3个字节，从bytes中陆续去取出3个字节存放到byteOne数组中
            System.arraycopy(bytes_go, j, byteOne, 0, 3);
            //Log.i(TAG, "byteone "+ i +": "+byteOne[0]+" "+byteOne[1]+" "+byteOne[2]);
            // 将三字节16进制转换成int型，由于byte转int需要是4字节，所以需要根据协议作补位操作
            value[i] = getIntData(byteOne);
            //Log.i(TAG, "value_"+ i +": "+value[i]);
            j = j + 3;
            i++;
        }

        //提取每个通道的数据
        ArrayList<int[]> value_arr = toChannel(value);

        //注意：此时的数据未乘常数ce
//        for(int d=0;d<value.length;d++){
//            //将double型数据保留4位小数
//            str_Data[d] = Float.parseFloat(String .format("%.4f",value[d]*ce*Math.pow(10,6)));
//        }

        return value_arr;
    }

    /**
     * Note: version2 EEG设备（SD卡存储）
     * @parameter bytes: 为传入的数据包，共26个字节，包含包头1个字节，包尾1个字节，以及中间8个通道的数据，每个通道的数据由3个字节组成,共24个字节
     * @return ArrayList{ 丢包标志位，channel1，channel2，channel3，channel4，channel5，channel6，channel7，channel8}
     */
        public int[] dataChange_SD(byte[] bytes){
            byte[] raw = new byte[96];
//            Log.i("Dataprocess","数据为："+byte2hex(bytes));

//            Log.i("Dataprocess", "dataChange_SD: raw的长度为"+raw.length+"，bytes的长度为"+bytes.length);
            //去掉包头包尾,剩24个字节
            for(int i=0;i<96;i++){
                raw[i] = bytes[i+1];
            }
//            Log.i("Dataprocess","删减后数据为："+byte2hex(raw));
            int j = 0;
            int i = 0;
            int[] value = new int[32];
            while (j < 96) {
                // 1个数据
                byte[] byteOne = new byte[3];
                // 1个数据有3个字节，从bytes中陆续去取出3个字节存放到byteOne数组中
                // 参数解释：原数组，原数组起始位置，目标数组，目标数组起始位置，一次copy的长度
                System.arraycopy(raw, j, byteOne, 0, 3);
                //Log.i(TAG, "byteone "+ i +": "+byteOne[0]+" "+byteOne[1]+" "+byteOne[2]);
                // 将三字节16进制转换成int型，由于byte转int需要是4字节，所以需要根据协议作补位操作
                value[i] =  getIntData(byteOne);
                //Log.i(TAG, "value_"+ i +": "+value[i]);
                j = j + 3;
                i++;
            }
//            Log.i("Dataprocess", "dataChange_SD--数据为："+toStringMethod(value));
        // TODO: 传输的数据格式
            return value;
        }

    /**
     * @ name:getIntData()
     * @ 功能：将3个字节数组转变成int型（byte转int须四个字节）
     * @ param: byte[] ,int
     * @ return: int
     */
    private int getIntData(byte[] bytes) {
        // 判断最高位，将byte[3]转为byte[4]
        byte b4 = 0;
        // 转为int
        int value;
        value = ((b4 << 24)
                | ((bytes[0] & 0xFF) << 16)
                | ((bytes[1] & 0xFF) << 8)
                | (bytes[2] & 0xFF));
        if ((bytes[0] & 0x80) > 0) {//为负数
            value |=0xFF000000;
        } else {
            value &=0x00FFFFFF;
        }

        return value;
    }

    /**
     * @name sendValue()
     * @功能 将每个通道的数据从转换后的数组中提取出来
     * @param value
     * @return ArrayList<int[]>
     */

    public  ArrayList<int[]> toChannel(int[] value){

        int[] ch0 = {num,num,num,num};
        int[] ch1 = new int[4]; int[] ch5 = new int[4];
        int[] ch2 = new int[4]; int[] ch6 = new int[4];
        int[] ch3 = new int[4]; int[] ch7 = new int[4];
        int[] ch4 = new int[4]; int[] ch8 = new int[4];



        //提取channel1的数据
        int z1 =0, k1=0;
        while (z1<32){
            ch1[k1] = value[z1];
            z1 = z1+8;
            //Log.i(TAG, "value_ch1: "+ ch1[k1]);
            k1++;
        }
        //提取channel2的数据
        int z2 =1, k2=0;
        while (z2<32){
            ch2[k2] = value[z2];
            //Log.i(TAG, "value_ch2: "+ch2[k2]);
            z2 = z2+8;
            k2++;
        }
        //提取channel3的数据
        int z3 =2, k3=0;
        while (z3<32){
            ch3[k3] = value[z3];
            z3 = z3+8;
            //Log.i(TAG, "value_ch3: "+ch3[k3]);
            k3++;
        }
        //提取channel4的数据
        int z4 =3, k4=0;
        while (z4<32){
            ch4[k4] = value[z4];
            z4 = z4+8;
            //Log.i(TAG, "value_ch4: "+ch4[k4]);
            k4++;
        }
        //提取channel5的数据
        int z5 =4, k5=0;
        while (z5<32){
            ch5[k5] = value[z5];
            z5 = z5+8;
            //Log.i(TAG, "get_value: "+ch5[k5]);
            k5++;
        }
        //提取channel6的数据
        int z6 =5, k6=0;
        while (z6<32){
            ch6[k6] = value[z6];
            z6 = z6+8;
            //Log.i(TAG, "value_ch6: "+ch6[k6]);
            k6++;
        }
        //提取channel7的数据
        int z7 =6, k7=0;
        while (z7<32){
            ch7[k7] = value[z7];
            z7 = z7+8;
            // Log.i(TAG, "value_ch7: "+ch7[k7]);
            k7++;
        }
        //提取channel8的数据
        int z8 =7, k8=0;
        while (z8<32){
            ch8[k8] = value[z8];
            z8 = z8+8;
            //  Log.i(TAG, "value_ch8: "+ch8[k8]);
            k8++;
        }

        ArrayList<int[]> value_int = new ArrayList<>();
        //将8个通道数据加入链表
        value_int.add(ch0);
        value_int.add(ch1);
        value_int.add(ch2);
        value_int.add(ch3);
        value_int.add(ch4);
        value_int.add(ch5);
        value_int.add(ch6);
        value_int.add(ch7);
        value_int.add(ch8);

        return value_int;
    }




    //******************************对存储数据做处理*******************************//
    public static LinkedList changeLinkedlist(LinkedList list) {


        //移除链表中前四个元素
        list.remove(0);
        list.remove(0);
        list.remove(0);
        list.remove(0);

        //在链表中4帧脑电数据后各添加一个回车
        list.add(24,"\r\n".getBytes());
        list.add(49,"\r\n".getBytes());
        list.add(74,"\r\n".getBytes());
        list.add(99,"\r\n".getBytes());


        //移除链表中后四个元素
        list.remove(100);
        list.remove(100);
        list.remove(100);
        list.remove(100);

        return list;
    }

    //******************************object类型转byte[]*******************************//
    public byte[] toByteArray (Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray ();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    /**
     * 求一个数的位数
     * @param num
     * @return
     */
    public static int getNumLenght(int num){
        num = num>0?num:-num;
        return String.valueOf(num).length();

    }
    /**
     * byte数组转16进制
     * @param bytes
     * @return
     */
    public String byte2hex(byte[] bytes){

        StringBuilder sb = new StringBuilder();

        String tmp = null;

        for(byte b: bytes){

            //将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制

            tmp = Integer.toHexString(0xFF & b);
            tmp = tmp+ " ";
            sb.append(tmp);

        }

        return sb.toString();

    }

    /**
     * 将int数组转成一个字符串
     * @param arr
     * @return
     */

    private String toStringMethod(int[] arr)
    {
        // 自定义一个字符缓冲区，
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        //遍历int数组，并将int数组中的元素转换成字符串储存到字符缓冲区中去
        for(int i=0;i<arr.length;i++)
        {
            if(i!=arr.length-1)
                sb.append(arr[i]+" ,");
            else
                sb.append(arr[i]+" ]");
        }
        return sb.toString();
    }

}
