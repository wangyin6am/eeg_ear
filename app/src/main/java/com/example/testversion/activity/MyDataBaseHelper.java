package com.example.testversion.activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDataBaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "EEGData";
    public static final String COLUMN_NAME = "username TEXT,";
    public static final String COLUMN_DATE = "Date TEXT,";
    public static final String COLUMN_SIGN = "sign INTEGER,";
    public static final String CHANNEL_1 = "Channel1 REAL,";
    public static final String CHANNEL_2 = "Channel2 REAL,";
    public static final String CHANNEL_3 = "Channel3 REAL,";
    public static final String CHANNEL_4 = "Channel4 REAL,";
    public static final String CHANNEL_5 = "Channel5 REAL,";
    public static final String CHANNEL_6 = "Channel6 REAL,";
    public static final String CHANNEL_7 = "Channel7 REAL,";
    public static final String CHANNEL_8 = "Channel8 REAL";


    public static final String sql = "CREATE TABLE " + TABLE_NAME + "("
//            + COLUMN_NAME
            + COLUMN_DATE
            + COLUMN_SIGN
            + CHANNEL_1
            + CHANNEL_2
            + CHANNEL_3
            + CHANNEL_4
            + CHANNEL_5
            + CHANNEL_6
            + CHANNEL_7
            + CHANNEL_8
            + ")";

    public MyDataBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
