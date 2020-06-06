package com.vieja.bricklist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteStatement
import android.os.AsyncTask
import android.util.Log
import android.util.Xml
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.StringReader
import java.io.StringWriter
import java.net.URL
import java.net.URLConnection
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory


class DBAccess private constructor(context: Context) {
    private val openHelper: SQLiteOpenHelper
    private var database: SQLiteDatabase? = null

    fun open() {
        database = openHelper.writableDatabase
    }

    fun addProject(id: Int, name: String): Long {
        var LastAccessed = System.currentTimeMillis()
        val cv = ContentValues()
        cv.put("Name", name)
        cv.put("LastAccessed", LastAccessed)
        val newRowId = database!!.insert("INVENTORIES", null, cv)
        return newRowId
    }

    fun addComponents(projectID: Long, result: String): MutableList<String> {
        val doc = convertStringToXMLDocument(result)
        val nodesList = doc!!.getElementsByTagName("ITEM")
        val partsNotInDB: MutableList<String> =
            ArrayList()
        for (i in 0 until nodesList.length) {
            val node: Element = nodesList.item(i) as Element
            val itemType = node.getElementsByTagName("ITEMTYPE").item(0).textContent
            val typeID = findTypeID(itemType)

            val colorCode = node.getElementsByTagName("COLOR").item(0).textContent
            val colorID = findColorID(colorCode)

            val Code = node.getElementsByTagName("ITEMID").item(0).textContent
            val itemID = findItemID(Code)

            val cv = ContentValues()
            cv.put("InventoryID", projectID)
            cv.put("TypeID", typeID)
            cv.put("ItemID", itemID)
            cv.put("ColorID", colorID)
            cv.put("QuantityInSet", node.getElementsByTagName("QTY").item(0).textContent)
            cv.put("Extra", node.getElementsByTagName("EXTRA").item(0).textContent)
            if (itemID != -1) {
                database!!.insert("InventoriesParts", null, cv)
            } else {
                partsNotInDB.add(Code)
                partsNotInDB.add(colorCode)
            }
        }
        return partsNotInDB
    }

    private fun findItemID(Code: String?): Int {
        var cur = database!!.rawQuery("SELECT id FROM Parts WHERE Code = \"" + Code + "\"", null)
        cur.moveToFirst()
        if (cur.isAfterLast) return -1
        else return cur.getInt(0)
    }

    private fun findColorID(colorCode: String?): Int {
        var cur =
            database!!.rawQuery("SELECT id FROM Colors WHERE Code = \"" + colorCode + "\"", null)
        cur.moveToFirst()
        return cur.getInt(0)
    }

    private fun findTypeID(itemType: String?): Int {
        var cur =
            database!!.rawQuery("SELECT id FROM ItemTypes WHERE CODE = \"" + itemType + "\"", null)
        cur.moveToFirst()
        return cur.getInt(0)
    }

    private fun convertStringToXMLDocument(xmlString: String): Document? {
        val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
        var builder: DocumentBuilder? = null
        try {
            builder = factory.newDocumentBuilder()
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
            val pr = Project(cursor.getString(0), cursor.getInt(1))
            list.add(pr)
            cursor.moveToNext()
        }
        cursor.close()
        return list
    }

    fun getAllProjects(): ArrayList<Project> {
        val list = ArrayList<Project>()
        val cursor = database!!.rawQuery("SELECT Name, id FROM Inventories", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val pr = Project(cursor.getString(0), cursor.getInt(1))
            list.add(pr)
            cursor.moveToNext()
        }
        cursor.close()
        return list
    }

