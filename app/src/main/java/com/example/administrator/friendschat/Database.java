package com.example.administrator.friendschat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    public Database(Context context) {
        super(context,"stuti",null,123);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table info(fname String,lname String,email String,number String ,password String,cpassword String)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
