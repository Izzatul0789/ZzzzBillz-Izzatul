package com.example.individualassignment_izzatul;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutMeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);  // your XML layout file

        // GitHub link
        TextView githubLink = findViewById(R.id.githubLink);
        githubLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/yourusername/yourproject")); // Change this!
                startActivity(browserIntent);
            }
        });

        // Let's Start button
        Button btnStart = findViewById(R.id.btnStart); // Make sure this matches XML id
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutMeActivity.this, WelcomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
