package com.wherego.user.Fragments;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
//import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.wherego.user.Activities.PickUpNotes;
import com.wherego.user.Adapter.ImageAdapter;
import com.wherego.user.MyCourier;
import com.wherego.user.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.wherego.user.Activities.CouponActivity;
import com.wherego.user.Activities.CustomGooglePlacesSearch;
import com.wherego.user.Activities.HistoryActivity;
import com.wherego.user.Activities.Payment;
import com.wherego.user.Activities.ShowProfile;
import com.wherego.user.Activities.UplaodImage;
import com.wherego.user.Helper.ConnectionHelper;
import com.wherego.user.Helper.CustomDialog;
import com.wherego.user.Helper.DataParser;
import com.wherego.user.Helper.SharedHelper;
import com.wherego.user.Helper.URLHelper;
import com.wherego.user.Models.CardInfo;
import com.wherego.user.Models.Driver;
import com.wherego.user.Models.PlacePredictions;
import com.wherego.user.Utils.MapAnimator;
import com.wherego.user.Utils.MapRipple;

import com.wherego.user.Utils.MyButton;
import com.wherego.user.Utils.MyTextView;
import com.wherego.user.Utils.ResponseListener;
import com.wherego.user.Utils.Utilities;
import com.wherego.user.chat.UserChatActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.wherego.user.MyCourier.trimMessage;

