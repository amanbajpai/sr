package com.ros.smartrocket.ui.fragment;

import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
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
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.flow.base.BaseActivity;
import com.ros.smartrocket.flow.task.AllTaskFragment;
import com.ros.smartrocket.ui.activity.MainActivity;
import com.ros.smartrocket.bl.NotificationBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.NotificationDbSchema;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.db.entity.UpdateUser;
import com.ros.smartrocket.flow.base.BaseFragment;
import com.ros.smartrocket.ui.dialog.CustomProgressDialog;
import com.ros.smartrocket.ui.dialog.LevelUpDialog;
import com.ros.smartrocket.ui.dialog.ShowProgressDialogInterface;
import com.ros.smartrocket.utils.eventbus.AvatarEvent;
import com.ros.smartrocket.utils.helpers.APIFacade;
import com.ros.smartrocket.images.ImageLoader;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.BytesBitmap;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.HelpShiftUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.image.AvatarImageManager;
import com.ros.smartrocket.utils.image.SelectImageManager;
import com.ros.smartrocket.ui.views.CustomTextView;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class MainMenuFragment extends BaseFragment implements OnClickListener, NetworkOperationListenerInterface,
        ShowProgressDialogInterface {
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
    @BindView(R.id.levelLayout)
    LinearLayout levelLayout;
    @BindView(R.id.shareButton)
    CustomTextView shareButton;
    @BindView(R.id.supportButton)
    CustomTextView supportButton;
    @BindView(R.id.settingsButton)
    CustomTextView settingsButton;
    @BindView(R.id.levelNumber)
    CustomTextView levelNumber;
    @BindView(R.id.agentId)
    CustomTextView agentId;

    private APIFacade apiFacade = APIFacade.getInstance();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private ResponseReceiver localReceiver;
    private AsyncQueryHandler handler;
    private File mCurrentPhotoFile;
    private CustomProgressDialog progressDialog;
    private MyAccount myAccount;
    private AvatarImageManager avatarImageManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        ViewGroup view = (ViewGroup) localInflater.inflate(R.layout.fragment_main_menu, null);
        ButterKnife.bind(this, view);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_PHOTO)) {
            mCurrentPhotoFile = (File) savedInstanceState.getSerializable(STATE_PHOTO);
        }

        handler = new DbHandler(getActivity().getContentResolver());
        avatarImageManager = new AvatarImageManager();
        levelProgressBar.setOnTouchListener((v, event) -> true);

        localReceiver = new ResponseReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Keys.REFRESH_MAIN_MENU);
        intentFilter.addAction(Keys.REFRESH_MAIN_MENU_MY_TASK_COUNT);
        intentFilter.addAction(Keys.REFRESH_PUSH_NOTIFICATION_LIST);

        getActivity().registerReceiver(localReceiver, intentFilter);

        setData(App.getInstance().getMyAccount());
        apiFacade.getMyAccount(getActivity());

        TasksBL.getMyTasksForMapFromDB(handler);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_PHOTO, mCurrentPhotoFile);
        super.onSaveInstanceState(outState);
    }

    public void setData(MyAccount myAccount) {
        this.myAccount = myAccount;
        String photoUrl = myAccount.getPhotoUrl();
        if (!TextUtils.isEmpty(photoUrl)) {
            ImageLoader.getInstance()
                    .loadBitmap(photoUrl, ImageLoader.SMALL_IMAGE_VAR,
                    result -> getActivity().runOnUiThread(() -> photoImageView.setImageBitmap(result))
            );
        } else {
            photoImageView.setImageResource(R.drawable.cam);
        }

        String levelIconUrl = myAccount.getLevelIconUrl();
        if (!TextUtils.isEmpty(levelIconUrl)) {
            ImageLoader.getInstance().loadBitmap(levelIconUrl, ImageLoader.SMALL_IMAGE_VAR,
                    result -> getActivity().runOnUiThread(() -> levelIcon.setImageBitmap(result))
            );
        }
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

            if (preferencesManager.getLastLevelNumber() == -1) {
                preferencesManager.setLastLevelNumber(myAccount.getLevelNumber());
            } else {
                new LevelUpDialog(getActivity());

                preferencesManager.setLastLevelNumber(myAccount.getLevelNumber());
            }
        }
        reputationTextView.setText(myAccount.getStringReputation());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (Keys.REFRESH_MAIN_MENU.equals(action)) {
                apiFacade.getMyAccount(getActivity());
            } else if (Keys.REFRESH_MAIN_MENU_MY_TASK_COUNT.equals(action)) {
                TasksBL.getMyTasksForMainMenuFromDB(handler);
            } else if (Keys.REFRESH_PUSH_NOTIFICATION_LIST.equals(action)) {
                NotificationBL.getUnreadNotificationsFromDB(handler);
            }
        }
    }

    class DbHandler extends AsyncQueryHandler {
        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TaskDbSchema.Query.All.TOKEN_QUERY:
                    int tasksCount = TasksBL.convertCursorToTasksCount(cursor);
                    myTasksCount.setText(String.valueOf(tasksCount));
                    break;
                case NotificationDbSchema.Query.TOKEN_QUERY:
                    if (NotificationBL.convertCursorToUnreadNotificationsCount(cursor) > 0) {
                        LocaleUtils.setCompoundDrawable(notificationsButton, R.drawable.notifications_blue);
                    } else {
                        LocaleUtils.setCompoundDrawable(notificationsButton, R.drawable.notifications_empty);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onNetworkOperationSuccess(BaseOperation operation) {
        if (Keys.GET_MY_ACCOUNT_OPERATION_TAG.equals(operation.getTag())) {
            ArrayList<MyAccount> responseEntities = (ArrayList<MyAccount>) operation.getResponseEntities();
            if (responseEntities.size() > 0) {
                MyAccount myAccount = responseEntities.get(0);
                setData(myAccount);
            }
        } else if (Keys.UPDATE_USER_OPERATION_TAG.equals(operation.getTag())) {
            finishUploadingPhoto();
        }
        dismissProgressBar();
    }

    @Override
    public void onNetworkOperationFailed(BaseOperation operation) {
        if (Keys.UPDATE_USER_OPERATION_TAG.equals(operation.getTag())) {
            finishUploadingPhoto();
        }
        UIUtils.showSimpleToast(getActivity(), operation.getResponseError());
        dismissProgressBar();
    }

    public void finishUploadingPhoto() {
        uploadPhotoProgressImage.clearAnimation();
        uploadPhotoProgressImage.setVisibility(View.GONE);
        apiFacade.getMyAccount(getActivity());
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
        ((BaseActivity) getActivity()).addNetworkOperationListener(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        NotificationBL.getUnreadNotificationsFromDB(handler);
    }

    @Override
    public void onStop() {
        ((BaseActivity) getActivity()).removeNetworkOperationListener(this);
        EventBus.getDefault().unregister(this);
        dismissProgressBar();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (getActivity() != null) {
            getActivity().unregisterReceiver(localReceiver);
        }
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

                    UpdateUser updateUserEntity = new UpdateUser();
                    updateUserEntity.setPhotoBase64(BytesBitmap.getBase64String(event.image.bitmap));

                    apiFacade.updateUser(getActivity(), updateUserEntity);
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

    @Override
    public void showDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        progressDialog = CustomProgressDialog.show(getActivity());
        progressDialog.setCancelable(false);
    }

    public void dismissProgressBar() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @OnClick({R.id.notificationsButton, R.id.photoImageView, R.id.findTasksButton, R.id.myTasksButton, R.id.cashingOutLayout, R.id.shareButton, R.id.supportButton, R.id.settingsButton})
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        Fragment fragment;

        switch (view.getId()) {
            case R.id.photoImageView:
                mCurrentPhotoFile = SelectImageManager.getTempFile(getActivity(), SelectImageManager.PREFIX_PROFILE);
                avatarImageManager.showSelectImageDialog(this, false, mCurrentPhotoFile);
                break;
            case R.id.nameTextView:
                if (myAccount != null && myAccount.getIsUpdateNameRequired()) {
                    DialogUtils.showUpdateFirstLastNameDialog(getActivity(), apiFacade, this);
                } else {
                    UIUtils.showSimpleToast(getContext(), R.string.update_name_not_allowed, Toast.LENGTH_LONG);
                }
                break;
            case R.id.findTasksButton:
                bundle.putString(Keys.CONTENT_TYPE, Keys.FIND_TASK);

                fragment = new AllTaskFragment();
                fragment.setArguments(bundle);
                ((MainActivity) getActivity()).startFragment(fragment);
                ((MainActivity) getActivity()).toggleMenu();
                break;
            case R.id.myTasksButton:
                bundle.putString(Keys.CONTENT_TYPE, Keys.MY_TASK);

                fragment = new AllTaskFragment();
                fragment.setArguments(bundle);

                ((MainActivity) getActivity()).startFragment(fragment);
                ((MainActivity) getActivity()).toggleMenu();
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
}
