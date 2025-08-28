package com.hudou.autotest.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Upsert;

import com.hudou.autotest.database.entity.ResultDataEntity;
import com.hudou.autotest.database.entity.ResultItemEntity;

import java.util.List;

@Dao
public interface ResultDao {
    @Insert
    long insertResultItem(ResultItemEntity item);

    @Insert
    long insertData(ResultDataEntity data);

    @Insert
    void insertResultDataList(List<ResultDataEntity> dataList);

    @Query("SELECT * FROM result_items")
    List<ResultItemEntity> getAllResultItems();

    @Query("SELECT * FROM result_data WHERE className = :className")
    List<ResultDataEntity> getResultDataForItem(String className);

    @Transaction
    @Query("DELETE FROM result_items")
    default void clearAll() {
        clearAllItems();
        clearAllData();
    }

    @Query("DELETE FROM result_items")
    void clearAllItems();

    @Query("DELETE FROM result_data")
    void clearAllData();

    @Query("SELECT * FROM result_data WHERE className = :className")
    List<ResultDataEntity> getDataForItem(String className);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long upsertResultItem(ResultItemEntity item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertResultDataList(List<ResultDataEntity> dataList);
}
