package com.ros.smartrocket.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.adapter.AnswerCheckBoxAdapter;
import com.ros.smartrocket.adapter.AnswerRadioButtonAdapter;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;

/**
 * Fragment for display About information
 */
public class QuestionType2Fragment extends BaseQuestionFragment implements AdapterView.OnItemClickListener {
    //private static final String TAG = QuestionType2Fragment.class.getSimpleName();
    private AnswerRadioButtonAdapter adapter;
    private Question question;
    private OnAnswerSelectedListener answerSelectedListener;
    private OnAnswerPageLoadingFinishedListener answerPageLoadingFinishedListener;

    private AsyncQueryHandler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        ViewGroup view = (ViewGroup) localInflater.inflate(R.layout.fragment_question_type_2, null);

        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable(Keys.QUESTION);
        }

        handler = new DbHandler(getActivity().getContentResolver());

        ListView list = (ListView) view.findViewById(R.id.answerList);
        list.setOnItemClickListener(this);

        TextView questionText = (TextView) view.findViewById(R.id.questionText);
        if(!TextUtils.isEmpty(question.getValidationComment())){
            TextView validationComment = (TextView) view.findViewById(R.id.validationComment);
            validationComment.setText(question.getValidationComment());
            validationComment.setVisibility(View.VISIBLE);
        }

        adapter = new AnswerRadioButtonAdapter(getActivity());
        list.setAdapter(adapter);

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
                    QuestionType2Fragment.this.question.setAnswers(AnswersBL.convertCursorToAnswersArray(cursor));

                    adapter.setData(question.getAnswers());

                    refreshNextButton();
                    break;
                default:
                    break;
            }
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

    public void refreshNextButton() {
        if (answerSelectedListener != null) {
            boolean selected = false;
            for (Answer answer : adapter.getData()) {
                if (answer.isChecked()) {
                    selected = true;
                    break;
                }
            }
            answerSelectedListener.onAnswerSelected(selected);
        }

        if (answerPageLoadingFinishedListener != null) {
            answerPageLoadingFinishedListener.onAnswerPageLoadingFinished();
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

    @Override
    public void onItemClick(AdapterView<?> arg0, View item, int position, long id) {
        Answer answer = adapter.getItem(position);
        answer.toggleChecked();
        //adapter.notifyDataSetChanged();

        AnswerCheckBoxAdapter.ViewHolder viewHolder = (AnswerCheckBoxAdapter.ViewHolder) item.getTag();
        viewHolder.getCheckBox().setChecked(answer.isChecked());

        refreshNextButton();
    }
}
