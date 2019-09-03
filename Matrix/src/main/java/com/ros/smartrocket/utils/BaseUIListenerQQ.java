package com.ros.smartrocket.utils;

import android.content.Context;
import android.widget.Toast;

import com.ros.smartrocket.R;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

public class BaseUIListenerQQ implements IUiListener {
    private Context context;

    protected BaseUIListenerQQ(Context context) {
        this.context = context;
    }

    @Override
    public void onComplete(Object response) {
        if (null == response) {
            return;
        }
        JSONObject jsonResponse = (JSONObject) response;
        if (jsonResponse.length() == 0) {
            return;
        }
        doComplete(jsonResponse);
    }

    @Override
    public void onError(UiError uiError) {
        Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel() {
        Toast.makeText(context, R.string.cancel, Toast.LENGTH_SHORT).show();
    }

    protected void doComplete(JSONObject json) {
    }
}