public class HomeFragment extends Fragment implements
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMarkerDragListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResponseListener,
        GoogleMap.OnCameraMoveListener, PaymentResultListener {

    private static final String TAG = "HomeFragment";
    String constructedURL;
    Activity activity;
    Context context;
    View rootView;
    HomeFragmentListener listener;
    double wallet_balance;
    String ETA;
    String service_type, service;
    String item_id = "";


    ArrayList<String> sourceAddress, sourcelat, sourceLng, destiAddress, destilat, destiLng;
    String ip_address = "";

    Boolean favourite = false;


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCameraMove() {
        utils.print("Current marker", "Zoom Level " + mMap.getCameraPosition().zoom);
        cmPosition = mMap.getCameraPosition();
        if (marker != null) {
            if (!mMap.getProjection().getVisibleRegion()
                    .latLngBounds.contains(marker.getPosition())) {
                utils.print("Current marker", "Current Marker is not visible");
                if (mapfocus.getVisibility() == View.INVISIBLE) {
                    mapfocus.setVisibility(View.VISIBLE);
                }
            } else {
                utils.print("Current marker", "Current Marker is visible");
                if (mapfocus.getVisibility() == View.VISIBLE) {
                    mapfocus.setVisibility(View.INVISIBLE);
                }
                if (mMap.getCameraPosition().zoom < 18.0f) {
                    if (mapfocus.getVisibility() == View.INVISIBLE) {
                        mapfocus.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    public interface HomeFragmentListener {
    }

    String isPaid = "", paymentMode = "";
    Utilities utils = new Utilities();
    int flowValue = 0;
    DrawerLayout drawer;
    int NAV_DRAWER = 0;
    String reqStatus = "";
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST = 18945;
    private final int ADD_CARD_CODE = 435;
    private static final int REQUEST_LOCATION = 1450;
    String feedBackRating;
    private ArrayList<CardInfo> cardInfoArrayList = new ArrayList<>();
    double height;
    double width;
    public String PreviousStatus = "";
    public String CurrentStatus = "";
    private Handler handleCheckStatus;
    String strPickLocation = "", strTag = "", strPickType = "";
    int size = 1;
    boolean once = true;
    int click = 1;
    boolean afterToday = false;
    boolean pick_first = true;
    Driver driver;

    //        <!-- Map frame -->
    LinearLayout mapLayout;
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    int value;
    Marker marker, marker1;
    Double latitude, longitude;
    String currentAddress;
    GoogleApiClient mGoogleApiClient;

    //        <!-- Source and Destination Layout-->
    LinearLayout sourceAndDestinationLayout;
    FrameLayout frmDestination;
    TextView destination, tvPaymentLabel;
    ImageView imgMenu, mapfocus, imgBack, shadowBack;
    View tripLine;
    LinearLayout errorLayout;
    ImageView destinationBorderImg;
    TextView frmSource, frmDest, txtSelectedAddressSource;
    LinearLayout srcDestLayout;

    TextView tvDistance, tvCash, tvETA, tvPrice;

//       <!--1. Request to providers -->

    LinearLayout lnrRequestProviders;
    RecyclerView rcvServiceTypes;
    ImageView imgPaymentType;
    ImageView imgSos;
    ImageView imgShareRide;
    TextView lblPaymentType, lblPaymentChange, booking_id;
    TextView btnRequestRides;
    String scheduledDate = "";
    String scheduledTime = "";
    String cancalReason = "";
    TextView txtPickUpNotes;

//        <!--1. Driver Details-->

    LinearLayout lnrHidePopup, lnrProviderPopup, lnrPriceBase,
            lnrPricemin, lnrPricekm;
    RelativeLayout lnrSearchAnimation;

    ImageView imgProviderPopup;
    TextView lblPriceMin, lblBasePricePopup, lblCapacity,
            lblServiceName, lblPriceKm, lblCalculationType,
            lblProviderDesc;
    MyButton btnDonePopup;

//         <!--2. Approximate Rate ...-->

    LinearLayout lnrApproximate;
    MyButton btnRequestRideConfirm;
    MyButton imgSchedule;
    CheckBox chkWallet;
    TextView lblEta, lblDis;
    TextView lblType;
    TextView lblApproxAmount, surgeDiscount, surgeTxt;
    View lineView;

    LinearLayout ScheduleLayout;
    TextView scheduleDate;
    TextView scheduleTime;
    MyButton scheduleBtn;
    DatePickerDialog datePickerDialog;

    LocationRequest mLocationRequest;

//         <!--3. Waiting For Providers ...-->

    RelativeLayout lnrWaitingForProviders;
    TextView lblNoMatch;
    ImageView imgCenter;
    MyButton btnCancelRide;
    private boolean mIsShowing;
    private boolean mIsHiding;
    RippleBackground rippleBackground;
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    String promocode_id, fixed, commision, discount, tax, surge, total;
//         <!--4. Driver Accepted ...-->

    LinearLayout lnrProviderAccepted, lnrAfterAcceptedStatus,
            AfterAcceptButtonLayout;
    ImageView imgProvider, imgServiceRequested;
    TextView lblProvider, lblETA,
            lblServiceRequested, lblModelNumber, lblSurgePrice;
    RatingBar ratingProvider;
    MaterialButton btnCancelTrip;
    ImageButton btnCall, btnchat, btnInfo;

//          <!--5. Invoice Layout ...-->

    LinearLayout lnrInvoice;
    TextView lblBasePrice, lblExtraPrice, lblDistancePrice,
            lblCommision, lblTaxPrice, lblTotalPrice,
            lblPaymentTypeInvoice,
            lblPaymentChangeInvoice;
    ImageView imgPaymentTypeInvoice;
    Button btnPayNow;

//          <!--6. Rate provider Layout ...-->

    LinearLayout lnrRateProvider;
    TextView lblProviderNameRate;
    //    ImageButton imgfav;
    ImageView imgProviderRate;
    RatingBar ratingProviderRate;
    EditText txtCommentsRate;
    Button btnSubmitReview;
    Float pro_rating;
    JSONObject itemObject;

//            <!-- Static marker-->

    RelativeLayout rtlStaticMarker;
    ImageView imgDestination;
    TextView btnDone;
    CameraPosition cmPosition;

    String current_lat = "",
            current_lng = "",
            current_address = "",
            source_lat = "",
            source_lng = "",
            source_address = "",
            dest_lat = "",
            dest_lng = "",
            dest_address = "";

    //Internet
    ConnectionHelper helper;
    Boolean isInternet;
    //RecylerView
    int currentPostion = 0;
    CustomDialog customDialog;
    //MArkers
    Marker availableProviders;
    private LatLng sourceLatLng;
    private LatLng destLatLng;
    private Marker sourceMarker;
    private Marker destinationMarker;
    private Marker providerMarker;
    ArrayList<LatLng> points = new ArrayList<LatLng>();
    ArrayList<Marker> lstProviderMarkers = new ArrayList<Marker>();
    AlertDialog alert;
    //Animation
    Animation slide_down, slide_up, slide_up_top, slide_up_down;

    ParserTask parserTask;
    String notificationTxt;
    boolean scheduleTrip = false;
    MapRipple mapRipple;
    ImageView schedule_ride;
    //  MY INITIALIZATION
    MyTextView lblCmfrmSourceAddress, lblCmfrmDestAddress;
    ImageView ImgConfrmCabType;
    TextView lblPromo;

    String current_chat_requestID = "";
    String currentProviderID = "";
    String userID = "";
    String providerFirstName = "";
    // MyTextView serviceItemPrice;
    LinearLayout layoutdriverstatus;

    LinearLayout driveraccepted;
    TextView txtdriveraccpted;
    LinearLayout driverArrived;
    TextView txtdriverArrived;
    ImageView imgarrived;
    LinearLayout driverPicked;
    TextView txtdriverpicked;
    ImageView imgPicked;
    LinearLayout driverCompleted;
    TextView txtdrivercompleted;
    ImageView imgDropped;

    private RecyclerView recyclerView;
    private ImageAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        handleCheckStatus = new Handler();
        if (bundle != null) {
            notificationTxt = bundle.getString("Notification");
            Log.e("HomeFragment", "onCreate: Notification" + notificationTxt);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
        }
//        checkIPaddress();
        customDialog = new CustomDialog(context);
        if (customDialog != null)
            customDialog.show();
        new Handler().postDelayed(() -> {
            init(rootView);
            //permission to access location
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                // Android M Permission check
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                initMap();
                MapsInitializer.initialize(getActivity());
            }
        }, 3000);

        reqStatus = SharedHelper.getKey(context, "req_status");
        if (reqStatus != null && !reqStatus.equalsIgnoreCase("null")
                && reqStatus.length() > 0) {
            if (reqStatus.equalsIgnoreCase("SEARCHING")) {
                Toast.makeText(context, "You have already requested to a order",
                        Toast.LENGTH_SHORT).show();
            }
        }
        Locale defaultLocale = Locale.getDefault();
        displayCurrencyInfoForLocale(defaultLocale);


        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            listener = (HomeFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement HomeFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void init(View rootView) {
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
        statusCheck();
//        <!-- Map frame -->
        schedule_ride = rootView.findViewById(R.id.schedule_ride);
        mapLayout = rootView.findViewById(R.id.mapLayout);
        drawer = rootView.findViewById(R.id.drawer_layout);
        drawer = activity.findViewById(R.id.drawer_layout);
//        <!-- Source and Destination Layout-->
        sourceAndDestinationLayout = rootView.findViewById(R.id.sourceAndDestinationLayout);
        srcDestLayout = rootView.findViewById(R.id.sourceDestLayout);
        frmSource = rootView.findViewById(R.id.frmSource);
        txtSelectedAddressSource = (TextView) rootView.findViewById(R.id.txtSelectedAddressSource);
        frmDest = rootView.findViewById(R.id.frmDest);
        frmDestination = rootView.findViewById(R.id.frmDestination);
        destination = rootView.findViewById(R.id.destination);
        tvPaymentLabel = rootView.findViewById(R.id.tvPaymentLabel);
        imgMenu = rootView.findViewById(R.id.imgMenu);
        imgSos = rootView.findViewById(R.id.imgSos);
        imgShareRide = rootView.findViewById(R.id.imgShareRide);
        mapfocus = rootView.findViewById(R.id.mapfocus);
        imgBack = rootView.findViewById(R.id.imgBack);
        shadowBack = rootView.findViewById(R.id.shadowBack);
        tripLine = (View) rootView.findViewById(R.id.trip_line);
        destinationBorderImg = rootView.findViewById(R.id.dest_border_img);
//        <!-- Request to providers-->
        lnrRequestProviders = rootView.findViewById(R.id.lnrRequestProviders);
//        cabService=rootView.findViewById(R.id.cabService);
//        packegeService=rootView.findViewById(R.id.packegeService);
        rcvServiceTypes = rootView.findViewById(R.id.rcvServiceTypes);
        imgPaymentType = rootView.findViewById(R.id.imgPaymentType);
        lblPaymentType = rootView.findViewById(R.id.lblPaymentType);
        lblPaymentChange = rootView.findViewById(R.id.lblPaymentType);
        booking_id = rootView.findViewById(R.id.booking_id);
        btnRequestRides = rootView.findViewById(R.id.btnRequestRides);
//        <!--  Driver and service type Details-->
        lnrSearchAnimation = rootView.findViewById(R.id.lnrSearch);
        lnrProviderPopup = rootView.findViewById(R.id.lnrProviderPopup);
        lnrPriceBase = rootView.findViewById(R.id.lnrPriceBase);
        lnrPricekm = rootView.findViewById(R.id.lnrPricekm);
        lnrPricemin = rootView.findViewById(R.id.lnrPricemin);
        lnrHidePopup = rootView.findViewById(R.id.lnrHidePopup);
        imgProviderPopup = rootView.findViewById(R.id.imgProviderPopup);

        lblServiceName = rootView.findViewById(R.id.lblServiceName);
        lblCapacity = rootView.findViewById(R.id.lblCapacity);
        lblPriceKm = rootView.findViewById(R.id.lblPriceKm);
        lblPriceMin = rootView.findViewById(R.id.lblPriceMin);
        lblCalculationType = rootView.findViewById(R.id.lblCalculationType);
        lblBasePricePopup = rootView.findViewById(R.id.lblBasePricePopup);
        lblProviderDesc = rootView.findViewById(R.id.lblProviderDesc);

        btnDonePopup = (MyButton) rootView.findViewById(R.id.btnDonePopup);
//         <!--2. Approximate Rate ...-->
        lnrApproximate = rootView.findViewById(R.id.lnrApproximate);
        imgSchedule = (MyButton) rootView.findViewById(R.id.imgSchedule);
        chkWallet = (CheckBox) rootView.findViewById(R.id.chkWallet);
        lblEta = rootView.findViewById(R.id.lblEta);
        lblDis = rootView.findViewById(R.id.lblDis);
        lblType = rootView.findViewById(R.id.lblType);
        lblApproxAmount = rootView.findViewById(R.id.lblApproxAmount);
        surgeDiscount = rootView.findViewById(R.id.surgeDiscount);
        surgeTxt = rootView.findViewById(R.id.surge_txt);
        btnRequestRideConfirm = (MyButton) rootView.findViewById(R.id.btnRequestRideConfirm);
        lineView = (View) rootView.findViewById(R.id.lineView);
        //Schedule Layout
        ScheduleLayout = rootView.findViewById(R.id.ScheduleLayout);
        scheduleDate = rootView.findViewById(R.id.scheduleDate);
        scheduleTime = rootView.findViewById(R.id.scheduleTime);
        scheduleBtn = (MyButton) rootView.findViewById(R.id.scheduleBtn);
//         <!--3. Waiting For Providers ...-->
        lnrWaitingForProviders = rootView.findViewById(R.id.lnrWaitingForProviders);
        lblNoMatch = rootView.findViewById(R.id.lblNoMatch);
        //imgCenter =  rootView.findViewById(R.id.imgCenter);
        btnCancelRide = rootView.findViewById(R.id.btnCancelRide);
        rippleBackground = (RippleBackground) rootView.findViewById(R.id.content);

//          <!--4. Driver Accepted ...-->

        lnrProviderAccepted = rootView.findViewById(R.id.lnrProviderAccepted);
        lnrAfterAcceptedStatus = rootView.findViewById(R.id.lnrAfterAcceptedStatus);
        AfterAcceptButtonLayout = rootView.findViewById(R.id.AfterAcceptButtonLayout);
        imgProvider = rootView.findViewById(R.id.imgProvider);
        imgServiceRequested = rootView.findViewById(R.id.imgServiceRequested);
        lblProvider = rootView.findViewById(R.id.lblProvider);

        lblETA = rootView.findViewById(R.id.lblETA);
        lblSurgePrice = rootView.findViewById(R.id.lblSurgePrice);
        lblServiceRequested = rootView.findViewById(R.id.lblServiceRequested);
        lblModelNumber = rootView.findViewById(R.id.lblModelNumber);
        ratingProvider = (RatingBar) rootView.findViewById(R.id.ratingProvider);
        btnCall = (ImageButton) rootView.findViewById(R.id.btnCall);
        btnInfo = rootView.findViewById(R.id.btnInfo);
        btnchat = rootView.findViewById(R.id.btnchat);
        btnCancelTrip = rootView.findViewById(R.id.btnCancelTrip);

        driveraccepted = rootView.findViewById(R.id.driveraccepted);
        txtdriveraccpted = rootView.findViewById(R.id.txtdriveraccpted);
        driverArrived = rootView.findViewById(R.id.driverArrived);
        txtdriverArrived = rootView.findViewById(R.id.txtdriverArrived);
        imgarrived = rootView.findViewById(R.id.imgarrived);
        driverPicked = rootView.findViewById(R.id.driverPicked);
        txtdriverpicked = rootView.findViewById(R.id.txtdriverpicked);

        imgPicked = rootView.findViewById(R.id.imgPicked);
        driverCompleted = rootView.findViewById(R.id.driverCompleted);
        txtdrivercompleted = rootView.findViewById(R.id.txtdrivercompleted);
        imgDropped = rootView.findViewById(R.id.imgDropped);
//           <!--5. Invoice Layout ...-->

        lnrInvoice = rootView.findViewById(R.id.lnrInvoice);
        lblBasePrice = rootView.findViewById(R.id.lblBasePrice);
        lblExtraPrice = rootView.findViewById(R.id.lblExtraPrice);
        lblDistancePrice = rootView.findViewById(R.id.lblDistancePrice);
        //lblCommision =  rootView.findViewById(R.id.lblCommision);
        lblTaxPrice = rootView.findViewById(R.id.lblTaxPrice);
        lblTotalPrice = rootView.findViewById(R.id.lblTotalPrice);
        lblPaymentTypeInvoice = rootView.findViewById(R.id.lblPaymentTypeInvoice);
        imgPaymentTypeInvoice = rootView.findViewById(R.id.imgPaymentTypeInvoice);
        lblPaymentChangeInvoice = rootView.findViewById(R.id.lblPaymentChangeInvoice);
        btnPayNow = rootView.findViewById(R.id.btnPayNow);

//          <!--6. Rate provider Layout ...-->

        lnrRateProvider = rootView.findViewById(R.id.lnrRateProvider);
        lblProviderNameRate = rootView.findViewById(R.id.lblProviderName);
//        imgfav=rootView.findViewById(R.id.imgfav);
//        lblProviderNameRate1 =  rootView.findViewById(R.id.lblProviderName1);
        imgProviderRate = rootView.findViewById(R.id.imgProviderRate);
        txtCommentsRate = rootView.findViewById(R.id.txtComments);
        ratingProviderRate = rootView.findViewById(R.id.ratingProviderRate);
        btnSubmitReview = rootView.findViewById(R.id.btnSubmitReview);

//            <!--Static marker-->

        rtlStaticMarker = rootView.findViewById(R.id.rtlStaticMarker);
        imgDestination = rootView.findViewById(R.id.imgDestination);
        btnDone = rootView.findViewById(R.id.btnDone);

        /* MY INITIALIZATION*/

        lblCmfrmSourceAddress = rootView.findViewById(R.id.lblCmfrmSourceAddress);
        lblCmfrmDestAddress = rootView.findViewById(R.id.lblCmfrmDestAddress);
        ;
        ImgConfrmCabType = rootView.findViewById(R.id.ImgConfrmCabType);
        layoutdriverstatus = rootView.findViewById(R.id.layoutdriverstatus);
        lblPromo = rootView.findViewById(R.id.lblPromo);
        // serviceItemPrice = (MyTextView) rootView.findViewById(R.id.serviceItemPrice);

        /// new card after ride accept
        tvDistance = rootView.findViewById(R.id.tvDistance);
        tvCash = rootView.findViewById(R.id.tvCash);
        tvETA = rootView.findViewById(R.id.tvETA);
        tvPrice = rootView.findViewById(R.id.tvPrice);
        txtPickUpNotes = rootView.findViewById(R.id.txtPickUpNotes);
        getCards();

        checkStatus();

        //check status every 3 sec

        schedule_ride.setOnClickListener(new OnClick());
        btnRequestRides.setOnClickListener(new OnClick());
        btnDonePopup.setOnClickListener(new OnClick());
        lnrHidePopup.setOnClickListener(new OnClick());
        btnRequestRideConfirm.setOnClickListener(new OnClick());
        btnCancelRide.setOnClickListener(new OnClick());
        btnCancelTrip.setOnClickListener(new OnClick());
        btnCall.setOnClickListener(new OnClick());
        btnchat.setOnClickListener(new OnClick());
        btnInfo.setOnClickListener(new OnClick());
        btnPayNow.setOnClickListener(new OnClick());
        btnSubmitReview.setOnClickListener(new OnClick());
//        imgfav.setOnClickListener(new OnClick());
        btnDone.setOnClickListener(new OnClick());
        frmDestination.setOnClickListener(new OnClick());
        frmDest.setOnClickListener(new OnClick());
        lblPaymentChange.setOnClickListener(new OnClick());
        imgPaymentType.setOnClickListener(new OnClick());
        frmSource.setOnClickListener(new OnClick());
        imgMenu.setOnClickListener(new OnClick());
        mapfocus.setOnClickListener(new OnClick());
        imgSchedule.setOnClickListener(new OnClick());
        imgBack.setOnClickListener(new OnClick());
        scheduleBtn.setOnClickListener(new OnClick());
        scheduleDate.setOnClickListener(new OnClick());
        scheduleTime.setOnClickListener(new OnClick());
        imgProvider.setOnClickListener(new OnClick());
        imgProviderRate.setOnClickListener(new OnClick());
        imgSos.setOnClickListener(new OnClick());
        imgShareRide.setOnClickListener(new OnClick());

        lnrRequestProviders.setOnClickListener(new OnClick());
        lnrProviderPopup.setOnClickListener(new OnClick());
        ScheduleLayout.setOnClickListener(new OnClick());
        lnrApproximate.setOnClickListener(new OnClick());
        lnrProviderAccepted.setOnClickListener(new OnClick());
        lnrInvoice.setOnClickListener(new OnClick());
        lnrRateProvider.setOnClickListener(new OnClick());
        lnrWaitingForProviders.setOnClickListener(new OnClick());
        lblPaymentChangeInvoice.setOnClickListener(new OnClick());
        txtPickUpNotes.setOnClickListener(new OnClick());
        flowValue = 0;
        layoutChanges();

        //Load animation
        slide_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slide_up_top = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_top);
        slide_up_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_down);


        /***********my initialization*/
        lblPromo.setOnClickListener(new OnClick());

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return true;

                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    if (!reqStatus.equalsIgnoreCase("SEARCHING")) {
                        utils.print("", "Back key pressed!");
                        if (lnrRequestProviders.getVisibility() == View.VISIBLE) {

                            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {


                                if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                                    LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                    flowValue = 0;
                                }
                            } else {


                                exitConfirmation();


                            }
                        } else if (lnrApproximate.getVisibility() == View.VISIBLE) {

                            flowValue = 1;
                        } else if (lnrWaitingForProviders.getVisibility() == View.VISIBLE) {
                            lnrSearchAnimation.setVisibility(View.GONE);
                            flowValue = 1;
                        } else if (ScheduleLayout.getVisibility() == View.VISIBLE) {
                            flowValue = 1;
                        } else {
                            exitConfirmation();
                        }
                        layoutChanges();
                        return true;
                    }
//                    if (!reqStatus.equalsIgnoreCase("SEARCHING")) {
//                        utils.print("", "Back key pressed!");
//                        if (lnrRequestProviders.getVisibility() == View.VISIBLE) {
//                            flowValue = 0;
//                            if (!current_lat.equalsIgnoreCase("") &&
//                                    !current_lng.equalsIgnoreCase("")) {
//                                LatLng myLocation = new LatLng(Double.parseDouble(current_lat),
//                                        Double.parseDouble(current_lng));
//                                CameraPosition cameraPosition = new
//                                        CameraPosition.Builder()
//                                        .target(myLocation)
//                                        .zoom(18).build();
//                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                                mMap.setPadding(0, 0, 0, 0);
//                            }
//                        } else if (lnrApproximate.getVisibility() == View.VISIBLE) {
//                            flowValue = 1;
//                        } else if (lnrWaitingForProviders.getVisibility() == View.VISIBLE) {
//                            flowValue = 1;
//                        } else if (ScheduleLayout.getVisibility() == View.VISIBLE) {
//                            flowValue = 1;
//                        } else {
//                            getActivity().finish();
//                        }
//                        layoutChanges();
//                        return true;
//                    }
                }
                return false;
            }
        });

    }

    private void exitConfirmation() {
        new AlertDialog.Builder(getContext())
                .setTitle(getActivity().getString(R.string.confirmation))
                .setMessage(getActivity().getString(R.string.do_you_want_exit))
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        getActivity().finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    @SuppressWarnings("MissingPermission")
    void initMap() {
        if (mMap == null) {
            FragmentManager fm = getChildFragmentManager();
            mapFragment = ((SupportMapFragment) fm.findFragmentById(R.id.provider_map));
            mapFragment.getMapAsync(this);
        }
        if (mMap != null) {
            setupMap();
        }
        new Handler().postDelayed(() -> getServiceList(), 8000);
        checkStatus();

    }


    @SuppressWarnings("MissingPermission")
    void setupMap() {
        if (mMap != null) {

            mMap.getUiSettings().setCompassEnabled(false);
            mMap.setBuildingsEnabled(true);
            mMap.setMyLocationEnabled(false);
            mMap.setOnMarkerDragListener(this);
            mMap.setOnCameraMoveListener(this);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            mMap.getUiSettings().setTiltGesturesEnabled(false);
        }


    }


    @Override
    public void onLocationChanged(Location location) {

        if (marker != null) {
            marker.remove();
        }
        if (location != null && location.getLatitude() != 0 && location.getLongitude() != 0) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .anchor(0.5f, 0.75f)
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_currentloaction));
            marker = mMap.addMarker(markerOptions);

            Log.e("MAP", "onLocationChanged: 1 " + location.getLatitude());
            Log.e("MAP", "onLocationChanged: 2 " + location.getLongitude());
            current_lat = "" + location.getLatitude();
            current_lng = "" + location.getLongitude();

            if (source_lat.equalsIgnoreCase("") || source_lat.length() < 0) {
                source_lat = current_lat;
            }
            if (source_lng.equalsIgnoreCase("") || source_lng.length() < 0) {
                source_lng = current_lng;
            }

            if (value == 0) {
                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.setPadding(0, 0, 0, 0);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.getUiSettings().setCompassEnabled(false);

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                currentAddress = utils.getCompleteAddressString(context, latitude, longitude);
                source_lat = "" + latitude;
                source_lng = "" + longitude;
                source_address = currentAddress;
                current_address = currentAddress;
                frmSource.setText(currentAddress);
                getProvidersList("");
                value++;
                if (activity != null && isAdded()) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                }
            }
        }
    }

    public void startApproximateSlide() {
        scheduledDate = "";
        scheduledTime = "";
        if (!frmSource.getText().toString().equalsIgnoreCase("") &&
                !destination.getText().toString().equalsIgnoreCase("") &&
                !frmDest.getText().toString().equalsIgnoreCase("")) {
            getApproximateFare();
            frmDest.setOnClickListener(null);
            frmSource.setOnClickListener(null);
            srcDestLayout.setOnClickListener(new HomeFragment.OnClick());
        } else {
            Toast.makeText(context, "Please enter both pickup and drop locations", Toast.LENGTH_SHORT).show();
        }
    }


    public static void displayCurrencyInfoForLocale(Locale locale) {
        System.out.println("Locale: " + locale.getDisplayName());
        Currency currency = Currency.getInstance(locale);
        System.out.println("Currency Code: " + currency.getCurrencyCode());
        System.out.println("Symbol: " + currency.getSymbol());
        System.out.println("Default Fraction Digits: " + currency.getDefaultFractionDigits());
        System.out.println();
    }

    boolean isRequestProviderScreen;
    private boolean isRideRequested;

    class OnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lblPaymentChangeInvoice:

                    addPayment();

                case R.id.imgPaymentType:
                    showChooser();
                    break;
                case R.id.txtPickUpNotes:

                    Intent intentPickuP = new Intent(getActivity(), PickUpNotes.class);
                    intentPickuP.putExtra("request_id", current_chat_requestID + "");
                    startActivity(intentPickuP);
                    break;
                case R.id.frmSource:
                    Intent intent = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                    intent.putExtra("cursor", "source");
                    intent.putExtra("s_address", frmSource.getText().toString());
                    intent.putExtra("d_address", destination.getText().toString());
                    intent.putExtra("d_address", frmDest.getText().toString());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                    break;
                case R.id.frmDestination:
                    Intent intent2 = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                    intent2.putExtra("cursor", "destination");
                    intent2.putExtra("s_address", frmSource.getText().toString());
                    intent2.putExtra("d_address", destination.getText().toString());
                    intent2.putExtra("d_address", frmDest.getText().toString());
                    startActivityForResult(intent2, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                    break;
                case R.id.frmDest:
                    Intent intent3 = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                    intent3.putExtra("cursor", "destination");
                    intent3.putExtra("s_address", frmSource.getText().toString());
                    intent3.putExtra("d_address", destination.getText().toString());
                    intent3.putExtra("d_address", frmDest.getText().toString());
                    startActivityForResult(intent3, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                    break;
                case R.id.lblPaymentType:
                    showChooser();
                    break;
                case R.id.btnRequestRides:
                    isRideRequested = true;
                    Log.e("service", service + "service");
                    SharedHelper.putKey(getContext(), "Schedule", "false");

                    if (!frmSource.getText().toString().equalsIgnoreCase("") &&
                            !destination.getText().toString().equalsIgnoreCase("") &&
                            !frmDest.getText().toString().equalsIgnoreCase("")) {
                        Intent intent6 = new Intent(getActivity(), UplaodImage.class);
                        intent6.putExtra("service_type", service_type);
                        intent6.putExtra("CourierType", "Item ");
                        startActivityForResult(intent6, 12);

                    } else {
                        Toast.makeText(context, "Please enter both pickup and drop locations", Toast.LENGTH_SHORT).show();
                    }
                    break;


                case R.id.schedule_ride:
                    flowValue = 7;
                    layoutChanges();
                    break;
                case R.id.btnRequestRideConfirm:
                    SharedHelper.putKey(context, "name", "");
//                    scheduledDate = "";
//                    scheduledTime = "";
//                    sendRequest();


                    if (SharedHelper.getKey(getContext(), "Schedule").equalsIgnoreCase("true")) {
                        Date date = null;

                        try {
                            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(SharedHelper.getKey(context, "scheduledDate"));
                        } catch (ParseException e) {
                            Log.d("dateParseError", e.toString());
                            e.printStackTrace();
                        }
                        long milliseconds = date.getTime();
                        scheduledTime = SharedHelper.getKey(context, "scheduledTime");
                        scheduledDate = SharedHelper.getKey(context, "scheduledDate");
                        if (!DateUtils.isToday(milliseconds)) {
                            sendRequest();
                        } else {

                            if (utils.checktimings(SharedHelper.getKey(context, "scheduledTime"))) {

                                sendRequest();
                            } else {
                                Toast.makeText(activity, getString(R.string.different_time), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        scheduledTime = "";
                        scheduledDate = "";
                        sendRequest();

                    }
                    break;
                case R.id.btnPayNow:


                    if (lblPaymentTypeInvoice.getText().toString().equalsIgnoreCase("CARD")) {
                        payNow();

                    } else if (lblPaymentTypeInvoice.getText().toString().equalsIgnoreCase("PAYPAL")) {
                        Log.d(TAG, "btnPayNowClick: " + lblTotalPrice.getText().toString());
                        //   confirmFinalPayment(lblTotalPrice.getText().toString());
//       payNowCard();
                        PayPalConfiguration config = new PayPalConfiguration()
                                // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
                                // or live (ENVIRONMENT_PRODUCTION)
                                .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
                                .clientId("AfkUnyokJW7R1C5ylbjsrST_bw8-qkO8yQSb_bUXtWS6KFrTvPs3IOB4XX7DTJlBiY1InG2q6gz5bmle\n" +
                                        "PAYPAL_SECRET=EAchM9cqDqo7iCiLZunNnMW2bgAFvAgAVaUdv_hGgoC9ShkIW07br0s8gf9hHjlFnvT-x3DSS7cfX56H\n" +
                                        "PAYPAL_MODE=sandbox");

                        PayPalPayment payment = new PayPalPayment(new BigDecimal(lblTotalPrice.getText().toString().replace("$", "")), "USD", " ",
                                PayPalPayment.PAYMENT_INTENT_SALE);
                        Intent intentPay = new Intent(getActivity(), PaymentActivity.class);
                        // send the same configuration for restart resiliency
                        intentPay.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                        intentPay.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                        startActivityForResult(intentPay, 0);

                    } else {
                        Checkout.preload(getActivity());
                        startPayment();
                    }
                    break;

                case R.id.lblPromo:
                    startActivity(new Intent(getActivity(), CouponActivity.class));
                    break;
                case R.id.btnSubmitReview:
                    submitReviewCall();
                    break;
//                case R.id.imgfav:
//                    if(favourite==false)
//                    {
//                        favourite=true;
////                        imgfav.setBackgroundResource(R.drawable.ic_favourite_red);
//                    }
//                    else {
//                        favourite=false;
////                        imgfav.setBackgroundResource(R.drawable.ic_favourite);
//                    }
//                    break;
                case R.id.lnrHidePopup:
                case R.id.btnDonePopup:
                    lnrHidePopup.setVisibility(View.GONE);
                    flowValue = 1;
                    layoutChanges();
                    click = 1;
                    break;
                case R.id.btnCancelRide:
                    showCancelRideDialog();
                    break;
                case R.id.btnCancelTrip:
                    if (btnCancelTrip.getText().toString().equals(getString(R.string.cancel_trip)))
                        showCancelRideDialog();
                    else {
                        String shareUrl = URLHelper.REDIRECT_SHARE_URL;
                        navigateToShareScreen(shareUrl);
                    }
                    break;
                case R.id.imgSos:
                    showSosPopUp();
                    break;
                case R.id.imgShareRide:
                    String url = "http://maps.google.com/maps?q=loc:";
                    navigateToShareScreen(url);
                    break;
                case R.id.imgProvider:
                    Intent intent1 = new Intent(activity, ShowProfile.class);
                    intent1.putExtra("driver", driver);
                    startActivity(intent1);
                    break;
                case R.id.imgProviderRate:
                    Intent intent4s = new Intent(activity, ShowProfile.class);
                    intent4s.putExtra("driver", driver);
                    startActivity(intent4s);
                    break;
                case R.id.btnCall:
                    Intent intentCall = new Intent(Intent.ACTION_DIAL);
                    intentCall.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
                    startActivity(intentCall);
                    break;

                case R.id.btnchat:
                    Intent intentChat = new Intent(context, UserChatActivity.class);
                    intentChat.putExtra("requestId", current_chat_requestID);
                    intentChat.putExtra("providerId", currentProviderID);
                    intentChat.putExtra("userId", userID);
                    intentChat.putExtra("userName", providerFirstName);
                    context.startActivity(intentChat);
//                    Toast.makeText(getActivity(),"Chat coming Soon",Toast.LENGTH_LONG).show();
                    break;

                case R.id.btnInfo:
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
                    bottomSheetDialog.setContentView(R.layout.courier_info);
                    bottomSheetDialog.show();
                    ArrayList<Image> images = new ArrayList<>();
                    ImageView imgClose = bottomSheetDialog.findViewById(R.id.imgClose);
                    TextView tvParcelname = bottomSheetDialog.findViewById(R.id.tvParcelname);
                    TextView tvParcelQty = bottomSheetDialog.findViewById(R.id.tvParcelQty);
                    TextView tvParcelWeight = bottomSheetDialog.findViewById(R.id.tvParcelWeight);
                    TextView tvReceiverName = bottomSheetDialog.findViewById(R.id.tvReceiverName);
                    TextView tvReceiverEmail = bottomSheetDialog.findViewById(R.id.tvReceiverEmail);
                    TextView tvreceiverMobile = bottomSheetDialog.findViewById(R.id.tvreceiverMobile);
                    TextView tvReceiverAddress = bottomSheetDialog.findViewById(R.id.tvReceiverAddress);
                    ImageView imgCourier = bottomSheetDialog.findViewById(R.id.imgCourier);
                    TextView tvTitle = bottomSheetDialog.findViewById(R.id.tvTitle);
                    TextView tvname = bottomSheetDialog.findViewById(R.id.tvname);
                    TextView tvWeight = bottomSheetDialog.findViewById(R.id.tvWeight);
                    TextView tvQuantity = bottomSheetDialog.findViewById(R.id.tvQuantity);
                    recyclerView = bottomSheetDialog.findViewById(R.id.recyclerView);
                    adapter = new ImageAdapter(getActivity());
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                    if (itemObject != null) {

                        tvTitle.setText(itemObject.optString("document_type") + "  Info");
                        tvname.setText(itemObject.optString("document_type") + "  Name");
                        tvWeight.setText(itemObject.optString("document_type") + "  Weight");
                        tvQuantity.setText(itemObject.optString("document_type") + "  Quantity");
                        tvParcelname.setText(itemObject.optString("name"));
                        tvParcelQty.setText(itemObject.optString("qty") + " Pcs");
                        tvParcelWeight.setText(itemObject.optString("weight") + " Gm");
                        tvReceiverName.setText(itemObject.optString("rec_name"));
                        tvReceiverEmail.setText(itemObject.optString("rec_email"));
                        tvreceiverMobile.setText(itemObject.optString("rec_mobile"));
                        tvReceiverAddress.setText(itemObject.optString("rec_address"));
                        if (itemObject.optJSONArray("item_image") != null) {
                            for (int k = 0; k < itemObject.optJSONArray("item_image").length(); k++) {
                                String imagePath = URLHelper.base + itemObject.optJSONArray("item_image")
                                        .optJSONObject(k).optString("image_path");
                                Image image = new Image(k, "a", imagePath);
                                images.add(image);
                            }
                            adapter.setData(images);


                        }

                        tvreceiverMobile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tvreceiverMobile.getText().toString()));
                                startActivity(intent);
                            }
                        });

                    }
                    imgClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetDialog.dismiss();
                        }
                    });


