package com.ros.smartrocket.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.db.entity.WeChatTokenResponse;
import com.ros.smartrocket.db.entity.WeChatUserInfoResponse;
import com.ros.smartrocket.dialog.CustomProgressDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.UIUtils;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler, NetworkOperationListenerInterface {

    public static final String INFO_TAG = "info_tag";
    public static final String WECHAT_TOKEN = "w_token";
    public static final String WECHAT_OPEN_ID = "w_open_id";
    private IWXAPI api;
    private CustomProgressDialog progressDialog;
    private APIFacade apiFacade = APIFacade.getInstance();
    WeChatTokenResponse tokenResponse;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkDeviceSettingsByOnResume(false);
        setContentView(R.layout.activity_launch);
        IWXAPI api = WXAPIFactory.createWXAPI(this, BuildConfig.WECHAT_APP_ID, false);
        api.registerApp(BuildConfig.WECHAT_APP_ID);
        api.handleIntent(getIntent(), this);
        progressDialog = CustomProgressDialog.show(this);
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
                    apiFacade.getWeChatToken(this, response.code);
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

    @Override
    protected void onStop() {
        removeNetworkOperationListener(this);
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        addNetworkOperationListener(this);
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.GET_WECHAT_TOKEN_OPERATION_TAG.equals(operation.getTag())) {
                tokenResponse = (WeChatTokenResponse) operation.getResponseEntities().get(0);
                apiFacade.getWeChatInfo(this, tokenResponse.getAccessToken(), tokenResponse.getOpenId());
            } else if (Keys.GET_WECHAT_INFO_OPERATION_TAG.equals(operation.getTag())) {
                WeChatUserInfoResponse infoResponse = (WeChatUserInfoResponse) operation.getResponseEntities().get(0);
                Intent i = new Intent();
                i.putExtra(INFO_TAG, infoResponse);
                i.putExtra(WECHAT_TOKEN, tokenResponse.getAccessToken());
                i.putExtra(WECHAT_OPEN_ID, tokenResponse.getOpenId());
                sendBroadcast(i.setAction(Keys.WECHAT_AUTH_SUCCESS));
                finish();
            }
        } else if (operation.getResponseErrorCode() != null && operation.getResponseErrorCode()
                == BaseNetworkService.NO_INTERNET) {
            DialogUtils.showBadOrNoInternetDialog(this);
        } else {
            UIUtils.showSimpleToast(this, operation.getResponseError(), Toast.LENGTH_LONG, Gravity.BOTTOM);
        }
    }
}
