package com.wherego.user.Activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


import com.wherego.user.R;


public class TermsOfUseActivity extends AppCompatActivity {

    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_use);


        backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(view -> {onBackPressed();
        });
    }
}
