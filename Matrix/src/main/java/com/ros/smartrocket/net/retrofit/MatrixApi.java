package com.ros.smartrocket.net.retrofit;

import com.ros.smartrocket.db.entity.account.register.ActivateAccount;
import com.ros.smartrocket.db.entity.account.AliPayAccount;
import com.ros.smartrocket.db.entity.AllowPushNotification;
import com.ros.smartrocket.db.entity.payment.PaymentInfo;
import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.AppVersion;
import com.ros.smartrocket.db.entity.account.CheckEmail;
import com.ros.smartrocket.db.entity.location.CheckLocation;
import com.ros.smartrocket.db.entity.location.CheckLocationResponse;
import com.ros.smartrocket.db.entity.task.ClaimTaskResponse;
import com.ros.smartrocket.db.entity.account.ExternalAuthResponse;
import com.ros.smartrocket.db.entity.account.ExternalAuthorize;
import com.ros.smartrocket.db.entity.file.FileToUploadResponse;
import com.ros.smartrocket.db.entity.account.Login;
import com.ros.smartrocket.db.entity.account.LoginResponse;
import com.ros.smartrocket.db.entity.LongUrl;
import com.ros.smartrocket.db.entity.account.MyAccount;
import com.ros.smartrocket.db.entity.account.NationalIdAccount;
import com.ros.smartrocket.db.entity.payment.PaymentField;
import com.ros.smartrocket.db.entity.question.Questions;
import com.ros.smartrocket.db.entity.account.register.ReferralCases;
import com.ros.smartrocket.db.entity.RegisterDevice;
import com.ros.smartrocket.db.entity.account.register.Registration;
import com.ros.smartrocket.db.entity.account.register.SaveReferralCase;
import com.ros.smartrocket.db.entity.task.SendTaskId;
import com.ros.smartrocket.db.entity.account.SetPassword;
import com.ros.smartrocket.db.entity.Sharing;
import com.ros.smartrocket.db.entity.ShortUrl;
import com.ros.smartrocket.db.entity.Subscription;
import com.ros.smartrocket.db.entity.account.Token;
import com.ros.smartrocket.db.entity.account.UpdateUser;
import com.ros.smartrocket.db.entity.task.Waves;
import com.ros.smartrocket.db.entity.account.WeChatTokenResponse;
import com.ros.smartrocket.db.entity.account.WeChatUserInfoResponse;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface MatrixApi {

    @POST("api/Authorize")
    Observable<LoginResponse> login(@Body Login loginEntity, @Query("language") String language);

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
    Observable<ResponseBody> updateUser(@Body UpdateUser updateUser);

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
    Single<ResponseBody> unClaimTask(@Body SendTaskId sendTaskId);

    @POST("api/Tasks/Validate")
    Single<ResponseBody> validateTask(@Body SendTaskId sendTaskId);

    @Multipart
    @POST("api/Tasks/QuestionFilePlain")
    Observable<FileToUploadResponse> sendFileMultiPart(@Part("fileModel") RequestBody model, @Part MultipartBody.Part file);

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
    Observable<Questions> getReDoQuestions(@Query("missionId") Integer waveId,
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

    @GET("api/PaymentsData/AliPayAccount")
    Single<AliPayAccount> getAliPayAccount();

    @POST("api/PaymentsData/AliPayAccount")
    Single<ResponseBody> integrateAliPayAccount(@Body AliPayAccount aliPayAccount);

    @GET("api/PaymentsData/NationalIdAccount")
    Single<NationalIdAccount> getNationalIdAccount();

    @POST("api/PaymentsData/NationalIdAccount")
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
    Single<ExternalAuthResponse> externalRegistration(@Body ExternalAuthorize externalAuthorizeEntity, @Query("language") String language);

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

    @GET
    Call<ResponseBody> getGeoCoding(@Url String url);

    @POST
    Observable<ShortUrl> getShortUrl(@Url String url, @Body LongUrl longUrl);

    @GET("api/PaymentFields")
    Single<List<PaymentField>> getPaymentFields(@Query("countryId") int countryId, @Query("language") String language);

    @POST("api/SavePaymentFields")
    Single<ResponseBody> savePaymentInfo(@Body List<PaymentInfo> request);

    @Multipart
    @POST("api/PaymentFieldFile")
    Observable<FileToUploadResponse> sendPaymentFile(@Part("fileModel") RequestBody model, @Part MultipartBody.Part file);
}
