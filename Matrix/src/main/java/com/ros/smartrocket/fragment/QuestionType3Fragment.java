package com.ros.smartrocket.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.utils.BytesBitmap;
import com.ros.smartrocket.utils.SelectImageManager;

/**
 * Fragment for display About information
 */
public class QuestionType3Fragment extends BaseQuestionFragment implements View.OnClickListener {
    private static final String TAG = QuestionType3Fragment.class.getSimpleName();
    private SelectImageManager selectImageManager = SelectImageManager.getInstance();
    private ViewGroup view;
    private TextView questionText;
    private ImageView photoImageView;
    private Bitmap photoBitmap;
    private Question question;

    private AsyncQueryHandler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        view = (ViewGroup) localInflater.inflate(R.layout.fragment_question_type_3, null);

        if(getArguments()!=null){
            question = (Question) getArguments().getSerializable(Keys.QUESTION);
        }

        handler = new DbHandler(getActivity().getContentResolver());

        questionText = (TextView) view.findViewById(R.id.questionText);
        photoImageView = (ImageView) view.findViewById(R.id.photo);

        view.findViewById(R.id.rePhotoButton).setOnClickListener(this);

        questionText.setText(question.getQuestion());
        AnswersBL.getAnswersListFromDB(handler, question.getId());

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

                    if (answers.length > 0) {
                        Answer answer = answers[0];
                        if (answer.getImageByteArray() != null) {
                            photoImageView.setImageBitmap(BytesBitmap.getBitmap(answer.getImageByteArray()));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void saveQuestion() {
        AnswersBL.setAnswersToDB(handler, question.getAnswers());
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
            case R.id.rePhotoButton:
                selectImageManager.showSelectImageDialog(getActivity(), true, new SelectImageManager.OnImageCompleteListener() {
                    @Override
                    public void onImageComplete(Bitmap bitmap) {
                        QuestionType3Fragment.this.photoBitmap = bitmap;
                        if (bitmap != null) {
                            photoImageView.setImageBitmap(bitmap);
                        } else {
                            photoImageView.setImageResource(R.drawable.no_photo);
                        }
                        //TODO set image byte array to answer
                        //question.getAnswers() = new Answer[]
                    }
                });
                break;
            case R.id.confirmButton:

                break;
            default:
                break;
        }
    }
}
