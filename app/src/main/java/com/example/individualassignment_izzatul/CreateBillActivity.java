package com.example.individualassignment_izzatul;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateBillActivity extends AppCompatActivity {

    private EditText editUnits, editRebate;
    private Spinner spinnerMonth;
    private RadioGroup radioGroupAge;
    private Button buttonCalculate, buttonSave;
    private TextView textTotalCharge, textFinalCost;

    private DataHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bill);

        // Initialize views
        dbHelper = new DataHelper(this);

        editUnits = findViewById(R.id.editUnits);
        editRebate = findViewById(R.id.editRebate);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        radioGroupAge = findViewById(R.id.radioGroupAge);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        buttonSave = findViewById(R.id.buttonSave);
        textTotalCharge = findViewById(R.id.textTotalCharge);
        textFinalCost = findViewById(R.id.textFinalCost);

        // Set up spinner with months
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.months_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateCharges();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBillData();
            }
        });
    }

    private void calculateCharges() {
        try {
            int units = Integer.parseInt(editUnits.getText().toString());
            String rebateStr = editRebate.getText().toString();
            double rebate = rebateStr.isEmpty() ? 0.0 : Double.parseDouble(rebateStr);

            if (rebate > 5.0) {
                Toast.makeText(CreateBillActivity.this, "Maximum rebate allowed is 5%", Toast.LENGTH_SHORT).show();
                return;
            }

            double totalCharge = computeTotalCharge(units);
            double finalCost = totalCharge - (totalCharge * rebate / 100);

            textTotalCharge.setText(String.format("Total Charge: RM %.2f", totalCharge));
            textFinalCost.setText(String.format("Final Cost after Rebate: RM %.2f", finalCost));
        } catch (NumberFormatException e) {
            Toast.makeText(CreateBillActivity.this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBillData() {
        try {
            String month = spinnerMonth.getSelectedItem().toString();
            int units = Integer.parseInt(editUnits.getText().toString());
            String rebateStr = editRebate.getText().toString();
            double rebate = rebateStr.isEmpty() ? 0.0 : Double.parseDouble(rebateStr);

            if (rebate > 5.0) {
                Toast.makeText(CreateBillActivity.this, "Maximum rebate allowed is 5%", Toast.LENGTH_SHORT).show();
                return;
            }

            double totalCharge = computeTotalCharge(units);
            double finalCost = totalCharge - (totalCharge * rebate / 100);

            // Get selected age group radio button
            int selectedId = radioGroupAge.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(CreateBillActivity.this, "Please select an age group", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton selectedRadio = findViewById(selectedId);
            String ageGroup = selectedRadio.getText().toString();

            // Save to DB
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("month", month);
            values.put("units", units);
            values.put("total", totalCharge);
            values.put("rebate", rebate);
            values.put("final_cost", finalCost);
            values.put("age_group", ageGroup);

            long result = db.insert("bill", null, values);

            if (result != -1) {
                Toast.makeText(CreateBillActivity.this, "Data Successfully Added", Toast.LENGTH_SHORT).show();
                editUnits.setText("");
                editRebate.setText("");
                radioGroupAge.clearCheck();
                textTotalCharge.setText("Total Charge: RM 0.00");
                textFinalCost.setText("Final Cost after Rebate: RM 0.00");
                Intent intent = new Intent(CreateBillActivity.this, ListViewActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(CreateBillActivity.this, "Error saving data", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(CreateBillActivity.this, "Please enter valid numbers before saving", Toast.LENGTH_SHORT).show();
        }
    }

    private double computeTotalCharge(int units) {
        double charge = 0.0;

        if (units <= 200) {
            charge = units * 0.218;
        } else if (units <= 300) {
            charge = (200 * 0.218) + ((units - 200) * 0.334);
        } else if (units <= 600) {
            charge = (200 * 0.218) + (100 * 0.334) + ((units - 300) * 0.516);
        } else {
            charge = (200 * 0.218) + (100 * 0.334) + (300 * 0.516) + ((units - 600) * 0.546);
        }

        return charge;
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
