package com.example.shoppinglist.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*

class SqlHelper(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "Database"

        //Table myslist adding
        val TABLE_mylist = "mylist"
        val mylist_idmylist = "id_mylist"
        val mylist_title = "title"
        val mylist_detail = "detail"
        val mylist_update_at = "update_at"
        val mylist_color = "color"

        //Table detail adding
        val TABLE_detail = "listdetail"
        val detail_id_detail = "id_detail"
        val detail_title = "title"
        val detail_update_at = "update_at"
        val detail_status="status"
        val detail_id_mylist = "id_mylist"

        val sql2 ="SELECT mylist.* FROM mylist " +
                "INNER JOIN listdetail ON mylist.id_mylist = listdetail.id_mylist " +
                "ORDER BY mylist.id_mylist DESC "

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_mylist + "("
                + mylist_idmylist + " INTEGER PRIMARY KEY," + mylist_title + " TEXT,"
                + mylist_detail + " TEXT," + mylist_update_at+ " TEXT,"+ mylist_color+" TEXT )")
        db?.execSQL(CREATE_CONTACTS_TABLE)

        val CREATE_detail_TABLE = ("CREATE TABLE " + TABLE_detail + "("
                + detail_id_detail + " INTEGER PRIMARY KEY," + detail_title + " TEXT,"
                + detail_update_at + " TEXT,"+ detail_status + " TEXT,"+ detail_id_mylist+" INTEGER," +
                " FOREIGN KEY ("+detail_id_detail+") REFERENCES "+TABLE_mylist+"("+mylist_idmylist+"));")

        db?.execSQL(CREATE_detail_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_mylist)
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_detail)
        onCreate(db)
    }

    //page1
    fun add_tomylist(title : String, detail : String,color:String ){

        val values = ContentValues()
        values.put(mylist_title, title)
        values.put(mylist_detail, detail)
        values.put(mylist_update_at, getCurrentDate())
        values.put(mylist_color, color)
        val db = this.writableDatabase
        db.insert(TABLE_mylist, null, values)
        db.close()

    }
    fun getAllmylist(): Cursor {
        val db= this.readableDatabase
        var sql ="SELECT * FROM "+ TABLE_mylist+" ORDER BY id_mylist DESC"
        val sql2 ="SELECT mylist.*,COUNT(listdetail.id_detail) as count FROM mylist " +
                "INNER JOIN listdetail ON mylist.id_mylist = listdetail.id_mylist "

        val sql3="SELECT mylist.*,(SELECT COUNT(listdetail.id_detail) FROM listdetail WHERE listdetail.id_mylist = mylist.id_mylist ) as count, " +
                "(SELECT COUNT(listdetail.id_detail) FROM listdetail WHERE listdetail.id_mylist = mylist.id_mylist AND listdetail.status = '1' ) as checked " +
                "FROM mylist ORDER BY mylist.id_mylist DESC"
        return  db.rawQuery(sql3,null)
    }
    fun getCurrentDate():String{
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return sdf.format(Date())
    }

    fun deletelist(id:String){
        val db = this.writableDatabase
        db.delete(TABLE_detail, detail_id_mylist + "=?", arrayOf(id))
        db.delete(TABLE_mylist, mylist_idmylist + "=?", arrayOf(id))
    }

    //page2
    fun add_todetail(title : String, id : String ){
        val values = ContentValues()
        values.put(detail_title, title)
        values.put(detail_id_mylist, id)
        values.put(detail_status, "0")
        values.put(detail_update_at, getCurrentDate())
        val db = this.writableDatabase
        db.insert(TABLE_detail, null, values)
        db.close()

    }
    fun getAlldetail(id : String): Cursor {
        val db= this.readableDatabase
        var sql ="SELECT * FROM "+ TABLE_detail+" WHERE "+ detail_id_mylist+" ="+id
        //var sql ="SELECT * FROM "+ TABLE_detail
        return  db.rawQuery(sql,null)
    }

    fun updateCheckbok(id:String,status:String){
        val values = ContentValues()
        values.put(detail_status, status)
        val db = this.writableDatabase
        val whereclause = "$detail_id_detail=?"
        val whereargs = arrayOf(id)
        db.update(TABLE_detail,values,whereclause,whereargs)
        db.close()
    }

    fun deletedetail(id:String){
        val db = this.writableDatabase
        db.delete(TABLE_detail, detail_id_detail + "=?", arrayOf(id))
    }

}