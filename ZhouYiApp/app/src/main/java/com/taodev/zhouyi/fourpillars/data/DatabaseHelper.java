package com.taodev.zhouyi.fourpillars.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 四柱八字数据库帮助类，继承SQLiteOpenHelper
 * 负责数据库的创建、升级和表结构管理
 *
 * @author li bo
 * @since 2025-11-22
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    /**
     * 数据库名称
     * 采用"fourpillars.db"命名，存储在应用私有目录下
     */
    private static final String DATABASE_NAME = "fourpillars.db";
    /**
     * 数据库版本号
     * <p>版本号变更规则：
     * - 主版本变更（架构重大调整）：+10
     * - 表结构变更：+5
     * - 字段新增：+1
     * </p>
     */
    private static final int DATABASE_VERSION = 1;

    // ============ 表结构定义 ============

    /**
     * 四柱计算结果表
     * 存储完整的四柱八字计算结果，以JSON格式序列化存储
     */
    public static final String TABLE_RESULTS = "results";
    // 自增主键
    public static final String COLUMN_ID = "id";
    // 出生日期（时间戳）
    public static final String COLUMN_BIRTH_DATE = "birth_date";
    // 计算结果JSON字符串
    public static final String COLUMN_RESULT_JSON = "result_json";
    // 记录创建时间
    public static final String COLUMN_CREATED_AT = "created_at";

    /**
     * 建表SQL：创建结果表，包含唯一约束UNIQUE（同一出生日期只存一条记录）
     */
    private static final String CREATE_TABLE_RESULTS = "CREATE TABLE " + TABLE_RESULTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_BIRTH_DATE + " INTEGER NOT NULL,"
            + COLUMN_RESULT_JSON + " TEXT NOT NULL,"
            + COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
            + "UNIQUE(" + COLUMN_BIRTH_DATE + ")"
            + ");";

    /**
     * 构造数据库帮助类实例
     *
     * @param context 应用上下文
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 数据库首次创建时执行
     * 创建所有必要的表结构，当前版本仅创建{@value #TABLE_RESULTS}表
     *
     * @param db 可写的SQLite数据库实例
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建结果表
        db.execSQL(CREATE_TABLE_RESULTS);
    }

    /**
     * 数据库版本升级时执行
     *
     * @param db         可写的SQLite数据库实例
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 数据库升级策略：删除旧表并重建
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
        onCreate(db);
    }
}
