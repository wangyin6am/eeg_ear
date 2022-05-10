package com.example.testversion.fragment2;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.testversion.MyApplication;
import com.example.testversion.R;
import com.example.testversion.activity.FFT;
import com.example.testversion.activity.MainActivity;
import com.example.testversion.activity.SW_Detect;
import com.example.testversion.service.BleService;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import butterknife.ButterKnife;

import static com.example.testversion.R.id;
import static com.example.testversion.R.layout;


@SuppressLint("HandlerLeak")
public class Fragment2 extends Fragment implements MainActivity.SendValue {

    boolean isReceive;
    Context fcontext ;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switch1,switch2,switch3,switch4,switch5,switch6,switch7,switch8;
    TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8;
    TextView v1,v2,v3,v4,v5,v6,v7,v8;
    String[] v_ch = new String[8];
    Button sampling_start,sampling_stop,ble_start,ble_stop,cal_HRV,set_Flag;
    ImageButton btn_up,btn_down,btn_add,btn_sub;
    //定义每个折线图
    LineChart Flag_Chart;
    LineChart LineChart1;
    LineChart LineChart2;
    LineChart LineChart3;
    LineChart LineChart4;
    LineChart LineChart5;
    LineChart LineChart6;
    LineChart LineChart7;
    LineChart LineChart8;

    BleService bluetoothService;
    BluetoothAdapter bleAdapter;
    LineData fLineData,LineData1,LineData2,LineData3,LineData4,LineData5,LineData6,LineData7,LineData8; // 线集合，所有折现以数组的形式存到此集合中
    // Chart需要的点数据链表
    List<Entry> mEntries1 = new ArrayList<>();
    List<Entry> mEntries2 = new ArrayList<>();
    List<Entry> mEntries3 = new ArrayList<>();
    List<Entry> mEntries4 = new ArrayList<>();
    List<Entry> mEntries5 = new ArrayList<>();
    List<Entry> mEntries6 = new ArrayList<>();
    List<Entry> mEntries7 = new ArrayList<>();
    List<Entry> mEntries8 = new ArrayList<>();
    List<Entry> fEntries = new ArrayList<>();
    //  Y值数据链表
    List<Float> List1 = new ArrayList<>();
    List<Float> List2 = new ArrayList<>();
    List<Float> List3 = new ArrayList<>();
    List<Float> List4 = new ArrayList<>();
    List<Float> List5 = new ArrayList<>();
    List<Float> List6 = new ArrayList<>();
    List<Float> List7 = new ArrayList<>();
    List<Float> List8 = new ArrayList<>();

    //定义每个图的折线
    LineDataSet fLineDataSet = new LineDataSet(fEntries, "");
    LineDataSet LineDataSet1 = new LineDataSet(mEntries1, "");
    LineDataSet LineDataSet2 = new LineDataSet(mEntries2, "");
    LineDataSet LineDataSet3 = new LineDataSet(mEntries3, "");
    LineDataSet LineDataSet4 = new LineDataSet(mEntries4, "");
    LineDataSet LineDataSet5 = new LineDataSet(mEntries5, "");
    LineDataSet LineDataSet6 = new LineDataSet(mEntries6, "");
    LineDataSet LineDataSet7 = new LineDataSet(mEntries7, "");
    LineDataSet LineDataSet8 = new LineDataSet(mEntries8, "");

    XAxis XAxisf,XAxis1,XAxis2,XAxis3,XAxis4,XAxis5,XAxis6,XAxis7,XAxis8; //X轴
    YAxis LeftYAxisf,LeftYAxis1,LeftYAxis2,LeftYAxis3,LeftYAxis4,LeftYAxis5,LeftYAxis6,LeftYAxis7,LeftYAxis8; //左侧Y轴

    float[] MOVE = new float[8];
    int move_ch = 0;
//    int[] multiple = {100,200,300,400,500,1000,1500,2000,5000,10000,20000};
    int mul = 500;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(layout.fragment_2, container, false);
        fcontext = MyApplication.getInstance();
        ButterKnife.bind(view);



//        Flag_Chart = view.findViewById(id.flag_chart);
        LineChart1 = view.findViewById(id.line_chart1);
        LineChart2 = view.findViewById(id.line_chart2);
        LineChart3 = view.findViewById(id.line_chart3);
        LineChart4 = view.findViewById(id.line_chart4);
        LineChart5 = view.findViewById(id.line_chart5);
        LineChart6 = view.findViewById(id.line_chart6);
        LineChart7 = view.findViewById(id.line_chart7);
        LineChart8 = view.findViewById(id.line_chart8);

      
        initView(view);//获取X轴Y轴

        initLine();//每个通道初始化折线图，动态创建折线

        initWay1();//动态添加数据

//        initWay2();//滤波

//        initWay3();

