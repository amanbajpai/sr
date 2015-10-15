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
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.adapter.MassAuditExpandableListAdapter;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;

import java.util.List;

/**
 * Mass Audit question type
 */
public class QuestionTypeMassAuditFragment extends BaseQuestionFragment {
    private Question question;
    private OnAnswerSelectedListener answerSelectedListener;
    private OnAnswerPageLoadingFinishedListener answerPageLoadingFinishedListener;
    private AsyncQueryHandler handler;
    private TextView mainSubQuestionTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.fragment_question_type_mass_audit, null);

        ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.massAuditExpandableListView);
        TextView questionTextView = (TextView) view.findViewById(R.id.massAuditQuestionText);
        mainSubQuestionTextView = (TextView) view.findViewById(R.id.massAuditMainSubQuestionText);

        handler = new DbHandler(getActivity().getContentResolver());

        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable(Keys.QUESTION);
        }

        if (question != null) {
            listView.setAdapter(new MassAuditExpandableListAdapter(getActivity(), question.getCategoriesArray()));
            questionTextView.setText(question.getQuestion());
        }

        refreshNextButton();

        QuestionsBL.getChildQuestionsListFromDB(handler, question.getTaskId(), question.getId());

        return view;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public boolean saveQuestion() {
        return true;
    }

    @Override
    public Question getQuestion() {
        return question;
    }

    public void refreshNextButton() {
        if (answerSelectedListener != null) {
            answerSelectedListener.onAnswerSelected(true);
        }

        if (answerPageLoadingFinishedListener != null) {
            answerPageLoadingFinishedListener.onAnswerPageLoadingFinished();
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

    class DbHandler extends AsyncQueryHandler {

        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case QuestionDbSchema.Query.TOKEN_QUERY:
                    List<Question> questions = QuestionsBL.convertCursorToQuestionList(cursor);
                    question.setChildQuestions(questions.toArray(new Question[questions.size()]));

                    Question mainSub = QuestionsBL.getMainSubQuestion(question);
                    if (mainSub != null) {
                        mainSubQuestionTextView.setText(mainSub.getQuestion());
                    }
                    break;
                default:
                    break;
            }
        }
    }
}