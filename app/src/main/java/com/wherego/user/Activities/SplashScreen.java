package com.wherego.user.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wherego.user.R;
import com.wherego.user.Helper.ConnectionHelper;
import com.wherego.user.Helper.SharedHelper;
import com.wherego.user.Helper.URLHelper;
import com.wherego.user.MyCourier;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.wherego.user.MyCourier.trimMessage;


public class SplashScreen extends AppCompatActivity {

    String TAG = "SplashActivity";
    public Activity activity = SplashScreen.this;
    public Context context = SplashScreen.this;
    ConnectionHelper helper;
    Boolean isInternet;
    String device_token, device_UDID;
    Handler handleCheckStatus;
    int retryCount = 0;
    AlertDialog alert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ConnectivityManager connectivityManager =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
        Log.e("hashkey",printKeyHash(SplashScreen.this)+"");

        handleCheckStatus = new Handler();

        //check status every 3 sec

        checkIPaddress();
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        handleCheckStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.w("Handler", "Called");
                if (helper.isConnectingToInternet()) {
                    if (SharedHelper.getKey(context,"loggedIn").equalsIgnoreCase(getString(R.string.True))) {
                            GetToken();
                            getProfile();
                    }
                    else {
                        GoToBeginActivity();
                    }
                    if(alert != null && alert.isShowing()){
                        alert.dismiss();
                    }
                }else{
                    showDialog();
                    handleCheckStatus.postDelayed(this, 3000);
                }
            }
        }, 3000);



    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }
    public void getProfile() {
           retryCount++;
           Log.e("GetPostAPI",""+ URLHelper.UserProfile+"?device_type=android&device_id="+device_UDID+"&device_token="+device_token);
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.UserProfile+"?device_type=android&device_id="+device_UDID+"&device_token="+device_token, object , new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.v("GetProfile",response.toString());
                    SharedHelper.putKey(context, "id", response.optString("id"));
                    SharedHelper.putKey(context, "first_name", response.optString("first_name"));
                    SharedHelper.putKey(context, "last_name", response.optString("last_name"));
                    SharedHelper.putKey(context, "email", response.optString("email"));
                    SharedHelper.putKey(context, "refer_code", response.optString("refer_code"));
                    SharedHelper.putKey(getApplicationContext(), "referral_code",
                            response.optString("referral_code"));
                    if (response.optString("picture").startsWith("http"))
                        SharedHelper.putKey(context, "picture", response.optString("picture"));
                    else
                        SharedHelper.putKey(context, "picture", URLHelper.base+"storage/app/public/"+response.optString("picture"));
                    SharedHelper.putKey(context, "gender", response.optString("gender"));
                    SharedHelper.putKey(context, "mobile", response.optString("mobile"));
                    SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));
                    SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));
                    if(!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                        SharedHelper.putKey(context, "currency","$");
                    else
                        SharedHelper.putKey(context, "currency","$");
                    SharedHelper.putKey(context,"sos",response.optString("sos"));
                    Log.e(TAG, "onResponse: Sos Call" + response.optString("sos"));
                    SharedHelper.putKey(context,"loggedIn",getString(R.string.True));
                    GoToMainActivity();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (retryCount < 5){
                        getProfile();
                    }else{
                        GoToBeginActivity();
                    }
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if(response != null && response.data != null){

                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if(response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500){
                                try{
                                    displayMessage(errorObj.optString("message"));
                                }catch (Exception e){
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            }else if(response.statusCode == 401){
                                refreshAccessToken();
                            }else if(response.statusCode == 422){

                                json = trimMessage(new String(response.data));
                                if(json !="" && json != null) {
                                    displayMessage(json);
                                }else{
                                    displayMessage(getString(R.string.please_try_again));
                                }

                            }else if(response.statusCode == 503){
                                displayMessage(getString(R.string.server_down));
                            }
                        }catch (Exception e){
                            displayMessage(getString(R.string.something_went_wrong));
                        }

                    } else {
                        if (error instanceof NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            getProfile();
                        }
                    }
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization",""+ SharedHelper.getKey(context, "token_type")+" "+ SharedHelper.getKey(context, "access_token"));
                    return headers;
                }
            };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyCourier.getInstance().addToRequestQueue(jsonObjectRequest);

        }


    @Override
    protected void onDestroy() {
        handleCheckStatus.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void refreshAccessToken() {


            JSONObject object = new JSONObject();
            try {

                object.put("grant_type", "refresh_token");
                object.put("client_id", URLHelper.client_id);
                object.put("client_secret", URLHelper.client_secret);
                object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
                object.put("scope", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Log.v("SignUpResponse", response.toString());
                    SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                    SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                    SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                    getProfile();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;

                    if (response != null && response.data != null) {
                        SharedHelper.putKey(context,"loggedIn",getString(R.string.False));
                        GoToBeginActivity();
                    } else {
                        if (error instanceof NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            refreshAccessToken();
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    return headers;
                }
            };

            MyCourier.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void GoToMainActivity(){
        Intent mainIntent = new Intent(activity, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void GoToBeginActivity(){
        Intent mainIntent = new Intent(activity, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void displayMessage(String toastString){
        Log.e("displayMessage",""+toastString);
        Toast.makeText(activity, toastString, Toast.LENGTH_SHORT).show();
    }

    public void GetToken() {
        try {
            if(!SharedHelper.getKey(context,"device_token").equals("") && SharedHelper.getKey(context,"device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                Log.i(TAG, "GCM Registration Token: " + device_token);
            }else{
                device_token = "COULD NOT GET FCM TOKEN";
                Log.i(TAG, "Failed to complete token refresh: " + device_token);
            }
        }catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh", e);
        }

        try {
            device_UDID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.i(TAG, "Device UDID:" + device_UDID);
        }catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.d(TAG, "Failed to complete device UDID");
        }
    }

    private void showDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.connect_to_wifi), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.quit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        if(alert == null){
            alert = builder.create();
            alert.show();
        }
    }


    public void checkIPaddress()
    {
        RequestQueue queue = Volley.newRequestQueue(this);

        String urlip = "http://checkip.amazonaws.com/";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlip, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("response",response+"");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.toString()+"");
            }
        });

        queue.add(stringRequest);
    }

}
