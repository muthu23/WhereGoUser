package com.wherego.delivery.user.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.snackbar.Snackbar;
import com.wherego.delivery.user.Helper.ConnectionHelper;
import com.wherego.delivery.user.Helper.CustomDialog;
import com.wherego.delivery.user.Helper.SharedHelper;
import com.wherego.delivery.user.Helper.URLHelper;
import com.wherego.delivery.user.R;
import com.wherego.delivery.user.MyCourier;

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

import static com.wherego.delivery.user.MyCourier.trimMessage;

public class ScheduleListActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_schedule_list);
        findViewByIdAndInitialize();
        if (isInternet) {
            getHistoryList();
        }

        backImg.setOnClickListener(v -> {
            startActivity(new Intent(ScheduleListActivity.this,
                    MainActivity.class));
            finish();
        });

    }

    public void getHistoryList() {
        customDialog = new CustomDialog(ScheduleListActivity.this);
        customDialog.setCancelable(false);
        customDialog.show();

        JsonArrayRequest jsonArrayRequest = new
                JsonArrayRequest(URLHelper.UPCOMING_TRIPS,
                        response -> {
                            if (response != null) {
                                postAdapter = new PostAdapter(response);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ScheduleListActivity.this) {
                                    @Override
                                    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                                        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
                                    }
                                });
                                if (postAdapter != null && postAdapter.getItemCount() > 0) {
                                    errorLayout.setVisibility(View.GONE);
                                    recyclerView.setAdapter(postAdapter);
                                } else {
                                    errorLayout.setVisibility(View.VISIBLE);
                                }

                            } else {
                                errorLayout.setVisibility(View.VISIBLE);
                            }

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
                            getHistoryList();
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "Bearer " + SharedHelper
                                .getKey(getApplicationContext(), "access_token"));
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
                    .inflate(R.layout.history_item, parent, false);
            return new PostAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PostAdapter.MyViewHolder holder, int position) {
            holder.txtdepartlocation.setText(jsonArray
                    .optJSONObject(position).optString("s_address"));
            holder.destinationlocatio.setText(jsonArray
                    .optJSONObject(position).optString("d_address"));
            holder.tripProviderRating.setRating(Float.parseFloat(jsonArray
                    .optJSONObject(position).optString("provider_rated")));

            try {
                if (!jsonArray.optJSONObject(position).optString("assigned_at", "")
                        .isEmpty()) {
                    String form = jsonArray.optJSONObject(position).optString("assigned_at");
                    try {
                        holder.journeydate.setText(getDate(form) + "th " +
                                getMonth(form) + " " + getYear(form));
                        holder.txtTime.setText(" at " + getTime(form));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txtdepartlocation, journeydate, destinationlocatio, txtTime;
            RatingBar tripProviderRating;

            public MyViewHolder(View itemView) {
                super(itemView);
                txtdepartlocation = itemView.findViewById(R.id.txtdepartlocation);
                journeydate = itemView.findViewById(R.id.journeydate);
                destinationlocatio = itemView.findViewById(R.id.destinationlocatio);
                txtTime = itemView.findViewById(R.id.txtTime);
                tripProviderRating = itemView.findViewById(R.id.tripProviderRating);

                itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(ScheduleListActivity.this,
                            HistoryDetails.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("post_value",
                            jsonArray.optJSONObject(getAdapterPosition()).toString());
                    intent.putExtra("tag", "past_trips");
                    startActivity(intent);
                });

            }
        }

        private String getMonth(String date) throws ParseException {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
            return monthName;
        }

        private String getDate(String date) throws ParseException {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String dateName = new SimpleDateFormat("dd").format(cal.getTime());
            return dateName;
        }

        private String getYear(String date) throws ParseException {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
            return yearName;
        }

        private String getTime(String date) throws ParseException {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String timeName = new SimpleDateFormat("hh:mm a").format(cal.getTime());
            return timeName;
        }
    }

    public void findViewByIdAndInitialize() {
        recyclerView = findViewById(R.id.recyclerView);
        errorLayout = findViewById(R.id.errorLayout);
        errorLayout.setVisibility(View.GONE);
        helper = new ConnectionHelper(ScheduleListActivity.this);
        isInternet = helper.isConnectingToInternet();
        toolbar = findViewById(R.id.lnrTitle);
        backImg = findViewById(R.id.backArrow);
    }

    public void displayMessage(String toastString) {
        Snackbar.make(findViewById(R.id.recyclerView),
                toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(ScheduleListActivity.this,
                "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(ScheduleListActivity.this,
                LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }
}
