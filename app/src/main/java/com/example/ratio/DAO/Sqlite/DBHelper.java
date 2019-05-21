package com.example.ratio.DAO.Sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ratio.Enums.DEFAULTS;
import com.example.ratio.Enums.PARSECLASS;
import com.example.ratio.Enums.STATUS;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DB_NAME = "LocalStorage.db";
    private static final String objectId = DEFAULTS.objectId.toString();
    private static final String createdAt = DEFAULTS.createdAt.toString();
    private static final String updatedAt = DEFAULTS.updatedAt.toString();


    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createStatus = String.format("CREATE TABLE IF NOT EXISTS %s" +
                "(%s character(10) PRIMARY KEY," +
                "%s datetime, " +
                "%s datetime, " +
                "%s varchar(255), " +
                "%s character(10))", PARSECLASS.STATUS.toString(),
                objectId, createdAt, updatedAt, STATUS.NAME.toString(), STATUS.PARENT.toString());
        db.execSQL(createStatus);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", PARSECLASS.STATUS.toString()));
        onCreate(db);
    }
}