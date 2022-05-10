package com.example.testversion.fragment4;

import android.content.Context;
import android.util.Log;

import com.example.testversion.jdsp.filter.Butterworth;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class SleepStage {
    private static String TAG="SleepStage";


    /**
     * 调用训练好的深度学习模型，进行睡眠分期
     * 模型输入：每次输入5帧的数据，数据维度为5*3750*5（通道,每30s的数据，5帧）
     * 模型输出：输出各分期的概率 ，顺序为 ：W，N1，N2，N3，REM，Unknown，输出格式为 float[6]
     * @param DATA 5个通道的信号
     * @param context 环境
     * @param Freq 采样率
     * @return 每帧的分期结果,即概率最大的阶段。0-Wake，1-N1,2-N2，3-N3，4-REM，5-Unknown
     */
    public ArrayList<Integer> stageDection(double[][] DATA, Context context,int Freq){
//        Random  random = new Random();
        //2、获取数据共有多少时段
        int num_epoch = DATA[0].length/(Freq*30);
        Log.i(TAG, "stageDection: 共有"+num_epoch+"个时段！");
        // 对数据进行0.3-35Hz的滤波
        double[][] DATA_filted = new double[DATA.length][DATA[0].length];
        for(int i=0;i<DATA.length;i++){
            double[] data = DATA[i];
            Butterworth butter1 = new Butterworth(data,125);
            DATA_filted[i] = butter1.bandPassFilter(4,0.3,35);
        }
        //保存结果的链表
        ArrayList<Integer> sleepStage = new ArrayList<>(num_epoch-4);
        //3、从缓存区加载模型
        Module module = Module.load(assetFilePath(context,"cpu_1_10_0.pt"));
        //Tensor封装的数据维度
        long[] shape = {1,5,3750,5};
        //给模型传入数据，每次传5帧
        for(int n=0;n<=num_epoch-5;n++){//n代表第几次传入
            //4、将数据存成一维数组
            float[] epochData = getTensor(DATA_filted,125,n);
            //5、将一维数组转换成三维张量
            Tensor inputTensor  = Tensor.fromBlob(epochData,shape);
            //6、运行模型,计算结果
            Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();
            //保存结果
            float[] result = outputTensor.getDataAsFloatArray();
            //得到最高的概率索引
            int stage_index = getMAX(result);
//            7、保存结果
            sleepStage.add(n,stage_index);

        }
        Log.i(TAG, "stageDection: sleepstage.size="+sleepStage.size());
        return sleepStage;
    }

    /**
     *  将数据转换成一维数组，以便后续3维张量的转换
     * @param DATA 5个通道的数据，EEG1，EEG2，EOGL,EOGR，EMG
     * @param Freq 采样率
     * @param times 第几次传入模型。每次传入5帧
     * @return
     */
    public float[] getTensor(double[][] DATA,int Freq,int times){
        //计算一个时段有多少个数据点
        int epoch_num = Freq*30; //epoch_num=3750
        float[] epochData = new float[93750];
        //开始点数
        int startPoint = epoch_num*times;
        //结束点数
        int endPoint = epoch_num*(times+5);
//        Log.i(TAG, "getTensor: startPoint="+startPoint+", endPoint="+endPoint);
        //一次传入5个时段中5个通道的数据，共有5*3750*5个数
        //一维数据结构为：[[5],[5]....[5]]
        //[5]为统一时间点的5个通道的数据，共有3750*5个，3750表示30s的数据个数，5表示帧数即时段
        int k=0;
        for (int i = startPoint; i < endPoint; i++) {//30s
            for (int j = 0; j < 5; j++) {//5个通道
                epochData[k] = (float) DATA[j][i];
                k++;
            }
        }
        return epochData;

    }
    private int getMAX(float[] result){
        if(result.length==0){return 0;}
        else{
            float max = result[0];
            int stage = 0;
            for(int i=1;i<result.length;i++){
                if(max<result[i]){
                    max = result[i];
                    stage = i;
                }
            }
            return stage;
        }

    }
    public float getMAX(ArrayList<Integer> result){
        if(result.size()==0){return 0;}
        else {
            float max = result.get(0);
            int stage = 0;
            for(int i=1;i<result.size();i++){
                if(max<result.get(i)){
                    max = result.get(i);
                    stage = i;
                }
            }
            return max;
        }
    }

    public float getAverage(ArrayList<Integer> result){
        if(result.size()==0){return 0;}
        else{
            float total=0;
            float n=result.size();
            for(int v:result){
                total+=v;
            }
            return total/n;
        }

    }




    /**
     * EEG数据分段，每30s分一段
     * @param data 单通道数据
     * @param Freq 采样率
     * @return 分段后数据
     */
    public ArrayList<float[]> toEpoch(float[] data,int Freq){
        ArrayList<float[]> data_slice = new ArrayList<float[]>(data.length/Freq*30);
        for(int j=0;j<data.length/Freq*30;j++){
            for(int i=j*Freq*30;i<Freq*30;i++){
                data_slice.get(j)[i] =data[i];
            }
        }
        return data_slice;
    }
    /**
     * 从asset中输入流中复制数据，将其写入磁盘，并为其返回绝对文件路径。
     * @param context
     * @param assetName
     * @return
     */
    public static String assetFilePath(Context context, String assetName) {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e("Fragment4", "Error process asset " + assetName + " to file path");
        }
        return null;
    }

}

