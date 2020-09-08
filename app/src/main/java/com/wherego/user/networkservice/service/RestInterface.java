package com.wherego.user.networkservice.service;



import com.google.gson.JsonElement;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;


public interface RestInterface {

    ///------------------------------ Upload video for provider ------------------------------//

    @Multipart
    @POST("api/user/additem")
    Call<JsonElement> addItem(@Header("X-Requested-With") String requestWith,
                                        @Header("Authorization") String Authorization,
                                        @PartMap() Map<String, RequestBody> partMap,
                                        @Part List<MultipartBody.Part> photo);
    @GET("api/user/request/check")
    Call<ResponseBody> addPickUpNotes(@Header("X-Requested-With") String requestWith,
                                      @Header("Authorization") String Authorization,
                                      @Query("special_note") String pickupNotes,
                                      @Query("request_id") String requestID);

}