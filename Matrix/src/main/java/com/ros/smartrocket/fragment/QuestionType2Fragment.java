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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.adapter.AnswerCheckBoxAdapter;
import com.ros.smartrocket.adapter.AnswerRadioBattonAdapter;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;

/**
 * Fragment for display About information
 */
public class QuestionType2Fragment extends BaseQuestionFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = QuestionType2Fragment.class.getSimpleName();
    private ViewGroup view;
    private ListView list;
    private TextView questionText;
    private AnswerRadioBattonAdapter adapter;
    private Question question;

    private AsyncQueryHandler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        view = (ViewGroup) localInflater.inflate(R.layout.fragment_question_type_2, null);

        if(getArguments()!=null){
            question = (Question) getArguments().getSerializable(Keys.QUESTION);
        }

        handler = new DbHandler(getActivity().getContentResolver());

        list = (ListView) view.findViewById(R.id.answerList);
        list.setOnItemClickListener(this);

        questionText = (TextView) view.findViewById(R.id.questionText);

        adapter = new AnswerRadioBattonAdapter(getActivity());
        list.setAdapter(adapter);

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
                    QuestionType2Fragment.this.question.setAnswers(AnswersBL.convertCursorToAnswersArray(cursor));

                    adapter.setData(question.getAnswers());
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

    @Override
    public void onItemClick(AdapterView<?> arg0, View item, int position, long id) {
        Answer answer = adapter.getItem(position);
        answer.toggleChecked();
        //adapter.notifyDataSetChanged();

        AnswerCheckBoxAdapter.ViewHolder viewHolder = (AnswerCheckBoxAdapter.ViewHolder) item.getTag();
        viewHolder.checkBox.setChecked(answer.isChecked());
    }
}