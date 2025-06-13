package com.example.individualassignment_izzatul;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper {

    // Database Name and Version
    private static final String DATABASE_NAME = "electricity.db";
    private static final int DATABASE_VERSION = 2;

    // Constructor
    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create bill table with age_group column
        String sql = "CREATE TABLE bill (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "month TEXT, " +
                "units INTEGER, " +
                "total REAL, " +
                "rebate REAL, " +
                "final_cost REAL, " +
                "age_group TEXT);";
        Log.d("DataHelper", "onCreate: " + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop old table if exists and recreate
        Log.d("DataHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS bill");
        onCreate(db);
    }

    // Insert a new bill
    public boolean insertBill(String month, int units, double total, double rebate, double finalCost, String ageGroup) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("month", month);
        values.put("units", units);
        values.put("total", total);
        values.put("rebate", rebate);
        values.put("final_cost", finalCost);
        values.put("age_group", ageGroup);

        long result = db.insert("bill", null, values);
        return result != -1;
    }

    // Delete a bill by ID
    public int deleteBillById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("bill", "id = ?", new String[]{String.valueOf(id)});
    }

    // ✅ Corrected update method to match your usage
    public boolean updateBillById(int id, String month, int units, double total, double rebate, double finalCost, String ageGroup) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("month", month);
        values.put("units", units);
        values.put("total", total);
        values.put("rebate", rebate);
        values.put("final_cost", finalCost);
        values.put("age_group", ageGroup);

        int result = db.update("bill", values, "id = ?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // Get all bills
    public Cursor getAllBills() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM bill", null);
    }

    // Optional: Get bill by ID
    public Cursor getBillById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM bill WHERE id = ?", new String[]{String.valueOf(id)});
    }
}
