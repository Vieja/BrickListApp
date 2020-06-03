package com.vieja.bricklist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.StringReader
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.collections.ArrayList


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

    fun addProject(id:Int, name:String ): Long {
        var LastAccessed = System.currentTimeMillis()
        val cv = ContentValues()
        cv.put("id",id)
        cv.put("Name",name)
        cv.put("LastAccessed",LastAccessed)
        val newRowId = database!!.insert("INVENTORIES",null,cv)
        return newRowId
    }

    fun addComponents(projectID: Long, result: String) {
        val doc = convertStringToXMLDocument(result)
        val nodesList = doc!!.getElementsByTagName("ITEM")
        for (i in 0 until nodesList.length) {
            val node : Element = nodesList.item(i) as Element

            val itemType = node.getElementsByTagName("ITEMTYPE").item(0).textContent
            val typeID = findTypeID(itemType)

            val colorCode = node.getElementsByTagName("COLOR").item(0).textContent
            val colorID = findColorID(colorCode)

            val cv = ContentValues()
            cv.put("InventoryID", projectID)
            cv.put("TypeID", typeID)
            cv.put("ItemID", node.getElementsByTagName("ITEMID").item(0).textContent)
            cv.put("ColorID", colorID)
            cv.put("QuantityInSet", node.getElementsByTagName("QTY").item(0).textContent)
            cv.put("Extra", node.getElementsByTagName("EXTRA").item(0).textContent)
            database!!.insert("InventoriesParts",null,cv)
        }
    }

    private fun findColorID(colorCode: String?): String {
        var cur =  database!!.rawQuery("SELECT id FROM Colors WHERE Code = \""+colorCode+"\"",null)
        cur.moveToFirst()
        return cur.getString(0)
    }

    private fun findTypeID(itemType: String?): String {
        var cur =  database!!.rawQuery("SELECT id FROM ItemTypes WHERE CODE = \""+itemType+"\"",null)
        cur.moveToFirst()
        return cur.getString(0)
    }

    private fun convertStringToXMLDocument(xmlString: String): Document? {
        //Parser that produces DOM object trees from XML content
        val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()

        //API to obtain DOM Document instance
        var builder: DocumentBuilder? = null
        try {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder()

            //Parse the content to Document object
            return builder.parse(InputSource(StringReader(xmlString)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getActiveProjects(): ArrayList<Project> {
        val list = ArrayList<Project>()
        val cursor = database!!.rawQuery("SELECT Name, id FROM Inventories WHERE Active = 1", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val pr = Project(cursor.getString(0),cursor.getString(1).toInt())
            list.add(pr)
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