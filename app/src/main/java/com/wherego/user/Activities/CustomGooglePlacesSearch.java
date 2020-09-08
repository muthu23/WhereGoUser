package com.wherego.user.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.wherego.user.Constants.AutoCompleteAdapter;
import com.wherego.user.Helper.SharedHelper;
import com.wherego.user.MyCourier;
import com.wherego.user.Models.PlacePredictions;
import com.wherego.user.R;
import com.wherego.user.Utils.Utilities;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

public class CustomGooglePlacesSearch extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    double latitude;
    double longitude;
    private ListView mAutoCompleteList;
    private EditText txtDestination, txtaddressSource;
    private String GETPLACESHIT = "places_hit";
    private PlacePredictions predictions = new PlacePredictions();
    private Location mLastLocation;
    private AutoCompleteAdapter mAutoCompleteAdapter;
    private static final int MY_PERMISSIONS_REQUEST_LOC = 30;
    private Handler handler;
    private GoogleApiClient mGoogleApiClient;
    TextView txtPickLocation;
    Utilities utils = new Utilities();
    ImageView backArrow, imgDestClose, imgSourceClose;
    TextView txtHomeAddress, txtWorkAddress;
    LinearLayout layoutWork, layoutHome;
    Activity thisActivity;
    String strSource = "";
    CardView cardExtra;
    String strSelected = "";
    private PlacePredictions placePredictions = new PlacePredictions();
    Bundle extras;

    LinearLayout llSavedAddress, llHome, llWork, llOther;
    TextView tvLocationTypeHome, tvLocationTypeWork, tvLocationTypeOther;
    TextView tvLocationAddressHome, tvLocationAddressWork, tvLocationAddressOther;
    PlacesClient placesClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedHelper.getKey(this,"selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        setContentView(R.layout.fragment_soruce_and_destination);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        thisActivity = this;
        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.google_map_api));

