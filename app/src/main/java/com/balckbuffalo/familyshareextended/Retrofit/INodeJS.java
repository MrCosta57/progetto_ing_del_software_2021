package com.balckbuffalo.familyshareextended.Retrofit;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
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

    @POST("users/authenticate/email/")
    @FormUrlEncoded
    Observable<String> loginUser(@Field("email") String email,
                                 @Field("password") String password,
                                 @Field("deviceToken") String deviceToken,
                                 @Field("language") String language,
                                 @Field("origin") String origin);


    /*@GET("user/{username}/{password}")
    @FormUrlEncoded
    Observable<String> Prova(@Header("Authorization") String token,
                             @Path("username") String username,
                             @Path("password") String password);*/

    /*NELLA CLASSE IN CUI DEVI INVIARE IL TOKEN
    * SharedPreferences sharedPreferences = getSharedPreferences("secret_shared_prefs", Context.MODE_PRIVATE);
    * token = sharedPreferences.getString("token");
                                );*/
}
