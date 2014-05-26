package com.ros.smartrocket.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
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
import android.widget.TextView;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.images.ImageLoader;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.SelectImageManager;

import java.io.File;

/**
 * Fragment for display About information
 */
public class QuestionType3Fragment extends BaseQuestionFragment implements View.OnClickListener {
    //private static final String TAG = QuestionType3Fragment.class.getSimpleName();
    private SelectImageManager selectImageManager = SelectImageManager.getInstance();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ViewGroup view;
    private TextView questionText;
    private ImageButton rePhotoButton;
    private ImageButton confirmButton;
    private ImageView photoImageView;
    private boolean isBitmapAdded = false;
    private boolean isBitmapConfirmed = false;
    private Question question;
    private OnAnswerSelectedListener answerSelectedListener;
    private OnAnswerPageLoadingFinishedListener answerPageLoadingFinishedListener;

    private AsyncQueryHandler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        view = (ViewGroup) localInflater.inflate(R.layout.fragment_question_type_3, null);

        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable(Keys.QUESTION);
        }

        handler = new DbHandler(getActivity().getContentResolver());

        questionText = (TextView) view.findViewById(R.id.questionText);

        if (!TextUtils.isEmpty(question.getValidationComment())) {
            TextView validationComment = (TextView) view.findViewById(R.id.validationComment);
            validationComment.setText(question.getValidationComment());
            validationComment.setVisibility(View.VISIBLE);

            questionText.setVisibility(View.GONE);
        }

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
                    QuestionType3Fragment.this.question.setAnswers(answers);

                    Answer answer = answers[0];
                    if (answer.isChecked() && answer.getFileUri() != null) {
                        isBitmapAdded = true;
                        isBitmapConfirmed = true;

                        Bitmap bitmap = SelectImageManager.prepareBitmap(new File(answer.getFileUri()));
                        photoImageView.setImageBitmap(bitmap);
                    } else {
                        isBitmapAdded = false;
                        isBitmapConfirmed = false;
                    }

                    refreshRePhotoButton();
                    refreshConfirmButton();
                    refreshNextButton();
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
                selectImageManager.setImageCompleteListener(new SelectImageManager.OnImageCompleteListener() {
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
                });
                break;
            case R.id.confirmButton:
                ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);

                File sourceImageFile = selectImageManager.getLastFile();
                File resultImageFile = selectImageManager.getScaledFile(sourceImageFile,
                        SelectImageManager.SIZE_IN_PX_2_MP, 0);

                Answer answer = question.getAnswers()[0];
                answer.setChecked(true);
                answer.setFileUri(Uri.fromFile(resultImageFile).getPath());
                answer.setFileSizeB(resultImageFile.length());
                answer.setFileName(resultImageFile.getName());

                AnswersBL.setAnswersToDB(handler, question.getAnswers());

                isBitmapConfirmed = true;

                refreshRePhotoButton();
                refreshConfirmButton();
                refreshNextButton();
                break;
            default:
                break;
        }
    }

    @Override
    public void setAnswerSelectedListener(OnAnswerSelectedListener answerSelectedListener) {
        this.answerSelectedListener = answerSelectedListener;
    }

    @Override
    public void setAnswerPageLoadingFinishedListener(OnAnswerPageLoadingFinishedListener
                                                             answerPageLoadingFinishedListener) {
        this.answerPageLoadingFinishedListener = answerPageLoadingFinishedListener;
    }

}
