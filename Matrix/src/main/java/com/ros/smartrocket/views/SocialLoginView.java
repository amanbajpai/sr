package com.ros.smartrocket.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.ExternalAuthorize;
import com.ros.smartrocket.interfaces.SocialLoginListener;
import com.ros.smartrocket.utils.UIUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SocialLoginView extends LinearLayout implements GoogleApiClient.OnConnectionFailedListener {
    private static final int G_SIGN_IN = 9001;
    private static final int GOOGLE_ID = 1;
    private static final int FB_ID = 2;
    private static final int WECHAT_ID = 3;
    private static final int QQ_ID = 4;

    @Bind(R.id.container)
    LinearLayout container;
    private AppCompatActivity activity;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager callbackManager;
    private IWXAPI api;
    private SocialLoginListener socialLoginListener;

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

    public void setUpSocialLogins(SocialLoginListener socialLoginListener, AppCompatActivity activity, int externalSource1, int externalSource2) {
        this.activity = activity;
        this.socialLoginListener = socialLoginListener;
        showSocialButton(externalSource1);
        showSocialButton(externalSource2);
        requestLayout();
    }

    private void setUpQQLoginButton() {
        CustomButton qqSignInButton = addSocialButton(R.string.continue_with_qq, R.drawable.ic_qq, R.drawable.button_fb_selector);
        qqSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setUpWeChatLoginButton() {
        CustomButton wechatSignInButton = addSocialButton(R.string.continue_with_wechat, R.drawable.ic_wechat, R.drawable.button_green_selector);
        wechatSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    private void setUpFbLoginButton() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (socialLoginListener != null) {
                    socialLoginListener.onExternalLoginStart();
                }
                getFacebookAccount(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getContext(), R.string.cancel, Toast.LENGTH_SHORT).show();
                if (socialLoginListener != null) {
                    socialLoginListener.onExternalLoginFinished();
                }
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                if (socialLoginListener != null) {
                    socialLoginListener.onExternalLoginFinished();
                }
            }
        });
        CustomButton fbSignInButton = addSocialButton(R.string.continue_with_fb, R.drawable.ic_fb, R.drawable.button_fb_selector);
        fbSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithFb();
            }
        });
    }

    private void getFacebookAccount(final AccessToken token) {
        GraphRequest request = GraphRequest.newMeRequest(
                token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        ExternalAuthorize authorize = new ExternalAuthorize();
                        authorize.setExternalAuthToken(token.getToken());
                        authorize.setExternalAuthSource(FB_ID);
                        try {
                            authorize.setFullName(object.getString("name"));
                            authorize.setGender("male".equals(object.getString("gender")) ? 1 : 2);
                            authorize.setEmail(object.getString("email"));
                        } catch (JSONException e) {
                            Log.e("FB auth", "JSON exception", e);
                        }
                        if (socialLoginListener != null) {
                            socialLoginListener.onExternalLoginSuccess(authorize);
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,gender,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void setUpGoogleSignInBtn() {
        CustomButton gSignInButton = addSocialButton(R.string.continue_with_google, R.drawable.ic_google, R.drawable.button_orange_selector);
        gSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    private void signInWithGoogle() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Toast.makeText(getContext(), "Tratata", Toast.LENGTH_SHORT).show();
                        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                        activity.startActivityForResult(signInIntent, G_SIGN_IN);
                    }
                });
    }

    private void signInWithFb() {
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("email", "public_profile"));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == G_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            ExternalAuthorize authorize = new ExternalAuthorize();
            authorize.setGender(1);
            if (mGoogleApiClient.hasConnectedApi(Plus.API)){
                Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                authorize.setGender(person.getGender() == 0 ? 1 : 2);
            }
            GoogleSignInAccount acct = result.getSignInAccount();
            authorize.setExternalAuthToken(result.getSignInAccount().getIdToken());
            authorize.setExternalAuthSource(GOOGLE_ID);
            authorize.setFullName(acct.getDisplayName());
            authorize.setEmail(acct.getEmail());
            if (socialLoginListener != null) {
                socialLoginListener.onExternalLoginSuccess(authorize);
            }
        } else {
            if (socialLoginListener != null) {
                socialLoginListener.onExternalLoginFinished();
            }
        }
    }

    private CustomButton addSocialButton(int buttonNameRes, int buttonIconnRes, int buttonBgRes) {
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
        socialButton.setCompoundDrawablesWithIntrinsicBounds(buttonIconnRes, 0, 0, 0);
        socialButton.setCompoundDrawablePadding(dp10);
        socialButton.setFont(3);
        socialButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        socialButton.setTextColor(getResources().getColor(R.color.white));
        return socialButton;
    }

}
