package com.wherego.delivery.user.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
;
import com.hbb20.CountryCodePicker;
import com.wherego.delivery.user.MyCourier;
import com.wherego.delivery.user.R;
import com.wherego.delivery.user.Helper.ConnectionHelper;
import com.wherego.delivery.user.Helper.CustomDialog;
import com.wherego.delivery.user.Helper.SharedHelper;
import com.wherego.delivery.user.Helper.URLHelper;
import com.wherego.delivery.user.Utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wherego.delivery.user.MyCourier.trimMessage;

public class RegisterActivity extends AppCompatActivity {

    public Context context = RegisterActivity.this;
    public Activity activity = RegisterActivity.this;
    String TAG = "RegisterActivity";
    String device_token, device_UDID;
    ImageView backArrow;
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    Utilities utils = new Utilities();
    Boolean fromActivity = false;
    String getemail, getfirst_name, getlast_name;
    public static int APP_REQUEST_CODE = 99;

    Button nextICON;
    //EditText email, first_name, last_name, mobile_no, password;
    EditText password, name, email;
    TextView txtLogIn;
    Spinner serviceSpinner;
    CheckBox chkPromoCode;
    private String blockCharacterSet = "~#^|$%&*!()_-*.,@/";

    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source,
                                   int start,
                                   int end,
                                   Spanned dest,
                                   int dstart,
                                   int dend) {
            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSmsPermission();

        findViewById();
        GetToken();

        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode
                    .ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        txtLogIn.setOnClickListener(view ->
                startActivity(new Intent(RegisterActivity.this,
                        LoginActivity.class)));

        nextICON.setOnClickListener(view -> {
            Pattern ps = Pattern.compile(".*[0-9].*");
            Matcher firstName = ps.matcher(name.getText().toString());

            if (email.getText().toString().equals("") ||
                    email.getText().toString()
                            .equalsIgnoreCase(getString(R.string.sample_mail_id))) {
                displayMessage(getString(R.string.email_validation));
            } else if (!Utilities.isValidEmail(email.getText().toString())) {
                displayMessage(getString(R.string.not_valid_email));
            } else if (name.getText().toString().equals("") ||
                    name.getText().toString()
                            .equalsIgnoreCase(getString(R.string.first_name))) {
                displayMessage(getString(R.string.first_name_empty));
            } else if (firstName.matches()) {
                displayMessage(getString(R.string.first_name_no_number));
            } else if (password.getText().toString().equals("") ||
                    password.getText().toString().
                            equalsIgnoreCase(getString(R.string.password_txt))) {
                displayMessage(getString(R.string.password_validation));
            } else if (password.length() < 6) {
                displayMessage(getString(R.string.password_size));
            } else {
                if (isInternet) {
                    openphonelogin();
                } else {
                    displayMessage(getString(R.string.something_went_wrong_net));
                }
            }

        });

    }

    Dialog dialog;

    private void openphonelogin() {

        dialog = new Dialog(RegisterActivity.this, R.style.AppTheme_NoActionBar);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.mobileverification);
        dialog.setCancelable(true);
        dialog.show();
        CountryCodePicker ccp = (CountryCodePicker) dialog.findViewById(R.id.ccp);
        Button nextIcon = dialog.findViewById(R.id.nextIcon);
        EditText mobile_no = dialog.findViewById(R.id.mobile_no);
        final String countryCode = ccp.getDefaultCountryCode();
        final String countryIso = ccp.getSelectedCountryNameCode();

        nextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = ccp.getSelectedCountryCodeWithPlus() + mobile_no.getText().toString();
                SharedHelper.putKey(getApplicationContext(), "mobile_number", phone);
                Log.v("Phonecode", phone + " ");
                Intent intent = new Intent(RegisterActivity.this, OtpVerification.class);
                intent.putExtra("phonenumber", phone);
                startActivityForResult(intent, APP_REQUEST_CODE);
                dialog.dismiss();
            }
        });

    }


    private static final String mREAD_SMS = Manifest.permission.READ_SMS;
    private static final String mRECEIVE_SMS = Manifest.permission.RECEIVE_SMS;
    private static final int SMS_PERMISSION_REQUEST_CODE = 1234;

    private void getSmsPermission() {
        Log.d(TAG, "getSmsPermission: getting sms permissions");
        String[] permissions = new String[]{Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                mREAD_SMS) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    mRECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
                //onActivityResult();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        SMS_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    SMS_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (customDialog != null && customDialog.isShowing())
            customDialog.dismiss();
    }

    public void findViewById() {

        password = findViewById(R.id.password);
        email = findViewById(R.id.etEmail);
        name = findViewById(R.id.etname);
//        etRefer=findViewById(R.id.etRefer);
        chkPromoCode = findViewById(R.id.chkPromoCode);
        nextICON = findViewById(R.id.nextIcon);
        txtLogIn = findViewById(R.id.txtLogIn);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();

        chkPromoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkPromoCode.isChecked()) {
                    openreferralDialog();
                    chkPromoCode.setChecked(false);
                }
            }
        });
    }

    private void openreferralDialog() {
        Dialog referralDialog = new Dialog(RegisterActivity.this);
        referralDialog.setContentView(R.layout.referral_dialog);
        referralDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        referralDialog.show();
        EditText etReferral = referralDialog.findViewById(R.id.etReferral);
        Button btnValidate = referralDialog.findViewById(R.id.btnValidate);

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etReferral.getText().toString().isEmpty()) {
                    etReferral.setError(getString(R.string.referral_code_is_not_apply));
                } else {
                    referralDialog.dismiss();
                    validateReferral(etReferral.getText().toString());
                }
            }
        });

    }

    String referralCode = "";

    private void validateReferral(String referralCodes) {

        customDialog = new CustomDialog(RegisterActivity.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {

            object.put("referral_code", referralCodes);

            utils.print("InputToreferralAPI", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.check_refrral,
                        object,
                        response -> {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            if (response.optString("status").equalsIgnoreCase("0")) {
                                referralCode = "";
                                chkPromoCode.setChecked(false);
                                displayMessage("Referral-Code doesn't found");
                            } else {
                                referralCode = referralCodes;
                                chkPromoCode.setText("ReferralCode Applied!");
                                chkPromoCode.setChecked(true);
                            }
                        },
                        error -> {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            String json = null;
                            String Message;
                            NetworkResponse response = error.networkResponse;

                            if (response != null && response.data != null) {
                                utils.print("MyTest", "" + error);
                                utils.print("MyTestError", "" + error.networkResponse);
                                utils.print("MyTestError1", "" + response.statusCode);
                                try {
                                    JSONObject errorObj = new JSONObject(new String(response.data));

                                    if (response.statusCode == 400 || response.statusCode == 405 ||
                                            response.statusCode == 500) {
                                        try {
                                            displayMessage(errorObj.optString("message"));
                                        } catch (Exception e) {
                                            displayMessage(getString(R.string.something_went_wrong));
                                        }
                                    } else if (response.statusCode == 401) {
                                        try {
                                            if (errorObj.optString("message")
                                                    .equalsIgnoreCase("invalid_token")) {
                                                //   Refresh token
                                            } else {
//                                                displayMessage(getString(R.string.email_exist));
                                            }
                                        } catch (Exception e) {
                                            displayMessage(getString(R.string.something_went_wrong));
                                        }

                                    } else if (response.statusCode == 422) {
                                        Snackbar.make(getCurrentFocus(),
                                                R.string.email_exist, Snackbar.LENGTH_SHORT)
                                                .setAction("Action", null).show();
                                        json = trimMessage(new String(response.data));
                                        if (json != "" && json != null) {
                                            if (json.startsWith("The email")) {

                                            }
                                            //displayMessage(json);
                                        } else {
                                            displayMessage(getString(R.string.please_try_again));
                                        }

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
                                    registerAPI();
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
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult");
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            dialog.dismiss();
//                SharedHelper.putKey(getApplicationContext(), "mobile", countryCodePicker.getSelectedCountryCodeWithPlus()+etMobile.getText().toString());
            if (SharedHelper.getKey(this, "isotpVerified").equalsIgnoreCase("true"))
                registerAPI();
            else displayMessage("OTP verification not successful");
        }
    }


    private void registerAPI() {
        customDialog = new CustomDialog(RegisterActivity.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("device_type", "android");
            object.put("device_id", device_UDID);
            object.put("device_token", "" + device_token);
            object.put("login_by", "manual");

            object.put("first_name", name.getText().toString());
            object.put("last_name", "");
            object.put("email", email.getText().toString());
            object.put("password", password.getText().toString());
            object.put("password_confirmation", password.getText().toString());
            object.put("refer_code", "");

            object.put("mobile", SharedHelper.getKey(RegisterActivity.this,
                    "mobile_number"));
            object.put("picture", "");
            object.put("social_unique_id", "");

            utils.print("InputToRegisterAPI", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.register,
                        object,
                        response -> {
                            Log.e("registerresponse", response + "");
                            if ((customDialog != null) && (customDialog.isShowing()))
                                //customDialog.dismiss();
                                utils.print("SignInResponse", response.toString());
                            SharedHelper.putKey(RegisterActivity.this,
                                    "email", email.getText().toString());
                            SharedHelper.putKey(RegisterActivity.this,
                                    "password", password.getText().toString());
                            signIn();
                        }, error -> {
                    Log.e("error", error + "");
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;

                    if (response != null && response.data != null) {
                        utils.print("MyTest", "" + error);
                        utils.print("MyTestError", "" + error.networkResponse);
                        utils.print("MyTestError1", "" + response.statusCode);
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 ||
                                    response.statusCode == 405 ||
                                    response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                try {
                                    if (errorObj.optString("message")
                                            .equalsIgnoreCase("invalid_token")) {
                                        //   Refresh token
                                    } else {
                                        displayMessage(errorObj.optString("message"));
                                    }
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }

                            } else if (response.statusCode == 422) {
                                //Toast.makeText(getApplication(),R.string.email_exist,Toast.LENGTH_SHORT).show();
                                Snackbar.make(getCurrentFocus(),
                                        R.string.email_exist, Snackbar.LENGTH_SHORT)
                                        .setAction("Action", null).show();
                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    if (json.startsWith("The email")) {

                                    }
                                    //displayMessage(json);
                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

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
                            registerAPI();
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

    private void GoToBeginActivity() {
        if (customDialog != null && customDialog.isShowing())
            customDialog.dismiss();
        Intent mainIntent = new Intent(RegisterActivity.this, RegisterActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        RegisterActivity.this.finish();
    }

    public void signIn() {
        if (isInternet) {
            customDialog = new CustomDialog(RegisterActivity.this);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            try {
                object.put("grant_type", "password");
                object.put("client_id", URLHelper.client_id);
                object.put("client_secret", URLHelper.client_secret);
                object.put("username", SharedHelper
                        .getKey(RegisterActivity.this, "email"));
                object.put("password", SharedHelper
                        .getKey(RegisterActivity.this, "password"));
                object.put("scope", "");
                object.put("device_type", "android");
                object.put("device_id", device_UDID);
                object.put("device_token", device_token);
                utils.print("InputToLoginAPI", "" + object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new
                    JsonObjectRequest(Request.Method.POST,
                            URLHelper.login,
                            object, response -> {
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                        utils.print("SignUpResponse", response.toString());
                        SharedHelper.putKey(context, "access_token",
                                response.optString("access_token"));
                        SharedHelper.putKey(context, "refresh_token",
                                response.optString("refresh_token"));
                        SharedHelper.putKey(context, "token_type",
                                response.optString("token_type"));
                        getProfile();
                    }, error -> {
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                        String json = null;
                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {
                            try {
                                JSONObject errorObj = new JSONObject(new String(response.data));

                                if (response.statusCode == 400 ||
                                        response.statusCode == 405 ||
                                        response.statusCode == 500) {
                                    try {
                                        displayMessage(errorObj.optString("message"));
                                    } catch (Exception e) {
                                        displayMessage(getString(R.string.something_went_wrong));
                                    }
                                } else if (response.statusCode == 401) {
                                    try {
                                        if (errorObj.optString("message")
                                                .equalsIgnoreCase("invalid_token")) {
                                            //Call Refresh token
                                        } else {
                                            displayMessage(errorObj.optString("message"));
                                        }
                                    } catch (Exception e) {
                                        displayMessage(getString(R.string.something_went_wrong));
                                    }

                                } else if (response.statusCode == 422) {

                                    json = trimMessage(new String(response.data));
                                    if (json != "" && json != null) {
                                        displayMessage(json);
                                    } else {
                                        displayMessage(getString(R.string.please_try_again));
                                    }

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
                                signIn();
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
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

    public void getProfile() {
        if (isInternet) {
            customDialog = new CustomDialog(RegisterActivity.this);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new
                    JsonObjectRequest(Request.Method.GET,
                            URLHelper.UserProfile,
                            object,
                            response -> {
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    //customDialog.dismiss();
                                    utils.print("GetProfile", response.toString());
                                SharedHelper.putKey(context, "refer_code", response.optString("refer_code"));
                                SharedHelper.putKey(RegisterActivity.this,
                                        "id", response.optString("id"));
                                SharedHelper.putKey(RegisterActivity.this,
                                        "first_name", response.optString("first_name"));
                                SharedHelper.putKey(RegisterActivity.this,
                                        "last_name", response.optString("last_name"));
                                SharedHelper.putKey(RegisterActivity.this,
                                        "email", response.optString("email"));
                                SharedHelper.putKey(RegisterActivity.this,
                                        "picture", URLHelper.base +
                                                "storage/app/public/" + response.optString("picture"));
                                SharedHelper.putKey(RegisterActivity.this,
                                        "gender", response.optString("gender"));
                                SharedHelper.putKey(RegisterActivity.this,
                                        "mobile", response.optString("mobile"));
                                SharedHelper.putKey(RegisterActivity.this,
                                        "wallet_balance", response.optString("wallet_balance"));
                                SharedHelper.putKey(RegisterActivity.this,
                                        "payment_mode", response.optString("payment_mode"));
                                if (!response.optString("currency")
                                        .equalsIgnoreCase("") &&
                                        response.optString("currency") != null)
                                    SharedHelper.putKey(context, "currency",
                                            response.optString("currency"));
                                else
                                    SharedHelper.putKey(context, "currency", "$");
                                SharedHelper.putKey(context, "sos", response.optString("sos"));
                                SharedHelper.putKey(RegisterActivity.this,
                                        "loggedIn", getString(R.string.True));

                                //phoneLogin();
                                GoToMainActivity();
                               /* if (!SharedHelper.getKey(activity,"account_kit_token").equalsIgnoreCase("")) {

                                }else {
                                    GoToMainActivity();
                                }*/

                            }, error -> {
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                        String json = null;
                        String Message;
                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {
                            try {
                                JSONObject errorObj = new JSONObject(new String(response.data));

                                if (response.statusCode == 400 ||
                                        response.statusCode == 405 ||
                                        response.statusCode == 500) {
                                    try {
                                        displayMessage(errorObj.optString("message"));
                                    } catch (Exception e) {
                                        displayMessage(getString(R.string.something_went_wrong));
                                    }
                                } else if (response.statusCode == 401) {
                                    try {
                                        if (errorObj.optString("message")
                                                .equalsIgnoreCase("invalid_token")) {
                                            refreshAccessToken();
                                        } else {
                                            displayMessage(errorObj.optString("message"));
                                        }
                                    } catch (Exception e) {
                                        displayMessage(getString(R.string.something_went_wrong));
                                    }

                                } else if (response.statusCode == 422) {

                                    json = trimMessage(new String(response.data));
                                    if (json != "" && json != null) {
                                        displayMessage(json);
                                    } else {
                                        displayMessage(getString(R.string.please_try_again));
                                    }

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
                                getProfile();
                            }
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("X-Requested-With", "XMLHttpRequest");
                            headers.put("Authorization", "" + SharedHelper
                                    .getKey(RegisterActivity.this,
                                            "token_type") + " " +
                                    SharedHelper.getKey(RegisterActivity.this,
                                            "access_token"));
                            return headers;
                        }
                    };

            MyCourier.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }
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

        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.login,
                        object,
                        response -> {

                            Log.v("SignUpResponse", response.toString());
                            SharedHelper.putKey(context, "access_token",
                                    response.optString("access_token"));
                            SharedHelper.putKey(context, "refresh_token",
                                    response.optString("refresh_token"));
                            SharedHelper.putKey(context, "token_type",
                                    response.optString("token_type"));
                            getProfile();


                        }, error -> {
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;

                    if (response != null && response.data != null) {
                        SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
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

    public void GetToken() {
        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") &&
                    SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                utils.print(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "COULD NOT GET FCM TOKEN";
                utils.print(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            utils.print(TAG, "Failed to complete token refresh");
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            utils.print(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            utils.print(TAG, "Failed to complete device UDID");
        }
    }

    public void GoToMainActivity() {
        if (customDialog != null && customDialog.isShowing())
            customDialog.dismiss();
        Intent mainIntent = new Intent(RegisterActivity.this,
                MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        RegisterActivity.this.finish();
    }

    public void displayMessage(String toastString) {
        utils.print("displayMessage", "" + toastString);
        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        if (fromActivity) {
            Intent mainIntent = new Intent(RegisterActivity.this, RegisterActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            RegisterActivity.this.finish();
        } else {
            Intent mainIntent = new Intent(RegisterActivity.this, LoginActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            RegisterActivity.this.finish();
        }
    }
}
