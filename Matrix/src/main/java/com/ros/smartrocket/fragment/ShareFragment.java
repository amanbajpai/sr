package com.ros.smartrocket.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.activity.ShareActivity;
import com.ros.smartrocket.db.entity.Sharing;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.GoogleUrlShortenManager;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.views.CustomButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Share app info fragment
 */
public class ShareFragment extends Fragment implements NetworkOperationListenerInterface {
    private static final String TAG = ShareFragment.class.getSimpleName();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private GoogleUrlShortenManager googleUrlShortenManager = GoogleUrlShortenManager.getInstance();
    private APIFacade apiFacade = APIFacade.getInstance();
    private ViewGroup view;
    private String shortUrl;
    private String subject;
    private String text;
    private Sharing sharing;
    @Bind(R.id.emailButton)
    CustomButton emailButton;
    @Bind(R.id.facebookButton)
    CustomButton facebookButton;
    @Bind(R.id.linkedinButton)
    CustomButton linkedinButton;
    @Bind(R.id.messageButton)
    CustomButton messageButton;
    @Bind(R.id.sinaWeiboButton)
    CustomButton sinaWeiboButton;
    @Bind(R.id.tencentWeiboButton)
    CustomButton tencentWeiboButton;
    @Bind(R.id.twitterButton)
    CustomButton twitterButton;
    @Bind(R.id.wechatButton)
    CustomButton wechatButton;
    @Bind(R.id.whatsappButton)
    CustomButton whatsappButton;
    @Bind(R.id.qzoneButton)
    CustomButton qzoneButton;

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

        shortUrl = Config.SHARE_URL;
        subject = getString(R.string.app_name);
        text = getString(R.string.share_text);
        ((BaseActivity) getActivity()).showProgressDialog(true);
        apiFacade.getSharingData(getActivity());

        ButterKnife.bind(this, view);
        return view;
    }

    public void showButtons(int bitMask) {
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
    }

    public void showButtonIfNeed(Button button, int bitMask, int socialId) {
        if ((bitMask & socialId) == socialId) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (Keys.GET_SHARING_DATA_OPERATION_TAG.equals(operation.getTag())) {
            ((BaseActivity) getActivity()).dismissProgressDialog();

            if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
                sharing = (Sharing) operation.getResponseEntities().get(0);
                if (sharing != null) {
                    if (!TextUtils.isEmpty(sharing.getSharedText())) {
                        text = sharing.getSharedText();
                    }
                    if (!TextUtils.isEmpty(sharing.getSharedLink())) {
                        getShortUrl(sharing.getSharedLink());
                    }

                }
            } else {
                UIUtils.showSimpleToast(getActivity(), operation.getResponseError());
            }
        }
    }

    public void getShortUrl(String longUrl) {
        //Generate Short url to share
        googleUrlShortenManager.getShortUrl(getActivity(), longUrl,
                new GoogleUrlShortenManager.OnShotrUrlReadyListener() {
                    @Override
                    public void onShortUrlReady(String url) {
                        shortUrl = url;
                        showButtons(sharing.getBitMaskSocialNetwork());
                    }

                    @Override
                    public void onGetShortUrlError(String errorString) {

                    }
                }
        );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View view = actionBar.getCustomView();
        ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.share_title);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.emailButton, R.id.facebookButton, R.id.linkedinButton, R.id.messageButton, R.id.sinaWeiboButton, R.id.tencentWeiboButton, R.id.twitterButton, R.id.wechatButton, R.id.whatsappButton, R.id.qzoneButton})
    public void onViewClicked(View view) {
        String shareType = "";
        Intent intent = null;
        switch (view.getId()) {
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
                App.getInstance().getDefaultTracker().send(new HitBuilders.EventBuilder()
                        .setCategory("Share")
                        .setAction(shareType)
                        .build());
                getActivity().startActivity(intent);
            } else {
                getActivity().startActivity(IntentUtils.getGooglePlayIntent(intent.getPackage()));
                UIUtils.showSimpleToast(getActivity(), R.string.toast_application_not_found);
            }
        } else {
            UIUtils.showSimpleToast(getActivity(), R.string.toast_sharing_disable_in_settings);
        }
    }

    public enum SocialNetworks {
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
        ((BaseActivity) getActivity()).addNetworkOperationListener(this);
    }

    @Override
    public void onStop() {
        ((BaseActivity) getActivity()).removeNetworkOperationListener(this);
        super.onStop();
    }
}