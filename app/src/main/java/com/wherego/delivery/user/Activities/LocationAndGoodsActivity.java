package com.wherego.delivery.user.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wherego.delivery.user.Adapter.LocationsAdapter;
import com.wherego.delivery.user.Helper.SharedHelper;
import com.wherego.delivery.user.Models.Locations;
import com.wherego.delivery.user.Models.PlacePredictions;
import com.wherego.delivery.user.R;

import java.util.ArrayList;

public class LocationAndGoodsActivity extends AppCompatActivity implements LocationsAdapter.LocationsListener {

    private static final String TAG = "LocationAndGoodsActivit";

    RecyclerView recyclerView;
    Button submitBtn;
    FloatingActionButton addFab;
    LocationsAdapter locationsAdapter;
    ArrayList<Locations> locationsArrayList = new ArrayList<>();
    ImageView backArrow;

    private Context context = LocationAndGoodsActivity.this;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST = 18945;

    Locations locations;
    public boolean isPickup;
    String dAddress;
    private String sAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_and_goods);
        findViewsById();
        sAddress = getIntent().getStringExtra("s_address");
        locationsArrayList.add(getLocation());
        setupRecyclerView();
        submitBtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            for (int i = 0; i < locationsArrayList.size(); i++) {

                if (locationsArrayList.get(i).getdAddress() == null || locationsArrayList.get(i).getdAddress().isEmpty()) {
                    Toast.makeText(context, context.getResources().getString(R.string.empty_dest), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (locationsArrayList.get(i).getDescription() == null) {
                    Toast.makeText(context, context.getResources().getString(R.string.empty_goods), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (locationsArrayList.get(0) != null) {
                int i = 1;
                for (Locations locations : locationsArrayList) {
                    intent.putExtra("Location Address" + i + "", locations);
                    i++;
                }
                intent.putExtra("Location size", locationsArrayList.size());
                Log.e(TAG, "onClick: ", locationsArrayList.get(0));
                intent.putExtra("pick_lo" +
                        "cation", "no");
                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_CANCELED, intent);
            }
            finish();
        });
        backArrow.setOnClickListener(v -> finish());
        addFab.setOnClickListener(v -> {
            locationsArrayList.add(getLocation());
            refreshAdapter();
        });


    }

    public void refreshAdapter() {
        locationsAdapter.setListModels(locationsArrayList);
        locationsAdapter.notifyDataSetChanged();
    }

    private Locations getLocation() {
        Locations locations = new Locations();
        locations.setsLatitude(SharedHelper.getKey(context, "curr_lat"));
        locations.setsLongitude(SharedHelper.getKey(context, "curr_lng"));
        locations.setsAddress(sAddress);
        locations.setdAddress("");
        locations.setdLatitude(null);
        locations.setdLongitude(null);
        return locations;
    }

    private void setupRecyclerView() {
        locationsAdapter = new LocationsAdapter(locationsArrayList, context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        locationsAdapter.setLocationsListener(this);
        recyclerView.setAdapter(locationsAdapter);
    }

    private void findViewsById() {
        recyclerView = findViewById(R.id.recyclerView);
        submitBtn = findViewById(R.id.submit_btn);
        addFab = findViewById(R.id.add_fab);
        backArrow = findViewById(R.id.backArrow);

    }

    @Override
    public void onCloseClick(Locations locations) {
        locationsArrayList.remove(locations);
        locationsAdapter.setListModels(locationsArrayList);
        locationsAdapter.notifyDataSetChanged();
    }

    public void goToSearch(String mvalue) {
        Intent intent = new Intent(this, CustomGooglePlacesSearch.class);
        intent.putExtra("cursor", mvalue);
        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
    }

    @Override
    public void onSrcClick(Locations locations) {
        isPickup = true;
        this.locations = locations;
        goToSearch("source");
    }

    @Override
    public void onDestClick(Locations locations) {
        isPickup = false;
        this.locations = locations;
        goToSearch("destination");
    }

    @Override
    public void onGoodsClick(Locations locations) {
        this.locations = locations;
        refreshAdapter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode && data.getSerializableExtra("Location Address") != null) {
            PlacePredictions placePredictions = (PlacePredictions) data.getSerializableExtra("Location Address");
            Log.e(TAG, "onActivityResult: " + placePredictions.toString());
            if (placePredictions.getStrDestLatitude() != null && placePredictions.getStrDestLongitude() != null) {
                if (!placePredictions.getStrDestLongitude().equalsIgnoreCase(locations.getsLongitude()) &&
                        !placePredictions.getStrDestLatitude().equalsIgnoreCase(locations.getsLatitude())) {
                    showCurrentLocation(placePredictions);
                } else {
                    showCurrentLocation(placePredictions);
                }
            }
        }
    }

    private void showCurrentLocation(PlacePredictions placePredictions) {
        if (isPickup) {
            for (Locations loc : locationsArrayList) {
                loc.setsLatitude(placePredictions.getStrDestLatitude());
                loc.setsLongitude(placePredictions.getStrDestLongitude());
                loc.setsAddress(placePredictions.getStrDestAddress());
            }
            SharedHelper.putKey(context, "curr_lat", placePredictions.getStrDestLatitude() + "");
            SharedHelper.putKey(context, "curr_lng", placePredictions.getStrDestLongitude() + "");
            dAddress = placePredictions.getStrDestAddress();
            refreshAdapter();
        } else {
            locations.setdLatitude(placePredictions.getStrDestLatitude());
            locations.setdLongitude(placePredictions.getStrDestLongitude());
            locations.setdAddress(placePredictions.getStrDestAddress());
            refreshAdapter();
        }
    }
}