package com.example.testversion.activity;

import android.util.Log;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class FFT {

    /**
     * 1-低通滤波
     * @param originalPoints 原始数据
     * @param Fs 采样频率
     * @param lowpass 低通的频率条件
     * @return 滤波后的数据
     */
    public static ArrayList<Float> LowPassFFT(ArrayList<Float> originalPoints, double Fs, int lowpass){
        int dataLen = originalPoints.size();
//        Log.i("FFT", "LowPassFFT/dataLen: "+originalPoints.size());
        double[] signal = new double[dataLen];
        for(int i = 0; i < originalPoints.size(); ++i){
            signal[i] = originalPoints.get(i);
//            Log.i("FFT", "LowPassFFT/signal: "+originalPoints.get(i));
        }
        double frequencyResolution = Fs / dataLen;//频率分辨率

        //进行傅里叶变换,使用的是commons-math包里面的快速傅里叶算法
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] complexArr = fft.transform(signal, TransformType.FORWARD);//正向傅里叶变换
        Log.i("FFT", "LowPassFFT: done！");
        //进行滤波,注意条件
        if(lowpass < Fs / 2.0){
            for(int i = 0; i < complexArr.length; i++){
                //对称
                if(i * frequencyResolution > lowpass && i * frequencyResolution < (Fs - lowpass)){
                    complexArr[i] = new Complex(0, 0);
                }
            }
        }
        //反傅里叶变换
        Complex[] timeDomainArr = fft.transform(complexArr, TransformType.INVERSE);
        ArrayList<Float> points = new ArrayList<>();
        for(int i = 0; i < timeDomainArr.length; i++){
            //只用获取实部，不用获取虚部(虚部理论上应该是0),实部的数据就是时域曲线
            float point = (float) timeDomainArr[i].getReal();
            points.add(point);
        }
        return points;
    }


    /**
     * 2-低通滤波
     * @param originalPoints 原始数据
     * @param Fs 采样频率
     * @param lowpass 低通的频率条件
     * @return 滤波后的数据
     */
    public static Queue<Float> LowPassFFT(Queue<Float> originalPoints, double Fs, int lowpass){
        int dataLen = originalPoints.size();//实际数据长度
//        Log.i("FFT", "LowPassFFT/dataLen: "+originalPoints.size());
        //建立数组，长度为最接近大于信号长度的第一个2的幂
        double[] signal = new double[1 << ((int) (Math.log(dataLen) / Math.log(2)) + 1)];
        //先把真实数据放入数组
        for(int i = 0; i < dataLen; ++i){
            signal[i] = originalPoints.poll();
//            Log.i("FFT", "LowPassFFT/signal: "+originalPoints.get(i));
        }
        //其余位置则插0
        for (int i = dataLen; i < signal.length; i++) {
            signal[i] = 0;
        }

        double frequencyResolution = Fs / dataLen;//频率分辨率

        //进行傅里叶变换,使用的是commons-math包里面的快速傅里叶算法
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        //注意：傅里叶变换必须满足数据源的长度为2的幂
        Complex[] complexArr = fft.transform(signal, TransformType.FORWARD);//正向傅里叶变换
        Log.i("FFT", "LowPassFFT: done！");
        //进行滤波,注意条件
        if(lowpass < Fs / 2.0){
            for(int i = 0; i < complexArr.length; i++){
                //对称
                if(i * frequencyResolution > lowpass && i * frequencyResolution < (Fs - lowpass)){
                    complexArr[i] = new Complex(0, 0);
                }
            }
        }

        //反傅里叶变换
        Complex[] timeDomainArr = fft.transform(complexArr, TransformType.INVERSE);
        Queue<Float> points = new LinkedList<>();
        for(int i = 0; i < timeDomainArr.length; i++){
            //只用获取实部，不用获取虚部(虚部理论上应该是0),实部的数据就是时域曲线
            float point = (float) timeDomainArr[i].getReal();
            points.add(point);
        }
        return points;
    }
    /**
     * 1-低通滤波
     * @param originalPoints 原始数据
     * @param Fs 采样频率
     * @param lowpass 低通的频率条件
     * @return 滤波后的数据
     */
    public static double[] LowPassFFT(double[] originalPoints, double Fs, int lowpass){
        int dataLen = originalPoints.length;
//        Log.i("FFT", "LowPassFFT/dataLen: "+originalPoints.size());
        double[] signal = new double[dataLen];
        for(int i = 0; i < originalPoints.length; ++i){
            signal[i] = originalPoints[i];
//            Log.i("FFT", "LowPassFFT/signal: "+originalPoints.get(i));
        }
        double frequencyResolution = Fs / dataLen;//频率分辨率

        //进行傅里叶变换,使用的是commons-math包里面的快速傅里叶算法
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] complexArr = fft.transform(signal, TransformType.FORWARD);//正向傅里叶变换
        Log.i("FFT", "LowPassFFT: done！");
        //进行滤波,注意条件
        if(lowpass < Fs / 2.0){
            for(int i = 0; i < complexArr.length; i++){
                //对称
                if(i * frequencyResolution > lowpass && i * frequencyResolution < (Fs - lowpass)){
                    complexArr[i] = new Complex(0, 0);
                }
            }
        }

        //反傅里叶变换
        Complex[] timeDomainArr = fft.transform(complexArr, TransformType.INVERSE);
        double[] points = new double[timeDomainArr.length];
        for(int i = 0; i < timeDomainArr.length; i++){
            //只用获取实部，不用获取虚部(虚部理论上应该是0),实部的数据就是时域曲线
            float point = (float) timeDomainArr[i].getReal();
            points[i]= point;
        }
        return points;
    }


    /**
     * 带通滤波
     * @param originalPoints 原始数据
     * @param Fs 采样频率
     * @param lowpass 低通的频率条件
     * @return 滤波后的数据
     */
    public static double[] BandPassFFT(double[] originalPoints, double Fs,int hightpass ,int lowpass ){
        int dataLen = originalPoints.length;//实际数据长度
//        Log.i("FFT", "LowPassFFT/dataLen: "+originalPoints.size());
        //建立数组，长度为最接近大于信号长度的第一个2的幂
        double[] signal = new double[1 << ((int) (Math.log(dataLen) / Math.log(2)) + 1)];
        //先把真实数据放入数组
        for(int i = 0; i < dataLen; ++i){
            signal[i] = originalPoints[i];
//            Log.i("FFT", "LowPassFFT/signal: "+originalPoints.get(i));
        }
        //其余位置则插0
        for (int i = dataLen; i < signal.length; i++) {
            signal[i] = 0;
        }

        double frequencyResolution = Fs / dataLen;//频率分辨率

        //进行傅里叶变换,使用的是commons-math包里面的快速傅里叶算法
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        //注意：傅里叶变换必须满足数据源的长度为2的幂
        Complex[] complexArr = fft.transform(signal, TransformType.FORWARD);//正向傅里叶变换
        Log.i("FFT", "LowPassFFT: done！");
        //进行滤波,注意条件
        if(lowpass < Fs / 2.0){
            for(int i = 0; i < complexArr.length; i++){
                //低通滤波
                if(i * frequencyResolution > lowpass && i * frequencyResolution < (Fs - lowpass)){
                    complexArr[i] = new Complex(0, 0);
                }
                //高通滤波
                if(i * frequencyResolution < hightpass && i * frequencyResolution > (Fs - hightpass)){
                    complexArr[i] = new Complex(0, 0);
                }
            }
        }

        //反傅里叶变换
        Complex[] timeDomainArr = fft.transform(complexArr, TransformType.INVERSE);
        double[] points = new double[timeDomainArr.length];
        for(int i = 0; i < timeDomainArr.length; i++){
            //只用获取实部，不用获取虚部(虚部理论上应该是0),实部的数据就是时域曲线
            double point = timeDomainArr[i].getReal();
            points[i] = point;
//            points.add(point);
        }
        return points;
    }

}
