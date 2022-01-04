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


    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_mylist + "("
                + mylist_idmylist + " INTEGER PRIMARY KEY," + mylist_title + " TEXT,"
                + mylist_detail + " TEXT," + mylist_update_at+ " TEXT,"+ mylist_color+" TEXT )")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_mylist)
        onCreate(db)
    }

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
        return  db.rawQuery(sql,null)
    }
    fun getCurrentDate():String{
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return sdf.format(Date())
    }
}