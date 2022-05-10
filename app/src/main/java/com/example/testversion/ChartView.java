package com.example.testversion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static android.content.ContentValues.TAG;


/**
 * Created by Frankie on 2016/5/26.
 */
public class ChartView extends SurfaceView implements SurfaceHolder.Callback {

    private Context mContext;
    private SurfaceHolder surfaceHolder;
    public static boolean isRunning;
    private Canvas mCanvas;

    private float ecgMax = 10000;//心电的最大值
    private String bgColor = "#8FBC8F";//背景颜色
    private int wave_speed = 50;//波速: 25mm/s
    private int sleepTime = 8; //每次锁屏的时间间距，单位:ms
    private float lockWidth;//每次锁屏需要画的
    private int ecgPerCount = 8;//每次画心电数据的个数，心电每秒有500个数据包

//    private static Queue<Integer> ecg0Datas = new LinkedList<Integer>();
    private static ConcurrentLinkedQueue<Integer> ecg0Datas = new ConcurrentLinkedQueue<>();
    private static Queue<Integer> ecg1Datas = new LinkedList<Integer>();

    private Paint mPaint;//画波形图的画笔
    private int lineColor = Color.parseColor("#00FF00");

    private Paint mGridPaint;//画背景网格的画笔
    //大网格颜色
    private int mGridColor = Color.parseColor("#EEE9E9");
    //小网格颜色
    //private int mSGridColor = Color.parseColor("#092100");
    private int mWidth;//控件宽度
    private int mHeight;//控件高度
    private float ecgYRatio;
    private int startY0;
    private int startY1;
    private int yOffset;//y坐标偏移的像素
    private int yOffset1;//波2的Y坐标偏移值
    private Rect rect;

    private int startX;//每次画点的X坐标起点
    private double ecgXOffset;//绘制得两个点之间的x轴间距
    private int blankLineWidth = 6;//右侧空白点的宽度


    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.surfaceHolder = getHolder();
        this.surfaceHolder.addCallback(this);
        rect = new Rect();
        converXOffset();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(lineColor);
        mPaint.setStrokeWidth(3);
        mPaint.setAntiAlias(true);

        mGridPaint = new Paint();

        //绘制得两个点之间的间距
        ecgXOffset = lockWidth/ ecgPerCount;
        //数据在布局中放大或缩小的倍数
        ecgYRatio = mHeight / ecgMax;

        yOffset = mHeight /15;
    }


    /**
     * 方法名：converXOffset()
     * 功  能：根据波速计算每次X坐标增加的像素，计算出每次锁屏应该画的px值
     */
    private void converXOffset(){
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        //获取屏幕对角线的长度，单位:px
        double diagonalMm = Math.sqrt(width * width + height * height) / dm.densityDpi;//单位：英寸
        diagonalMm = diagonalMm * 2.54 * 10;//转换单位为：毫米
        double diagonalPx = width * width + height * height;
        diagonalPx = Math.sqrt(diagonalPx);
        //每毫米有多少px
        double px1mm = diagonalPx / diagonalMm;
        //每秒画多少px
        double px1s = wave_speed * px1mm;
        //每次锁屏所需画的宽度
        lockWidth = (float) (px1s * (sleepTime / 200f));
    }


    /********************Surface回调函数*************************/
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.parseColor(bgColor));
        holder.unlockCanvasAndPost(canvas);
        startThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        isRunning = true;
        init();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopThread();
    }

    @SuppressLint("ResourceAsColor")