        return view;
    }

    Handler.Callback callback;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.callback = (Handler.Callback) context;
    }

    /**
     * 实例化对象
     */
    public void initView(@NotNull View v){
        bluetoothService = new BleService();
        sampling_start = v.findViewById(id.sampling_start);
        sampling_stop = v.findViewById(id.sampling_stop);
        ble_start = v.findViewById(id.ble_start);
        ble_stop = v.findViewById(id.ble_stop);

        set_Flag = v.findViewById(id.setFlag);

        btn_up = v.findViewById(id.btn_up);
        btn_down = v.findViewById(id.btn_down);
        btn_add = v.findViewById(id.btn_add);
        btn_sub = v.findViewById(id.btn_sub);

        switch1 = v.findViewById(id.switch1);
        switch2 = v.findViewById(id.switch2);
        switch3 = v.findViewById(id.switch3);
        switch4 = v.findViewById(id.switch4);
        switch5 = v.findViewById(id.switch5);
        switch6 = v.findViewById(id.switch6);
        switch7 = v.findViewById(id.switch7);
        switch8 = v.findViewById(id.switch8);

        tv1 = v.findViewById(id.textView1);
        tv2 = v.findViewById(id.textView2);
        tv3 = v.findViewById(id.textView3);
        tv4 = v.findViewById(id.textView4);
        tv5 = v.findViewById(id.textView5);
        tv6 = v.findViewById(id.textView6);
        tv7 = v.findViewById(id.textView7);
        tv8 = v.findViewById(id.textView8);

        v1 = v.findViewById(id.v_ch1);
        v2 = v.findViewById(id.v_ch2);
        v3 = v.findViewById(id.v_ch3);
        v4 = v.findViewById(id.v_ch4);
        v5 = v.findViewById(id.v_ch5);
        v6 = v.findViewById(id.v_ch6);
        v7 = v.findViewById(id.v_ch7);
        v8 = v.findViewById(id.v_ch8);
//        fLineData = new LineData();  Flag_Chart.setData(fLineData);
        LineData1 = new LineData();  LineChart1.setData(LineData1);
        LineData2 = new LineData();  LineChart2.setData(LineData2);
        LineData3 = new LineData();  LineChart3.setData(LineData3);
        LineData4 = new LineData();  LineChart4.setData(LineData4);
        LineData5 = new LineData();  LineChart5.setData(LineData5);
        LineData6 = new LineData();  LineChart6.setData(LineData6);
        LineData7 = new LineData();  LineChart7.setData(LineData7);
        LineData8 = new LineData();  LineChart8.setData(LineData8);

        // 得到x轴
//        XAxisf = Flag_Chart.getXAxis();
        XAxis1 = LineChart1.getXAxis(); XAxis5 = LineChart5.getXAxis();
        XAxis2 = LineChart2.getXAxis(); XAxis6 = LineChart6.getXAxis();
        XAxis3 = LineChart3.getXAxis(); XAxis7 = LineChart7.getXAxis();
        XAxis4 = LineChart4.getXAxis(); XAxis8 = LineChart8.getXAxis();

        // 得到左侧Y轴
//        LeftYAxisf = Flag_Chart.getAxisLeft();
        LeftYAxis1 = LineChart1.getAxisLeft();  LeftYAxis5 = LineChart5.getAxisLeft();
        LeftYAxis2 = LineChart2.getAxisLeft();  LeftYAxis6 = LineChart6.getAxisLeft();
        LeftYAxis3 = LineChart3.getAxisLeft();  LeftYAxis7 = LineChart7.getAxisLeft();
        LeftYAxis4 = LineChart4.getAxisLeft();  LeftYAxis8 = LineChart8.getAxisLeft();

        //关闭x轴，y轴
//        setFlagXYAxis(XAxisf,LeftYAxisf);
        setXYAxis(XAxis1,LeftYAxis1,10);
        setXYAxis(XAxis2,LeftYAxis2,10);
        setXYAxis(XAxis3,LeftYAxis3,10);
        setXYAxis(XAxis4,LeftYAxis4,10);
        setXYAxis(XAxis5,LeftYAxis5,10);
        setXYAxis(XAxis6,LeftYAxis6,10);
        setXYAxis(XAxis7,LeftYAxis7,10);
        setXYAxis(XAxis8,LeftYAxis8,10);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (bleAdapter == null) {
            bleAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        setButtonListener(sampling_start);
        setButtonListener(sampling_stop);
        setButtonListener(ble_start);
        setButtonListener(ble_stop);
//        setButtonListener(cal_HRV);
        setButtonListener(set_Flag);
        setImageButtonListener(btn_up);
        setImageButtonListener(btn_down);
        setImageButtonListener(btn_add);
        setImageButtonListener(btn_sub);
        //对TextView进行设置
        setTextViewListener(tv1);
        setTextViewListener(tv2);
        setTextViewListener(tv3);
        setTextViewListener(tv4);
        setTextViewListener(tv5);
        setTextViewListener(tv6);
        setTextViewListener(tv7);
        setTextViewListener(tv8);
        //对switch进行设置
        setSwitch(switch1,0);
        setSwitch(switch2,1);
        setSwitch(switch3,2);
        setSwitch(switch4,3);
        setSwitch(switch5,4);
        setSwitch(switch6,5);
        setSwitch(switch7,6);
        setSwitch(switch8,7);
        switch1.setChecked(false);
        super.onActivityCreated(savedInstanceState);
    }

    public void setButtonListener(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View v) {
                switch (button.getId()){
                    case id.sampling_start:
                        Handler handler1 = new Handler(callback);
                        Message msg1 = Message.obtain();
                        msg1.what = 0;
                        handler1.sendMessage(msg1);
                        break;
                    case id.sampling_stop:
                        Handler handler2 = new Handler(callback);
                        Message msg2 = Message.obtain();
                        msg2.what = 1;
                        handler2.sendMessage(msg2);
                        break;
                    case id.ble_start:
                        Handler handler3 = new Handler(callback);
                        Message msg3 = Message.obtain();
                        msg3.what = 2;
                        handler3.sendMessage(msg3);
                        break;
                    case id.ble_stop:
                        Handler handler4 = new Handler(callback);
                        Message msg4 = Message.obtain();
                        msg4.what = 3;
                        handler4.sendMessage(msg4);
                        break;
                    case id.setFlag:
                        Handler handler6 = new Handler(callback);
                        Message msg6 = Message.obtain();
                        msg6.what = 5;
                        handler6.sendMessage(msg6);
                    default:
                        break;
                }
//                String str = button.getText().toString();
//                Handler handler = new Handler(callback);
//                Message msg = Message.obtain();
//                if(str.equals("开始")){
//                    button.setText("取消");
//                    msg.what = 1;
//                    msg.obj = null;
//                    handler.sendMessage(msg);
//                    Log.i(TAG, "onClick: is not Press !");
//                }else {
//                    button.setText("取消");
//                    msg.what = 0;
//                    msg.obj = null;
//                    handler.sendMessage(msg);
//                    button.setText("开始");
//                    Log.i(TAG, "onClick: is Press !");
//                    // 断开连接
//                }
            }
        });
    }


    int m = 2;
    int s = 1;
    public void setImageButtonListener(ImageButton imageButton){
        imageButton.setOnClickListener(new View.OnClickListener() {
            int count = 0;
            @SuppressLint({"NonConstantResourceId", "ShowToast"})
            @Override
            public void onClick(View view) {
                switch(imageButton.getId()){
                    case id.btn_up://上移
//                                if(s>1){s = (int)(Float.parseFloat(v_ch[0])/2); }
//                                else{s =1;}
                        MOVE[move_ch]+=s;
                        Log.i(TAG, "onClick: MOVE = "+MOVE[move_ch]);
                        break;
                    case id.btn_down://下移
//                                if(s>1){ s = (int)(Float.parseFloat(v_ch[0])/2); }
//                                else{s = 1;}
                        MOVE[move_ch]-=s;
                        Log.i(TAG, "onClick: MOVE = "+MOVE[move_ch]);
                        break;
                    case id.btn_add://放大
//                        m = mul/2;
                        mul/=m;
                        Log.i(TAG, "onClick: mul = "+mul);
                        break;
                    case id.btn_sub://缩小
//                        m = mul/2;
                        mul*=m;
                        Log.i(TAG, "onClick: mul = "+mul);
                        break;
                }
            }
        });
    }
    /**
     * 设置textview的点击监听事件
     * 点击textview，将对应的chart置于最上层
     */
    public void setTextViewListener(TextView tv){
        tv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View v) {
                switch(tv.getId()){
                    case id.textView1:
                        LineChart1.bringToFront();
                        move_ch = 0;
                        break;
                    case id.textView2:
                        LineChart2.bringToFront();
                        move_ch = 1;
                        break;
                    case id.textView3:
                        LineChart3.bringToFront();
                        move_ch = 2;
                        break;
                    case id.textView4:
                        LineChart4.bringToFront();
                        move_ch = 3;
                        break;
                    case id.textView5:
                        LineChart5.bringToFront();
                        move_ch = 4;
                        break;
                    case id.textView6:
                        LineChart6.bringToFront();
                        move_ch = 5;
                        break;
                    case id.textView7:
                        LineChart7.bringToFront();
                        move_ch = 6;
                        break;
                    case id.textView8:
                        LineChart8.bringToFront();
                        move_ch = 7;
                        break;
                }

            }
        });
    }

    public void setTextThread(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                v1.setText(v_ch[0]);
                v2.setText(v_ch[1]);
                v3.setText(v_ch[2]);
                v4.setText(v_ch[3]);
                v5.setText(v_ch[4]);
                v6.setText(v_ch[5]);
                v7.setText(v_ch[6]);
                v8.setText(v_ch[7]);
            }
        });

    }
    /**
     * 进行switch的相关设置,控制绘图
     */
    public void setSwitch(Switch sw,int i) {
        // 1、设置初始状态为off
        sw.setChecked(false);
        // 2、设置监听事件
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch(buttonView.getId()){
                    case id.switch1:
                        if(isChecked){
                            channel_on[0] = 1;
                            LineChart1.setData(LineData1);//加载数据
                        }else {
                            channel_on[0] = 0;
                            LineChart1.clear();//清空数据集
                        }
                        break;
                    case id.switch2:
                        if(isChecked){
                            channel_on[1] = 1;
                            LineChart2.setData(LineData2);
                        }else {
                            channel_on[1] = 0;
                            LineChart2.clear();
                        }
                        break;
                    case id.switch3:
                        if(isChecked){
                            channel_on[2] = 1;
                            LineChart3.setData(LineData3);
                        }else {
                            channel_on[2] = 0;
                            LineChart3.clear();
                        }
                        break;
                    case id.switch4:
                        if(isChecked){
                            channel_on[3] = 1;
                            LineChart4.setData(LineData4);
                        }else {
                            channel_on[3] = 0;
                            LineChart4.clear();}
                        break;
                    case id.switch5:

                        if(isChecked){
                            channel_on[4] = 1;
                            LineChart5.setData(LineData5);
                        }else {
                            channel_on[4] = 0;
                            LineChart5.clear();
                        }
                        break;
                    case id.switch6:
                        if(isChecked){
                            channel_on[5] = 1;
                            LineChart6.setData(LineData6);
                        }else {
                            channel_on[5] = 0;
                            LineChart6.clear();
                        }
                        break;
                    case id.switch7:
                        if(isChecked){
                            channel_on[6] = 1;
                            LineChart7.setData(LineData7);
                        }else {
                            channel_on[6] = 0;
                            LineChart7.clear();
                        }
                        break;
                    case id.switch8:
                        if(isChecked){
                            channel_on[7] = 1;
                            LineChart8.setData(LineData8);
                        }else {
                            channel_on[7] = 0;
                            LineChart8.clear();
                        }
                        break;
                }

            }
        });
    }

    /**
     * 设置flag_chart的x轴 y轴
     * @param xAxis
     * @param yAxis
     */
    public void setFlagXYAxis(XAxis xAxis,YAxis yAxis){
        xAxis.setEnabled(false);
        yAxis.setEnabled(false);
        yAxis.setAxisMaximum(3);
        yAxis.setAxisMinimum(0);
        yAxis.setDrawLabels(false); // 不设置坐标轴数据标签;
        yAxis.setDrawAxisLine(false); // 不绘制坐标轴线
        yAxis.setDrawGridLines(false); // 是否绘制网格线
        yAxis.setLabelCount(4, false);
//        yAxis.setDrawTopYLabelEntry(true);
//        yAxis.setDrawGridLinesBehindData(true);
    }

    /**
     * 设置X轴 Y轴
     */
    public void setXYAxis(XAxis xAxis,YAxis yAxis,int labelcount){
        xAxis.setEnabled(false);
        yAxis.setEnabled(false);
        yAxis.setAxisMaximum(9);
        yAxis.setAxisMinimum(-1);
//        yAxis.setDrawLabels(true); // 不设置坐标轴数据标签;
//        yAxis.setDrawAxisLine(false); // 不绘制坐标轴线
//        yAxis.setDrawGridLines(false); // 不绘制网格线
        yAxis.setLabelCount(3, false);
//        yAxis.setDrawTopYLabelEntry(true);
//        yAxis.setDrawGridLinesBehindData(true);

    }

    public void initLine(){

        // 初始化折线图
//        setFlag_Chart();
        initLineChart(LineChart1,Color.parseColor("#E92E63"));
        initLineChart(LineChart2,Color.parseColor("#9C27B0"));
        initLineChart(LineChart3,Color.parseColor("#651FFF"));
        initLineChart(LineChart4,Color.parseColor("#5677FC"));
        initLineChart(LineChart5,Color.parseColor("#8BC34A"));
        initLineChart(LineChart6,Color.parseColor("#FFC107"));
        initLineChart(LineChart7,Color.parseColor("#FF9800"));
        initLineChart(LineChart8,Color.parseColor("#FF5722"));

        //动态创建一条折线
//        setFlagLine();
        createLine(List1,mEntries1,LineDataSet1,Color.parseColor("#E92E63"),LineData1,LineChart1);
        createLine(List2,mEntries2,LineDataSet2,Color.parseColor("#9C27B0"),LineData2,LineChart2);
        createLine(List3,mEntries3,LineDataSet3,Color.parseColor("#651FFF"),LineData3,LineChart3);
        createLine(List4,mEntries4,LineDataSet4,Color.parseColor("#5677FC"),LineData4,LineChart4);
        createLine(List5,mEntries5,LineDataSet5,Color.parseColor("#8BC34A"),LineData5,LineChart5);
        createLine(List6,mEntries6,LineDataSet6,Color.parseColor("#FFC107"),LineData6,LineChart6);
        createLine(List7,mEntries7,LineDataSet7,Color.parseColor("#FF9800"),LineData7,LineChart7);
        createLine(List8,mEntries8,LineDataSet8,Color.parseColor("#FF5722"),LineData8,LineChart8);
    }
    /**
     * 初始化 flag_chart折线图
     */
    public void setFlag_Chart() {

        Flag_Chart.setDoubleTapToZoomEnabled(true);
        // 不显示数据描述
        Flag_Chart.getDescription().setEnabled(false);
        // 没有数据的时候，显示“暂无数据”
        Flag_Chart.setNoDataText(" ");
        //设置图标基本属性
        Flag_Chart.fitScreen();
        Flag_Chart.setViewPortOffsets(0,0,0,0);
        Flag_Chart.setPinchZoom(false);//禁止x轴y轴同时进行缩放
        Flag_Chart.setScaleEnabled(false);//是否缩放两个轴
        Flag_Chart.setScaleEnabled(false);//启用/禁用缩放图表上的两个轴，设置为false以禁止通过在其上双击缩放图表,如果这个设为true，则x轴Y轴都能缩放
        Flag_Chart.setScaleXEnabled(false);//是否可以缩放X轴
        Flag_Chart.setScaleYEnabled(false);//是否可以缩放Y轴
        Flag_Chart.setHighlightPerTapEnabled(false);//是否显示在图上的点击位置。设置为false，以防止值由敲击姿态被突出显示。

        Flag_Chart.getAxisRight().setEnabled(false);//关闭右侧Y轴
        Flag_Chart.setDrawGridBackground(false);//设置是否绘制网格背景
        Flag_Chart.setDrawBorders(true);//设置是否显示边界
        Flag_Chart.setMaxVisibleValueCount(100);//设置最大可见绘制的 chart count 的数量
        Flag_Chart.setDragEnabled(false); //是否可以拖动
        Legend legend = Flag_Chart.getLegend();
        legend.setEnabled(false);// 这里不显示图例
    }

    /**
     * 初始化折线图
     */

    public void initLineChart(LineChart lineChart,int color) {

        lineChart.setDoubleTapToZoomEnabled(true);

        // 不显示数据描述
        lineChart.getDescription().setEnabled(false);
        // 没有数据的时候，显示“暂无数据”
        lineChart.setNoDataText(" ");
        //设置图标基本属性
        lineChart.fitScreen();
        lineChart.setViewPortOffsets(0,0,0,0);
        lineChart.setPinchZoom(false);//禁止x轴y轴同时进行缩放
        lineChart.setScaleEnabled(false);//是否缩放两个轴
        lineChart.setScaleEnabled(false);//启用/禁用缩放图表上的两个轴，设置为false以禁止通过在其上双击缩放图表,如果这个设为true，则x轴Y轴都能缩放
        lineChart.setScaleXEnabled(true);//是否可以缩放X轴
        lineChart.setScaleYEnabled(true);//是否可以缩放Y轴
        lineChart.setHighlightPerTapEnabled(false);//是否显示在图上的点击位置。设置为false，以防止值由敲击姿态被突出显示。

        lineChart.getAxisRight().setEnabled(false);//关闭右侧Y轴
        lineChart.setDrawGridBackground(false);//设置是否绘制网格背景
        lineChart.setDrawBorders(false);//设置是否显示边界
        lineChart.setMaxVisibleValueCount(100);//设置最大可见绘制的 chart count 的数量
        lineChart.setDragEnabled(true); //是否可以拖动
//        lineChart.setAutoScaleMinMaxEnabled(true);//根据数据动态设置最大值最小值的范围

        //折线图例 标签 设置
        //自定义设置图例
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);
//        mylegend = new LegendEntry();
        //设置图例

        //动态设置自定义图例
