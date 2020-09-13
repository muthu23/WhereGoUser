package com.wherego.delivery.user.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.wherego.delivery.user.Helper.ConnectionHelper;
import com.wherego.delivery.user.Helper.SharedHelper;
import com.wherego.delivery.user.Helper.URLHelper;
import com.wherego.delivery.user.Helper.VolleyMultipartRequest;
import com.wherego.delivery.user.MyCourier;
import com.wherego.delivery.user.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import static com.wherego.delivery.user.MyCourier.trimMessage;

public class UpdateProfile extends AppCompatActivity implements View.OnClickListener {
ImageView backArrow;
TextView toolName;
    String parameter,value;
    EditText editText;
    TextInputLayout text_input_layout;
    Button btnUpdate;
    Boolean isInternet;
    ConnectionHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        toolName=(TextView)findViewById(R.id.toolName);
        backArrow=(ImageView) findViewById(R.id.backArrow);
        editText=(EditText)findViewById(R.id.editText);
        btnUpdate=(Button)findViewById(R.id.btnUpdate) ;
        text_input_layout=(TextInputLayout)findViewById(R.id.text_input_layout);
        backArrow.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        helper = new ConnectionHelper(getApplicationContext());
        isInternet = helper.isConnectingToInternet();
        getIntentData();


    }

    private void getIntentData() {
         parameter=getIntent().getStringExtra("parameter");
         value=getIntent().getStringExtra("value");
         if(parameter.equalsIgnoreCase("first_name"))
         {

             toolName.setText("Update Name");
             text_input_layout.setHelperText("This name will be shown to the driver during ride pickup");
             editText.setHint("Name");
             text_input_layout.setHint("Enter  Name");
             editText.setText(value);
             editText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
         }

        if(parameter.equalsIgnoreCase("email"))
        {

            toolName.setText("Update Account Email");
            text_input_layout.setHelperText("It is updated to the your account");
            editText.setHint("Email");
            text_input_layout.setHint("Enter Email");
            editText.setText(value);
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
        if(parameter.equalsIgnoreCase("mobile"))
        {

            toolName.setText("Update Mobile No");
            editText.setHint("Mobile No");
            text_input_layout.setHint("Enter Mobile No");
            editText.setText(value);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.backArrow)
        {
            onBackPressed();
            finish();
        }
        if (v.getId()==R.id.btnUpdate)
        {
            if (editText.getText().toString().equals(""))
            {
                text_input_layout.setError("This field is not empty");
            }
            else {
                if (isInternet) {
                    if (parameter.equals("first_name"))
                    {

                        SharedHelper.putKey(getApplicationContext(), "first_name",editText.getText().toString() );
//                        if (strList.size()==2)
//                        {
//                            SharedHelper.putKey(getApplicationContext(), "last_name",strList.get(1) );
//                        }

                        updateProfileWithoutImage();
                    }
                   else  {
                        SharedHelper.putKey(getApplicationContext(), parameter, editText.getText().toString());
                        updateProfileWithoutImage();
                    }




                }
            }
        }
    }


    private void updateProfileWithoutImage() {

        Dialog dialogCustom=new Dialog(UpdateProfile.this);
        dialogCustom.setContentView(R.layout.custom_dialog);
        dialogCustom.setCancelable(false);
        dialogCustom.show();

        VolleyMultipartRequest volleyMultipartRequest = new
                VolleyMultipartRequest(Request.Method.POST,
                        URLHelper.UseProfileUpdate,
                        response -> {

                            dialogCustom.dismiss();
                            String res = new String(response.data);
                            try {
                                JSONObject jsonObject = new JSONObject(res);
                                SharedHelper.putKey(getApplicationContext(), "id", jsonObject.optString("id"));
                                SharedHelper.putKey(getApplicationContext(), "first_name", jsonObject.optString("first_name"));
                                SharedHelper.putKey(getApplicationContext(), "last_name", jsonObject.optString("last_name"));
                                SharedHelper.putKey(getApplicationContext(), "email", jsonObject.optString("email"));
                                if (jsonObject.optString("picture").equals("") || jsonObject.optString("picture") == null) {
                                    SharedHelper.putKey(getApplicationContext(), "picture", "");
                                } else {
                                    if (jsonObject.optString("picture").startsWith("http"))
                                        SharedHelper.putKey(getApplicationContext(), "picture", jsonObject.optString("picture"));
                                    else
                                        SharedHelper.putKey(getApplicationContext(), "picture", URLHelper.base + "storage/app/public/" + jsonObject.optString("picture"));
                                }

                                SharedHelper.putKey(getApplicationContext(), "gender", jsonObject.optString("gender"));
                                SharedHelper.putKey(getApplicationContext(), "mobile", jsonObject.optString("mobile"));
                                SharedHelper.putKey(getApplicationContext(), "wallet_balance", jsonObject.optString("wallet_balance"));
                                SharedHelper.putKey(getApplicationContext(), "payment_mode", jsonObject.optString("payment_mode"));
                              callSuccess();
//                                GoToMainActivity();
                                Toast.makeText(UpdateProfile.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                                //displayMessage(getString(R.string.update_success));

                            } catch (JSONException e) {
                                e.printStackTrace();
                                displayMessage(getString(R.string.something_went_wrong));
                            }


                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialogCustom.dismiss();
                        String json = null;
                        String Message;
                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {
                            try {
                                JSONObject errorObj = new JSONObject(new String(response.data));

                                if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                    try {
                                        displayMessage(errorObj.optString("message"));
                                    } catch (Exception e) {
                                        displayMessage(getString(R.string.something_went_wrong));
                                    }
                                } else if (response.statusCode == 401) {
//                                    refreshAccessToken("UPDATE_PROFILE_WITHOUT_IMAGE");
                                } else if (response.statusCode == 422) {

                                    json = trimMessage(new String(response.data));
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
                                updateProfileWithoutImage();
                            }
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("first_name", SharedHelper.getKey(getApplicationContext(),"first_name"));
                        params.put("last_name", "");
                        params.put("email",  SharedHelper.getKey(getApplicationContext(),"email"));
                        params.put("mobile",  SharedHelper.getKey(getApplicationContext(),"mobile"));
                        params.put("picture", "");

                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" + SharedHelper.getKey(getApplicationContext(), "token_type")
                                + " " + SharedHelper.getKey(getApplicationContext(), "access_token"));
                        return headers;
                    }
                };
        MyCourier.getInstance().addToRequestQueue(volleyMultipartRequest);

    }

    private void callSuccess() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result","result");
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }
}
