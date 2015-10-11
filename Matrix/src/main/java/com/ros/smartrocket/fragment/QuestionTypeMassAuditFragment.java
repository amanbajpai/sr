package com.ros.smartrocket.fragment;

import android.content.AsyncQueryHandler;
import android.content.Context;
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
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;

/**
 * Mass Audit question type
 */
public class QuestionTypeMassAuditFragment extends BaseQuestionFragment {
    private Question question;
    private OnAnswerSelectedListener answerSelectedListener;
    private OnAnswerPageLoadingFinishedListener answerPageLoadingFinishedListener;

    private AsyncQueryHandler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.fragment_question_type_mass_audit, null);

        ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.massAuditExpandableListView);
        TextView questionTextView = (TextView) view.findViewById(R.id.massAuditQuestionText);

        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable(Keys.QUESTION);
            questionTextView.setText(question != null ? question.getQuestion() : "");
        }

        if (question != null) {
            listView.setAdapter(new MassAuditExpandableListAdapter(getActivity(), question.getCategoriesArray()));
        }

//        handler = new DbHandler(getActivity().getContentResolver());
//        AnswersBL.getAnswersListFromDB(handler, question.getTaskId(), question.getMissionId(), question.getId());

        return view;
    }

//    class DbHandler extends AsyncQueryHandler {
//
//        public DbHandler(ContentResolver cr) {
//            super(cr);
//        }
//
//        @Override
//        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
//            switch (token) {
//                case AnswerDbSchema.Query.TOKEN_QUERY:
//                    Answer[] answers = AnswersBL.convertCursorToAnswersArray(cursor);
//                    QuestionTypeMassAuditFragment.this.question.setAnswers(answers);
////                    adapter.setData(question.getAnswers());
//
////                    refreshNextButton();
//                    break;
//                default:
//                    break;
//            }
//        }
//    }

//    public void refreshNextButton() {
//        if (answerSelectedListener != null) {
//            boolean selected = false;
//            for (Answer answer : adapter.getData()) {
//                if (answer.getChecked()) {
//                    selected = true;
//                    break;
//                }
//            }
//            answerSelectedListener.onAnswerSelected(selected);
//        }
//
//        if (answerPageLoadingFinishedListener != null) {
//            answerPageLoadingFinishedListener.onAnswerPageLoadingFinished();
//        }
//    }

//    @Override
//    public boolean saveQuestion() {
//        if (question != null && question.getAnswers() != null && question.getAnswers().length > 0) {
//            AnswersBL.updateAnswersToDB(handler, question.getAnswers());
//            return true;
//        } else {
//            return false;
//        }
//    }

//    @Override
//    public void clearAnswer() {
//        if (question != null && question.getAnswers() != null && question.getAnswers().length > 0) {
//            Answer[] answers = question.getAnswers();
//            for (Answer answer : answers) {
//                answer.setChecked(false);
//            }
//
//            AnswersBL.updateAnswersToDB(handler, answers);
//        }
//    }

    @Override
    public Question getQuestion() {
        return question;
    }

    @Override
    public void setAnswerSelectedListener(OnAnswerSelectedListener answerSelectedListener) {
        this.answerSelectedListener = answerSelectedListener;
    }

    @Override
    public void setAnswerPageLoadingFinishedListener(OnAnswerPageLoadingFinishedListener
                                                             answerPageLoadingFinishedListener) {
        this.answerPageLoadingFinishedListener = answerPageLoadingFinishedListener;
    }}