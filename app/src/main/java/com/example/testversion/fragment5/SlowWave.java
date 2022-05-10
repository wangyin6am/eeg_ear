package com.example.testversion.fragment5;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.testversion.jdsp.filter.Butterworth;
import com.example.testversion.jdsp.transform.FastFourier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SlowWave {
    float n3time;
    double[] dataFilterd;
    LinkedList<LinkedList<Double>> SlowWaves= new LinkedList<>();//记录所有慢波的幅值
    LinkedList<int[]> Index = new LinkedList<>();//记录每个慢波的开始和结束位置
    String TAG = "SlowWave";

    /**
     * 慢波检测
     * 需满足三个条件
     * 1、相邻两个正到负零点在0.25-1s内
     * 2、1条件内的波形负峰峰值<40微伏
     * 3、1条件内的正峰和负峰幅值相差>75微伏
     * @param N3Data
     */
    public void slowWaveDetection(double[] N3Data){
        //计算N2期总时间
        n3time = N3Data.length/125/60;
        // 1、原始信号滤波0.5-4Hz
        Butterworth butter = new Butterworth(N3Data,125);
        dataFilterd = butter.bandPassFilter(4,0.5,4);
        // 复制一份数据，不对数据直接操作
        double[] datafilted = dataFilterd.clone();
        int[] index_con1 = new int[datafilted.length];//用来存储所有的正到负零点索引
        int k=0;
        // 2、将所有正到负零点的索引记录下来
        for(int i=1;i<datafilted.length-1;i++){
            if(datafilted[i-1]>0 && datafilted[i]<=0 && datafilted[i+1]<0){ //找到正到负的过零点
                index_con1[k] = i;
               k++;
            }
        }
        // 3、计算是否符合持续时间在0.25-1s之内
        int start,end;
        for(int i=0;i<index_con1.length-1;i++){
            if((index_con1[i+1]-index_con1[i])>=31 && (index_con1[i+1]-index_con1[i])<=125){//符合持续时间在0.25-1s之内
                start = index_con1[i];
                end = index_con1[i+1];
                LinkedList<Double> slowwave = new LinkedList<>();
                SlowWaves.add(slowwave);
                double max=0,min=0;
                for(int j=start;j<=end;j++){//将符合条件1的数据加入链表
                    SlowWaves.getLast().add(datafilted[j]);
                    //获取峰值
                    if(datafilted[j]>max){max = datafilted[j];}
                    //获取谷值
                    if(datafilted[j]<min){min = datafilted[j];}
                }
                // 4、判断是否峰峰值>75,谷值<-40
                if(min<-40 && (max-min)>75){//符合
                    int[] index = new int[2];
                   Index.add(index);
                   //存入慢波的开始和结束位置
                   Index.getLast()[0]=start;
                   Index.getLast()[1]=end;
                }else {
                    //移除加入的元素
                    SlowWaves.pollLast();
                }
            }
        }
        Log.i(TAG, "slowWaveDetection: 检测完成！慢波个数为="+SlowWaves.size());
    }

    /**
     * 获取滤波后的N3数据
     * @return
     */
    public double[] getDataFilterd(){
            return dataFilterd;
    }

    /**
     * 获取所有慢波的开始和结束位置
     * @return
     */
    public LinkedList<int[]> getIndex(){
            return Index;
    }
    /**
     * 判别条件 1：寻找由正到负的零点
     * @param fft_data 滤波后的数据
     * @return 零点的索引值
     */
    public boolean find_P(ArrayList<Float> fft_data){
        Log.i(TAG, "find_P/fft_data.size: "+fft_data.size());

        boolean findit = false;
        //用来检测由正到负的零点位置
        while(fft_data.size()>125 && !findit){
            Log.i(TAG, "find_P/fft_data.size: "+fft_data.size());
            if( (fft_data.get(0)>0) && (fft_data.get(1)<0) ){//情况1： 当数据从正到负，但没出现零点，则取正数
                Log.i(TAG, "find_P/(1): 找到！开始进行第二步判别！");
                Log.i(TAG, "find_P/(1): 过零点的值为 "+fft_data.get(0)+" , "+fft_data.get(1));
                //开始寻找负到正的零点
                findit = find_N(fft_data);
                if(!findit){//如果未找到以此零点为左端点的目的区间，则重新寻找从正到负的零点
                    Log.i(TAG, "find_P/(1): 第二步判别失败");
                    fft_data.remove(0);
                }else{
//                    point_zero = fft_data.get(0);
                    Log.i(TAG, "find_P/(1): findit is true! 找到，完成检测！");
                    Log.i(TAG, "find_P/(1): 过零点的值为 "+fft_data.get(0)+" , "+fft_data.get(1));
                    //如果找到则跳出循环
                    findit = true;
                }

            }else if( (fft_data.get(0)>0) && (fft_data.get(1)==0) && (fft_data.get(2)<0) ) {//情况2：索引为1的元素为由正到负的零点
                Log.i(TAG, "find_P/(2): 找到！开始进行第二步判别！");
                //开始寻找负到正的零点
                findit = find_N(fft_data);
                if(!findit){//如果未找到以此零点为左端点的目的区间，则重新寻找从正到负的零点
                    Log.i(TAG, "find_P/(2): 第二步判别失败！");
                    fft_data.remove(0);
                }else{
                    //如果找到则跳出循环
                    findit = true;
                    Log.i(TAG, "find_P/(2): findit is true! 找到，完成检测！");
                    Log.i(TAG, "find_P/()2: fft_data.get(1) = "+fft_data.get(1));
                }
            }else{//情况3：若为找到符合条件的“正到负”的零点，则删除首元素
                Log.i(TAG, "find_P/(3): 未找到，继续检测下一个元素");
                fft_data.remove(0);
            }

        }
        return findit;


    }


    /**
     * 判别条件 2：寻找由负到正的零点
     *
     */
    public boolean find_N(ArrayList<Float> fft_data){
        Log.i(TAG, "find_N/fft_data.size: "+fft_data.size());
        boolean findit = false;
        int i=1;
        int right = 0;
        while( i<fft_data.size()-1 && !findit ){
            if(  fft_data.get(i)<0  &&  fft_data.get(i+1)>0 ){//情况1： 找到! 数据从负到正，但没出现零点，此时取正负数索引
                Log.i(TAG, "find_N/(1): 找到！零点左右的值为： "+fft_data.get(i)+" , "+fft_data.get(i+1));
                Log.i(TAG, "find_N/(1): i = "+i);
                if(i <= 8){//索引不符合条件，继续寻找
                    Log.i(TAG, "find_N/(1): 找到！但 i<8 继续寻找！");
                    i++;
                }
//                else if(i>125){//索引超出限制条件，跳出循环
//                    Log.i(TAG, "find_N/(1): 找到！但 i<8 继续寻找！");
//                    break;
//                }
                else{//符合条件，进行下一步判断
                    Log.i(TAG, "find_N/(1): 找到！进行第三步判别！");
                    findit = detectNPeak(fft_data.subList(0,i));//如果符合判别条件3，则返回true，跳出循环
                    //如果不符合判别条件3，则返回false，继续寻找符合判别条件2的“负到正零点”
                    if(!findit) {
                        Log.i(TAG, "find_N/(1): 找到！第三步判别失败，i++！");
                        i++;
                    }

                }
            }else if( (fft_data.get(i-1)<0) && (fft_data.get(i)==0) && (fft_data.get(i+1)>0) ) {//情况2：找到! 负到正的零点
                Log.i(TAG, "find_N/(2): 找到！零点值为： "+fft_data.get(i)+" i = "+i);
                if(i <= 8){//索引不符合条件，继续寻找
                    Log.i(TAG, "find_N/(2): i<8 继续寻找！");
                    i++;
                }
//                else if(i>125){//索引超出限制条件，跳出循环
//                    break;
//                }
                else{//符合条件，进行下一步判断
                    Log.i(TAG, "find_N/(2): 符合条件！进行第三步判别！");
                    findit = detectNPeak(fft_data.subList(0,i));
                }
            }else{//情况3：未找到!
                Log.i(TAG, "find_N/(3): 未找到！继续寻找！"+"i = "+i);
                if(i<=8){//继续寻找
                    i++;
                }else if(i>125){//超过限定区间，停止寻找
                    break;
                }else{
                    i++;
                }
            }
        }
        Log.i(TAG, "find_N/findit =  "+findit);
        return findit;
    }

    /**
     * 判别条件 3：检测谷值是否符合条件
     */
    public boolean detectNPeak(List<Float> interval){
        Log.i(TAG, "detectNPeak/interval.size: "+interval.size());
        int i=1;
        boolean findit = false;
        while( i<interval.size()-2 && !findit ){
            if( interval.get(i-1)>interval.get(i)  &&  interval.get(i+1)>interval.get(i)  &&  interval.get(i)<-40) {
                Log.i(TAG, "detectNPeak/谷值为: "+interval.get(i)+" i = "+i);
                //找到负峰,且峰值小于-40
                findit = true;
            }
            else{
                i++;
            }
        }
        return findit;
    }



    /**
     * 计算慢波的密度
     * 单位：个/每分钟
     */
    public float calDensity(){
        float density = n3time/Index.size() ;
        return density;
    }

    /**
     * 计算每个慢波的持续时间
     *  单位：s（秒）
     */
    float[] Duration;
    public float[] calDuration(){
        Duration = new float[SlowWaves.size()];
        float duration=0;
        for(int i=0;i<Index.size();i++){
            duration=(Index.get(i)[1]-Index.get(i)[0]+1)/125f;
            Duration[i]=duration;
        }
        return Duration;
    }
    /**
     * 计算所有慢波的平均持续时间
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
     * 计算每个慢波的能量
     * 每个慢波的能量=各点幅度的平方
     * 单位：微伏
     * @return
     */
    float[] Energy ;
    public float[] calEnergy(){
        Energy = new float[SlowWaves.size()];
        for(int i=0;i<SlowWaves.size();i++){
            List<Double> spindle = SlowWaves.get(i);
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
     * 计算所有慢波能量的平均值
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
     * 计算慢波平均功率
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
     * 计算每个慢波的最大幅度
     * 单位:微伏
     */
    float[] Amplitude;
    public float[] calAmplitude(){
        Amplitude = new float[SlowWaves.size()];
        for(int i=0;i<SlowWaves.size();i++){
            Amplitude[i]=getMAX(SlowWaves.get(i));
        }
        return Amplitude;
    }

    /**
     * 计算所有慢波的平均幅值
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
     * 计算每个慢波的频率
     * 不太正确，后期需要更改
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void calFrequency(){
        for(int i=0;i<SlowWaves.size();i++){
            double[] slowwave = new double[SlowWaves.get(i).size()];
            for(int j=0;j<SlowWaves.get(i).size();j++){
                slowwave[j]=SlowWaves.get(i).get(j);
            }
            FastFourier fastFourier = new FastFourier(slowwave);
            double[] sw_freq = fastFourier.getMagnitude(true);
            Log.i(TAG, "calFrequency: sw_freq.length="+sw_freq.length);
        }


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





}
