package com.example.testversion.fragment5;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.testversion.jdsp.filter.Butterworth;
import com.example.testversion.jdsp.transform.DiscreteFourier;
import com.example.testversion.jdsp.transform.Hilbert;

import java.util.LinkedList;
import java.util.List;

public class Spindle {

    String TAG = "Spindle";
    float n2time;
    double[] dataFilterd ;//存储
    LinkedList<LinkedList<Double>> Spindles = new LinkedList<>();//存储每个纺锤波的幅值
    LinkedList<int[]> Index = new LinkedList<>();//记录每个纺锤波的开始和结束位置
    double Threshold;

    /**
     * 检测纺锤波
     * @param N2Data N2期的所有数据
     */
    public void spindleDetection(double[] N2Data){
        //计算N2期总时间
        n2time = N2Data.length/125/60;
        // 1、原始信号滤波10-16Hz
        Butterworth butter1 = new Butterworth(N2Data,125);
        dataFilterd = butter1.bandPassFilter(4,10,16);
        double[] data_filterd2 = dataFilterd.clone();
        // 3、获取希尔伯特变换后信号
//        FourierTransform.Direction mDirection = FourierTransform.Direction.Forward;
//        double[] data_hilbert = HilbertTransform.FHT(data_filterd2,mDirection);
        Hilbert hilbert = new Hilbert(data_filterd2);
        hilbert.transform();
        hilbert.getOutput();
        double[] envelope = hilbert.getAmplitudeEnvelope();
        // 4、求包络线
//        double[] envelope = new double[data_filterd2.length];
//        for(int i=0;i<data_filterd2.length;i++){
//            envelope[i] = Math.hypot(data_filterd2[i],data_hilbert[i]);
//        }
        // 5、求阈值=包络平均值+标准差
        double sum=0,mean=0;
        double powSum=0,sd=0;
        for(double d:envelope){
            sum+=d;
        }
        mean = sum/envelope.length;
        for(double b:envelope){
            powSum += Math.pow(b-mean,2);
        }
        sd = Math.sqrt(powSum/(envelope.length-1));
        Threshold = mean+sd;
        Log.i(TAG, "spindleDetection: threshold = "+Threshold);
        int pointCount=0;//用来记录阈值之上持续的点的个数
        int spindleCount=0;//用来记录检测到的纺锤波的索引
        // 6、检测纺锤波:包络线在阈值上持续0.5到2s则认为是纺锤波
//        LinkedList<Double> spindle = new LinkedList<>();//存储单个纺锤波的幅值
        //先添加一个空链表
        LinkedList<Double> spindle = new LinkedList<>();
        Spindles.add(spindle);
        int[] index = new int[2];//记录单个纺锤波的开始结束位置
        for(int i=0;i<envelope.length;i++){
            if(envelope[i]>=Threshold){
                Spindles.getLast().add(envelope[i]);
                pointCount++;
                if(envelope[i-1]<Threshold&&envelope[i+1]>Threshold){
                    index[0] = i;
                }
            }else {
                //持续时间大于0.5s小于2s
                if((pointCount>62)&&(pointCount<250)){
                    index[1] = i-1;
//                    Log.i(TAG, "spindleDetection: 检测到一个！pointCount="+pointCount+", start="+index[0]+", end="+index[1]);
                    //给最外层链表添加一个元素
                    LinkedList<Double> p = new LinkedList<>();
                    Spindles.add(p);
                    int[] value = new int[2];
                    Index.add(value);
                    Index.getLast()[0]=index[0];
                    Index.getLast()[1]=index[1];
                    spindleCount++;
                    pointCount = 0;
                }else {//如果不是
                    Spindles.pollLast();
                    LinkedList<Double> p = new LinkedList<>();
                    Spindles.add(p);
                    pointCount = 0;
                }

            }
        }
        Log.i(TAG, "spindleDetection: 检测完成！纺锤波个数为："+Spindles.size());
    }

    /**
     * 获取所有纺锤波的起始和结束位置
     * @return LinkedList<int[]>
     */
    public LinkedList<int[]> getIndex(){
        return Index;
    }

    /**
     * 返回所有纺锤波的每个点的电压值
     * @return LinkedList<LinkedList<Double>>
     */
    public LinkedList<LinkedList<Double>> getSpindles(){
        Log.i(TAG, "getIndex: Spindles.size="+Spindles.size());
        return Spindles;
    }

    /**
     * 获取经过10-16Hz滤波之后的N2期的数据
     * @return double[]
     */
    public double[] getDataFilterd(){
        Log.i(TAG, "getDataFilterd: dataFilterd.size="+dataFilterd.length);
        return dataFilterd;
    }

    /**
     * 获取检测纺锤波的自适应阈值
     * @return double
     */
    public double getThreshold(){
        return Threshold;
    }

    /**
     * 计算纺锤波的密度
     * 单位：个/每分钟
     */
    public float calDensity(){
        float density = n2time/Index.size() ;
        return density;
    }