//        legend.setExtra(new LegendEntry[]{mylegend});
        //重置取消自定义的图例
//        legend.resetCustom();

        //lineChart.animateX(1500);//设置XY轴动画效果

        //设置Y轴属性
//        YLeftAxis.get(id).setEnabled(false);
//        YLeftAxis.get(id).setLabelCount(10, false);
//        YLeftAxis.get(id).isAxisMaxCustom();
//        LineCharts.get(id).invalidate();
//        YLeftAxis.get(id).setDrawGridLines(true);//设置是否绘制横网格线，true绘制
//        LineCharts.get(id).setVisibleYRangeMaximum(30, YAxis.AxisDependency.RIGHT);// 当前统计图表中最多在Y轴坐标线上显示的总量
    }

    /**
     * 初始化Flag_chart中的折线
     */
    private void setFlagLine() {
        // LineDataSet1 = new LineDataSet(null, "");  //添加一个空的 LineData
        fLineDataSet.setDrawFilled(false);
        fLineDataSet.setLineWidth(1.5f); // 设置折线宽度
        fLineDataSet.setColor(Color.parseColor("#14e715"));// 设置折线颜色
        fLineDataSet.setDrawValues(false);//是否显示折线上的值
        fLineDataSet.setDrawFilled(false); //设置折线图填充
        fLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        fLineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);//设置曲线展示为圆滑曲线（如果不设置则默认折线）
        fLineDataSet.setDrawCircles(false);//设置是否显示折线上的圆点，true代表显示
