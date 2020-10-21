package com.wherego.delivery.user.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.wherego.delivery.user.Adapter.PlacesAutoCompleteAdapter;
import com.wherego.delivery.user.Helper.SharedHelper;
import com.wherego.delivery.user.Models.PlacePredictions;
import com.wherego.delivery.user.R;
import com.wherego.delivery.user.Utils.MyBoldTextView;
import com.wherego.delivery.user.Utils.Utilities;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomGooglePlacesSearch extends AppCompatActivity implements PlacesAutoCompleteAdapter.ClickListener, GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener, OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_LOC = 30;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final int PICK_LOCATION_REQUEST_CODE = 3;
    double latitude;
    double longitude;
    TextView txtPickLocation;
    public static float DEFAULT_ZOOM = 18;
    Utilities utils = new Utilities();
    ImageView backArrow, imgDestClose, imgSourceClose;
    LinearLayout lnrFavorite;
    Activity thisActivity;
    String strSource = "";
    String strSelected = "";
    Bundle extras;
    TextView txtHomeLocation, txtWorkLocation;
    LinearLayout lnrHome, lnrWork;
    RelativeLayout rytAddressSource;
    RecyclerView rvRecentResults, rvLocation;
    String formatted_address = "";
    private EditText txtDestination, txtaddressSource;
    private TextView loc_done;
    private String GETPLACESHIT = "places_hit";
    private PlacePredictions predictions = new PlacePredictions();
    private Location mLastLocation;
    private Handler handler;
    private PlacePredictions placePredictions = new PlacePredictions();
    private int UPDATE_HOME_WORK = 1;

    private FusedLocationProviderClient fusedLocationClient;
    private PlacesClient placesClient;

    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    LinearLayoutManager mLinearLayoutManager;

    private CardView locationBsLayout;
    private CoordinatorLayout dd;

    public Location mLastKnownLocation;
    private boolean isLocationRvClick = false;
    private boolean isSettingLocationClick = false;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private boolean mLocationPermissionGranted;
    private GoogleMap mGoogleMap;
    private String s_address;
    private Double s_latitude;
    private Double s_longitude;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private BottomSheetBehavior mBottomSheetBehavior;
    boolean isEnableIdle = false;
    private boolean isMapKeyShow = false;
    String address = "";
    LatLng target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_soruce_and_destination);
        thisActivity = this;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        initializePlacesApiClient();

        com.google.android.libraries.places.api.Places.initialize(getApplicationContext(), getResources().getString(R.string.google_api_key));
        placesClient = Places.createClient(this);

        txtDestination = (EditText) findViewById(R.id.txtDestination);
        txtaddressSource = (EditText) findViewById(R.id.txtaddressSource);
        loc_done = (TextView) findViewById(R.id.loc_done);

        backArrow = (ImageView) findViewById(R.id.backArrow);
        imgDestClose = (ImageView) findViewById(R.id.imgDestClose);
        imgSourceClose = (ImageView) findViewById(R.id.imgSourceClose);

        txtPickLocation = (TextView) findViewById(R.id.txtPickLocation);
        txtWorkLocation = (TextView) findViewById(R.id.txtWorkLocation);
        txtHomeLocation = (TextView) findViewById(R.id.txtHomeLocation);

        lnrFavorite = (LinearLayout) findViewById(R.id.lnrFavorite);
        lnrHome = (LinearLayout) findViewById(R.id.lnrHome);
        lnrWork = (LinearLayout) findViewById(R.id.lnrWork);

        rytAddressSource = (RelativeLayout) findViewById(R.id.rytAddressSource);

        rvRecentResults = (RecyclerView) findViewById(R.id.rvRecentResults);
        rvLocation = findViewById(R.id.locations_rv);

        locationBsLayout = findViewById(R.id.location_bs_layout);
        dd = findViewById(R.id.dd);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String cursor = getIntent().getExtras().getString("cursor");
        if (getIntent().getExtras().getString("s_address") != null) {
            String s_address = getIntent().getExtras().getString("s_address");
            txtaddressSource.setText(s_address);
        }
        if (getIntent().getExtras().getString("s_address") != null) {
            String d_address = getIntent().getExtras().getString("d_address");

            if (d_address != null && !d_address.equalsIgnoreCase("")) {
                txtDestination.setText(d_address);
            }
        }

        if (cursor.equalsIgnoreCase("source")) {
            strSelected = "source";
            txtaddressSource.requestFocus();
            imgSourceClose.setVisibility(View.GONE);
            imgDestClose.setVisibility(View.GONE);
        } else {
            txtDestination.requestFocus();
            strSelected = "destination";
            imgDestClose.setVisibility(View.GONE);
            imgSourceClose.setVisibility(View.GONE);
        }

        String strStatus = SharedHelper.getKey(thisActivity, "req_status");

        if (strStatus.equalsIgnoreCase("PICKEDUP")) {
            if (SharedHelper.getKey(thisActivity, "track_status").equalsIgnoreCase("YES")) {
                rytAddressSource.setVisibility(View.GONE);
            } else {
                rytAddressSource.setVisibility(View.VISIBLE);
            }
        }

        loc_done.setOnClickListener(v -> {
            if(target!=null && address!=null)
                setGoogleAddress(target, address);
        });


        mBottomSheetBehavior = BottomSheetBehavior.from(locationBsLayout);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });



        txtaddressSource.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    strSelected = "source";
                    imgSourceClose.setVisibility(View.GONE);
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
                    // imgDestClose.setVisibility(View.VISIBLE);
                } else {
                    imgDestClose.setVisibility(View.GONE);
                }
            }
        });

        imgDestClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtDestination.setText("");
                //  imgDestClose.setVisibility(View.GONE);
                txtDestination.requestFocus();
            }
        });

        imgSourceClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtaddressSource.setText("");
                imgSourceClose.setVisibility(View.GONE);
                txtaddressSource.requestFocus();
            }
        });

        txtPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.hideKeypad(thisActivity, thisActivity.getCurrentFocus());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.putExtra("pick_location", "yes");
                        intent.putExtra("type", strSelected);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }, 500);
            }
        });

        lnrHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedHelper.getKey(CustomGooglePlacesSearch.this, "home").equalsIgnoreCase("")) {
                    gotoHomeWork("home");
                } else {
                    if (strSelected.equalsIgnoreCase("destination")) {
                        placePredictions.strDestAddress = SharedHelper.getKey(CustomGooglePlacesSearch.this, "home");
                        placePredictions.strDestLatitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "home_lat");
                        placePredictions.strDestLongitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "home_lng");
                        LatLng latlng = new LatLng(Double.parseDouble(placePredictions.strDestLatitude), Double.parseDouble(placePredictions.strDestLatitude));
                        placePredictions.strDestLatLng = "" + latlng;
                        if (!txtaddressSource.getText().toString().equalsIgnoreCase(SharedHelper.getKey(CustomGooglePlacesSearch.this, "home"))) {
                            txtDestination.setText(SharedHelper.getKey(CustomGooglePlacesSearch.this, "home"));
                            txtDestination.setSelection(0);
                        } else {
                            Toast.makeText(thisActivity, getResources().getString(R.string.source_dest_not_same), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        placePredictions.strSourceAddress = SharedHelper.getKey(CustomGooglePlacesSearch.this, "home");
                        placePredictions.strSourceLatitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "home_lat");
                        placePredictions.strSourceLongitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "home_lng");
                        LatLng latlng = new LatLng(Double.parseDouble(placePredictions.strSourceLatitude), Double.parseDouble(placePredictions.strSourceLongitude));
                        placePredictions.strSourceLatLng = "" + latlng;
                        txtaddressSource.setText(placePredictions.strSourceAddress);
                        txtaddressSource.setSelection(0);
                        txtDestination.requestFocus();
                        mAutoCompleteAdapter = null;
                    }

                    if (!txtDestination.getText().toString().equalsIgnoreCase("")) {
                        setAddress();
                    }
                }
            }
        });

        lnrWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txtaddressSource.getText().toString().equalsIgnoreCase(txtDestination.getText().toString())) {
                    if (SharedHelper.getKey(CustomGooglePlacesSearch.this, "work").equalsIgnoreCase("")) {
                        gotoHomeWork("work");
                    } else {
                        if (strSelected.equalsIgnoreCase("destination")) {
                            placePredictions.strDestAddress = SharedHelper.getKey(CustomGooglePlacesSearch.this, "work");
                            placePredictions.strDestLatitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "work_lat");
                            placePredictions.strDestLongitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "work_lng");
                            LatLng latlng = new LatLng(Double.parseDouble(placePredictions.strDestLatitude), Double.parseDouble(placePredictions.strDestLatitude));
                            placePredictions.strDestLatLng = "" + latlng;
                            if (!txtaddressSource.getText().toString().equalsIgnoreCase(SharedHelper.getKey(CustomGooglePlacesSearch.this, "work"))) {
                                txtDestination.setText(SharedHelper.getKey(CustomGooglePlacesSearch.this, "work"));
                                txtDestination.setSelection(0);
                            } else {
                                Toast.makeText(thisActivity, getResources().getString(R.string.source_dest_not_same), Toast.LENGTH_SHORT).show();
                            }
                            txtDestination.setSelection(0);
                        } else {
                            placePredictions.strSourceAddress = SharedHelper.getKey(CustomGooglePlacesSearch.this, "work");
                            placePredictions.strSourceLatitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "work_lat");
                            placePredictions.strSourceLongitude = SharedHelper.getKey(CustomGooglePlacesSearch.this, "work_lng");
                            LatLng latlng = new LatLng(Double.parseDouble(placePredictions.strSourceLatitude), Double.parseDouble(placePredictions.strSourceLongitude));
                            placePredictions.strSourceLatLng = "" + latlng;
                            txtaddressSource.setText(placePredictions.strSourceAddress);
                            txtaddressSource.setSelection(0);
                            txtDestination.requestFocus();
                            mAutoCompleteAdapter = null;
                        }

                        if (!txtDestination.getText().toString().equalsIgnoreCase("")) {
                            setAddress();
                        }
                    }
                }
            }
        });

        //get permission for Android M
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            getLastKnownLocation();
        } else {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOC);
            } else {
                getLastKnownLocation();
            }
        }

        //Add a text change listener to implement autocomplete functionality
        txtDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                imgDestClose.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // optimised way is to start searching for laction after user has typed minimum 3 chars
                imgDestClose.setVisibility(View.GONE);
                strSelected = "destination";
                if (!s.toString().equals("")) {
                    rvLocation.setVisibility(View.VISIBLE);
                    loc_done.setVisibility(View.GONE);
                    mAutoCompleteAdapter.getFilter().filter(s.toString());
                    if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                if (s.toString().equals("")) {
                    rvLocation.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                imgDestClose.setVisibility(View.GONE);
            }

        });

        //Add a text change listener to implement autocomplete functionality
        txtaddressSource.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                imgSourceClose.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // optimised way is to start searching for laction after user has typed minimum 3 chars
                strSelected = "source";
                if (!s.toString().equals("")) {
                    rvLocation.setVisibility(View.VISIBLE);
                    loc_done.setVisibility(View.GONE);
                    mAutoCompleteAdapter.getFilter().filter(s.toString());
                    if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                if (s.toString().equals("")) {
                    rvLocation.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                imgSourceClose.setVisibility(View.GONE);
            }

        });

        //txtDestination.setText("");
        txtDestination.setSelection(txtDestination.getText().length());

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initializeadapter();
    }



