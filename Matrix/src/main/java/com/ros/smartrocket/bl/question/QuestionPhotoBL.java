package com.ros.smartrocket.bl.question;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.dialog.CustomProgressDialog;
import com.ros.smartrocket.eventbus.PhotoEvent;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.utils.*;
import com.ros.smartrocket.utils.image.SelectImageManager;

import de.greenrobot.event.EventBus;

import java.io.File;
import java.util.Arrays;

/**
 * Multiple photo question type
 */
public class QuestionPhotoBL extends QuestionBaseBL implements View.OnClickListener {
    private static final String STATE_PHOTO = "com.ros.smartrocket.STATE_PHOTO";
    private static final String EXTRA_LAST_PHOTO_FILE = "com.ros.smartrocket.EXTRA_LAST_PHOTO_FILE";
    private static final String EXTRA_IS_PHOTO_FROM_GALLERY = "com.ros.smartrocket.EXTRA_IS_PHOTO_FROM_GALLERY";
    private static final String STATE_SELECTED_FRAME = "current_selected_photo";
    private static final String TAG = "Question Photo";

    private ImageButton rePhotoButton;
    private ImageButton deletePhotoButton;
    private ImageButton confirmButton;
    private ImageView photoImageView;
    private LinearLayout galleryLayout;
    private CustomProgressDialog progressDialog;

    private File mCurrentPhotoFile;
    private File lastPhotoFile;

    private int currentSelectedPhoto = 0;
    private boolean isLastFileFromGallery;
    private boolean isBitmapAdded = false;
    private boolean isBitmapConfirmed = false;
    private SelectImageManager selectImageManager;