    /**
     * 计算每个纺锤波的持续时间
     *  单位：s（秒）
     */
    float[] Duration;
    public float[] calDuration(){
        Duration = new float[Spindles.size()];
        float duration=0;
        for(int i=0;i<Index.size();i++){
            duration=(Index.get(i)[1]-Index.get(i)[0]+1)/125f;
            Duration[i]=duration;
        }
        return Duration;
    }

    /**
     * 计算所有纺锤波的平均持续时间
     * @return avg[0]为平均值，avg[1]为标准差
     */
    public float[] calAvgDuration(){
        float[] avgDuration = new float[2];
        calDuration();
        float sum = 0;
        for(float f:Duration){
            sum+=f;
        }
        avgDuration[0] = sum/Duration.length;
        avgDuration[1] = calStandardDeviation(avgDuration[0],Duration);
        return avgDuration;
    }
    /**
     * 计算每个纺锤波的能量
     * 能量 = 所有幅值的平方和
     * 单位：微伏
     * @return 所有纺锤波的能量
     */
    float[] Energy;
    public float[] calEnergy(){
        Energy = new float[Spindles.size()];
        for(int i=0;i<Spindles.size();i++){
            List<Double> spindle = Spindles.get(i);
            int length = spindle.size();
            double energy=0;
            for(int j=0;j<length;j++){
                energy+=Math.pow(spindle.get(j),2);
            }
            Energy[i]=(float) energy;
        }
        return Energy;
    }

    /**
     * 计算所有纺锤波能量的平均值
     * @return AvgEnergy[0]为平均值，AvgEnergy[1]为标准差
     */
    float[] AvgEnergy = new float[2];
    public float[] calAvgEnergy(){
        calEnergy();
        float sum=0;
        for(float v:Energy){
            sum += v;
        }
        AvgEnergy[0] = sum/Energy.length/1000;
        AvgEnergy[1] = calStandardDeviation(AvgEnergy[0],Energy)/1000;
        return AvgEnergy;
    }

    /**
     * 计算纺锤波平均功率
     * 功率 = 能量/采样率
     * @param freq 采样率
     * @return 平均功率 AvgPower[0]为平均值，AvgPower[1]为标准差
     */
    public float[] calAvgPower(int freq){
        float[] AvgPower = new float[2];
        AvgPower[0] = AvgEnergy[0]/freq;
        AvgPower[1] = AvgEnergy[1]/freq;
        return AvgPower;
    }
    /**
     * 计算每个纺锤波的最大幅度
     * 单位:微伏
     */
    float[] Amplitude;
    public float[] calAmplitude(){
        Amplitude = new float[Spindles.size()];
        for(int i=0;i<Spindles.size();i++){
            Amplitude[i]=getMAX(Spindles.get(i));
        }
        return Amplitude;
    }

    /**
     * 计算所有纺锤波的平均幅值
     * @return AvgAmplitude[0]为平均值，AvgAmplitude[1]为标准差
     */
    public float[] calAvgAmplitude(){
        float[] AvgAmplitude = new float[2];
        calAmplitude();
        float sum = 0;
        for(float f:Amplitude){
            sum+=f;
        }
        AvgAmplitude[0] = sum/Amplitude.length;
        AvgAmplitude[1] = calStandardDeviation(AvgAmplitude[0],Amplitude);
        return AvgAmplitude;
    }



    /**
     * 计算标准差
     * @param value
     * @return
     */
    public float calStandardDeviation(float avg,float[] value){
        float sum = 0;
        for(float v:value){
            sum += Math.pow(v-avg,2);
        }

        float SD = (float) Math.sqrt(sum/(value.length-1));
        return SD;
    }
    /**
     * 计算每个纺锤波的频率
     * ！不太正确，后期需要更改 ！
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public double[] calFrequency(){
        double[] sp_freq = new double[Spindles.size()];
        for(int i=0;i<Spindles.size();i++){
            double[] spindle = new double[Spindles.get(i).size()];
            for(int j=0;j<Spindles.get(i).size();j++){
                spindle[j] = Spindles.get(i).get(j);
                DiscreteFourier dFourier = new DiscreteFourier(spindle);
                dFourier.transform();
                double[] freq = dFourier.getMagnitude(true);
                sp_freq[i]=getMAX(freq);
            }
        }
        return sp_freq;
    }

    public float getMAX(List<Double> result){
        if(result.size()==0){return 0;}
        else {
            double max = result.get(0);
            int stage = 0;
            for(int i=1;i<result.size();i++){
                if(max<result.get(i)){
                    max = result.get(i);
                    stage = i;
                }
            }
            return (float) max;
        }
    }
    public float getMAX(double[] result){
        if(result.length==0){return 0;}
        else {
            double max = result[0];
            int stage = 0;
            for(int i=1;i<result.length;i++){
                if(max<result[i]){
                    max = result[i];
                    stage = i;
                }
            }
            return (float) max;
        }
    }
}
