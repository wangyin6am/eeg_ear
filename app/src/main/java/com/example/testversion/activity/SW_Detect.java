package com.example.testversion.activity;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SW_Detect {
    String TAG = "SW_Dectect";
    ArrayList<Float> de_data = new ArrayList<>();


    Float point_zero = 0f;

    public boolean detect(ArrayList<Float> fft_data){

            //获取区间两个零点间的区间
            boolean iffind =  find_P(fft_data);

            return iffind;
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
                        point_zero = fft_data.get(0);
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
}