//        Log.i(TAG, "createLine: 初始化折线"+fLineDataSet);
        //判断是否存在折线，如果不存在则创建
        if (fLineData == null) {
            fLineData = new LineData();
            fLineData.addDataSet(fLineDataSet);
            Flag_Chart.setData(fLineData);
        } else {
            Flag_Chart.getLineData().addDataSet(fLineDataSet);
        }
        Flag_Chart.invalidate();
    }



    /**
     * 动态的创建一条折线
     */
    private void createLine(List<Float> datalist, List<Entry> entries, LineDataSet lineDataSet, int color, LineData lineData, LineChart lineChart) {
//
//        for (int i = 0; i < datalist.size(); i++) {
//            /**
//             * 在此可查看 Entry构造方法，可发现 可传入数值 Entry(float x, float y)
//             * 也可传入Drawable， Entry(float x, float y, Drawable icon) 可在XY轴交点 设置Drawable图像展示
//             */
//            Log.i(TAG, "createLine: 添加entry "+datalist.size());
//            Entry entry = new Entry(i, datalist.get(i));// Entry(x,y)
//            entries.add(entry);
//           // Log.i(TAG, "createLine: "+datalist.size());
//        }

        // 初始化折线
        initLineDataSet(lineDataSet, color);
        Log.i(TAG, "createLine: 初始化折线"+lineDataSet);

        //判断是否存在折线，如果不存在则创建
        if (lineData == null) {
            lineData = new LineData();
            lineData.addDataSet(lineDataSet);
            lineChart.setData(lineData);
        } else {
            lineChart.getLineData().addDataSet(lineDataSet);
        }
        lineChart.invalidate();
    }


    /**
     * 初始化折线(一条线)
     */
    private void initLineDataSet(LineDataSet lineDataSet,int color) {

        // LineDataSet1 = new LineDataSet(null, "");  //添加一个空的 LineData
        lineDataSet.setDrawFilled(false);
        lineDataSet.setLineWidth(1.0f); // 设置折线宽度
        lineDataSet.setColor(color);// 设置折线颜色
        lineDataSet.setDrawValues(true);//是否显示折线上的值
        lineDataSet.setDrawFilled(false); //设置折线图填充
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);//设置曲线展示为圆滑曲线（如果不设置则默认折线）

        lineDataSet.setDrawCircles(false);//设置是否显示折线上的圆点，true代表显示
        lineDataSet.setDrawCircleHole(true);//设置折线点是否空心
        lineDataSet.setCircleRadius(1f);
        lineDataSet.setCircleHoleRadius(1f);
    }

    /**
     *  动态添加新的数据点
     *  number lineChart lineData lineDataSet
     */
    String TAG = "Fragment2";
    int flag = 0;
    public void addEntry(LineChart lineChart,LineData lineData,LineDataSet lineDataSet,float value,int order) {

        //添加新的数据点
//        if(lineDataSet.getEntryCount()>2000){
//            for(int i = 0;i<1000;i++){
//                lineDataSet.removeFirst();
//            }
//        }
        Entry entry = new Entry(lineDataSet.getEntryCount(), value);
        //赋值给textview
        String s = Float.toString(value);
        v_ch[order] = s;
        Message msg = new Message();
        msg.what = order;
        mhandler.sendMessage(msg);
        lineData.addEntry(entry,0);
        //通知数据已经改变
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        //设置视窗X轴显示的范围最大值
        lineChart.setVisibleXRangeMaximum(1600);
        //将当前视窗的左侧(边缘)移动到指定的x值。
        lineChart.moveViewToX(lineData.getEntryCount() - 1590);
//        if(flag == 1){
////            ArrayList<Integer> colors = new ArrayList<>();
////            colors.add(Color.BLUE);
////            colors.add(Color.BLACK);
//        }else{
//
//        }
        lineChart.invalidate();

        //重置所有的移动和缩放,并让图表完全适配它的边界(完全缩小).
        //lineChart.fitScreen();

    }
    /**
     * 设置 可以显示X Y 轴自定义值的 MarkerView
     */