//    private void init() {
//        mPaint = new Paint();
//        mPaint.setColor(R.color.green);
//        mPaint.setStrokeWidth(6);
//
//        ecgXOffset = lockWidth / ecgPerCount;
//        startY0 = mHeight * (1 / 4);//波1初始Y坐标是控件高度的1/4
//        startY1 = mHeight & (3 / 4);
//        ecgYRatio = mHeight / 2 / ecgMax;
//
//        yOffset1 = mHeight / 2;
//    }

    /********************线程*************************/

    private void startThread() {
        isRunning = true;
        new Thread(drawRunnable).start();
    }



    private void stopThread(){
        isRunning = false;
    }


    Runnable drawRunnable = new Runnable() {
        @Override
        public void run() {
            while(isRunning){
                long startTime = System.currentTimeMillis();

                startDrawWave();

                long endTime = System.currentTimeMillis();
                if(endTime - startTime < sleepTime){
                    try {
                        Thread.sleep(sleepTime - (endTime - startTime));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    private void startDrawWave(){
        rect.set(startX, 0, (int) (startX + lockWidth + blankLineWidth), mHeight);
        mCanvas = surfaceHolder.lockCanvas(rect);
        if(mCanvas == null) return;
        mCanvas.drawColor(Color.parseColor(bgColor));
        initBackground(mCanvas);
        drawWave0();
        //drawWave1();

        surfaceHolder.unlockCanvasAndPost(mCanvas);

        startX = (int) (startX + lockWidth);
        if(startX > mWidth){
            startX = 0;
        }
    }

    @SuppressLint("ResourceAsColor")
    private void initBackground(Canvas canvas) {

        //背景颜色
        int mBackgroundColor = Color.WHITE;
        canvas.drawColor(mBackgroundColor);//背景色

        //画小网格
        //竖线个数
        //小网格的宽度
//        int mSGridWidth = 10;
//        int vSNum = mWidth / mSGridWidth;
//        //横线个数
//        int hSNum = mHeight/ mSGridWidth;
//        //mGridPaint.setColor(mSGridColor);
//        mGridPaint.setStrokeWidth(2);
//        //画竖线
//        for(int i = 0;i<vSNum+1;i++){
//            canvas.drawLine(i* mSGridWidth,0,i* mSGridWidth,mHeight,mGridPaint);
//        }
//        //画横线
//        for(int i = 0;i<hSNum+1;i++){
//            canvas.drawLine(0,i* mSGridWidth,mWidth,i* mSGridWidth,mGridPaint);
//        }

        //画大网格
        //竖线个数
        //大网格宽度
        int mGridWidth = 50;
        int vNum = mWidth / mGridWidth;
        //横线个数
        int hNum = mHeight / mGridWidth;
        mGridPaint.setColor(mGridColor);
        mGridPaint.setStrokeWidth(2);
        //画竖线
        for(int i = 0;i<vNum+1;i++){
            canvas.drawLine(i* mGridWidth,0,i* mGridWidth,mHeight,mGridPaint);
        }
        //画横线
        for(int i = 0;i<hNum+1;i++){
            canvas.drawLine(0,i* mGridWidth,mWidth,i* mGridWidth,mGridPaint);
        }

    }

    /**
     * 画波1
     */



    private void drawWave0(){
        try{
            float mStartX = startX;
            if(ecg0Datas.size() > ecgPerCount){
                for(int i=0;i<ecgPerCount;i++){
                        float newX = (float) (mStartX + ecgXOffset);
                        //int newY = ecg0Datas.peek();
                        int newY = ecgConver(ecg0Datas.peek());
                        mCanvas.drawLine(mStartX, startY0, newX, newY, mPaint);
                        mStartX = newX;
                        startY0 = newY;
                        ecg0Datas.poll();
//                        Log.i(TAG, "EcgData0_poll: "+ecg0Datas.size());
                }
            }else{
                /**
                 * 如果没有数据
                 * 因为有数据一次画ecgPerCount个数，那么无数据时候就应该画ecgPercount倍数长度的中线
                 */
                int newX = (int) (mStartX + ecgXOffset * ecgPerCount);
                int newY = ecgConver((int) (ecgMax / 2));
                mCanvas.drawLine(mStartX, startY0, newX, newY, mPaint);
                startY0 = newY;
            }
        }catch (NoSuchElementException e){
            e.printStackTrace();
        }
    }

    /**
     * 画波2
     */
//    private void drawWave1(){
//        try{
//            float mStartX = startX;
//            if(ecg1Datas.size() > ecgPerCount){
//                for(int i=0;i<ecgPerCount;i++){
//                    float newX = (float) (mStartX + ecgXOffset);
//                    int newY = ecgConver(ecg1Datas.poll()) + yOffset1;
//                    mCanvas.drawLine(mStartX, startY1, newX, newY, mPaint);
//                    mStartX = newX;
//                    startY1 = newY;
//                }
//            }else{
//                /**
//                 * 如果没有数据
//                 * 因为有数据一次画ecgPerCount个数，那么无数据时候就应该画ecgPercount倍数长度的中线
//                 */
//                int newX = (int) (mStartX + ecgXOffset * ecgPerCount);
//                int newY = ecgConver((int) (ecgMax / 2)) + yOffset1;
//                mCanvas.drawLine(mStartX, startY1, newX, newY, mPaint);
//                startY1 = newY;
//            }
//        }catch (NoSuchElementException e){}
//    }

    /**
     * 将心电数据转换成用于显示的Y坐标
     * @param data
     * @return
     */
    private int ecgConver(int data){
        data = (int) (ecgMax - data);
        data = (int) (data * ecgYRatio);
        return data;
    }

    public static void addEcgData0(int data){
        ecg0Datas.add(data);
        Log.i(TAG, "EcgData0_add: "+ecg0Datas.size());
    }

    public static void addEcgData1(int data){
        ecg1Datas.add(data);
    }

}
