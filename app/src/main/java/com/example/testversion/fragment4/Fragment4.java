package com.example.testversion.fragment4;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.testversion.Manager.PieChartManagger;
import com.example.testversion.R;
import com.example.testversion.Manager.LineChartManager;
import com.example.testversion.fragment3.FileAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Fragment4 extends Fragment implements View.OnClickListener {

    private static final String TAG="Fragment4";
    SendResult sendResult;
    Handler mHandler;
    Button model,export;
    TextView totaltime_w,totaltime_rem,totaltime_n1,totaltime_n2,totaltime_n3;
    TextView percent_w,percent_rem,percent_n1,percent_n2,percent_n3;
    TextView epochnum_w,epochnum_rem,epochnum_n1,epochnum_n2,epochnum_n3;
    TextView duration_w,duration_rem,duration_n1,duration_n2,duration_n3;
    TextView avgduration_w,avgduration_rem,avgduration_n1,avgduration_n2,avgduration_n3;
    TextView filename;

    TextView[] totaltime = new TextView[5];
    TextView[] percent = new TextView[5];
    TextView[] durationnum = new TextView[5];
    TextView[] duration = new TextView[5];
    TextView[] avgduration = new TextView[5];

    SleepStage sleepStage = new SleepStage();
    private FileAdapter fileAdapter;

    ArrayList<Integer> result;//存储模型分类结果的数组

    private PieChart pieChart;
    private LineChart lineChart;


    private List<File> fileList = new LinkedList<>();//指定路径下的文件列表
    private File selectedFile;//要进行睡眠分期的文件



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_4, container, false);
        initview(view);
        fileAdapter = new FileAdapter(getActivity());
        return view;

    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setListeners();
          mHandler = new Handler(){
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(@SuppressLint("HandlerLeak") Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        Toast.makeText(getActivity(),msg.obj.toString(),Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(getActivity(),msg.obj.toString(),Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(getActivity(),msg.obj.toString(),Toast.LENGTH_SHORT).show();
                        filename.setText(" - "+selectedFile.getName());
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + msg.what);
                }
                }

         };
          //对接口进行实例化，否则会报空指针异常
          sendResult =(SendResult)getContext();
        super.onActivityCreated(savedInstanceState);
    }
    public void setListeners(){
        model.setOnClickListener(this);
    }

    public void initview(View view){
        model = view.findViewById(R.id.sleepstage);
        export = view.findViewById(R.id.export);

        pieChart = view.findViewById(R.id.pieChart);
        lineChart = view.findViewById(R.id.linechart);
        filename = view.findViewById(R.id.filename);

        totaltime_w = view.findViewById(R.id.totaltime_w);      totaltime[0] = totaltime_w;
        totaltime_n1 = view.findViewById(R.id.totaltime_N1);    totaltime[1] = totaltime_n1;
        totaltime_n2 = view.findViewById(R.id.totaltime_N2);    totaltime[2] = totaltime_n2;
        totaltime_n3 = view.findViewById(R.id.totaltime_N3);    totaltime[3] = totaltime_n3;
        totaltime_rem = view.findViewById(R.id.totaltime_rem);  totaltime[4] = totaltime_rem;

        percent_w = view.findViewById(R.id.percent_w);      percent[0] = percent_w;
        percent_n1 = view.findViewById(R.id.percent_N1);    percent[1] = percent_n1;
        percent_n2 = view.findViewById(R.id.percent_N2);    percent[2] = percent_n2;
        percent_n3 = view.findViewById(R.id.percent_N3);    percent[3] = percent_n3;
        percent_rem = view.findViewById(R.id.percent_rem);  percent[4] = percent_rem;

        epochnum_w = view.findViewById(R.id.epochnum_w);        durationnum[0] = epochnum_w;
        epochnum_n1 = view.findViewById(R.id.epochnum_N1);      durationnum[1] = epochnum_n1;
        epochnum_n2 = view.findViewById(R.id.epochnum_N2);      durationnum[2] = epochnum_n2;
        epochnum_n3 = view.findViewById(R.id.epochnum_N3);      durationnum[3] = epochnum_n3;
        epochnum_rem = view.findViewById(R.id.epochnum_rem);    durationnum[4] = epochnum_rem;

        duration_w = view.findViewById(R.id.duration_w);        duration[0] = duration_w;
        duration_n1 = view.findViewById(R.id.duration_N1);      duration[1] = duration_n1;
        duration_n2 = view.findViewById(R.id.duration_N2);      duration[2] = duration_n2;
        duration_n3 = view.findViewById(R.id.duration_N3);      duration[3] = duration_n3;
        duration_rem = view.findViewById(R.id.duration_rem);    duration[4] = duration_rem;

        avgduration_w = view.findViewById(R.id.avgduration_w);      avgduration[0] = avgduration_w;
        avgduration_n1 = view.findViewById(R.id.avgduration_N1);    avgduration[1] = avgduration_n1;
        avgduration_n2 = view.findViewById(R.id.avgduration_N2);    avgduration[2] = avgduration_n2;
        avgduration_n3 = view.findViewById(R.id.avgduration_N3);    avgduration[3] = avgduration_n3;
        avgduration_rem = view.findViewById(R.id.avgduration_rem);  avgduration[4] = avgduration_rem;

//        showRingPieChart();
//        showLineChart();
    }


    /**
     * 设置点击事件
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sleepstage:
                //扫描指定路径下的文件列表
                fileList = fileAdapter.scanFiles(getActivity().getExternalFilesDir(null).toString());
                //弹出文件选择对话框
                showListDialog();
                break;
            case R.id.export:
                //创建保存分期结果的文件夹
                createFolder();
                //导出分期结果
                exportPolysomnography();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    /**
     * 创建选择文件夹，名字同分析睡眠数据
     */
    private  void createFolder(){
        //创建文件夹
        String foldername = getContext().getExternalFilesDir(null)+"/"+"Polysomnography";
        File folder = new File(foldername);
        if (!folder.exists()){
            folder.mkdir();
        }
    }

    /**
     * 导出分期结果，文件名同数据文件
     */
    private void exportPolysomnography(){
        String export_filename = getContext().getExternalFilesDir(null)+"/"+"Polysomnography/"+selectedFile.getName()+".txt";
        File file = new File(export_filename);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            for(int v:result){
                outputStream.write((v+" ").getBytes());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    /**
     * 弹出文件选择对话框
     * 选择要分析的文件
     */
    private void showListDialog() {
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(getActivity());
        listDialog.setTitle("请选择要分析的文件：");
        List<Map<String, Object>> listItems = new ArrayList<>();
        for (File file:fileList) {
            Map<String, Object> maps = new HashMap<>();
            maps.put("文件名", file.getName());
            maps.put("文件大小",FileAdapter.getFileSize(file));
            listItems.add(maps);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),listItems
                ,R.layout.filelist_item,new String[]{"文件名","文件大小"},new int[]{R.id.table_name,R.id.file_size});
        listDialog.setAdapter(simpleAdapter, (dialogInterface, which) -> showConfirmDialog(which));
        listDialog.show();
    }

    /**
     * 弹出确认对话框
     * 确认刚才选择的文件
     * @param which 文件列表的位置
     */
    private void showConfirmDialog(int which){
        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(getActivity());
        selectedFile = fileList.get(which);
        //创建一个新的对话框，确认文件选择
        confirmDialog.setMessage("是否对文件"+selectedFile.getName()+"进行分析？");
        confirmDialog.setPositiveButton("确定", (dialogInterface, i) -> showChannelDialog(selectedFile));
        confirmDialog.setNegativeButton("取消", (dialogInterface, i) -> showListDialog());
        confirmDialog.show();
    }

    /**
     * 弹出通道选择对话框
     * 用来获取C3、C4、EOGL、EOGR、EMG对应的位置
     */
    EditText edit_C3,edit_C4,edit_EOGL,edit_EOGR,edit_EMG;
    int[] ch_line = new int[5];//C3,C4,EOGL,EOGR,EMG;
    public void showChannelDialog(File file){
        AlertDialog.Builder channelDialog = new AlertDialog.Builder(getActivity());
        final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_channel,null);
        channelDialog.setTitle("请指定以下信号对应的通道(从0开始计数)：");
        channelDialog.setView(dialogView);
        channelDialog.setPositiveButton("确定",
                (DialogInterface.OnClickListener) (dialog, which) -> {
                    // 获取EditView中的输入内容

                    edit_C3 =  dialogView.findViewById(R.id.c3);
                    edit_C4 =  dialogView.findViewById(R.id.c4);
                    edit_EOGL =  dialogView.findViewById(R.id.eogl);
                    edit_EOGR =  dialogView.findViewById(R.id.eogr);
                    edit_EMG =  dialogView.findViewById(R.id.emg);

                    ch_line[0] = Integer.parseInt(edit_C3.getText().toString());
                    ch_line[1] = Integer.parseInt(edit_C4.getText().toString());
                    ch_line[2] = Integer.parseInt(edit_EOGL.getText().toString());
                    ch_line[3] = Integer.parseInt(edit_EOGR.getText().toString());
                    ch_line[4] = Integer.parseInt(edit_EMG.getText().toString());

                    readFile(file,ch_line);
                });
        channelDialog.show();
    }

    /**
     * 读取文件对应通道的数据
     * @param file 读取的文件
     * @param ch_line 需要读取的行数
     */
    public void readFile(File file,int[] ch_line){
        new Thread(() -> {
            Message msg = new Message();
            msg.what = 0;
            msg.obj = "正在读取文件......";
            mHandler.sendMessage(msg);
            FileReader fileReader;
//            float[][] DATA = new float[5][];
//            ArrayList<float[]> AllData = new ArrayList<>();
            double[][] DATA = new double[5][];
            ArrayList<double[]> AllData = new ArrayList<>();
            try {
                fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String tmp;
                while((tmp = bufferedReader.readLine())!=null){//按行读取
                    //以空格分割
                    String[] str = tmp.split("\\s+");
//                    float[] data = new float[str.length];
                    double[] data = new double[str.length];
                    for(int i=0;i<str.length;i++){
                        if(!str[i].equals("")){
                            //转换成float格式
//                            data[i] = Float.parseFloat(str[i]);
                            //转换成double格式
                            data[i]=Double.parseDouble(str[i]);
                        }
                    }
                    AllData.add(data);
//                        if (l==flag){//判断是否为指定通道
//                            DATA[k] = data;
//                            Log.i(TAG, "run: 传入第"+l+"通道");
//                            k++;
//                            if(k<ch_line.length){
//                                flag = ch_line[k];
//                            }else {break;}
//                        }
//                        Log.i(TAG, "readFile: data.length="+data.length);
//                        l++;
                }
                for(int i=0;i<DATA.length;i++){
                    DATA[i] = AllData.get(ch_line[i]);
                    Log.i(TAG, "run: 传入第"+ch_line[i]+"通道");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //传入从文件中读取的数据开始阶段划分
            sleepStageDetection(DATA);
        }).start();
    }

    /**
     * 调用模型进行睡眠分期
     * @param DATA 传入5个通道的数组
     */
    public void sleepStageDetection(double[][] DATA){
//        Toast.makeText(getActivity(),"正在分析，请稍后......",Toast.LENGTH_LONG).show();
        //开启新的线程进行数据分析
        Message message = new Message();
        message.obj = "正在分析，请稍后......";
        message.what = 1;
        mHandler.sendMessage(message);
        //调用深度学习模型计算每个时段的分期
        result = sleepStage.stageDection(DATA,getActivity(),125);
        //通过接口给fragment5传递数据，做波形检测
        sendResult.getPSG(DATA[0],result);
        //对分期结果进行后处理，消除毛刺
        ArrayList<Integer> repro_result1 = reProcessing(result);
        ArrayList<Integer> repro_result2 = reProcessing(repro_result1);
        //绘制睡眠分期图
        showLineChart(repro_result2);
        //得到每个睡眠阶段的占比
        float[] per_stage = calStageInfo(repro_result2);
        //绘制统计图
        showRingPieChart(per_stage);
        //填入表格数据
        setTabelText();
    }

    /**
     * 分期结果后处理函数
     * @param raw 分期后的结果链表
     * @return 后处理过的分期结果链表
     */
    public ArrayList<Integer> reProcessing(ArrayList<Integer> raw){
        int right;
        int middle;
        int left;
        for(int i=1;i<raw.size()-2;i++){
            right = raw.get(i-1);
            middle = raw.get(i);
            left = raw.get(i+2);
            if((middle!=right)&&(right == left)){
                raw.set(i,left);
                raw.set(i+1,left);
            }
        }
        return raw;
    }


    /**
     * 绘制睡眠阶段图LineChart
     * @param result 分期的结果数组
     */
    private void showLineChart(ArrayList<Integer> result){
        ArrayList<Entry> values = new ArrayList<>();
        int x=0;
        for(int y:result){
            switch (y){
                case 0:
                    values.add(new Entry(x,5));
                    break;
                case 1:
                    values.add(new Entry(x,3));
                    break;
                case 2:
                    values.add(new Entry(x,2));
                    break;
                case 3:
                    values.add(new Entry(x,1));
                    break;
                case 4:
                    values.add(new Entry(x,4));
                    break;
            }
            x++;
        }
        Log.i(TAG, "showLineChart: value.size="+values.size());
        LineChartManager lineChartManager = new LineChartManager(lineChart);
        lineChartManager.showLineChart(lineChart,values);
    }
    /**
     * 绘制睡眠阶段占比环形图
     * @param Percentage_stage 每个阶段的占比数组
     */
    private void showRingPieChart(float[] Percentage_stage) {
        //设置每份所占数量
        List<PieEntry> yvals = new ArrayList<>();
        yvals.add(new PieEntry(Percentage_stage[0], "WAKE"));
        yvals.add(new PieEntry(Percentage_stage[1], "N1"));
        yvals.add(new PieEntry(Percentage_stage[2], "N2"));
        yvals.add(new PieEntry(Percentage_stage[3], "N3"));
        yvals.add(new PieEntry(Percentage_stage[4], "REM"));
        // 设置每份的颜色
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#f5a658"));
        colors.add(Color.parseColor("#675cf2"));
        colors.add(Color.parseColor("#496cef"));
        colors.add(Color.parseColor("#aa63fa"));
        colors.add(Color.parseColor("#6785f2"));

        PieChartManagger pieChartManagger=new PieChartManagger(pieChart);
        pieChartManagger.showRingPieChart(yvals,colors);
    }

    /**
     * 计算各阶段占比
     * @param result 每个时段的划分结果
     * @return 各阶段占比，顺序为：Wake，REM，N1，N2，N3
     */
    float[] per_stage = new float[5];//睡眠阶段占比：wake,n1,n2,n3,rem
    float[] num_stage = new float[5];//每个睡眠阶段的epoch数: wake,n1,n2,n3,rem
    int[] num_duration = new int[5];//每个睡眠阶段的段数，持续一次算一段：wake,n1,n2,n3,rem
    ArrayList<ArrayList<Integer>> stagepoint = new ArrayList<>(5);
    //外层链表存储每个睡眠阶段，内层链表存储每个睡眠阶段的每次持续的epoch数，顺序为:wake<>,n1<>,n2<>,n3<>,rem<>
    ArrayList<Integer> wake = new ArrayList<>();
    ArrayList<Integer> n1 = new ArrayList<>();
    ArrayList<Integer> n2 = new ArrayList<>();
    ArrayList<Integer> n3 = new ArrayList<>();
    ArrayList<Integer> rem = new ArrayList<>();
    float[] max_duration = new float[5];//各阶段的最大持续时间
    float[] avg_duration = new float[5];//各阶段的平均持续时间

    public float[] calStageInfo(ArrayList<Integer> result){
        stagepoint.add(wake);
        stagepoint.add(n1);
        stagepoint.add(n2);
        stagepoint.add(n3);
        stagepoint.add(rem);
        float[] totalnum = new float[6];//每个阶段的总数{wake,n1,n2,n3,rem，unknown}
        int[] num = new int[5];//用于每个阶段的计数{wake,n1,n2,n3,rem}
        int f = result.get(0);
        for(int i=0;i<result.size();i++){
            //如果睡眠阶段没有变化
            if(result.get(i)==f){
                num[result.get(i)]++;
                f = result.get(i);
                if(i==(result.size()-1)){//判断是否为最后一个
                    stagepoint.get(result.get(i)).add(num[result.get(i)]);
                    //清0
                    num[result.get(i)] = 1;
                }
            }else {//如果睡眠阶段发生变化
                //存入上一个阶段的持续时间
                stagepoint.get(result.get(i-1)).add(num[result.get(i-1)]);
                //清0
                num[result.get(i-1)] = 1;
                f = result.get(i);
            }
            //存入每个阶段的总epoch数
            totalnum[result.get(i)]++;
        }
        Log.i(TAG, "calStageInfo: totalnum_wake="+totalnum[0]);
        float total = result.size();
        for(int i=0;i<5;i++){
            //存入各阶段总时间
            num_stage[i] = totalnum[i];
            //存入每个阶段的持续次数
            num_duration[i] = (stagepoint.get(i)).size();
            //存入各阶段占比
            DecimalFormat df = new DecimalFormat("#.00");
            per_stage[i] = Float.parseFloat(df.format(totalnum[i]/total));
        }

        SleepStage sleepStage = new SleepStage();
        for(int i=0;i<max_duration.length;i++){
            //计算各阶段最长持续时间
            max_duration[i]=sleepStage.getMAX(stagepoint.get(i));
            //计算各阶段平均持续时间
            avg_duration[i]=sleepStage.getAverage(stagepoint.get(i));
        }
        for(int v:stagepoint.get(0)){
            Log.i(TAG, "calStageInfo: stagepoint_wake"+v);
        }

        return per_stage;
    }

    /**
     * 填充数据统计表
     */
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setTabelText(){
        Message message = new Message();
        message.obj = "分析完成！";
        message.what = 2;
        mHandler.sendMessage(message);
        //填入总时间
        for(int i=0;i<percent.length;i++){
            totaltime[i].setText(Float.toString(num_stage[i]*30/60));
            Log.i(TAG, "setTabelText: stage"+i+"="+num_stage[i]);
        }
        //填入阶段占比
        for(int i=0;i<percent.length;i++){
            percent[i].setText(Float.toString(per_stage[i]));
        }
        //填入出现段数，持续一次为一段
        for(int i=0;i<percent.length;i++){
            durationnum[i].setText(Float.toString(num_duration[i]));
        }
        //填入最长持续时间
        for(int i=0;i<percent.length;i++){
            duration[i].setText(String.format("%.2f",max_duration[i]*30/60 ));
        }
        //填入平均持续时间
        for(int i=0;i<percent.length;i++){
            avgduration[i].setText(String.format("%.2f",avg_duration[i]*30/60f));
        }
    }


    /**
     * 定义接口：sendResult
     * 接口作用：通过Activity给fragment5传递分期结果
     */
    public interface SendResult{
        //data:C3或C4数据，psgResult：分期结果
        void getPSG(double[] data,ArrayList<Integer> psgResult);
    }





}