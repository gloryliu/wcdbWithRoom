package com.liu.wcdb.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.liu.wcdb.entity.User;

import java.util.List;

/**
 * @author liuzhenrong
 * @date 2019-12-06 16:34
 * @desc
 */
@Dao
public interface UserDao {

    @Insert
    void insert(User user);

    @Query("select * from tb_user")
    List<User> getAll();

    @Query("select uid from tb_user order by uid desc limit 0,1")
    int getMaxUid();
}
