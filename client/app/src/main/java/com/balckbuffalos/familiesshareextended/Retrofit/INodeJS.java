package com.balckbuffalos.familiesshareextended.Retrofit;

import androidx.annotation.ColorInt;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.time.LocalTime;
import java.util.Date;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

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

    @GET("profiles/")
    Observable<String> profilesInfo(@Header("Authorization") String token,
                                   @Query("searchBy") String search_By,
                                   @Query("ids") String[] ids,
                                   @Query("visible") Boolean visible);

    @GET("users/{id}/groups")
    Observable<String> groupList(@Header("Authorization") String token,
                                 @Path("id") String id,
                                 @Query("user_id") String user_id);

    @GET("groups/{id}/settings")
    Observable<String> groupSettings(@Header("Authorization") String token,
                                 @Path("id") String id,
                                 @Query("user_id") String user_id);


    @POST("profiles/change_greenpass_available")
    @FormUrlEncoded
    Observable<String> changeGreenpassState(@Header("Authorization") String token,
                                            @Field("user_id") String user_id,
                                            @Query("greenpass_available") Boolean greenpass_available);


    @POST("profiles/change_is_positive_state")
    @FormUrlEncoded
    Observable<String> changePositivity(@Header("Authorization") String token,
                                        @Field("user_id") String user_id,
                                        @Query("is_positive") Boolean is_positive);



    @POST("groups/{id}/activities")
    @FormUrlEncoded
    Observable<String> createActivity(@Header("Authorization") String token,
                                      @Path("id") String group_id,
                                      @Query("user_id") String user_id,
                                      @Field("activity") JSONObject activity,
                                      @Field("events") JSONArray events);

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

    @GET("groups/{group_id}/activities/{activity_id}")
    Observable<String> activityInfo(@Header("Authorization") String token,
                                    @Path("group_id") String group_id,
                                    @Path("activity_id") String activity_id,
                                    @Query("user_id") String user_id);

    @PATCH("groups/{id}")
    @FormUrlEncoded
    Observable<String> editGroup(@Header("Authorization") String token,
                                 @Path("id") String group_id,
                                 @Query("user_id") String user_id,
                                 @Field("visible") Boolean visible,
                                 @Field("name") String name,
                                 @Field("description") String description,
                                 @Field("location") String location,
                                 @Field("background") String background,
                                 @Field("contact_type") String contact_type);

    @PATCH("groups/{group_id}/activities/{activity_id}/timeslots/{timeslot_id}")
    @FormUrlEncoded
    Observable<String> editPartecipants(@Header("Authorization") String token,
                                        @Path("group_id") String group_id,
                                        @Path("activity_id") String activity_id,
                                        @Path("timeslot_id") String timeslot_id,
                                        @Query("user_id") String user_id,
                                        @Field("adminChanges") Boolean adminChanges,
                                        @Field("summary") String summary,
                                        @Field("description") String description,
                                        @Field("location") String location,
                                        @Field("start") String start,
                                        @Field("end") String end,
                                        @Field("extendedProperties") JSONObject extendedProperties,
                                        @Field("notifyUsers") Boolean notifyUsers);

    @POST("groups/")
    @FormUrlEncoded
    Observable<String> createGroup(@Header("Authorization") String token,
                                   @Query("user_id") String user_id,
                                   @Field("invite_ids") String[] invite_ids,
                                   @Field("location") String location,
                                   @Field("owner_id") String owner_id,
                                   @Field("contact_type") String contact_type,
                                   @Field("contact_info") String contact_info,
                                   @Field("visible") Boolean visible,
                                   @Field("name") String name,
                                   @Field("description") String description);

    @GET("users/{id}/children")
    Observable<String> getChildren(@Header("Authorization") String token,
                                   @Path("id") String id,
                                   @Query("user_id") String user_id);

    @GET("groups/{groupId}/activities/{activityId}/timeslots")
    Observable<String> timeslotsActivity(@Header("Authorization") String token,
                                         @Path("groupId") String group_id,
                                         @Path("activityId") String activity_id,
                                         @Query("user_id") String user_id);
    @GET("cabinet/{id}")
    Observable<String> listFiles(@Header("Authorization") String token,
                                 @Path("id") String id,
                                 @Query("user_id") String user_id);

    @GET("cabinet/{group_id}/{file_id}")
    @Streaming
    Observable<Response<ResponseBody>> getFile(@Header("Authorization") String token,
                                                @Path("group_id") String group_id,
                                                @Path("file_id") String file_id,
                                                @Query("user_id") String user_id);

    @Multipart
    @POST("cabinet/{id}")
    Observable<String> addFile(@Header("Authorization") String token,
                               @Path("id") String id,
                               @Query("user_id") String user_id,
                               @Query("description") String description,
                               @Part MultipartBody.Part file);

    @POST("cabinet/{id}/read_notifications")
    Observable<String> readNotifications(@Header("Authorization") String token,
                                         @Path("id") String id,
                                         @Query("user_id") String user_id);

    @DELETE("cabinet/{group_id}/{file_id}")
    Observable<String> deleteFile(@Header("Authorization") String token,
                                               @Path("group_id") String group_id,
                                               @Path("file_id") String file_id,
                                               @Query("user_id") String user_id);

    @DELETE("groups/{group_id}/activities/{activity_id}")
    Observable<String> deleteActivity(@Header("Authorization") String token,
                                  @Path("group_id") String group_id,
                                  @Path("activity_id") String activity_id,
                                  @Query("user_id") String user_id);

    @DELETE("groups/{group_id}")
    Observable<String> deleteGroup(@Header("Authorization") String token,
                                      @Path("group_id") String group_id,
                                      @Query("user_id") String user_id);
}
