package com.liu.wcdb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.liu.wcdb.db.EncryptedDBHelper;
import com.liu.wcdb.db.PlainTextDBHelper;
import com.tencent.wcdb.database.SQLiteDatabase;
import com.tencent.wcdb.database.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WCDB.EncryptDBSample";

    private SQLiteDatabase mDB;
    private SQLiteOpenHelper mDBHelper;
    private int mDBVersion;

    private ListView mListView;
    private SimpleCursorAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new SimpleCursorAdapter(this, R.layout.main_listitem, null,
                new String[] {"content", "_id", "sender"},
                new int[] {R.id.list_tv_content, R.id.list_tv_id, R.id.list_tv_sender},
                0);

        mListView.setAdapter(mAdapter);

        findViewById(R.id.btn_init_plain).setOnClickListener(new View.OnClickListener() {
            // Init plain-text button pressed.
            // Create or open database in version 1, then refresh adapter.

            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Cursor>() {
                    @Override
                    protected void onPreExecute() {
                        mAdapter.changeCursor(null);
                    }

                    @Override
                    protected Cursor doInBackground(Void... params) {
                        if (mDBHelper != null && mDB != null && mDB.isOpen()) {
                            mDBHelper.close();
                            mDBHelper = null;
                            mDB = null;
                        }

                        mDBHelper = new PlainTextDBHelper(MainActivity.this);
                        mDBHelper.setWriteAheadLoggingEnabled(true);
                        mDB = mDBHelper.getWritableDatabase();
                        mDBVersion = mDB.getVersion();
                        return mDB.rawQuery("SELECT rowid as _id, content, '???' as sender FROM message;",
                                null);
                    }

                    @Override
                    protected void onPostExecute(Cursor cursor) {
                        mAdapter.changeCursor(cursor);
                    }
                }.execute();
            }
        });

        findViewById(R.id.btn_init_encrypted).setOnClickListener(new View.OnClickListener() {
            // Init encrypted button pressed.
            // Create or open database in version 2, then refresh adapter.
            // If plain-text database exists and encrypted one does not, transfer all
            // data from the plain-text database (which in version 1), then upgrade it
            // to version 2.

            // See EncryptedDBHelper.java for details about data transfer and schema upgrade.

            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Cursor>() {
                    @Override
                    protected void onPreExecute() {
                        mAdapter.changeCursor(null);
                    }

                    @Override
                    protected Cursor doInBackground(Void... params) {
                        if (mDBHelper != null && mDB != null && mDB.isOpen()) {
                            mDBHelper.close();
                            mDBHelper = null;
                            mDB = null;
                        }

                        String passphrase = "passphrase";
                        mDBHelper = new EncryptedDBHelper(MainActivity.this, passphrase);
                        mDBHelper.setWriteAheadLoggingEnabled(true);
                        mDB = mDBHelper.getWritableDatabase();
                        mDBVersion = mDB.getVersion();
                        return mDB.rawQuery("SELECT rowid as _id, content, sender FROM message;",
                                null);
                    }

                    @Override
                    protected void onPostExecute(Cursor cursor) {
                        mAdapter.changeCursor(cursor);
                    }
                }.execute();
            }
        });

        findViewById(R.id.btn_insert).setOnClickListener(new View.OnClickListener() {
            // Insert button pressed.
            // Insert a message to the database.

            // To test data transfer, init plain-text database, insert messages,
            // then init encrypted database.

            final DateFormat DATE_FORMAT = SimpleDateFormat.getDateTimeInstance();

            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Cursor>() {
                    @Override
                    protected void onPreExecute() {
                        mAdapter.changeCursor(null);
                    }

                    @Override
                    protected Cursor doInBackground(Void... params) {
                        if (mDB == null || !mDB.isOpen())
                            return null;

                        String message = "Message inserted on " + DATE_FORMAT.format(new Date());

                        if (mDBVersion == 1) {
                            mDB.execSQL("INSERT INTO message VALUES (?);",
                                    new Object[]{message});
                            return mDB.rawQuery("SELECT rowid as _id, content, '???' as sender FROM message;",
                                    null);
                        } else {
                            mDB.execSQL("INSERT INTO message VALUES (?, ?);",
                                    new Object[]{message, "Me"});
                            return mDB.rawQuery("SELECT rowid as _id, content, sender FROM message;",
                                    null);
                        }
                    }

                    @Override
                    protected void onPostExecute(Cursor cursor) {
                        if (cursor == null)
                            return;
                        mAdapter.changeCursor(cursor);
                    }
                }.execute();
            }
        });

        findViewById(R.id.btn_room).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RoomActivity.class);
                startActivity(intent);
            }
        });
    }
}
