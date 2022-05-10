package com.example.testversion.fragment6;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.testversion.R;
import com.example.testversion.activity.MainActivity;


public class PSQISubmitFragment extends Fragment implements View.OnClickListener {
    String TAG = "PSQISubmitFragment";
    TextView tv_score,tv_name,tv_sex,tv_age,tv_A,tv_B,tv_C,tv_D,tv_E,tv_F,tv_G,tv_SUM;
    ImageButton back;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.psqi_submit, container, false);
        initview(view);
        return view;

    }
    public void initview(View v){
        tv_score = v.findViewById(R.id.score);
        tv_name = v.findViewById(R.id.name);
        tv_sex = v.findViewById(R.id.sex);
        tv_age = v.findViewById(R.id.age);
        tv_A = v.findViewById(R.id.tv_A);
        tv_B = v.findViewById(R.id.tv_B);
        tv_C = v.findViewById(R.id.tv_C);
        tv_D = v.findViewById(R.id.tv_D);
        tv_E = v.findViewById(R.id.tv_E);
        tv_F = v.findViewById(R.id.tv_F);
        tv_G = v.findViewById(R.id.tv_G);
        tv_SUM = v.findViewById(R.id.tv_sum);
        back = v.findViewById(R.id.back);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setListner();
        super.onActivityCreated(savedInstanceState);
    }

    public void setListner(){
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.reLoadFragView();
                mainActivity.replese(3);
                break;
            default:
                break;
        }
    }
    public void getScore(String[] info, int[] score){
        Log.i(TAG, "getScore: 得分为 A："+score[0]+", B:"+score[1]+", C:"+score[2]
                +", D:"+score[3]+", E:"+score[4]+", F:"+score[5]+", G:"+score[6]+", 总分:"+score[7]);
        tv_name.setText("姓名："+info[0]);
        tv_sex.setText("性别："+info[1]);
        tv_age.setText("年龄："+info[2]);
        tv_A.setText(String.valueOf(score[0]));
        tv_B.setText(String.valueOf(score[1]));
        tv_C.setText(String.valueOf(score[2]));
        tv_D.setText(String.valueOf(score[3]));
        tv_E.setText(String.valueOf(score[4]));
        tv_F.setText(String.valueOf(score[5]));
        tv_G.setText(String.valueOf(score[6]));
        tv_SUM.setText(String.valueOf(score[7]));
        int sum = score[7];
        if(sum<=5){
            tv_score.setText("睡眠质量很好");
        }else if(sum>=6 && sum<=10){
            tv_score.setText("睡眠质量还行");
        }else if(sum>=11 && sum<=15){
            tv_score.setText("睡眠质量一般");
        }else {
            tv_score.setText("睡眠质量很差");
        }
    }



}