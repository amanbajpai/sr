package com.ros.smartrocket.presentation.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.presentation.account.activity.ActivityMvpPresenter;
import com.ros.smartrocket.presentation.account.activity.ActivityMvpView;
import com.ros.smartrocket.presentation.account.activity.ActivityPresenter;
import com.ros.smartrocket.presentation.base.BaseFragment;
import com.ros.smartrocket.ui.dialog.ActivityLogDialog;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.eventbus.AvatarEvent;
import com.ros.smartrocket.utils.image.AvatarImageManager;
import com.ros.smartrocket.utils.image.SelectImageManager;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class MyAccountFragment extends BaseFragment implements MyAccountMvpView, ActivityMvpView {
    private static final String STATE_PHOTO = "com.ros.smartrocket.presentation.account.my.STATE_PHOTO";
    @BindView(R.id.photoImageView)
    ImageView photoImageView;
    @BindView(R.id.uploadPhotoProgressImage)
    ImageView uploadPhotoProgressImage;
    @BindView(R.id.nameTextView)
    CustomTextView nameTextView;
    @BindView(R.id.agentIdTxt)
    CustomTextView agentIdTxt;
    @BindView(R.id.emailTxt)
    CustomTextView emailTxt;
    @BindView(R.id.joinDateTxt)
    CustomTextView joinDateTxt;
    private File mCurrentPhotoFile;
    private AvatarImageManager avatarImageManager;
    private MyAccount myAccount;
    private MyAccountMvpPresenter<MyAccountMvpView> accPresenter;
    private ActivityMvpPresenter<ActivityMvpView> activityPresenter;
    private PreferencesManager preferences = PreferencesManager.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        ViewGroup view = (ViewGroup) localInflater.inflate(R.layout.fragment_my_account, null);
        ButterKnife.bind(this, view);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_PHOTO))
            mCurrentPhotoFile = (File) savedInstanceState.getSerializable(STATE_PHOTO);
        avatarImageManager = new AvatarImageManager();
        setData(App.getInstance().getMyAccount());
        return view;
    }

    private void setData(MyAccount myAccount) {
        this.myAccount = myAccount;
        showAvatar(myAccount.getPhotoUrl());
        nameTextView.setText(myAccount.getSingleName());
        agentIdTxt.setText(String.valueOf(myAccount.getId()));
        emailTxt.setText(preferences.getLastEmail());
        joinDateTxt.setText(UIUtils.getFormattedJoiningDate(myAccount.getJoined()));
    }

    private void initPresenters() {
        accPresenter = new MyAccountPresenter<>(false);
        accPresenter.attachView(this);
        activityPresenter = new ActivityPresenter<>();
        activityPresenter.attachView(this);
    }

    @Override
    public void onAccountLoaded(MyAccount account) {
        setData(account);
    }

    @Override
    public void onUserImageUpdated() {
        finishUploadingPhoto();
    }

    @Override
    public void onUserNameUpdated() {
        accPresenter.getAccount();
    }

    @Override
    public void onUserUpdateFailed() {
        finishUploadingPhoto();
    }

    @Override
    public void onActivitySent() {
        if (PreferencesManager.getInstance().getShowActivityDialog())
            new ActivityLogDialog(getActivity(), PreferencesManager.getInstance().getLastEmail());
        else
            UIUtils.showSimpleToast(getActivity(), getString(R.string.activity_log_description_toast)
                    + PreferencesManager.getInstance().getLastEmail());
    }

    public void finishUploadingPhoto() {
        uploadPhotoProgressImage.clearAnimation();
        uploadPhotoProgressImage.setVisibility(View.GONE);
        accPresenter.getAccount();
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
                    accPresenter.updateUserImage(event.image.bitmap);
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

    private void changeUserAvatar() {
        mCurrentPhotoFile = SelectImageManager.getTempFile(getActivity(), SelectImageManager.PREFIX_PROFILE);
        avatarImageManager.showSelectImageDialog(this, false, mCurrentPhotoFile);
    }

    private void changeUserName() {
        if (myAccount != null && myAccount.getIsUpdateNameRequired())
            DialogUtils.showUpdateFirstLastNameDialog(getActivity(), accPresenter);
        else
            UIUtils.showSimpleToast(getContext(), R.string.update_name_not_allowed, Toast.LENGTH_LONG);
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
    public void showNetworkError(BaseNetworkError networkError) {
        UIUtils.showSimpleToast(getActivity(), networkError.getErrorMessageRes());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_PHOTO, mCurrentPhotoFile);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onStart() {
        super.onStart();
        initPresenters();
        accPresenter.getAccount();
        activityPresenter.attachView(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        accPresenter.detachView();
        activityPresenter.detachView();
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @OnClick({R.id.photoImageView, R.id.uploadPhotoProgressImage, R.id.nameTextView, R.id.activityBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.photoImageView:
                changeUserAvatar();
                break;
            case R.id.nameTextView:
                changeUserName();
                break;
            case R.id.activityBtn:
                activityPresenter.sendActivity();
                break;
        }
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
}
