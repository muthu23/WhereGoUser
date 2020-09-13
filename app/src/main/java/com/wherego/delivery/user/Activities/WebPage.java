package com.wherego.delivery.user.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.wherego.delivery.user.Helper.CustomDialog;
import com.wherego.delivery.user.R;

public class WebPage extends AppCompatActivity {

    TextView tvPage;
    ImageView imgBack;
    String url = "";
    WebView webView;
    CustomDialog customDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_page);

        customDialog = new CustomDialog(WebPage.this);
        customDialog.show();
        String page = getIntent().getStringExtra("page");
        url = getIntent().getStringExtra("url");
        tvPage = findViewById(R.id.tvPage);
        imgBack = findViewById(R.id.imgBack);
        tvPage.setText(page);
        webView = findViewById(R.id.webView);


        imgBack.setOnClickListener(v -> onBackPressed());

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                customDialog.dismiss();
                webView.loadUrl("javascript:(function() { " +
                        "document.getElementsByClassName('container')[0].style.display='none'; })()");
                webView.loadUrl("javascript:(function() { " +
                        "document.getElementsByClassName('footer-topborder')[0].style.display='none'; })()");
                webView.loadUrl("javascript:(function() { " +
                        "document.getElementsByClassName('bottom-footer')[0].style.display='none'; })()");
                webView.loadUrl("javascript:(function() { " +
                        "document.getElementsByClassName('citesandcountries')[0].style.display='none'; })()");
            }
        });
        webView.loadUrl(url);
    }
}
