package com.example.testversion.fragment3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.testversion.R;
import com.example.testversion.activity.FFT;
import com.example.testversion.activity.MyDataBaseHelper;
import com.example.testversion.activity.MyDataBaseMethod;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import static com.example.testversion.fragment3.ExcelUtils.UTF8_ENCODING;


public class Fragment3 extends Fragment {

    private FileAdapter fileAdapter;
    Context fcontext;
    CheckBox box_ch1,box_ch2, box_ch3, box_ch4, box_ch5, box_ch6, box_ch7, box_ch8, box_all;

    private MyDataBaseHelper dbhelper;
    private MyDataBaseMethod myDBMethod;
    private SQLiteDatabase db;

    private String DBname = "EEG_DataBase";
    private String tablename;

    private WritableWorkbook wwb;
    private String[] title = {"Date","Channel1","Channel2","Channel3","Channel4","Channel5","Channel6","Channel7","Channel8","time"};
    String TAG = "Fragment3";
    int[] ch_choose = new int[8];
    View view;
    ListView lv = null;

    //创建 左滑出现菜单项的ListView 的实例
    public SwipeMenuListView data_list;
    //创建折线图
    LineChart mlineChart;
    //创建折线数据集
    LineData mlineData;
    //创建点集合
    List<Entry> mEntries1 = new ArrayList<>();
    List<Entry> mEntries2 = new ArrayList<>();
    List<Entry> mEntries3 = new ArrayList<>();
    List<Entry> mEntries4 = new ArrayList<>();
    List<Entry> mEntries5 = new ArrayList<>();
    List<Entry> mEntries6 = new ArrayList<>();
    List<Entry> mEntries7 = new ArrayList<>();
    List<Entry> mEntries8 = new ArrayList<>();
    //创建一条折线的数据集
    LineDataSet mlineDataSet1 = new LineDataSet(mEntries1,"");
    LineDataSet mlineDataSet2 = new LineDataSet(mEntries2,"");
    LineDataSet mlineDataSet3 = new LineDataSet(mEntries3,"");
    LineDataSet mlineDataSet4 = new LineDataSet(mEntries4,"");
    LineDataSet mlineDataSet5 = new LineDataSet(mEntries5,"");
    LineDataSet mlineDataSet6 = new LineDataSet(mEntries6,"");
    LineDataSet mlineDataSet7 = new LineDataSet(mEntries7,"");
    LineDataSet mlineDataSet8 = new LineDataSet(mEntries8,"");
    //创建X轴
    XAxis mXAxis;
    //创建左侧Y轴
    YAxis mLeftYAxis;
    //创建存储数据的队列
    Queue<Float> ch1 = new LinkedList<>();
    Queue<Float> ch2 = new LinkedList<>();
    Queue<Float> ch3 = new LinkedList<>();
    Queue<Float> ch4 = new LinkedList<>();
    Queue<Float> ch5 = new LinkedList<>();
    Queue<Float> ch6 = new LinkedList<>();
    Queue<Float> ch7 = new LinkedList<>();
    Queue<Float> ch8 = new LinkedList<>();
    Queue<String> time = new LinkedList<>();
    Queue<Queue<Float>> DataQueue = new LinkedList<>();

    Queue<Float> ch1_fft = new LinkedList<>();
    Queue<Float> ch2_fft = new LinkedList<>();
    Queue<Float> ch3_fft = new LinkedList<>();
    Queue<Float> ch4_fft = new LinkedList<>();
    Queue<Float> ch5_fft = new LinkedList<>();
    Queue<Float> ch6_fft = new LinkedList<>();
    Queue<Float> ch7_fft = new LinkedList<>();
    Queue<Float> ch8_fft = new LinkedList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_3, container, false);
        this.fcontext = getActivity();
        //实例化
        initView(view);
        //实例化一个MyDataBaseHelper对象
        dbhelper = new MyDataBaseHelper(getContext(),DBname,null,1);
        db = dbhelper.getWritableDatabase();
        //新建一个适配器。用来适配ListView，显示文件列表
        fileAdapter = new FileAdapter(getActivity());
        //扫描指定路径下的文件列表
