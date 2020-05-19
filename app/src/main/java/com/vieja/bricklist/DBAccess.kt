package com.vieja.bricklist

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*


class DBAccess private constructor(context: Context) {
    private val openHelper: SQLiteOpenHelper
    private var database: SQLiteDatabase? = null

    /**
     * Open the database connection.
     */
    fun open() {
        database = openHelper.writableDatabase
    }

    /**
     * Close the database connection.
     */
    fun close() {
        if (database != null) {
            database!!.close()
        }
    }

    /**
     * Read all quotes from the database.
     *
     * @return a List of quotes
     */
    val quotes: List<String>
        get() {
            val list: MutableList<String> =
                ArrayList()
            val cursor = database!!.rawQuery("SELECT * FROM Codes", null)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                list.add(cursor.getString(0))
                cursor.moveToNext()
            }
            cursor.close()
            return list
        }

    companion object {
        private var instance: DBAccess? = null

        /**
         * Return a singleton instance of DatabaseAccess.
         *
         * @param context the Context
         * @return the instance of DabaseAccess
         */
        fun getInstance(context: Context): DBAccess? {
            if (instance == null) {
                instance = DBAccess(context)
            }
            return instance
        }
    }

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    init {
        openHelper = DBOpenHelper(context)
    }
}