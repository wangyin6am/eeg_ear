package com.example.testversion.fragment6;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.testversion.R;


public class Fragment6 extends Fragment {
    SetScore myInterface;
    int glad=0;
    int data;
    int s=0;

    int t1_one ;
    int t1_two ;
    int t2;
    int t3_one ;
    int t3_two ;
    int t4_one;
    int t4_two;
    int sleeptime;//睡眠时间（分钟制）
    int bedtime;//床上时间
    float h;//睡眠时间（小时制）
    float  efsleep;//睡眠效率
    String name,age,sex;
    EditText edit_name,edit_age,edit_sex;
    Button submit;
    Context mContext;

    private EditText topic1_one;
    private EditText topic1_two;
    private EditText topic2;
    private EditText topic3_one;
    private EditText topic3_two;
    private EditText topic4_one;
    private EditText topic4_two;

    private RadioGroup rg3;
    private RadioGroup rg5;
    private RadioGroup rg6;
    private RadioGroup rg7;
    private RadioGroup rg8;
    private RadioGroup rg9;
    private RadioGroup rg10;
    private RadioGroup rg11;
    private RadioGroup rg12;
    private RadioGroup rg13;
    private RadioGroup rg14;
    private RadioGroup rg15;
    private RadioGroup rg16;
    private RadioGroup rg17;
    private RadioGroup rg18;

    private Spinner spinner;

    /*****************定义代表七个成分得分的变量*********************/
    int[] composition=new int[8];

