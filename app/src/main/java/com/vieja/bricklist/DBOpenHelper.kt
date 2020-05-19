package com.vieja.bricklist

import android.content.Context
import android.database.Cursor
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper

class DBOpenHelper(context: Context) : SQLiteAssetHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    fun getTest(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_CODES where id = 1", null)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "BrickList.db"
        val TABLE_CODES = "CODES"
        val COLUMN_ID = "id"
        val COLUMN_ITEM_ID = "ItemID"
    }
}