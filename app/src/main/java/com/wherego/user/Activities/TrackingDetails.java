package com.wherego.user.Activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.snackbar.Snackbar;
import com.wherego.user.Helper.ConnectionHelper;
import com.wherego.user.Helper.CustomDialog;
import com.wherego.user.Helper.SharedHelper;
import com.wherego.user.Helper.URLHelper;
import com.wherego.user.R;
import com.wherego.user.MyCourier;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.wherego.user.MyCourier.trimMessage;

public class TrackingDetails extends AppCompatActivity {
    Boolean isInternet;
    PostAdapter postAdapter;
    RecyclerView recyclerView;
    RelativeLayout errorLayout;
    ConnectionHelper helper;
    CustomDialog customDialog;


    ImageView backImg;
    LinearLayout toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_details);


        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();


        findViewByIdAndInitialize();
        if (isInternet) {
            getTrackingList();
        }

        backImg.setOnClickListener(v -> {
            startActivity(new Intent(TrackingDetails.this,
                    MainActivity.class));
            finish();
        });
    }
    public void getTrackingList() {
        customDialog = new CustomDialog(TrackingDetails.this);
        customDialog.setCancelable(false);
        customDialog.show();
        String url=URLHelper.base+"api/user/OrderTrackingLists?user_id="+SharedHelper.getKey(TrackingDetails.this,"id");
        Log.e("url",url+"");
        JsonArrayRequest jsonArrayRequest = new
                JsonArrayRequest(url,
                        response -> {
                            Log.v("GetHistoryList", response.toString());
                            if (response != null) {
                                postAdapter = new PostAdapter(response);
                                //  recyclerView.setHasFixedSize(true);
                                RecyclerView.LayoutManager mLayoutManager = new
                                        LinearLayoutManager(getApplicationContext());
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                if (postAdapter != null && postAdapter.getItemCount() > 0) {
                                    errorLayout.setVisibility(View.GONE);
                                    recyclerView.setAdapter(postAdapter);
                                } else {
                                    errorLayout.setVisibility(View.VISIBLE);
                                }
                            } else {
                                errorLayout.setVisibility(View.VISIBLE);
                            }
                            if ((customDialog != null)&& (customDialog.isShowing()))
                                customDialog.dismiss();

                        }, error -> {
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
                                GoToBeginActivity();
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
                            getTrackingList();
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "Bearer " + SharedHelper.getKey(getApplicationContext(),
                                "access_token"));
                        return headers;
                    }
                };
        MyCourier.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    private class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
        JSONArray jsonArray;

        public PostAdapter(JSONArray array) {
            this.jsonArray = array;
        }

        public void append(JSONArray array) {
            try {
                for (int i = 0; i < array.length(); i++) {
                    this.jsonArray.put(array.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public PostAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.track_item, parent, false);
            return new PostAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PostAdapter.MyViewHolder holder, int position) {
            holder.txtdepartlocation.setText(jsonArray
                    .optJSONObject(position).optString("s_address"));
            holder.destinationlocatio.setText(jsonArray
                    .optJSONObject(position).optString("d_address"));

            holder.txtStatus.setText(jsonArray
                    .optJSONObject(position).optString("status"));
            holder.txtassigned.setText("Assigned at: "+jsonArray
                    .optJSONObject(position).optString("assigned_at"));
            holder.booking_id.setText(jsonArray
                    .optJSONObject(position).optString("booking_id"));

        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txtdepartlocation,destinationlocatio,txtStatus,txtassigned,booking_id;


            public MyViewHolder(View itemView) {
                super(itemView);
                txtdepartlocation = itemView.findViewById(R.id.txtdepartlocation);
                destinationlocatio = itemView.findViewById(R.id.destinationlocatio);
                txtStatus = itemView.findViewById(R.id.txtStatus);
                txtassigned=itemView.findViewById(R.id.txtassigned);
                booking_id=itemView.findViewById(R.id.booking_id);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(TrackingDetails.this,MainActivity.class);
                        intent.putExtra("track","track");
                        startActivity(intent);
                        finish();
                    }
                });


            }
        }

        private String getMonth(String date) throws ParseException {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
            return monthName;
        }

        private String getDate(String date) throws ParseException {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String dateName = new SimpleDateFormat("dd").format(cal.getTime());
            return dateName;
        }

        private String getYear(String date) throws ParseException {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
            return yearName;
        }

        private String getTime(String date) throws ParseException {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String timeName = new SimpleDateFormat("hh:mm a").format(cal.getTime());
            return timeName;
        }
    }
    public void findViewByIdAndInitialize() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        errorLayout = (RelativeLayout) findViewById(R.id.errorLayout);
        errorLayout.setVisibility(View.GONE);
        helper = new ConnectionHelper(TrackingDetails.this);
        isInternet = helper.isConnectingToInternet();
        toolbar = (LinearLayout)findViewById(R.id.lnrTitle);
        backImg = (ImageView) findViewById(R.id.backArrow);
    }

    public void displayMessage(String toastString) {
        Snackbar.make(findViewById(R.id.recyclerView), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(TrackingDetails.this, "loggedIn",
                getString(R.string.False));
        Intent mainIntent = new Intent(TrackingDetails.this,
                LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }
}
