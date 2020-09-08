package com.wherego.user.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.wherego.user.Helper.SharedHelper;
import com.wherego.user.R;
import com.wherego.user.Utils.Utilities;


public class ReferActivity extends AppCompatActivity {
    ImageView imgback;
    TextView txtWhatsapp;
    TextView tvCode;
    ImageView imgShare;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(this,"selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        setContentView(R.layout.activity_refer);

        imgback =findViewById(R.id.imgback);
        txtWhatsapp = findViewById(R.id.txtWhatsapp);
        tvCode  =  findViewById(R.id.tvCode);
        imgShare =  findViewById(R.id.imgShare);
        imgback.setOnClickListener(v -> onBackPressed());
        imgShare.setOnClickListener(v -> shareContent());
        txtWhatsapp.setOnClickListener(v -> sharecontentWhatsapp());
        tvCode.setText( SharedHelper.getKey(getApplicationContext(), "referral_code"));
    }

    private void shareContent() {

        String contents = getString(R.string.share_greet)+
                tvCode.getText().toString()+getString(R.string.share_signup);
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, contents);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ReferActivity.this, getString(R.string.share_application_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    private void sharecontentWhatsapp() {

        String contents = getString(R.string.share_greet)+
                tvCode.getText().toString()+getString(R.string.share_signup)+
                " https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName();
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, contents);
        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            new Utilities().showAlert(ReferActivity.this,getString(R.string.whatsapp_not_found));

        }
    }
}
