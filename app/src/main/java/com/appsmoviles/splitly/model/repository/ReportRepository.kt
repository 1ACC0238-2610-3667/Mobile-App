package com.appsmoviles.splitly.model.repository

import android.content.ContentValues
import android.content.Context
import com.appsmoviles.splitly.model.beans.Report
import com.appsmoviles.splitly.model.db.OpenHelper

class ReportRepository(context: Context) {
    private val dbHelper = OpenHelper(context)

    fun saveReport(report: Report): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("title", report.title)
            put("date", report.date)
            put("total_amount", report.totalAmount)
            put("details_json", report.detailsJson)
        }
        return db.insert("reports", null, values)
    }

    fun getAllReports(): List<Report> {
        val reports = mutableListOf<Report>()
        val db = dbHelper.readableDatabase
        val cursor = db.query("reports", null, null, null, null, null, "id DESC")

        with(cursor) {
            while (moveToNext()) {
                reports.add(
                    Report(
                        id = getInt(getColumnIndexOrThrow("id")),
                        title = getString(getColumnIndexOrThrow("title")),
                        date = getString(getColumnIndexOrThrow("date")),
                        totalAmount = getDouble(getColumnIndexOrThrow("total_amount")),
                        detailsJson = getString(getColumnIndexOrThrow("details_json"))
                    )
                )
            }
        }
        cursor.close()
        return reports
    }
}
