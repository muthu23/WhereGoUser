package com.wherego.delivery.user.Activities;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;
import com.molpay.molpayxdk.MOLPayActivity;
import com.wherego.delivery.user.BuildConfig;
import com.wherego.delivery.user.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.wherego.delivery.user.Helper.CustomDialog;
import com.wherego.delivery.user.Helper.SharedHelper;
import com.wherego.delivery.user.Helper.URLHelper;
import com.wherego.delivery.user.Models.CardInfo;
import com.wherego.delivery.user.MyCourier;
import com.wherego.delivery.user.Utils.MyBoldTextView;
import com.wherego.delivery.user.Utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class ActivityWallet extends AppCompatActivity implements View.OnClickListener {

    private final int ADD_CARD_CODE = 435;

    private Button add_fund_button;
    private ProgressDialog loadingDialog;
    private CardView wallet_card, add_money_card;

    private Button add_money_button;
    private EditText money_et;
    private MyBoldTextView balance_tv;
    private String session_token;
    private Button one, two, three;
    private double update_amount = 0;
    private ArrayList<CardInfo> cardInfoArrayList;
    private String currency = "";
    private CustomDialog customDialog;
    private Context context;
    private TextView currencySymbol;
    ImageView backArrow;

    Utilities utils = new Utilities();
    private CardInfo cardInfo;

    boolean loading;
    final CharSequence[] items = {"Store", "Online Banking"};
    String item_choose = "Store";
    int status_code = 00;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cardInfoArrayList = new ArrayList<>();
        add_fund_button = (Button) findViewById(R.id.add_fund_button);
        wallet_card = (CardView) findViewById(R.id.wallet_card);
        add_money_card = (CardView) findViewById(R.id.add_money_card);
        balance_tv = (MyBoldTextView) findViewById(R.id.balance_tv);
        currencySymbol = (TextView) findViewById(R.id.currencySymbol);
        backArrow=(ImageView)findViewById(R.id.backArrow);
        context = this;
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);

        currencySymbol.setText(SharedHelper.getKey(context, "currency"));
        money_et = (EditText) findViewById(R.id.money_et);
        one = (Button) findViewById(R.id.one);
        two = (Button) findViewById(R.id.two);
        three = (Button) findViewById(R.id.three);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        backArrow.setOnClickListener(this);
        one.setText(SharedHelper.getKey(context, "currency") + "5");
        two.setText(SharedHelper.getKey(context, "currency") + "10");
        three.setText(SharedHelper.getKey(context, "currency") + "20");

        money_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.toString().length() == 0)
                    add_fund_button.setVisibility(View.GONE);
                else add_fund_button.setVisibility(View.VISIBLE);
                if (count == 1 || count == 0) {
                   /* one.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                    two.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                    three.setBackground(getResources().getDrawable(R.drawable.border_stroke));*/
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        add_fund_button.setOnClickListener(this);
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage("Please wait...");

        session_token = SharedHelper.getKey(this, "access_token");

        wallet_card.setVisibility(View.VISIBLE);
        add_money_card.setVisibility(View.VISIBLE);

        getBalance();
        getCards(false);
    }

    private void getBalance() {
        if ((customDialog != null))
            customDialog.show();
        Ion.with(this)
                .load(URLHelper.getUserProfileUrl)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(ActivityWallet.this, "token_type") + " " + session_token)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        // response contains both the headers and the string result
                        if ((customDialog != null) && customDialog.isShowing())
                            customDialog.dismiss();
                        if (e != null) {
                            if (e instanceof TimeoutException) {
                                displayMessage(getString(R.string.please_try_again));
                            }
                            if (e instanceof NetworkErrorException) {
                                getBalance();
                            }
                            return;
                        }
                        if (response != null) {
                            if (response.getHeaders().code() == 200) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.getResult());
                                    currency = jsonObject.optString("currency");
                                    balance_tv.setText(jsonObject.optString("currency") + jsonObject.optString("wallet_balance"));
                                    SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                if ((customDialog != null) && customDialog.isShowing())
                                    customDialog.dismiss();
                                if (response.getHeaders().code() == 401) {
                                    refreshAccessToken("GET_BALANCE");
                                }
                            }
                        } else {

                        }
                    }
                });
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                utils.print("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                if (tag.equalsIgnoreCase("GET_BALANCE")) {
                    getBalance();
                } else if (tag.equalsIgnoreCase("GET_CARDS")) {
                    getCards(loading);
                } else {
                    addMoney(cardInfo);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = "";
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                    utils.GoToBeginActivity(ActivityWallet.this);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void getCards(final boolean showLoading) {
        loading = showLoading;
        if (loading) {
            if (customDialog != null)
                customDialog.show();
        }
        Ion.with(this)
                .load(URLHelper.CARD_PAYMENT_LIST)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(ActivityWallet.this, "token_type") + " " + session_token)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        // response contains both the headers and the string result
                        if (response != null) {
                            if (showLoading) {
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
                            }
                            if (e != null) {
                                if (e instanceof TimeoutException) {
                                    displayMessage(getString(R.string.please_try_again));
                                }
                                if (e instanceof NetworkErrorException) {
                                    getCards(showLoading);
                                }
                                return;
                            }
                            if (response.getHeaders().code() == 200) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response.getResult());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject cardObj = jsonArray.getJSONObject(i);
                                        CardInfo cardInfo = new CardInfo();
                                        cardInfo.setCardId(cardObj.optString("card_id"));
                                        cardInfo.setCardType(cardObj.optString("brand"));
                                        cardInfo.setLastFour(cardObj.optString("last_four"));
                                        cardInfoArrayList.add(cardInfo);
                                    }

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                if (response.getHeaders().code() == 401) {
                                    refreshAccessToken("GET_CARDS");
                                }
                            }
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_fund_button:
                if (money_et.getText().toString().isEmpty()) {
                    update_amount = 0;
                    Toast.makeText(this, "Enter an amount greater than 0", Toast.LENGTH_SHORT).show();
                } else {
                   /* update_amount = Double.parseDouble(money_et.getText().toString());
                    //  payByPayPal(update_amount);
                    if (cardInfoArrayList.size() > 0) {
                        showChooser();
                    } else {
                        gotoAddCard();
                    }*/

                    Showdialog();
                }
                break;

            case R.id.one:

                two.setTextColor(getResources().getColor(R.color.black_text_color));
                three.setTextColor(getResources().getColor(R.color.black_text_color));
                one.setTextColor(getResources().getColor(R.color.white));

                one.setBackground(getResources().getDrawable(R.drawable.border_stroke_black));
                two.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                three.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                money_et.setText("5");
                break;
            case R.id.two:

                one.setTextColor(getResources().getColor(R.color.black_text_color));
                three.setTextColor(getResources().getColor(R.color.black_text_color));
                two.setTextColor(getResources().getColor(R.color.white));

                one.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                two.setBackground(getResources().getDrawable(R.drawable.border_stroke_black));
                three.setBackground(getResources().getDrawable(R.drawable.border_stroke));

                money_et.setText("10");
                break;
            case R.id.three:
                one.setTextColor(getResources().getColor(R.color.black_text_color));
                two.setTextColor(getResources().getColor(R.color.black_text_color));
                three.setTextColor(getResources().getColor(R.color.white));

                one.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                two.setBackground(getResources().getDrawable(R.drawable.border_stroke));
                three.setBackground(getResources().getDrawable(R.drawable.border_stroke_black));
                money_et.setText("20");
                break;
            case R.id.backArrow:
                finish();
                break;
        }
    }

    private void gotoAddCard() {
        Intent mainIntent = new Intent(this, AddCard.class);
        startActivityForResult(mainIntent, ADD_CARD_CODE);
    }

    void Showdialog() {

       AlertDialog.Builder builder = new AlertDialog.Builder(ActivityWallet.this);
        builder.setTitle("Top up through");
        builder.setSingleChoiceItems(items, 0, (dialog, item) -> item_choose = items[item] + "");
        builder.setPositiveButton("Yes",
                (dialog, id) -> {
                       goToMOLPAY(money_et.getText().toString().trim());
                    //AddMoney("00", "2.00", "425809363");

                });
        builder.setNegativeButton("No",
                (dialog, id) -> {
                });
        AlertDialog alert = builder.create();
        alert.show();

        Button buttonbackground = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonbackground.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

        Button buttonbackground1 = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonbackground1.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));


    }


    private void goToMOLPAY(String amount) {

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());

        HashMap<String, Object> paymentDetails = new HashMap<>();
        paymentDetails.put(MOLPayActivity.mp_amount, amount);
        paymentDetails.put(MOLPayActivity.mp_username, BuildConfig.mp_username);
        paymentDetails.put(MOLPayActivity.mp_merchant_ID, BuildConfig.mp_merchant_ID);
        paymentDetails.put(MOLPayActivity.mp_app_name, BuildConfig.mp_app_name);
        paymentDetails.put(MOLPayActivity.mp_password, BuildConfig.mp_password);
        paymentDetails.put(MOLPayActivity.mp_order_ID, "wgdu-"+timeStamp+"-"+SharedHelper.getKey(ActivityWallet.this, "id"));
        paymentDetails.put(MOLPayActivity.mp_currency, "MYR");
        paymentDetails.put(MOLPayActivity.mp_country, BuildConfig.mp_country);
        paymentDetails.put(MOLPayActivity.mp_verification_key, BuildConfig.mp_verification_key);

        if (item_choose.equalsIgnoreCase("Store")) {
            paymentDetails.put(MOLPayActivity.mp_channel, "cash");

            String allowedchannels[] = {"cash"};
            paymentDetails.put(MOLPayActivity.mp_allowed_channels, allowedchannels);

        } else if (item_choose.equalsIgnoreCase("DebitCard")) {
            paymentDetails.put(MOLPayActivity.mp_channel, "multi");
        } else if (item_choose.equalsIgnoreCase("Online Banking")) {
            paymentDetails.put(MOLPayActivity.mp_channel, "fpx");
            String allowedchannels[] = {"fpx"};
            paymentDetails.put(MOLPayActivity.mp_allowed_channels, allowedchannels);
        }


        paymentDetails.put(MOLPayActivity.mp_channel_editing, true);
        Date currentTime = Calendar.getInstance().getTime();

        paymentDetails.put(MOLPayActivity.mp_bill_description, currentTime + "-" + SharedHelper.getKey(getApplicationContext(), "email"));
        paymentDetails.put(MOLPayActivity.mp_bill_name, SharedHelper.getKey(ActivityWallet.this, "first_name") + " " + SharedHelper.getKey(ActivityWallet.this, "last_name"));
        paymentDetails.put(MOLPayActivity.mp_bill_email, SharedHelper.getKey(ActivityWallet.this, "email"));
        paymentDetails.put(MOLPayActivity.mp_bill_mobile, SharedHelper.getKey(ActivityWallet.this, "mobile"));
        paymentDetails.put(MOLPayActivity.mp_request_type, "");
        paymentDetails.put(MOLPayActivity.mp_sandbox_mode, false);


        Intent intent = new Intent(ActivityWallet.this, MOLPayActivity.class);
        intent.putExtra(MOLPayActivity.MOLPayPaymentDetails, paymentDetails);
        startActivityForResult(intent, MOLPayActivity.MOLPayXDK);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CARD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("isAdded", false);
                if (result) {
                    getCards(true);
                }
            }
        }
        if (requestCode == MOLPayActivity.MOLPayXDK && resultCode == RESULT_OK) {
            Log.d(MOLPayActivity.MOLPAY, "MOLPay result = " + data.getStringExtra(MOLPayActivity.MOLPayTransactionResult));
            JSONObject MolPay_Response;
            try {
                MolPay_Response = new JSONObject(data.getStringExtra(MOLPayActivity.MOLPayTransactionResult));
                if (MolPay_Response.optString("status_code").equalsIgnoreCase("00")) {
                    AddMoney(MolPay_Response.optString("status_code"), MolPay_Response.optString("amount"), MolPay_Response.optString("txn_ID"));
                } else if (MolPay_Response.optString("status_code").equalsIgnoreCase("22")) {
                    status_code = 22;
                    Toast.makeText(getApplicationContext(), MolPay_Response.getJSONArray("notes") + "", Toast.LENGTH_LONG).show();
                    AddMoney(MolPay_Response.optString("status_code"), MolPay_Response.optString("amount"), MolPay_Response.optString("txn_ID"));
                } else {
                    Toast.makeText(getApplicationContext(), MolPay_Response.optString("error_message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }

        }
    }

    private void showChooser() {

        final String[] cardsList = new String[cardInfoArrayList.size()];

        for (int i = 0; i < cardInfoArrayList.size(); i++) {
            cardsList[i] = "XXXX-XXXX-XXXX-" + cardInfoArrayList.get(i).getLastFour();
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle("Add money using");
        builderSingle.setSingleChoiceItems(cardsList, 0, null);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.custom_tv);

        for (int j = 0; j < cardInfoArrayList.size(); j++) {
            String card = "";
            card = "XXXX-XXXX-XXXX-" + cardInfoArrayList.get(j).getLastFour();
            arrayAdapter.add(card);
        }
        builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                Log.e("Items clicked===>", "" + selectedPosition);
                cardInfo = cardInfoArrayList.get(selectedPosition);
                addMoney(cardInfoArrayList.get(selectedPosition));
            }
        });
        builderSingle.setNegativeButton(
                "cancel",
                (dialog, which) -> dialog.dismiss());
//        builderSingle.setAdapter(
//                arrayAdapter,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        addMoney(cardInfoArrayList.get(which));
//                    }
//                });
        builderSingle.show();
    }

    private void AddMoney(String status_code, String amount, String txn_id) {

        JsonObject json = new JsonObject();
        json.addProperty("status", status_code);
        json.addProperty("amount", amount);
        json.addProperty("tranID", txn_id);

        Ion.with(this)
                .load(URLHelper.addCardUrl)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(ActivityWallet.this, "token_type") + " " + session_token)
                .setJsonObjectBody(json)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        // response contains both the headers and the string result

                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();

                        if (e != null) {
                            if (e instanceof TimeoutException) {
                                displayMessage(getString(R.string.please_try_again));
                            }
                            if (e instanceof NetworkErrorException) {
                              // addMoney(cardInfo);
                            }
                            return;
                        }

                        if (response.getHeaders().code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.getResult());
                                Toast.makeText(ActivityWallet.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                                JSONObject userObj = jsonObject.getJSONObject("user");
                                balance_tv.setText(currency + userObj.optString("wallet_balance"));
                                SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                                money_et.setText("");
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            try {
                                if (response != null && response.getHeaders() != null) {
                                    if (response.getHeaders().code() == 401) {
                                        refreshAccessToken("ADD_MONEY");
                                    }
                                }
                            } catch (Exception exception) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

    }


    private void addMoney(final CardInfo cardInfo) {
        if (customDialog != null)
            customDialog.show();

        JsonObject json = new JsonObject();
        json.addProperty("card_id", cardInfo.getCardId());
        json.addProperty("amount", money_et.getText().toString());

        Ion.with(this)
                .load(URLHelper.addCardUrl)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(ActivityWallet.this, "token_type") + " " + session_token)
                .setJsonObjectBody(json)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        // response contains both the headers and the string result

                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();

                        if (e != null) {
                            if (e instanceof TimeoutException) {
                                displayMessage(getString(R.string.please_try_again));
                            }
                            if (e instanceof NetworkErrorException) {
                                addMoney(cardInfo);
                            }
                            return;
                        }

                        if (response.getHeaders().code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.getResult());
                                Toast.makeText(ActivityWallet.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                                JSONObject userObj = jsonObject.getJSONObject("user");
                                balance_tv.setText(currency + userObj.optString("wallet_balance"));
                                SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
                                money_et.setText("");
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            try {
                                if (response != null && response.getHeaders() != null) {
                                    if (response.getHeaders().code() == 401) {
                                        refreshAccessToken("ADD_MONEY");
                                    }
                                }
                            } catch (Exception exception) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

}
