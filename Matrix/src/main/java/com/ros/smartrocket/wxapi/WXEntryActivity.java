package com.ros.smartrocket.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import com.ros.smartrocket.App;
import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.account.WeChatTokenResponse;
import com.ros.smartrocket.db.entity.account.WeChatUserInfoResponse;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.UIUtils;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    private static final String GET_WECHAT_TOKEN = "https://api.wechat.com/sns/oauth2/access_token";
    private static final String GET_WECHAT_USER_INFO = "https://api.wechat.com/sns/userinfo";
    public static final String INFO_TAG = "info_tag";
    public static final String WECHAT_TOKEN = "w_token";
    public static final String WECHAT_OPEN_ID = "w_open_id";
    private IWXAPI api;
    WeChatTokenResponse tokenResponse;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkDeviceSettingsByOnResume(false);
        setContentView(R.layout.activity_launch);
        api = WXAPIFactory.createWXAPI(this, BuildConfig.WECHAT_APP_ID, false);
        api.registerApp(BuildConfig.WECHAT_APP_ID);
        api.handleIntent(getIntent(), this);
        showLoading(false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp resp) {
        int result = R.string.success;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (resp instanceof SendAuth.Resp) {
                    SendAuth.Resp response = (SendAuth.Resp) resp;
                    getWeChatToken(response.code);
                } else {
                    finish();
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.cancel;
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.error;
                finish();
                break;
            default:
                result = R.string.error;
                break;
        }
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }

    private void getWeChatToken(String code) {
        Call<WeChatTokenResponse> call = App.getInstance().getApi()
                .getWeChatToken(GET_WECHAT_TOKEN, BuildConfig.WECHAT_APP_ID, BuildConfig.WECHAT_APP_SECRET, code, "authorization_code");
        call.enqueue(new Callback<WeChatTokenResponse>() {
            @Override
            public void onResponse(Call<WeChatTokenResponse> call, Response<WeChatTokenResponse> response) {
                if (response.isSuccessful()) {
                    getWeChatInfo(response.body());
                } else {
                    onNetworkOperationFailed(new NetworkError(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<WeChatTokenResponse> call, Throwable t) {
                onNetworkOperationFailed(new NetworkError(t));
            }
        });
    }

    private void getWeChatInfo(WeChatTokenResponse tokenResponse) {
        Call<WeChatUserInfoResponse> call = App.getInstance().getApi()
                .getWeChatInfo(GET_WECHAT_USER_INFO, tokenResponse.getAccessToken(), tokenResponse.getOpenId());
        call.enqueue(new Callback<WeChatUserInfoResponse>() {
            @Override
            public void onResponse(Call<WeChatUserInfoResponse> call, Response<WeChatUserInfoResponse> response) {
                if (response.isSuccessful()) {
                    handleWeChatInfo(response.body());
                } else {
                    onNetworkOperationFailed(new NetworkError(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<WeChatUserInfoResponse> call, Throwable t) {
                onNetworkOperationFailed(new NetworkError(t));
            }
        });
    }

    private void handleWeChatInfo(WeChatUserInfoResponse infoResponse) {
        Intent i = new Intent();
        i.putExtra(INFO_TAG, infoResponse);
        i.putExtra(WECHAT_TOKEN, tokenResponse.getAccessToken());
        i.putExtra(WECHAT_OPEN_ID, tokenResponse.getOpenId());
        sendBroadcast(i.setAction(Keys.WECHAT_AUTH_SUCCESS));
        finish();
    }

    public void onNetworkOperationFailed(NetworkError error) {
        if (error.getErrorCode() == NetworkError.NO_INTERNET) {
            DialogUtils.showBadOrNoInternetDialog(this);
        } else {
            UIUtils.showSimpleToast(this, error.getErrorMessageRes(), Toast.LENGTH_LONG, Gravity.BOTTOM);
        }
    }
}