/*    private void setGoogleAddress(int position) {
        if (mGoogleApiClient != null) {
            utils.print("", "Place ID == >" + predictions.getPlaces().get(position).getPlaceID());
            Places.GeoDataApi.getPlaceById(mGoogleApiClient, predictions.getPlaces().get(position).getPlaceID())
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if (places.getStatus().isSuccess()) {
                                Place myPlace = places.get(0);
                                LatLng queriedLocation = myPlace.getLatLng();
                                Log.v("Latitude is", "" + queriedLocation.latitude);
                                Log.v("Longitude is", "" + queriedLocation.longitude);
                                if (strSelected.equalsIgnoreCase("destination")) {
                                    placePredictions.strDestAddress = myPlace.getAddress().toString();
                                    placePredictions.strDestLatLng = myPlace.getLatLng().toString();
                                    placePredictions.strDestLatitude = myPlace.getLatLng().latitude + "";
                                    placePredictions.strDestLongitude = myPlace.getLatLng().longitude + "";
                                    txtDestination.setText(placePredictions.strDestAddress);
                                    txtDestination.setSelection(0);
                                } else {
                                    placePredictions.strSourceAddress = myPlace.getAddress().toString();
                                    placePredictions.strSourceLatLng = myPlace.getLatLng().toString();
                                    placePredictions.strSourceLatitude = myPlace.getLatLng().latitude + "";
                                    placePredictions.strSourceLongitude = myPlace.getLatLng().longitude + "";
                                    txtaddressSource.setText(placePredictions.strSourceAddress);
                                    txtaddressSource.setSelection(0);
                                    txtDestination.requestFocus();
                                    mAutoCompleteAdapter = null;
                                }
                            }

                            if (txtDestination.getText().toString().length() > 0) {
                                places.release();
                                if (strSelected.equalsIgnoreCase("destination")) {
                                    if (!placePredictions.strDestAddress.equalsIgnoreCase(placePredictions.strSourceAddress)) {
                                        setAddress();
                                    } else {
                                        utils.showAlert(thisActivity, thisActivity.getResources().getString(R.string.source_dest_not_same));
                                    }
                                }
                            } else {
                                txtDestination.requestFocus();
                                txtDestination.setText("");
                                imgDestClose.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }*/

    private void setGoogleAddress(Place place) {
        if (strSelected.equalsIgnoreCase("destination")) {
            placePredictions.strDestAddress = place.getAddress();
            if (place.getLatLng() != null) {
                placePredictions.strDestLatLng = place.getLatLng().toString();
                placePredictions.strDestLatitude = place.getLatLng().latitude + "";
                placePredictions.strDestLongitude = place.getLatLng().longitude + "";
            }
            txtDestination.setText(placePredictions.strDestAddress);
            txtDestination.setSelection(0);
        } else {
            placePredictions.strSourceAddress = place.getAddress();
            if (place.getLatLng() != null) {
                placePredictions.strSourceLatLng = place.getLatLng().toString();
                placePredictions.strSourceLatitude = place.getLatLng().latitude + "";
                placePredictions.strSourceLongitude = place.getLatLng().longitude + "";
            }
            txtaddressSource.setText(placePredictions.strSourceAddress);
            txtaddressSource.setSelection(0);
            txtDestination.requestFocus();
        }

        if (txtDestination.getText().toString().length() > 0) {
            if (strSelected.equalsIgnoreCase("destination")) {
                if (!placePredictions.strDestAddress
                        .equalsIgnoreCase(placePredictions.strSourceAddress)) {
                    setAddress();
                } else {
                    utils.showAlert(thisActivity,
                            getResources().getString(R.string.source_dest_not_same));
                }
            }
        } else {
            txtDestination.requestFocus();
            txtDestination.setText("");
            //imgDestClose.setVisibility(View.GONE);
        }
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
        urlString.append("&key=" + getResources().getString(R.string.google_api_key));

        Log.d("FINAL URL:::   ", urlString.toString());
        return urlString.toString();
    }

    @SuppressLint("MissingPermission")
    private void getLastKnownLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mLastLocation = location;
                            latitude = mLastLocation.getLatitude();
                            longitude = mLastLocation.getLongitude();
                        }
                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOC: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    getLastKnownLocation();
                } else {
                    // permission denied!
                    Toast.makeText(this, "Please grant permission for using this app!", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    updateLocationUI();
                    getDeviceLocation();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void place(Place place) {
        if (place != null) {
            LatLng latLng = place.getLatLng();
            setGoogleAddress(place);
        }
    }

    private void initializePlacesApiClient() {
        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
        // Create a new Places client instance
        placesClient = com.google.android.libraries.places.api.Places.createClient(this);
    }

    public void initializeadapter() {
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, placesClient);
        mAutoCompleteAdapter.setClickListener(this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        rvLocation.setLayoutManager(mLinearLayoutManager);
        rvLocation.setAdapter(mAutoCompleteAdapter);
    }

    void setAddress() {
        utils.hideKeypad(thisActivity, getCurrentFocus());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                if (placePredictions != null) {
                    intent.putExtra("Location Address", placePredictions);
                    intent.putExtra("pick_lo" +
                            "cation", "no");
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
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void gotoHomeWork(String strTag) {
       /* Intent intentHomeWork = new Intent(CustomGooglePlacesSearch.this, .class);
        intentHomeWork.putExtra("tag", strTag);
        startActivityForResult(intentHomeWork, UPDATE_HOME_WORK);*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_HOME_WORK) {
            if (resultCode == Activity.RESULT_OK) {
                //getFavoriteLocations();
            }
        }
    }

    @Override
    public void onCameraIdle() {
        rvLocation.setVisibility(View.GONE);
        imgDestClose.setVisibility(View.VISIBLE);
        try {
            CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
            if (isEnableIdle) {
                rvLocation.setVisibility(View.GONE);
                String address = getAddress(cameraPosition.target);
                System.out.println("onCameraIdle " + address);
                isMapKeyShow = true;
                if (isMapKeyShow) hideKeyboard();
                else showKeyboard();
                setLocationText(address, cameraPosition.target);
            }
            isEnableIdle = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setLocationText(String address, LatLng target) {

        if (address != null && target != null) {
            txtDestination.setText(address);
            Log.d("LocationPick", "setLocationText1: " + txtDestination.getTag());
            loc_done.setVisibility(View.VISIBLE);
            rvLocation.setVisibility(View.GONE);
            this.address = address;
            this.target = target;
            imgDestClose.setVisibility(View.VISIBLE);
        }
    }

    private void setGoogleAddress(LatLng place, String address) {
        if (strSelected.equalsIgnoreCase("destination")) {
            placePredictions.strDestAddress = address;
            if (place != null) {
                placePredictions.strDestLatLng = place.toString() + "";
                placePredictions.strDestLatitude = place.latitude + "";
                placePredictions.strDestLongitude = place.longitude + "";
            }
            txtDestination.setText(placePredictions.strDestAddress);
            txtDestination.setSelection(0);
        } else {
            placePredictions.strSourceAddress = address;
            if (place != null) {
                placePredictions.strSourceLatLng = place.toString() + "";
                placePredictions.strSourceLatitude = place.latitude + "";
                placePredictions.strSourceLongitude = place.longitude + "";
            }
            txtaddressSource.setText(placePredictions.strSourceAddress);
            txtaddressSource.setSelection(0);
            txtDestination.requestFocus();
        }

        if (txtDestination.getText().toString().length() > 0) {
            if (strSelected.equalsIgnoreCase("destination")) {
                if (!placePredictions.strDestAddress
                        .equalsIgnoreCase(placePredictions.strSourceAddress)) {
                    setAddress();
                } else {
                    utils.showAlert(thisActivity,
                            getResources().getString(R.string.source_dest_not_same));
                }
            }
        } else {
            txtDestination.requestFocus();
            txtDestination.setText("");
            imgDestClose.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        } catch (Resources.NotFoundException e) {
            Log.d("Map:Style", "Can't find style. Error: ");
        }
        this.mGoogleMap = googleMap;
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mLocationPermissionGranted = true;
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    private void updateLocationUI() {
        if (mGoogleMap == null) return;
        try {
            if (mLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mGoogleMap.setOnCameraMoveListener(this);
                mGoogleMap.setOnCameraIdleListener(this);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        mLastLocation = mLastKnownLocation;
                        latitude = mLastLocation.getLatitude();
                        longitude = mLastLocation.getLongitude();
                        mGoogleMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(new LatLng(
                                        mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()
                                ), DEFAULT_ZOOM));
                    } else {
                        Log.d("Map", "Current location is null. Using defaults.");
                        Log.e("Map", "Exception: %s", task.getException());
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void showKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public String getAddress(LatLng currentLocation) {
        String address = null;
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1);
//            List<Address> addresses = geocoder.getFromLocation(36.851547, 10.297449, 1);
            if ((addresses != null) && !addresses.isEmpty()) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                if (returnedAddress.getMaxAddressLineIndex() > 0)
                    for (int j = 0; j < returnedAddress.getMaxAddressLineIndex(); j++)
                        strReturnedAddress.append(returnedAddress.getAddressLine(j)).append("");
                else strReturnedAddress.append(returnedAddress.getAddressLine(0)).append("");
                address = strReturnedAddress.toString();
            }
        } catch (Exception e) {
            Log.e("MAP", "getAddress: " + e);
        }
        return address;
    }


}
