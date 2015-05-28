package com.ros.smartrocket.fragment;

import android.content.*;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.dialog.CustomProgressDialog;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.SelectImageManager;
import com.ros.smartrocket.utils.UIUtils;

import java.io.File;
import java.util.Arrays;

/**
 * Multiple photo question type
 */
public class QuestionType7Fragment extends BaseQuestionFragment implements View.OnClickListener {
    private SelectImageManager selectImageManager = SelectImageManager.getInstance();
    private LayoutInflater localInflater;
    private ImageButton rePhotoButton;
    private ImageButton deletePhotoButton;
    private ImageButton confirmButton;
    private ImageView photoImageView;
    private LinearLayout galleryLayout;
    private boolean isBitmapAdded = false;
    private boolean isBitmapConfirmed = false;
    private Question question;
    private OnAnswerSelectedListener answerSelectedListener;
    private OnAnswerPageLoadingFinishedListener answerPageLoadingFinishedListener;
    private AsyncQueryHandler handler;
    private int currentSelectedPhoto = 0;
    private CustomProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        localInflater = inflater.cloneInContext(contextThemeWrapper);

        ViewGroup view = (ViewGroup) localInflater.inflate(R.layout.fragment_question_type_7, null);

        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable(Keys.QUESTION);
        }

        handler = new DbHandler(getActivity().getContentResolver());

        showProgressDialog();

        TextView questionText = (TextView) view.findViewById(R.id.questionText);
        if (!TextUtils.isEmpty(question.getPresetValidationText())) {
            TextView presetValidationComment = (TextView) view.findViewById(R.id.presetValidationComment);
            presetValidationComment.setText(question.getPresetValidationText());
            presetValidationComment.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(question.getValidationComment())) {
            TextView validationComment = (TextView) view.findViewById(R.id.validationComment);
            validationComment.setText(question.getValidationComment());
            validationComment.setVisibility(View.VISIBLE);
        }

        galleryLayout = (LinearLayout) view.findViewById(R.id.galleryLayout);

        photoImageView = (ImageView) view.findViewById(R.id.photo);
        photoImageView.setOnClickListener(this);

        rePhotoButton = (ImageButton) view.findViewById(R.id.rePhotoButton);
        rePhotoButton.setOnClickListener(this);

        deletePhotoButton = (ImageButton) view.findViewById(R.id.deletePhotoButton);
        deletePhotoButton.setOnClickListener(this);

        confirmButton = (ImageButton) view.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(this);

        if (question.getMaximumPhotos() > 1) {
            questionText.setText(question.getQuestion() + getString(R.string.maximum_photo, question.getMaximumPhotos()));
        } else {
            questionText.setText(question.getQuestion());
        }
        AnswersBL.getAnswersListFromDB(handler, question.getTaskId(), question.getId());

        return view;
    }

    class DbHandler extends AsyncQueryHandler {

        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case AnswerDbSchema.Query.TOKEN_QUERY:
                    Answer[] answers = AnswersBL.convertCursorToAnswersArray(cursor);

                    if (answers.length == 0) {
                        question.setAnswers(addEmptyAnswer(answers));
                    } else {
                        question.setAnswers(answers);
                    }

                    refreshPhotoGallery(question.getAnswers());

                    selectGalleryPhoto(0);

                    hideProgressDialog();
                    break;
                default:
                    break;
            }
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            switch (token) {
                case AnswerDbSchema.Query.TOKEN_UPDATE:
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            switch (token) {
                case AnswerDbSchema.Query.TOKEN_DELETE:
                    if (question.getAnswers().length == question.getMaximumPhotos()) {
                        question.setAnswers(addEmptyAnswer(question.getAnswers()));
                    }

                    AnswersBL.getAnswersListFromDB(handler, question.getTaskId(), question.getId());
                    break;
                default:
                    break;
            }
        }
    }

    public void selectGalleryPhoto(int position) {
        currentSelectedPhoto = position;

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

            photoImageView.setImageBitmap(null);
            photoImageView.setBackgroundResource(R.drawable.camera_icon);
        }

        refreshRePhotoButton();
        refreshDeletePhotoButton();
        refreshConfirmButton();
        refreshNextButton();
    }

    public void refreshNextButton() {
        if (answerSelectedListener != null) {
            boolean selected = isBitmapAdded && isBitmapConfirmed;

            answerSelectedListener.onAnswerSelected(selected);
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
    public boolean saveQuestion() {
        return true;
    }

    @Override
    public void clearAnswer() {
        if (question != null && question.getAnswers() != null && question.getAnswers().length > 0) {
            Answer[] answers = question.getAnswers();
            for (Answer answer : answers) {
                answer.setChecked(false);
            }

            AnswersBL.updateAnswersToDB(handler, answers);
        }
    }

    @Override
    public Question getQuestion() {
        return question;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        selectImageManager.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo:
                if (isBitmapAdded) {
                    String filePath;
                    boolean rotateByExif;
                    if (!isBitmapConfirmed) {
                        filePath = Uri.fromFile(selectImageManager.getLastFile()).getPath();
                        rotateByExif = !selectImageManager.isLastFileFromGallery();
                    } else {
                        Answer answer = question.getAnswers()[currentSelectedPhoto];
                        filePath = answer.getFileUri();
                        rotateByExif = false;
                    }

                    startActivity(IntentUtils.getFullScreenImageIntent(getActivity(), filePath, rotateByExif));
                    break;
                }
            case R.id.rePhotoButton:
                if (question.getPhotoSource() == 0) {
                    selectImageManager.startCamera(getActivity());
                } else if (question.getPhotoSource() == 1) {
                    selectImageManager.startGallery(getActivity());
                } else {
                    selectImageManager.showSelectImageDialog(getActivity(), true);
                }

                selectImageManager.setImageCompleteListener(imageCompleteListener);
                break;
            case R.id.deletePhotoButton:
                AnswersBL.deleteAnswerFromDB(handler, question.getAnswers()[currentSelectedPhoto]);
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
        File sourceImageFile = selectImageManager.getLastFile();
        File resultImageFile = selectImageManager.getScaledFile(sourceImageFile,
                SelectImageManager.SIZE_IN_PX_2_MP, 0);

        if (resultImageFile.exists()) {
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

        //Save empty answer to DB
        Uri uri = getActivity().getContentResolver().insert(AnswerDbSchema.CONTENT_URI, answer.toContentValues());
        long id = ContentUris.parseId(uri);

        answer.set_id(id);

        Answer[] resultAnswerArray = Arrays.copyOf(currentAnswerArray, currentAnswerArray.length + 1);
        resultAnswerArray[currentAnswerArray.length] = answer;

        return resultAnswerArray;
    }

    SelectImageManager.OnImageCompleteListener imageCompleteListener = new SelectImageManager.OnImageCompleteListener() {
        @Override
        public void onStartLoading() {
            showProgressDialog();
        }

        @Override
        public void onImageComplete(Bitmap bitmap) {
            isBitmapAdded = bitmap != null;
            isBitmapConfirmed = false;

            if (bitmap != null) {
                photoImageView.setImageBitmap(bitmap);
            } else {
                photoImageView.setImageResource(R.drawable.btn_camera_error_selector);
            }

            refreshRePhotoButton();
            refreshDeletePhotoButton();
            refreshConfirmButton();
            refreshNextButton();

            hideProgressDialog();
        }

        @Override
        public void onSelectImageError(int imageFrom) {
            hideProgressDialog();
            DialogUtils.showPhotoCanNotBeAddDialog(getActivity());
        }
    };

    @Override
    public void setAnswerSelectedListener(OnAnswerSelectedListener answerSelectedListener) {
        this.answerSelectedListener = answerSelectedListener;
    }

    @Override
    public void setAnswerPageLoadingFinishedListener(OnAnswerPageLoadingFinishedListener
                                                             answerPageLoadingFinishedListener) {
        this.answerPageLoadingFinishedListener = answerPageLoadingFinishedListener;
    }

    public void refreshPhotoGallery(Answer[] answers) {
        galleryLayout.removeAllViews();

        for (int i = 0; i < answers.length; i++) {
            Answer answer = answers[i];
            addItemToGallery(i, answer);
        }
    }

    public void addItemToGallery(final int position, Answer answer) {
        View convertView = localInflater.inflate(R.layout.list_item_photo_gallery, null);
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
                selectGalleryPhoto(position);
            }
        });

        galleryLayout.addView(convertView);
    }

    public void showProgressDialog() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = CustomProgressDialog.show(getActivity());
                progressDialog.setCancelable(false);
            }
        });

    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


}
