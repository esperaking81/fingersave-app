package com.digitalpersona.uareu.UareUSampleJava;

import android.support.annotation.Nullable;

import java.util.HashMap;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.QueryMap;

public class WebService {

    public WebService() {
    }

    private final Retrofit getRetrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://fakeurl.com")
            .build();
    private WebApis webApis = getRetrofit.create(WebApis.class);


    public void post(String url, @Nullable @Body HashMap<String, Object> body,
                     @Nullable @QueryMap HashMap<String, Object> params) {
        webApis.post(url, body, params);
    }

    public void get(String url, @Nullable @Body HashMap<String, Object> body,
                    @Nullable @QueryMap HashMap<String, Object> params) {
        webApis.get(url, body, params);
    }
}
