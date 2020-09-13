package com.wherego.delivery.user.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.wherego.delivery.user.Helper.URLHelper;
import com.wherego.delivery.user.R;


public class LegalActivity extends AppCompatActivity {

    private ImageView backArrow;
    private TextView
            termsConditionTextView,
            privacyPolicyTextView,
            copyrightTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal);
        backArrow = findViewById(R.id.backArrow);
        termsConditionTextView = findViewById(R.id.termsConditionTextView);
        privacyPolicyTextView = findViewById(R.id.privacyPolicyTextView);
        copyrightTextView = findViewById(R.id.copyrightTextView);

        backArrow.setOnClickListener(view -> onBackPressed());

        privacyPolicyTextView.setOnClickListener(v -> {
            Intent intent =new Intent(LegalActivity.this,WebPage.class);
            intent.putExtra("page",getString(R.string.privacy_policy));
            intent.putExtra("url", URLHelper.privacyPolicy);
            startActivity(intent);
        });

        termsConditionTextView.setOnClickListener(v ->
        {
            Intent intent =new Intent(LegalActivity.this,WebPage.class);
            intent.putExtra("page",getString(R.string.terms_amp_conditions));
            intent.putExtra("url", URLHelper.termcondition);
            startActivity(intent);
        });

        copyrightTextView.setOnClickListener(v -> {
            Intent intent =new Intent(LegalActivity.this,WebPage.class);
            intent.putExtra("page","Refund Policy");
            intent.putExtra("url", URLHelper.refund);
            startActivity(intent);
        });



    }
}
