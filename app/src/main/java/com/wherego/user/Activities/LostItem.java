package com.wherego.user.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.wherego.user.Helper.CustomDialog;
import com.wherego.user.Helper.SharedHelper;
import com.wherego.user.Helper.URLHelper;
import com.wherego.user.MyCourier;
import com.wherego.user.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LostItem extends AppCompatActivity {
    TextInputEditText txtDescription,txtShareDetails;
    MaterialButton button;
    String trip_id ;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_item);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtShareDetails = findViewById(R.id.txtShareDetails);
        txtDescription  = findViewById(R.id.txtDescription);
        button =  findViewById(R.id.button);
        trip_id = getIntent().getStringExtra("trip_id");
        button.setOnClickListener(v -> submitRequest());
        
        getLostItemRequest();
        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void getLostItemRequest() {

        CustomDialog  customDialog = new CustomDialog(LostItem.this);
        customDialog.setCancelable(false);
        if(customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("trip_id", trip_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.get_lost_item+trip_id, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ((customDialog != null)&& (customDialog.isShowing()))
                    customDialog.dismiss();
                Log.v("SignInResponse", response.toString());

                if (response.optJSONArray("Data").length()>0) {

                    txtShareDetails.setText(response.optJSONArray("Data").optJSONObject(0).optString("lost_item"));
                    txtDescription.setText(response.optJSONArray("Data").optJSONObject(0).optString("description"));
                    button.setText(response.optJSONArray("Data").optJSONObject(0).optString("status"));
                    txtShareDetails.setEnabled(false);
                    txtDescription.setEnabled(false);
                    button.setEnabled(false);
                    button.setVisibility(View.GONE);
                    displayMessage("Successfully get your request");
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null)&& (customDialog.isShowing()))
                    customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                Log.e("MyTest",""+error);
                Log.e("MyTestError",""+error.networkResponse);
                Log.e("MyTestError1",""+response.statusCode);
                if(response != null && response.data != null){
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        Log.e("ErrorChangePasswordAPI",""+errorObj.toString());

                        if(response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500){
                            try{
                                displayMessage(errorObj.optString("error"));
                            }catch (Exception e){
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        }else if(response.statusCode == 401){

                        }else if(response.statusCode == 422){
                            json = MyCourier.trimMessage(new String(response.data));
                            if(json !="" && json != null) {
                                displayMessage(json);
                            }else{
                                displayMessage(getString(R.string.please_try_again));
                            }
                        }else{
                            displayMessage(getString(R.string.please_try_again));
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

                    }
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(LostItem.this, "access_token"));
                return headers;
            }
        };

        MyCourier.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void submitRequest() {
        if (txtShareDetails.getText().toString().isEmpty())
        {
            txtShareDetails.setError("Share details is required");
        }
        else if (txtDescription.getText().toString().isEmpty())
        {
            txtDescription.setError("Description is required");
        }
        else {
            sendLostItem();
        }
    }

    private void sendLostItem() {

      CustomDialog  customDialog = new CustomDialog(LostItem.this);
        customDialog.setCancelable(false);
        if(customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("trip_id", trip_id);
            object.put("subject", txtDescription.getText().toString());
            object.put("description", txtDescription.getText().toString());
            object.put("lost_item", txtShareDetails.getText().toString());




        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.add_lost_item, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ((customDialog != null)&& (customDialog.isShowing()))
                    customDialog.dismiss();
                Log.v("SignInResponse", response.toString());
                displayMessage("Successfully registered your request");
                txtShareDetails.setEnabled(false);
                txtDescription.setEnabled(false);
                button.setEnabled(false);
                button.setVisibility(View.GONE);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null)&& (customDialog.isShowing()))
                    customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                Log.e("MyTest",""+error);
                Log.e("MyTestError",""+error.networkResponse);
                Log.e("MyTestError1",""+response.statusCode);
                if(response != null && response.data != null){
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        Log.e("ErrorChangePasswordAPI",""+errorObj.toString());

                        if(response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500){
                            try{
                                displayMessage(errorObj.optString("error"));
                            }catch (Exception e){
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        }else if(response.statusCode == 401){

                        }else if(response.statusCode == 422){
                            json = MyCourier.trimMessage(new String(response.data));
                            if(json !="" && json != null) {
                                displayMessage(json);
                            }else{
                                displayMessage(getString(R.string.please_try_again));
                            }
                        }else{
                            displayMessage(getString(R.string.please_try_again));
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

                    }
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(LostItem.this, "access_token"));
                return headers;
            }
        };

        MyCourier.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void displayMessage(String toastString){
        Log.e("displayMessage",""+toastString);
        Snackbar.make(findViewById(R.id.txtShareDetails),toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }
}
