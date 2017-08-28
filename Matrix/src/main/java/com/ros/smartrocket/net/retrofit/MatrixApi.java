package com.ros.smartrocket.net.retrofit;

import com.ros.smartrocket.db.entity.ActivateAccount;
import com.ros.smartrocket.db.entity.AliPayAccount;
import com.ros.smartrocket.db.entity.AllowPushNotification;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.AppVersion;
import com.ros.smartrocket.db.entity.CheckEmail;
import com.ros.smartrocket.db.entity.CheckLocation;
import com.ros.smartrocket.db.entity.CheckLocationResponse;
import com.ros.smartrocket.db.entity.ClaimTaskResponse;
import com.ros.smartrocket.db.entity.ExternalAuthResponse;
import com.ros.smartrocket.db.entity.ExternalAuthorize;
import com.ros.smartrocket.db.entity.Login;
import com.ros.smartrocket.db.entity.LoginResponse;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.db.entity.NationalIdAccount;
import com.ros.smartrocket.db.entity.NotUploadedFile;
import com.ros.smartrocket.db.entity.Questions;
import com.ros.smartrocket.db.entity.ReferralCases;
import com.ros.smartrocket.db.entity.RegisterDevice;
import com.ros.smartrocket.db.entity.Registration;
import com.ros.smartrocket.db.entity.RegistrationResponse;
import com.ros.smartrocket.db.entity.SaveReferralCase;
import com.ros.smartrocket.db.entity.SendTaskId;
import com.ros.smartrocket.db.entity.SetPassword;
import com.ros.smartrocket.db.entity.Sharing;
import com.ros.smartrocket.db.entity.Subscription;
import com.ros.smartrocket.db.entity.Token;
import com.ros.smartrocket.db.entity.UpdateUser;
import com.ros.smartrocket.db.entity.Waves;
import com.ros.smartrocket.db.entity.WeChatTokenResponse;
import com.ros.smartrocket.db.entity.WeChatUserInfoResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface MatrixApi {

    @POST("api/Authorize")
    Call<LoginResponse> login(@Body Login loginEntity);

    @POST("api/Authorize/SetTermsAndConditions")
    Call<ResponseBody> sendTandC();

    @GET("api/Authorize/Versioning")
    Call<AppVersion> getAppVersion();

    @GET("api/Authorize/ForgotPassword")
    Call<ResponseBody> forgotPassword(@Query("email") String email, @Query("language") String language);

    @POST("api/Authorize/Activate")
    Call<ResponseBody> activateAccount(@Body ActivateAccount activateAccountEntity);

    @POST("api/Authorize/ChangePassword")
    Call<ResponseBody> setPassword(@Body SetPassword setPasswordEntity);

    @POST("api/Authorize/Register")
    Call<RegistrationResponse> registration(@Body Registration registrationEntity, @Query("language") String language);

    @POST("api/Authorize/UpdateUser")
    Call<ResponseBody> updateUser(@Body UpdateUser updateUser);

    @POST("api/Authorize/PositionCheck")
    Call<CheckLocationResponse> checkLocationForRegistration(@Body CheckLocation checkLocationEntity);

    @GET("api/Authorize/ReferralCases")
    Call<ReferralCases> getReferralCases(@Query("countryId") int countryId, @Query("language") String language);

    @POST("api/Authorize/ReferralCase")
    Call<ResponseBody> saveReferralCases(@Body SaveReferralCase caseEntity);

    @GET("api/Waves")
    Call<Waves> getWaves(@Query("latitude") double latitude,
                         @Query("longitude") double longitude,
                         @Query("radius") int radius,
                         @Query("language") String language);

    @GET("api/Tasks/ByCurrentUser")
    Call<Waves> getMyTasksOperation(@Query("language") String language);

    @POST("api/Tasks/Claim")
    Call<ClaimTaskResponse> claimTask(@Body SendTaskId sendTaskId);

    @POST("api/Tasks/Unclaimed")
    Call<ResponseBody> unclaimTask(@Body SendTaskId sendTaskId);

    @POST("api/Tasks/Validate")
    Call<ResponseBody> validateTaskOperation(@Body SendTaskId sendTaskId);

    @POST("api/Tasks/QuestionFile")
    Call<ResponseBody> sendFile(@Header(RetrofitHolder.CONTENT_TYPE_HEADER) String header, @Body NotUploadedFile notUploadedFile);

    @POST("api/Tasks/Answers")
    Call<ResponseBody> sendAnswers(@Body List<Answer> answers,
                                   @Query("missionId") Integer missionId,
                                   @Query("language") String language);

    @POST("api/Tasks/Start")
    Call<ResponseBody> startTask(@Body SendTaskId sendTaskId);

    @GET("api/Waves/Questionnaire")
    Call<Questions> getQuestions(@Query("waveId") Integer waveId,
                                 @Query("taskId") Integer taskId,
                                 @Query("language") String language);

    @GET("api/Tasks/Re-Do-Questions")
    Call<Questions> getReDoQuestions(@Query("waveId") Integer waveId,
                                     @Query("taskId") Integer taskId,
                                     @Query("language") String language);

    @POST("api/Authorize/RegisterApplicant")
    Call<ResponseBody> subscribe(@Body Subscription subscriptionEntity);

    @POST("api/Authorize/RegisterDevice")
    Call<ResponseBody> registerGCMId(@Body RegisterDevice registerDeviceEntity);

    @GET("api/Authorize/Account")
    Call<MyAccount> getMyAccount(@Query("language") String language);

    @POST("SendActivity")
    Call<ResponseBody> sendActivity();

    @POST("WithdrawMoney")
    Call<ResponseBody> cashingOut();

    @GET("api/Socials/SharingData")
    Call<Sharing> getSharingData(@Query("language") String language);

    @POST("api/Authorize/ReIssueCredentials")
    Call<Token> getNewToken(@Body Token token);

    @GET("api/Payments/AliPayAccount")
    Call<AliPayAccount> getAliPayAccount();

    @POST("api/Payments/AliPayAccount")
    Call<ResponseBody> integrateAliPayAccount(@Body AliPayAccount aliPayAccount);

    @GET("api/Payments/NationalIdAccount")
    Call<NationalIdAccount> getNationalIdAccount();

    @POST("api/Authorize/AllowPushNotification")
    Call<AllowPushNotification> allowPushNotification(@Body AllowPushNotification allowPushNotification);

    @GET("api/Authorize/CheckEmail")
    Call<CheckEmail> checkEmail(@Query("email") String email);

    @POST("api/Authorize/UpdatePromoCode")
    Call<ResponseBody> setPromoCode(@Query("promoCode") String promoCode);

    @POST("api/Authorize/ExternalAuthorize")
    Call<ExternalAuthResponse> externalAuth(@Body ExternalAuthorize externalAuthorizeEntity, @Query("language") String language);

    @POST("api/Authorize/ExternalRegister")
    Call<ResponseBody> externalRegistration(@Body ExternalAuthorize externalAuthorizeEntity, @Query("language") String language);

    @POST("api/Authorize/Terminate")
    Call<ResponseBody> closeAccount();

    @GET
    Call<WeChatTokenResponse> getWeChatToken(@Url String url,
                                             @Query("appid") String appId,
                                             @Query("secret") String secret,
                                             @Query("code") String code,
                                             @Query("grant_type") String grantType);

    @GET
    Call<WeChatUserInfoResponse> getWeChatInfo(@Url String url,
                                               @Query("access_token") String token,
                                               @Query("openid") String openId);

}
