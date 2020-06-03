package com.vieja.bricklist

import android.content.Context
import android.database.Cursor
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper

class DBOpenHelper(context: Context) : SQLiteAssetHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    fun getTest(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM CODES where id = 1", null)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "BrickList.db"
    }
}