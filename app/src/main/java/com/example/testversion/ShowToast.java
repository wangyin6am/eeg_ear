package com.example.testversion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

/**
 * @author: Tian
 * @Date; 2019/9/23 14:56
 */
public class ShowToast {
    /*********************************************************
     * 功能：实时显示Toast
     */
    private static Toast mToast;

    @SuppressLint("ShowToast")
    public static void show(Context context, int textID, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, textID, duration);
        } else {
            mToast.setText(textID);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

    @SuppressLint("ShowToast")
    public static void show(Context context, String text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, duration);
        } else {
            mToast.setText(text);
            mToast.setDuration(duration);
        }
        mToast.show();
    }
}
