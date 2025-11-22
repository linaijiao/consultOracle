package com.zhouyi.fourpillars.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.zhouyi.core.repository.IFourPillarsRepository;
import com.zhouyi.fourpillars.model.FourPillarsResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 四柱八字结果数据仓库，负责与SQLite数据库交互
 * 提供结果的保存、查询、删除等持久化操作
 *
 * @author li bo
 * @since 2025-11-22
 */
public class FourPillarsRepository implements IFourPillarsRepository {
    // 数据库帮助类：管理数据库连接和表结构
    private final DatabaseHelper dbHelper;
    // JSON序列化工具：用于结果对象的存储与恢复
    private final Gson gson = new Gson();

    /**
     * 构造函数：初始化数据库帮助类
     *
     * @param context 应用上下文
     */
    public FourPillarsRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    /**
     * 保存或更新四柱八字计算结果
     *
     * @param result 完整的四柱八字计算结果对象（必须包含非空的birthDate字段）
     * @return true表示保存/更新成功，false表示操作失败（如JSON序列化错误）
     */
    @Override
    public boolean save(FourPillarsResult result) {
        // 使用ContentValues封装数据，通过insertWithOnConflict实现存在则替换
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_BIRTH_DATE, result.getBirthDate().getTime());
        values.put(DatabaseHelper.COLUMN_RESULT_JSON, gson.toJson(result));

        long id = db.insertWithOnConflict(DatabaseHelper.TABLE_RESULTS, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return id != -1;
    }

    /**
     * 根据出生日期查询计算结果
     *
     * @param birthDate 精确到毫秒的出生日期时间戳（作为查询唯一键）
     * @return 匹配的结果对象，若不存在则返回null
     */
    @Override
    public FourPillarsResult getByBirthDate(Date birthDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_RESULTS,
                new String[]{DatabaseHelper.COLUMN_RESULT_JSON},
                DatabaseHelper.COLUMN_BIRTH_DATE + " = ?",
                new String[]{String.valueOf(birthDate.getTime())},
                null, null, null);

        FourPillarsResult result = null;
        if (cursor.moveToFirst()) {
            String json = cursor.getString(0);
            result = gson.fromJson(json, FourPillarsResult.class);
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 查询所有保存的计算结果
     *
     * @return 不可为null的结果列表（空结果返回size=0的列表）
     */
    @Override
    public List<FourPillarsResult> getAllResults() {
        List<FourPillarsResult> results = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_RESULTS,
                new String[]{DatabaseHelper.COLUMN_RESULT_JSON},
                null, null, null, null,
                DatabaseHelper.COLUMN_CREATED_AT + " DESC");

        if (cursor.moveToFirst()) {
            do {
                String json = cursor.getString(0);
                results.add(gson.fromJson(json, FourPillarsResult.class));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return results;
    }

    /**
     * 根据出生日期删除记录
     *
     * @param birthDate 要删除记录的出生日期时间戳
     * @return true表示删除成功或记录不存在，false表示数据库操作失败
     */
    @Override
    public boolean delete(Date birthDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete(DatabaseHelper.TABLE_RESULTS,
                DatabaseHelper.COLUMN_BIRTH_DATE + " = ?",
                new String[]{String.valueOf(birthDate.getTime())});
        db.close();
        return rowsDeleted > 0;
    }
}
