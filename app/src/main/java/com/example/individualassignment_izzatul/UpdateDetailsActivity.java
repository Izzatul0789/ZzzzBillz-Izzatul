package com.example.individualassignment_izzatul;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateDetailsActivity extends AppCompatActivity {

    private EditText editMonth, editUnits, editRebate;
    private TextView textTotalCharge, textFinalCost;
    private Button buttonCalculate, buttonUpdate;

    private DataHelper dbHelper;
    private int billId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_details);

        dbHelper = new DataHelper(this);

        editMonth = findViewById(R.id.editMonth);
        editUnits = findViewById(R.id.editUnits);
        editRebate = findViewById(R.id.editRebate);
        textTotalCharge = findViewById(R.id.textTotalCharge);
        textFinalCost = findViewById(R.id.textFinalCost);
        buttonCalculate = findViewById(R.id.btnCalculate);
        buttonUpdate = findViewById(R.id.btnUpdate);

        // Get data from intent
        billId = getIntent().getIntExtra("bill_id", -1);
        String month = getIntent().getStringExtra("month");
        int units = getIntent().getIntExtra("units", 0);
        double total = getIntent().getDoubleExtra("total", 0.0);
        double rebate = getIntent().getDoubleExtra("rebate", 0.0);
        double finalCost = getIntent().getDoubleExtra("final_cost", 0.0);

        // Set data in fields
        editMonth.setText(month);
        editUnits.setText(String.valueOf(units));
        editRebate.setText(String.valueOf(rebate));
        textTotalCharge.setText(String.format("Total Charge: RM %.2f", total));
        textFinalCost.setText(String.format("Final Cost after Rebate: RM %.2f", finalCost));

        buttonCalculate.setOnClickListener(v -> calculateCharges());

        buttonUpdate.setOnClickListener(v -> updateBillData());
    }

    private void calculateCharges() {
        try {
            int units = Integer.parseInt(editUnits.getText().toString());
            String rebateStr = editRebate.getText().toString();
            double rebate = rebateStr.isEmpty() ? 0.0 : Double.parseDouble(rebateStr);

            if (rebate > 5.0) {
                Toast.makeText(this, "Maximum rebate allowed is 5%", Toast.LENGTH_SHORT).show();
                return;
            }

            double totalCharge = computeTotalCharge(units);
            double finalCost = totalCharge - (totalCharge * rebate / 100);

            textTotalCharge.setText(String.format("Total Charge: RM %.2f", totalCharge));
            textFinalCost.setText(String.format("Final Cost after Rebate: RM %.2f", finalCost));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBillData() {
        try {
            String month = editMonth.getText().toString().trim();
            int units = Integer.parseInt(editUnits.getText().toString());
            String rebateStr = editRebate.getText().toString();
            double rebate = rebateStr.isEmpty() ? 0.0 : Double.parseDouble(rebateStr);

            if (rebate > 5.0) {
                Toast.makeText(this, "Maximum rebate allowed is 5%", Toast.LENGTH_SHORT).show();
                return;
            }

            double totalCharge = computeTotalCharge(units);
            double finalCost = totalCharge - (totalCharge * rebate / 100);

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("month", month);
            values.put("units", units);
            values.put("total", totalCharge);
            values.put("rebate", rebate);
            values.put("final_cost", finalCost);

            int rowsAffected = db.update("bill", values, "id=?", new String[]{String.valueOf(billId)});

            if (rowsAffected > 0) {
                Toast.makeText(this, "Bill updated successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish(); // Close activity
            } else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private double computeTotalCharge(int units) {
        if (units <= 200) {
            return units * 0.218;
        } else if (units <= 300) {
            return (200 * 0.218) + ((units - 200) * 0.334);
        } else if (units <= 600) {
            return (200 * 0.218) + (100 * 0.334) + ((units - 300) * 0.516);
        } else {
            return (200 * 0.218) + (100 * 0.334) + (300 * 0.516) + ((units - 600) * 0.546);
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
