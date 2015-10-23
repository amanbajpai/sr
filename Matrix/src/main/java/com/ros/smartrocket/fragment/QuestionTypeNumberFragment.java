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
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.question.QuestionTypeNumberBL;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;

/**
 * Numeric question type
 */
public class QuestionTypeNumberFragment extends BaseQuestionFragment {
    private QuestionTypeNumberBL questionBL;
    private AsyncQueryHandler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        ViewGroup view = (ViewGroup) localInflater.inflate(R.layout.fragment_question_type_6, null);

        Question question = (Question) getArguments().getSerializable(Keys.QUESTION);
        questionBL = new QuestionTypeNumberBL(view, question, savedInstanceState);

        this.handler = new DbHandler(getActivity().getContentResolver());
        AnswersBL.getAnswersListFromDB(handler, question.getTaskId(), question.getMissionId(), question.getId());

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        questionBL.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean saveQuestion() {
        return questionBL.saveQuestion(handler);
    }

    @Override
    public void clearAnswer() {
        questionBL.clearAnswer(handler);
    }

    @Override
    public Question getQuestion() {
        return questionBL.getQuestion();
    }

    @Override
    public void setAnswerSelectedListener(OnAnswerSelectedListener answerSelectedListener) {
        questionBL.setAnswerSelectedListener(answerSelectedListener);
    }

    @Override
    public void setAnswerPageLoadingFinishedListener(OnAnswerPageLoadingFinishedListener listener) {
        questionBL.setAnswerPageLoadingFinishedListener(listener);
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
                    questionBL.fillViewWithAnswers(answers);
                    break;
                default:
                    break;
            }
        }
    }

}
