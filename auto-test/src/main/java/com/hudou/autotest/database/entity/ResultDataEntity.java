package com.hudou.autotest.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "result_data",
        indices = {@Index(value = {"className", "methodName"}, unique = true)},
        foreignKeys = @ForeignKey(
                entity = ResultItemEntity.class,
                parentColumns = "className",
                childColumns = "className",
                onDelete = ForeignKey.CASCADE
        )
)
public class ResultDataEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String className;   // 对应 ResultItemEntity.className
    public String caseName;    //对应nocard_001
    public String methodName;  // 业务键：测试方法名

    public String result;
    public String chineseDescription;
    public String englishDescription;
    public String detail;
}
