package com.ros.smartrocket.bl.question;

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

    @Override
    public void configureView() {
        listView.setAdapter(new MassAuditExpandableListAdapter(activity, question.getCategoriesArray(), tickListener));
        refreshNextButton();
    }

    @Override
    public void loadAnswers() {
        // Do nothing
    }

    private View.OnClickListener tickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            L.v("CLICK", v.toString());
            getActivity().getSupportFragmentManager().beginTransaction()
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
