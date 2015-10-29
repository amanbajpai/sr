package com.ros.smartrocket.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    
    @Bind(R.id.massAuditSubquestionsLayout)
    LinearLayout subQuestionsLayout;
    @Bind(R.id.massAuditSubQuestionsTitle)
    TextView titleTextView;
    @Bind(R.id.massAuditSubQuestionsSubtitle)
    TextView subtitleTextView;

    private SubQuestionsMassAuditAdapter adapter;

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
        
        titleTextView.setText(getArguments().getString(KEY_TITLE));
        subtitleTextView.setText(getArguments().getString(KEY_SUBTITLE));

        Question[] questions = (Question[]) getArguments().getSerializable(KEY_QUES);
        List<Question> questionsWithoutMain = new ArrayList<>();
        if (questions != null) {
            for (Question question : questions) {
                if (question.getType() != Question.QuestionType.MAIN_SUB_QUESTION.getTypeId()) {
                    questionsWithoutMain.add(question);
                }
            }
        }

        adapter = new SubQuestionsMassAuditAdapter(getActivity(), this,
                questionsWithoutMain.toArray(new Question[questionsWithoutMain.size()]));
        for (int i = 0; i < adapter.getCount(); i++) {
            View item = adapter.getView(i, null, subQuestionsLayout);
            if (item != null && item.getParent() == null) {
                subQuestionsLayout.addView(item);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.onStart();
    }

    @Override
    public void onStop() {
        adapter.onStop();
        super.onStart();
    }

    @Override
    public void onDestroy() {
        adapter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        adapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!adapter.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.cancelSubQuestionsButton)
    void cancelClick() {
        getFragmentManager().popBackStack();
    }
}