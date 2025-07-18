package com.hudou.autotest.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "result_items",
        indices = {@Index(value = {"className"}, unique = true)}
)
public class ResultItemEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "className")
    public String className;

    public String startTime;
    public String endTime;
    public boolean isStartTimeSet;
}