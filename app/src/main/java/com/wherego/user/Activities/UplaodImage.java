package com.wherego.user.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonElement;
import com.wherego.user.Adapter.ImageAdapter;
import com.wherego.user.BuildConfig;
import com.wherego.user.Helper.CustomDialog;
import com.wherego.user.Helper.URLHelper;
import com.wherego.user.R;
import com.wherego.user.Helper.BitmapCompletion;
import com.wherego.user.Helper.SharedHelper;
import com.wherego.user.Utils.Utils;
import com.wherego.user.networkservice.service.RestInterface;
import com.wherego.user.networkservice.service.ServiceGenerator;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UplaodImage extends AppCompatActivity implements BitmapCompletion {

    Activity activity;
    private byte[] b;
    ArrayList<byte[]> byteArray;
    private byte[] imageOne;
    private byte[] imageTwo;
    private byte[] imageThree;
    private byte[] imageFour;
    private byte[] imageFive;

    private String nameOne = "";
    private String nameTwo = "";
    private String nameThree = "";
    private String nameFour = "";
    private String nameFive = "";

    ArrayList<String> filename;
    private String fileExtOne = "";
    private String fileExtTwo = "";
    private String fileExtThree = "";
    private String fileExtFour = "";
    private String fileExtFive = "";

    private File file;
    ImageView iv_img, img;
    int count = -1;
    private String first = "",
            boring_depth = "";
    private String fileExt = "";
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int TAKE_PICTURE = 0;
    private static final int GET_PICTURE = 1;
    private Uri cameraImageUri = null;
    private Uri galleryImageUri = null;
    private Bitmap bmp = null;
    AppCompatRadioButton self;
    public static String dateFormat = "yyyyMMdd_HHmmss";
    String service_type;
    RelativeLayout parentLayout;
    public Context context = UplaodImage.this;
    RadioGroup needRadio;
    //    EditText ed_name, ed_quanitity, ed_discription, ed_notesInfo;
    ImageView backArrow;
    TextView txtServiceType, info;
    LinearLayout needLayout;
    String courierType = "Self";
    String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE"};

    EditText ed_name, ed_weight, ed_height, ed_width, ed_quantity, ed_notes,
            ed_receiver_name, ed_receiver_number, ed_receiver_mail, ed_receiver_address, ed_receiver_email;
    ImageView image1, image2, image3, image4, image5;


    static String strSDCardPathName = Environment.getExternalStorageDirectory() + "/MyDelivery" + "/";
    LinearLayout layoutItem;

    TextView tvCourierDetails;
    TextView tvSize;
    TextInputLayout tl_name, tl_quantity, tl_weight;
    String receiverName, receiverMobile, receiverEmail, receiverAddress;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA

    };
    String CourierType = "";
    ConstraintLayout layoutSize;
    RadioGroup radioSign;
    private ArrayList<Image> images = new ArrayList<>();

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat
                .checkSelfPermission(activity,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private RecyclerView recyclerView;
    private ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        verifyStoragePermissions(UplaodImage.this);
        txtServiceType = findViewById(R.id.txtServiceType);
        info = findViewById(R.id.info);
        byteArray = new ArrayList<>();
        filename = new ArrayList<>();

        CourierType = getIntent().getStringExtra("CourierType");
        tvCourierDetails = findViewById(R.id.tvCourierDetails);

        layoutSize = findViewById(R.id.layoutSize);
        ed_name = findViewById(R.id.ed_name);
        ed_weight = findViewById(R.id.ed_weight);
        ed_height = findViewById(R.id.ed_height);
        ed_width = findViewById(R.id.ed_width);
        ed_quantity = findViewById(R.id.ed_quantity);
        ed_notes = findViewById(R.id.ed_notes);
        ed_receiver_name = findViewById(R.id.ed_receiver_name);
        ed_receiver_number = findViewById(R.id.ed_receiver_number);
        ed_receiver_mail = findViewById(R.id.ed_receiver_mail);
        ed_receiver_address = findViewById(R.id.ed_receiver_address);
        needRadio = findViewById(R.id.needRadio);


        iv_img = findViewById(R.id.iv_img);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        image5 = findViewById(R.id.image5);
        needLayout = findViewById(R.id.needLayout);
        layoutItem = findViewById(R.id.layoutItem);

        radioSign = findViewById(R.id.radioSign);
        self = findViewById(R.id.self);
        ed_receiver_email = findViewById(R.id.ed_receiver_email);
        parentLayout = findViewById(R.id.parentLayout);
        service_type = getIntent().getStringExtra("service_type");
        backArrow = findViewById(R.id.backArrow);
        tvSize = findViewById(R.id.tvSize);
        tl_name = findViewById(R.id.tl_name);
        tl_quantity = findViewById(R.id.tl_quantity);
        tl_weight = findViewById(R.id.tl_weight);
        recyclerView = findViewById(R.id.recyclerView);
        backArrow.setOnClickListener(view -> onBackPressed());
        self.setChecked(true);
        tvCourierDetails.setText(CourierType + " Details");
        tl_name.setHint(CourierType + " Name");
        tl_quantity.setHint(CourierType + " Quantity");
        tl_weight.setHint(CourierType + " Weight(GM)");
        tvSize.setText(CourierType + " Size");


        receiverName = SharedHelper.getKey(UplaodImage.this, "first_name");
        receiverMobile = SharedHelper.getKey(UplaodImage.this, "mobile");
        receiverEmail = SharedHelper.getKey(UplaodImage.this, "email");
        receiverAddress = "";

        needRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.self) {
                    courierType = "Self";
                    needLayout.setVisibility(View.GONE);
                    receiverName = SharedHelper.getKey(UplaodImage.this, "first_name");
                    receiverMobile = SharedHelper.getKey(UplaodImage.this, "mobile");
                    receiverEmail = SharedHelper.getKey(UplaodImage.this, "email");
                    receiverAddress = "";
                }
                if (checkedId == R.id.other) {
                    courierType = "Other";
                    needLayout.setVisibility(View.VISIBLE);
                    receiverName = ed_receiver_name.getText().toString();
                    receiverMobile = ed_receiver_number.getText().toString();
                    receiverEmail = ed_receiver_email.getText().toString();
                    receiverAddress = ed_receiver_address.getText().toString();
                }
            }
        });

        adapter = new ImageAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }


    public void onClick(View v) {
        if (v.getId() == R.id.rl_submit) {

            if (ed_name.getText().toString().isEmpty()) {
                tl_name.setError(CourierType + " name is not empty");
            } else if (ed_quantity.getText().toString().isEmpty()) {
                tl_quantity.setError(CourierType + " quantity is not empty");
            } else if (ed_weight.getText().toString().isEmpty()) {
                tl_weight.setError(CourierType + " weight is not empty");
            } else {
                if (courierType == "Self") {
                    sendDataToServer("no", UplaodImage.this);
                } else {
                    if (ed_receiver_name.getText().toString().isEmpty()) {
                        ed_receiver_name.setError("Receiver Name is not empty");
                    } else if (ed_receiver_number.getText().toString().isEmpty()) {
                        ed_receiver_name.setError("Receiver name is not empty");
                    } else if (ed_receiver_email.getText().toString().isEmpty()) {
                        ed_receiver_email.setError("Receiver Email is not empty");
                    } else if (ed_receiver_address.getText().toString().isEmpty()) {
                        ed_receiver_address.setError("Receiver Address is not empty");
                    } else {
                        sendDataToServer("no", UplaodImage.this);
                    }
                }
            }

            //  finish();
        } else if (v.getId() == R.id.rl_cancel) {
            Intent intent = new Intent();
            intent.putExtra("cancel", "previous");
            setResult(RESULT_CANCELED, intent);
            finish();
        } else {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                            5);
                }
            }

            start();
