package com.balckbuffalo.familyshareextended.Retrofit;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface INodeJS {
    @POST("users/")
    @FormUrlEncoded
    Observable<String> registerUser(@Field("given_name") String given_name,
                                    @Field("family_name") String family_name,
                                    @Field("email") String email,
                                    @Field("password") String password,
                                    @Field("visible") Boolean visible,
                                    @Field("language") String language);

    @POST("login")
    @FormUrlEncoded
    Observable<String> loginUser(@Field("email") String email,
                                 @Field("password") String password);
}
