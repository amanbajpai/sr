package com.ros.smartrocket.presentation.main.menu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.presentation.account.AccountMvpPresenter;
import com.ros.smartrocket.presentation.account.AccountMvpView;
import com.ros.smartrocket.presentation.account.AccountPresenter;
import com.ros.smartrocket.presentation.base.BaseFragment;
import com.ros.smartrocket.presentation.main.MainActivity;
import com.ros.smartrocket.presentation.task.AllTaskFragment;
import com.ros.smartrocket.ui.dialog.LevelUpDialog;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.HelpShiftUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.eventbus.AvatarEvent;
import com.ros.smartrocket.utils.image.AvatarImageManager;
import com.ros.smartrocket.utils.image.SelectImageManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class MainMenuFragment extends BaseFragment implements OnClickListener, AccountMvpView, MenuMvpView {
    private static final String STATE_PHOTO = "com.ros.smartrocket.MainMenuFragment.STATE_PHOTO";
    @BindView(R.id.photoImageView)
    ImageView photoImageView;
    @BindView(R.id.uploadPhotoProgressImage)
    ImageView uploadPhotoProgressImage;
    @BindView(R.id.nameTextView)
    CustomTextView nameTextView;
    @BindView(R.id.myTasksCount)
    CustomTextView myTasksCount;
    @BindView(R.id.balanceTextView)
    CustomTextView balanceTextView;
    @BindView(R.id.rocketPointNumberTextView)
    CustomTextView rocketPointNumberTextView;
    @BindView(R.id.levelName)
    CustomTextView levelName;
    @BindView(R.id.levelIcon)
    ImageView levelIcon;
    @BindView(R.id.levelProgressBar)
    SeekBar levelProgressBar;
    @BindView(R.id.minLevelExperience)
    CustomTextView minLevelExperience;
    @BindView(R.id.maxLevelExperience)
    CustomTextView maxLevelExperience;
    @BindView(R.id.notificationsButton)
    CustomTextView notificationsButton;
    @BindView(R.id.reputationTextView)
    CustomTextView reputationTextView;
    @BindView(R.id.levelNumber)
    CustomTextView levelNumber;
    @BindView(R.id.agentId)
    CustomTextView agentId;

    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private ResponseReceiver localReceiver;
    private File mCurrentPhotoFile;
    private MyAccount myAccount;
    private AvatarImageManager avatarImageManager;
    private AccountMvpPresenter<AccountMvpView> accountPresenter;
    private MenuMvpPresenter<MenuMvpView> menuPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        ViewGroup view = (ViewGroup) localInflater.inflate(R.layout.fragment_main_menu, null);
        ButterKnife.bind(this, view);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_PHOTO))
            mCurrentPhotoFile = (File) savedInstanceState.getSerializable(STATE_PHOTO);
        avatarImageManager = new AvatarImageManager();
        levelProgressBar.setOnTouchListener((v, event) -> true);

        initReceiver();
        setData(App.getInstance().getMyAccount());
        return view;
    }

    private void initPresenters() {
        accountPresenter = new AccountPresenter<>(false);
        accountPresenter.attachView(this);
        menuPresenter = new MenuPresenter<>();
        menuPresenter.attachView(this);
    }

    private void initReceiver() {
        localReceiver = new ResponseReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Keys.REFRESH_MAIN_MENU);
        intentFilter.addAction(Keys.REFRESH_MAIN_MENU_MY_TASK_COUNT);
        intentFilter.addAction(Keys.REFRESH_PUSH_NOTIFICATION_LIST);
        getActivity().registerReceiver(localReceiver, intentFilter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_PHOTO, mCurrentPhotoFile);
        super.onSaveInstanceState(outState);
    }

    public void setData(MyAccount myAccount) {
        this.myAccount = myAccount;
        showAvatar(myAccount.getPhotoUrl());
        showLevelIcon(myAccount.getLevelIconUrl());

        agentId.setText(String.valueOf(myAccount.getId()));
        nameTextView.setText(myAccount.getSingleName());
        nameTextView.setOnClickListener(this);
        balanceTextView.setText(UIUtils.getBalanceOrPrice(myAccount.getBalance(),
                myAccount.getCurrencySign(), 2, BigDecimal.ROUND_DOWN));
        levelName.setText(String.valueOf(myAccount.getLevelName()));
        String level = myAccount.getLevelNumber() != null ? String.valueOf(myAccount.getLevelNumber()) + ". " : "";
        levelNumber.setText(level);
        minLevelExperience.setText(String.valueOf(myAccount.getMinLevelExperience()));
        maxLevelExperience.setText(String.valueOf(myAccount.getMaxLevelExperience()));

        if (myAccount.getExperience() != null) {
            int maxProgress = myAccount.getMaxLevelExperience() - myAccount.getMinLevelExperience();
            int currentProgress = myAccount.getExperience() - myAccount.getMinLevelExperience();
            levelProgressBar.setMax(maxProgress);
            levelProgressBar.setProgress(currentProgress);
            rocketPointNumberTextView.setText(String.valueOf(myAccount.getExperience()));
        }

        if (myAccount.getLevelNumber() != null
                && preferencesManager.getLastLevelNumber() != myAccount.getLevelNumber()) {
            if (preferencesManager.getLastLevelNumber() != -1)
                new LevelUpDialog(getActivity());
            preferencesManager.setLastLevelNumber(myAccount.getLevelNumber());
        }
        reputationTextView.setText(myAccount.getStringReputation());
    }

    private void showLevelIcon(String levelIconUrl) {
        if (!TextUtils.isEmpty(levelIconUrl))
            Picasso.with(getActivity())
                    .load(levelIconUrl)
                    .error(R.drawable.cam)
                    .into(levelIcon);
    }

    private void showAvatar(String photoUrl) {
        if (!TextUtils.isEmpty(photoUrl))
            Picasso.with(getActivity())
                    .load(photoUrl)
                    .error(R.drawable.cam)
                    .into(photoImageView);
        else
            photoImageView.setImageResource(R.drawable.cam);
    }

    @Override
    public void onAccountLoaded(MyAccount account) {
        setData(account);
    }

    @Override
    public void setUnreadNotificationsCount(int count) {
        LocaleUtils.setCompoundDrawable(notificationsButton, count > 0
                ? R.drawable.notifications_blue
                : R.drawable.notifications_empty);
    }

    @Override
    public void setMyTasksCount(int count) {
        myTasksCount.setText(String.valueOf(count));
    }

    @Override
    public void onUserImageUpdated() {
        finishUploadingPhoto();
    }

    @Override
    public void onUserNameUpdated() {
        accountPresenter.getAccount();
    }

    @Override
    public void onUserUpdateFailed() {
        finishUploadingPhoto();
    }

    public class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Keys.REFRESH_MAIN_MENU.equals(action)) {
                accountPresenter.getAccount();
            } else if (Keys.REFRESH_MAIN_MENU_MY_TASK_COUNT.equals(action)) {
                menuPresenter.getMyTasksCount();
            } else if (Keys.REFRESH_PUSH_NOTIFICATION_LIST.equals(action)) {
                menuPresenter.getUnreadNotificationsCount();
            }
        }
    }

    public void finishUploadingPhoto() {
        uploadPhotoProgressImage.clearAnimation();
        uploadPhotoProgressImage.setVisibility(View.GONE);
        accountPresenter.getAccount();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (intent != null && intent.getData() != null) {
            intent.putExtra(SelectImageManager.EXTRA_PREFIX, SelectImageManager.PREFIX_PROFILE);
            avatarImageManager.onActivityResult(requestCode, resultCode, intent, getActivity());
        } else if (mCurrentPhotoFile != null) {
            intent = new Intent();
            intent.putExtra(SelectImageManager.EXTRA_PHOTO_FILE, mCurrentPhotoFile);
            intent.putExtra(SelectImageManager.EXTRA_PREFIX, SelectImageManager.PREFIX_PROFILE);
            avatarImageManager.onActivityResult(requestCode, resultCode, intent, getActivity());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        initPresenters();
        accountPresenter.getAccount();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        menuPresenter.getUnreadNotificationsCount();
        menuPresenter.getMyTasksCount();
    }

    @Override
    public void onStop() {
        accountPresenter.detachView();
        menuPresenter.detachView();
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (getActivity() != null) getActivity().unregisterReceiver(localReceiver);
        super.onDestroy();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(AvatarEvent event) {
        switch (event.type) {
            case START_LOADING:
                uploadPhotoProgressImage.startAnimation(AnimationUtils.loadAnimation(getActivity(),
                        R.anim.rotate));
                uploadPhotoProgressImage.setVisibility(View.VISIBLE);
                break;
            case IMAGE_COMPLETE:
                if (event.image != null && event.image.bitmap != null) {
                    uploadPhotoProgressImage.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate));
                    uploadPhotoProgressImage.setVisibility(View.VISIBLE);
                    photoImageView.setImageBitmap(event.image.bitmap);
                    menuPresenter.updateUserImage(event.image.bitmap);
                } else {
                    photoImageView.setImageResource(R.drawable.btn_camera_error_selector);
                }
                break;
            case SELECT_IMAGE_ERROR:
                uploadPhotoProgressImage.clearAnimation();
                uploadPhotoProgressImage.setVisibility(View.GONE);
                DialogUtils.showPhotoCanNotBeAddDialog(getActivity());
                break;
        }
    }

    @OnClick({R.id.notificationsButton, R.id.photoImageView, R.id.findTasksButton, R.id.myTasksButton, R.id.cashingOutLayout, R.id.shareButton, R.id.supportButton, R.id.settingsButton})
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.photoImageView:
                changeUserAvatar();
                break;
            case R.id.nameTextView:
                changeUserName();
                break;
            case R.id.findTasksButton:
                startAllTaskFragment(bundle);
                break;
            case R.id.myTasksButton:
                startMyTaskFragment(bundle);
                break;
            case R.id.notificationsButton:
                getActivity().startActivity(IntentUtils.getNotificationsIntent(getActivity()));
                break;
            case R.id.shareButton:
                getActivity().startActivity(IntentUtils.getShareIntent(getActivity()));
                ((MainActivity) getActivity()).toggleMenu();
                break;
            case R.id.cashingOutLayout:
                getActivity().startActivity(IntentUtils.getCashOutIntent(getActivity()));
                ((MainActivity) getActivity()).toggleMenu();
                break;
            case R.id.supportButton:
                HelpShiftUtils.showFAQ(getActivity());
                break;
            case R.id.settingsButton:
                getActivity().startActivity(IntentUtils.getSettingIntent(getActivity()));
                ((MainActivity) getActivity()).toggleMenu();
                break;
            default:
                break;
        }
    }

    private void startMyTaskFragment(Bundle bundle) {
        Fragment fragment;
        bundle.putString(Keys.CONTENT_TYPE, Keys.MY_TASK);
        fragment = new AllTaskFragment();
        fragment.setArguments(bundle);
        startFragmentAndCloseMenu(fragment);
    }

    private void startAllTaskFragment(Bundle bundle) {
        Fragment fragment;
        bundle.putString(Keys.CONTENT_TYPE, Keys.FIND_TASK);
        fragment = new AllTaskFragment();
        fragment.setArguments(bundle);
        startFragmentAndCloseMenu(fragment);
    }

    private void startFragmentAndCloseMenu(Fragment fragment) {
        ((MainActivity) getActivity()).startFragment(fragment);
        ((MainActivity) getActivity()).toggleMenu();
    }

    private void changeUserAvatar() {
        mCurrentPhotoFile = SelectImageManager.getTempFile(getActivity(), SelectImageManager.PREFIX_PROFILE);
        avatarImageManager.showSelectImageDialog(this, false, mCurrentPhotoFile);
    }

    private void changeUserName() {
        if (myAccount != null && myAccount.getIsUpdateNameRequired())
            DialogUtils.showUpdateFirstLastNameDialog(getActivity(), menuPresenter);
        else
            UIUtils.showSimpleToast(getContext(), R.string.update_name_not_allowed, Toast.LENGTH_LONG);
    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        UIUtils.showSimpleToast(getActivity(), networkError.getErrorMessageRes());
    }
}
