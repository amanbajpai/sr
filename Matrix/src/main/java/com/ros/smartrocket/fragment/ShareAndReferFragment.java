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
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

/**
 * Share app info fragment
 */
public class ShareAndReferFragment extends Fragment implements OnClickListener {
    //private static final String TAG = ShareAndReferFragment.class.getSimpleName();
    private ViewGroup view;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private String shortUrl;
    private String subject;
    private String text;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        view = (ViewGroup) localInflater.inflate(R.layout.fragment_share_and_refer, null);

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
        Intent intent = null;
        switch (v.getId()) {
            case R.id.emailButton:
                intent = IntentUtils.getEmailIntent(subject, "", text + " " + shortUrl);
                break;
            case R.id.messageButton:
                intent = IntentUtils.getSmsIntent(getActivity(), "", text + " " + shortUrl);
                break;
            case R.id.facebookButton:
                intent = IntentUtils.getShareFacebookIntent(subject, text + " " + shortUrl);
                break;
            case R.id.twitterButton:
                intent = IntentUtils.getShareTwitterIntent(subject, text + " " + shortUrl);
                break;
            case R.id.linkedindButton:
                intent = IntentUtils.getShareLinkedInIntent(subject, text + " " + shortUrl);
                break;
            case R.id.whatsappButton:
                intent = IntentUtils.getShareWhatsAppIntent(subject, text + " " + shortUrl);
                break;
            case R.id.wechatButton:
                intent = IntentUtils.getShareWeChatIntent(subject, text + " " + shortUrl);
                break;
            case R.id.tencentWeiboButton:
                intent = IntentUtils.getShareTencentWeiboIntent(subject, text + " " + shortUrl);
                break;
            case R.id.sinaWeiboButton:
                intent = IntentUtils.getShareSinaWeiboIntent(subject, text + " " + shortUrl);
                break;
            case R.id.qzoneButton:
                intent = IntentUtils.getShareQZoneIntent(subject, text + " " + shortUrl);
                break;
            default:
                break;
        }

        if (intent != null) {
            if (IntentUtils.isIntentAvailable(getActivity(), intent)) {
                getActivity().startActivity(intent);
            } else {
                getActivity().startActivity(IntentUtils.getGooglePlayIntent(intent.getPackage()));
                UIUtils.showSimpleToast(getActivity(), R.string.toast_application_not_found);
            }
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
        ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.share_and_refer_title);

        super.onCreateOptionsMenu(menu, inflater);
    }
}