//                   Toast.makeText(getActivity(),"Info coming Soon",Toast.LENGTH_LONG).show();
                    break;
                case R.id.btnDone:
                    pick_first = true;
                    try {
                        utils.print("centerLat", cmPosition.target.latitude + "");
                        utils.print("centerLong", cmPosition.target.longitude + "");

                        Geocoder geocoder = null;
                        List<Address> addresses;
                        geocoder = new Geocoder(getActivity(), Locale.getDefault());

                        String city = "", state = "", address = "";

                        try {
                            addresses = geocoder.getFromLocation(cmPosition.target.latitude, cmPosition.target.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            address = addresses.get(0).getAddressLine(0);
                            city = addresses.get(0).getLocality();
                            state = addresses.get(0).getAdminArea();
                            ; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (strPickType.equalsIgnoreCase("source")) {
                            source_address = "" + address + "," + city + "," + state;
                            source_lat = "" + cmPosition.target.latitude;
                            source_lng = "" + cmPosition.target.longitude;
                            if (dest_lat.equalsIgnoreCase("")) {
                                Toast.makeText(context, "Select destination", Toast.LENGTH_SHORT).show();
                                Intent intentDest = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                                intentDest.putExtra("cursor", "destination");
                                intentDest.putExtra("s_address", source_address);
                                startActivityForResult(intentDest, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                            } else {
                                source_lat = "" + cmPosition.target.latitude;
                                source_lng = "" + cmPosition.target.longitude;

                                mMap.clear();
//                            setValuesForSourceAndDestination();
                                flowValue = 1;
                                layoutChanges();
                                strPickLocation = "";
                                strPickType = "";
                                getServiceList();

                                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(cmPosition.target.latitude,
                                        cmPosition.target.longitude));
                                CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                                mMap.moveCamera(center);
                                mMap.moveCamera(zoom);

                            }
                        } else {
                            dest_lat = "" + cmPosition.target.latitude;
                            if (dest_lat.equalsIgnoreCase(source_lat)) {
                                Toast.makeText(context, "Both source and destination are same", Toast.LENGTH_SHORT).show();
                                Intent intentDest = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                                intentDest.putExtra("cursor", "destination");
                                intentDest.putExtra("s_address", frmSource.getText().toString());
                                startActivityForResult(intentDest, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                            } else {
                                dest_address = "" + address + "," + city + "," + state;
                                dest_lat = "" + cmPosition.target.latitude;
                                dest_lng = "" + cmPosition.target.longitude;

                                mMap.clear();
//                            setValuesForSourceAndDestination();
                                flowValue = 1;
                                layoutChanges();
                                strPickLocation = "";
                                strPickType = "";
                                getServiceList();

                                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(cmPosition.target.latitude,
                                        cmPosition.target.longitude));
                                CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                                mMap.moveCamera(center);
                                mMap.moveCamera(zoom);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Can't able to get the address!.Please try again", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.imgBack:
                    if (lnrRequestProviders.getVisibility() == View.VISIBLE) {
                        flowValue = 0;
                        isRequestProviderScreen = false;

                        getProvidersList("");
                        frmSource.setOnClickListener(new OnClick());
                        frmDest.setOnClickListener(new OnClick());
                        srcDestLayout.setOnClickListener(null);
                        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                            destinationBorderImg.setVisibility(View.VISIBLE);
                            //verticalView.setVisibility(View.GONE);
                            LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            getProvidersList("");

                            mMap.setPadding(0, 0, 0, 0);
                        }
                    } else if (lnrApproximate.getVisibility() == View.VISIBLE) {
                        isRequestProviderScreen = true;
                        frmSource.setOnClickListener(new OnClick());
                        frmDest.setOnClickListener(new OnClick());

                        flowValue = 1;
                    } else if (lnrWaitingForProviders.getVisibility() == View.VISIBLE) {
                        isRequestProviderScreen = false;
                        flowValue = 1;
                    } else if (ScheduleLayout.getVisibility() == View.VISIBLE) {
                        isRequestProviderScreen = false;
                        flowValue = 1;
                    }
                    layoutChanges();
                    break;
                case R.id.imgMenu:
                    if (NAV_DRAWER == 0) {
                        if (drawer != null)
                            drawer.openDrawer(Gravity.LEFT);
                    } else {
                        NAV_DRAWER = 0;
                        if (drawer != null)
                            drawer.closeDrawers();
                    }
                    break;
                case R.id.mapfocus:
                    Double crtLat, crtLng;
                    if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                        crtLat = Double.parseDouble(current_lat);
                        crtLng = Double.parseDouble(current_lng);

                        if (crtLat != null && crtLng != null) {
                            LatLng loc = new LatLng(crtLat, crtLng);
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(16).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            mapfocus.setVisibility(View.INVISIBLE);
                        }
                    }
                    break;
                case R.id.imgSchedule:
                    flowValue = 1;
                    layoutChanges();
                    break;
                case R.id.scheduleBtn:
                    if (service.contains("PARCEL")) {
                        SharedHelper.putKey(context, "name", "");
                        if (scheduledDate != "" && scheduledTime != "") {
                            Date date = null;
                            try {
                                date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(scheduledDate);
                            } catch (ParseException e) {
                                Log.d("dateParseError", e.toString());
                                e.printStackTrace();
                            }
                            long milliseconds = date.getTime();
                            SharedHelper.putKey(context, "Schedule", "true");
                            SharedHelper.putKey(context, "scheduledDate", scheduledDate);
                            SharedHelper.putKey(context, "scheduledTime", scheduledTime);
                            if (!DateUtils.isToday(milliseconds)) {

                                Intent intent6 = new Intent(getActivity(), UplaodImage.class);
                                intent6.putExtra("service_type", service_type);
                                startActivityForResult(intent6, 12);
                            } else {
                                if (utils.checktimings(scheduledTime)) {
                                    sendRequest();
                                } else {
                                    Toast.makeText(activity, getString(R.string.different_time),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(activity, getString(R.string.choose_date_time), Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        SharedHelper.putKey(context, "name", "");
                        if (scheduledDate != "" && scheduledTime != "") {
                            Date date = null;
                            try {
                                date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(scheduledDate);
                            } catch (ParseException e) {
                                Log.d("dateParseError", e.toString());
                                e.printStackTrace();
                            }
                            long milliseconds = date.getTime();
                            SharedHelper.putKey(context, "Schedule", "true");
                            SharedHelper.putKey(context, "scheduledDate", scheduledDate);
                            SharedHelper.putKey(context, "scheduledTime", scheduledTime);
                            if (!DateUtils.isToday(milliseconds)) {
                                sendRequest();
                            } else {
                                if (utils.checktimings(scheduledTime)) {
                                    sendRequest();
                                } else {
                                    Toast.makeText(activity, getString(R.string.different_time),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(activity, getString(R.string.choose_date_time), Toast.LENGTH_SHORT).show();
                        }
                    }


                    break;
                case R.id.scheduleDate:
                    // calender class's instance and get current date , month and year from calender
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR); // current year
                    int mMonth = c.get(Calendar.MONTH); // current month
                    int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                    // date picker dialog
                    datePickerDialog = new DatePickerDialog(activity,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    // set day of month , month and year value in the edit text
                                    String choosedMonth = "";
                                    String choosedDate = "";
                                    String choosedDateFormat = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                    scheduledDate = choosedDateFormat;
                                    try {
                                        choosedMonth = utils.getMonth(choosedDateFormat);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (dayOfMonth < 10) {
                                        choosedDate = "0" + dayOfMonth;
                                    } else {
                                        choosedDate = "" + dayOfMonth;
                                    }
                                    afterToday = utils.isAfterToday(year, monthOfYear, dayOfMonth);
                                    scheduleDate.setText(choosedDate + " " + choosedMonth + " " + year);
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.getDatePicker().setMaxDate((System.currentTimeMillis() - 1000) + (1000 * 60 * 60 * 24 * 7));
                    datePickerDialog.show();
                    break;
                case R.id.scheduleTime:
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                        int callCount = 0;   //To track number of calls to onTimeSet()

                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                            if (callCount == 0) {
                                String choosedHour = "";
                                String choosedMinute = "";
                                String choosedTimeZone = "";
                                String choosedTime = "";

                                scheduledTime = selectedHour + ":" + selectedMinute;

                                if (selectedHour > 12) {
                                    choosedTimeZone = "PM";
                                    selectedHour = selectedHour - 12;
                                    if (selectedHour < 10) {
                                        choosedHour = "0" + selectedHour;
                                    } else {
                                        choosedHour = "" + selectedHour;
                                    }
                                } else {
                                    choosedTimeZone = "AM";
                                    if (selectedHour < 10) {
                                        choosedHour = "0" + selectedHour;
                                    } else {
                                        choosedHour = "" + selectedHour;
                                    }
                                }

                                if (selectedMinute < 10) {
                                    choosedMinute = "0" + selectedMinute;
                                } else {
                                    choosedMinute = "" + selectedMinute;
                                }
                                choosedTime = choosedHour + ":" + choosedMinute + " " + choosedTimeZone;

                                if (scheduledDate != "" && scheduledTime != "") {
                                    Date date = null;
                                    try {
                                        date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(scheduledDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        Log.d("dateParseError1", e.toString());
                                    }
                                    long milliseconds = date.getTime();
                                    if (!DateUtils.isToday(milliseconds)) {
                                        scheduleTime.setText(choosedTime);
                                    } else {
                                        if (utils.checktimings(scheduledTime)) {
                                            scheduleTime.setText(choosedTime);
                                        } else {
                                            Toast toast = new Toast(activity);
                                            toast.makeText(activity, getString(R.string.different_time), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(activity, getString(R.string.choose_date_time), Toast.LENGTH_SHORT).show();
                                }
                            }
                            callCount++;
                        }
                    }, hour, minute, false);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                    break;
            }
        }
    }


    public void startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */


        final Checkout co = new Checkout();

        try {
            String amount = lblTotalPrice.getText().toString().replace("$", "").trim();
            Log.v("totalamount", amount + " ");
            float finalAmount = (Float.parseFloat(amount)) * 100;
            Log.v("finalAmount", finalAmount + " ");
            JSONObject options = new JSONObject();
            options.put("name", "Razorpay Corp");
            options.put("description", "WhereGo Charges");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", finalAmount + "");

            JSONObject preFill = new JSONObject();
            preFill.put("email", SharedHelper.getKey(getActivity(), "email"));
            preFill.put("contact", SharedHelper.getKey(getActivity(), "mobile"));

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            e.printStackTrace();

            Log.v("razorpayError", e.getLocalizedMessage() + "  ");
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void callpay(String razorpayPaymentID) {
        paymentType = "RAZORPAY";
        paymentId = razorpayPaymentID;
        payNow();
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            paymentType = "RAZORPAY";
            paymentId = razorpayPaymentID;
            payNow();
            Toast.makeText(getActivity(), "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.v(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(getActivity(), "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.v(TAG, "Exception in onPaymentError", e);
        }
    }

//    @SuppressWarnings("unused")
//    @Override
//    public void onPaymentSuccess(String razorpayPaymentID) {

//    }
//
//
//    @SuppressWarnings("unused")
//    @Override
//    public void onPaymentError(int code, String response) {
//        try {
//            Toast.makeText(getActivity(), "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            Log.v(TAG, "Exception in onPaymentError", e);
//        }
//    }

    private void addPayment() {


        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject objects = new JSONObject();
        JSONObject obj = new JSONObject();
        try {
            objects.put("request_id", SharedHelper.getKey(context, "request_id"));
            objects.put("promocode_id", promocode_id);
            objects.put("fixed", fixed);
            objects.put("commision", commision);
            objects.put("discount", discount);
            objects.put("tax", tax);
            objects.put("surge", surge);
            objects.put("total", total);

            Log.e("objectsaddPayment", "sendRequest: " + objects);


            Log.e("new object", obj.toString() + "objects");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("parameter", objects + "object");

        MyCourier.getInstance().cancelRequestInQueue("send_request");
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.ADD_PAYMENT,
                        objects,
                        response -> {
                            Log.e("response", response + "response");
                            if (response != null) {
                                utils.print("addPaymentResponse", response.toString());
                                try {
                                    if (response.getString("message").contains("add")) {
                                        Intent intentPay = new Intent(getActivity(), Payment.class);
                                        intentPay.putExtra("total", total);
                                        startActivity(intentPay);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, error -> {
                    Log.e("error", error.toString() + "error");
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));
                            Log.e("SendREquest", "onErrorResponse: " +
                                    errorObj.optString("message"));
                            if (response.statusCode == 400 ||
                                    response.statusCode == 405 ||
                                    response.statusCode == 500) {
                                try {
//                                    utils.showAlert(context, errorObj.optString("error"));
                                } catch (Exception e) {
                                    utils.showAlert(context,
                                            context.getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                refreshAccessToken("SEND_REQUEST");
                            } else if (response.statusCode == 422) {
                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
//                                    utils.showAlert(context, json);
                                } else {
                                    utils.showAlert(context,
                                            context.getString(R.string.please_try_again));
                                }
                            } else if (response.statusCode == 503) {
                                utils.showAlert(context,
                                        context.getString(R.string.server_down));
                            } else {
                                utils.showAlert(context,
                                        context.getString(R.string.please_try_again));
                            }
                        } catch (Exception e) {
                            utils.showAlert(context,
                                    context.getString(R.string.something_went_wrong));
                        }
                    } else {
                        utils.showAlert(context,
                                context.getString(R.string.please_try_again));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" +
                                SharedHelper.getKey(context, "token_type") + " " +
                                SharedHelper.getKey(context, "access_token"));
                        Log.e("headers", headers + "headers");
                        return headers;
                    }
                };

        MyCourier.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void navigateToShareScreen(String shareUrl) {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String name = SharedHelper.getKey(context, "first_name") + " " + SharedHelper.getKey(context, "last_name");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "WhereGo -" + "Mr/Mrs." + name + " would like to share a trip with you at " +
                    shareUrl + current_lat + "," + current_lng);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Share applications not found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSosPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(getString(R.string.emaergeny_call))
                .setCancelable(false);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 3);
                } else {
                    Intent intentCall = new Intent(Intent.ACTION_CALL);
                    intentCall.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "sos")));
                    startActivity(intentCall);
                }
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void sosRequest() {

    }

    private void showCancelRideDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(getString(R.string.cancel_ride_alert));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showreasonDialog();
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


//    public void setNewValuesForApproximateLayout() {
//        if (isInternet) {
//            String surge = SharedHelper.getKey(context, "surge");
//            System.out.println("SURGE SURGE SURGE 123456 " + surge);
//            //lblApproxAmount.setText(SharedHelper.getKey(context, "currency") + "" + SharedHelper.getKey(context, "estimated_fare"));
//            //lblEta.setText(SharedHelper.getKey(context, "eta_time"));
//            //serviceItemPrice.setText(SharedHelper.getKey(context, "currency") + "" + SharedHelper.getKey(context, "estimated_fare"));
//
//            if (!SharedHelper.getKey(context, "name").equalsIgnoreCase("")
//                    && !SharedHelper.getKey(context, "name").equalsIgnoreCase(null)
//                    && !SharedHelper.getKey(context, "name").equalsIgnoreCase("null")) {
//                // btnRequestRides.setText(SharedHelper.getKey(context, "name"));
//
//
//                //lblType.setText(SharedHelper.getKey(context, "name"));
//            } else {
//                lblType.setText("" + "Sedan");
//            }
//
//            if ((customDialog != null) && (customDialog.isShowing()))
//                customDialog.dismiss();
//        }
//    }

    public void getNewApproximateFare(String service_type1, final MyTextView view) {
        scheduledDate = "";
        scheduledTime = "";
        JSONObject object = new JSONObject();
        sourceAddress = new ArrayList<>();
        sourcelat = new ArrayList<>();
        sourceLng = new ArrayList<>();
        destiAddress = new ArrayList<>();

//        if (size == 1) {
        if (destLatLng != null) {
            constructedURL = URLHelper.ESTIMATED_FARE_DETAILS_API + "" +
                    "?s_latitude=" + source_lat
                    + "&s_latitude_2=" + ""
                    + "&s_latitude_3=" + ""
                    + "&s_address=" + source_address
                    + "&s_address_2=" + ""
                    + "&s_address_3=" + ""
                    + "&d_address=" + dest_address
                    + "&d_address_2=" + ""
                    + "&d_address_3=" + ""
                    + "&s_longitude=" + source_lng
                    + "&s_longitude_2=" + ""
                    + "&s_longitude_3=" + ""
                    + "&d_latitude=" + destLatLng.latitude + ""
                    + "&d_latitude_2=" + ""
                    + "&d_latitude_3=" + ""
                    + "&d_longitude=" + destLatLng.longitude + ""
                    + "&d_longitude_2=" + ""
                    + "&d_longitude_3=" + ""
                    + "&service_type=" + service_type1
                    + "&ip_address=" + ip_address;
        } else {
            constructedURL = URLHelper.ESTIMATED_FARE_DETAILS_API + "" +
                    "?s_latitude=" + source_lat
                    + "&s_latitude_2=" + ""
                    + "&s_latitude_3=" + ""
                    + "&s_address=" + source_address
                    + "&s_address_2=" + ""
                    + "&s_address_3=" + ""
                    + "&d_address=" + dest_address
                    + "&d_address_2=" + ""
                    + "&d_address_3=" + ""
                    + "&s_longitude=" + source_lng
                    + "&s_longitude_2=" + ""
                    + "&s_longitude_3=" + ""
                    + "&d_latitude=" + dest_lat + ""
                    + "&d_latitude_2=" + ""
                    + "&d_latitude_3=" + ""
                    + "&d_longitude=" + dest_lng + ""
                    + "&d_longitude_2=" + ""
                    + "&d_longitude_3=" + ""
                    + "&service_type=" + service_type1
                    + "&ip_address=" + ip_address;
        }
//        }


        Log.e("constructedURL", constructedURL + "constructedURL");
        System.out.println("getNewApproximateFare getNewApproximateFare " + constructedURL);
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.GET,
                        constructedURL,
                        object,
                        response -> {
                            Log.e("ApproximateResponse", response + "");
                            if (response != null) {
                                if (!response.optString("estimated_fare").equalsIgnoreCase("")) {
                                    utils.print("ApproximateResponse", response.toString());

                                    String stringPrice = response.optString("estimated_fare");
                                    float floatPrice = Float.parseFloat(stringPrice);
                                    int intPrice = Math.round(floatPrice);
                                    SharedHelper.putKey(context, "estimated_fare", String.valueOf(intPrice));

//                                    SharedHelper.putKey(context, "estimated_fare",
//                                            response.optString("estimated_fare"));
                                    System.out.println("getNewApproximateFare getNewApproximateFare " +
                                            response.optString("estimated_fare"));
                                    SharedHelper.putKey(context,
                                            "distance", response.optString("distance"));
                                    SharedHelper.putKey(context,
                                            "eta_time", response.optString("time"));
                                    SharedHelper.putKey(context,
                                            "surge", response.optString("surge"));
                                    SharedHelper.putKey(context,
                                            "surge_value", response.optString("surge_value"));
                                    try {

                                        JSONObject object1 = response.getJSONObject("country");
                                        String curncy = object1.getString("currency_symbol");
                                        SharedHelper.putKey(context, "currency", curncy);
                                        Log.e("currency", curncy + "currency");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    System.out.println("SURGE SURGE SURGE 123456 "
                                            + response.optString("surge"));
                                    //   setNewValuesForApproximateLayout();
                                    double wallet_balance = response.optDouble("wallet_balance");
                                    SharedHelper.putKey(context, "wallet_balance",
                                            "" + response.optDouble("wallet_balance"));
                                    view.setText(SharedHelper.getKey(context,
                                            "currency") + "" +
                                            SharedHelper.getKey(context, "estimated_fare"));
                                    if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
                                        lineView.setVisibility(View.VISIBLE);
                                        chkWallet.setVisibility(View.VISIBLE);
                                    } else {
                                        lineView.setVisibility(View.GONE);
                                        chkWallet.setVisibility(View.GONE);
                                    }
                                   /* flowValue = 2;
                                    layoutChanges();*/
                                }
                            }
                        }, error -> {
                    Log.e("ApproximateResp", error.toString() + "error");
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 ||
                                    response.statusCode == 500) {
                                try {
//                                    utils.showAlert(context, errorObj.optString("message"));
                                } catch (Exception e) {
                                    utils.showAlert(context, context
                                            .getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                refreshAccessToken("APPROXIMATE_RATE");
                            } else if (response.statusCode == 422) {
                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
//                                    utils.showAlert(context, json);
                                } else {
//                                            utils.showAlert(context,
//                                                    context.getString(R.string.please_try_again));
                                }
                            } else if (response.statusCode == 503) {
                                utils.showAlert(context,
                                        context.getString(R.string.server_down));
                            } else {
//                                        utils.showAlert(context,
//                                                context.getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            utils.showAlert(context,
                                    context.getString(R.string.something_went_wrong));
                        }

                    } else {
//                                utils.showAlert(context, context.getString(R.string.please_try_again));
                    }

                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type")
                                + " " + SharedHelper.getKey(context, "access_token"));
                        Log.e("headers", headers + "getNewApproximateFare");
                        return headers;
                    }
                };

        MyCourier.getInstance().addToRequestQueue(jsonObjectRequest);

    }


    private void showreasonDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.cancel_dialog, null);
        final EditText reasonEtxt = (EditText) view.findViewById(R.id.reason_etxt);
        Button submitBtn = (Button) view.findViewById(R.id.submit_btn);
        builder.setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setView(view)
                .setCancelable(true);
        final AlertDialog dialog = builder.create();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancalReason = reasonEtxt.getText().toString();
                cancelRequest();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void layoutChanges() {
        try {
            utils.hideKeypad(getActivity(), getActivity().getCurrentFocus());
            if (lnrApproximate.getVisibility() == View.VISIBLE) {
                lnrApproximate.startAnimation(slide_down);
            } else if (ScheduleLayout.getVisibility() == View.VISIBLE) {
                ScheduleLayout.startAnimation(slide_down);
            } else if (lnrRequestProviders.getVisibility() == View.VISIBLE) {
                lnrRequestProviders.startAnimation(slide_down);
            } else if (lnrProviderPopup.getVisibility() == View.VISIBLE) {
                lnrProviderPopup.startAnimation(slide_down);
                lnrSearchAnimation.startAnimation(slide_up_down);
                lnrSearchAnimation.setVisibility(View.VISIBLE);
            } else if (lnrInvoice.getVisibility() == View.VISIBLE) {
                lnrInvoice.startAnimation(slide_down);
            } else if (lnrRateProvider.getVisibility() == View.VISIBLE) {
                lnrRateProvider.startAnimation(slide_down);
            } else if (lnrInvoice.getVisibility() == View.VISIBLE) {
                lnrInvoice.startAnimation(slide_down);
            }
            lnrRequestProviders.setVisibility(View.GONE);
            lnrProviderPopup.setVisibility(View.GONE);
            lnrApproximate.setVisibility(View.GONE);
            lnrWaitingForProviders.setVisibility(View.GONE);
            lnrProviderAccepted.setVisibility(View.GONE);
            lnrInvoice.setVisibility(View.GONE);
            lnrRateProvider.setVisibility(View.GONE);
            ScheduleLayout.setVisibility(View.GONE);
            rtlStaticMarker.setVisibility(View.GONE);
            frmDestination.setVisibility(View.GONE);
            imgMenu.setVisibility(View.INVISIBLE);
            imgBack.setVisibility(View.GONE);
            shadowBack.setVisibility(View.GONE);
            txtCommentsRate.setText("");
            scheduleDate.setText("" + context.getString(R.string.sample_date));
            scheduleTime.setText("" + context.getString(R.string.sample_time));
            if (flowValue == 0) {
                layoutdriverstatus.setVisibility(View.GONE);
                if (imgMenu.getVisibility() == View.INVISIBLE) {

                    frmSource.setOnClickListener(new OnClick());
                    frmDest.setOnClickListener(new OnClick());
                    srcDestLayout.setOnClickListener(null);
                    if (mMap != null) {
                        mMap.clear();
                        stopAnim();
                        setupMap();
                    }
                }


                frmDestination.setVisibility(View.VISIBLE);
                lnrRequestProviders.setVisibility(View.GONE);

                imgMenu.setVisibility(View.VISIBLE);
                destination.setText("");
                frmDest.setText("");
                frmSource.setText("" + current_address);
//                dest_address = "";
//                dest_lat = "";
//                dest_lng = "";
                source_lat = "" + current_lat;
                source_lng = "" + current_lng;
                source_address = "" + current_address;
                sourceAndDestinationLayout.setVisibility(View.VISIBLE);
            } else if (flowValue == 1) {
                layoutdriverstatus.setVisibility(View.GONE);
                frmSource.setVisibility(View.VISIBLE);
                destinationBorderImg.setVisibility(View.GONE);
                frmDestination.setVisibility(View.GONE);
              /*  if(isRequestProviderScreen){
                    frmDestination.setVisibility(View.GONE);
                } else {
                    frmDestination.setVisibility(View.VISIBLE);
                }*/

                imgBack.setVisibility(View.VISIBLE);
                lnrRequestProviders.startAnimation(slide_up);
                lnrRequestProviders.setVisibility(View.VISIBLE);
                if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
                    if (lineView != null && chkWallet != null) {
                        lineView.setVisibility(View.VISIBLE);
                        chkWallet.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (lineView != null && chkWallet != null) {
                        lineView.setVisibility(View.GONE);
                        chkWallet.setVisibility(View.GONE);
                    }
                }
                chkWallet.setChecked(false);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(true);
                    destinationMarker.setDraggable(true);
                }

            } else if (flowValue == 2) {
                frmDestination.setVisibility(View.GONE);
                imgBack.setVisibility(View.VISIBLE);
                chkWallet.setChecked(false);
                lnrApproximate.startAnimation(slide_up);
                lnrApproximate.setVisibility(View.VISIBLE);
                if (sourceMarker != null && destinationMarker != null) {
                    sourceMarker.setDraggable(false);
                    destinationMarker.setDraggable(false);
                }
            } else if (flowValue == 3) {
                imgBack.setVisibility(View.VISIBLE);
                lnrWaitingForProviders.setVisibility(View.VISIBLE);

                //sourceAndDestinationLayout.setVisibility(View.GONE);
            } else if (flowValue == 4) {
                imgMenu.setVisibility(View.VISIBLE);
                lnrProviderAccepted.startAnimation(slide_up);


                lnrProviderAccepted.setVisibility(View.VISIBLE);
            } else if (flowValue == 5) {
                imgMenu.setVisibility(View.VISIBLE);
                lnrInvoice.startAnimation(slide_up);
                lnrInvoice.setVisibility(View.VISIBLE);
            } else if (flowValue == 6) {
                imgMenu.setVisibility(View.VISIBLE);
                lnrRateProvider.startAnimation(slide_up);
                lnrRateProvider.setVisibility(View.VISIBLE);
                LayerDrawable drawable = (LayerDrawable) ratingProviderRate.getProgressDrawable();
                drawable.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                drawable.getDrawable(1).setColorFilter(Color.parseColor("#FFAB00"),
                        PorterDuff.Mode.SRC_ATOP);
                drawable.getDrawable(2).setColorFilter(Color.parseColor("#FFAB00"),
                        PorterDuff.Mode.SRC_ATOP);
                ratingProviderRate.setRating(1.0f);
                feedBackRating = "1";
                ratingProviderRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                        if (rating < 1.0f) {
                            ratingProviderRate.setRating(1.0f);
                            feedBackRating = "1";
                        }
                        feedBackRating = String.valueOf((int) rating);
                    }
                });
            } else if (flowValue == 7) {
                imgBack.setVisibility(View.VISIBLE);
                ScheduleLayout.startAnimation(slide_up);
                ScheduleLayout.setVisibility(View.VISIBLE);
            } else if (flowValue == 8) {
                // clear all views
                shadowBack.setVisibility(View.GONE);
            } else if (flowValue == 9) {

                rtlStaticMarker.setVisibility(View.VISIBLE);
                shadowBack.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.style_json));

