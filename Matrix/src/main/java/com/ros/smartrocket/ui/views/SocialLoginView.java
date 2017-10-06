package com.ros.smartrocket.ui.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.AdditionalAuthClaim;
import com.ros.smartrocket.db.entity.ExternalAuthorize;
import com.ros.smartrocket.db.entity.WeChatUserInfoResponse;
import com.ros.smartrocket.interfaces.SocialLoginListener;
import com.ros.smartrocket.utils.BaseUIListenerQQ;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.wxapi.WXEntryActivity;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SocialLoginView extends LinearLayout implements GoogleApiClient.OnConnectionFailedListener {
    private static final int G_SIGN_IN_CODE = 9001;
    public static final int WECHAT_SIGN_IN_CODE = 9002;
    private static final int GOOGLE_ID = 1;
    private static final int FB_ID = 2;
    private static final int WECHAT_ID = 3;
    private static final int QQ_ID = 4;
    public static final String QQ_NICKNAME = "nickname";
    public static final String GENDER = "gender";
    public static final String ALL = "all";
    public static final String NAME = "name";
    public static final String MALE = "male";
    public static final String EMAIL = "email";

    @BindView(R.id.container)
    LinearLayout container;
    private AppCompatActivity activity;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager fbCallbackManager;
    private SocialLoginListener socialLoginListener;
    public static Tencent qqApi;
    private IUiListener qqLoginListener;

    public SocialLoginView(Context context) {
        super(context);
        init();
    }

    public SocialLoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SocialLoginView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void showSocialButton(int socialId) {
        switch (socialId) {
            case GOOGLE_ID:
                setUpGoogleSignInBtn();
                break;
            case FB_ID:
                setUpFbLoginButton();
                break;
            case WECHAT_ID:
                setUpWeChatLoginButton();
                break;
            case QQ_ID:
                setUpQQLoginButton();
                break;
            default:
                // do nothing
                break;
        }
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_social_login, this, true);
        ButterKnife.bind(this);
    }

    public void setUpSocialLoginButtons(SocialLoginListener socialLoginListener, AppCompatActivity activity, int externalSource1, int externalSource2) {
        this.activity = activity;
        this.socialLoginListener = socialLoginListener;
        container.removeAllViews();
        showSocialButton(externalSource1);
        showSocialButton(externalSource2);
        requestLayout();
    }

    //-----------QQ------------//

    private void setUpQQLoginButton() {
        CustomButton qqSignInButton = addSocialButton(R.string.continue_with_qq, R.drawable.ic_qq, R.drawable.button_fb_selector);
        qqLoginListener = new BaseUIListenerQQ(activity) {
            @Override
            public void doComplete(JSONObject response) {
                handleQQResponse(response);
            }
        };
        qqApi = Tencent.createInstance(Keys.QQ_APP_ID, activity);
        qqSignInButton.setOnClickListener(v -> qqApi.login(activity, ALL, qqLoginListener));
    }

    public void handleQQResponse(JSONObject jsonObject) {
        if (jsonObject.has(Constants.PARAM_ACCESS_TOKEN)) {
            handleQQAuth(jsonObject);
        } else if (jsonObject.has(QQ_NICKNAME)) {
            handleQQUserInfo(jsonObject);
        }
    }

    private void handleQQAuth(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                qqApi.setAccessToken(token, expires);
                qqApi.setOpenId(openId);
                socialLoginListener.onExternalLoginStart();
                UserInfo userInfo = new UserInfo(activity, qqApi.getQQToken());
                userInfo.getUserInfo(qqLoginListener);
            }
        } catch (Exception e) {
            Log.v("Exception", "SocialLoginView", e);
        }
    }

    private void handleQQUserInfo(JSONObject jsonObject) {
        ExternalAuthorize authorize = new ExternalAuthorize();
        authorize.setExternalAuthToken(qqApi.getAccessToken());
        authorize.setExternalAuthSource(QQ_ID);
        try {
            authorize.setFullName(jsonObject.getString(QQ_NICKNAME));
            authorize.setGender("男".equals(jsonObject.getString(GENDER)) ? 1 : 2);
            onLoginSuccess(authorize);
        } catch (JSONException e) {
            Log.e("QQ auth", "JSON exception", e);
        }
    }

    //-----------WC------------//

    private void setUpWeChatLoginButton() {
        CustomButton weChatSignInButton = addSocialButton(R.string.continue_with_wechat, R.drawable.ic_wechat, R.drawable.button_green_selector);
        weChatSignInButton.setOnClickListener(v -> {
            IWXAPI api = WXAPIFactory.createWXAPI(getContext(), BuildConfig.WECHAT_APP_ID, false);
            api.registerApp(BuildConfig.WECHAT_APP_ID);
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_smart_rocket";
            api.sendReq(req);
        });
    }

    private void handleWeChatUserInfo(WeChatUserInfoResponse info, String token, String openId) {
        ExternalAuthorize authorize = new ExternalAuthorize();
        authorize.setExternalAuthSource(WECHAT_ID);
        authorize.setExternalAuthToken(token);
        authorize.setAdditionalAuthClaims(new AdditionalAuthClaim(openId));
        authorize.setGender(info.getSex());
        onLoginSuccess(authorize);
    }

    //-----------FB------------//

    private void setUpFbLoginButton() {
        fbCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                onLoginStart();
                getFacebookAccount(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getContext(), R.string.cancel, Toast.LENGTH_SHORT).show();
                onLoginFinished();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                onLoginFinished();
            }
        });
        CustomButton fbSignInButton = addSocialButton(R.string.continue_with_fb, R.drawable.ic_fb, R.drawable.button_fb_selector);
        fbSignInButton.setOnClickListener(v -> signInWithFb());
    }

    private void getFacebookAccount(final AccessToken token) {
        GraphRequest request = GraphRequest.newMeRequest(
                token,
                (object, response) -> handleFBAuth(token, object));
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,gender,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void handleFBAuth(AccessToken token, JSONObject object) {
        ExternalAuthorize authorize = new ExternalAuthorize();
        authorize.setExternalAuthToken(token.getToken());
        authorize.setExternalAuthSource(FB_ID);
        try {
            authorize.setFullName(object.getString(NAME));
            authorize.setGender(MALE.equals(object.getString(GENDER)) ? 1 : 2);
            authorize.setEmail(object.getString(EMAIL));
        } catch (JSONException e) {
            Log.e("FB auth", "JSON exception", e);
        }
        onLoginSuccess(authorize);
    }

    private void signInWithFb() {
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("email", "public_profile"));
    }

    //-----------GOOGLE------------//

    private void setUpGoogleSignInBtn() {
        CustomButton gSignInButton = addSocialButton(R.string.continue_with_google, R.drawable.ic_google, R.drawable.button_orange_selector);
        gSignInButton.setOnClickListener(v -> signInWithGoogle());
        if (mGoogleApiClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                    .requestProfile()
                    .requestIdToken("318949058113-59oe5om5c2496k08vsfesna671rtvn1n.apps.googleusercontent.com")
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .enableAutoManage(activity, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .addApi(Plus.API)
                    .build();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void signInWithGoogle() {
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    status -> {
                        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                        activity.startActivityForResult(signInIntent, G_SIGN_IN_CODE);
                    });
        }
    }


    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            ExternalAuthorize authorize = new ExternalAuthorize();
            if (mGoogleApiClient.hasConnectedApi(Plus.API)) {
                Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                authorize.setGender(person.getGender() == 0 ? 1 : 2);
                String birthday = person.getBirthday();
                if (!TextUtils.isEmpty(birthday)) {
                    authorize.setBirthday(UIUtils.googleProfileDateToString(birthday));
                }
            }
            GoogleSignInAccount acct = result.getSignInAccount();
            authorize.setExternalAuthToken(result.getSignInAccount().getIdToken());
            authorize.setExternalAuthSource(GOOGLE_ID);
            authorize.setFullName(acct.getDisplayName());
            authorize.setEmail(acct.getEmail());
            onLoginSuccess(authorize);
        } else {
            onLoginFinished();
        }
    }

    //-----------OTHER------------//

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case G_SIGN_IN_CODE:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleGoogleSignInResult(result);
                break;
            case Constants.REQUEST_LOGIN:
            case Constants.REQUEST_APPBAR:
                Tencent.onActivityResultData(requestCode, resultCode, data, qqLoginListener);
                break;
            case WECHAT_SIGN_IN_CODE:
                WeChatUserInfoResponse response = (WeChatUserInfoResponse) data.getSerializableExtra(WXEntryActivity.INFO_TAG);
                String token = data.getStringExtra(WXEntryActivity.WECHAT_TOKEN);
                String openId = data.getStringExtra(WXEntryActivity.WECHAT_OPEN_ID);
                handleWeChatUserInfo(response, token, openId);
                break;
            default:
                fbCallbackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }


    }

    private CustomButton addSocialButton(int buttonNameRes, int buttonIconRes, int buttonBgRes) {
        CustomButton socialButton = new CustomButton(getContext(), null, R.style.Button);
        int dp10 = UIUtils.getPxFromDp(getContext(), 10);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dp10, 0, 0);
        socialButton.setLayoutParams(lp);
        socialButton.setText(buttonNameRes);
        socialButton.setAllCaps(true);
        container.addView(socialButton);
        socialButton.setBackgroundResource(buttonBgRes);
        socialButton.setPadding(dp10, dp10, dp10, dp10);
        socialButton.setGravity(Gravity.CENTER_VERTICAL);
        socialButton.setShadowLayer(1, 0, 1, R.color.grey);
        socialButton.setMinHeight(UIUtils.getPxFromDp(getContext(), 48));
        LocaleUtils.setCompoundDrawable(socialButton, buttonIconRes);
        socialButton.setCompoundDrawablePadding(dp10);
        socialButton.setFont(3);
        socialButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        socialButton.setTextColor(getResources().getColor(R.color.white));
        return socialButton;
    }

    private void onLoginStart() {
        if (socialLoginListener != null) {
            socialLoginListener.onExternalLoginStart();
        }
    }

    private void onLoginFinished() {
        if (socialLoginListener != null) {
            socialLoginListener.onExternalLoginFinished();
        }
    }

    private void onLoginSuccess(ExternalAuthorize authorize) {
        if (socialLoginListener != null) {
            socialLoginListener.onExternalLoginSuccess(authorize);
        }
    }

    public void onDestroy() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(activity);
            mGoogleApiClient.disconnect();
        }
    }
}
