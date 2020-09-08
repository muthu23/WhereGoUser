package com.wherego.user.networkservice.service;



import com.google.gson.GsonBuilder;
import com.wherego.user.BuildConfig;
import com.wherego.user.Helper.URLHelper;
import com.wherego.user.networkservice.helper.AppConstant;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {



    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(AppConstant.LIVE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory
                            .create(new GsonBuilder()
                                    .setLenient()
                                    .serializeNulls()
                                    .create()));

    private static Retrofit.Builder builderPlaces =
            new Retrofit.Builder()
                    .baseUrl(AppConstant.PLACES_BASE_URL)
                    .addConverterFactory(GsonConverterFactory
                            .create(new GsonBuilder()
                                    .setLenient()
                                    .serializeNulls()
                                    .create()));


    private static Retrofit retrofit = builder.build();

    private static Retrofit retrofitPlaces = builderPlaces.build();

    private static HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY);

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();

    public static <S> S createService(Class<S> serviceClass) {
        httpClient.connectTimeout(URLHelper.DEFAULT_CONNECTION_TIME_OUT, TimeUnit.SECONDS);
        httpClient.readTimeout(URLHelper.DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);
        httpClient.writeTimeout(URLHelper.DEFAULT_WRITE_TIME_OUT, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG && !httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
        }
        builder.client(httpClient.build());
        retrofit = builder.build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createServicePlaces(Class<S> serviceClass) {
        httpClient.connectTimeout(URLHelper.DEFAULT_CONNECTION_TIME_OUT, TimeUnit.SECONDS);
        httpClient.readTimeout(URLHelper.DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);
        httpClient.writeTimeout(URLHelper.DEFAULT_WRITE_TIME_OUT, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG && !httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
        }
        builderPlaces.client(httpClient.build());
        retrofitPlaces = builderPlaces.build();
        return retrofitPlaces.create(serviceClass);
    }

}
