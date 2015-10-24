package com.ros.smartrocket.bl.question;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import butterknife.Bind;
import com.ros.smartrocket.R;
import com.ros.smartrocket.adapter.MassAuditExpandableListAdapter;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.fragment.SubQuestionsMassAuditFragment;
import com.ros.smartrocket.utils.L;

import java.util.List;

public final class QuestionMassAuditBL extends QuestionBaseBL {
    @Bind(R.id.massAuditMainSubQuestionText)
    TextView mainSubQuestionTextView;
    @Bind(R.id.massAuditExpandableListView)
    ExpandableListView listView;

    private FragmentActivity activity;

    @Override
    public void initView(View view, Question question, Bundle savedInstanceState) {
        super.initView(view, question, savedInstanceState);

        activity = (FragmentActivity) view.getContext();
        listView.setAdapter(new MassAuditExpandableListAdapter(activity, question.getCategoriesArray(), tickListener));

        refreshNextButton();
    }

    private View.OnClickListener tickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            L.v("CLICK", v.toString());
            activity.getSupportFragmentManager().beginTransaction()
                    .add(R.id.subquestionsLayout,
                            SubQuestionsMassAuditFragment.makeInstance(question.getChildQuestions(),
                                    mainSubQuestionTextView.getText().toString(), "#subtitle#"))
                    .addToBackStack(null).commit();
        }
    };

    public void subQuestionsLoaded(List<Question> questions) {
        question.setChildQuestions(questions.toArray(new Question[questions.size()]));
        Question mainSub = QuestionsBL.getMainSubQuestion(question);
        if (mainSub != null) {
            mainSubQuestionTextView.setText(mainSub.getQuestion());
        }
    }
}
