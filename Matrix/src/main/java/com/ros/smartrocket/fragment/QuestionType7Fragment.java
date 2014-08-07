package com.ros.smartrocket.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.SelectImageManager;

import java.io.File;
import java.util.Arrays;

/**
 * Fragment for display About information
 */
public class QuestionType7Fragment extends BaseQuestionFragment implements View.OnClickListener {
    private SelectImageManager selectImageManager = SelectImageManager.getInstance();
    private LayoutInflater localInflater;
    private ImageButton rePhotoButton;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        localInflater = inflater.cloneInContext(contextThemeWrapper);

        ViewGroup view = (ViewGroup) localInflater.inflate(R.layout.fragment_question_type_7, null);

        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable(Keys.QUESTION);
        }

        handler = new DbHandler(getActivity().getContentResolver());

        TextView questionText = (TextView) view.findViewById(R.id.questionText);

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

        confirmButton = (ImageButton) view.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(this);

        questionText.setText(question.getQuestion());
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
                    break;
                default:
                    break;
            }
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            switch (token) {
                case AnswerDbSchema.Query.TOKEN_UPDATE:
                    ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
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

        if (answer.isChecked() && answer.getFileUri() != null) {
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

    @Override
    public void saveQuestion() {
        //AnswersBL.updateAnswersToDB(handler, question.getAnswers());
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
            case R.id.confirmButton:
                ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);

                File sourceImageFile = selectImageManager.getLastFile();
                File resultImageFile = selectImageManager.getScaledFile(sourceImageFile,
                        SelectImageManager.SIZE_IN_PX_2_MP, 0);

                Answer answer = question.getAnswers()[currentSelectedPhoto];
                boolean needAddEmptyAnswer = !answer.isChecked();

                answer.setChecked(true);
                answer.setFileUri(Uri.fromFile(resultImageFile).getPath());
                answer.setFileSizeB(resultImageFile.length());
                answer.setFileName(resultImageFile.getName());
                answer.setValue(resultImageFile.getName());

                AnswersBL.updateAnswersToDB(handler, question.getAnswers());

                if (needAddEmptyAnswer && question.getAnswers().length < question.getMaximumPhotos()) {
                    question.setAnswers(addEmptyAnswer(question.getAnswers()));
                }

                refreshPhotoGallery(question.getAnswers());

                isBitmapConfirmed = true;

                refreshRePhotoButton();
                refreshConfirmButton();
                refreshNextButton();

                ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
                break;
            default:
                break;
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
        public void onImageComplete(Bitmap bitmap) {
            isBitmapAdded = bitmap != null;
            isBitmapConfirmed = false;

            if (bitmap != null) {
                photoImageView.setImageBitmap(bitmap);
            } else {
                photoImageView.setImageResource(R.drawable.btn_camera_error_selector);
            }

            refreshRePhotoButton();
            refreshConfirmButton();
            refreshNextButton();
        }

        @Override
        public void onSelectImageError(int imageFrom) {
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

        if (!TextUtils.isEmpty(answer.getFileUri())) {
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
}