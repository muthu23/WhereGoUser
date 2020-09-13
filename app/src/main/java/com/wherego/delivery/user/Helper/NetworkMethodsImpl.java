package com.wherego.delivery.user.Helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.wherego.delivery.user.R;
import com.wherego.delivery.user.Utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class NetworkMethodsImpl  {
    private static final String TAG = "NetworkMethodsImpl";
    Activity activity;
    Context mContext;
    RequestQueue queue;
    protected int _splashTime = 500;
    protected boolean _active = true;
    ProgressDialog pd_loading;
    int fixedValue = -1;
    String messageData = "";

    String item_id="";
    public  ProgressDialog  pDialog;
    public static final int ADD_CONSTANT_VALUE = -1;
    public static final int ADD_REVENUE_MODAL = 1;


    public NetworkMethodsImpl(Activity activity) {
        mContext = activity.getApplicationContext();
        queue = Volley.newRequestQueue(mContext);
        pd_loading = new ProgressDialog(activity);
    }


    public NetworkMethodsImpl(Activity activity, Activity context) {
        mContext = activity.getApplicationContext();
        queue = Volley.newRequestQueue(mContext);


    }

    public NetworkMethodsImpl() {

    }




    public void saveProfileAccount( final Activity act,
                                    final Map<String, String> paramMap ,
                                    String url,
                                    final ArrayList<String> nameOne,
                                    final  ArrayList<byte[]> imageOne) {

        Log.v("paramMap",paramMap+"");
        Log.v("url",url+"");



        if (Utils.isConnectingToInternet(act)) {
            final ProgressDialog pd_loading = new ProgressDialog(act);
            pd_loading.setCancelable(false);
            activity = act;

            pDialog = new  ProgressDialog (activity);
            // pDialog.setTitle("Loading...");
            pDialog.setCancelable(false);
            pDialog.setMessage("Loading...");
            pDialog.show();
            VolleyMultipartRequest multipartRequest = new
                    VolleyMultipartRequest(Request.Method.POST,
                            url,
                            new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {

                     String resultResponse = new String(response.data);
                        Log.e("resultResponse", resultResponse.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(resultResponse);

                       // {"error":"no","success":"yes","item_id":19,"message":"Insert Items"}
                        messageData = jsonObject.optString("success");
                        Log.v("test", jsonObject.toString());

                        if (jsonObject.optString("success").equalsIgnoreCase("yes")) {

                            item_id = jsonObject.optString("item_id");
                            handler_.sendEmptyMessage(0);
                            if (pDialog != null || pDialog.isShowing()) pDialog.dismiss();
                        } else {
                            handler_.sendEmptyMessage(1);
                            if (pDialog != null || pDialog.isShowing()) pDialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // pd_loading.dismiss();
                        pDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    String errorMessage = "Unknown error";
                    if (networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorMessage = "Request timeout";
                        } else if (error.getClass().equals(NoConnectionError.class)) {
                            errorMessage = "Failed to connect server";
                        }
                    }
                    pDialog.dismiss();
                    Log.v("Error Block", errorMessage);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> param = new HashMap<>();
                    return paramMap;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                   // headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "" +
                            SharedHelper.getKey(act, "token_type") + " " +
                            SharedHelper.getKey(act, "access_token"));
                    Log.e("headers", headers + "headers");


                    return headers;
                }


                @Override
                protected Map<String, DataPart> getByteData() {


                    Map<String, DataPart> parmas1 = new HashMap<>();
                    if (nameOne.size()==1) {
                        parmas1.put("image0", new DataPart(nameOne.get(0), imageOne.get(0), "image/jpeg"));
                    }
                    if (nameOne.size()==2) {
                        parmas1.put("image0", new DataPart(nameOne.get(0), imageOne.get(0), "image/jpeg"));
                        parmas1.put("image1", new DataPart(nameOne.get(1), imageOne.get(1), "image/jpeg"));
                    }
                    if (nameOne.size()==3) {
                        parmas1.put("image0", new DataPart(nameOne.get(0), imageOne.get(0), "image/jpeg"));
                        parmas1.put("image1", new DataPart(nameOne.get(1), imageOne.get(1), "image/jpeg"));
                        parmas1.put("image2", new DataPart(nameOne.get(2), imageOne.get(2), "image/jpeg"));
                    }
                    if (nameOne.size()==4) {
                        parmas1.put("image0", new DataPart(nameOne.get(0), imageOne.get(0), "image/jpeg"));
                        parmas1.put("image1", new DataPart(nameOne.get(1), imageOne.get(1), "image/jpeg"));
                        parmas1.put("image2", new DataPart(nameOne.get(2), imageOne.get(2), "image/jpeg"));
                        parmas1.put("image4", new DataPart(nameOne.get(3), imageOne.get(3), "image/jpeg"));
                    }
                    if (nameOne.size()==5) {
                        parmas1.put("image0", new DataPart(nameOne.get(0), imageOne.get(0), "image/jpeg"));
                        parmas1.put("image1", new DataPart(nameOne.get(1), imageOne.get(1), "image/jpeg"));
                        parmas1.put("image2", new DataPart(nameOne.get(2), imageOne.get(2), "image/jpeg"));
                        parmas1.put("image4", new DataPart(nameOne.get(3), imageOne.get(3), "image/jpeg"));
                        parmas1.put("image5", new DataPart(nameOne.get(4), imageOne.get(4), "image/jpeg"));
                    }
                    else {

                    }


                    return parmas1;
                }


            };


            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue queue = Volley.newRequestQueue(act);
            queue.add(multipartRequest);
        } else {
            Toast.makeText(activity, activity.getResources().getString(R.string.no_internet_connection),
                    Toast.LENGTH_SHORT).show();
        }


    }



    private Handler handlerLogin = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }


    };

    private final Handler handler_ = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if(msg.what==0){
                Intent intent = new Intent();
                intent.putExtra("ok", "next");
                intent.putExtra("item_id", item_id);
                activity.setResult(RESULT_OK, intent);
                activity.finish();
            }
        }
    };

}
