package com.example.individualassignment_izzatul;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewDetailsActivity extends AppCompatActivity {

    TextView textMonth, textUnits, textTotal, textRebate, textFinalCost, textAgeGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);

        // Link TextViews
        textMonth = findViewById(R.id.textMonth);
        textUnits = findViewById(R.id.textUnits);
        textTotal = findViewById(R.id.textTotal);
        textRebate = findViewById(R.id.textRebate);
        textFinalCost = findViewById(R.id.textFinalCost);
        textAgeGroup = findViewById(R.id.textAgeGroup);

        // Get data from Intent
        String month = getIntent().getStringExtra("month");
        int units = getIntent().getIntExtra("units", 0);
        double total = getIntent().getDoubleExtra("total", 0.0);
        double rebate = getIntent().getDoubleExtra("rebate", 0.0);
        double finalCost = getIntent().getDoubleExtra("final_cost", 0.0);
        String ageGroup = getIntent().getStringExtra("age_group");

        // Set values
        textMonth.setText(month);
        textUnits.setText(String.valueOf(units));
        textTotal.setText(String.format("RM %.2f", total));
        textRebate.setText(String.format("%.1f%%", rebate));
        textFinalCost.setText(String.format("RM %.2f", finalCost));
        textAgeGroup.setText(ageGroup);

        // Back button
        Button btnBack = findViewById(R.id.btnBack); // Make sure your XML uses id: btnBack
        btnBack.setOnClickListener(v -> finish()); // Go back to ListViewActivity
    }
}
