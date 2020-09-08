package com.wherego.user.Activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import com.wherego.user.Helper.SharedHelper;
import com.wherego.user.Helper.URLHelper;
import com.wherego.user.R;
import com.wherego.user.MyCourier;


public class Profile extends AppCompatActivity implements View.OnClickListener {
    ImageView backArrow;
    TextView txtuserName,txtEdituser,txtVehiclename,txtvehicleEdit,txtPassword;
    CircleImageView img_profile;
    ImageView img_car;
    Button btnLogout;
    GoogleApiClient mGoogleApiClient;
    Locale myLocale;
    TextView txtReview,txtPoint;
    String currentLanguage = "en", currentLang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        currentLanguage = getIntent().getStringExtra(currentLang);
        findview();
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void findview() {
        img_profile= findViewById(R.id.img_profile);
        img_car= findViewById(R.id.img_car);
        backArrow = findViewById(R.id.backArrow);
        txtuserName= findViewById(R.id.txtuserName);
        txtEdituser= findViewById(R.id.txtEdituser);
        txtVehiclename= findViewById(R.id.txtVehiclename);
        txtvehicleEdit= findViewById(R.id.txtvehicleEdit);
        txtPassword= findViewById(R.id.txtPassword);
        txtPoint  =  findViewById(R.id.txtPoint);
        btnLogout= findViewById(R.id.btnLogout);

        txtReview = findViewById(R.id.txtReview);
        txtVehiclename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getPackageName(); // package name of the app
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        txtuserName.setText(SharedHelper.getKey(Profile.this, "first_name"));
        if(!SharedHelper.getKey(Profile.this, "picture").isEmpty()){
        Picasso.get().load(SharedHelper.getKey(Profile.this, "picture"))
                .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .into(img_profile);
        }
        if (!SharedHelper.getKey(Profile.this, "service_image").isEmpty()) {
            Picasso.get().load(SharedHelper.getKey(Profile.this, "service_image"))
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .into(img_car);
        }
        txtEdituser.setOnClickListener(this);
        txtPassword.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        txtReview.setOnClickListener(this);
        txtPoint.setOnClickListener(this);
    }






    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.txtEdituser)
        {
            startActivity(new Intent(Profile.this,EditProfile.class));
        }
        if(v.getId()==R.id.txtPoint)
        {
//            startActivity(new Intent(Profile.this,RewardedActivity.class));
        }
        if (v.getId()==R.id.txtPassword)
        {
            startActivity(new Intent(Profile.this, ChangePassword.class));
        }

        if (v.getId()==R.id.txtReview)
        {
            startActivity(new Intent(Profile.this, UserReview.class));
        }
        if (v.getId()==R.id.btnLogout)
        {
            showLogoutDialog();
        }


    }

    private void showLogoutDialog() {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Logout");
            builder.setMessage(getString(R.string.logout_alert));

            builder.setPositiveButton(R.string.yes,
                    (dialog, which) -> logout());

            builder.setNegativeButton(R.string.no, (dialog, which) -> {
                //Reset to previous seletion menu in navigation
                dialog.dismiss();

            });
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setOnShowListener(arg -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
            });
            dialog.show();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public void logout() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", SharedHelper.getKey(getApplicationContext(), "id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.LOGOUT,
                        object,
                        response -> {


                            if (SharedHelper.getKey(getApplicationContext(), "login_by").equals("facebook"))
                                LoginManager.getInstance().logOut();
                            if (SharedHelper.getKey(getApplicationContext(), "login_by").equals("google"))
                                signOut();
                            if (!SharedHelper.getKey(Profile.this, "account_kit_token").equalsIgnoreCase("")) {
                                Log.e("MainActivity", "Account kit logout: " + SharedHelper.getKey(Profile.this, "account_kit_token"));

                                SharedHelper.putKey(Profile.this, "account_kit_token", "");
                            }



                            //SharedHelper.putKey(context, "email", "");
                            SharedHelper.clearSharedPreferences(Profile.this);
                            Intent goToLogin = new Intent(Profile.this, LoginActivity.class);
                            goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(goToLogin);
                            finish();
                        }, error -> {
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.getString("message"));
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                /*refreshAccessToken();*/
                            } else if (response.statusCode == 422) {

                                json = MyCourier.trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    displayMessage(json);
                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

                            } else if (response.statusCode == 503) {
                                displayMessage(getString(R.string.server_down));
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            displayMessage(getString(R.string.something_went_wrong));
                        }

                    } else {
                        if (error instanceof NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            logout();
                        }
                    }
                }) {
                    @Override
                    public java.util.Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        Log.e("getHeaders: Token", SharedHelper.getKey(getApplicationContext(), "access_token") + SharedHelper.getKey(getApplicationContext(), "token_type"));
                        headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(getApplicationContext(), "access_token"));
                        return headers;
                    }
                };

        MyCourier.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Snackbar.make(findViewById(R.id.img_profile), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //taken from google api console (Web api client id)
//                .requestIdToken("795253286119-p5b084skjnl7sll3s24ha310iotin5k4.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                FirebaseAuth.getInstance().signOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> {
                        if (status.isSuccess()) {
                            Log.d("MainAct", "Google User Logged out");
                           /* Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();*/
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("MAin", "Google API Client Connection Suspended");
            }
        });
    }

}
