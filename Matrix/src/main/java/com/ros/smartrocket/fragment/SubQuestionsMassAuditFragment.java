package com.ros.smartrocket.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.ros.smartrocket.R;
import com.ros.smartrocket.adapter.SubQuestionsMassAuditAdapter;
import com.ros.smartrocket.db.entity.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Subquestions fragment
 */
public class SubQuestionsMassAuditFragment extends Fragment {
    public static final String KEY_QUES = "com.ros.smartrocket.fragment.SubQuestionsMassAuditFragment.KEY_QUESTIONS";
    public static final String KEY_TITLE = "com.ros.smartrocket.fragment.SubQuestionsMassAuditFragment.KEY_TITLE";
    public static final String KEY_SUBTITLE = "com.ros.smartrocket.fragment.SubQuestionsMassAuditFragment.KEY_SUBTITLE";
    
    @Bind(R.id.massAuditSubquestionsListView)
    ListView listView;
    @Bind(R.id.massAuditSubQuestionsTitle)
    TextView titleTextView;
    @Bind(R.id.massAuditSubQuestionsSubtitle)
    TextView subtitleTextView;

    public static Fragment makeInstance(Question[] questions, String title, String subtitle) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_QUES, questions);
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_SUBTITLE, subtitle);

        Fragment fragment = new SubQuestionsMassAuditFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mass_audit_subquestions, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        Question[] questions = (Question[]) getArguments().getSerializable(KEY_QUES);
        titleTextView.setText(getArguments().getString(KEY_TITLE));
        subtitleTextView.setText(getArguments().getString(KEY_SUBTITLE));
        
        //TODO Debug
        List<Question> goodQuestions = new ArrayList<>();
        for (Question question : questions) {
            if (question.getType() == Question.QuestionType.NUMBER.getTypeId()
                    || question.getType() == Question.QuestionType.OPEN_COMMENT.getTypeId()
                    || question.getType() == Question.QuestionType.INSTRUCTION.getTypeId()) {
                goodQuestions.add(question);
            }
        }

        SubQuestionsMassAuditAdapter adapter = new SubQuestionsMassAuditAdapter(getActivity(),
                goodQuestions.toArray(new Question[goodQuestions.size()]));
        listView.setAdapter(adapter);
    }
}