package com.example.vibecodingpractice04 // 注意：请确保这里的包名和你的项目一致！

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "BingoDatabase.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "GameHistory"
        const val COL_ID = "id"
        const val COL_USERNAME = "username"       // 属性 i: 用户名
        const val COL_ROUND = "round_number"      // 属性 ii: 游戏回合数
        const val COL_NUMBERS = "bingo_numbers"   // 属性 iii: 五个 Bingo 数字
        const val COL_TIME = "system_time"        // 属性 iv: 系统时间
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // 创建表，包含要求的四个属性
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_USERNAME TEXT, " +
                "$COL_ROUND INTEGER, " +
                "$COL_NUMBERS TEXT, " +
                "$COL_TIME TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // 插入一局游戏记录的公共方法
    fun insertGameRecord(username: String, round: Int, numbers: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_USERNAME, username)
            put(COL_ROUND, round)
            put(COL_NUMBERS, numbers)
            // 获取系统当前时间并格式化
            val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            put(COL_TIME, currentTime)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }
}