//            if (!success) {
//                utils.print("Map:Style", "Style parsing failed.");
//            } else {
//                utils.print("Map:Style", "Style Applied.");
//            }
        } catch (Resources.NotFoundException e) {
            utils.print("Map:Style", "Can't find style. Error: ");
        }

        mMap = googleMap;

        setupMap();

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
//                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
//            mMap.setMyLocationEnabled(true);
        }


        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {

            }
        });

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (pick_first) {
                    // Cleaning all the markers.
//                if (googleMap != null) {
//                    googleMap.clear();
//                }
                    LatLng target = googleMap.getCameraPosition().target;
                    updateLocation(target);
                }

            }
        });


    }


    private void updateLocation(LatLng centerLatLng) {
        if (centerLatLng != null) {
            Geocoder geocoder = new Geocoder(context,
                    Locale.getDefault());

            List<Address> addresses = new ArrayList<Address>();
            try {
                addresses = geocoder.getFromLocation(centerLatLng.latitude,
                        centerLatLng.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null && addresses.size() > 0) {

                String addressIndex0 = (addresses.get(0).getAddressLine(0) != null) ? addresses
                        .get(0).getAddressLine(0) : null;
                String addressIndex1 = (addresses.get(0).getAddressLine(1) != null) ? addresses
                        .get(0).getAddressLine(1) : null;
                String addressIndex2 = (addresses.get(0).getAddressLine(2) != null) ? addresses
                        .get(0).getAddressLine(2) : null;
                String addressIndex3 = (addresses.get(0).getAddressLine(3) != null) ? addresses
                        .get(0).getAddressLine(3) : null;
                String completeAddress = addressIndex0 + "," + addressIndex1;

                if (addressIndex2 != null) {
                    completeAddress += "," + addressIndex2;
                }
                if (addressIndex3 != null) {
                    completeAddress += "," + addressIndex3;
                }
                if (completeAddress != null) {
                    //mLocationTextView.setText(completeAddress);
                    txtSelectedAddressSource.setText(completeAddress);
                }
            }
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(context)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        1);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    private boolean isDragging;

    @Override
    public void onMarkerDragEnd(Marker marker) {
        String title = "";

        if (marker != null && marker.getTitle() != null) {
            title = marker.getTitle();

            if (sourceMarker != null && title.equalsIgnoreCase("Source")) {
                LatLng markerLocation = sourceMarker.getPosition();
                Geocoder geocoder;
                List<Address> addresses = null;
                geocoder = new Geocoder(getActivity(), Locale.getDefault());

                source_lat = markerLocation.latitude + "";
                source_lng = markerLocation.longitude + "";

                try {
                    addresses = geocoder.getFromLocation(markerLocation.latitude, markerLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    if (addresses.size() > 0) {
                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        ; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        SharedHelper.putKey(context, "source", "" + address + "," + city + "," + state);
                        source_address = "" + address + "," + city + "," + state;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (destinationMarker != null && title.equalsIgnoreCase("Destination")) {
                LatLng markerLocation = destinationMarker.getPosition();
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getActivity(), Locale.getDefault());

                dest_lat = "" + markerLocation.latitude;
                dest_lng = "" + markerLocation.longitude;

                try {
                    addresses = geocoder.getFromLocation(markerLocation.latitude, markerLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    if (addresses.size() > 0) {
                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        ; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        SharedHelper.putKey(context, "destination", "" + address + "," + city + "," + state);
                        dest_address = "" + address + "," + city + "," + state;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            mMap.clear();
            setValuesForSourceAndDestination();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    initMap();
                    MapsInitializer.initialize(getActivity());
                } /*else {
                    showPermissionReqDialog();
                }*/
                break;
            case 2:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                }
                break;
            case 3:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "sos")));
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showPermissionReqDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setMessage("WhereGo needs the location permission, Please accept to use location functionality")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Prompt the user once explanation has been shown
                        // requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), HomeFragment.TAG);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .create()
                .show();
    }

    private void showDialogForGPSIntent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("GPS is disabled in your device. Enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                activity.startActivity(callGPSSettingIntent);
                            }
                        });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    CharSequence pickUpLocationName = "";
    CharSequence dropLocationName = "";
//    CharSequence pickUpLocationName1 = "";
//    CharSequence dropLocationName1 = "";
//    CharSequence pickUpLocationName2 = "";
//    CharSequence dropLocationName2 = "";

    String paymentType = "";
    String paymentId = "";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST) {
            if (parserTask != null) {
                parserTask = null;
            }
            if (resultCode == Activity.RESULT_OK) {
                if (marker != null) {
                    marker.remove();

                }

                destilat = new ArrayList<>();
                destiLng = new ArrayList<>();
                PlacePredictions placePredictions;
                placePredictions = (PlacePredictions) data.getSerializableExtra("Location Address");
                strPickLocation = data.getExtras().getString("pick_location");
                strPickType = data.getExtras().getString("type");
                size = data.getExtras().getInt("size");


                Log.e("size", size + " size");
                if (strPickLocation.equalsIgnoreCase("yes")) {
                    pick_first = true;
                    mMap.clear();
                    flowValue = 9;
                    layoutChanges();
                    float zoomLevel = 16.0f; //This goes up to 21
                    stopAnim();
                } else {
                    if (placePredictions != null) {
                        if (!placePredictions.strSourceAddress.equalsIgnoreCase("")) {
                            source_lat = "" + placePredictions.strSourceLatitude;
                            source_lng = "" + placePredictions.strSourceLongitude;
                            source_address = placePredictions.strSourceAddress;
                            if (!placePredictions.strSourceLatitude.equalsIgnoreCase("")
                                    && !placePredictions.strSourceLongitude.equalsIgnoreCase("")) {
                                double latitude = Double.parseDouble(placePredictions.strSourceLatitude);
                                double longitude = Double.parseDouble(placePredictions.strSourceLongitude);
                                LatLng location = new LatLng(latitude, longitude);

                                //mMap.clear();
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .anchor(0.5f, 0.75f)
                                        .position(location)
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_source));
                                marker = mMap.addMarker(markerOptions);
                                sourceMarker = mMap.addMarker(markerOptions);
                               /* CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(16).build();
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
                            }

                        }


                        if (!placePredictions.strDestAddress.equalsIgnoreCase("")) {
                            dest_lat = "" + placePredictions.strDestLatitude;
                            dest_lng = "" + placePredictions.strDestLongitude;
                            dest_address = placePredictions.strDestAddress;
                            dropLocationName = dest_address;

//                            sourceAddress,sourcelat,sourceLng,destiAddress,destilat,destiLng
                            SharedHelper.putKey(context, "current_status", "2");

                        }


                        if (dest_address.equalsIgnoreCase("")) {
                            flowValue = 1;
                            frmSource.setText(source_address);
                            getServiceList();
                        } else {
                            flowValue = 1;

                            if (cardInfoArrayList.size() > 0) {
                                getCardDetailsForPayment(cardInfoArrayList.get(0));

                            }
                            getServiceList();
                        }

                        layoutChanges();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.e("paymentExample", confirm.toJSONObject().toString(4));

                        // TODO: send 'confirm' to your server for verification.
                        // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                        // for more details.
                        paymentType = "PAYPAL";
                        paymentId = confirm.getProofOfPayment().getPaymentId();
                        payNow();
//                    JSONObject jsonObject=confirm.toJSONObject().getJSONObject("response");
//                            addPayment(jsonObject.getString("id"));
                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            }
        }
        if (requestCode == ADD_CARD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("isAdded", false);
                if (result) {
                    getCards();
                }
            }
        }

        if (requestCode == 12) {
            if (resultCode == Activity.RESULT_OK) {
                startApproximateSlide();
                item_id = data.getStringExtra("item_id");

            } else if (resultCode == Activity.RESULT_CANCELED) {
                flowValue = 1;
                layoutChanges();
            }
        }

        if (requestCode == 5555) {
            if (resultCode == Activity.RESULT_OK) {
                CardInfo cardInfo = data.getParcelableExtra("card_info");
                getCardDetailsForPayment(cardInfo);
            }
        }
        if (requestCode == REQUEST_LOCATION) {
            Log.e("GPS Result Status", "onActivityResult: " + requestCode);
            Log.e("GPS Result Status", "onActivityResult: " + data);
        } else {
            Log.e("GPS Result Status else", "onActivityResult: " + requestCode);
            Log.e("GPS Result Status else", "onActivityResult: " + data);
        }
    }

    void showProviderPopup(JSONObject jsonObject) {
        lnrSearchAnimation.startAnimation(slide_up_top);
        lnrSearchAnimation.setVisibility(View.GONE);
        lnrProviderPopup.setVisibility(View.VISIBLE);
        lnrRequestProviders.setVisibility(View.GONE);

        Glide.with(activity).load(jsonObject.optString("image")).placeholder(R.drawable.pickup_drop_icon).dontAnimate()
                .error(R.drawable.pickup_drop_icon).into(imgProviderPopup);

        lnrPriceBase.setVisibility(View.GONE);
        lnrPricemin.setVisibility(View.GONE);
        lnrPricekm.setVisibility(View.GONE);

        if (jsonObject.optString("calculator").equalsIgnoreCase("MIN")
                || jsonObject.optString("calculator").equalsIgnoreCase("HOUR")) {
            lnrPriceBase.setVisibility(View.VISIBLE);
            lnrPricemin.setVisibility(View.VISIBLE);
            if (jsonObject.optString("calculator").equalsIgnoreCase("MIN")) {
                lblCalculationType.setText("Minutes");
            } else {
                lblCalculationType.setText("Hours");
            }
        } else if (jsonObject.optString("calculator").equalsIgnoreCase("DISTANCE")) {
            lnrPriceBase.setVisibility(View.VISIBLE);
            lnrPricekm.setVisibility(View.VISIBLE);
            lblCalculationType.setText("Distance");
        } else if (jsonObject.optString("calculator").equalsIgnoreCase("DISTANCEMIN")
                || jsonObject.optString("calculator").equalsIgnoreCase("DISTANCEHOUR")) {
            lnrPriceBase.setVisibility(View.VISIBLE);
            lnrPricemin.setVisibility(View.VISIBLE);
            lnrPricekm.setVisibility(View.VISIBLE);
            if (jsonObject.optString("calculator").equalsIgnoreCase("DISTANCEMIN")) {
                lblCalculationType.setText("Distance and Minutes");
            } else {
                lblCalculationType.setText("Distance and Hours");
            }
        }

        if (!jsonObject.optString("capacity").equalsIgnoreCase("null")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lblCapacity.setText(jsonObject.optString("capacity"));
            } else {
                lblCapacity.setText(jsonObject.optString("capacity") + " peoples");
            }
        } else {
            lblCapacity.setVisibility(View.GONE);
        }

        lblServiceName.setText("" + jsonObject.optString("name"));
        String basePriceString = jsonObject.optString("fixed");
        float baseFloatPrice = Float.parseFloat(basePriceString);
        int intBasePrice = Math.round(baseFloatPrice);

        String priceString = jsonObject.optString("price");
        float floatPrice = Float.parseFloat(priceString);
        int intPrice = Math.round(floatPrice);

        lblBasePricePopup.setText(SharedHelper.getKey(context, "currency") +
                intBasePrice + "");

        lblPriceKm.setText(SharedHelper.getKey(context, "currency") +
                intPrice + "");

        lblPriceMin.setText(SharedHelper.getKey(context, "currency") +
                jsonObject.optString("minute"));

        if (jsonObject.optString("description").equalsIgnoreCase("null")) {
            lblProviderDesc.setVisibility(View.GONE);
        } else {
            lblProviderDesc.setVisibility(View.VISIBLE);
            lblProviderDesc.setText("" + jsonObject.optString("description"));
        }
    }

    public void setValuesForApproximateLayout() {
        if (isInternet) {
            String surge = SharedHelper.getKey(context, "surge");
            if (surge.equalsIgnoreCase("1")) {
                surgeDiscount.setVisibility(View.VISIBLE);
                surgeTxt.setVisibility(View.VISIBLE);
                surgeDiscount.setText(SharedHelper.getKey(context, "surge_value"));
            } else {
                surgeDiscount.setVisibility(View.GONE);
                surgeTxt.setVisibility(View.GONE);
            }


            lblCmfrmSourceAddress.setText(source_address);
            lblCmfrmDestAddress.setText(dest_address);
            lblApproxAmount.setText(SharedHelper.getKey(context, "currency") + "" +
                    SharedHelper.getKey(context, "estimated_fare"));
            lblEta.setText(SharedHelper.getKey(context, "eta_time"));
            lblDis.setText(SharedHelper.getKey(context, "distance") + " Km");
//            tvETA.setText(SharedHelper.getKey(context, "eta_time"));
            if (!SharedHelper.getKey(context, "first_name").equalsIgnoreCase("")
                    && !SharedHelper.getKey(getActivity(), "first_name").equalsIgnoreCase(null)
                    && !SharedHelper.getKey(getActivity(), "first_name").equalsIgnoreCase("null")) {
                lblType.setText(SharedHelper.getKey(getActivity(), "first_name"));
            } else {
                lblType.setText("" + "Sedan");
            }

            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
        }
    }

    private void getCards() {
        Ion.with(getActivity())
                .load(URLHelper.CARD_PAYMENT_LIST)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization",
                        SharedHelper.getKey(context, "token_type") + " " +
                                SharedHelper.getKey(context, "access_token"))
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<com.koushikdutta.ion.Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, com.koushikdutta.ion.Response<String> response) {
                        // response contains both the headers and the string result
                        try {
                            if (response.getHeaders().code() == 200) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response.getResult());
                                    if (jsonArray.length() > 0) {
                                        CardInfo cardInfo = new CardInfo();
                                        cardInfo.setCardId("CASH");
                                        cardInfo.setCardType("CASH");
                                        cardInfo.setLastFour("CASH");
                                        cardInfoArrayList.add(cardInfo);
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject cardObj = jsonArray.getJSONObject(i);
                                            cardInfo = new CardInfo();
                                            cardInfo.setCardId(cardObj.optString("card_id"));
                                            cardInfo.setCardType(cardObj.optString("brand"));
                                            cardInfo.setLastFour(cardObj.optString("last_four"));
                                            cardInfoArrayList.add(cardInfo);
                                        }
                                    }

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            CardInfo cardInfo = new CardInfo();
                            cardInfo.setCardId("CASH");
                            cardInfo.setCardType("CASH");
                            cardInfo.setLastFour("CASH");
                            cardInfoArrayList.add(cardInfo);
                        }
                    }
                });

    }

    /// for getting available type of service
    public void getServiceList() {
        Log.e("callServicelist", "callServicelist");

        customDialog.setCancelable(true);
        if (customDialog != null)
            customDialog.show();
        JsonArrayRequest jsonArrayRequest = new
                JsonArrayRequest(URLHelper.GET_SERVICE_LIST_API,
                        response -> {
                            Log.e("service_list_response", response + "response");
                            utils.print("GetServices", response.toString());
                            if (SharedHelper.getKey(context, "service_type")
                                    .equalsIgnoreCase("")) {
                                SharedHelper.putKey(context, "service_type", "" +
                                        response.optJSONObject(0).optString("id"));
                            }
                            customDialog.dismiss();
                            if (response.length() > 0) {
                                currentPostion = 0;
                                ServiceListAdapter serviceListAdapter = new
                                        ServiceListAdapter(response);
                                Log.e("rcvServiceTypesrespons1", response + "response");
                                rcvServiceTypes.setLayoutManager(new LinearLayoutManager(activity,
                                        LinearLayoutManager.HORIZONTAL, false));
                                rcvServiceTypes.setAdapter(serviceListAdapter);
                                getProvidersList(SharedHelper.getKey(context, "service_type"));
                            } else {
                                utils.displayMessage(getView(), getString(R.string.no_service));
                            }
                            mMap.clear();
                            setValuesForSourceAndDestination();
                        }, error -> {
                    Log.e("service_list_error", error.toString() + "error");
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
                                    utils.displayMessage(getView(),
                                            errorObj.optString("message"));
                                } catch (Exception e) {
                                    utils.displayMessage(getView(),
                                            getString(R.string.something_went_wrong));
                                }
                                flowValue = 1;
                                layoutChanges();
                            } else if (response.statusCode == 401) {
                                refreshAccessToken("SERVICE_LIST");
                            } else if (response.statusCode == 422) {

                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    utils.displayMessage(getView(), json);
                                } else {
                                    utils.displayMessage(getView(),
                                            getString(R.string.please_try_again));
                                }
                                flowValue = 1;
                                layoutChanges();
                            } else if (response.statusCode == 503) {
                                utils.displayMessage(getView(),
                                        getString(R.string.server_down));
                                flowValue = 1;
                                layoutChanges();
                            } else {
                                utils.displayMessage(getView(),
                                        getString(R.string.please_try_again));
                                flowValue = 1;
                                layoutChanges();
                            }

                        } catch (Exception e) {
                            utils.displayMessage(getView(),
                                    getString(R.string.something_went_wrong));
                            flowValue = 1;
                            layoutChanges();
                        }

                    } else {
                        utils.displayMessage(getView(),
                                getString(R.string.please_try_again));
                        flowValue = 1;
                        layoutChanges();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" +
                                SharedHelper.getKey(context, "token_type") + " "
                                + SharedHelper.getKey(context, "access_token"));
                        Log.i(TAG, "getHeaders param : " + headers);
                        return headers;


                    }
                };
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyCourier.getInstance().addToRequestQueue(jsonArrayRequest);
    }


    //    / for getting approx fair
    public void getApproximateFare() {

        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();

        constructedURL = URLHelper.ESTIMATED_FARE_DETAILS_API + "" +
                "?s_latitude=" + source_lat
                + "&s_latitude_2=" + ""
                + "&s_latitude_3=" + ""
                + "&s_address=" + source_address
                + "&s_address_2=" + ""
                + "&s_address_3=" + ""
                + "&d_address=" + dest_address
                + "&d_address_2=" + ""
                + "&d_address_3=" + ""
                + "&s_longitude=" + source_lng
                + "&s_longitude_2=" + ""
                + "&s_longitude_3=" + ""
                + "&d_latitude=" + dest_lat
                + "&d_latitude_2=" + ""
                + "&d_latitude_3=" + ""
                + "&d_longitude=" + dest_lng
                + "&d_longitude_2=" + ""
                + "&d_longitude_3=" + ""
                + "&service_type=" + SharedHelper.getKey(context, "service_type")
                + "&ip_address=" + ip_address;


        System.out.println("getNewApproximateFare getNewApproximateFare " + constructedURL);
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.GET,
                        constructedURL,
                        object,
                        response -> {
                            customDialog.dismiss();
                            if (response != null) {
                                if (!response.optString("estimated_fare").equalsIgnoreCase("")) {
                                    utils.print("ApproximateResponse", response.toString());

                                    String stringPrice = response.optString("estimated_fare");
                                    float floatPrice = Float.parseFloat(stringPrice);
                                    int intPrice = Math.round(floatPrice);
                                    SharedHelper.putKey(context, "estimated_fare", String.valueOf(intPrice));

                                    SharedHelper.putKey(context, "distance", response.optString("distance"));
                                    SharedHelper.putKey(context, "eta_time", response.optString("time"));
                                    SharedHelper.putKey(context, "surge", response.optString("surge"));
                                    SharedHelper.putKey(context, "surge_value", response.optString("surge_value"));
                                    try {

                                        JSONObject object1 = response.getJSONObject("country");
                                        String curncy = object1.getString("currency_symbol");
                                        SharedHelper.putKey(context, "currency", curncy);
                                        Log.e("currency", curncy + "currency");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    setValuesForApproximateLayout();
                                    double wallet_balance = response.optDouble("wallet_balance");
                                    SharedHelper.putKey(context, "wallet_balance", "" + response.optDouble("wallet_balance"));

                                    if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
                                        lineView.setVisibility(View.VISIBLE);
                                        chkWallet.setVisibility(View.VISIBLE);
                                    } else {
                                        lineView.setVisibility(View.GONE);
                                        chkWallet.setVisibility(View.GONE);
                                    }
                                    flowValue = 2;
                                    layoutChanges();
                                }
                            }
                        }, error -> {
                    utils.print("ApproximateResponseError", error.toString());
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 ||
                                    response.statusCode == 500) {
                                try {
//                                    utils.showAlert(context, errorObj.optString("message"));
                                } catch (Exception e) {
////                                    utils.showAlert(context,
//                                            context.getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                refreshAccessToken("APPROXIMATE_RATE");
                            } else if (response.statusCode == 422) {
                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
//                                    utils.showAlert(context, json);
                                } else {
                                    //                                utils.showAlert(context, context.getString(R.string.please_try_again));
                                }
                            } else if (response.statusCode == 503) {
//                                utils.showAlert(context, context.getString(R.string.server_down));
                            } else {
                                //                            utils.showAlert(context, context.getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            utils.showAlert(context, context.getString(R.string.something_went_wrong));
                        }

                    } else {
                        //                    utils.showAlert(context, context.getString(R.string.please_try_again));
                    }

                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                        return headers;
                    }
                };

        MyCourier.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    // get estimated time
    public void getDriverETA() {
        LatLng DriverLocation = providerMarker.getPosition();
        Double driver_lat = DriverLocation.latitude;
        Double driver_longi = DriverLocation.longitude;
        Log.e("TAG", "DRIVER LOCATION:" + driver_lat + "," + driver_longi);
        JSONObject object = new JSONObject();
        String constructedURL1 = URLHelper.ESTIMATED_FARE_DETAILS_API + "" +
                "?s_latitude=" + driver_lat
                + "&s_longitude=" + driver_longi
                + "&d_latitude=" + source_lat
                + "&d_longitude=" + source_lng
                + "&service_type=" + SharedHelper.getKey(context, "service_type");

        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.GET,
                        constructedURL1,
                        object,
                        response -> {
                            if (response != null) {
                                utils.print("EtaResponse", "" + response.toString());
                                if (!response.optString("time").equalsIgnoreCase("")) {
                                    ETA = response.optString("time");
                                } else {
                                    ETA = "--";
                                }
                                lblETA.setText(ETA);
//                                tvETA.setText(ETA);
                            }
                        }, error -> {
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
//                                    utils.showAlert(context,
//                                            errorObj.optString("message"));
                                } catch (Exception e) {
//                                    utils.showAlert(context,
//                                            context.getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                refreshAccessToken("APPROXIMATE_RATE");
                            } else if (response.statusCode == 422) {
                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
//                                    utils.showAlert(context, json);
                                } else {
//                                    utils.showAlert(context,
//                                            context.getString(R.string.please_try_again));
                                }
                            } else if (response.statusCode == 503) {
                                utils.showAlert(context,
                                        context.getString(R.string.server_down));
                            } else {
                                utils.showAlert(context,
                                        context.getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            utils.showAlert(context,
                                    context.getString(R.string.something_went_wrong));
                        }

                    } else {
                        utils.showAlert(context,
                                context.getString(R.string.please_try_again));
                    }

                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" +
                                SharedHelper.getKey(context, "token_type") + " " +
                                SharedHelper.getKey(context, "access_token"));
                        return headers;
                    }
                };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyCourier.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    void getProvidersList(String strTag) {
        String providers_request = URLHelper.GET_PROVIDERS_LIST_API + "?" +
                "latitude=" + current_lat +
                "&longitude=" + current_lng +
                "&service=" + strTag;

        utils.print("Get all providers", "" + providers_request);
        utils.print("service_type", "" +
                SharedHelper.getKey(context, "service_type"));

        for (int i = 0; i < lstProviderMarkers.size(); i++) {
            lstProviderMarkers.get(i).remove();
        }
        JsonArrayRequest jsonArrayRequest = new
                JsonArrayRequest(providers_request,
                        response -> {
                            utils.print("GetProvidersList", response.toString());
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject jsonObj = response.getJSONObject(i);
                                    utils.print("GetProvidersList",
                                            jsonObj.getString("latitude")
                                                    + "," + jsonObj.getString("longitude"));
                                    if (!jsonObj.getString("latitude")
                                            .equalsIgnoreCase("") &&
                                            !jsonObj.getString("longitude")
                                                    .equalsIgnoreCase("")) {
                                        Double proLat = Double
                                                .parseDouble(jsonObj.getString("latitude"));
                                        Double proLng = Double
                                                .parseDouble(jsonObj.getString("longitude"));
                                        Float rotation = 0.0f;
                                        MarkerOptions markerOptions = new MarkerOptions()
                                                .anchor(0.5f, 0.75f)
                                                .position(new LatLng(proLat, proLng))
                                                .rotation(rotation)
                                                .icon(BitmapDescriptorFactory
                                                        .fromResource(R.drawable
                                                                .provider_location_icon));
                                        lstProviderMarkers.add(mMap.addMarker(markerOptions));
                                        builder.include(new LatLng(proLat, proLng));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, error -> {
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {

                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 ||
                                    response.statusCode == 405 ||
                                    response.statusCode == 500) {
                            } else if (response.statusCode == 401) {
                                refreshAccessToken("PROVIDERS_LIST");
                            } else if (response.statusCode == 422) {
                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    //                                utils.showAlert(context, json);
                                } else {
                                    //                                utils.showAlert(context,
                                    // context.getString(R.string.please_try_again));
                                }
                            } else if (response.statusCode == 503) {
                                //                            utils.showAlert(context,
                                // context.getString(R.string.please_try_again));
                            } else {
                                //                            utils.showAlert(context,
                                // context.getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            //                        utils.showAlert(context,
                            // context.getString(R.string.something_went_wrong));
                        }

                    } else {
                        //                    utils.showAlert(context, context.getString(R.string.no_drivers_found));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                        return headers;
                    }
                };
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyCourier.getInstance().addToRequestQueue(jsonArrayRequest);

    }

    // send request for ride
    public void sendRequest() {

        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject objects = new JSONObject();
        JSONObject obj = new JSONObject();
        try {
            Log.e("size", size + "size");
            Log.e("s_latitude", source_lat + "source_lat");
//            if (size == 1) {
            Log.e("call1", "call1");
            objects.put("s_latitude", source_lat);
            objects.put("s_longitude", source_lng);
            objects.put("d_latitude", dest_lat);
            objects.put("d_longitude", dest_lng);
            objects.put("s_address", source_address);
            objects.put("d_address", dest_address);

            objects.put("s_latitude_2", "");
            objects.put("s_longitude_2", "");
            objects.put("d_latitude_2", "");
            objects.put("d_longitude_2", "");
            objects.put("s_address_2", "");
            objects.put("d_address_2", "");

            objects.put("s_latitude_3", "");
            objects.put("s_longitude_3", "");
            objects.put("d_latitude_3", "");
            objects.put("d_longitude_3", "");
            objects.put("s_address_3", "");
            objects.put("d_address_3", "");
//            }

            objects.put("service_type", SharedHelper.getKey(context, "service_type"));
            objects.put("distance", SharedHelper.getKey(context, "distance"));

            objects.put("schedule_date", scheduledDate);
            objects.put("schedule_time", scheduledTime);

            objects.put("item_id", item_id);
            objects.put("ip_address", ip_address);

            Log.e("objects", "sendRequest: " + objects);

            if (chkWallet.isChecked()) {
                objects.put("use_wallet", 1);
            } else {
                objects.put("use_wallet", 0);
            }
            if (SharedHelper.getKey(context, "payment_mode").equals("CASH")) {
                objects.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
            } else {
                objects.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
                objects.put("card_id", SharedHelper.getKey(context, "card_id"));
            }
            utils.print("SendRequestInput", "" + objects.toString());

            Log.e("new object", obj.toString() + "objects");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("parameter", objects + "object");

        MyCourier.getInstance().cancelRequestInQueue("send_request");
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.SEND_REQUEST_API,
                        objects,
                        response -> {
                            Log.e("response", response + "response");
                            if (response != null) {
                                utils.print("SendRequestResponse", response.toString());
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
                                if (response.optString("request_id", "").equals("")) {
                                    utils.displayMessage(getView(),
                                            response.optString("message"));
                                } else {
                                    SharedHelper.putKey(context,
                                            "current_status", "");
                                    SharedHelper.putKey(context,
                                            "request_id", "" + response.optString("request_id"));
                                    if (!scheduledDate.equalsIgnoreCase("") &&
                                            !scheduledTime.equalsIgnoreCase(""))
                                        scheduleTrip = true;
                                    else
                                        scheduleTrip = false;
                                    flowValue = 3;
                                    layoutChanges();
                                }
                            }
                        }, error -> {
                    Log.e("error", error.toString() + "error");
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));
                            Log.e("SendREquest", "onErrorResponse: " +
                                    errorObj.optString("message"));
                            if (response.statusCode == 400 ||
                                    response.statusCode == 405 ||
                                    response.statusCode == 500) {
                                try {
//                                    utils.showAlert(context, errorObj.optString("error"));
                                } catch (Exception e) {
                                    utils.showAlert(context,
                                            context.getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                refreshAccessToken("SEND_REQUEST");
                            } else if (response.statusCode == 422) {
                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
//                                    utils.showAlert(context, json);
                                } else {
                                    utils.showAlert(context,
                                            context.getString(R.string.please_try_again));
                                }
                            } else if (response.statusCode == 503) {
                                utils.showAlert(context,
                                        context.getString(R.string.server_down));
                            } else {
                                utils.showAlert(context,
                                        context.getString(R.string.please_try_again));
                            }
                        } catch (Exception e) {
                            utils.showAlert(context,
                                    context.getString(R.string.something_went_wrong));
                        }
                    } else {
                        utils.showAlert(context,
                                context.getString(R.string.please_try_again));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" +
                                SharedHelper.getKey(context, "token_type") + " " +
                                SharedHelper.getKey(context, "access_token"));
                        Log.e("headers", headers + "headers");
                        return headers;
                    }
                };

        MyCourier.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    /// cancel ride request
    public void cancelRequest() {

        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("cancel_reason", cancalReason);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.CANCEL_REQUEST_API,
                        object,
                        response -> {
                            utils.print("CancelRequestResponse", response.toString());
                            Toast.makeText(context, getResources()
                                    .getString(R.string.request_cancel), Toast.LENGTH_SHORT).show();
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            mapClear();
                            SharedHelper.putKey(context, "request_id", "");
                            flowValue = 0;
                            PreviousStatus = "";
                            layoutChanges();
                            setupMap();
                        }, error -> {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        flowValue = 4;
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 ||
                                    response.statusCode == 405 ||
                                    response.statusCode == 500) {
                                try {
                                    utils.displayMessage(getView(), errorObj.optString("message"));
                                } catch (Exception e) {
                                    utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                                }
                                layoutChanges();
                            } else if (response.statusCode == 401) {
                                refreshAccessToken("CANCEL_REQUEST");
                            } else if (response.statusCode == 422) {

                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    utils.displayMessage(getView(), json);
                                } else {
                                    utils.displayMessage(getView(), getString(R.string.please_try_again));
                                }
                                layoutChanges();
                            } else if (response.statusCode == 503) {
                                utils.displayMessage(getView(), getString(R.string.server_down));
                                layoutChanges();
                            } else {
                                utils.displayMessage(getView(), getString(R.string.please_try_again));
                                layoutChanges();
                            }

                        } catch (Exception e) {
                            utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                            layoutChanges();
                        }

                    } else {
                        utils.displayMessage(getView(), getString(R.string.please_try_again));
                        layoutChanges();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                        return headers;
                    }
                };

        MyCourier.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void setValuesForSourceAndDestination() {
        if (isInternet) {
            if (!source_lat.equalsIgnoreCase("")) {
                if (!source_address.equalsIgnoreCase("")) {
                    frmSource.setText(source_address);
                } else {
                    frmSource.setText(current_address);
                }
            } else {
                frmSource.setText(current_address);
            }

            /***************************************CHANGES HERE TO HIDE SOURCE ADDRESS AND DESTINATION ADDRESS TEXTVIEW***********************************************/

            if (!dest_lat.equalsIgnoreCase("")) {
                destination.setText(dest_address);
                frmDestination.setVisibility(View.GONE);
                frmDest.setText(dest_address);

            }

            /***************************************CHANGES HERE TO HIDE SOURCE ADDRESS AND DESTINATION ADDRESS TEXTVIEW***********************************************/

            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                sourceLatLng = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
            }
            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
            }


            if (sourceLatLng != null && destLatLng != null) {
                utils.print("LatLng", "Source:" +
                        sourceLatLng + " Destination: " + destLatLng);
                // String url = getDirectionsUrl(sourceLatLng, destLatLng);
               /* DownloadTask downloadTask = new DownloadTask();
                // Start downloading json data from Google Directions API
                downloadTask.execute(url);*/
                pickUpLocationName = source_address;
                String url = getUrl(sourceLatLng.latitude,
                        sourceLatLng.longitude,
                        destLatLng.latitude,
                        destLatLng.longitude);
                FetchUrl fetchUrl = new FetchUrl();
                fetchUrl.execute(url);
            }

        }
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
                            SharedHelper.putKey(context, "access_token",
                                    response.optString("access_token"));
                            SharedHelper.putKey(context, "refresh_token",
                                    response.optString("refresh_token"));
                            SharedHelper.putKey(context, "token_type",
                                    response.optString("token_type"));
                            if (tag.equalsIgnoreCase("SERVICE_LIST")) {
                                getServiceList();

                            } else if (tag.equalsIgnoreCase("APPROXIMATE_RATE")) {
//                                getApproximateFare();
                            } else if (tag.equalsIgnoreCase("SEND_REQUEST")) {
                                sendRequest();
                            } else if (tag.equalsIgnoreCase("CANCEL_REQUEST")) {
                                cancelRequest();
                            } else if (tag.equalsIgnoreCase("PROVIDERS_LIST")) {
                                getProvidersList("");
                            } else if (tag.equalsIgnoreCase("SUBMIT_REVIEW")) {
                                submitReviewCall();
                            } else if (tag.equalsIgnoreCase("PAY_NOW")) {
                                payNow();
                            }
                        }, error -> {
                    String json = "";
                    NetworkResponse response = error.networkResponse;

                    if (response != null && response.data != null) {
                        try {
                            SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                            utils.GoToBeginActivity(getActivity());
                        } catch (Exception e) {
                            e.printStackTrace();
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

    // payment chooser
    private void showChooser() {
        Intent intent = new Intent(getActivity(), Payment.class);
        startActivityForResult(intent, 5555);
    }

    /// card details
    private void getCardDetailsForPayment(CardInfo cardInfo) {
        if (cardInfo.getLastFour().equals("CASH")) {
            SharedHelper.putKey(context, "payment_mode", "CASH");
            //   imgPaymentType.setImageResource(R.drawable.money1);
            lblPaymentType.setText("CASH");
            chkWallet.setChecked(false);
        } else if (cardInfo.getLastFour().equals("PAYPAL")) {
            chkWallet.setChecked(false);
            SharedHelper.putKey(context, "payment_mode", "PAYPAL");
            //   imgPaymentType.setImageResource(R.drawable.money1);
            lblPaymentType.setText("PAYPAL");
        } else if (cardInfo.getLastFour().equals("RAZORPAY")) {
            chkWallet.setChecked(false);
            SharedHelper.putKey(context, "payment_mode", "RAZORPAY");
            //   imgPaymentType.setImageResource(R.drawable.money1);
            lblPaymentType.setText("RAZORPAY");
        } else if (cardInfo.getLastFour().equals("WALLET")) {
            chkWallet.setChecked(true);
            SharedHelper.putKey(context, "payment_mode", "CASH");
            //   imgPaymentType.setImageResource(R.drawable.money1);
            lblPaymentType.setText(getString(R.string.action_wallet));

        } else {

            SharedHelper.putKey(context, "card_id", cardInfo.getCardId());
//            SharedHelper.putKey(context, "payment_mode", "M-Pesa");
            SharedHelper.putKey(context, "payment_mode", "CARD");
            imgPaymentType.setImageResource(R.drawable.appicon);
            lblPaymentType.setText("xxxx" + cardInfo.getLastFour());
        }
    }

    /// pay now options
    public void payNow() {

        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("payment_mode", paymentMode);
            object.put("is_paid", isPaid);
            if (paymentType.contains("PAYPAL") || paymentType.contains("RAZORPAY")) {
                object.put("payment_id", paymentId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.PAY_NOW_API,
                        object,
                        response -> {
                            utils.print("PayNowRequestResponse", response.toString());
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            flowValue = 6;
                            layoutChanges();
                        }, error -> {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    String json = "";
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 ||
                                    response.statusCode == 405 ||
                                    response.statusCode == 500) {
                                try {
                                    utils.displayMessage(getView(),
                                            errorObj.optString("message"));
                                } catch (Exception e) {
                                    utils.displayMessage(getView(),
                                            getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                refreshAccessToken("PAY_NOW");
                            } else if (response.statusCode == 422) {

                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    utils.displayMessage(getView(), json);
                                } else {
                                    utils.displayMessage(getView(),
                                            getString(R.string.please_try_again));
                                }
                            } else if (response.statusCode == 503) {
                                utils.displayMessage(getView(),
                                        getString(R.string.server_down));
                            } else {
                                utils.displayMessage(getView(),
                                        getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            utils.displayMessage(getView(),
                                    getString(R.string.something_went_wrong));
                        }

                    } else {
                        utils.displayMessage(getView(),
                                getString(R.string.please_try_again));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "" +
                                SharedHelper.getKey(context, "token_type") + " " +
                                SharedHelper.getKey(context, "access_token"));
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        return headers;
                    }
                };

        MyCourier.getInstance().addToRequestQueue(jsonObjectRequest);

    }


    /// repeatedly check the ride status
    private void checkStatus() {
        try {
            utils.print("Handler", "Inside");
            if (isInternet) {
                final JsonObjectRequest jsonObjectRequest = new
                        JsonObjectRequest(Request.Method.GET,
                                URLHelper.REQUEST_STATUS_CHECK_API,
                                null,
                                response -> {
                                    SharedHelper.putKey(context, "req_status", "");
                                    try {
                                        if (customDialog != null && customDialog.isShowing()) {
                                            customDialog.dismiss();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    reqStatus = "";
//                                    Log.e("handleeresponse", response+"");
                                    utils.print("CheckStatusResponse", "" + response.toString());

                                    if (response.optJSONArray("data") != null &&
                                            response.optJSONArray("data").length() > 0) {
                                        utils.print("response", "not null");
                                        try {
                                            JSONArray requestStatusCheck = response.optJSONArray("data");
                                            JSONObject requestStatusCheckObject = requestStatusCheck.getJSONObject(0);
                                            //Driver Detail
                                            if (requestStatusCheckObject.optJSONObject("provider") != null) {

                                                current_chat_requestID = requestStatusCheckObject.optString("id");
                                                currentProviderID = requestStatusCheckObject.optJSONObject("provider").optString("id");
                                                userID = requestStatusCheckObject.optJSONObject("user").optString("id");
                                                driver = new Driver();
                                                providerFirstName = requestStatusCheckObject.optJSONObject("provider").optString("first_name");
                                                driver = new Driver();
                                                driver.setFname(requestStatusCheckObject
                                                        .optJSONObject("provider").optString("first_name"));
                                                driver.setLname(requestStatusCheckObject
                                                        .optJSONObject("provider").optString("last_name"));
                                                driver.setEmail(requestStatusCheckObject
                                                        .optJSONObject("provider").optString("email"));
                                                driver.setMobile(requestStatusCheckObject
                                                        .optJSONObject("provider").optString("mobile"));
                                                driver.setImg(requestStatusCheckObject
                                                        .optJSONObject("provider").optString("avatar"));
                                                driver.setRating(requestStatusCheckObject
                                                        .optJSONObject("provider").optString("rating"));

                                            }

                                            if (requestStatusCheckObject.optJSONObject("item") != null) {
                                                itemObject = requestStatusCheckObject.optJSONObject("item");
                                            }
                                            String status = requestStatusCheckObject.optString("status");
                                            reqStatus = requestStatusCheckObject.optString("status");
                                            SharedHelper.putKey(context, "req_status",
                                                    requestStatusCheckObject.optString("status"));
                                            String wallet = requestStatusCheckObject.optString("use_wallet");
                                            source_lat = requestStatusCheckObject.optString("s_latitude");
                                            source_lng = requestStatusCheckObject.optString("s_longitude");
                                            dest_lat = requestStatusCheckObject.optString("d_latitude");
                                            dest_lng = requestStatusCheckObject.optString("d_longitude");

                                            if (!source_lat.equalsIgnoreCase("") &&
                                                    !source_lng.equalsIgnoreCase("")) {
                                                LatLng myLocation = new LatLng(Double
                                                        .parseDouble(source_lat),
                                                        Double.parseDouble(source_lng));
                                                CameraPosition cameraPosition = new
                                                        CameraPosition.Builder()
                                                        .target(myLocation)
                                                        .zoom(16)
                                                        .build();
                                            }

                                            // surge price
                                            if (requestStatusCheckObject.optString("surge")
                                                    .equalsIgnoreCase("1")) {
                                                lblSurgePrice.setVisibility(View.VISIBLE);
                                            } else {
                                                lblSurgePrice.setVisibility(View.GONE);
                                            }

                                            utils.print("PreviousStatus", "" + PreviousStatus);

                                            if (!PreviousStatus.equals(status)) {
                                                mMap.clear();
                                                PreviousStatus = status;
                                                flowValue = 8;
                                                layoutChanges();
                                                SharedHelper.putKey(context, "request_id", ""
                                                        + requestStatusCheckObject.optString("id"));
                                                reCreateMap();
                                                utils.print("ResponseStatus",
                                                        "SavedCurrentStatus: " +
                                                                CurrentStatus + " Status: " + status);
                                                switch (status) {
                                                    case "SEARCHING":
                                                        show(lnrWaitingForProviders);
                                                        //rippleBackground.startRippleAnimation();
                                                        strTag = "search_completed";
                                                        if (!source_lat.equalsIgnoreCase("")
                                                                && !source_lng.equalsIgnoreCase("")) {
                                                            LatLng myLocation1 = new LatLng(Double.parseDouble(source_lat),
                                                                    Double.parseDouble(source_lng));
                                                            CameraPosition cameraPosition1 = new
                                                                    CameraPosition.Builder()
                                                                    .target(myLocation1).zoom(16).build();
                                                            mMap.moveCamera(CameraUpdateFactory
                                                                    .newCameraPosition(cameraPosition1));
                                                        }
                                                        break;
                                                    case "CANCELLED":
                                                        layoutdriverstatus.setVisibility(View.GONE);
                                                        strTag = "";
                                                        imgSos.setVisibility(View.GONE);
                                                        break;
                                                    case "ACCEPTED":
                                                        driveraccepted.setVisibility(View.VISIBLE);
                                                        driverArrived.setVisibility(View.GONE);
                                                        driverPicked.setVisibility(View.GONE);
                                                        driverCompleted.setVisibility(View.GONE);
                                                        txtdriveraccpted.setText(getString(R.string.arriving));
                                                        imgarrived.setImageResource(R.drawable.arriveddisable);
                                                        imgPicked.setImageResource(R.drawable.pickeddisable);
                                                        layoutdriverstatus.setVisibility(View.VISIBLE);
                                                        strTag = "ride_accepted";
                                                        try {
                                                            JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                            JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                            JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                            JSONObject payment_method = requestStatusCheckObject.getJSONObject("user");
                                                            tvPrice.setText(SharedHelper.getKey(getActivity(), "currency") + " " + SharedHelper.getKey(context, "estimated_fare"));
                                                            tvETA.setText(SharedHelper.getKey(context, "eta_time"));
//                                                            SharedHelper.putKey(context, "estimated_fare", response.optString("estimated_fare"));
//                                                            SharedHelper.putKey(context, "distance", response.optString("distance"));
//                                                            SharedHelper.putKey(context, "eta_time", response.optString("time"));
                                                            tvCash.setText(payment_method.optString("payment_mode"));
//                                                            String d = requestStatusCheckObject.optString("distance");
                                                            String d = SharedHelper.getKey(context, "distance");
                                                            float dist = Float.parseFloat(d);
                                                            tvDistance.setText(new DecimalFormat("##.#").format(dist) + "KM");
//                                                            tvDistance.setText(requestStatusCheckObject.optString("distance"));
                                                            SharedHelper.putKey(context, "provider_mobile_no", "" + provider.optString("mobile"));
                                                            lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                            if (provider.optString("avatar").startsWith("http"))
                                                                Picasso.get()
                                                                        .load(provider.optString("avatar"))
                                                                        .placeholder(R.drawable.user)
                                                                        .error(R.drawable.user)
                                                                        .into(imgProvider);
                                                            else
                                                                Picasso.get()
                                                                        .load(URLHelper.base + "storage/app/public/" + provider.optString("avatar"))
                                                                        .placeholder(R.drawable.user)
                                                                        .error(R.drawable.user).into(imgProvider);
                                                            lblServiceRequested.setText(service_type.optString("name"));
                                                            lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                            Picasso.get().load(service_type.optString("image"))
                                                                    .placeholder(R.drawable.car_select).error(R.drawable.car_select)
                                                                    .into(imgServiceRequested);
                                                            ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                            //lnrAfterAcceptedStatus.setVisibility(View.GONE);

                                                            lblETA.setVisibility(View.VISIBLE);
                                                            AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                            show(lnrProviderAccepted);
                                                            flowValue = 9;
                                                            layoutChanges();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                        break;
                                                    case "STARTED":
                                                        strTag = "ride_started";
                                                        driveraccepted.setVisibility(View.VISIBLE);
                                                        driverArrived.setVisibility(View.GONE);
                                                        driverPicked.setVisibility(View.GONE);
                                                        driverCompleted.setVisibility(View.GONE);
                                                        txtdriveraccpted.setText(getString(R.string.arriving));
                                                        imgarrived.setImageResource(R.drawable.arriveddisable);
                                                        imgPicked.setImageResource(R.drawable.pickeddisable);

                                                        layoutdriverstatus.setVisibility(View.VISIBLE);
                                                        try {
                                                            JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                            JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                            JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                            JSONObject payment_method = requestStatusCheckObject.getJSONObject("user");
                                                            tvCash.setText(payment_method.optString("payment_mode"));
                                                            tvPrice.setText(SharedHelper.getKey(getActivity(), "currency") + " " + SharedHelper.getKey(context, "estimated_fare"));
                                                            tvETA.setText(SharedHelper.getKey(context, "eta_time"));
                                                            String d = requestStatusCheckObject.optString("distance");
                                                            float dist = Float.parseFloat(d);
                                                            tvDistance.setText(new DecimalFormat("##.#").format(dist) + "KM");
//                                                            tvDistance.setText(requestStatusCheckObject.optString("distance"));
                                                            SharedHelper.putKey(context, "provider_mobile_no", "" + provider.optString("mobile"));
                                                            lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                            if (provider.optString("avatar").startsWith("http"))
                                                                Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.user).error(R.drawable.user).into(imgProvider);
                                                            else
                                                                Picasso.get().load(URLHelper.base + "storage/app/public/" + provider.optString("avatar")).placeholder(R.drawable.user).error(R.drawable.user).into(imgProvider);
                                                            lblServiceRequested.setText(service_type.optString("name"));
                                                            lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                            Picasso.get().load(service_type.optString("image")).placeholder(R.drawable.car_select)
                                                                    .error(R.drawable.car_select).into(imgServiceRequested);
                                                            ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                            //lnrAfterAcceptedStatus.setVisibility(View.GONE);

                                                            lblETA.setVisibility(View.VISIBLE);
                                                            AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                            flowValue = 4;
                                                            layoutChanges();
                                                            if (!requestStatusCheckObject.optString("schedule_at").equalsIgnoreCase("null")) {
                                                                SharedHelper.putKey(context, "current_status", "");
                                                                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                                                                intent.putExtra("tag", "upcoming");
                                                                startActivity(intent);
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                        break;

                                                    case "ARRIVED":
                                                        driveraccepted.setVisibility(View.GONE);
                                                        driverArrived.setVisibility(View.VISIBLE);
                                                        driverPicked.setVisibility(View.GONE);
                                                        driverCompleted.setVisibility(View.GONE);
                                                        txtdriverArrived.setText(getString(R.string.arrived));
                                                        imgarrived.setImageResource(R.drawable.arriveddisable);
                                                        imgPicked.setImageResource(R.drawable.pickeddisable);
                                                        imgDropped.setImageResource(R.drawable.complete);
                                                        layoutdriverstatus.setVisibility(View.VISIBLE);
                                                        once = true;
                                                        strTag = "ride_arrived";
                                                        utils.print("MyTest", "ARRIVED");
                                                        try {
                                                            utils.print("MyTest", "ARRIVED TRY");
                                                            JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                            JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                            JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                            JSONObject payment_method = requestStatusCheckObject.getJSONObject("user");
                                                            tvCash.setText(payment_method.optString("payment_mode"));
                                                            tvPrice.setText(SharedHelper.getKey(getActivity(), "currency") + " " + SharedHelper.getKey(context, "estimated_fare"));
                                                            tvETA.setText(SharedHelper.getKey(context, "eta_time"));
                                                            String d = requestStatusCheckObject.optString("distance");
                                                            float dist = Float.parseFloat(d);
                                                            tvDistance.setText(new DecimalFormat("##.#").format(dist) + "KM");
//                                                            tvDistance.setText(requestStatusCheckObject.optString("distance"));
                                                            lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                            if (provider.optString("avatar").startsWith("http"))
                                                                Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.user).error(R.drawable.user).into(imgProvider);
                                                            else
                                                                Picasso.get().load(URLHelper.base + "storage/app/public/" + provider.optString("avatar")).placeholder(R.drawable.user).error(R.drawable.user).into(imgProvider);
                                                            lblServiceRequested.setText(service_type.optString("name"));
                                                            lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                            Picasso.get().load(service_type.optString("image")).placeholder(R.drawable.car_select).error(R.drawable.car_select).into(imgServiceRequested);
                                                            ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                            lnrAfterAcceptedStatus.setVisibility(View.VISIBLE);
                                                            tripLine.setVisibility(View.VISIBLE);

                                                            lblETA.setVisibility(View.GONE);
                                                            AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                            flowValue = 4;
                                                            layoutChanges();
                                                        } catch (Exception e) {
                                                            utils.print("MyTest", "ARRIVED CATCH");
                                                            e.printStackTrace();
                                                        }
                                                        break;

                                                    case "PICKEDUP":
                                                        once = true;
                                                        driveraccepted.setVisibility(View.GONE);
                                                        driverArrived.setVisibility(View.GONE);
                                                        driverPicked.setVisibility(View.VISIBLE);
                                                        driverCompleted.setVisibility(View.GONE);
                                                        txtdriverpicked.setText(getString(R.string.picked_up));
                                                        imgarrived.setImageResource(R.drawable.arriveddisable);
                                                        imgPicked.setImageResource(R.drawable.pickeddisable);
                                                        imgDropped.setImageResource(R.drawable.complete);
                                                        layoutdriverstatus.setVisibility(View.VISIBLE);
                                                        strTag = "ride_picked";
                                                        try {
                                                            JSONObject provider = requestStatusCheckObject.getJSONObject("provider");
                                                            JSONObject service_type = requestStatusCheckObject.getJSONObject("service_type");
                                                            JSONObject provider_service = requestStatusCheckObject.getJSONObject("provider_service");
                                                            JSONObject payment_method = requestStatusCheckObject.getJSONObject("user");
                                                            tvCash.setText(payment_method.optString("payment_mode"));
                                                            tvPrice.setText(SharedHelper.getKey(getActivity(), "currency") + " " + SharedHelper.getKey(context, "estimated_fare"));
                                                            tvETA.setText(SharedHelper.getKey(context, "eta_time"));
                                                            String d = requestStatusCheckObject.optString("distance");
                                                            float dist = Float.parseFloat(d);
                                                            tvDistance.setText(new DecimalFormat("##.#").format(dist) + "KM");
//                                                            tvDistance.setText(requestStatusCheckObject.optString("distance"));
                                                            lblProvider.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                            if (provider.optString("avatar").startsWith("http"))
                                                                Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.user).error(R.drawable.user).into(imgProvider);
                                                            else
                                                                Picasso.get().load(URLHelper.base + "storage/app/public/" + provider.optString("avatar")).placeholder(R.drawable.user).error(R.drawable.user).into(imgProvider);
                                                            lblServiceRequested.setText(service_type.optString("name"));
                                                            lblModelNumber.setText(provider_service.optString("service_model") + "\n" + provider_service.optString("service_number"));
                                                            Picasso.get().load(service_type.optString("image")).placeholder(R.drawable.car_select).error(R.drawable.car_select).into(imgServiceRequested);
                                                            ratingProvider.setRating(Float.parseFloat(provider.optString("rating")));
                                                            lnrAfterAcceptedStatus.setVisibility(View.VISIBLE);
                                                            tripLine.setVisibility(View.VISIBLE);
                                                            imgSos.setVisibility(View.VISIBLE);

                                                            lblETA.setVisibility(View.VISIBLE);
                                                            btnCancelTrip.setText(getString(R.string.share));
                                                            AfterAcceptButtonLayout.setVisibility(View.VISIBLE);
                                                            flowValue = 4;
                                                            layoutChanges();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                        break;

                                                    case "DROPPED":
                                                        once = true;
                                                        strTag = "";
                                                        driveraccepted.setVisibility(View.GONE);
                                                        driverArrived.setVisibility(View.GONE);
                                                        driverPicked.setVisibility(View.GONE);
                                                        driverCompleted.setVisibility(View.VISIBLE);
                                                        txtdrivercompleted.setText("Trip completed");
                                                        imgarrived.setImageResource(R.drawable.arriveddisable);
                                                        imgPicked.setImageResource(R.drawable.pickeddisable);
                                                        imgDropped.setImageResource(R.drawable.complete);
                                                        layoutdriverstatus.setVisibility(View.VISIBLE);
                                                        imgSos.setVisibility(View.VISIBLE);
                                                        //imgShareRide.setVisibility(View.VISIBLE);
                                                        try {
                                                            JSONObject provider = requestStatusCheckObject.optJSONObject("provider");
                                                            if (requestStatusCheckObject.optJSONObject("payment") != null) {
                                                                JSONObject payment = requestStatusCheckObject.optJSONObject("payment");
                                                                isPaid = requestStatusCheckObject.optString("paid");
                                                                paymentMode = requestStatusCheckObject.optString("payment_mode");
                                                                lblBasePrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("fixed"));
                                                                lblTaxPrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("tax"));
                                                                lblDistancePrice.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("distance"));
                                                                //lblCommision.setText(SharedHelper.getKey(context, "currency") + "" + payment.optString("commision"));
                                                                lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                                        + payment.optString("total"));

                                                                promocode_id = payment.optString("promocode_id");
                                                                fixed = payment.optString("fixed");
                                                                commision = payment.optString("commision");
                                                                discount = payment.optString("discount");
                                                                tax = payment.optString("tax");
                                                                surge = payment.optString("surge");
                                                                total = payment.optString("total");

                                                            }
                                                            if (requestStatusCheckObject.optString("booking_id") != null &&
                                                                    !requestStatusCheckObject.optString("booking_id").equalsIgnoreCase("")) {
                                                                booking_id.setText(requestStatusCheckObject.optString("booking_id"));
                                                                tvPaymentLabel.setText(SharedHelper.getKey(getActivity(), "first_name").split(",")[0] + " owes");
                                                            } else {
                                                                booking_id.setVisibility(View.GONE);
                                                            }
                                                            if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")) {
                                                                btnPayNow.setVisibility(View.GONE);
                                                                flowValue = 5;
                                                                layoutChanges();
                                                                imgPaymentTypeInvoice.setImageResource(R.drawable.money1);
                                                                lblPaymentTypeInvoice.setText("CASH");
                                                            } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")
                                                                    && wallet.equalsIgnoreCase("1")) {
                                                                btnPayNow.setVisibility(View.GONE);
                                                                flowValue = 5;
                                                                layoutChanges();
                                                                imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                                lblPaymentTypeInvoice.setText("CASH AND WALLET");
                                                            } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")) {
                                                                btnPayNow.setVisibility(View.VISIBLE);
                                                                flowValue = 5;
                                                                layoutChanges();
                                                                imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                                lblPaymentTypeInvoice.setText("CARD");
                                                            } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("PAYPAL")) {
                                                                btnPayNow.setVisibility(View.VISIBLE);
                                                                flowValue = 5;
                                                                layoutChanges();
                                                                imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                                lblPaymentTypeInvoice.setText("PAYPAL");
                                                            } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("RAZORPAY")) {
                                                                btnPayNow.setVisibility(View.VISIBLE);
                                                                flowValue = 5;
                                                                layoutChanges();
                                                                imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                                lblPaymentTypeInvoice.setText("RAZORPAY");
                                                            } else if (isPaid.equalsIgnoreCase("1")) {
                                                                btnPayNow.setVisibility(View.GONE);
                                                                lblProviderNameRate.setText(getString(R.string.rate_provider) + " " + provider.optString("first_name") + " " + provider.optString("last_name"));
//                                                                lblProviderNameRate1.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                                if (provider.optString("avatar").startsWith("http"))
                                                                    Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.user).into(imgProvider);
                                                                else
                                                                    Picasso.get().load(URLHelper.base + "storage/app/public/" + provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.user).into(imgProvider);
                                                                flowValue = 6;
                                                                layoutChanges();
                                                            }

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                        break;

                                                    case "COMPLETED":
                                                        strTag = "";
                                                        layoutdriverstatus.setVisibility(View.GONE);
                                                        try {
                                                            if (requestStatusCheckObject.optJSONObject("payment") != null) {
                                                                JSONObject payment = requestStatusCheckObject.optJSONObject("payment");
                                                                lblBasePrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                                        + payment.optString("fixed"));
                                                                lblTaxPrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                                        + payment.optString("tax"));
                                                                lblDistancePrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                                        + payment.optString("distance"));
                                                                lblTotalPrice.setText(SharedHelper.getKey(context, "currency") + ""
                                                                        + payment.optString("total"));
                                                            }
                                                            JSONObject provider = requestStatusCheckObject.optJSONObject("provider");
                                                            isPaid = requestStatusCheckObject.optString("paid");
                                                            paymentMode = requestStatusCheckObject.optString("payment_mode");
                                                            imgSos.setVisibility(View.GONE);
                                                            //imgShareRide.setVisibility(View.GONE);
                                                            // lblCommision.setText(payment.optString("commision"));
                                                            if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CASH")) {
                                                                flowValue = 5;
                                                                layoutChanges();
                                                                btnPayNow.setVisibility(View.GONE);
                                                                imgPaymentTypeInvoice.setImageResource(R.drawable.money_icon);
                                                                lblPaymentTypeInvoice.setText("CASH");
                                                            } else if (isPaid.equalsIgnoreCase("0") && paymentMode.equalsIgnoreCase("CARD")) {
                                                                flowValue = 5;
                                                                layoutChanges();
                                                                imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                                lblPaymentTypeInvoice.setText("CARD");
                                                                btnPayNow.setVisibility(View.VISIBLE);
                                                            } else if (isPaid.equalsIgnoreCase("1")) {
                                                                btnPayNow.setVisibility(View.GONE);
                                                                lblProviderNameRate.setText(getString(R.string.rate_provider) + " " + provider.optString("first_name") + " " + provider.optString("last_name"));
//                                                                lblProviderNameRate1.setText(provider.optString("first_name") + " " + provider.optString("last_name"));
                                                                if (provider.optString("avatar").startsWith("http"))
                                                                    Picasso.get().load(provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.user).into(imgProviderRate);
                                                                else
                                                                    Picasso.get().load(URLHelper.base + "storage/app/public/" + provider.optString("avatar")).placeholder(R.drawable.loading).error(R.drawable.user).into(imgProviderRate);
                                                                flowValue = 6;
                                                                layoutChanges();
                                                                //imgPaymentTypeInvoice.setImageResource(R.drawable.visa);
                                                                // lblPaymentTypeInvoice.setText("CARD");
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                        break;
                                                }
                                            }

                                            if ("ACCEPTED".equals(status) || "STARTED".equals(status) ||
                                                    "ARRIVED".equals(status) || "PICKEDUP".equals(status) || "DROPPED".equals(status)) {
                                                utils.print("Livenavigation", "" + status);
                                                utils.print("Destination Current Lat", "" + requestStatusCheckObject.getJSONObject("provider").optString("latitude"));
                                                utils.print("Destination Current Lng", "" + requestStatusCheckObject.getJSONObject("provider").optString("longitude"));
                                                liveNavigation(status, requestStatusCheckObject.getJSONObject("provider").optString("latitude"),
                                                        requestStatusCheckObject.getJSONObject("provider").optString("longitude"));
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Activity activity = getActivity();
                                            if (activity != null && isAdded()) {
                                                utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                                            }
                                        }
                                    } else if (PreviousStatus.equalsIgnoreCase("SEARCHING")) {
                                        SharedHelper.putKey(context, "current_status", "");
                                        if (scheduledDate != null && scheduledTime != null && !scheduledDate.equalsIgnoreCase("")
                                                && !scheduledTime.equalsIgnoreCase("")) {
                                           /* Toast.makeText(context, getString(R.string.schdule_accept), Toast.LENGTH_SHORT).show();
                                            if (scheduleTrip){
                                                Intent intent = new Intent(activity,HistoryActivity.class);
                                                intent.putExtra("tag","upcoming");
                                                startActivity(intent);
                                            }*/
                                        } else
                                            Toast.makeText(context, getString(R.string.no_drivers_found), Toast.LENGTH_SHORT).show();
                                        strTag = "";
                                        PreviousStatus = "";
                                        flowValue = 0;
                                        layoutChanges();
                                        mMap.clear();
                                        mapClear();
                                        mMap.setPadding(0, 0, 0, 0);
                                    } else if (PreviousStatus.equalsIgnoreCase("STARTED")) {
                                        SharedHelper.putKey(getActivity(), "current_status", "");
                                        Toast.makeText(getActivity(), getString(R.string.driver_busy), Toast.LENGTH_SHORT).show();
                                        strTag = "";
                                        PreviousStatus = "";
                                        flowValue = 0;
                                        layoutChanges();
                                        mMap.clear();
                                        mapClear();
                                        mMap.setPadding(0, 0, 0, 0);
                                    } else if (PreviousStatus.equalsIgnoreCase("ARRIVED")) {
                                        SharedHelper.putKey(context, "current_status", "");
                                        Toast.makeText(context, getString(R.string.driver_busy), Toast.LENGTH_SHORT).show();
                                        strTag = "";
                                        PreviousStatus = "";
                                        flowValue = 0;
                                        layoutChanges();
                                        mMap.clear();
                                        mapClear();
                                        mMap.setPadding(0, 0, 0, 0);
                                    }
                                }, error -> {
                            Log.e("handleeError", error + "Error");
                            utils.print("CheckStatusError", error.toString());
                            try {
                                if (customDialog != null && customDialog.isShowing()) {
                                    customDialog.dismiss();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            reqStatus = "";
                            SharedHelper.putKey(context, "req_status", "");
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<>();
                                headers.put("X-Requested-With", "XMLHttpRequest");
                                utils.print("Authorization", "" +
                                        SharedHelper.getKey(context, "token_type")
                                        + " " + SharedHelper.getKey(context, "access_token"));

                                headers.put("Authorization", "" + SharedHelper.getKey(context,
                                        "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                                return headers;
                            }
                        };
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        200000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                MyCourier.getInstance().addToRequestQueue(jsonObjectRequest);

            } else {
                utils.displayMessage(getView(), getString(R.string.oops_connect_your_internet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mapClear() {
        if (parserTask != null)
            parserTask.cancel(true);
        mMap.clear();
        source_lat = "";
        source_lng = "";
        dest_lat = "";
        dest_lng = "";
        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
            LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void reCreateMap() {
        if (mMap != null) {
            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                sourceLatLng = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
            }
            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
            }

            utils.print("LatLng", "Source:" + sourceLatLng + " Destination: " + destLatLng);
            //String url = getDirectionsUrl(sourceLatLng, destLatLng);
            String url = getUrl(sourceLatLng.latitude, sourceLatLng.longitude, destLatLng.latitude, destLatLng.longitude);
            FetchUrl fetchUrl = new FetchUrl();
            fetchUrl.execute(url);
        }
    }

    private void show(final View view) {
        mIsShowing = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(0)
                .setInterpolator(INTERPOLATOR)
                .setDuration(500);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mIsShowing = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Canceling a show should hide the view
                mIsShowing = false;
                if (!mIsHiding) {
                    hide(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }

    private void hide(final View view) {
        mIsHiding = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(view.getHeight())
                .setInterpolator(INTERPOLATOR)
                .setDuration(200);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Prevent drawing the View after it is gone
                mIsHiding = false;
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Canceling a hide should show the view
                mIsHiding = false;
                if (!mIsShowing) {
                    show(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }

    //// for live navigation and check eta
    public void liveNavigation(String status, String lat, String lng) {
        Log.e("Livenavigation", "ProLat" + lat + " ProLng" + lng);
        if (!lat.equalsIgnoreCase("") && !lng.equalsIgnoreCase("")) {
            Double proLat = Double.parseDouble(lat);
            Double proLng = Double.parseDouble(lng);

            Float rotation = 0.0f;

            MarkerOptions markerOptions = new MarkerOptions()
                    .anchor(0.5f, 0.75f)
                    .position(new LatLng(proLat, proLng))
                    .rotation(rotation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_location_icon));

            if (providerMarker != null) {
                rotation = getBearing(providerMarker.getPosition(), markerOptions.getPosition());
                markerOptions.rotation(rotation * (180.0f / (float) Math.PI));
                providerMarker.remove();
                getDriverETA();
            }

            providerMarker = mMap.addMarker(markerOptions);
        }

    }

    public float getBearing(LatLng oldPosition, LatLng newPosition) {
        double deltaLongitude = newPosition.longitude - oldPosition.longitude;
        double deltaLatitude = newPosition.latitude - oldPosition.latitude;
        double angle = (Math.PI * .5f) - Math.atan(deltaLatitude / deltaLongitude);

        if (deltaLongitude > 0) {
            return (float) angle;
        } else if (deltaLongitude < 0) {
            return (float) (angle + Math.PI);
        } else if (deltaLatitude < 0) {
            return (float) Math.PI;
        }

        return 0.0f;
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableLoc();
        }
    }

    private void enableLoc() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d("Location error",
                                "Connected");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(connectionResult -> Log.d("Location error",
                        "Location error " + connectionResult.getErrorCode())).build();
        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            Log.e("GPS Location", "onResult: " + result1);
            Log.e("GPS Location", "onResult Status: " + result1.getStatus());
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    }
                    break;

                case LocationSettingsStatusCodes.CANCELED:
                    showDialogForGPSIntent();
                    break;
            }
        });
//	        }

    }

    public void submitReviewCall() {

        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            if (favourite == true) {
                object.put("request_id", SharedHelper.getKey(context, "request_id"));
                object.put("rating", feedBackRating);
                object.put("user_favorite", "1");
                object.put("comment", "" + txtCommentsRate.getText().toString());
            } else {
                object.put("request_id", SharedHelper.getKey(context, "request_id"));
                object.put("rating", feedBackRating);
                object.put("user_favorite", "0");
                object.put("comment", "" + txtCommentsRate.getText().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.RATE_PROVIDER_API,
                        object,
                        response -> {
                            utils.print("SubmitRequestResponse", response.toString());
                            utils.hideKeypad(context, activity.getCurrentFocus());
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            destination.setText("");
                            frmDest.setText("");
                            mapClear();
                            flowValue = 0;
                            getProvidersList("");
                            layoutChanges();
                            if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                                LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }
                        }, error -> {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {

                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    utils.displayMessage(getView(), errorObj.optString("message"));
                                } catch (Exception e) {
                                    utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                                }

                            } else if (response.statusCode == 401) {
                                refreshAccessToken("SUBMIT_REVIEW");
                            } else if (response.statusCode == 422) {

                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    utils.displayMessage(getView(), json);
                                } else {
                                    utils.displayMessage(getView(), getString(R.string.please_try_again));
                                }
                            } else if (response.statusCode == 503) {
                                utils.displayMessage(getView(), getString(R.string.server_down));
                            } else {
                                utils.displayMessage(getView(), getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                        }

                    } else {
                        utils.displayMessage(getView(), getString(R.string.please_try_again));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") +
                                " " + SharedHelper.getKey(context, "access_token"));
                        return headers;
                    }
                };

        MyCourier.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    //// recycler adapter
    private class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.MyViewHolder> {
        JSONArray jsonArray;
        private SparseBooleanArray selectedItems;
        int selectedPosition;

        public ServiceListAdapter(JSONArray array) {
            this.jsonArray = array;
        }


        @Override
        public ServiceListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.service_type_list_item, null);
            return new ServiceListAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ServiceListAdapter.MyViewHolder holder, final int position) {
            utils.print("Title: ", "" +
                    jsonArray.optJSONObject(position).optString("name") +
                    " Image: " + jsonArray.optJSONObject(position).optString("image") +
                    " Grey_Image:" + jsonArray.optJSONObject(position).optString("grey_image"));

            holder.serviceTitle.setText(jsonArray.optJSONObject(position).optString("name"));
            holder.serviceCapacity.setText(jsonArray.optJSONObject(position).optString("capacity") + "Gm");
            System.out.println("POSITION IS CALLEDD " + position);

            if (position == 0) {
                System.out.println("POSITION IS ssssssssssssssss 0 " +
                        SharedHelper.getKey(context, "estimated_fare"));
                /* holder.serviceItemPrice.setText(SharedHelper.getKey(context, "currency") + "" + SharedHelper.getKey(context, "estimated_fare"));*/
                getNewApproximateFare(String.valueOf(position + 1), holder.serviceItemPrice);
            }
            if (position == 1) {
                System.out.println("POSITION IS ssssssssssssssss 1 " +
                        SharedHelper.getKey(context, "estimated_fare2"));
                /*holder.serviceItemPrice.setText(SharedHelper.getKey(context, "currency") + "" + SharedHelper.getKey(context, "estimated_fare2"));*/
                getNewApproximateFare(String.valueOf(position + 1), holder.serviceItemPrice);
            }
            if (position == 2) {
                System.out.println("POSITION IS ssssssssssssssss 2 " +
                        SharedHelper.getKey(context, "estimated_fare3"));
                /*holder.serviceItemPrice.setText(SharedHelper.getKey(context, "currency") + "" + SharedHelper.getKey(context, "estimated_fare2"));*/
                getNewApproximateFare(String.valueOf(position + 1), holder.serviceItemPrice);
            }
            if (position == 3) {
                System.out.println("POSITION IS ssssssssssssssss 3 " +
                        SharedHelper.getKey(context, "estimated_fare4"));
                /*holder.serviceItemPrice.setText(SharedHelper.getKey(context, "currency") + "" + SharedHelper.getKey(context, "estimated_fare2"));*/
                getNewApproximateFare(String.valueOf(9), holder.serviceItemPrice);
            }

            if (position == currentPostion) {
                SharedHelper.putKey(context, "service_type", "" +
                        jsonArray.optJSONObject(position).optString("id"));
                Glide.with(activity).load(URLHelper.base + jsonArray.optJSONObject(position).optString("image"))
                        .placeholder(R.drawable.packagethumbail)
                        .dontAnimate()
                        .error(R.drawable.packagethumbail)
                        .into(holder.serviceImg);
                holder.selector_background.setBackgroundResource(R.drawable.full_rounded_button);
                holder.serviceTitle.setTextColor(getResources().getColor(R.color.text_color_white));
                Glide.with(activity).load(URLHelper.base + jsonArray.optJSONObject(position).optString("image"))
                        .placeholder(R.drawable.packagethumbail)
                        .dontAnimate()
                        .error(R.drawable.packagethumbail)
                        .into(ImgConfrmCabType);
                service_type = jsonArray.optJSONObject(position).optString("id");
                service = holder.serviceTitle.getText().toString();

            } else {
                Glide.with(activity).load(URLHelper.base + jsonArray.optJSONObject(position).optString("image"))
                        .placeholder(R.drawable.car_select)
                        .dontAnimate()
                        .error(R.drawable.car_select)
                        .into(holder.serviceImg);
                holder.selector_background.setBackgroundColor(getResources()
                        .getColor(R.color.transparent));
                holder.serviceTitle.setTextColor(getResources()
                        .getColor(R.color.white));

            }

            holder.linearLayoutOfList.setTag(position);

            holder.linearLayoutOfList.setOnClickListener(view -> {
                if (position == currentPostion) {
                    Log.e("position", position + "position");
                    try {
                        lnrHidePopup.setVisibility(View.VISIBLE);
                        showProviderPopup(jsonArray.getJSONObject(position));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                currentPostion = Integer.parseInt(view.getTag().toString());
                SharedHelper.putKey(context, "service_type", "" +
                        jsonArray.optJSONObject(Integer.parseInt(view.getTag()
                                .toString())).optString("id"));
                SharedHelper.putKey(context, "name", "" +
                        jsonArray.optJSONObject(currentPostion)
                                .optString("name"));
                notifyDataSetChanged();

                utils.print("service_type", "" +
                        SharedHelper.getKey(context, "service_type"));
                utils.print("Service name", "" +
                        SharedHelper.getKey(context, "name"));
                getProvidersList(SharedHelper.getKey(context, "service_type"));
            });
        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            MyTextView serviceTitle;
            MyTextView serviceItemPrice;
            ImageView serviceImg;
            LinearLayout linearLayoutOfList;
            FrameLayout selector_background;
            TextView serviceCapacity;

            public MyViewHolder(View itemView) {
                super(itemView);
                serviceTitle = itemView.findViewById(R.id.serviceItem);
                serviceImg = itemView.findViewById(R.id.serviceImg);
                linearLayoutOfList = itemView.findViewById(R.id.LinearLayoutOfList);
                selector_background = itemView.findViewById(R.id.selector_background);
                serviceItemPrice = itemView.findViewById(R.id.serviceItemPrice);
                serviceCapacity = itemView.findViewById(R.id.serviceCapacity);
                height = itemView.getHeight();
                width = itemView.getWidth();
            }
        }
    }


    private void startAnim(ArrayList<LatLng> routeList) {
        if (mMap != null && routeList.size() > 1) {
            MapAnimator.getInstance().animateRoute(mMap, routeList);
        } else {
            Toast.makeText(context, "Map not ready", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onDestroy() {
        PreviousStatus="";
        handleCheckStatus.removeCallbacksAndMessages(null);
        if (mapRipple != null && mapRipple.isAnimationRunning()) {
            mapRipple.stopRippleMapAnimation();
        }
        super.onDestroy();
    }

    private void stopAnim() {
        if (mMap != null) {
            MapAnimator.getInstance().stopAnim();
        } else {
            Toast.makeText(context, "Map not ready", Toast.LENGTH_LONG).show();
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.connect_to_wifi), (dialog, id) ->
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest,
                    this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            Log.e("FetchUrl", url + "FetchUrl");
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.e("Background Task data", data.toString());
            } catch (Exception e) {
                Log.e("Background Task", e.toString());
            }
            Log.e("Background data", data + "");
            return data;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            Log.e("FetchUrl_result", result + "result");

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String,
            Integer,
            List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            Log.e("ParserTask_jsonadata", jsonData + "parser");

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.v("ParserTask", "Executing routes");
                Log.v("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.v("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            Log.v("ParserTask_result", result + "result");

            Log.v("result_null", "not_nullnull");

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            String distance = "";
            String duration = "";
            isDragging = false;
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) {
                        duration = (String) point.get("duration");
                        continue;
                    }


                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                if (!source_lat.equalsIgnoreCase("") &&
                        !source_lng.equalsIgnoreCase("")) {
                    LatLng location = new LatLng(Double.parseDouble(source_lat),
                            Double.parseDouble(source_lng));
                    //mMap.clear();
                    if (sourceMarker != null)
                        sourceMarker.remove();
                    MarkerOptions markerOptions = new MarkerOptions()
                            .anchor(0.5f, 0.75f)
                            .position(location).draggable(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_source));
                    marker = mMap.addMarker(markerOptions);
                    sourceMarker = mMap.addMarker(markerOptions);
                }
                if (!dest_lat.equalsIgnoreCase("") &&
                        !dest_lng.equalsIgnoreCase("")) {
                    destLatLng = new LatLng(Double.parseDouble(dest_lat),
                            Double.parseDouble(dest_lng));
                    if (destinationMarker != null)
                        destinationMarker.remove();
                    MarkerOptions destMarker = new MarkerOptions()
                            .position(destLatLng).draggable(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_destination));
                    destinationMarker = mMap.addMarker(destMarker);

                    Display display = getActivity().getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y / 3;

                    System.out.println("HEIGHT IS " + height + "WIDTH IS " + width);
                    mMap.setPadding(100, 100, 100, height + 200);

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(sourceMarker.getPosition());
                    builder.include(destinationMarker.getPosition());
                    LatLngBounds bounds = builder.build();
                    int padding = 0; // offset from edges of the map in pixels
                    bounds.getCenter();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.moveCamera(cu);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(context.getResources().getColor(R.color.colorPrimary));
                Log.v("onPostExecute", "onPostExecute lineoptions decoded");
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null && points != null) {
                //mMap.addPolyline(lineOptions);
                startAnim(points);
            } else {
                Log.v("onPostExecute", "without Polylines drawn");
                callPolylines();
            }
        }
    }

    private void callPolylines() {
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;

        String distance = "";
        String duration = "";
        isDragging = false;

        points = new ArrayList<>();
        lineOptions = new PolylineOptions();

        distance = "";
        duration = "";

        points.add(new LatLng(Double.parseDouble(source_lat),
                Double.parseDouble(source_lng)));
        points.add(new LatLng(Double.parseDouble(dest_lat),
                Double.parseDouble(dest_lng)));

        if (!source_lat.equalsIgnoreCase("") &&
                !source_lng.equalsIgnoreCase("")) {
            LatLng location = new LatLng(Double.parseDouble(source_lat),
                    Double.parseDouble(source_lng));
            //mMap.clear();
            if (sourceMarker != null)
                sourceMarker.remove();
            MarkerOptions markerOptions = new MarkerOptions()
                    .anchor(0.5f, 0.75f)
                    .position(location).draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_source));
            marker = mMap.addMarker(markerOptions);
            sourceMarker = mMap.addMarker(markerOptions);


            //CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(18).build();
            //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        if (!dest_lat.equalsIgnoreCase("") &&
                !dest_lng.equalsIgnoreCase("")) {
            destLatLng = new LatLng(Double.parseDouble(dest_lat),
                    Double.parseDouble(dest_lng));
            if (destinationMarker != null)
                destinationMarker.remove();
            MarkerOptions destMarker = new MarkerOptions()
                    .position(destLatLng).draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_destination));
            destinationMarker = mMap.addMarker(destMarker);

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y / 3;

            System.out.println("HEIGHT IS " + height + "WIDTH IS " + width);
            mMap.setPadding(100, 100, 100, height + 200);


            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(sourceMarker.getPosition());
            builder.include(destinationMarker.getPosition());
            LatLngBounds bounds = builder.build();
            int padding = 0; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.moveCamera(cu);