// Create a new Places client instance
         placesClient = Places.createClient(this);
        txtDestination =  findViewById(R.id.txtDestination);
        txtaddressSource =  findViewById(R.id.txtaddressSource);
        mAutoCompleteList = (ListView) findViewById(R.id.searchResultLV);

        txtWorkAddress = findViewById(R.id.txtWorkAddress);
        txtHomeAddress = findViewById(R.id.txtHomeAddress);
        layoutWork = findViewById(R.id.layoutWork);
        layoutHome = findViewById(R.id.layoutHome);
        backArrow =  findViewById(R.id.backArrow);
        imgDestClose =  findViewById(R.id.imgDestClose);
        imgSourceClose =  findViewById(R.id.imgSourceClose);
        cardExtra = findViewById(R.id.cardExtra);
        txtPickLocation =  findViewById(R.id.txtPickLocation);

        String cursor = getIntent().getExtras().getString("cursor");
        String s_address = getIntent().getExtras().getString("s_address");
        String d_address = getIntent().getExtras().getString("d_address");
        Log.e("CustomGoogleSearch", "onCreate: source " + s_address);
        Log.e("CustomGoogleSearch", "onCreate: destination" + d_address);
        txtaddressSource.setText(s_address);

        if (d_address != null && !d_address.equalsIgnoreCase("")) {
            txtDestination.setText(d_address);
        }

        if (cursor.equalsIgnoreCase("source")) {
            strSelected = "source";
            txtaddressSource.requestFocus();
            imgSourceClose.setVisibility(View.VISIBLE);
            imgDestClose.setVisibility(View.GONE);
        } else {
            txtDestination.requestFocus();
            strSelected = "destination";
            imgDestClose.setVisibility(View.VISIBLE);
            imgSourceClose.setVisibility(View.GONE);
        }

        txtaddressSource.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    strSelected = "source";
                    imgSourceClose.setVisibility(View.VISIBLE);
                } else {
                    imgSourceClose.setVisibility(View.GONE);
                }
            }
        });

        txtDestination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    strSelected = "destination";
                    imgDestClose.setVisibility(View.VISIBLE);
                } else {
                    imgDestClose.setVisibility(View.GONE);
                }
            }
        });

        imgDestClose.setOnClickListener(v -> {
            txtDestination.setText("");
            mAutoCompleteList.setVisibility(View.GONE);
            imgDestClose.setVisibility(View.GONE);
            txtDestination.requestFocus();
        });

        imgSourceClose.setOnClickListener(v -> {
            txtaddressSource.setText("");
            mAutoCompleteList.setVisibility(View.GONE);
            imgSourceClose.setVisibility(View.GONE);
            txtaddressSource.requestFocus();
        });

        txtPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.hideKeypad(thisActivity, thisActivity.getCurrentFocus());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.putExtra("Location Address", placePredictions);
                        intent.putExtra("pick_location", "yes");
                        intent.putExtra("type", strSelected);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }, 500);
            }
        });

        if (SharedHelper.getKey(CustomGooglePlacesSearch.this, "Home_address") != null
                && SharedHelper.getKey(CustomGooglePlacesSearch.this, "Home_address") != "") {
            txtHomeAddress.setVisibility(View.VISIBLE);
            txtHomeAddress.setText(SharedHelper.getKey(CustomGooglePlacesSearch.this, "Home_address"));
            layoutHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String longitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "Homelatitude");
                    String latitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "Homelongitude");
                    LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    placePredictions.strDestAddress = SharedHelper.getKey(CustomGooglePlacesSearch.this, "Home_address");
                    placePredictions.strDestLatLng = latLng + "";
                    placePredictions.strDestLatitude = latitude + "";
                    placePredictions.strDestLongitude = longitude + "";
                    if (!placePredictions.strDestAddress.equalsIgnoreCase(placePredictions.strSourceAddress)) {
                        setAddress();
                    } else {
                        utils.showAlert(thisActivity, "Source and Destination address should not be same!");
                    }

                }
            });
        } else {
            layoutHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CustomGooglePlacesSearch.this, FavouritePlaceSearch.class);
                    intent.putExtra("type", "Home");
                    startActivityForResult(intent, 2);
                }
            });
        }
        if (SharedHelper.getKey(CustomGooglePlacesSearch.this, "Work_address") != null &&
                SharedHelper.getKey(CustomGooglePlacesSearch.this, "Work_address") != "") {
            txtWorkAddress.setVisibility(View.VISIBLE);
            txtWorkAddress.setText(SharedHelper.getKey(CustomGooglePlacesSearch.this, "Work_address"));
            layoutWork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String longitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "Worklatitude");
                    String latitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "Worklongitude");
                    LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    placePredictions.strDestAddress = SharedHelper.getKey(CustomGooglePlacesSearch.this, "Work_address");
                    placePredictions.strDestLatLng = latLng + "";
                    placePredictions.strDestLatitude = latitude + "";
                    placePredictions.strDestLongitude = longitude + "";
                    setAddress();
                }
            });
        } else {
            layoutWork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CustomGooglePlacesSearch.this, FavouritePlaceSearch.class);
                    intent.putExtra("type", "Work");
                    startActivityForResult(intent, 2);
                }
            });
        }

        txtDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                imgDestClose.setVisibility(View.VISIBLE);
                cardExtra.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // optimised way is to start searching for laction after user has typed minimum 3 chars
                imgDestClose.setVisibility(View.VISIBLE);
                cardExtra.setVisibility(View.GONE);
                strSelected = "destination";
                if (txtDestination.getText().length() > 0) {
                    txtPickLocation.setVisibility(View.VISIBLE);
                    imgDestClose.setVisibility(View.VISIBLE);
                    txtPickLocation.setText(getString(R.string.pin_location));
                    Runnable run = new Runnable() {
                        @Override
                        public void run() {
                            // cancel all the previous requests in the queue to optimise your network calls during autocomplete search
                            MyCourier.getInstance().cancelRequestInQueue(GETPLACESHIT);

                            JSONObject object = new JSONObject();
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getPlaceAutoCompleteUrl(txtDestination.getText().toString()), object, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.v("PayNowRequestResponse", response.toString());
                                    Log.v("PayNowRequestResponse", response.toString());
                                    Gson gson = new Gson();
                                    predictions = gson.fromJson(response.toString(), PlacePredictions.class);
                                    if (predictions.getPlaces()!=null) {
                                        if (mAutoCompleteAdapter == null) {
                                            mAutoCompleteList.setVisibility(View.VISIBLE);
                                            mAutoCompleteAdapter = new AutoCompleteAdapter(CustomGooglePlacesSearch.this, predictions.getPlaces(), CustomGooglePlacesSearch.this);
                                            mAutoCompleteList.setAdapter(mAutoCompleteAdapter);
                                        } else {
                                            mAutoCompleteList.setVisibility(View.VISIBLE);
                                            mAutoCompleteAdapter.clear();
                                            mAutoCompleteAdapter.addAll(predictions.getPlaces());
                                            mAutoCompleteAdapter.notifyDataSetChanged();
                                            mAutoCompleteList.invalidate();
                                        }
                                    }
                                    else {
                                        Log.v("prediction error",predictions.getPlaces()+"");
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.v("PayNowRequestResponse", error.toString());
                                }
                            });
                            MyCourier.getInstance().addToRequestQueue(jsonObjectRequest);

                        }

                    };

                    // only canceling the network calls will not help, you need to remove all callbacks as well
                    // otherwise the pending callbacks and messages will again invoke the handler and will send the request
                    if (handler != null) {
                        handler.removeCallbacksAndMessages(null);
                    } else {
                        handler = new Handler();
                    }
                    handler.postDelayed(run, 1000);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                imgDestClose.setVisibility(View.VISIBLE);
            }

        });

        //Add a text change listener to implement autocomplete functionality
        txtaddressSource.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                imgSourceClose.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // optimised way is to start searching for laction after user has typed minimum 3 chars
                strSelected = "source";
                if (txtaddressSource.getText().length() > 0) {
                    txtPickLocation.setVisibility(View.VISIBLE);
                    imgSourceClose.setVisibility(View.VISIBLE);
                    txtPickLocation.setText(getString(R.string.pin_location));
                    if (mAutoCompleteAdapter == null)
                        mAutoCompleteList.setVisibility(View.VISIBLE);
                    Runnable run = new Runnable() {

                        @Override
                        public void run() {
                            // cancel all the previous requests in the queue to optimise your network calls during autocomplete search
                            MyCourier.getInstance().cancelRequestInQueue(GETPLACESHIT);

                            JSONObject object = new JSONObject();
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getPlaceAutoCompleteUrl(txtaddressSource.getText().toString()),
                                    object, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.v("PayNowRequestResponse", response.toString());
                                    Log.v("PayNowRequestResponse", response.toString());
                                    Gson gson = new Gson();
                                    predictions = gson.fromJson(response.toString(), PlacePredictions.class);
                                    if (mAutoCompleteAdapter == null) {
                                        mAutoCompleteAdapter = new AutoCompleteAdapter(CustomGooglePlacesSearch.this, predictions.getPlaces(), CustomGooglePlacesSearch.this);
                                        mAutoCompleteList.setAdapter(mAutoCompleteAdapter);
                                    } else {
                                        mAutoCompleteList.setVisibility(View.VISIBLE);
                                        mAutoCompleteAdapter.clear();
                                        mAutoCompleteAdapter.addAll(predictions.getPlaces());
                                        mAutoCompleteAdapter.notifyDataSetChanged();
                                        mAutoCompleteList.invalidate();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.v("PayNowRequestResponse", error.toString());
                                }
                            });
                            MyCourier.getInstance().addToRequestQueue(jsonObjectRequest);

                        }

                    };

                    // only canceling the network calls will not help, you need to remove all callbacks as well
                    // otherwise the pending callbacks and messages will again invoke the handler and will send the request
                    if (handler != null) {
                        handler.removeCallbacksAndMessages(null);
                    } else {
                        handler = new Handler();
                    }
                    handler.postDelayed(run, 1000);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                imgSourceClose.setVisibility(View.VISIBLE);
            }

        });

        //txtDestination.setText("");
        txtDestination.setSelection(txtDestination.getText().length());

        mAutoCompleteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (txtaddressSource.getText().toString().equalsIgnoreCase("")) {
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                        LayoutInflater inflater = (LayoutInflater) thisActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        builder.setMessage("Please choose pickup location")
                                .setTitle(thisActivity.getString(R.string.app_name))
                                .setCancelable(true)
                                .setIcon(R.mipmap.ic_launcher_round)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        txtaddressSource.requestFocus();
                                        txtDestination.setText("");
                                        imgDestClose.setVisibility(View.GONE);
                                        mAutoCompleteList.setVisibility(View.GONE);
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        setGoogleAddress(position);
                    }catch (Exception e)
                    {e.printStackTrace();}
                }
            }
        });
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setAddress();
                finish();
            }
        });

    }
        String TAG="CustomGoogleplacesearch";
    private void setGoogleAddress(int position)throws Exception {
//        if (mGoogleApiClient != null) {


            // Define a Place ID.
            String placeId = predictions.getPlaces().get(position).getPlaceID();

// Specify the fields to return.
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME
                    , Place.Field.ADDRESS, Place.Field.LAT_LNG);