    @SuppressLint("SetTextI18n")
    @Override
    public void configureView() {
        showProgressDialog();
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

        if (savedInstanceState != null) {
            mCurrentPhotoFile = (File) savedInstanceState.getSerializable(STATE_PHOTO);
            currentSelectedPhoto = savedInstanceState.getInt(STATE_SELECTED_FRAME, 0);
            lastPhotoFile = (File) savedInstanceState.getSerializable(EXTRA_LAST_PHOTO_FILE);
            isLastFileFromGallery = savedInstanceState.getBoolean(EXTRA_IS_PHOTO_FROM_GALLERY);

            if (lastPhotoFile != null && lastPhotoFile.exists()) {
                photoImageView.setImageURI(Uri.fromFile(lastPhotoFile));
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void validateView() {
        super.validateView();
        questionText.setMovementMethod(LinkMovementMethod.getInstance());
        if (question.getMaximumPhotos() > 1) {
            String string = getActivity().getString(R.string.maximum_photo, question.getMaximumPhotos());
            questionText.setText(Html.fromHtml(question.getQuestion() + string));
        } else {
            questionText.setText(Html.fromHtml(question.getQuestion()));
        }

        loadAnswers();
    }

    @Override
    public void onPause() {
        hideProgressDialog();
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mCurrentPhotoFile != null) {
            outState.putSerializable(STATE_PHOTO, mCurrentPhotoFile);
            outState.putInt(STATE_SELECTED_FRAME, currentSelectedPhoto);
        }
        outState.putSerializable(EXTRA_LAST_PHOTO_FILE, lastPhotoFile);
        outState.putBoolean(EXTRA_IS_PHOTO_FROM_GALLERY, isLastFileFromGallery);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (mCurrentPhotoFile != null) {
            intent = new Intent();
            intent.putExtra(SelectImageManager.EXTRA_PHOTO_FILE, mCurrentPhotoFile);
            intent.putExtra(SelectImageManager.EXTRA_PREFIX, question.getTaskId().toString());
            selectImageManager.onActivityResult(requestCode, resultCode, intent, getActivity());
            return true;
        } else if (intent != null && intent.getData() != null) {
            intent.putExtra(SelectImageManager.EXTRA_PREFIX, question.getTaskId().toString());
            selectImageManager.onActivityResult(requestCode, resultCode, intent, getActivity());
            return true;
        }

        return false;
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(PhotoEvent event) {
        L.v(TAG, "Event " + event.type);
        switch (event.type) {
            case START_LOADING:
                showProgressDialog();
                break;
            case IMAGE_COMPLETE:
                L.v(TAG, "onImageComplete");
                lastPhotoFile = event.image.imageFile;
                isLastFileFromGallery = event.image.isFileFromGallery;
                isBitmapAdded = event.image.bitmap != null;
                isBitmapConfirmed = false;

                if (event.image.bitmap != null) {
                    L.v(TAG, "Set Bitmap not null " + QuestionPhotoBL.this);
                    photoImageView.setImageBitmap(event.image.bitmap);
                } else {
                    photoImageView.setImageResource(R.drawable.btn_camera_error_selector);
                }

                refreshRePhotoButton();
                refreshDeletePhotoButton();
                refreshConfirmButton();
                refreshNextButton();

                hideProgressDialog();
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
            ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
        }
    }

    @Override
    protected void answersDeleteComplete() {
        if (question.getAnswers().length == question.getMaximumPhotos()) {
            question.setAnswers(addEmptyAnswer(question.getAnswers()));
        }
        AnswersBL.getAnswersListFromDB(handler, question.getTaskId(), question.getMissionId(), question.getId(),
                getProductId());
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
            boolean selected = isBitmapAdded && isBitmapConfirmed;
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
                        Intent intent = IntentUtils.getFullScreenImageIntent(getActivity(), filePath, rotateByExif);
                        getActivity().startActivity(intent);
                    }
                    break;
                }
            case R.id.rePhotoButton:
                if (question.getPhotoSource() == 0) {
                    // From camera
                    mCurrentPhotoFile = SelectImageManager.getTempFile(getActivity(), question.getTaskId().toString());
                    SelectImageManager.startCamera(fragment, mCurrentPhotoFile);
                } else if (question.getPhotoSource() == 1) {
                    // From gallery
                    SelectImageManager.startGallery(fragment);
                } else {
                    File fileToPhoto = SelectImageManager.getTempFile(getActivity(), question.getTaskId().toString());
                    selectImageManager.showSelectImageDialog(fragment, true, fileToPhoto);
                }
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
                MatrixLocationManager.getCurrentLocation(false, new MatrixLocationManager.GetCurrentLocationListener() {
                    @Override
                    public void getLocationStart() {
                        showProgressDialog();
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
                break;
            default:
                break;
        }
    }

    public void confirmButtonPressAction(Location location) {
        File resultImageFile = SelectImageManager.getScaledFile(lastPhotoFile,
                SelectImageManager.SIZE_IN_PX_2_MP, 0, isLastFileFromGallery);

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

            AnswersBL.updateAnswersToDB(handler, question.getAnswers());

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

    public Answer[] addEmptyAnswer(Answer[] currentAnswerArray) {
        Answer answer = new Answer();
        answer.setRandomId();
        answer.setQuestionId(question.getId());
        answer.setTaskId(question.getTaskId());
        answer.setMissionId(question.getMissionId());
        answer.setProductId(product != null ? product.getId() : 0);

        //Save empty answer to DB
        Uri uri = getActivity().getContentResolver().insert(AnswerDbSchema.CONTENT_URI, answer.toContentValues());
        long id = ContentUris.parseId(uri);

        answer.set_id(id);

        Answer[] resultAnswerArray = Arrays.copyOf(currentAnswerArray, currentAnswerArray.length + 1);
        resultAnswerArray[currentAnswerArray.length] = answer;

        return resultAnswerArray;
    }

    public void refreshPhotoGallery(Answer[] answers) {
        galleryLayout.removeAllViews();

        for (int i = 0; i < answers.length; i++) {
            Answer answer = answers[i];
            addItemToGallery(i, answer);
        }
    }

    public void addItemToGallery(final int position, Answer answer) {
        View convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_photo_gallery, null);
        ImageView photo = (ImageView) convertView.findViewById(R.id.image);
        ImageView imageFrame = (ImageView) convertView.findViewById(R.id.imageFrame);

        if (!TextUtils.isEmpty(answer.getFileUri()) && answer.getChecked()) {
            Bitmap bitmap = SelectImageManager.prepareBitmap(new File(answer.getFileUri()), 100, 0, false);
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

    public void showProgressDialog() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgressDialog();

                if (progressDialog == null || !progressDialog.isShowing()) {
                    progressDialog = CustomProgressDialog.show(getActivity());
                    progressDialog.setCancelable(false);
                }
            }
        });

    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
