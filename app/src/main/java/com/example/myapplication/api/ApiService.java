package com.example.myapplication.api;

import com.example.myapplication.model.DescribePost;
import com.example.myapplication.model.StreamingPost;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    //http://192.168.1.3:8000
    //http://42.114.82.85:8000

    public static final String DOMAIN = "http://1.53.102.32:8000/";

    Gson gson = new GsonBuilder().setDateFormat("yyyy MM dd HH:mm:ss").create();

    ApiService API_SERVICE = new Retrofit.Builder()
            .baseUrl(DOMAIN)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @POST("streaming")
    Call<String> Streaming(@Body StreamingPost file);

    @POST("describe")
    Call<String> Describe(@Body DescribePost file);
}