// Construct a request object, passing the place ID and fields array.
            FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                Place place = response.getPlace();

                if (strSelected.equalsIgnoreCase("destination")) {
//                                    placePredictions.strDestAddress = myPlace.getAddress().toString();
                    placePredictions.strDestAddress =  predictions.getPlaces().get(position).getPlaceDesc();
                    placePredictions.strDestLatLng = place.getLatLng().toString();
                    placePredictions.strDestLatitude = place.getLatLng().latitude + "";
                    placePredictions.strDestLongitude = place.getLatLng().longitude + "";
                    txtDestination.setText(placePredictions.strDestAddress);
                    txtDestination.setSelection(0);
                } else {
                    placePredictions.strSourceAddress =predictions.getPlaces().get(position).getPlaceDesc();
                    placePredictions.strSourceLatLng = place.getLatLng().toString();
                    placePredictions.strSourceLatitude = place.getLatLng().latitude + "";
                    placePredictions.strSourceLongitude = place.getLatLng().longitude + "";
                    txtaddressSource.setText(placePredictions.strSourceAddress);
                    txtaddressSource.setSelection(0);
                    txtDestination.requestFocus();
                    mAutoCompleteAdapter = null;
                }

                mAutoCompleteList.setVisibility(View.GONE);

                if (txtDestination.getText().toString().length() > 0) {
                    if (strSelected.equalsIgnoreCase("destination")) {
                        if (!placePredictions.strDestLatitude.equalsIgnoreCase(placePredictions.strSourceLatitude)) {
                            setAddress();
                        } else {
                            utils.showAlert(thisActivity, "Source and Destination address should not be same!");
                        }
                    }
                } else {
                    txtDestination.requestFocus();
                    txtDestination.setText("");
                    imgDestClose.setVisibility(View.GONE);
                    mAutoCompleteList.setVisibility(View.GONE);
                }


                Log.i(TAG, "Place found: " + place.getName());
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    // Handle error with given status code.
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                }
            });

    }

    public String getPlaceAutoCompleteUrl(String input) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/place/autocomplete/json");
        urlString.append("?input=");
        try {
            urlString.append(URLEncoder.encode(input, "utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlString.append("&location=");
        urlString.append(latitude + "," + longitude); // append lat long of current location to show nearby results.
        urlString.append("&radius=500&language=en");
        urlString.append("&key=" + getResources().getString(R.string.google_map_api));

        Log.d("FINAL URL:::   ", urlString.toString());
        return urlString.toString();
    }

//    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .build();
//    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

//    public void fetchLocation() {
//        //Build google API client to use fused location
//        buildGoogleApiClient();
//
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.connect();
//        }
//    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            try {
                String result = data.getStringExtra("result");
                String type = data.getStringExtra("type");
                if (type.equalsIgnoreCase("home")) {
                    if (SharedHelper.getKey(CustomGooglePlacesSearch.this, "Home_address") != "" && SharedHelper.getKey(CustomGooglePlacesSearch.this, "Home_address") != null) {
                        String longitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "Homelatitude");
                        String latitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "Homelongitude");
                        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        placePredictions.strDestAddress = SharedHelper.getKey(CustomGooglePlacesSearch.this, "Home_address");
                        placePredictions.strDestLatLng = latLng + "";
                        placePredictions.strDestLatitude = latitude + "";
                        placePredictions.strDestLongitude = longitude + "";
                        if (!placePredictions.strDestAddress.equalsIgnoreCase(placePredictions.strSourceAddress)) {
                            setAddress();
                        } else {
                            utils.showAlert(thisActivity, "Source and Destination address should not be same!");
                        }


                    }
                } else if (type.equalsIgnoreCase("work")) {
                    if (SharedHelper.getKey(CustomGooglePlacesSearch.this, "Work_address") != "" && SharedHelper.getKey(CustomGooglePlacesSearch.this, "Work_address") != null) {
                        String longitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "Worklatitude");
                        String latitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "Worklongitude");
                        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        placePredictions.strDestAddress = SharedHelper.getKey(CustomGooglePlacesSearch.this, "Work_address");
                        placePredictions.strDestLatLng = latLng + "";
                        placePredictions.strDestLatitude = latitude + "";
                        placePredictions.strDestLongitude = longitude + "";
                        if (!placePredictions.strDestAddress.equalsIgnoreCase(placePredictions.strSourceAddress)) {
                            setAddress();
                        } else {
                            utils.showAlert(thisActivity, "Source and Destination address should not be same!");
                        }

                    }
                }

            } catch (Exception e) {

            }


        }

    }//on
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOC: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
//                    fetchLocation();
                } else {
                    // permission denied!
                    Toast.makeText(this, "Please grant permission for using this app!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {
        setAddress();
        super.onBackPressed();
    }

    void setAddress() {
        utils.hideKeypad(thisActivity, getCurrentFocus());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                if (placePredictions != null) {
                    intent.putExtra("Location Address", placePredictions);
                    intent.putExtra("pick_location", "no");
                    setResult(RESULT_OK, intent);
                } else {
                    setResult(RESULT_CANCELED, intent);
                }
                finish();
            }
        }, 500);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }
}
