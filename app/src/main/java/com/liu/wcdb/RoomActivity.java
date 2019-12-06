package com.liu.wcdb;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.room.Room;

import com.liu.wcdb.entity.User;
import com.tencent.wcdb.database.SQLiteCipherSpec;
import com.tencent.wcdb.room.db.WCDBOpenHelperFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuzhenrong
 * @date 2019-12-06 16:24
 * @desc
 */
public class RoomActivity extends AppCompatActivity {

    private MyAdapter mAdapter;
    private ListView mListView;
    private List<User> userList = new ArrayList<>();
    private UserRoomDatabase db;
    private int baseUid = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        initRoom();
        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);

        findViewById(R.id.btn_insert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, List<User>>() {
                    @Override
                    protected void onPreExecute() {
                        userList.clear();
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    protected List<User> doInBackground(Void... params) {
                        for (int i=0;i<10000*10000;i++){
                            baseUid++;
                            User user = new User(baseUid,"厚度和搜到好好读书搜狐誓师大会胡搜搜狐董浩叔叔很多和闪兑搜搜活动活动合适的话是实时誓师大会闪兑和花多少誓师大会视频黑色的厚度和平时多喝点水花生豆合适的话合适的话琥珀色和视频神猴大叔闪兑和hh"+baseUid,baseUid+i);
                            db.getUserDao().insert(user);
                        }
                        baseUid = db.getUserDao().getMaxUid();
                        return db.getUserDao().getAll();
                    }

                    @Override
                    protected void onPostExecute(List<User> data) {
                        userList.addAll(data);
                        mAdapter.notifyDataSetChanged();
                    }
                }.execute();
            }
        });
        loadData();
    }

    private void initRoom(){
        SQLiteCipherSpec cipherSpec = new SQLiteCipherSpec()
                .setPageSize(4096)
                .setKDFIteration(64000);

        WCDBOpenHelperFactory factory = new WCDBOpenHelperFactory()
                .passphrase("passphrase".getBytes())  // passphrase to the database, remove this line for plain-text
                .cipherSpec(cipherSpec)               // cipher to use, remove for default settings
                .writeAheadLoggingEnabled(true)       // enable WAL mode, remove if not needed
                .asyncCheckpointEnabled(true);        // enable asynchronous checkpoint, remove if not needed

         db = Room.databaseBuilder(this, UserRoomDatabase.class, "app-db")
                .allowMainThreadQueries()
                .openHelperFactory(factory)   // specify WCDBOpenHelperFactory when opening database
                .build();
    }

    private void loadData(){
        new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected void onPreExecute() {
                userList.clear();
                mAdapter.notifyDataSetChanged();
            }

            @Override
            protected List<User> doInBackground(Void... params) {
                baseUid = db.getUserDao().getMaxUid();
                return db.getUserDao().getAll();
            }

            @Override
            protected void onPostExecute(List<User> data) {
                userList.addAll(data);
                mAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    public class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return userList!=null? userList.size():0;
        }

        @Override
        public User getItem(int position) {
            return userList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = null;
            if(convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room,parent,false);
                textView = convertView.findViewById(R.id.tv_info);
                convertView.setTag(textView);
            }else {
                textView = (TextView) convertView.getTag();
            }
            User user = getItem(position);
            if(user!=null){
                textView.setText(String.format("uid=%d,name=%s,age=%d",user.getUid(),user.getName(),user.getAge()));
            }
            return convertView;
        }
    }


}
