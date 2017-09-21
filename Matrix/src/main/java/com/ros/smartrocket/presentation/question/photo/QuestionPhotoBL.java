package com.ros.smartrocket.presentation.question.photo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.bl.AnswersBL;
import com.ros.smartrocket.db.bl.QuestionsBL;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.map.location.MatrixLocationManager;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.presentation.question.base.QuestionBaseBL;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.MyLog;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.eventbus.PhotoEvent;
import com.ros.smartrocket.utils.image.RequestCodeImageHelper;
import com.ros.smartrocket.utils.image.SelectImageManager;

import java.io.File;

import de.greenrobot.event.EventBus;


public class QuestionPhotoBL extends QuestionBaseBL implements View.OnClickListener {
    private static final String STATE_PHOTO = "com.ros.smartrocket.STATE_PHOTO";
    private static final String EXTRA_LAST_PHOTO_FILE = "com.ros.smartrocket.EXTRA_LAST_PHOTO_FILE";
    private static final String EXTRA_IS_PHOTO_FROM_GALLERY = "com.ros.smartrocket.EXTRA_IS_PHOTO_FROM_GALLERY";
    private static final String STATE_SELECTED_FRAME = "current_selected_photo";
    private static final String IS_IMAGE_REQUESTED = "is_photo_requested";
    private static final String TAG = "Question Photo";

    private ImageButton rePhotoButton;
    private ImageButton deletePhotoButton;
    private ImageButton confirmButton;
    private ImageView photoImageView;
    private LinearLayout galleryLayout;

    private File mCurrentPhotoFile;
    private File lastPhotoFile;

    private int currentSelectedPhoto = 0;
    private boolean isLastFileFromGallery;
    private boolean isBitmapAdded = false;
    private boolean isBitmapConfirmed = false;
    private SelectImageManager selectImageManager;
    private boolean isImageRequested;

    @SuppressLint("SetTextI18n")
    @Override
    public void configureView() {
        showLoading();
        selectImageManager = new SelectImageManager();
        galleryLayout = (LinearLayout) view.findViewById(R.id.galleryLayout);

        photoImageView = (ImageView) view.findViewById(R.id.photo);
        photoImageView.setOnClickListener(this);

        rePhotoButton = (ImageButton) view.findViewById(R.id.rePhotoButton);
        rePhotoButton.setOnClickListener(this);

        deletePhotoButton = (ImageButton) view.findViewById(R.id.deletePhotoButton);
        deletePhotoButton.setOnClickListener(this);

        confirmButton = (ImageButton) view.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(this);
        EventBus.getDefault().register(this);
        if (savedInstanceState != null) {
            mCurrentPhotoFile = (File) savedInstanceState.getSerializable(STATE_PHOTO);
            currentSelectedPhoto = savedInstanceState.getInt(STATE_SELECTED_FRAME, 0);
            lastPhotoFile = (File) savedInstanceState.getSerializable(EXTRA_LAST_PHOTO_FILE);
            isLastFileFromGallery = savedInstanceState.getBoolean(EXTRA_IS_PHOTO_FROM_GALLERY);
            isImageRequested = savedInstanceState.getBoolean(IS_IMAGE_REQUESTED);
            if (lastPhotoFile != null && lastPhotoFile.exists()) {
                photoImageView.setImageURI(Uri.fromFile(lastPhotoFile));
            }
        }
    }

