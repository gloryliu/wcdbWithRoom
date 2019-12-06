package com.liu.wcdb.db;

import android.content.Context;

import com.tencent.wcdb.database.SQLiteDatabase;
import com.tencent.wcdb.database.SQLiteOpenHelper;

/**
 * @author liuzhenrong
 * @date 2019-12-06 11:23
 * @desc
 */
public class PlainTextDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "plain-text.db";
    private static final int DATABASE_VERSION = 1;

    public PlainTextDBHelper(Context context) {

        // Call "plain-text" version of the superclass constructor.
        super(context, DATABASE_NAME, null, DATABASE_VERSION, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE message (content TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do nothing.
    }
}
