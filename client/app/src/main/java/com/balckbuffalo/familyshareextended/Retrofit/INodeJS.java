package com.balckbuffalo.familyshareextended.Retrofit;

import java.util.Date;

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
    Observable<String> authenticateUser(@Field("email") String email,
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

    @POST("users/{id}/children")
    @FormUrlEncoded
    Observable<String> insertChild(@Header("Authorization") String token,
                                   @Path("id") String id,
                                   @Query("user_id") String user_id,
                                   @Field("birthdate") Date date,
                                   @Field("given_name") String name,
                                   @Field("family_name") String surname,
                                   @Field("gender") String gender,
                                   @Field("allergies") String allergies,
                                   @Field("other_info") String other_info,
                                   @Field("special_needs") String special_needs,
                                   @Field("background") String background,
                                   @Field("imagePath") String image_path);

    @GET("groups/{id}/members")
    Observable<String> membersList(@Header("Authorization") String token,
                                    @Path("id") String group_id,
                                    @Query("user_id") String user_id);

    @GET("groups/{id}")
    Observable<String> groupInfo(@Header("Authorization") String token,
                                   @Path("id") String group_id,
                                   @Query("user_id") String user_id);


    @GET("/{groupId}/activities/{activityId}/timeslots")
    Observable<String> timeslotsActivity(@Header("Authorization") String token,
                                 @Path("groupId") String group_id,
                                 @Path("activityId") String activity_id,
                                 @Query("user_id") String user_id);
}
