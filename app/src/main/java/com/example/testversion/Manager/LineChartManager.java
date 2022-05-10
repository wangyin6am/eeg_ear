package com.example.testversion.Manager;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class LineChartManager {
    String TAG = "LineChartManager";
    LineChart lineChart;
    private String[] YAxis = {"","N3","N2","N1","REM","Wake",""};

    /**
     * 构造函数-1
     * @param lineChart
     */
    public LineChartManager(LineChart lineChart) {
        this.lineChart = lineChart;
        Log.i(TAG, "LineChartManager1");
        initLineChart1(lineChart);
        setXAxis(lineChart);
//        setYAxis2(lineChart,0);
        setYAxis(lineChart);
    }

    /**
     * 构造函数-2
     * @param lineChart
     * @param limitheight
     */
    public LineChartManager(LineChart lineChart,float limitheight) {
        this.lineChart = lineChart;
        Log.i(TAG, "LineChartManager2");
//        this.lineChart = lineChart;
        initLineChart2(lineChart);
        setXAxis(lineChart);
        setYAxis2(lineChart,limitheight);
    }

    private void initLineChart1(LineChart lineChart) {
        //创建描述信息
        Description description = new Description();
        description.setEnabled(false);
        lineChart.setDescription(description);//设置图表描述信息
        lineChart.setNoDataText("没有数据");//没有数据时显示的文字
        lineChart.setNoDataTextColor(Color.BLUE);//没有数据时显示文字的颜色
        lineChart.setDrawGridBackground(false);//chart 绘图区后面的背景矩形将绘制
        lineChart.setDrawBorders(false);//禁止绘制图表边框的线
        lineChart.setTouchEnabled(true); // 设置是否可以触摸,如果设置为false则无法交互
        lineChart.setDragEnabled(true);// 是否可以拖拽
        lineChart.setScaleXEnabled(true);//是否可以缩放X轴
        lineChart.setScaleYEnabled(false);//是否可以缩放Y轴
        lineChart.setScaleEnabled(true);// 是否可以缩放x和y轴, 默认是true
        lineChart.setHighlightPerTapEnabled(false);//是否显示在图上的点击位置。设置为false，以防止值由敲击姿态被突出显示。
//        lineChart.setPinchZoom(false);  //设置x轴和y轴能否同时缩放。默认是否
        lineChart.setDoubleTapToZoomEnabled(false);//设置是否可以通过双击屏幕放大图表。默认是true
        lineChart.setHighlightPerDragEnabled(false);//能否拖拽高亮线(数据点与坐标的提示线)，默认是true
        lineChart.setDragDecelerationEnabled(false);//拖拽滚动时，手放开是否会持续滚动，默认是true（false是拖到哪是哪，true拖拽之后还会有缓冲）
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);// 这里不显示图例

        //lineChart.setBorderColor(); //设置 chart 边框线的颜色。
        //lineChart.setBorderWidth(); //设置 chart 边界线的宽度，单位 dp。
        //lineChart.setLogEnabled(true);//打印日志
        //lineChart.notifyDataSetChanged();//刷新数据
        //lineChart.invalidate();//重绘
        // lineChart.setDragDecelerationFrictionCoef(0.99f);//与上面那个属性配合，持续滚动时的速度快慢，[0,1) 0代表立即停止。

    }
    private void initLineChart2(LineChart lineChart) {
        //创建描述信息
        Description description = new Description();
        description.setEnabled(false);
        lineChart.setDescription(description);//设置图表描述信息
        lineChart.setNoDataText("没有数据");//没有数据时显示的文字
        lineChart.setNoDataTextColor(Color.BLUE);//没有数据时显示文字的颜色
        lineChart.setDrawGridBackground(false);//chart 绘图区后面的背景矩形将绘制
        lineChart.setDrawBorders(true);//禁止绘制图表边框的线
        lineChart.setTouchEnabled(true); // 设置是否可以触摸,如果设置为false则无法交互
        lineChart.setDragEnabled(true);// 是否可以拖拽
        lineChart.setScaleXEnabled(true);//是否可以缩放X轴
        lineChart.setScaleYEnabled(true);//是否可以缩放Y轴
        lineChart.setScaleEnabled(true);// 是否可以缩放x和y轴, 默认是true
        lineChart.setHighlightPerTapEnabled(false);//是否显示在图上的点击位置。设置为false，以防止值由敲击姿态被突出显示。
//        lineChart.setPinchZoom(false);  //设置x轴和y轴能否同时缩放。默认是否
        lineChart.setDoubleTapToZoomEnabled(false);//设置是否可以通过双击屏幕放大图表。默认是true
        lineChart.setHighlightPerDragEnabled(false);//能否拖拽高亮线(数据点与坐标的提示线)，默认是true
        lineChart.setDragDecelerationEnabled(false);//拖拽滚动时，手放开是否会持续滚动，默认是true（false是拖到哪是哪，true拖拽之后还会有缓冲）
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);// 这里不显示图例

        //lineChart.setBorderColor(); //设置 chart 边框线的颜色。
        //lineChart.setBorderWidth(); //设置 chart 边界线的宽度，单位 dp。
        //lineChart.setLogEnabled(true);//打印日志
        //lineChart.notifyDataSetChanged();//刷新数据
        //lineChart.invalidate();//重绘
        // lineChart.setDragDecelerationFrictionCoef(0.99f);//与上面那个属性配合，持续滚动时的速度快慢，[0,1) 0代表立即停止。

    }
    private void setXAxis(LineChart lineChart) {
        //获取此图表的x轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setEnabled(false);//设置轴启用或禁用 如果禁用以下的设置全部不生效
        xAxis.setDrawAxisLine(false);//是否绘制轴线
        xAxis.setDrawGridLines(true);//设置x轴上每个点对应的线
        xAxis.setDrawLabels(true);//绘制标签  指x轴上的对应数值
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
        xAxis.setGranularity(125f);
        xAxis.setAvoidFirstLastClipping(true);//图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
//        xAxis.setLabelRotationAngle(10f);//设置x轴标签的旋转角度

        //xAxis.setTextSize(20f);//设置字体
        //xAxis.setTextColor(Color.BLACK);//设置字体颜色
        //设置竖线的显示样式为虚线
        //lineLength控制虚线段的长度
        //spaceLength控制线之间的空间
//        xAxis.enableGridDashedLine(10f, 10f, 0f);
//        xAxis.setAxisMinimum(0f);//设置x轴的最小值
//        xAxis.setAxisMaximum(10f);//设置最大值

//        设置x轴显示标签数量  还有一个重载方法]r.GRAY);//设置轴标签的颜色
        xAxis.setTextSize(5f);//设置轴标签的大小
        //自定义x轴标签显示格式
//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                String hour,min,sec,Time;
//                int start_h=22,start_m=0,start_s=0;
//                int s,m,h;
//                //小时数
//                h = (int)value/(125*60*60);
//                //分钟数
//                m = (int)value/(125*60)-h*60;
//                //秒数
//                s = (int)(value/125-h*3600-m*60);
//                //判断秒数
//                if(s>=60-start_s){
//                    sec = Integer.toString(s-60+start_s);
//                    m+=1;
//                }else {sec = Integer.toString(start_s+s);}
//                if(Integer.parseInt(sec)<10){
//                    sec += "0";
//                }
//                //判断分钟数
//                if(m>=60-start_m){
//                    min = Integer.toString(m-60+start_m);
//                    h+=1;
//                }else {min = Integer.toString(start_m+m);}
//                if(Integer.parseInt(min)<10){
//                    min += "0";
//                }
//                //判断小时数
//                if(h>=24-start_h){
//                    hour = Integer.toString(h-24+start_h);
//                }else {hour = "0"+Integer.toString(h);}
//                if(Integer.parseInt(hour)<10){
//                    hour += "0";
//                }
//
//                Time = hour+":"+min+":"+sec;
//                return Time;
//            }
//        });
//        xAxis.setGridLineWidth(10f);//设置竖线大小
//        xAxis.setGridColor(Color.RED);//设置竖线颜色
//        xAxis.setAxisLineColor(Color.GREEN);//设置x轴线颜色
//        xAxis.setAxisLineWidth(5f);//设置x轴线宽度
    }

    private void setYAxis(LineChart lineChart) {
        /**
         * Y轴默认显示左右两个轴线
         */
        //获取右边的轴线
        YAxis rightAxis = lineChart.getAxisRight();
        //设置图表右边的y轴禁用
        rightAxis.setEnabled(false);
        //获取左边的轴线
        YAxis leftAxis = lineChart.getAxisLeft();
//        leftAxis.setDrawAxisLine(true);//是否绘制轴线
        //设置网格线为虚线效果
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        //是否绘制0所在的网格线
        leftAxis.setDrawZeroLine(false);
        leftAxis.setAxisMaximum(6);
        leftAxis.setAxisMinimum(0);
        leftAxis.setLabelCount(6, false);
//        leftAxis.setYOffset(20f);
        leftAxis.setValueFormatter(new IAxisValueFormatter()
        {
            @Override
            public String getFormattedValue(float value, AxisBase axis)
            {
                return YAxis[(int) value];
            }
        });
    }
    private void setYAxis2(LineChart lineChart,float limitHeight) {
        /**
         * Y轴默认显示左右两个轴线
         */
        //获取右边的轴线
        YAxis rightAxis = lineChart.getAxisRight();
        //设置图表右边的y轴禁用
        rightAxis.setEnabled(false);
        //获取左边的轴线
        YAxis leftAxis = lineChart.getAxisLeft();
//        leftAxis.setDrawAxisLine(true);//是否绘制轴线
        //设置网格线为虚线效果
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setLabelCount(4);
        //是否绘制0所在的网格线
        leftAxis.setDrawZeroLine(false);
        if(limitHeight != -1){
            heightLimit(limitHeight,leftAxis);
        }

    }

    /**
     * 绘制一条线
     * @param lineChart
     * @param values
     */
    public void showLineChart(LineChart lineChart,ArrayList<Entry> values) {
        //LineDataSet每一个对象就是一条连接线
        LineDataSet lineDataSet;

        //判断图表中原来是否有数据
        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            //获取数据
            lineDataSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            lineDataSet.setValues(values);
            //刷新数据
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            //设置数据1  参数1：数据源 参数2：图例名称
            lineDataSet = new LineDataSet(values,"");
            lineDataSet.setColor(Color.BLACK);
            lineDataSet.setCircleColor(Color.BLACK);
            lineDataSet.setLineWidth(1f);//设置线宽
            lineDataSet.setDrawCircles(false);//设置是否显示折线上的圆点，true代表显示
            lineDataSet.setDrawFilled(false);//设置禁用范围背景填充

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(lineDataSet); // add the datasets
            //创建LineData对象 属于LineChart折线图的数据集合
            LineData lineData = new LineData(dataSets);
            // 添加到图表中
            lineChart.setData(lineData);
            //设置是否显示折现上的数据
            lineData.setDrawValues(true);
            //绘制图表
            lineChart.invalidate();
//            lineDataSet.enableDashedHighlightLine(10f, 5f, 0f);//点击后的高亮线的显示样式
//            lineDataSet.setHighlightEnabled(true);//是否禁用点击高亮线
//            lineDataSet.setHighLightColor(Color.RED);//设置点击交点后显示交高亮线的颜色
//            lineDataSet.setValueTextSize(9f);//设置显示值的文字大小

        }
    }

    /**
     * 绘制两条线
     * @param lineChart
     * @param values1
     * @param values2
     */
    public void showLineChart2(LineChart lineChart,ArrayList<Entry> values1,ArrayList<Entry> values2) {
        //LineDataSet每一个对象就是一条连接线
        LineDataSet lineDataSet1;
        LineDataSet lineDataSet2;
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        //绘制第一条线
        //判断图表中原来是否有数据
        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            //获取数据
            lineDataSet1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            lineDataSet1.setValues(values1);
            //刷新数据
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            //设置数据1  参数1：数据源 参数2：图例名称
            lineDataSet1 = new LineDataSet(values1,"");
            lineDataSet1.setColor(Color.BLACK);
            lineDataSet1.setCircleColor(Color.BLACK);
            lineDataSet1.setLineWidth(1f);//设置线宽
            lineDataSet1.setDrawCircles(false);//设置是否显示折线上的圆点，true代表显示
            lineDataSet1.setDrawFilled(false);//设置禁用范围背景填充
        }
        //绘制第二条线
        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            //获取数据
            lineDataSet2 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            lineDataSet2.setValues(values2);
            //刷新数据
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            //设置数据1  参数1：数据源 参数2：图例名称
            lineDataSet2 = new LineDataSet(values2,"");
            lineDataSet2.setColor(Color.RED);
            lineDataSet2.setCircleColor(Color.RED);
            lineDataSet2.setLineWidth(1f);//设置线宽
            lineDataSet2.setDrawCircles(false);//设置是否显示折线上的圆点，true代表显示
            lineDataSet2.setDrawFilled(false);//设置禁用范围背景填充
        }

        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);
        //创建LineData对象 属于LineChart折线图的数据集合
        LineData lineData = new LineData(dataSets);
        // 添加到图表中
        lineChart.setData(lineData);
        //设置是否显示折现上的数据
        lineData.setDrawValues(true);
        //绘制图表
        lineChart.invalidate();
