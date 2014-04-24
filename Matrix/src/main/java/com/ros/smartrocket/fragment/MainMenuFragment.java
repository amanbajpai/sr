package com.ros.smartrocket.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.activity.MainActivity;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.dialog.LevelUpDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.images.ImageLoader;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

public class MainMenuFragment extends Fragment implements OnClickListener, NetworkOperationListenerInterface {
    //private static final String TAG = MainMenuFragment.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();

    private ResponseReceiver localReceiver;
    private ImageView photoImageView;
    private ImageView levelIcon;
    private TextView nameTextView;
    private TextView balanceTextView;
    private TextView levelTextView;
    private TextView levelName;
    private TextView minLevelExperience;
    private TextView maxLevelExperience;
    private SeekBar levelProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        ViewGroup view = (ViewGroup) localInflater.inflate(R.layout.fragment_main_menu, null);

        photoImageView = (ImageView) view.findViewById(R.id.photoImageView);
        levelIcon = (ImageView) view.findViewById(R.id.levelIcon);
        nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        balanceTextView = (TextView) view.findViewById(R.id.balanceTextView);
        levelTextView = (TextView) view.findViewById(R.id.levelNumberTextView);
        levelName = (TextView) view.findViewById(R.id.levelName);
        minLevelExperience = (TextView) view.findViewById(R.id.minLevelExperience);
        maxLevelExperience = (TextView) view.findViewById(R.id.maxLevelExperience);
        levelProgressBar = (SeekBar) view.findViewById(R.id.levelProgressBar);
        levelProgressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        view.findViewById(R.id.findTasksButton).setOnClickListener(this);
        view.findViewById(R.id.myTasksButton).setOnClickListener(this);
        view.findViewById(R.id.myAccountButton).setOnClickListener(this);
        view.findViewById(R.id.shareButton).setOnClickListener(this);
        view.findViewById(R.id.supportButton).setOnClickListener(this);
        view.findViewById(R.id.settingsButton).setOnClickListener(this);

        localReceiver = new ResponseReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Keys.REFRESH_MAIN_MENU);

        getActivity().registerReceiver(localReceiver, intentFilter);

        setData(App.getInstance().getMyAccount());
        apiFacade.getMyAccount(getActivity());

        return view;
    }

    public void setData(MyAccount myAccount) {
        String photoUrl = myAccount.getPhotoUrl();
        if (!TextUtils.isEmpty(photoUrl)) {
            ImageLoader.getInstance().loadBitmap(photoUrl, ImageLoader.SMALL_IMAGE_VAR,
                    new ImageLoader.OnFetchCompleteListener() {

                        @Override
                        public void onFetchComplete(final Bitmap result) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    photoImageView.setImageBitmap(result);
                                }
                            });
                        }
                    }
            );
        }

        String levelIconUrl = myAccount.getLevelIconUrl();
        if (!TextUtils.isEmpty(levelIconUrl)) {
            ImageLoader.getInstance().loadBitmap(levelIconUrl, ImageLoader.SMALL_IMAGE_VAR,
                    new ImageLoader.OnFetchCompleteListener() {

                        @Override
                        public void onFetchComplete(final Bitmap result) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    levelIcon.setImageBitmap(result);
                                }
                            });
                        }
                    }
            );
        }

        nameTextView.setText(myAccount.getName());
        balanceTextView.setText(myAccount.getBalance() + " " + getString(R.string.hk));
        levelTextView.setText(String.valueOf(myAccount.getLevelNumber()));
        levelName.setText(String.valueOf(myAccount.getLevelName()));
        minLevelExperience.setText(String.valueOf(myAccount.getMinLevelExperience()));
        maxLevelExperience.setText(String.valueOf(myAccount.getMaxLevelExperience()));

        if (myAccount.getExperience() != null) {
            int maxProgress = myAccount.getMaxLevelExperience() - myAccount.getMinLevelExperience();
            int currentProgress = myAccount.getExperience() - myAccount.getMinLevelExperience();

            levelProgressBar.setMax(maxProgress);
            levelProgressBar.setProgress(currentProgress);
        }


        if (myAccount.getLevelNumber() != null
                && preferencesManager.getLastLevelNumber() != myAccount.getLevelNumber()) {
            if (preferencesManager.getLastLevelNumber() == -1) {
                preferencesManager.setLastLevelNumber(myAccount.getLevelNumber());
            }

            if (preferencesManager.getLastLevelNumber() != myAccount.getLevelNumber()) {
                new LevelUpDialog(getActivity());
                preferencesManager.setLastLevelNumber(myAccount.getLevelNumber());
            }
        }
    }

    public class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (Keys.REFRESH_MAIN_MENU.equals(action)) {
                apiFacade.getMyAccount(getActivity());
            }
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.GET_MY_ACCOUNT_OPERATION_TAG.equals(operation.getTag())) {
                MyAccount myAccount = (MyAccount) operation.getResponseEntities().get(0);
                setData(myAccount);
            }
        } else {
            UIUtils.showSimpleToast(getActivity(), operation.getResponseError());
        }
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        Fragment fragment;

        switch (v.getId()) {
            case R.id.findTasksButton:
                bundle.putString(Keys.CONTENT_TYPE, Keys.FIND_TASK);

                fragment = new AllTaskFragment();
                fragment.setArguments(bundle);
                ((MainActivity) getActivity()).startFragment(fragment);
                ((MainActivity) getActivity()).togleMenu();
                break;
            case R.id.myTasksButton:
                bundle.putString(Keys.CONTENT_TYPE, Keys.MY_TASK);

                fragment = new AllTaskFragment();
                fragment.setArguments(bundle);

                ((MainActivity) getActivity()).startFragment(fragment);
                ((MainActivity) getActivity()).togleMenu();
                break;
            case R.id.myAccountButton:
                getActivity().startActivity(IntentUtils.getBrowserIntent(Config.PROFILE_PAGE_URL));
                //((MainActivity) getActivity()).startFragment(new MyAccountFragment());
                //((MainActivity) getActivity()).togleMenu();
                break;
            case R.id.shareButton:
                ((MainActivity) getActivity()).startFragment(new ShareFragment());
                ((MainActivity) getActivity()).togleMenu();
                break;
            case R.id.supportButton:
                getActivity().startActivity(IntentUtils.getBrowserIntent(Config.KNOWLEDGE_BASE_URL));
                /*((MainActivity) getActivity()).startFragment(new SupportFragment());
                ((MainActivity) getActivity()).togleMenu();*/
                break;
            case R.id.settingsButton:
                ((MainActivity) getActivity()).startFragment(new SettingsFragment());
                ((MainActivity) getActivity()).togleMenu();
                break;
            default:
                break;
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

    @Override
    public void onDestroy() {
        if (getActivity() != null) {
            getActivity().unregisterReceiver(localReceiver);
        }
        super.onDestroy();
    }
}