//            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(5);
            lineOptions.color(Color.BLACK);
            startAnim(points);

            Log.d("onPostExecute", "onPostExecute lineoptions decoded");

        }
    }

    private String getUrl(double source_latitude, double source_longitude,
                          double dest_latitude, double dest_longitude) {

        // Origin of route
        String str_origin = "origin=" + source_latitude + "," + source_longitude;

        // Destination of route
        String str_dest = "destination=" + dest_latitude + "," + dest_longitude;


        // Sensor enabled
        String sensor = "sensor=false" + "&key=" + getActivity().getResources().getString(R.string.google_place_api);

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.v("directionUrl", url + "url");


        return url;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!SharedHelper.getKey(context, "wallet_balance").equalsIgnoreCase("")) {
            wallet_balance = Double.parseDouble(SharedHelper.getKey(context, "wallet_balance"));
        }

        handleCheckStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (helper.isConnectingToInternet()) {
                    if (!isAdded()) {
                        return;
                    }
                    checkStatus();
                    utils.print("Handler", "Called");
                    if (alert != null && alert.isShowing()) {
                        alert.dismiss();
                        alert = null;
                    }
                } else {
                    showDialog();
                }
                handleCheckStatus.postDelayed(this, 10000);
            }
        }, 10000);

        if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
            if (lineView != null && chkWallet != null) {
                lineView.setVisibility(View.VISIBLE);
                chkWallet.setVisibility(View.VISIBLE);
            }
        } else {
            if (lineView != null && chkWallet != null) {
                lineView.setVisibility(View.GONE);
                chkWallet.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void getJSONArrayResult(String strTag, JSONArray response) {
        if (strTag.equalsIgnoreCase("Get Services")) {
            utils.print("GetServices", response.toString());
            if (SharedHelper.getKey(context, "service_type").equalsIgnoreCase("")) {
                SharedHelper.putKey(context, "service_type", "" +
                        response.optJSONObject(0).optString("id"));
            }
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            if (response.length() > 0) {
                currentPostion = 0;
                Log.e("rcvServiceTypesresponse", response + "response");
                ServiceListAdapter serviceListAdapter = new ServiceListAdapter(response);
                rcvServiceTypes.setLayoutManager(new LinearLayoutManager(activity,
                        LinearLayoutManager.HORIZONTAL, false));
                rcvServiceTypes.setAdapter(serviceListAdapter);
                getProvidersList(SharedHelper.getKey(context, "service_type"));
            } else {
                utils.displayMessage(getView(), getString(R.string.no_service));
            }
            mMap.clear();
            setValuesForSourceAndDestination();
        }
    }

//    public void checkIPaddress() {
//        RequestQueue queue = Volley.newRequestQueue(getActivity());
//
//        String urlip = "http://checkip.amazonaws.com/";
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlip, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.e("response", response + "");
//                ip_address = response.trim();
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                checkIPaddress();
//                Log.e("error", error.toString() + "");
//            }
//        });
//
//        queue.add(stringRequest);
//    }
}






