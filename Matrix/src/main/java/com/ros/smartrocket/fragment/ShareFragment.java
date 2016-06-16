package com.ros.smartrocket.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
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

/**
 * Share app info fragment
 */
public class ShareFragment extends Fragment implements OnClickListener, NetworkOperationListenerInterface {
    private static final String TAG = ShareFragment.class.getSimpleName();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private GoogleUrlShortenManager googleUrlShortenManager = GoogleUrlShortenManager.getInstance();
    private APIFacade apiFacade = APIFacade.getInstance();
    private ViewGroup view;
    private String shortUrl;
    private String subject;
    private String text;
    private Sharing sharing;
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

        view = (ViewGroup) localInflater.inflate(R.layout.fragment_share_and_refer, null);
        easyTracker = EasyTracker.getInstance(getActivity());

        shortUrl = Config.SHARE_URL;
        subject = getString(R.string.app_name);
        text = getString(R.string.share_text);

        ((ShareActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);
        apiFacade.getSharingData(getActivity());

        return view;
    }

    public void showButtons(int bitMask) {
        Button emailButton = (Button) view.findViewById(R.id.emailButton);
        Button messageButton = (Button) view.findViewById(R.id.messageButton);
        Button twitterButton = (Button) view.findViewById(R.id.twitterButton);
        Button facebookButton = (Button) view.findViewById(R.id.facebookButton);
        Button linkedinButton = (Button) view.findViewById(R.id.linkedinButton);
        Button whatsappButton = (Button) view.findViewById(R.id.whatsappButton);
        Button wechatButton = (Button) view.findViewById(R.id.wechatButton);
        Button tencentWeiboButton = (Button) view.findViewById(R.id.tencentWeiboButton);
        Button sinaWeiboButton = (Button) view.findViewById(R.id.sinaWeiboButton);
        Button qzoneButton = (Button) view.findViewById(R.id.qzoneButton);

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
            button.setOnClickListener(this);
        } else {
            button.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (Keys.GET_SHARING_DATA_OPERATION_TAG.equals(operation.getTag())) {
            ((BaseActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);

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

        private SocialNetworks(int bitMask) {
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