package com.appsmoviles.splitly.model.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class OpenHelper(context: Context): SQLiteOpenHelper(
    context, "offlineDb.db", null, 2
){
    override fun onCreate(db: SQLiteDatabase?) {
        val queries = listOf(
            "create table users(_ID integer primary key autoincrement, name text, age integer, email text)",
            "create table reports(id integer primary key autoincrement, title text, date text, total_amount real, details_json text)"
        )

        queries.forEach {
            db!!.execSQL(it)
        }
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        p1: Int,
        p2: Int
    ) {
        val queries = listOf("drop table if exists users", "drop table if exists reports")

        queries.forEach {
            db!!.execSQL(it)
        }
        onCreate(db)
    }

}