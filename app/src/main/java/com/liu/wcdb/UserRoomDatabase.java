package com.liu.wcdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.liu.wcdb.dao.UserDao;
import com.liu.wcdb.entity.User;

/**
 * @author liuzhenrong
 * @date 2019-12-06 16:37
 * @desc
 */
@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class UserRoomDatabase extends RoomDatabase {
    public abstract UserDao getUserDao();
}