    @Override
    public void destroyView() {
        super.destroyView();
        EventBus.getDefault().unregister(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void validateView() {
        super.validateView();
        questionText.setMovementMethod(LinkMovementMethod.getInstance());
        String subQuestionNumber = TextUtils.isEmpty(question.getSubQuestionNumber())
                ? "" : question.getSubQuestionNumber();
        if (question.getMaximumPhotos() > 1) {
            String string = getActivity().getString(R.string.maximum_photo, question.getMaximumPhotos());
            questionText.setText(Html.fromHtml(subQuestionNumber + question.getQuestion() + string));
        } else {
            questionText.setText(Html.fromHtml(subQuestionNumber + question.getQuestion()));
        }

        loadAnswers();
    }

    @Override
    public void onPause() {
        hideProgressDialog();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mCurrentPhotoFile != null) {
            outState.putSerializable(STATE_PHOTO, mCurrentPhotoFile);
            outState.putInt(STATE_SELECTED_FRAME, currentSelectedPhoto);
        }
        outState.putSerializable(EXTRA_LAST_PHOTO_FILE, lastPhotoFile);
        outState.putBoolean(EXTRA_IS_PHOTO_FROM_GALLERY, isLastFileFromGallery);
        outState.putBoolean(IS_IMAGE_REQUESTED, isImageRequested);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (isImageRequested) {
            if (mCurrentPhotoFile != null) {
                intent = new Intent();
                intent.putExtra(SelectImageManager.EXTRA_PHOTO_FILE, mCurrentPhotoFile);
                intent.putExtra(SelectImageManager.EXTRA_PREFIX, question.getTaskId().toString());
                selectImageManager.onActivityResult(requestCode, resultCode, intent, getActivity());
                isImageRequested = false;
                return true;
            } else if (intent != null && intent.getData() != null) {
                intent.putExtra(SelectImageManager.EXTRA_PREFIX, question.getTaskId().toString());
                selectImageManager.onActivityResult(requestCode, resultCode, intent, getActivity());
                isImageRequested = false;
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(PhotoEvent event) {
        L.v(TAG, "Event " + event.type);
        switch (event.type) {
            case START_LOADING:
                showLoading();
                break;
            case IMAGE_COMPLETE:
                MyLog.v(TAG, "ImageComplete");
                if (event.requestCode == null
                        || RequestCodeImageHelper.getBigPart(event.requestCode) == question.getOrderId()) {
                    lastPhotoFile = event.image.imageFile;
                    isLastFileFromGallery = event.image.isFileFromGallery;
                    isBitmapAdded = event.image.bitmap != null;
                    isBitmapConfirmed = false;

                    if (event.image.bitmap != null) {
                        L.v(TAG, "Set Bitmap not null " + QuestionPhotoBL.this);
                        photoImageView.setImageBitmap(event.image.bitmap);
                        onConfirmButtonPressed();
                    } else {
                        photoImageView.setImageResource(R.drawable.btn_camera_error_selector);
                        hideProgressDialog();
                    }

                    refreshRePhotoButton();
                    refreshDeletePhotoButton();
                    refreshConfirmButton();
                    refreshNextButton();
                }
                break;
            case SELECT_IMAGE_ERROR:
                hideProgressDialog();
                DialogUtils.showPhotoCanNotBeAddDialog(getActivity());
                break;
        }
    }

    @Override
    protected void fillViewWithAnswers(Answer[] answers) {
        if (answers.length == 0) {
            question.setAnswers(addEmptyAnswer(answers));
        } else {
            question.setAnswers(answers);
        }

        refreshPhotoGallery(question.getAnswers());
        if (!isBitmapAdded) {
            selectGalleryPhoto(0);
        }
        hideProgressDialog();
    }

    @Override
    protected void answersUpdate() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            ((BaseActivity) getActivity()).hideLoading();
        }
    }

    @Override
    protected void answersDeleteComplete() {
        if (question.getAnswers().length == question.getMaximumPhotos() && !isLastAnswerEmpty()) {
            question.setAnswers(addEmptyAnswer(question.getAnswers()));
        }
        if (getProductId() != null) {
            AnswersBL.getAnswersListFromDB(handler, question.getTaskId(), question.getMissionId(), question.getId(),
                    getProductId());
        } else {
            AnswersBL.getAnswersListFromDB(handler, question.getTaskId(), question.getMissionId(), question.getId());
        }
        isBitmapAdded = false;
        currentSelectedPhoto = 0;
    }

    public void selectGalleryPhoto(int position) {
        Answer answer = question.getAnswers()[position];

        for (int i = 0; i < galleryLayout.getChildCount(); i++) {
            View convertView = galleryLayout.getChildAt(i);
            ImageView imageFrame = (ImageView) convertView.findViewById(R.id.imageFrame);

            if (i == currentSelectedPhoto) {
                imageFrame.setVisibility(View.VISIBLE);
            } else {
                imageFrame.setVisibility(View.GONE);
            }
        }

        if (answer.getChecked() && answer.getFileUri() != null) {
            isBitmapAdded = true;
            isBitmapConfirmed = true;

            Bitmap bitmap = SelectImageManager.prepareBitmap(new File(answer.getFileUri()));
            photoImageView.setImageBitmap(bitmap);
        } else {
            isBitmapAdded = false;
            isBitmapConfirmed = false;

            L.v(TAG, "selectGalleryPhoto " + position + " set bitmap null " + QuestionPhotoBL.this);
            photoImageView.setImageBitmap(null);
            photoImageView.setBackgroundResource(R.drawable.camera_icon);
        }

        refreshRePhotoButton();
        refreshDeletePhotoButton();
        refreshConfirmButton();
        refreshNextButton();
    }

    @Override
    public void refreshNextButton() {
        if (answerSelectedListener != null) {
            boolean selected = isPhotosAdded();
            answerSelectedListener.onAnswerSelected(selected, question.getId());
        }

        if (answerPageLoadingFinishedListener != null) {
            answerPageLoadingFinishedListener.onAnswerPageLoadingFinished();
        }
    }

    public void refreshConfirmButton() {
        if (isBitmapAdded) {
            confirmButton.setVisibility(View.VISIBLE);
            confirmButton.setEnabled(!isBitmapConfirmed);
            if (isBitmapConfirmed) {
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

    public void refreshRePhotoButton() {
        if (isBitmapAdded) {
            rePhotoButton.setVisibility(View.VISIBLE);
        } else {
            rePhotoButton.setVisibility(View.GONE);
        }

    }

    public void refreshDeletePhotoButton() {
        if (isBitmapAdded) {
            deletePhotoButton.setVisibility(View.VISIBLE);
        } else {
            deletePhotoButton.setVisibility(View.GONE);
        }
    }

    @Override
    public Question getQuestion() {
        try {
            return QuestionsBL.getQuestionsFromDB(question.getWaveId(), question.getTaskId(), question.getMissionId(),
                    question.getId());
        } catch (Exception e) {
            return question;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo:
                if (isBitmapAdded) {
                    String filePath = "";
                    boolean rotateByExif = false;
                    if (!isBitmapConfirmed) {
                        filePath = lastPhotoFile.getPath();
                        rotateByExif = !isLastFileFromGallery;
                    } else if (question.getAnswers().length > currentSelectedPhoto) {
                        Answer answer = question.getAnswers()[currentSelectedPhoto];
                        filePath = answer.getFileUri();
                    }

                    if (!TextUtils.isEmpty(filePath)) {
                        Intent intent = IntentUtils.getFullScreenImageIntent(getActivity(), filePath);
                        getActivity().startActivity(intent);
                    }
                    break;
                }
            case R.id.rePhotoButton:
                if (question.getPhotoSource() == 0) {
                    // From camera
                    mCurrentPhotoFile = SelectImageManager.getTempFile(getActivity(), question.getTaskId().toString());
                    SelectImageManager.startCamera(fragment, mCurrentPhotoFile, question.getOrderId());
                } else if (question.getPhotoSource() == 1) {
                    // From gallery
                    SelectImageManager.startGallery(fragment, question.getOrderId());
                } else {
                    File fileToPhoto = SelectImageManager.getTempFile(getActivity(), question.getTaskId().toString());
                    selectImageManager.showSelectImageDialog(fragment, true, fileToPhoto);
                }
                isImageRequested = true;
                break;
            case R.id.deletePhotoButton:
                if (isBitmapConfirmed) {
                    if (question.getAnswers().length > currentSelectedPhoto) {
                        AnswersBL.deleteAnswerFromDB(handler, question.getAnswers()[currentSelectedPhoto]);
                    }
                } else {
                    isBitmapAdded = false;
                    refreshRePhotoButton();
                    refreshDeletePhotoButton();
                    refreshConfirmButton();
                    refreshPhotoGallery(question.getAnswers());
                    photoImageView.setImageBitmap(null);
                }
                break;
            case R.id.confirmButton:
                onConfirmButtonPressed();
                break;
            default:
                break;
        }
    }

    private void onConfirmButtonPressed() {
        MatrixLocationManager.getCurrentLocation(false, new MatrixLocationManager.GetCurrentLocationListener() {
            @Override
            public void getLocationStart() {
                showLoading();
            }

            @Override
            public void getLocationInProcess() {
            }

            @Override
            public void getLocationSuccess(Location location) {
                if (getActivity() == null) {
                    return;
                }

                confirmButtonPressAction(location);
                hideProgressDialog();
            }

            @Override
            public void getLocationFail(String errorText) {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    UIUtils.showSimpleToast(getActivity(), errorText);
                }
                hideProgressDialog();
            }
        });
    }

    private void confirmButtonPressAction(Location location) {
        File resultImageFile = SelectImageManager.getScaledFile(lastPhotoFile,
                SelectImageManager.SIZE_IN_PX_2_MP, 0);

        if (resultImageFile.exists() && question.getAnswers().length > currentSelectedPhoto) {
            Answer answer = question.getAnswers()[currentSelectedPhoto];
            boolean needAddEmptyAnswer = !answer.getChecked();

            answer.setChecked(true);
            answer.setFileUri(Uri.fromFile(resultImageFile).getPath());
            answer.setFileSizeB(resultImageFile.length());
            answer.setFileName(resultImageFile.getName());
            answer.setValue(resultImageFile.getName());
            answer.setLatitude(location.getLatitude());
            answer.setLongitude(location.getLongitude());
            if (!isPreview()) {
                AnswersBL.updateAnswersToDB(handler, question.getAnswers());
            }
            if (needAddEmptyAnswer && question.getAnswers().length < question.getMaximumPhotos()) {
                question.setAnswers(addEmptyAnswer(question.getAnswers()));
            }

            refreshPhotoGallery(question.getAnswers());

            isBitmapConfirmed = true;

            refreshRePhotoButton();
            refreshConfirmButton();
            refreshNextButton();
        }
    }


    private boolean isLastAnswerEmpty() {
        boolean result = false;
        int lastPos = question.getAnswers().length - 1;
        if (TextUtils.isEmpty(question.getAnswers()[lastPos].getFileUri())) {
            result = true;
        }
        return result;
    }

    private void refreshPhotoGallery(Answer[] answers) {
        galleryLayout.removeAllViews();

        for (int i = 0; i < answers.length; i++) {
            Answer answer = answers[i];
            addItemToGallery(i, answer);
        }
    }

    private void addItemToGallery(final int position, Answer answer) {
        View convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_photo_gallery, null);
        ImageView photo = (ImageView) convertView.findViewById(R.id.image);
        ImageView imageFrame = (ImageView) convertView.findViewById(R.id.imageFrame);

        if (!TextUtils.isEmpty(answer.getFileUri()) && answer.getChecked()) {
            Bitmap bitmap = SelectImageManager.prepareBitmap(new File(answer.getFileUri()), 100, 0);
            photo.setImageBitmap(bitmap);
        } else {
            photo.setBackgroundResource(R.drawable.camera_icon);
        }

        if (position == currentSelectedPhoto) {
            imageFrame.setVisibility(View.VISIBLE);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelectedPhoto = position;
                selectGalleryPhoto(position);
            }
        });

        galleryLayout.addView(convertView);
    }

    private boolean isPhotosAdded() {
        boolean result = false;
        for (Answer answer : question.getAnswers()) {
            if (!TextUtils.isEmpty(answer.getFileUri()) && answer.getChecked()) {
                result = true;
                break;
            }
        }
        return result;
    }
}
