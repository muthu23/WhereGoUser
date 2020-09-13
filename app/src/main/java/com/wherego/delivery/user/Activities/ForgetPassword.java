package com.wherego.delivery.user.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

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


public class ForgetPassword extends AppCompatActivity implements View.OnClickListener {
    Dialog dialog;
    String TAG = "ForgetPassword";
    public Context context = ForgetPassword.this;
    Button nextIcon;
    TextInputLayout newPasswordLayout, confirmPasswordLayout, OtpLay;
    LinearLayout ll_resend;
    EditText newPassowrd, confirmPassword, OTP;
    EditText email;
    EditText mobile_no;
    CustomDialog customDialog;
    String validation = "",
            str_newPassword,
            str_confirmPassword,
            id,
            str_email = "",
            str_otp,
            server_opt,
            getemail,
            getmobile,
            str_number;
    ConnectionHelper helper;
    Boolean isInternet;
    TextView note_txt;
    Boolean fromActivity = false;
    Button resend;

    Utilities utils = new Utilities();
    EditText number;
    public static int APP_REQUEST_CODE = 99;
    String phoneNumberString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        findViewById();

        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


//        etEmail=findViewById(R.id.etEmail);

    }

    private void findViewById() {

        mobile_no = findViewById(R.id.mobile_no);
        email = findViewById(R.id.email);
        number = findViewById(R.id.number);

        nextIcon =findViewById(R.id.nextIcon);

        note_txt =  findViewById(R.id.note);
        newPassowrd = findViewById(R.id.new_password);
        OTP = findViewById(R.id.otp);
        confirmPassword = findViewById(R.id.confirm_password);
        confirmPasswordLayout =  findViewById(R.id.confirm_password_lay);
        OtpLay =  findViewById(R.id.otp_lay);
        newPasswordLayout =  findViewById(R.id.new_password_lay);
        resend =  findViewById(R.id.resend);
        ll_resend =  findViewById(R.id.ll_resend);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
        str_email = SharedHelper.getKey(ForgetPassword.this, "email");
        email.setText(str_email);
        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        nextIcon.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nextIcon:

                str_email = email.getText().toString();
                str_number = number.getText().toString();
                if (validation.equalsIgnoreCase("")) {
                    if (email.getText().toString().equals("")) {
                        displayMessage(getString(R.string.email_validation));
                    } else if (!Utilities.isValidEmail(email.getText().toString())) {
                        displayMessage(getString(R.string.not_valid_email));
                    }
                    else {
                        if (isInternet) {
                            forgetPassword();
                        } else {
                            displayMessage(getString(R.string.something_went_wrong_net));
                        }

                    }
                } else {
                    str_newPassword = newPassowrd.getText().toString();
                    str_confirmPassword = confirmPassword.getText().toString();
                    str_otp = OTP.getText().toString();
                    if (str_newPassword.equals("") || str_newPassword.equalsIgnoreCase(getString(R.string.new_password))) {
                        displayMessage(getString(R.string.password_validation));
                    } else if (str_newPassword.length() < 6) {
                        displayMessage(getString(R.string.password_size));
                    } else if (str_confirmPassword.equals("") || str_confirmPassword.equalsIgnoreCase(getString(R.string.confirm_password)) || !str_newPassword.equalsIgnoreCase(str_confirmPassword)) {
                        displayMessage(getString(R.string.confirm_password_validation));
                    } else if (str_confirmPassword.length() < 6) {
                        displayMessage(getString(R.string.password_size));
                    } else {
                        if (isInternet) {
                            resetpassword();
                        } else {
                            displayMessage(getString(R.string.something_went_wrong_net));
                        }
                    }
                }

                break;

            case R.id.imgBack:

                onBackPressed();
                finish();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Utilities.hideKeyboard(ForgetPassword.this);

    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Snackbar.make(findViewById(R.id.mobile_no), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }


    private void forgetPassword() {
        str_number = number.getText().toString();
        customDialog = new CustomDialog(ForgetPassword.this);
        customDialog.setCancelable(false);
        customDialog.show();

        StringRequest jsonObjectRequest = new
                StringRequest(Request.Method.POST,
                        URLHelper.FORGET_PASSWORD,
                        response -> {
                            customDialog.dismiss();
                            Log.e("ForgetPasswordResponse", response);
                            try {
                                JSONObject obj = new JSONObject(response);

                                JSONObject userObject = obj.getJSONObject("user");
                                if (userObject.getString("mobile") != null) {
                                    id = userObject.getString("id");
                                    getemail = userObject.getString("email");
                                    getmobile = userObject.getString("mobile");
//                                        if (getmobile == str_number) {
                                    Log.e("getmobile", getmobile + "");
                                    openphonelogin();

//                                        } else {
//                                            displayMessage("You have entered different mobile number");
//                                        }
                                } else {
                                    displayMessage("Mobile no is not exist with this email_id");
                                }


                            } catch (JSONException e) {
                                displayMessage("Mobile no is not exist with this email_id");
                                e.printStackTrace();
                            }

                        },
                        error -> {
                            Log.e("volleyerror", error.toString() + "");
                            customDialog.dismiss();
                            String json = null;
                            String Message;
                            NetworkResponse response = error.networkResponse;

                            if (response != null && response.data != null) {
                                Log.e("MyTest", "" + error);
                                Log.e("MyTestError", "" + error.networkResponse);
                                Log.e("MyTestError1", "" + response.statusCode);
                                try {

                                    JSONObject errorObj = new JSONObject(new String(response.data));

                                    if (response.statusCode == 400 || response.statusCode == 405 ||
                                            response.statusCode == 500) {
                                        try {
                                            displayMessage(errorObj.optString("message"));
                                        } catch (Exception e) {
                                            displayMessage("Something went wrong.");
                                        }
                                    } else if (response.statusCode == 401) {
                                        try {
                                            if (errorObj.optString("message")
                                                    .equalsIgnoreCase("invalid_token")) {
                                                refreshAccessToken("FORGOT_PASSWORD");
                                            } else {
                                                displayMessage(errorObj.optString("message"));
                                            }
                                        } catch (Exception e) {
                                            displayMessage("Something went wrong.");
                                        }
                                    } else if (response.statusCode == 422) {
                                        json = MyCourier.trimMessage(new String(response.data));
                                        if (json != "" && json != null) {
                                            displayMessage(json);
                                        } else {
                                            displayMessage("Please try again.");
                                        }
                                    } else {
                                        displayMessage("Please try again.");
                                    }

                                } catch (Exception e) {
                                    displayMessage("Something went wrong.");
                                }

                            } else {
                                if (error instanceof NoConnectionError) {
                                    displayMessage(getString(R.string.oops_connect_your_internet));
                                } else if (error instanceof NetworkError) {
                                    displayMessage(getString(R.string.oops_connect_your_internet));
                                } else if (error instanceof TimeoutError) {
                                    //forgetPassword();
                                }
                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        return headers;
                    }

                    @Override
                    public Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("email", str_email);
                        Log.e(TAG, "params: " + params.toString());
                        return params;
                    }
                };

        MyCourier.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    private void resetpassword() {
        customDialog = new CustomDialog(ForgetPassword.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            object.put("password", str_newPassword);
            object.put("password_confirmation", str_confirmPassword);
            Log.e("ResetPassword", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.RESET_PASSWORD,
                        object,
                        response -> {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            Log.v("ResetPasswordResponse", response.toString());
                            try {
                                JSONObject object1 = new JSONObject(response.toString());
                                Toast.makeText(context, object1.optString("message"),
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgetPassword.this,
                                        LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    Log.e("MyTest", "" + error);
                    Log.e("MyTestError", "" + error.networkResponse);
                    Log.e("MyTestError1", "" + response.statusCode);
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 ||
                                    response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage("Something went wrong.");
                                }
                            } else if (response.statusCode == 401) {
                                try {
                                    if (errorObj.optString("message")
                                            .equalsIgnoreCase("invalid_token")) {
                                        refreshAccessToken("RESET_PASSWORD");
                                    } else {
                                        displayMessage(errorObj.optString("message"));
                                    }
                                } catch (Exception e) {
                                    displayMessage("Something went wrong.");
                                }

                            } else if (response.statusCode == 422) {

                                json = MyCourier.trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    displayMessage(json);
                                } else {
                                    displayMessage("Please try again.");
                                }

                            } else {
                                displayMessage("Please try again.");
                            }

                        } catch (Exception e) {
                            displayMessage("Something went wrong.");
                        }


                    } else {
                        if (error instanceof NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            resetpassword();
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
    private void refreshAccessToken(final String tag) {

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

                            utils.print("SignUpResponse", response.toString());
                            SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                            SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                            SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                            if (tag.equalsIgnoreCase("FORGOT_PASSWORD")) {
                                forgetPassword();
                            } else {
                                resetpassword();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String json = "";
                        NetworkResponse response = error.networkResponse;

                        if (response != null && response.data != null) {
                            SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                            utils.GoToBeginActivity(ForgetPassword.this);
                        } else {
                            if (error instanceof NoConnectionError) {
                                displayMessage(getString(R.string.oops_connect_your_internet));
                            } else if (error instanceof NetworkError) {
                                displayMessage(getString(R.string.oops_connect_your_internet));
                            } else if (error instanceof TimeoutError) {
                                refreshAccessToken(tag);
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
    private void openphonelogin() {

        dialog = new Dialog(ForgetPassword.this,R.style.AppTheme_NoActionBar);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.mobileverification);
        dialog.setCancelable(false);
        dialog.show();
        CountryCodePicker ccp=(CountryCodePicker)dialog.findViewById(R.id.ccp);
        Button nextIcon=dialog.findViewById(R.id.nextIcon);
        EditText mobile_no=dialog.findViewById(R.id.mobile_no);
        final String countryCode=ccp.getDefaultCountryCode();
        final String countryIso=ccp.getSelectedCountryNameCode();
        nextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phoneNumberString = ccp.getSelectedCountryCodeWithPlus() + mobile_no.getText().toString();

                SharedHelper.putKey(getApplicationContext(), "mobile_number", phoneNumberString);
                Log.v("Phonecode",phoneNumberString+" ");
                Intent intent =new Intent(ForgetPassword.this, OtpVerification.class);
                intent.putExtra("phonenumber",phoneNumberString);
                startActivityForResult(intent,APP_REQUEST_CODE);
                dialog.dismiss();
//                String phone=ccp.getDefaultCountryCode()+mobile_no.getText().toString();
//                PhoneNumber phoneNumber = new PhoneNumber(ccp.getSelectedCountryCode(),mobile_no.getText().toString(),ccp.getSelectedCountryNameCode());
//                phoneLogin(phoneNumber);


            }
        });

    }


    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult");
        if (data != null) {
            if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request




                if (getmobile != null) {
                    String[] separated = phoneNumberString.split("\\+");
                    String phoneSplit = separated[1];
                    if (getmobile.contains(phoneSplit)) {
                        email.setFocusable(false);
                        email.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                        email.setClickable(false);

                        validation = "reset";
//                                    titleText.setText(R.string.reset_password);
                        newPasswordLayout.setVisibility(View.VISIBLE);
                        confirmPasswordLayout.setVisibility(View.VISIBLE);
                        OtpLay.setVisibility(View.GONE);
                        note_txt.setVisibility(View.GONE);
                        //OTP.performClick();
                        ll_resend.setVisibility(View.GONE);
                    } else {
                        displayMessage("Mobile no is not match with register emailid");
                    }
                }

                SharedHelper.putKey(ForgetPassword.this, "mobile", phoneNumberString);



            }
        }
    }
}


