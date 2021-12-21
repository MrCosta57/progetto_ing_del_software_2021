package com.balckbuffalos.familiesshareextended.Retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static Retrofit instance;

    public static Retrofit getInstance() {
        String IPV4 = "157.138.176.149";
        String port="4000";
        if (instance == null)
            instance = new Retrofit.Builder()
                    .baseUrl("http://" + IPV4 + ":"+port+"/api/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

        return instance;
    }
}