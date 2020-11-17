package com.digitalpersona.uareu.UareUSampleJava;

import android.support.annotation.Nullable;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface WebApis {

    @POST
    @FormUrlEncoded
    public Call<Object> post(
            @Url String url,
            @Nullable @Body HashMap<String, Object> body,
            @Nullable @QueryMap HashMap<String, Object> params
    );

    @GET
    @FormUrlEncoded
    public Call<Object> get(
            @Url String url,
            @Nullable @Body HashMap<String, Object> body,
            @Nullable @QueryMap HashMap<String, Object> params
    );
}
