package com.ros.smartrocket.fragment;

import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.activity.MainActivity;
import com.ros.smartrocket.bl.NotificationBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.NotificationDbSchema;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.db.entity.UploadPhoto;
import com.ros.smartrocket.dialog.LevelUpDialog;
import com.ros.smartrocket.eventbus.PhotoEvent;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.images.ImageLoader;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.BytesBitmap;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.MultipassUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.SelectImageManager;
import com.ros.smartrocket.utils.UIUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class MainMenuFragment extends Fragment implements OnClickListener, NetworkOperationListenerInterface {
    private static final String STATE_PHOTO = "com.ros.smartrocket.MainMenuFragment.STATE_PHOTO";

    private APIFacade apiFacade = APIFacade.getInstance();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private ResponseReceiver localReceiver;
    private AsyncQueryHandler handler;
    private ImageView photoImageView;
    private ImageView uploadPhotoProgressImage;
    private ImageView levelIcon;
    private TextView myTasksCount;
    private TextView nameTextView;
    private TextView balanceTextView;
    private TextView rocketPointNumberTextView;
    private TextView levelName;
    private TextView minLevelExperience;
    private TextView maxLevelExperience;
    private SeekBar levelProgressBar;
    private File mCurrentPhotoFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        ViewGroup view = (ViewGroup) localInflater.inflate(R.layout.fragment_main_menu, null);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_PHOTO)) {
            mCurrentPhotoFile = (File) savedInstanceState.getSerializable(STATE_PHOTO);
        }

        handler = new DbHandler(getActivity().getContentResolver());

        photoImageView = (ImageView) view.findViewById(R.id.photoImageView);
        uploadPhotoProgressImage = (ImageView) view.findViewById(R.id.uploadPhotoProgressImage);
        levelIcon = (ImageView) view.findViewById(R.id.levelIcon);
        myTasksCount = (TextView) view.findViewById(R.id.myTasksCount);
        nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        balanceTextView = (TextView) view.findViewById(R.id.balanceTextView);
        rocketPointNumberTextView = (TextView) view.findViewById(R.id.rocketPointNumberTextView);
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

        view.findViewById(R.id.photoImageView).setOnClickListener(this);
        view.findViewById(R.id.findTasksButton).setOnClickListener(this);
        view.findViewById(R.id.myTasksButton).setOnClickListener(this);
        view.findViewById(R.id.myAccountButton).setOnClickListener(this);
        view.findViewById(R.id.notificationsButton).setOnClickListener(this);
        view.findViewById(R.id.shareButton).setOnClickListener(this);
        view.findViewById(R.id.supportButton).setOnClickListener(this);
        view.findViewById(R.id.settingsButton).setOnClickListener(this);
        view.findViewById(R.id.cashingOutLayout).setOnClickListener(this);

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
        } else {
            photoImageView.setImageResource(R.drawable.cam);
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
        balanceTextView.setText(UIUtils.getBalanceOrPrice(getActivity(), myAccount.getBalance(),
                myAccount.getCurrencySign(), 0, BigDecimal.ROUND_DOWN));
        levelName.setText(String.valueOf(myAccount.getLevelName()));
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
                        getView().findViewById(R.id.notificationsIndicator).setVisibility(View.VISIBLE);
                    } else {
                        getView().findViewById(R.id.notificationsIndicator).setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.GET_MY_ACCOUNT_OPERATION_TAG.equals(operation.getTag())) {
                ArrayList<MyAccount> responseEntities = (ArrayList<MyAccount>) operation.getResponseEntities();
                if (responseEntities.size() > 0) {
                    MyAccount myAccount = responseEntities.get(0);
                    setData(myAccount);
                }
            } else if (Keys.UPLOAD_PHOTO_OPERATION_TAG.equals(operation.getTag())) {
                finishUploadingPhoto();
            }
        } else {
            if (Keys.UPLOAD_PHOTO_OPERATION_TAG.equals(operation.getTag())) {
                finishUploadingPhoto();
            }
            UIUtils.showSimpleToast(getActivity(), operation.getResponseError());
        }
    }

    public void finishUploadingPhoto() {
        uploadPhotoProgressImage.clearAnimation();
        uploadPhotoProgressImage.setVisibility(View.GONE);
        apiFacade.getMyAccount(getActivity());
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        Fragment fragment;

        switch (v.getId()) {
            case R.id.photoImageView:
                mCurrentPhotoFile = SelectImageManager.getTempFile(getActivity(), SelectImageManager.PREFIX_PROFILE);
                SelectImageManager.showSelectImageDialog(getActivity(), false, mCurrentPhotoFile);
                break;
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
                break;
            case R.id.notificationsButton:
                getActivity().startActivity(IntentUtils.getNotificationsIntent(getActivity()));
                break;
            case R.id.shareButton:
                //((MainActivity) getActivity()).startFragment(new ShareFragment());
                getActivity().startActivity(IntentUtils.getShareIntent(getActivity()));
                ((MainActivity) getActivity()).togleMenu();
                break;
            case R.id.cashingOutLayout:
                //((MainActivity) getActivity()).startFragment(new CashingOutFragment());
                getActivity().startActivity(IntentUtils.getCashOutIntent(getActivity()));
                ((MainActivity) getActivity()).togleMenu();
                break;
            case R.id.supportButton:
                String uid = preferencesManager.getLastEmail();
                long validTimeInMillis = DateUtils.DAY_IN_MILLIS;
                String customerEmail = preferencesManager.getLastEmail();
                String customerName = preferencesManager.getLastEmail();

                MultipassUtils multipassUtils = new MultipassUtils(uid, validTimeInMillis, customerEmail, customerName);
                getActivity().startActivity(IntentUtils.getBrowserIntent(multipassUtils.buildUrl()));
                break;
            case R.id.settingsButton:
                //((MainActivity) getActivity()).startFragment(new SettingsFragment());
                getActivity().startActivity(IntentUtils.getSettingIntent(getActivity()));
                ((MainActivity) getActivity()).togleMenu();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (intent != null && intent.getData() != null) {
            intent.putExtra(SelectImageManager.EXTRA_PREFIX, SelectImageManager.PREFIX_PROFILE);
            SelectImageManager.onActivityResult(requestCode, resultCode, intent, getActivity());
        } else if (mCurrentPhotoFile != null) {
            intent = new Intent();
            intent.putExtra(SelectImageManager.EXTRA_PHOTO_FILE, mCurrentPhotoFile);
            intent.putExtra(SelectImageManager.EXTRA_PREFIX, SelectImageManager.PREFIX_PROFILE);
            SelectImageManager.onActivityResult(requestCode, resultCode, intent, getActivity());
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
    public void onEventMainThread(PhotoEvent event) {
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

                    UploadPhoto uploadPhotoEntity = new UploadPhoto();
                    uploadPhotoEntity.setPhotoBase64(BytesBitmap.getBase64String(event.image.bitmap));

                    apiFacade.uploadPhoto(getActivity(), uploadPhotoEntity);
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
}