//        fileAdapter.scanFiles(fcontext.getExternalFilesDir(null).toString());
        fileAdapter.scanDataBase(db);
        //设置适配器，绑定适配器
        data_list.setAdapter(fileAdapter);/*通过BaseAdapter下面的getview()方法显示*/

        /* 建立左滑显示的menu */
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                //添加“上传” item至menu
                SwipeMenuItem importItem = new SwipeMenuItem(getContext());
                importItem.setBackground(new ColorDrawable(Color.rgb(0x56,0x66,0xfc)));
                importItem.setTitle("导出");
                importItem.setWidth(150);
                importItem.setTitleColor(Color.WHITE);
                importItem.setTitleSize(13);
                menu.addMenuItem(importItem);
                //添加“删除”menu至item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xff,0x57,0x22)));
                deleteItem.setTitle("删除");
                deleteItem.setWidth(150);
                deleteItem.setTitleColor(Color.WHITE);
                deleteItem.setTitleSize(13);
                menu.addMenuItem(deleteItem);
            }
        };
        data_list.setMenuCreator(creator);

        /* 建立左滑menu的监听事件 */
       data_list.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
               //position：listview中的item的位置索引； index：menu中的索引
               //获取当前位置的表名
               tablename = fileAdapter.tablename.get(position);

               Log.i("SQLite", "onMenuItemClick/ delete: "+position+"  tablename: "+tablename);
               switch (index){
                   case 0://导出
                       //导出至CSV
                       Log.i(TAG, "onMenuItemClick: tablename = "+tablename);
                       boolean ok;
                       if(ok = myDBMethod.Export_CSV(getContext(),tablename)){
                           Toast.makeText(getContext(),"导出成功",Toast.LENGTH_SHORT).show();
                        }else{
                           Toast.makeText(getContext(),"导出失败",Toast.LENGTH_SHORT).show();
                       }
//                       readDataFromDataBase(tablename);
//                       channelFFT();
//                       myDBMethod.createTable(getContext(),tablename+"_FFT");
//                       myDBMethod.insertData2(getContext(),tablename+"_FFT", DataQueue);
                       break;
                   case 1://删除
//                       myDBMethod.deleteTable(getContext(),tablename);//直接调用MyDataBaseMethod类里的方法会创建一个新数据库，导致找不到要删除的表
                       db.execSQL("drop table "+tablename);
                       Toast.makeText(getContext(),"删除数据表",Toast.LENGTH_SHORT).show();
                       break;
                   default:
                       break;
               }
               dbhelper = new MyDataBaseHelper(getContext(),"EEG_DataBase",null,1);
               db = dbhelper.getWritableDatabase();
               fileAdapter.scanDataBase(db);
               data_list.setAdapter(fileAdapter);
               return false;
           }
       });
        return view;
    }

    private void ExportCSV() throws IOException {
        //创建文件夹
        String foldername = getContext().getExternalFilesDir(null)+"/"+"CSV";
        File folder = new File(foldername);
        if (!folder.exists()){
            folder.mkdir();
        }

        String filename = foldername + "/" + tablename + ".csv";
        File file = new File(filename);
        //从数据库读取数据
        readDataFromDataBase(tablename);
        Log.i(TAG, "ExportExcel: DataQueue.size = "+DataQueue.size());
        // 创建excel表
        createCSV(file);
        //将数据写入excel表
//        dataToExcel(file);
    }

    public void ExportToCSV(Cursor c, String fileName) {


    }

    /**
     * 创建excel表
     */
    public void createCSV(File file) {
        WritableSheet sheet = null;
        try {
            if (file.exists()) {
               file.delete();
            }
            // 创建表
            wwb = Workbook.createWorkbook(file);
            // 创建表单,其中sheet表示该表格的名字,0表示第一个表格,
            sheet = wwb.createSheet("sheet1", 0);
            // 插入表头
            for(int i=0;i<title.length;i++){
                // 第一个参数表示,0表示第一列,第二个参数表示行,同样0表示第一行,第三个参数表示想要添加到单元格里的数据.
                Label lb = new Label(i,0,title[i]);
                // 添加到指定表格里.
                sheet.addCell(lb);
            }
            // 从内存中写入文件中
            wwb.write();
            wwb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * excel表插入数据
     */
    private void dataToExcel(File file) {
        if (DataQueue != null && DataQueue.size() > 0) {
            WritableWorkbook writebook = null;
            InputStream in = null;
            try {
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding(UTF8_ENCODING);
                in = new FileInputStream(file);
                Workbook workbook = Workbook.getWorkbook(in);
                writebook = Workbook.createWorkbook(file,workbook);
                WritableSheet sheet = writebook.getSheet(0);
                int i =0, j = 0;
               while(!DataQueue.isEmpty()) {//当数据条数大于65535时必须重建一张sheet

                    Log.i(TAG, "dataToExcel: DataQueue.size = "+DataQueue.size());
                    Queue<Float> que = DataQueue.poll();
                    while(!que.isEmpty()) {
                        DecimalFormat decimalFormat=new DecimalFormat(".000");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                        String p = decimalFormat.format(que.poll());
                        Label lb = new Label(j+1,i+1,p);
                        sheet.addCell(lb);
//                        sheet.setColumnView(i+1,700);
                        i++;
                    }
                    j++;
                }
                writebook.write();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
    /** Note:
     *  onHiddenChanged————fragment父类继承函数
     *  fragment不在最前端显示时 hidden = true，否则 hidden = false
     **/
    @Override
    public void onHiddenChanged(boolean hidden) {

        Log.i(TAG, "onHiddenChanged: run!");
        if(!hidden){
//            fileAdapter.scanFiles(fcontext.getExternalFilesDir(null).toString());
//            fileAdapter.notifyDataSetChanged();
//            fileAdapter.notifyDataSetInvalidated();
            fileAdapter.scanDataBase(db);
            data_list.setAdapter(fileAdapter);
        }
        super.onHiddenChanged(hidden);
    }

    public void initView(View view){
        myDBMethod = new MyDataBaseMethod(fcontext);
        data_list = (SwipeMenuListView)view.findViewById(R.id.file_list);

//        box_all = view.findViewById(R.id.allS);
        box_ch1 = view.findViewById(R.id.ch1);
        box_ch2 = view.findViewById(R.id.ch2);
        box_ch3 = view.findViewById(R.id.ch3);
        box_ch4 = view.findViewById(R.id.ch4);
        box_ch5 = view.findViewById(R.id.ch5);
        box_ch6 = view.findViewById(R.id.ch6);
        box_ch7 = view.findViewById(R.id.ch7);
        box_ch8 = view.findViewById(R.id.ch8);

        CheckBoxListener mycheckBoxListener = new CheckBoxListener();

        box_ch1.setOnCheckedChangeListener(mycheckBoxListener);
        box_ch2.setOnCheckedChangeListener(mycheckBoxListener);
        box_ch3.setOnCheckedChangeListener(mycheckBoxListener);
        box_ch4.setOnCheckedChangeListener(mycheckBoxListener);
        box_ch5.setOnCheckedChangeListener(mycheckBoxListener);
        box_ch6.setOnCheckedChangeListener(mycheckBoxListener);
        box_ch7.setOnCheckedChangeListener(mycheckBoxListener);
        box_ch8.setOnCheckedChangeListener(mycheckBoxListener);

//        chip = view.findViewById(R.id.chip);
        mlineChart = view.findViewById(R.id.chart);
        //将折线集合加入链表
        //将折线链表载入linedata
        mlineData = new LineData();
        mlineChart.setData(mlineData);

        mXAxis = mlineChart.getXAxis();
        mLeftYAxis = mlineChart.getAxisLeft();
        //设置折线
        setLine(mlineDataSet1,"#E92E63");
        setLine(mlineDataSet2,"#9C27B0");
        setLine(mlineDataSet3,"#651FFF");
        setLine(mlineDataSet4,"#5677FC");
        setLine(mlineDataSet5,"#8BC34A");
        setLine(mlineDataSet6,"#FFC107");
        setLine(mlineDataSet7,"#FF9800");
        setLine(mlineDataSet8,"#FF5722");
        //设置x轴y轴
        setXYAxis();
        //设置折线图
        setChart();
        for(int i=0;i<8;i++){
            showLine(i,false);
        }

    }


    /**
     * 自定义CheckBox的监听器
     */
    class CheckBoxListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                switch (buttonView.getId()){
                    case R.id.ch1:
                        showLine(0,true);
                        break;
                    case R.id.ch2:
                        showLine(1,true);
                        break;
                    case R.id.ch3:
                        showLine(2,true);
                        break;
                    case R.id.ch4:
                        showLine(3,true);
                        break;
                    case R.id.ch5:
                        showLine(4,true);
                        break;
                    case R.id.ch6:
                        showLine(5,true);
                        break;
                    case R.id.ch7:
                        showLine(6,true);
                        break;
                    case R.id.ch8:
                        showLine(7,true);
                        break;
                }
            }
            else{
                switch (buttonView.getId()){
                    case R.id.ch1:
                        showLine(0,false);
                        break;
                    case R.id.ch2:
                        showLine(1,false);
                        break;
                    case R.id.ch3:
                        showLine(2,false);
                        break;
                    case R.id.ch4:
                        showLine(3,false);
                        break;
                    case R.id.ch5:
                        showLine(4,false);
                        break;
                    case R.id.ch6:
                        showLine(5,false);
                        break;
                    case R.id.ch7:
                        showLine(6,false);
                        break;
                    case R.id.ch8:
                        showLine(7,false);
                        break;
                }
            }
        }
    }

    /**
     * 设置flag_chart的x轴 y轴
     */
    public void setXYAxis(){
        mXAxis.setEnabled(false);
        mLeftYAxis.setEnabled(true);
        mLeftYAxis.setAxisMaximum(9);
        mLeftYAxis.setAxisMinimum(0);
        mLeftYAxis.setDrawLabels(true); // 不设置坐标轴数据标签;
        mLeftYAxis.setDrawAxisLine(true); // 不绘制坐标轴线
        mLeftYAxis.setDrawGridLines(false); // 是否绘制网格线
        mLeftYAxis.setLabelCount(9,true);
//        mLeftYAxis.setLabelCount(4, false);
//        yAxis.setDrawTopYLabelEntry(true);
//        yAxis.setDrawGridLinesBehindData(true);
    }


    /**
     * 初始化折线图
     */
    public void setChart() {

        mlineChart.setDoubleTapToZoomEnabled(true);
        // 不显示数据描述
        mlineChart.getDescription().setEnabled(false);
        // 没有数据的时候，显示“暂无数据”
        mlineChart.setNoDataText(" ");
        //设置图标基本属性
        mlineChart.fitScreen();
        mlineChart.setViewPortOffsets(0,0,0,0);
        mlineChart.setPinchZoom(false);//禁止x轴y轴同时进行缩放
//        mlineChart.setScaleEnabled(false);//是否缩放两个轴
        mlineChart.setScaleEnabled(true);//启用/禁用缩放图表上的两个轴，设置为false以禁止通过在其上双击缩放图表,如果这个设为true，则x轴Y轴都能缩放
        mlineChart.setScaleXEnabled(true);//是否可以缩放X轴
        mlineChart.setScaleYEnabled(true);//是否可以缩放Y轴
        mlineChart.setHighlightPerTapEnabled(false);//是否显示在图上的点击位置。设置为false，以防止值由敲击姿态被突出显示。
        mlineChart.getAxisRight().setEnabled(false);//关闭右侧Y轴
        mlineChart.setDrawGridBackground(false);//设置是否绘制网格背景
        mlineChart.setDrawBorders(false);//设置是否显示边界
//        mlineChart.setMaxVisibleValueCount(100);//设置最大可见绘制的 chart count 的数量
        mlineChart.setDragEnabled(true); //是否可以拖动
//        mlineChart.setVisibleXRangeMaximum(100);
        Legend legend = mlineChart.getLegend();
        legend.setEnabled(false);// 这里不显示图例
    }


    /**
     * 初始化折线
     */
    private void setLine(LineDataSet mlineDataSet,String color) {
        // LineDataSet1 = new LineDataSet(null, "");  //添加一个空的 LineData
        mlineDataSet.setDrawFilled(false);
        mlineDataSet.setLineWidth(1.5f); // 设置折线宽度
        mlineDataSet.setColor(Color.parseColor(color));// 设置折线颜色
        mlineDataSet.setDrawValues(false);//是否显示折线上的值
        mlineDataSet.setDrawFilled(false); //设置折线图填充
        mlineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        mlineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);//设置曲线展示为圆滑曲线（如果不设置则默认折线）
        mlineDataSet.setDrawCircles(false);//设置是否显示折线上的圆点，true代表显示
//        Log.i(TAG, "createLine: 初始化折线"+fLineDataSet);
        //判断是否存在折线，如果不存在则创建
        if (mlineData == null) {
            mlineData = new LineData();
            mlineData.addDataSet(mlineDataSet);
            mlineChart.setData(mlineData);
        } else {
            //将折线加入折线集
            mlineChart.getLineData().addDataSet(mlineDataSet);
        }
        mlineChart.invalidate();
    }

    /**
     * 显示或隐藏折线
     * @param index 隐藏折现的索引值
     * @param ifshow 是否显示（true表示显示）
     */
    public void showLine(int index,boolean ifshow){
        mlineChart.getLineData().getDataSets().get(index).setVisible(ifshow);
        mlineChart.invalidate();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        listSetClickListener();
        super.onActivityCreated(savedInstanceState);
        //设置列表点击事件


    }

    /**
     * 文件列表点击事件
     **/
    public void listSetClickListener(){

        data_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

//            boolean[] state = {false,false,false,false,false,false,false,false,false};
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tablename = fileAdapter.tablename.get(position);
                //创建询问是否绘制对话框
                AlertDialog builder = new AlertDialog.Builder(getActivity())
                        .setTitle("是否要绘制 "+tablename+" ？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.i(TAG, "listSetClickListener/PositiveButton: 读取数据！");
                                readDataFromDataBase(tablename);
                                //对数据进行滤波操作
                                channelFFT();
                                //计算每个通道的最大值
                                getMaxValue();
                                //将数据载入折线
                                setDataThread();
                            }
                        }).create();

                builder.show();

                //创建选择通道对话框
