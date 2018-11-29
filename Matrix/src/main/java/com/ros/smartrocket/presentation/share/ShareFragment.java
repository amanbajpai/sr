package com.ros.smartrocket.presentation.share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Sharing;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.presentation.base.BaseFragment;
import com.ros.smartrocket.ui.views.CustomButton;
import com.ros.smartrocket.utils.GoogleUrlShortenManager;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShareFragment extends BaseFragment implements ShareMvpView {
    @BindView(R.id.emailButton)
    CustomButton emailButton;
    @BindView(R.id.facebookButton)
    CustomButton facebookButton;
    @BindView(R.id.linkedinButton)
    CustomButton linkedinButton;
    @BindView(R.id.messageButton)
    CustomButton messageButton;
    @BindView(R.id.sinaWeiboButton)
    CustomButton sinaWeiboButton;
    @BindView(R.id.tencentWeiboButton)
    CustomButton tencentWeiboButton;
    @BindView(R.id.twitterButton)
    CustomButton twitterButton;
    @BindView(R.id.wechatButton)
    CustomButton wechatButton;
    @BindView(R.id.whatsappButton)
    CustomButton whatsappButton;
    @BindView(R.id.qzoneButton)
    CustomButton qzoneButton;
    @BindView(R.id.shareLayout)
    LinearLayout shareLayout;

    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private GoogleUrlShortenManager googleUrlShortenManager = new GoogleUrlShortenManager();
    private String shortUrl;
    private String subject;
    private String text;
    private Sharing sharing;
    private ShareMvpPresenter<ShareMvpView> presenter;

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
        ButterKnife.bind(this, view);
        initSharingData();
        initPresenter();
        return view;
    }

    private void initPresenter() {
        presenter = new SharePresenter<>();
        presenter.attachView(this);
        presenter.getSharingData();
    }

    private void initSharingData() {
        //shortUrl = Config.SHARE_URL;
        subject = getString(R.string.app_name);
        text = getString(R.string.share_text);
    }

    public void showButtons(Integer bitMask) {
        if (bitMask != null) {
            showButtonIfNeed(emailButton, bitMask, SocialNetworks.Email.getId());
            showButtonIfNeed(messageButton, bitMask, SocialNetworks.Message.getId());
            showButtonIfNeed(twitterButton, bitMask, SocialNetworks.Twitter.getId());
            showButtonIfNeed(facebookButton, bitMask, SocialNetworks.Facebook.getId());
            showButtonIfNeed(linkedinButton, bitMask, SocialNetworks.LinkedIn.getId());
            showButtonIfNeed(whatsappButton, bitMask, SocialNetworks.WhatsApp.getId());
            showButtonIfNeed(wechatButton, bitMask, SocialNetworks.WeChat.getId());
            showButtonIfNeed(tencentWeiboButton, bitMask, SocialNetworks.TencentWeibo.getId());
            showButtonIfNeed(sinaWeiboButton, bitMask, SocialNetworks.SinaWeibo.getId());
            showButtonIfNeed(qzoneButton, bitMask, SocialNetworks.Qzone.getId());
        } else {
            shareLayout.setVisibility(View.GONE);
        }
    }

    public void showButtonIfNeed(Button button, int bitMask, int socialId) {
        button.setVisibility((bitMask & socialId) == socialId ? View.VISIBLE : View.GONE);
    }

    public void getShortUrl(String longUrl) {
        googleUrlShortenManager.getShortUrl(longUrl,
                new GoogleUrlShortenManager.OnShortUrlReadyListener() {
                    @Override
                    public void onShortUrlReady(String url) {
                        shortUrl = url;
                        showButtons(sharing.getBitMaskSocialNetwork());
                    }

                    @Override
                    public void onGetShortUrlError(String errorString) {
                        showButtons(sharing.getBitMaskSocialNetwork());
                    }
                }
        );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            View view = actionBar.getCustomView();
            ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.share_title);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @OnClick({R.id.emailButton, R.id.facebookButton, R.id.linkedinButton, R.id.messageButton, R.id.sinaWeiboButton, R.id.tencentWeiboButton, R.id.twitterButton, R.id.wechatButton, R.id.whatsappButton, R.id.qzoneButton})
    public void onViewClicked(View view) {
        Intent intent = null;
        String shareType = "";
        switch (view.getId()) {
            case R.id.emailButton:
                shareType = "Email";
                intent = IntentUtils.getEmailIntent(subject, "", text + " " + shortUrl);
                break;
            case R.id.messageButton:
                shareType = "Sms";
                intent = IntentUtils.getSmsIntent(getActivity(), "", text + " " + shortUrl);
                break;
            case R.id.facebookButton:
                shareType = "Facebook";
                intent = IntentUtils.getShareFacebookIntent(subject, shortUrl);
                break;
            case R.id.twitterButton:
                shareType = "Twitter";
                intent = IntentUtils.getShareTwitterIntent(subject, text + " " + shortUrl);
                break;
            case R.id.linkedinButton:
                shareType = "LinkedIn";
                intent = IntentUtils.getShareLinkedInIntent(subject, text + " " + shortUrl);
                break;
            case R.id.whatsappButton:
                shareType = "WhatsApp";
                intent = IntentUtils.getShareWhatsAppIntent(subject, text + " " + shortUrl);
                break;
            case R.id.wechatButton:
                shareType = "WeChat";
                intent = IntentUtils.getShareWeChatIntent(subject, text + " " + shortUrl);
                break;
            case R.id.tencentWeiboButton:
                shareType = "tencentWeibo";
                intent = IntentUtils.getShareTencentWeiboIntent(subject, text + " " + shortUrl);
                break;
            case R.id.sinaWeiboButton:
                shareType = "sinaWeibo";
                intent = IntentUtils.getShareSinaWeiboIntent(subject, text + " " + shortUrl);
                break;
            case R.id.qzoneButton:
                shareType = "Qzone";
                intent = IntentUtils.getShareQZoneIntent(subject, text + " " + shortUrl);
                break;
            default:
                break;
        }
        if (preferencesManager.getUseSocialSharing() && intent != null) {
            App.getInstance().getDefaultTracker().send(new HitBuilders.EventBuilder()
                    .setCategory("Share")
                    .setAction(shareType)
                    .build());
//            Bundle params = new Bundle();
//            params.putString("category", "Share");
//            params.putString("action", shareType);
//            App.getInstance().mFirebaseAnalytics.logEvent("SmartRocket", params);

            if (IntentUtils.isIntentAvailable(getActivity(), intent)) {
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
    public void showNetworkError(BaseNetworkError networkError) {
        UIUtils.showSimpleToast(getActivity(), networkError.getErrorMessageRes());
    }

    @Override
    public void onSharingLoaded(Sharing sharing) {
        this.sharing = sharing;
        if (sharing != null) {
            if (!TextUtils.isEmpty(sharing.getSharedText()))
                text = sharing.getSharedText();
            if (!TextUtils.isEmpty(sharing.getSharedLink()))
                shortUrl = sharing.getSharedLink();
            showButtons(sharing.getBitMaskSocialNetwork());
//                getShortUrl(sharing.getSharedLink());
        }
    }

    private enum SocialNetworks {
        Email(1),
        Message(2),
        Facebook(4),
        Twitter(8),
        LinkedIn(16),
        WhatsApp(32),
        WeChat(64),
        TencentWeibo(128),
        SinaWeibo(256),
        Qzone(512);

        private int bitMask;

        SocialNetworks(int bitMask) {
            this.bitMask = bitMask;
        }

        public int getId() {
            return bitMask;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.attachView(this);
    }

    @Override
    public void onStop() {
        presenter.detachView();
        super.onStop();
    }
}