package com.example.testversion.fragment5;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.testversion.Manager.CombinedChartManager;
import com.example.testversion.Manager.LineChartManager;
import com.example.testversion.R;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Fragment5 extends Fragment implements View.OnClickListener {
    String TAG = "Fragment5";
    Button btn_spindle,btn_slowwave;
    private LineChart mlineChart1 ,mlineChart2;
    List<Float> RawData = new ArrayList<>();
    private ArrayList<Integer> psgResult = new ArrayList<>() ;

    TextView sp_density,sp_duration,sp_amplitude,sp_energy,sp_power;
    TextView sw_density,sw_duration,sw_amplitude,sw_energy,sw_power;
    List<TextView> tV_sp = new ArrayList<>();
    List<TextView> tV_sw = new ArrayList<>();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_5, container, false);
        initview(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setChlickListener();
        super.onActivityCreated(savedInstanceState);
    }

    private void initview(View view){
        btn_spindle = (Button) view.findViewById(R.id.spindle);
        btn_slowwave = (Button) view.findViewById(R.id.slowwave);
        mlineChart1 = (LineChart)view.findViewById(R.id.linechart_spindle);
        mlineChart2 = (LineChart)view.findViewById(R.id.linechart_slowwave);

        sp_density = (TextView)view.findViewById(R.id.sp_density);
        sp_duration = (TextView)view.findViewById(R.id.sp_duration);
        sp_amplitude = (TextView)view.findViewById(R.id.sp_amplitude);
        sp_energy = (TextView)view.findViewById(R.id.sp_energy);
        sp_power = (TextView)view.findViewById(R.id.sp_power);

        tV_sp.add(sp_density);
        tV_sp.add(sp_duration);
        tV_sp.add(sp_amplitude);
        tV_sp.add(sp_energy);
        tV_sp.add(sp_power);

        sw_density = (TextView)view.findViewById(R.id.sw_density);
        sw_duration = (TextView)view.findViewById(R.id.sw_duration);
        sw_amplitude = (TextView)view.findViewById(R.id.sw_amplitude);
        sw_energy = (TextView)view.findViewById(R.id.sw_energy);
        sw_power = (TextView)view.findViewById(R.id.sw_power);

        tV_sw.add(sw_density);
        tV_sw.add(sw_duration);
        tV_sw.add(sw_amplitude);
        tV_sw.add(sw_energy);
        tV_sw.add(sw_power);

//        combinedChartSP = (CombinedChart) view.findViewById(R.id.sp_combinechart);
//        combinedChartSW = (CombinedChart) view.findViewById(R.id.sw_combinechart);
        if(mlineChart1 == null||mlineChart2 == null){
            Log.i(TAG, "initview: mlineChart is null!");
        }
    }

    private void setChlickListener(){
        btn_spindle.setOnClickListener(this);
        btn_slowwave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.spindle:
                Toast.makeText(getActivity(),"开始检测纺锤波！",Toast.LENGTH_SHORT).show();
                startSpindleDetection(RawData,psgResult);

                break;
            case R.id.slowwave:
                Toast.makeText(getActivity(),"开始检测慢波！",Toast.LENGTH_SHORT).show();
                startSlowWaveDetection(RawData,psgResult);
                break;
            default:
                break;
        }
    }

    public void getRawData(double[] data, ArrayList<Integer> psgresult){
        for(int i=0;i<data.length;i++){
            RawData.add((float)data[i]);
        }
        psgResult = psgresult;
        Log.i(TAG, "getRawData: Fragment5收到数据!");
    }

    /**
     * 纺锤波检测方法
     * @param rawData C3通道原始数据
     * @param result 各时段睡眠分期结果
     */
    public void startSpindleDetection(List<Float> rawData, ArrayList<Integer> result){
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                Log.i(TAG, "run: 0");
                ArrayList<Double> n2Data = new ArrayList<>();
                //提取所有N2期的数据
                for(int i=0;i<result.size();i++){
                    if(result.get(i) == 2){
                        int start = (i+2)*3750;
                        int end = start+3750;
                        for(int j=start;j<end;j++){
                            n2Data.add((double)rawData.get(j));
                        }
                    }
                }
                Log.i(TAG, "run: 1");
                double[] N2_data = new double[n2Data.size()];
                for(int i=0;i<n2Data.size();i++){
                    N2_data[i] = n2Data.get(i);
                }
                Log.i(TAG, "run: 2");
                Spindle mSpindle = new Spindle();
                mSpindle.spindleDetection(N2_data);
                LinkedList<int[]> index = mSpindle.getIndex();
                double[] dataFilterd = mSpindle.getDataFilterd();
                double threshold = mSpindle.getThreshold();
                Log.i(TAG, "run: 3");
                //计算纺锤波参数
                float[] avgDuration = mSpindle.calAvgDuration();
                float[] avgAmplitude = mSpindle.calAvgAmplitude();
                float density = mSpindle.calDensity();
                float[] avgEnergy = mSpindle.calAvgEnergy();
                float[] avgPower = mSpindle.calAvgPower(125);//计算平均功率前必须先计算平均能量
                Log.i(TAG, "run: 4");
                // 传入数据：滤波后数据；每个纺锤波的开始结束位置
                setLineChartValue(mlineChart1,dataFilterd,index,(float)threshold,Color.rgb(248,147,29));
                Log.i(TAG, "run: 5");
                //填入统计数据
                setStaticData(tV_sp,density,avgDuration,avgAmplitude,avgEnergy,avgPower);
                Log.i(TAG, "run: 6");
                //绘制纺锤波参数组合图
//                setCombinedChartValue(combinedChartSP,index.size(),Energy,Amplitude,Duration);
            }
        }).start();

    }

    /**
     * 慢波检测方法
     * @param rawData C4原始数据
     * @param result 各时段分期结果
     */
    public void startSlowWaveDetection(List<Float> rawData, ArrayList<Integer> result){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Double> n3Data = new ArrayList<>();
                //提取所有N3期的数据
                for(int i=0;i<result.size();i++){
                    if(result.get(i) == 3){
                        int start = (i+2)*3750;
                        int end = start+3750;
                        for(int j=start;j<end;j++){
                            n3Data.add((double)rawData.get(j));
                        }
                    }
                }
                Log.i(TAG, "run: 1!");
                //将数据转存到数组
                double[] N3_data = new double[n3Data.size()];
                for(int i=0;i<n3Data.size();i++){
                    N3_data[i] = n3Data.get(i);
                }
                Log.i(TAG, "run: 2!");
                SlowWave mSlowWave = new SlowWave();
                mSlowWave.slowWaveDetection(N3_data);
                LinkedList<int[]> index = mSlowWave.getIndex();
                double[] dataFilterd = mSlowWave.getDataFilterd();
                float[] avgDuration = mSlowWave.calAvgDuration();
                float[] avgAmplitude = mSlowWave.calAvgAmplitude();
                float density = mSlowWave.calDensity();
                float[] avgEnergy = mSlowWave.calAvgEnergy();
                float[] avgPower = mSlowWave.calAvgPower(125);//计算平均功率前必须先计算平均能量
                Log.i(TAG, "run: 3!");
                // 传入数据：滤波后数据；每个慢波的开始结束位置
                setLineChartValue(mlineChart2,dataFilterd,index,-1,Color.rgb(38,188,213));
                //填入统计数据
                setStaticData(tV_sw,density,avgDuration,avgAmplitude,avgEnergy,avgPower);
                Log.i(TAG, "run: 4!");
//                setCombinedChartValue(combinedChartSW,index.size(),Energy,Amplitude,Duration);
            }
        }).start();

    }

    /**
     * 给Linechart传入数据
     * @param value
     * @param Index
     */
    private void setLineChartValue(LineChart lineChart,double[] value, LinkedList<int[]> Index,float threshold,int color){
        ArrayList<Entry> Entrys = new ArrayList<>();
        int x1=0;
        for(double y:value){
            Entrys.add(new Entry(x1,(float) y));
            x1++;
        }
        if (mlineChart1 == null){
            Log.i(TAG, "showLineChart: mlineChart is null!");
        }
        //传入颜色链表
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for(int i=0;i<Entrys.size();i++){
            colors.add(Color.rgb(207,207,207 ));
        }
        for(int i=0;i<Index.size();i++){
            int right = Index.get(i)[0];
            int left = Index.get(i)[1];
            Log.i(TAG, "setLineChartValue: start="+right+" ,end="+left);
            for(int j=right;j<=left;j++){
                colors.set(j,color);
            }
        }
        Log.i(TAG, "setLineChartValue: 传入数据");
        LineChartManager lineChartManager = new LineChartManager(lineChart,threshold);
        lineChartManager.showLineChart3(lineChart,Entrys,colors);
    }




    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void setStaticData(List<TextView> textViews,float density, float[] avgDuration, float[] avgAmplitude,float[] avgEnergy,float[] avgPower){
        textViews.get(0).setText(String.format("%.2f",density));
        textViews.get(1).setText(String.format("%.2f",avgDuration[0]) +"±"+String.format("%.2f",avgDuration[1]));
        textViews.get(2).setText(String.format("%.2f",avgAmplitude[0])+"±"+String.format("%.2f",avgAmplitude[1]));
        textViews.get(3).setText(String.format("%.2f",avgEnergy[0])+"±"+String.format("%.2f",avgEnergy[1]));
        textViews.get(4).setText(String.format("%.2f",avgPower[0])+"±"+String.format("%.2f",avgPower[1]));
    }




    /**
     * 给组合图添加数据
     * @param mCombinedChart1 组合图实例
     * @param num 纺锤波数量
     * @param barData1 柱状图1的数据
     * @param barData2 柱状图2的数据
     * @param lineData 线性图的数据
     */
    private void setCombinedChartValue(CombinedChart mCombinedChart1,int num,List<Float> barData1,List<Float> barData2,List<Float> lineData){
        // x轴数据
        List<String> xData = new ArrayList<>();
        for (int i = 1; i <=num; i++) {
            xData.add(String.valueOf(i));
        }
        // 两个柱状图数据
        List<List<Float>> yBarDatas = new ArrayList<>();
        yBarDatas.add(barData1);
        yBarDatas.add(barData2);

        List<String> barNames = new ArrayList<>();
        barNames.add("能量");
        barNames.add("幅度");
        //颜色集合
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(252,157,154));
        colors.add(Color.rgb(70, 130, 180 ));
        colors.add(Color.rgb(255, 193, 37 ));

        //管理类
        CombinedChartManager combineChartManager1 = new CombinedChartManager(mCombinedChart1);
        combineChartManager1.showCombinedChart(xData, yBarDatas, lineData,barNames, "持续时间", colors, colors.get(2));
    }


}