//            Dialog d = ImageChoose();
//            d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
//            d.show();
        }


    }

    private void start() {
//        boolean folderMode = folderModeSwitch.isChecked();
//        boolean multipleMode = multipleModeSwitch.isChecked();
//        boolean cameraOnly = cameraOnlySwitch.isChecked();

        ImagePicker.with(this)
                .setFolderMode(true)
                .setCameraOnly(false)
                .setFolderTitle("Album")
                .setMultipleMode(true)
                .setSelectedImages(images)
                .setMaxSize(10)
                .setBackgroundColor("#02A54F")
                .setAlwaysShowDoneButton(true)
                .setRequestCode(100)
                .setKeepScreenOn(true)
                .start();

    }

    /**
     * Image Chooser Dialog
     *
     * @return Dialog
     */
    private Dialog ImageChoose() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UplaodImage.this);
        if (bmp == null) {
            CharSequence[] ch = {};
            ch = new CharSequence[]{getString(R.string.Gallery), getString(R.string.Camera)};
            builder.setTitle(getString(R.string.choose_image)).setItems(ch,
                    (dialog, which) -> {
                        // TODO Auto-generated method stub
                        switch (which) {
                            case 0:
                                getPhoto();
                                break;
                            case 1:
                                if (ContextCompat.checkSelfPermission(UplaodImage.this,
                                        android.Manifest.permission.CAMERA) ==
                                        PackageManager.PERMISSION_GRANTED) {
                                    takePhoto();
                                } else {
                                    Toast.makeText(UplaodImage.this,
                                            R.string.camera_error_permission,
                                            Toast.LENGTH_LONG).show();
                                }
                                break;
                            default:
                                break;
                        }
                    });
        } else {
            builder.setTitle(getString(R.string.choose_image)).setItems(
                    new CharSequence[]{getString(R.string.Gallery), getString(R.string.Camera)},
                    (dialog, which) -> {
                        // TODO Auto-generated method stub
                        switch (which) {
                            case 0:
                                getPhoto();
                                break;
                            case 1:
                                if (ContextCompat.checkSelfPermission(UplaodImage.this,
                                        android.Manifest.permission.CAMERA) ==
                                        PackageManager.PERMISSION_GRANTED) {
                                    takePhoto();
                                } else {
                                    Toast.makeText(UplaodImage.this,
                                            R.string.camera_error_permission,
                                            Toast.LENGTH_LONG).show();
                                }
                                break;
                            default:
                                break;
                        }
                    });
        }
        return builder.create();
    }

    /**
     * Get from library
     */
    private void getPhoto() {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GET_PICTURE);
    }

    /**
     * Take Photo
     */
    private void takePhoto() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraImageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    /**
     * Create a file Uri for saving an image or video
     */
    private Uri getOutputMediaFileUri(int type) {
        Uri uri = FileProvider.getUriForFile(UplaodImage.this,
                BuildConfig.APPLICATION_ID + ".provider",
                getOutputMediaFile(type));
        return uri;
    }

    /**
     * Create a File for saving an image or video
     */
    @SuppressLint("SimpleDateFormat")
    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "APP");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                return null;
            }
        }
        String timeStamp = new SimpleDateFormat(dateFormat)
                .format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + getString(R.string.IMG) + timeStamp + getString(R.string.PNG));
        } else {
            return null;
        }

        return mediaFile;
    }

    HashMap<String, String> imagePathList = new HashMap<>();
    ArrayList<Uri> imageUriList = new ArrayList<>();
    ArrayList<File> imageFileList = new ArrayList<>();
    ArrayList<byte[]> listOfBytes = new ArrayList<>();
    ArrayList<String> imageNames = new ArrayList<>();


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            adapter.setData(images);
        }


    }

    @Override
    public void onBitmapScaleComplete(Bitmap bmp) {
        int dim = Utils.getSquareCropDimensionForBitmap(bmp);
        this.bmp = ThumbnailUtils.extractThumbnail(bmp, dim, dim);
        if (bmp != null) {
            byteArray.clear();
            if (imageUriList.size() <= 4) {
                for (int i = 0; i < imageUriList.size(); i++) {
                    if (i == 0) {
                        Log.e("0index", i + "");
                        imageOne = Utils.convertBitmapToByte(bmp);

                        byteArray.add(imageOne);
                        Log.d("bmp", String.valueOf(bmp));
                        Log.d("imageOne", String.valueOf(imageOne));
                        try {
                            image1.setImageBitmap(bmp);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
//                        image1.setImageBitmap(bmp);
                    }
                    if (i == 1) {
                        Log.e("1index", i + "");
                        imageTwo = Utils.convertBitmapToByte(bmp);
                        byteArray.add(imageTwo);
                        try {
                            image2.setImageBitmap(bmp);
                        } catch (Exception e1) {
                            e1.printStackTrace();
//                        image2.setImageBitmap(bmp);
                        }
                    }
                    if (i == 2) {
                        Log.e("2index", i + "");
                        imageThree = Utils.convertBitmapToByte(bmp);
                        byteArray.add(imageThree);
                        try {
                            image3.setImageBitmap(bmp);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
//                        image3.setImageBitmap(bmp);
                    }
                    if (i == 3) {
                        imageFour = Utils.convertBitmapToByte(bmp);
                        byteArray.add(imageFour);
                        try {
                            image4.setImageBitmap(bmp);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
//                        image4.setImageBitmap(bmp);
                    }
                    if (i == 4) {
                        imageFive = Utils.convertBitmapToByte(bmp);
                        byteArray.add(imageFive);
                        try {
                            image5.setImageBitmap(bmp);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
//                        image5.setImageBitmap(bmp);
                    }

                }

            }
        }
    }


    public void sendDataToServer(String callLoader, final Activity act) { //?stage={stepId}&id={userId}


        Map<String, RequestBody> params = new HashMap<>();
        params.put("name", createPartFromString(ed_name.getText().toString()));
        params.put("weight", createPartFromString(ed_weight.getText().toString()));
        params.put("height", createPartFromString(ed_height.getText().toString()));
        params.put("width", createPartFromString(ed_width.getText().toString()));
        params.put("qty", createPartFromString(ed_quantity.getText().toString()));
        params.put("special_notes", createPartFromString(ed_notes.getText().toString()));
        params.put("rec_name", createPartFromString(receiverName));
        params.put("rec_mobile", createPartFromString(receiverMobile));
        params.put("rec_email", createPartFromString(receiverEmail));
        params.put("rec_address", createPartFromString(receiverAddress));
        params.put("service_type", createPartFromString(service_type));
        params.put("user_id", createPartFromString(SharedHelper.getKey(getApplicationContext(), "id")));
        params.put("TotalImages", createPartFromString("0"));

        Log.d("uploadParams", String.valueOf(params));
//        NetworkMethodsImpl NetworkCall = new NetworkMethodsImpl();
//        NetworkCall.saveProfileAccount(UplaodImage.this,
//                params,
//                URLHelper.base + "api/user/additem", filename, byteArray);

        uploadVideo(images, params);

    }

    String messageData = "";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void uploadVideo(ArrayList<Image> images, Map<String, RequestBody> params) {
        RestInterface restInterface = ServiceGenerator.createService(RestInterface.class);
        CustomDialog customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        String token = "Bearer " + SharedHelper.getKey(context, "access_token");
        String requestWith = URLHelper.REQUEST_WITH;

        List<MultipartBody.Part> photo = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            photo.add(prepareMulipartVideo(images.get(i).getPath()));
        }
        Call<JsonElement> call = restInterface.addItem(requestWith,
                token,
                params,
                photo);
        call.enqueue(new Callback<JsonElement>() {
            /*   @Override
               public void onResponse(Call<UploadVideoResponse> call,
                                      Response<UploadVideoResponse> response) {
                   Log.d("Ongoing", response.toString());
                   if ((customDialog != null) && customDialog.isShowing())
                       customDialog.dismiss();
               }
   */
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.v("onResponse UploadVideo", response.toString());
                if ((customDialog != null) && customDialog.isShowing())
                    customDialog.dismiss();

                String resultResponse = new String(response.body().toString());
                Log.e("resultResponse", resultResponse.toString());
                try {
                    JSONObject jsonObject = new JSONObject(resultResponse);

                    // {"error":"no","success":"yes","item_id":19,"message":"Insert Items"}
                    messageData = jsonObject.optString("success");
                    Log.v("test", jsonObject.toString());

                    if (jsonObject.optString("success").equalsIgnoreCase("yes")) {

                        item_id = jsonObject.optString("item_id");
                        handler_.sendEmptyMessage(0);
                    } else {
                        handler_.sendEmptyMessage(1);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // pd_loading.dismiss();
                }
                //Log.d("Ongoing", response.toString());
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                if ((customDialog != null) && customDialog.isShowing())
                    customDialog.dismiss();
                Log.v("onFailure()==", "UploadVideo onFailure" + t.getMessage());
            }
        });
    }


    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    private MultipartBody.Part prepareMulipartVideo(String path) {
        File file = new File(path);
        int videoSize = Integer.parseInt(String.valueOf(file.length() / 1024));
        Log.d("imageSize", String.valueOf(videoSize));
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData("image[]", file.getName(), requestFile);
    }

    String item_id = "";
    private final Handler handler_ = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Intent intent = new Intent();
                intent.putExtra("ok", "next");
                intent.putExtra("item_id", item_id);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    };
}