//            lineDataSet.enableDashedHighlightLine(10f, 5f, 0f);//点击后的高亮线的显示样式
//            lineDataSet.setHighlightEnabled(true);//是否禁用点击高亮线
//            lineDataSet.setHighLightColor(Color.RED);//设置点击交点后显示交高亮线的颜色
//            lineDataSet.setValueTextSize(9f);//设置显示值的文字大小


    }

    /**
     * 不同折点不同颜色
     * @param lineChart
     * @param Entrys
     */
    public void showLineChart3(LineChart lineChart,ArrayList<Entry> Entrys,ArrayList<Integer> colors) {
        Log.i(TAG, "showLineChart3: 绘制LineChart！");
        //LineDataSet每一个对象就是一条连接线
        LineDataSet lineDataSet;

        //判断图表中原来是否有数据
        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            //获取数据
            lineDataSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            lineDataSet.setValues(Entrys);
            //刷新数据
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            //设置数据1  参数1：数据源 参数2：图例名称
            lineDataSet = new LineDataSet(Entrys,"");
//            lineDataSet.setColor(Color.BLACK);
            lineDataSet.setColors(colors);
//            lineDataSet.setCircleColor(Color.BLACK);
//            lineDataSet.setCircleColors(colors);
            lineDataSet.setLineWidth(1f);//设置线宽
            lineDataSet.setCircleRadius(1f);
            lineDataSet.setDrawCircles(false);//设置是否显示折线上的圆点，true代表显示
            lineDataSet.setDrawFilled(false);//设置禁用范围背景填充

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(lineDataSet); // add the datasets
            //创建LineData对象 属于LineChart折线图的数据集合
            LineData lineData = new LineData(dataSets);
            // 添加到图表中
            lineChart.setData(lineData);
            //设置是否显示折现上的数据
            lineData.setDrawValues(true);
            //绘制图表
            lineChart.invalidate();

//            lineDataSet.enableDashedHighlightLine(10f, 5f, 0f);//点击后的高亮线的显示样式
//            lineDataSet.setHighlightEnabled(true);//是否禁用点击高亮线
//            lineDataSet.setHighLightColor(Color.RED);//设置点击交点后显示交高亮线的颜色
//            lineDataSet.setValueTextSize(9f);//设置显示值的文字大小

        }
    }

    /**
     * 设置限制线
     * @param height
     * @param yAxis
     */
    public void heightLimit(float height,YAxis yAxis){
        LimitLine limitLine = new LimitLine(height,"threshold");
        //设置警告线的的位置，LimitLabelPosition枚举值
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        //设置警告线的名称
        limitLine.setLabel(Float.toString(height));
        //设置警告线的颜色
        limitLine.setLineColor(Color.rgb(255,193,37));
        //设置警告线的宽度
        limitLine.setLineWidth(1f);
        //是否启用
        limitLine.setEnabled(true);
        //设置警告线上文本的颜色
        limitLine.setTextColor(Color.rgb(255,193,37));
        //设置警告线上文本的字体大小
        limitLine.setTextSize(8f);
        //设置警告线上文本的字体类型，如字体加粗等
        limitLine.setTypeface(Typeface.DEFAULT_BOLD);
        //设置警告线在x轴上的偏移量
//        limitLine.setXOffset();
        yAxis.addLimitLine(limitLine);
    }
}
