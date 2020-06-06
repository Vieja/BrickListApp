package com.vieja.bricklist

import android.content.Context
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper

class DBOpenHelper(context: Context) :
    SQLiteAssetHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "BrickList.db"
    }
}