//    public void setMarkerView(LineChart lineChart) {
//        LineChartMarkViewDemo mv = new LineChartMarkViewDemo(this);
//        mv.setChartView(lineChart);
//        lineChart.setMarker(mv);
//        lineChart.invalidate();
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // 清空消息
        LineClear(Flag_Chart);
        LineClear(LineChart1);LineClear(LineChart5);
        LineClear(LineChart2);LineClear(LineChart6);
        LineClear(LineChart3);LineClear(LineChart7);
        LineClear(LineChart4);LineClear(LineChart8);

    }
    public void LineClear(LineChart lineChart){
        lineChart.clearAllViewportJobs();
        lineChart.removeAllViewsInLayout();
        lineChart.removeAllViews();
    }

    /**
     * 线程 1：折线图动态添加数据
     *
     */
    public void initWay1(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
//                        while(!queue_flag.isEmpty()){addEntry(Flag_Chart,fLineData,fLineDataSet,queue_flag.poll());}
                        while(!queue1.isEmpty()){addEntry(LineChart1,LineData1,LineDataSet1,(queue1.poll()/mul+MOVE[0]),0);}
                        while(!queue2.isEmpty()){addEntry(LineChart2,LineData2,LineDataSet2,(queue2.poll()/mul+MOVE[1]),1);}
                        while(!queue3.isEmpty()){addEntry(LineChart3,LineData3,LineDataSet3,(queue3.poll()/mul+MOVE[2]),2);}
                        while(!queue4.isEmpty()){addEntry(LineChart4,LineData4,LineDataSet4,(queue4.poll()/mul+MOVE[3]),3);}
                        while(!queue5.isEmpty()){addEntry(LineChart5,LineData5,LineDataSet5,(queue5.poll()/mul+MOVE[4]),4);}
                        while(!queue6.isEmpty()){addEntry(LineChart6,LineData6,LineDataSet6,(queue6.poll()/mul+MOVE[5]),5);}
                        while(!queue7.isEmpty()){addEntry(LineChart7,LineData7,LineDataSet7,(queue7.poll()/mul+MOVE[6]),6);}
                        while(!queue8.isEmpty()){addEntry(LineChart8,LineData8,LineDataSet8,(queue8.poll()/mul+MOVE[7]),7);}
                    }
                },0,400);

            }
        }).start();
    }
    Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            this.obtainMessage(msg.what);
            int order = msg.what;
            switch (order){
                case 0:
                    v1.setText(v_ch[order]);
                    break;
                case 1:
                    v2.setText(v_ch[order]);
                    break;
                case 2:
                    v3.setText(v_ch[order]);
                    break;
                case 3:
                    v4.setText(v_ch[order]);
                    break;
                case 4:
                    v5.setText(v_ch[order]);
                    break;
                case 5:
                    v6.setText(v_ch[order]);
                    break;
                case 6:
                    v7.setText(v_ch[order]);
                    break;
                case 7:
                    v8.setText(v_ch[order]);
                    break;

            }
        }
    };
    public void sendMessage(int order){
        Message msg = new Message();
        msg.what = order;
        mhandler.sendMessage(msg);

    }


    ConcurrentLinkedQueue<Float> queue1_fft = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<Float> queue2_fft = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<Float> queue3_fft = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<Float> queue4_fft = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<Float> queue5_fft = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<Float> queue6_fft = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<Float> queue7_fft = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<Float> queue8_fft = new ConcurrentLinkedQueue<>();

    private final ConcurrentLinkedQueue<Integer> queue_flag = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Float> queue1 = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Float> queue2 = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Float> queue3 = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Float> queue4 = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Float> queue5 = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Float> queue6 = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Float> queue7 = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Float> queue8 = new ConcurrentLinkedQueue<>();

    private final ConcurrentLinkedQueue<Integer> que_hrv = new ConcurrentLinkedQueue<>();

