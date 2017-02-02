package com.ros.smartrocket.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.BaseActivity;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkDeviceSettingsByOnResume(false);
        setContentView(R.layout.activity_launch);
        api = WXAPIFactory.createWXAPI(this, Keys.WECHAT_APP_ID, false);
        api.registerApp(Keys.WECHAT_APP_ID);
        api.handleIntent(getIntent(), this);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_smart_rocket";
        api.sendReq(req);
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
        String result = "";

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "Yes baby";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "Cancel baby";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "Error baby (";
                break;
            default:
                result = "HZ baby";
                break;
        }

        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }
}
