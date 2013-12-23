package com.ros.smartrocket.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;

/**
 * Fragment for display About information
 */
public class QuestionType4Fragment extends BaseQuestionFragment {
    private static final String TAG = QuestionType4Fragment.class.getSimpleName();
    private ViewGroup view;
    private TextView questionText;
    private EditText answerEditText;
    private Question question;

    private AsyncQueryHandler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        view = (ViewGroup) localInflater.inflate(R.layout.fragment_question_type_4, null);

        handler = new DbHandler(getActivity().getContentResolver());

        questionText = (TextView) view.findViewById(R.id.questionText);
        answerEditText = (EditText) view.findViewById(R.id.answerEditText);

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
                    QuestionType4Fragment.this.question.setAnswers(answers);

                    if (answers.length > 0) {
                        answerEditText.setText(answers[0].getText());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setQuestion(Question question) {
        this.question = question;
        questionText.setText(question.getDescription());
        AnswersBL.getAnswersListFromDB(handler, question.getId());
    }

    @Override
    public void saveQuestion() {
        AnswersBL.setAnswersToDB(handler, question.getAnswers());
    }

    @Override
    public Question getQuestion() {
        return question;
    }

}