//    public void fft(){
//        ArrayList<Float> ch1 = new ArrayList<>();
//        if(queue1.size()>125){
//            for(float v:queue1){
//                ch1.add(v);
//            }
//        }
//        ArrayList<Float> ch1_fft= FFT.LowPassFFT(ch1,10,35);
//        for(float v:ch1_fft){
//            queue1_fft.add(v);
//        }
//    }

    /**
     * 线程 2：实时滤波
     *
     */
    public void initWay2(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("initway2", "run/: Thread run!");
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
//                                Log.i("initway2", "run/: timer run!");
                                //对数据进行滤波
                                dataFFT(queue1,queue1_fft);
                                dataFFT(queue2,queue2_fft);
                                dataFFT(queue3,queue3_fft);
                                dataFFT(queue4,queue4_fft);
                                dataFFT(queue5,queue5_fft);
                                dataFFT(queue6,queue6_fft);
                                dataFFT(queue7,queue7_fft);
                                dataFFT(queue8,queue8_fft);

                            }
                        },100,100);
            }
        }).start();
    }

    /**
     *
     * 对数据进行滤波
     * @param que 需要滤波的原始数据
     * @param que_fft 存储滤波后数据的队列
     */
    private void dataFFT(Queue<Float> que,Queue<Float> que_fft) {
        if(que.size()>150){
            Log.i("initway2", "run/: que1.size>32!");
            ArrayList<Float> ft = new ArrayList<>();
            for(int i=0;i<128;i++){
                ft.add(que.poll());
            }
            ArrayList<Float> fft= FFT.LowPassFFT(ft,250,35);
            //将滤波后的数据存入ch1_fft，以便做慢波检测
//                                    ch1_fft.addAll(fft);
            for(int i=0;i<fft.size();i++){
                que_fft.add(fft.get(i));
            }
            Log.i("initway2", "run/queue1_fft.size: "+queue1_fft.size());
        }
    }

    //    ArrayList<Float> ch1 = new ArrayList<>();
        ConcurrentLinkedQueue<Float> que1 = new ConcurrentLinkedQueue<>();
    /**
     * 接口继承函数：getArray
     * 功能：接收来自activity的数据
     * 参数：ArrayList<int[]> value, boolean receive
     * 返回值：void
     */

    @Override
    public void getArray(int[] value, boolean receive) {

        isReceive = receive;
        if(value==null){return;}
//        for(float v:value.get(1)){
//                queue_flag.add(1);
//                queue1.add(v);
////                queue1.add((float) ((v*0.5364/10000)+4.5));
//            Log.i("FFT", "queue1_V: "+(float)v);
//            }
        if(channel_on[0]==1){
            for(int i=0;i<value.length;i+=8){
                queue1.add((float)(value[i]*0.54));
//                Log.i(TAG, "getArray: 1通道数据为："+value[i]);
            }
        }
        if(channel_on[1]==1){
            for(int i=1;i<value.length;i+=8){
                queue2.add((float)(value[i]*0.54));
            }
        }
        if(channel_on[2]==1){
            for(int i=2;i<value.length;i+=8){
                queue3.add((float)(value[i]*0.54));
            }
        }
        if(channel_on[3]==1){
            for(int i=3;i<value.length;i+=8){
                queue4.add((float)(value[i]*0.54));
            }
        }
        if(channel_on[4]==1){
//            Log.i(TAG, "switch/getArray: channel 5 on !");
            for(int i=4;i<value.length;i+=8){
                queue5.add((float)(value[i]*0.54));
            }
        }
        if(channel_on[5]==1){
            for(int i=5;i<value.length;i+=8){
                queue6.add((float)(value[i]*0.54));
            }
        }
        if(channel_on[6]==1){
            for(int i=6;i<value.length;i+=8){
                queue7.add((float)(value[i]*0.54));
            }
        }
        if(channel_on[7]==1){
            for(int i=7;i<value.length;i+=8){
                queue8.add((float)(value[i]*0.54));
            }
        if(ifcalhrv){
            Log.i(TAG, "getArray: ifcalhrv = "+ifcalhrv);
            for(int i=7;i<value.length;i+=8){
                que_hrv.add(value[i]*6);
            }
        }

        }
    }
    int bc = 0;
    int[] channel_on = new int[]{0,0,0,0,0,0,0,0};
    boolean ifcalhrv = false;

    public void MyToast(){
        Toast toast = new Toast(fcontext);
        //设置Toast显示位置，居中，向 X、Y轴偏移量均为0
        toast.setGravity(Gravity.CENTER, 300, -150);
        //获取自定义视图
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(fcontext).inflate(R.layout.toast, null);
//        TextView tvMessage = (TextView) view.findViewById(R.id.tv_toast);
//        //设置文本
//        tvMessage.setText(msg);
//        toast.setView(view);
        //设置显示时长
        toast.setDuration(Toast.LENGTH_SHORT);
        //设置视图
        toast.setView(view);
        toast.show();
    }
}