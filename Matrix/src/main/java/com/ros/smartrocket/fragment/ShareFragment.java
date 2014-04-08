package com.ros.smartrocket.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

/**
 * Share app info fragment
 */
public class ShareFragment extends Fragment implements OnClickListener {
    private static final String TAG = ShareFragment.class.getSimpleName();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private String shortUrl;
    private String subject;
    private String text;
    private EasyTracker easyTracker;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        ViewGroup view = (ViewGroup) localInflater.inflate(R.layout.fragment_share_and_refer, null);
        easyTracker = EasyTracker.getInstance(getActivity());

        shortUrl = preferencesManager.getShortUrlToShare();
        subject = getString(R.string.app_name);
        text = getString(R.string.app_name);

        view.findViewById(R.id.emailButton).setOnClickListener(this);
        view.findViewById(R.id.messageButton).setOnClickListener(this);
        view.findViewById(R.id.twitterButton).setOnClickListener(this);
        view.findViewById(R.id.facebookButton).setOnClickListener(this);
        view.findViewById(R.id.linkedindButton).setOnClickListener(this);
        view.findViewById(R.id.whatsappButton).setOnClickListener(this);
        view.findViewById(R.id.wechatButton).setOnClickListener(this);
        view.findViewById(R.id.tencentWeiboButton).setOnClickListener(this);
        view.findViewById(R.id.sinaWeiboButton).setOnClickListener(this);
        view.findViewById(R.id.qzoneButton).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        String shareType = "";
        Intent intent = null;
        switch (v.getId()) {
            case R.id.emailButton:
                shareType = "Email";
                intent = IntentUtils.getEmailIntent(subject, "", text + " " + shortUrl);
                break;
            case R.id.messageButton:
                shareType = "SMS";
                intent = IntentUtils.getSmsIntent(getActivity(), "", text + " " + shortUrl);
                break;
            case R.id.facebookButton:
                shareType = "Facebook";
                intent = IntentUtils.getShareFacebookIntent(subject, text + " " + shortUrl);
                break;
            case R.id.twitterButton:
                shareType = "Twitter";
                intent = IntentUtils.getShareTwitterIntent(subject, text + " " + shortUrl);
                break;
            case R.id.linkedindButton:
                shareType = "LinkedIn";
                intent = IntentUtils.getShareLinkedInIntent(subject, text + " " + shortUrl);
                break;
            case R.id.whatsappButton:
                shareType = "WhatsApp";
                intent = IntentUtils.getShareWhatsAppIntent(subject, text + " " + shortUrl);
                break;
            case R.id.wechatButton:
                shareType = "Wechat";
                intent = IntentUtils.getShareWeChatIntent(subject, text + " " + shortUrl);
                break;
            case R.id.tencentWeiboButton:
                shareType = "TencentWeibo";
                intent = IntentUtils.getShareTencentWeiboIntent(subject, text + " " + shortUrl);
                break;
            case R.id.sinaWeiboButton:
                shareType = "SinaWeibo";
                intent = IntentUtils.getShareSinaWeiboIntent(subject, text + " " + shortUrl);
                break;
            case R.id.qzoneButton:
                shareType = "QZone";
                intent = IntentUtils.getShareQZoneIntent(subject, text + " " + shortUrl);
                break;
            default:
                break;
        }

        if (preferencesManager.getUseSocialSharing() && intent != null) {
            if (IntentUtils.isIntentAvailable(getActivity(), intent)) {
                easyTracker.send(MapBuilder.createEvent(TAG, "Share", shareType, null).build());
                getActivity().startActivity(intent);
            } else {
                getActivity().startActivity(IntentUtils.getGooglePlayIntent(intent.getPackage()));
                UIUtils.showSimpleToast(getActivity(), R.string.toast_application_not_found);
            }
        } else {
            UIUtils.showSimpleToast(getActivity(), R.string.toast_sharing_disable_in_settings);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        final ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View view = actionBar.getCustomView();
        ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.share_title);

        super.onCreateOptionsMenu(menu, inflater);
    }
}
