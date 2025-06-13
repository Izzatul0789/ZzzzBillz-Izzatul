package com.example.individualassignment_izzatul;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListViewActivity extends AppCompatActivity {

    private DataHelper dbHelper;
    private ListView listView;
    private ArrayList<String> billList;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        listView = findViewById(R.id.listView);
        dbHelper = new DataHelper(this);

        loadBills();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (cursor != null && cursor.moveToPosition(position)) {
                String month = cursor.getString(cursor.getColumnIndexOrThrow("month"));
                int units = cursor.getInt(cursor.getColumnIndexOrThrow("units"));
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
                double rebate = cursor.getDouble(cursor.getColumnIndexOrThrow("rebate"));
                double finalCost = cursor.getDouble(cursor.getColumnIndexOrThrow("final_cost"));
                String ageGroup = cursor.getString(cursor.getColumnIndexOrThrow("age_group"));
                int rowId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));

                String[] options = {"View Details", "Update Details", "Delete"};

                new AlertDialog.Builder(ListViewActivity.this)
                        .setTitle("Choose an option")
                        .setItems(options, (dialog, which) -> {
                            switch (which) {
                                case 0: // View Details
                                    Intent viewIntent = new Intent(ListViewActivity.this, ViewDetailsActivity.class);
                                    viewIntent.putExtra("month", month);
                                    viewIntent.putExtra("units", units);
                                    viewIntent.putExtra("total", total);
                                    viewIntent.putExtra("rebate", rebate);
                                    viewIntent.putExtra("final_cost", finalCost);
                                    viewIntent.putExtra("age_group", ageGroup);
                                    startActivity(viewIntent);
                                    break;

                                case 1: // Update Details
                                    Intent updateIntent = new Intent(ListViewActivity.this, UpdateDetailsActivity.class);
                                    updateIntent.putExtra("bill_id", rowId); // ✅ FIXED key
                                    updateIntent.putExtra("month", month);
                                    updateIntent.putExtra("units", units);
                                    updateIntent.putExtra("total", total);
                                    updateIntent.putExtra("rebate", rebate);
                                    updateIntent.putExtra("final_cost", finalCost);
                                    startActivityForResult(updateIntent, 101);
                                    break;

                                case 2: // Delete
                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                    db.delete("bill", "id=?", new String[]{String.valueOf(rowId)});
                                    loadBills(); // Refresh
                                    break;
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            loadBills(); // ✅ Refresh after update
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBills();
    }

    private void loadBills() {
        if (cursor != null) cursor.close();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM bill ORDER BY rowid DESC", null);

        billList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String month = cursor.getString(cursor.getColumnIndexOrThrow("month"));
                double finalCost = cursor.getDouble(cursor.getColumnIndexOrThrow("final_cost"));
                String display = month + "    RM" + String.format("%.2f", finalCost);
                billList.add(display);
            } while (cursor.moveToNext());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, billList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        if (cursor != null) cursor.close();
        dbHelper.close();
        super.onDestroy();
    }
}