//                AlertDialog builder = new AlertDialog.Builder(getActivity())
//                        .setTitle("请选择要绘制的通道：")
//                        .setMultiChoiceItems(R.array.channel, state, new DialogInterface.OnMultiChoiceClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//
//                            }
//                        })
//                        .setPositiveButton("完成", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                                if(lv.getCheckedItemPositions().get(0)){
//                                    ch_choose = new int[]{1,1,1,1,1,1,1,1};
//                                }else{
//                                    for(int v=1;v<9;v++){
//                                        if(lv.getCheckedItemPositions().get(v)){
//                                            ch_choose[v-1] = 1;
//                                        }else{
//                                            ch_choose[v-1] = 0;
//                                        }
//
//                                    }
//                                }
//                                state = new boolean[]{false,false,false,false,false,false,false,false,false};
//                            }
//                        }).create();
//                lv = builder.getListView();
//                builder.show();
//                ch_choose = new int[]{0,0,0,0,0,0,0,0};

                //创建询问是否绘制数据对话框
            }
        });

}

    /**
     * 从数据库读取数据
     * @param tablename 要读取的表名
     */
    ArrayList<float[]> arrayList = new ArrayList<>();
    private void readDataFromDataBase(String tablename) {
        //从数据库中读取数据
        ch1 = myDBMethod.queryData(fcontext,tablename,new String[]{"Channel1"});
        ch2 = myDBMethod.queryData(fcontext,tablename,new String[]{"Channel2"});
        ch3 = myDBMethod.queryData(fcontext,tablename,new String[]{"Channel3"});
        ch4 = myDBMethod.queryData(fcontext,tablename,new String[]{"Channel4"});
        ch5 = myDBMethod.queryData(fcontext,tablename,new String[]{"Channel5"});
        ch6 = myDBMethod.queryData(fcontext,tablename,new String[]{"Channel6"});
        ch7 = myDBMethod.queryData(fcontext,tablename,new String[]{"Channel7"});
        ch8 = myDBMethod.queryData(fcontext,tablename,new String[]{"Channel8"});
//        time = myDBMethod.queryData(fcontext,tablename,new String[]{"Time"});
        Log.i(TAG, "readDataFromDataBase/ch1.size: "+ch1.size());

    }

    public void channelFFT(){
//        Queue<Float> fft = new LinkedList<>();
//        while(ch1.size()>4096){
//            fft = FFT.LowPassFFT(ch1,125,30);
//            fft
//        }
        ch1_fft= FFT.LowPassFFT(ch1,125,30);
        ch2_fft= FFT.LowPassFFT(ch2,125,30);
        ch3_fft= FFT.LowPassFFT(ch3,125,30);
        ch4_fft= FFT.LowPassFFT(ch4,125,30);
        ch5_fft= FFT.LowPassFFT(ch5,125,30);
        ch6_fft= FFT.LowPassFFT(ch6,125,30);
        ch7_fft= FFT.LowPassFFT(ch7,125,30);
        ch8_fft= FFT.LowPassFFT(ch8,125,30);

        DataQueue.add(ch1_fft);
        DataQueue.add(ch2_fft);
        DataQueue.add(ch3_fft);
        DataQueue.add(ch4_fft);
        DataQueue.add(ch5_fft);
        DataQueue.add(ch6_fft);
        DataQueue.add(ch7_fft);
        DataQueue.add(ch8_fft);

        Log.i(TAG, "channelFFT/ch1_fft.size: "+ch1_fft.size());
    }
    /**
     * 将最大值装入数组
     */
    float[] chMax = new float[8];
    public void getMaxValue(){
        chMax[0] = getChannelMax(ch1_fft);
        chMax[1] = getChannelMax(ch2_fft);
        chMax[2] = getChannelMax(ch3_fft);
        chMax[3] = getChannelMax(ch4_fft);
        chMax[4] = getChannelMax(ch5_fft);
        chMax[5] = getChannelMax(ch6_fft);
        chMax[6] = getChannelMax(ch7_fft);
        chMax[7] = getChannelMax(ch8_fft);
    }

    /**
     * 获取每个队列的最大值
     * @param que 进行操作的队列
     * @return 最大值
     */
    public float getChannelMax(Queue<Float> que){
        Float max = que.peek();
        Iterator it1 = que.iterator();
        while(it1.hasNext()){
            Float v = (float)it1.next();
            if(v > max && v!=0){
                max = v;
            }
        }
        return max;
    }
    /**
     * 将数据库数据载入折线图
     */
    public void setDataThread(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int i=0;
                //清空上一次存储的数据
                mEntries1.clear();
                mEntries2.clear();
                mEntries3.clear();
                mEntries4.clear();
                mEntries5.clear();
                mEntries6.clear();
                mEntries7.clear();
                mEntries8.clear();
                //载入新的数据
                while(!ch1_fft.isEmpty()){
                    Entry l1 = new Entry(i,(float) (ch1_fft.poll()*0.5364/chMax[0]+7.5));
                    mlineData.addEntry(l1,0);
                    Log.i(TAG, "setData/mEntries1.size: "+mEntries1.size());
                    Entry l2 = new Entry(i,(float) (ch2_fft.poll()*0.5364/chMax[1]+6.5));
                    mlineData.addEntry(l2,1);
                    Entry l3 = new Entry(i,(float) (ch3_fft.poll()*0.5364/chMax[2]+5.5));
                    mlineData.addEntry(l3,2);
                    Entry l4 = new Entry(i,(float) (ch4_fft.poll()*0.5364/chMax[3]+4.5));
                    mlineData.addEntry(l4,3);
                    Entry l5 = new Entry(i,(float) (ch5_fft.poll()*0.5364/chMax[4]+3.5));
                    mlineData.addEntry(l5,4);
                    Entry l6 = new Entry(i,(float) (ch6_fft.poll()*0.5364/chMax[5]+2.5));
                    mlineData.addEntry(l6,5);
                    Entry l7 = new Entry(i,(float) (ch7_fft.poll()*0.5364/chMax[6]+1.5));
                    mlineData.addEntry(l7,6);
                    Entry l8 = new Entry(i,(float) (ch8_fft.poll()*0.5364/chMax[7]+0.5));
                    mlineData.addEntry(l8,7);
                    i++;
                }

                for(int v=0;i<8;i++){
                    mlineChart.getLineData().getDataSets().get(v).setVisible(false);
                }
                mlineData.notifyDataChanged();
                mlineChart.notifyDataSetChanged();
                mlineChart.invalidate();
            }
        };
        runnable.run();
    }

    public void setDataToLine(){

        if (mlineChart.getData() != null &&
                mlineChart.getData().getDataSetCount() > 04) {
            mlineDataSet1 = (LineDataSet) mlineChart.getData().getDataSetByIndex(0);
            mlineDataSet2 = (LineDataSet) mlineChart.getData().getDataSetByIndex(1);
            mlineDataSet1.setValues(mEntries1);
            mlineChart.getData().notifyDataChanged();
            mlineChart.notifyDataSetChanged();
        } else {
            mlineDataSet1 = new LineDataSet(mEntries1,"");
            mlineDataSet2 = new LineDataSet(mEntries2,"");
            mlineDataSet3 = new LineDataSet(mEntries3,"");
            mlineDataSet4 = new LineDataSet(mEntries4,"");
            mlineDataSet5 = new LineDataSet(mEntries5,"");
            mlineDataSet6 = new LineDataSet(mEntries6,"");
            mlineDataSet7 = new LineDataSet(mEntries7,"");
            mlineDataSet8 = new LineDataSet(mEntries8,"");

            setLine(mlineDataSet1,"#E92E63");
            setLine(mlineDataSet2,"#9C27B0");
            setLine(mlineDataSet3,"#651FFF");
            setLine(mlineDataSet4,"#5677FC");
            setLine(mlineDataSet5,"#8BC34A");
            setLine(mlineDataSet6,"#FFC107");
            setLine(mlineDataSet7,"#FF9800");
            setLine(mlineDataSet8,"#FF5722");

//            lineDataSets.add(mlineDataSet1);
//            lineDataSets.add(mlineDataSet2);
//            lineDataSets.add(mlineDataSet3);
//            lineDataSets.add(mlineDataSet4);
//            lineDataSets.add(mlineDataSet5);
//            lineDataSets.add(mlineDataSet6);
//            lineDataSets.add(mlineDataSet7);
//            lineDataSets.add(mlineDataSet8);
//
//            LineData data = new LineData(lineDataSets);
//            mlineChart.setData(data);
        }
    }




}