    /************定义第2成分、第5成分、第7成分的数组***************/
    int[] nu2=new int[3];
    //成分2的数组
    int[] nu5=new int[10];
    //成分5的数组
    int[] nu7=new int[3];
    //成分7的数组
    int[] value=new int[18];

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_6, container, false);
        intView(view);//初始化函数
        setLisener();//设置点击监听事件函数
        mContext = getContext();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        myInterface = (SetScore)getContext();
        super.onActivityCreated(savedInstanceState);
    }

    private void intView(View v) {

        topic1_one = v.findViewById(R.id.topic1_one);
        topic1_two = v.findViewById(R.id.topic1_two);
        topic2 = v.findViewById(R.id.topic2);
        topic3_one = v.findViewById(R.id.topic3_one);
        topic3_two = v.findViewById(R.id.topic3_two);
        topic4_one = v.findViewById(R.id.topic4_one);
        topic4_two = v.findViewById(R.id.topic4_two);

        edit_name=v.findViewById(R.id.name);
        edit_age=v.findViewById(R.id.age);
        edit_sex = v.findViewById(R.id.sex);

        rg3 = v.findViewById(R.id.radiogroup3);
        rg5 = v.findViewById(R.id.radiogroup5);
        rg6 = v.findViewById(R.id.radiogroup6);
        rg7 = v.findViewById(R.id.radiogroup7);
        rg8 = v.findViewById(R.id.radiogroup8);
        rg9 = v.findViewById(R.id.radiogroup9);
        rg10 = v.findViewById(R.id.radiogroup10);
        rg11 = v.findViewById(R.id.radiogroup11);
        rg12 = v.findViewById(R.id.radiogroup12);
        rg13 = v.findViewById(R.id.radiogroup13);
        rg14 = v.findViewById(R.id.radiogroup14);
        rg15 = v.findViewById(R.id.radiogroup15);
        rg16 = v.findViewById(R.id.radiogroup16);
        rg17 = v.findViewById(R.id.radiogroup17);
        rg18 = v.findViewById(R.id.radiogroup18);


        submit = v.findViewById(R.id.submit);
    }

    public  void setLisener(){


        rg3.setOnCheckedChangeListener(click3);
        rg5.setOnCheckedChangeListener(click5);
        rg6.setOnCheckedChangeListener(click6);
        rg7.setOnCheckedChangeListener(click7);
        rg8.setOnCheckedChangeListener(click8);
        rg9.setOnCheckedChangeListener(click9);
        rg10.setOnCheckedChangeListener(click10);
        rg11.setOnCheckedChangeListener(click11);
        rg12.setOnCheckedChangeListener(click12);
        rg13.setOnCheckedChangeListener(click13);
        rg14.setOnCheckedChangeListener(click14);
        rg15.setOnCheckedChangeListener(click15);
        rg16.setOnCheckedChangeListener(click16);
        rg17.setOnCheckedChangeListener(click17);
        rg18.setOnCheckedChangeListener(click18);

        submit.setOnClickListener(sub);


    }

    /*************************第3题********************************/
    RadioGroup.OnCheckedChangeListener click3=new RadioGroup.OnCheckedChangeListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch(checkedId){
                case R.id.today:
                    data=1;
                    break;
                case R.id.tomorrow:
                    data=0;
                    break;
                default:
                    break;
            }
        }
    };
    /*************************第5题********************************/
    RadioGroup.OnCheckedChangeListener click5=new RadioGroup.OnCheckedChangeListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch(checkedId){
                case R.id.group5rb1:
                    nu2[1]=0;
                    break;
                case R.id.group5rb2:
                    nu2[1]=1;
                    break;
                case R.id.group5rb3:
                    nu2[1]=2;
                    break;
                case R.id.group5rb4:
                    nu2[1]=3;
                    break;
                default:
                    break;
            }
        }
    };
    /*************************第6题********************************/
    RadioGroup.OnCheckedChangeListener click6=new RadioGroup.OnCheckedChangeListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch(checkedId){
                case R.id.group6rb1:
                    nu5[0]=0;
                    break;
                case R.id.group6rb2:
                    nu5[0]=1;
                    break;
                case R.id.group6rb3:
                    nu5[0]=2;
                    break;
                case R.id.group6rb4:
                    nu5[0]=3;
                    break;
                default:
                    break;
            }
        }
    };
    /*************************第7题********************************/
    @SuppressLint("NonConstantResourceId")
    RadioGroup.OnCheckedChangeListener click7=new RadioGroup.OnCheckedChangeListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch(checkedId){
                case R.id.group7rb1:
                    nu5[1]=0;
                    break;
                case R.id.group7rb2:
                    nu5[1]=1;
                    break;
                case R.id.group7rb3:
                    nu5[1]=2;
                    break;
                case R.id.group7rb4:
                    nu5[1]=3;
                    break;
                default:
                    break;
            }
        }
    };
    /*************************第8题********************************/
    @SuppressLint("NonConstantResourceId")
    RadioGroup.OnCheckedChangeListener click8=new RadioGroup.OnCheckedChangeListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch(checkedId){
                case R.id.group8rb1:
                    nu5[2]=0;
                    break;
                case R.id.group8rb2:
                    nu5[2]=1;
                    break;
                case R.id.group8rb3:
                    nu5[2]=2;
                    break;
                case R.id.group8rb4:
                    nu5[2]=3;
                    break;
                default:
                    break;
            }
        }
    };
    /*************************第9题********************************/
    @SuppressLint("NonConstantResourceId")
    RadioGroup.OnCheckedChangeListener click9= (radioGroup, checkedId) -> {
        switch(checkedId){
            case R.id.group9rb1:
                nu5[3]=0;
                break;
            case R.id.group9rb2:
                nu5[3]=1;
                break;
            case R.id.group9rb3:
                nu5[3]=2;
                break;
            case R.id.group9rb4:
                nu5[3]=3;
                break;
            default:
                break;
        }
    };
    /*************************第10题********************************/
    @SuppressLint("NonConstantResourceId")
    RadioGroup.OnCheckedChangeListener click10= (radioGroup, checkedId) -> {
        switch(checkedId){
            case R.id.group10rb1:
                nu5[4]=0;
                break;
            case R.id.group10rb2:
                nu5[4]=1;
                break;
            case R.id.group10rb3:
                nu5[4]=2;
                break;
            case R.id.group10rb4:
                nu5[4]=3;
                break;
            default:
                break;
        }
    };
    /*************************第11题********************************/
    @SuppressLint("NonConstantResourceId")
    RadioGroup.OnCheckedChangeListener click11= (radioGroup, checkedId) -> {
        switch(checkedId){
            case R.id.group11rb1:
                nu5[5]=0;
                break;
            case R.id.group11rb2:
                nu5[5]=1;
                break;
            case R.id.group11rb3:
                nu5[5]=2;
                break;
            case R.id.group11rb4:
                nu5[5]=3;
                break;
            default:
                break;
        }
    };
    /*************************第12题********************************/
    @SuppressLint("NonConstantResourceId")
    RadioGroup.OnCheckedChangeListener click12= (radioGroup, checkedId) -> {
        switch(checkedId){
            case R.id.group12rb1:
                nu5[6]=0;
                break;
            case R.id.group12rb2:
                nu5[6]=1;
                break;
            case R.id.group12rb3:
                nu5[6]=2;
                break;
            case R.id.group12rb4:
                nu5[6]=3;
                break;
            default:
                break;
        }
    };
    /*************************第13题********************************/
    @SuppressLint("NonConstantResourceId")
    RadioGroup.OnCheckedChangeListener click13= (radioGroup, checkedId) -> {
        switch(checkedId){
            case R.id.group13rb1:
                nu5[7]=0;
                break;
            case R.id.group13rb2:
                nu5[7]=1;
                break;
            case R.id.group13rb3:
                nu5[7]=2;
                break;
            case R.id.group13rb4:
                nu5[7]=3;
                break;
            default:
                break;
        }
    };
    /*************************第14题********************************/
    @SuppressLint("NonConstantResourceId")
    RadioGroup.OnCheckedChangeListener click14= (radioGroup, checkedId) -> {
        switch(checkedId){
            case R.id.group14rb1:
                nu5[8]=0;
                break;
            case R.id.group14rb2:
                nu5[8]=1;
                break;
            case R.id.group14rb3:
                nu5[8]=2;
                break;
            case R.id.group14rb4:
                nu5[8]=3;
                break;
            default:
                break;
        }
    };
    /*************************第15题********************************/
    @SuppressLint("NonConstantResourceId")
    RadioGroup.OnCheckedChangeListener click15= (radioGroup, checkedId) -> {
        switch(checkedId){
            case R.id.group15rb1:
                composition[0]=0;
                break;
            case R.id.group15rb2:
                composition[0]=1;
                break;
            case R.id.group15rb3:
                composition[0]=2;
                break;
            case R.id.group15rb4:
                composition[0]=3;
                break;
            default:
                break;
        }
    };
    /*************************第16题********************************/
    @SuppressLint("NonConstantResourceId")
    RadioGroup.OnCheckedChangeListener click16= (radioGroup, checkedId) -> {
        switch(checkedId){
            case R.id.group16rb1:
                composition[5]=0;
                break;
            case R.id.group16rb2:
                composition[5]=1;
                break;
            case R.id.group16rb3:
                composition[5]=2;
                break;
            case R.id.group16rb4:
                composition[5]=3;
                break;
            default:
                break;
        }
    };
    /*************************第17题********************************/
    @SuppressLint("NonConstantResourceId")
    RadioGroup.OnCheckedChangeListener click17= (radioGroup, checkedId) -> {
        switch(checkedId){
            case R.id.group17rb1:
                nu7[0]=0;
                break;
            case R.id.group17rb2:
                nu7[0]=1;
                break;
            case R.id.group17rb3:
                nu7[0]=2;
                break;
            case R.id.group17rb4:
                nu7[0]=3;
                break;
            default:
                break;
        }
    };
    /*************************第18题********************************/
    @SuppressLint("NonConstantResourceId")
    RadioGroup.OnCheckedChangeListener click18= (radioGroup, checkedId) -> {

        switch(checkedId){
            case R.id.group18rb1:
                nu7[1]=0;
                break;
            case R.id.group18rb2:
                nu7[1]=1;
                break;
            case R.id.group18rb3:
                nu7[1]=2;
                break;
            case R.id.group18rb4:
                nu7[1]=3;
                break;
            default:
                break;
        }
    };
    /************************“提交”按钮点击事件****************************/
    View.OnClickListener sub = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            glad=0;

            /**********
             * 成分一：第6题
             * 成分二：第2题和第5题
             * 成分三：第4题
             * 成分四：第1题和第3题和第4题
             * 成分五：第6~14题
             * 成分六：第16题
             * 成分七：第17题和第18题
             **********/


            /******第1题的强制类型转换******/
            String topic1_1 =topic1_one.getText().toString();
            String topic1_2 =topic1_two.getText().toString();

            if("".equals(topic1_1)){
                topic1_1 = "0";

            }
            if("".equals(topic1_2)){

                topic1_2 = "0";
            }
            t1_one = Integer.parseInt(topic1_1);
            t1_two = Integer.parseInt(topic1_2);


            /*******第2题强制类型转换********/
            String topic2_ = topic2 .getText().toString();

            if("".equals(topic2_)){

                topic2_ = "0";
            }
            t2 = Integer.parseInt(topic2_);//强制类型转换成整形
            /******第3题的强制类型转换******/
            String topic3_1 =topic3_one.getText().toString();
            String topic3_2 =topic3_two.getText().toString();
            if("".equals(topic3_1)){

                topic3_1 = "0";
            }
            if("".equals(topic3_2)){
                topic3_2 = "0";
            }
            t3_one = Integer.parseInt(topic3_1);
            t3_two = Integer.parseInt(topic3_2);

            /*******第4题的强制类型转换******/
            String topic4_1 =topic4_one.getText().toString();
            String topic4_2 =topic4_two.getText().toString();

            if(("".equals(topic4_1))){
                topic4_1 = "0";

            }

            if(("".equals(topic4_2))){
                topic4_2 = "0";
            }
            t4_one = Integer.parseInt(topic4_1);
            t4_two = Integer.parseInt(topic4_2);


            /* ***********************成分二***************************/

            /* * ******判断第2题的值**********/

            /* *****通过判断j的区间给number1赋值
             *  t2:0-15，sum=1,case1
             *  t2:16-30，sum=2,case2
             *  t2:31-60，sum=3,case3
             *  t2:>60，sum=4,case4
             * *********/
            int sum = ((t2 > 0) ?1:0)+((t2 > 15) ?1:0)+((t2 > 30) ?1:0)+((t2 > 60) ?1:0);
            switch(sum){
                case 1:
                    nu2[0]=0;
                    break;
                case 2:
                    nu2[0]=1;
                    break;
                case 3:
                    nu2[0]=2;
                    break;
                case 4:
                    nu2[0]=3;
                    break;
                default:
                    break;
            }
            //nu2[0]为第2题得分，nu2[1]为第5题得分，nu2[2]为成分二总得分
            nu2[2]=nu2[0]+nu2[1];

            /****
             * nu2[2]=0,sum2=4,case4
             * nu2[2]=1~2,sum2=3,case3
             * nu2[2]=3~4,sum2=2,case2
             * nu2[2]=5~6,sum2=1,case1
             */

            int sum2 = ((nu2[2] < 1) ?1:0)+((nu2[2] < 3) ?1:0)+((nu2[2] < 5) ?1:0)+((nu2[2] < 7) ?1:0);
            switch(sum2){
                case 4:
                    composition[1]=0;
                    break;
                case 3:
                    composition[1]=1;
                    break;
                case 2:
                    composition[1]=2;
                    break;
                case 1:
                    composition[1]=3;
                    break;
                default:
                    break;
            }
            /*******************成分三******************/
            /*  7≤h,number2=4,case 4
             * 6≤h<7,number2=3,case 3
             * 5≤h<6,number2=2,case 2
             * h<5,number2=1,case 1
             */
            h = t4_one+t4_two/60;

            int number2 =((h > 7 || h == 7) ?1:0)+((h > 6 || h == 6) ?1:0 )+((h > 5 || h == 5) ?1:0)+((h > 0) ?1:0);

            switch (number2){
                case 4:
                    composition[2]=0;
                    break;
                case 3:
                    composition[2]=1;
                    break;
                case 2:
                    composition[2]=2;
                    break;
                case 1:
                    composition[2]=3;
                    break;
                default:
                    break;
            }

            /********************成分四*************************/
            //当天
            if(data==1){
                bedtime=(t3_one-t1_one)*60-t1_two+t3_two;
            }
            //第二天
            else if(data==0){
                bedtime=(24-t1_one)*60-t1_two+(t3_one*60)+t3_two;
            }
            /**
             * 床上时间（bedtime）=起床时间-上床时间
             * 睡眠效率（efsleep）=睡眠时间（sleeptime）/床上时间（bedtime）
             */
            sleeptime=(t4_one*60)+t4_two;

            if(bedtime==0){
                efsleep=0;
            }
            else{
                efsleep = sleeptime*100/bedtime;//睡眠效率}
            }
            /***
             * efsleep≥0.85，result=4，case 4
             * 0.75≤efsleep＜0.85，result=3 ，case3
             * 0.65≤efsleep＜0.75，result=2 ，case2
             * efsleep＜0.65，result=1 ，case1
             * */
            int result=((efsleep > 85 || efsleep == 85) ?1:0)+((efsleep > 75 || efsleep == 75) ?1:0)+((efsleep > 65 || efsleep == 65) ?1:0)+((efsleep>0||efsleep==0)==true?1:0);

            switch (result){
                case 4:
                    composition[3]=0;
                    break;
                case 3:
                    composition[3]=1;
                    break;
                case 2:
                    composition[3]=2;
                    break;
                case 1:
                    composition[3]=3;
                    break;
                default:
                    break;
            }
            /* *******************成分五**************************/
            /* ***********将6至14题累加******************/
            int t=0;
            for(int i=0;i<9;i++)
            {
                t +=nu5[i];
            }
            /* ***判断累加值*******
             * t=0，num=4,case4
             * t=1~9，num=3,case3
             * t=10~18，num=2,case2
             * t=19~27，num=1,case1
             * ************/
            int num = ((t<1)?1:0)+((t < 10) ?1:0)+((t < 19) ?1:0)+((t < 28) ?1:0);
            /* ************将成分五所有项累加，判断区间再赋值*****************/
            switch(num){
                case 1:
                    composition[4]=3;
                    break;
                case 2:
                    composition[4]=2;
                    break;
                case 3:
                    composition[4]=1;
                    break;
                case 4:
                    composition[4]=0;
                    break;
                default:
                    break;
            }

            /******第七成分：将17题与18题的结果累加，根据值大小重新给成分七赋值*****/
            nu7[2]=nu7[0]+nu7[1];
            /***********
             * t=0，num=4,case4
             * t=1~2，num=3,case3
             * t=3~4，num=2,case2
             * t=5~6，num=1,case1
             * ************/
            int sum7 = ((nu7[2] < 1) ?1:0)+((nu7[2] < 3) ?1:0)+((nu7[2] < 5) ?1:0)+((nu7[2] < 7) ?1:0);
            switch(sum7){
                case 1:
                    composition[6]=3;
                    break;
                case 2:
                    composition[6]=2;
                    break;
                case 3:
                    composition[6]=1;
                    break;
                case 4:
                    composition[6]=0;
                    break;
                default:
                    break;
            }
            /***************************最终得分*******************************/
            /**********七个成分相加*************/

            for(int i=0;i<7;i++)
            {
                glad +=composition[i];
            }
            composition[7] = glad;
            name = edit_name.getText().toString();
            sex = edit_sex.getText().toString();
            age = edit_age.getText().toString();

            String[] info = new String[]{name,sex,age};
//            getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.container,new PSQISubmitFragment()).commit();
            myInterface.setScore(info,composition);

//            Intent intent1 =new Intent(ActivityPsqi.this,SubmitActivity.class);
//            Bundle bundle1=new Bundle();
//            bundle1.putInt("part1",composition[0]);
//            bundle1.putInt("part2",composition[1]);
//            bundle1.putInt("part3",composition[2]);
//            bundle1.putInt("part4",composition[3]);
//            bundle1.putInt("part5",composition[4]);
//            bundle1.putInt("part6",composition[5]);
//            bundle1.putInt("part7",composition[6]);
//            bundle1.putInt("glad",glad);
//            intent1.putExtras(bundle1);
//            startActivity(intent1);
        }
    };

    public interface SetScore{
        void setScore(String[] info,int[] score);
    }

}

