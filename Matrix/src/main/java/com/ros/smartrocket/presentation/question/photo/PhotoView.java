package com.ros.smartrocket.presentation.question.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.presentation.question.base.BaseQuestionView;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.eventbus.PhotoEvent;
import com.ros.smartrocket.utils.image.SelectImageManager;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class PhotoView extends BaseQuestionView<PhotoMvpPresenter<PhotoMvpView>> implements PhotoMvpView {

    @BindView(R.id.galleryLayout)
    LinearLayout galleryLayout;
    @BindView(R.id.photo)
    ImageView photo;
    @BindView(R.id.rePhotoButton)
    ImageButton rePhotoButton;
    @BindView(R.id.deletePhotoButton)
    ImageButton deletePhotoButton;
    @BindView(R.id.confirmButton)
    ImageButton confirmButton;
    private int currentSelectedPhoto = 0;

    public PhotoView(Context context) {
        super(context);
    }

    public PhotoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void validateView(Question question) {
        super.validateView(question);
        questionText.setMovementMethod(LinkMovementMethod.getInstance());
        String subQuestionNumber = TextUtils.isEmpty(question.getSubQuestionNumber())
                ? "" : question.getSubQuestionNumber();
        if (question.getMaximumPhotos() > 1) {
            String string = getContext().getString(R.string.maximum_photo, question.getMaximumPhotos());
            questionText.setText(Html.fromHtml(subQuestionNumber + question.getQuestion() + string));
        } else {
            questionText.setText(Html.fromHtml(subQuestionNumber + question.getQuestion()));
        }
        presenter.loadAnswers();
    }

    @Override
    public void configureView(Question question) {
        showLoading(false);
//        if (savedInstanceState != null) {
//            mCurrentPhotoFile = (File) savedInstanceState.getSerializable(STATE_PHOTO);
//            currentSelectedPhoto = savedInstanceState.getInt(STATE_SELECTED_FRAME, 0);
//            lastPhotoFile = (File) savedInstanceState.getSerializable(EXTRA_LAST_PHOTO_FILE);
//            isLastFileFromGallery = savedInstanceState.getBoolean(EXTRA_IS_PHOTO_FROM_GALLERY);
//            isImageRequested = savedInstanceState.getBoolean(IS_IMAGE_REQUESTED);
//            if (lastPhotoFile != null && lastPhotoFile.exists()) {
//                photoImageView.setImageURI(Uri.fromFile(lastPhotoFile));
//            }
//        }
    }

    @Override
    public void fillViewWithAnswers(List<Answer> answers) {
        if (answers.isEmpty()) presenter.addEmptyAnswer();
        refreshPhotoGallery(answers);
        hideLoading();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(PhotoEvent event) {
        presenter.onPhotoEvent(event);
    }

    @OnClick({R.id.photo, R.id.rePhotoButton, R.id.deletePhotoButton, R.id.confirmButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.photo:
                presenter.onPhotoClicked(currentSelectedPhoto);
                break;
            case R.id.rePhotoButton:
                presenter.onPhotoRequested(currentSelectedPhoto);
                break;
            case R.id.deletePhotoButton:
                presenter.onPhotoDeleted(currentSelectedPhoto);
                break;
            case R.id.confirmButton:
                presenter.onPhotoConfirmed(currentSelectedPhoto);
                break;
        }
    }


    @Override
    public int getLayoutResId() {
        return R.layout.view_photo_question;
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPause() {
        hideLoading();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void showPhotoCanNotBeAddDialog() {
        DialogUtils.showPhotoCanNotBeAddDialog(getContext());
    }

    @Override
    public void refreshPhotoGallery(List<Answer> answers) {
        galleryLayout.removeAllViews();
        for (int i = 0; i < answers.size(); i++) {
            addItemToGallery(i, answers.get(i));
        }
    }

    private void addItemToGallery(final int position, Answer answer) {
        View convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_photo_gallery, null);
        ImageView photo = (ImageView) convertView.findViewById(R.id.image);
        ImageView imageFrame = (ImageView) convertView.findViewById(R.id.imageFrame);
        if (!TextUtils.isEmpty(answer.getFileUri()) && answer.getChecked()) {
            Bitmap bitmap = SelectImageManager.prepareBitmap(new File(answer.getFileUri()), 100, 0);
            photo.setImageBitmap(bitmap);
        } else {
            photo.setBackgroundResource(R.drawable.camera_icon);
        }

        if (position == currentSelectedPhoto) imageFrame.setVisibility(View.VISIBLE);
        convertView.setOnClickListener(v -> {
            currentSelectedPhoto = position;
            presenter.selectGalleryPhoto(position);
        });

        galleryLayout.addView(convertView);
    }

    @Override
    public void selectGalleryPhoto(int position) {
        for (int i = 0; i < galleryLayout.getChildCount(); i++) {
            View convertView = galleryLayout.getChildAt(i);
            ImageView imageFrame = (ImageView) convertView.findViewById(R.id.imageFrame);
            if (i == currentSelectedPhoto)
                imageFrame.setVisibility(View.VISIBLE);
            else
                imageFrame.setVisibility(View.GONE);
        }
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        photo.setImageBitmap(bitmap);
        if (bitmap == null) photo.setBackgroundResource(R.drawable.camera_icon);
    }

    @Override
    public int getCurrentPos() {
        return currentSelectedPhoto;
    }

    @Override
    public void setCurrentPos(int pos) {
        currentSelectedPhoto = pos;
    }

    @Override
    public void refreshConfirmButton(boolean isPhotoAdded) {
        if (isPhotoAdded) {
            confirmButton.setVisibility(View.VISIBLE);
            confirmButton.setEnabled(!isPhotoAdded);
            if (isPhotoAdded) {
                confirmButton.setBackgroundResource(R.drawable.btn_square_green);
                confirmButton.setImageResource(R.drawable.check_square_white);
            } else {
                confirmButton.setBackgroundResource(R.drawable.btn_square_active);
                confirmButton.setImageResource(R.drawable.check_square_green);
            }
        } else {
            confirmButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void refreshRePhotoButton(boolean isPhotoAdded) {
        if (isPhotoAdded) {
            rePhotoButton.setVisibility(View.VISIBLE);
        } else {
            rePhotoButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void refreshDeletePhotoButton(boolean isPhotoAdded) {
        if (isPhotoAdded) {
            deletePhotoButton.setVisibility(View.VISIBLE);
        } else {
            deletePhotoButton.setVisibility(View.GONE);
        }
    }
}
