package com.balckbuffalo.familyshareextended.Retrofit;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface INodeJS {
    @POST("users/")
    @FormUrlEncoded
    Observable<String> signUpUser(@Field("given_name") String given_name,
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

    @GET("users/{id}/groups")
    Observable<String> groupList(@Header("Authorization") String token,
                                 @Path("id") String id,
                                 @Query("user_id") String user_id);

    @GET("groups/{id}/activities")
    Observable<String> activityList(@Header("Authorization") String token,
                                 @Path("id") String group_id,
                                 @Query("user_id") String user_id);


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