    fun getComponentsOfProject(id: Int): List<Component> {
        val list = ArrayList<Component>()
        val cursor = database!!.rawQuery(
            "SELECT i.id, coalesce(p.NamePL, p.Name), coalesce(c.NamePL, c.Name), i.QuantityInStore, i.QuantityInSet, p.id, i.ColorID\n" +
                    "FROM InventoriesParts i, Colors c, Parts p\n" +
                    "where i.ColorID = c.id AND\n" +
                    " i.ItemID = p.id AND\n" +
                    " i.InventoryID = " + id, null
        )
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            var byteArray = getImage(cursor.getString(5), cursor.getString(6))
            val quantity = cursor.getString(3) + " of " + cursor.getString(4)
            val pr = Component(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                quantity,
                byteArray
            )
            list.add(pr)
            cursor.moveToNext()
        }
        cursor.close()
        return list
    }

    private fun getImage(itemID: String, colorID: String): ByteArray? {
        val cursor = database!!.rawQuery(
            "SELECT Image, Code, id from Codes where ItemID=" + itemID + " and ColorID=" + colorID,
            null
        )
        cursor.moveToFirst()
        if (cursor.isAfterLast) {
            //Klocek jest stary, nie ma DesignID

            val cursor2 = database!!.rawQuery("SELECT Code from Parts where id = " + itemID, null)
            cursor2.moveToFirst()
            var name = cursor2.getString(0)

            val cursor4 = database!!.rawQuery("SELECT Code from Colors where id = " + colorID, null)
            cursor4.moveToFirst()
            var color = cursor4.getInt(0)

            val cv = ContentValues()
            cv.put("ItemID", itemID)
            cv.put("ColorID", colorID)
            val id_nowego = database!!.insert("Codes", null, cv)

            val downloadIMG = DownloadImg(this, id_nowego.toInt())
            downloadIMG.execute("http://img.bricklink.com/P/" + color + "/" + name + ".gif")

            cursor2.close()
            cursor4.close()

        } else {
            val img = cursor.getBlob(0)
            if (img == null) {
                val downloadIMG = DownloadImg(this, cursor.getInt(2))
                downloadIMG.execute("https://www.lego.com/service/bricks/5/2/" + cursor.getInt(1))
                cursor.close()

                val cursor3 = database!!.rawQuery(
                    "SELECT Image from Codes where ItemID=" + itemID + " and ColorID=" + colorID,
                    null
                )
                cursor3.moveToFirst()
                if (!cursor3.isAfterLast) return cursor3.getBlob(0)
            }
            return img
        }
        return null
    }

    fun updateQuantity(add: Boolean, id: Int): String {
        val cursor = database!!.rawQuery(
            "SELECT QuantityInSet, QuantityInStore from InventoriesParts where id=" + id,
            null
        )
        cursor.moveToFirst()
        val inSet = cursor.getInt(0)
        var inStore = cursor.getInt(1)
        if (add) {
            if (inStore < inSet) {
                inStore += 1
                val sql =
                    "UPDATE InventoriesParts SET QuantityInStore = " + inStore + " WHERE id = " + id
                val insertStmt: SQLiteStatement = database!!.compileStatement(sql)
                insertStmt.clearBindings()
                insertStmt.executeUpdateDelete()
            }
        } else {
            if (inStore > 0) {
                inStore -= 1
                val sql =
                    "UPDATE InventoriesParts SET QuantityInStore = " + inStore + " WHERE id = " + id
                val insertStmt: SQLiteStatement = database!!.compileStatement(sql)
                insertStmt.clearBindings()
                insertStmt.executeUpdateDelete()
            }
        }
        return "" + inStore + " of " + inSet
    }

    fun deactivateProject(id: Int): Int {
        val cursor = database!!.rawQuery("SELECT Active from Inventories where id=" + id, null)
        cursor.moveToFirst()
        val active = cursor.getInt(0)
        var sql = ""
        if (active == 1) sql = "UPDATE Inventories SET Active = 0 WHERE id = " + id
        else sql = "UPDATE Inventories SET Active = 1 WHERE id = " + id
        val insertStmt: SQLiteStatement = database!!.compileStatement(sql)
        insertStmt.clearBindings()
        insertStmt.executeUpdateDelete()
        return active
    }

    fun checkIfActive(id: Int): Int {
        val cursor = database!!.rawQuery("SELECT Active from Inventories where id=" + id, null)
        cursor.moveToFirst()
        return cursor.getInt(0)
    }

    fun getXML(id: Int): String {
        val xmlSerializer = Xml.newSerializer()
        val writer = StringWriter()
        xmlSerializer.setOutput(writer)
        xmlSerializer.startDocument("UTF-8", false)

        val cursor = database!!.rawQuery(
            "SELECT t.Code, p.Code, c.Code, i.QuantityInSet-i.QuantityInStore  from InventoriesParts i, ItemTypes t, Parts p, Colors c\n" +
                    "where i.TypeID = t.id and i.ItemID = p.id and i.ColorID = c.id\n" +
                    "and i.QuantityInSet-i.QuantityInStore > 0", null
        )

        cursor.moveToFirst()

        xmlSerializer.startTag("", "INVENTORY")
        while (!cursor.isAfterLast) {

            xmlSerializer.startTag("", "ITEM")

            xmlSerializer.startTag("", "ITEMTYPE")
            xmlSerializer.text(cursor.getString(0))
            xmlSerializer.endTag("", "ITEMTYPE")

            xmlSerializer.startTag("", "ITEMID")
            xmlSerializer.text(cursor.getString(1))
            xmlSerializer.endTag("", "ITEMID")

            xmlSerializer.startTag("", "COLOR")
            xmlSerializer.text(cursor.getInt(2).toString())
            xmlSerializer.endTag("", "COLOR")

            xmlSerializer.startTag("", "QTYFILLED")
            xmlSerializer.text(cursor.getInt(3).toString())
            xmlSerializer.endTag("", "QTYFILLED")

            xmlSerializer.endTag("", "ITEM")

            cursor.moveToNext()
        }
        xmlSerializer.endTag("", "INVENTORY")
        xmlSerializer.endDocument()

        val result = writer.toString().replace("--><", "-->\n<")
        val res = result.substring(55).replace("<", "\n<").replace("\n</", "</")
        return res

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

        private class DownloadImg(val dbAccess: DBAccess, val id: Int) :
            AsyncTask<String, Void, String>() {
            private val TAG = "DownloadImg"

            override fun doInBackground(vararg url: String?): String {
                val byteArray = getLogoImage(url[0])
                if (byteArray != null) {
                    val dbAccess = dbAccess
                    dbAccess.open()
                    val sql =
                        "UPDATE Codes SET Image = ? WHERE id = " + id
                    val insertStmt: SQLiteStatement = dbAccess.database!!.compileStatement(sql)
                    insertStmt.clearBindings()
                    insertStmt.bindBlob(1, byteArray)
                    var res = insertStmt.executeUpdateDelete()
                    return res.toString()
                }
                return "doInBackground: byte Array is null"
            }

            private fun getLogoImage(url: String?): ByteArray? {
                try {
                    val imageUrl = URL(url)
                    val ucon: URLConnection = imageUrl.openConnection()

                    val `is`: InputStream = ucon.getInputStream()

                    val baos = ByteArrayOutputStream()
                    val buffer = ByteArray(1024)
                    var read = 0

                    while (`is`.read(buffer, 0, buffer.size).also({ read = it }) != -1) {
                        baos.write(buffer, 0, read)
                    }

                    baos.flush()
                    return baos.toByteArray()
                } catch (e: java.lang.Exception) {
                    Log.d("ImageManager", "Error: $e")
                }
                return null
            }
        }
    }

    init {
        openHelper = DBOpenHelper(context)
    }
}