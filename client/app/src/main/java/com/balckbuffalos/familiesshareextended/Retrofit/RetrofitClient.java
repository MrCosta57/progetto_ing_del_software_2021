package com.balckbuffalos.familiesshareextended.Retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

// Official documentation website: https://square.github.io/retrofit/
public class RetrofitClient {
    private static Retrofit instance;

    public static Retrofit getInstance() {
        String IPV4 = "192.168.1.7";
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