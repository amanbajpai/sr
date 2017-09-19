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

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface MatrixApi {

    @POST("api/Authorize")
    Observable<LoginResponse> login(@Body Login loginEntity);

    @POST("api/Authorize/SetTermsAndConditions")
    Single<ResponseBody> sendTandC();

    @GET("api/Authorize/Versioning")
    Single<AppVersion> getAppVersion();

    @GET("api/Authorize/ForgotPassword")
    Single<ResponseBody> forgotPassword(@Query("email") String email, @Query("language") String language);

    @POST("api/Authorize/Activate")
    Single<ResponseBody> activateAccount(@Body ActivateAccount activateAccountEntity);

    @POST("api/Authorize/ChangePassword")
    Single<ResponseBody> setPassword(@Body SetPassword setPasswordEntity);

    @POST("api/Authorize/Register")
    Single<ResponseBody> registration(@Body Registration registrationEntity, @Query("language") String language);

    @POST("api/Authorize/UpdateUser")
    Single<ResponseBody> updateUser(@Body UpdateUser updateUser);

    @POST("api/Authorize/PositionCheck")
    Single<CheckLocationResponse> checkLocationForRegistration(@Body CheckLocation checkLocationEntity);

    @GET("api/Authorize/ReferralCases")
    Single<ReferralCases> getReferralCases(@Query("countryId") int countryId, @Query("language") String language);

    @POST("api/Authorize/ReferralCase")
    Single<ResponseBody> saveReferralCases(@Body SaveReferralCase caseEntity);

    @GET("api/Waves")
    Observable<Waves> getWaves(@Query("latitude") double latitude,
                         @Query("longitude") double longitude,
                         @Query("radius") int radius,
                         @Query("language") String language);

    @GET("api/Tasks/ByCurrentUser")
    Observable<Waves> getMyTasks(@Query("language") String language);

    @POST("api/Tasks/Claim")
    Observable<ClaimTaskResponse> claimTask(@Body SendTaskId sendTaskId);

    @POST("api/Tasks/Unclaimed")
    Single<ResponseBody> unclaimTask(@Body SendTaskId sendTaskId);

    @POST("api/Tasks/Validate")
    Single<ResponseBody> validateTask(@Body SendTaskId sendTaskId);

    @POST("api/Tasks/QuestionFile")
    Single<ResponseBody> sendFile(@Header(RetrofitHolder.CONTENT_TYPE_HEADER) String header, @Body NotUploadedFile notUploadedFile);

    @POST("api/Tasks/Answers")
    Single<ResponseBody> sendAnswers(@Body List<Answer> answers,
                                   @Query("missionId") Integer missionId,
                                   @Query("language") String language);

    @POST("api/Tasks/Start")
    Single<ResponseBody> startTask(@Body SendTaskId sendTaskId);

    @GET("api/Waves/Questionnaire")
    Observable<Questions> getQuestions(@Query("waveId") Integer waveId,
                                 @Query("taskId") Integer taskId,
                                 @Query("language") String language);

    @GET("api/Tasks/Re-Do-Questions")
    Observable<Questions> getReDoQuestions(@Query("waveId") Integer waveId,
                                     @Query("taskId") Integer taskId,
                                     @Query("language") String language);

    @POST("api/Authorize/RegisterApplicant")
    Single<ResponseBody> subscribe(@Body Subscription subscriptionEntity);

    @POST("api/Authorize/RegisterDevice")
    Single<ResponseBody> registerGCMId(@Body RegisterDevice registerDeviceEntity);

    @GET("api/Authorize/Account")
    Observable<MyAccount> getMyAccount(@Query("language") String language);

    @POST("SendActivity")
    Single<ResponseBody> sendActivity(@Query("language") String language);

    @POST("WithdrawMoney")
    Single<ResponseBody> cashingOut();

    @GET("api/Socials/SharingData")
    Single<Sharing> getSharingData(@Query("language") String language);

    @POST("api/Authorize/ReIssueCredentials")
    Single<Token> getNewToken(@Body Token token);

    @GET("api/Payments/AliPayAccount")
    Single<AliPayAccount> getAliPayAccount();

    @POST("api/Payments/AliPayAccount")
    Single<ResponseBody> integrateAliPayAccount(@Body AliPayAccount aliPayAccount);

    @GET("api/Payments/NationalIdAccount")
    Single<NationalIdAccount> getNationalIdAccount();

    @POST("api/Payments/NationalIdAccount")
    Single<ResponseBody> integrateNationalPayAccount(@Body NationalIdAccount account);

    @POST("api/Authorize/AllowPushNotification")
    Single<ResponseBody> allowPushNotification(@Body AllowPushNotification allowPushNotification);

    @GET("api/Authorize/CheckEmail")
    Single<CheckEmail> checkEmail(@Query("email") String email);

    @POST("api/Authorize/UpdatePromoCode")
    Single<ResponseBody> setPromoCode(@Query("promoCode") String promoCode);

    @POST("api/Authorize/ExternalAuthorize")
    Single<Response<ExternalAuthResponse>> externalAuth(@Body ExternalAuthorize externalAuthorizeEntity, @Query("language") String language);

    @POST("api/Authorize/ExternalRegister")
    Single<ResponseBody> externalRegistration(@Body ExternalAuthorize externalAuthorizeEntity, @Query("language") String language);

    @POST("api/Authorize/Terminate")
    Single<ResponseBody> closeAccount();

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
