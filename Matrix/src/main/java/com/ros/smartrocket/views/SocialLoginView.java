package com.ros.smartrocket.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
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
import com.google.android.gms.auth.GoogleAuthUtil;
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
import com.ros.smartrocket.Config;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.ExternalAuthorize;
import com.tencent.mm.sdk.openapi.IWXAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SocialLoginView extends LinearLayout implements GoogleApiClient.OnConnectionFailedListener {
    private static final int G_SIGN_IN = 9001;
    @Bind(R.id.fb_login_btn)
    CustomButton fbLoginBtn;
    @Bind(R.id.google_sign_in_btn)
    CustomButton googleSignInBtn;
    private AppCompatActivity activity;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager callbackManager;
    private IWXAPI api;

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

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_social_login, this, true);
        ButterKnife.bind(this);
    }

    public void setUpSocialLogins(AppCompatActivity activity) {
        this.activity = activity;
        if (Config.USE_BAIDU) {
            // TODO setUp chinese login
        } else {
            setUpFbLoginButton();
            setUpGoogleSignInBtn();
        }
    }

    private void setUpFbLoginButton() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getFacebookAccount(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getContext(), "Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
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
                        authorize.setExternalAuthToken(token.toString());
                        authorize.setExternalAuthSource(0);
                        try {
                            authorize.setFullName(object.getString("name"));
                            authorize.setGender("male".equals(object.getString("gender")) ? 1 : 2);
                        } catch (JSONException e) {
                            Log.e("FB auth", "JSON exception", e);
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void setUpGoogleSignInBtn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
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
        googleSignInBtn.setVisibility(GONE);
    }


    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activity.startActivityForResult(signInIntent, G_SIGN_IN);
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
            // Signed in successfully
            Toast.makeText(getContext(), "Success. Google is good guy!", Toast.LENGTH_SHORT).show();
            GoogleSignInAccount acct = result.getSignInAccount();
            ExternalAuthorize authorize = new ExternalAuthorize();
            authorize.setExternalAuthToken(result.getSignInAccount().getIdToken());
            authorize.setExternalAuthSource(1);
            authorize.setFullName(acct.getDisplayName());
        } else {
            // Signed out
        }
    }

    @OnClick({R.id.google_sign_in_btn, R.id.fb_login_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.google_sign_in_btn:
                signInWithGoogle();
                break;
            case R.id.fb_login_btn:
                signInWithFb();
                break;
        }
    }
}
