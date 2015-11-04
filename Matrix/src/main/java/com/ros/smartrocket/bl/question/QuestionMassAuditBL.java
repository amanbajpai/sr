package com.ros.smartrocket.bl.question;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import butterknife.Bind;
import com.ros.smartrocket.R;
import com.ros.smartrocket.adapter.MassAuditExpandableListAdapter;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Product;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.eventbus.SubQuestionsSubmitEvent;
import com.ros.smartrocket.fragment.SubQuestionsMassAuditFragment;
import de.greenrobot.event.EventBus;

import java.util.HashMap;
import java.util.List;

public final class QuestionMassAuditBL extends QuestionBaseBL {
    @Bind(R.id.massAuditMainSubQuestionText)
    TextView mainSubQuestionTextView;
    @Bind(R.id.massAuditExpandableListView)
    ExpandableListView listView;

    private MassAuditExpandableListAdapter adapter;
    private HashMap<Integer, TickCrossAnswerPair> answersMap;

    @Override
    public void configureView() {
        adapter = new MassAuditExpandableListAdapter(activity, question.getCategoriesArray(),
                tickListener, crossListener);
        listView.setAdapter(adapter);
        refreshNextButton();
    }

    @Override
    protected void fillViewWithAnswers(Answer[] answers) {
        question.setAnswers(answers);
        answersMap = convertToMap(answers);
        adapter.setData(answersMap);
        refreshNextButton();
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void loadAnswers() {
        // No
    }

    public void subQuestionsLoaded(List<Question> questions) {
        question.setChildQuestions(questions.toArray(new Question[questions.size()]));
        Question mainSub = QuestionsBL.getMainSubQuestion(question);
        if (mainSub != null) {
            mainSubQuestionTextView.setText(mainSub.getQuestion());
            AnswersBL.getAnswersListFromDB(handler, question.getTaskId(), question.getMissionId(), mainSub.getId());
        }
    }

    @NonNull
    private HashMap<Integer, TickCrossAnswerPair> convertToMap(Answer[] answers) {
        HashMap<Integer, TickCrossAnswerPair> map = new HashMap<>();

        for (Answer answer : answers) {
            if (map.get(answer.getProductId()) == null) {
                map.put(answer.getProductId(), new TickCrossAnswerPair());
            }

            TickCrossAnswerPair pair = map.get(answer.getProductId());
            if (answer.getValue().equals("1")) {
                pair.tickAnswer = answer;
            } else {
                pair.crossAnswer = answer;
            }
        }

        return map;
    }

    /// ======================================================================================================== ///
    /// ========================================== LISTENERS =================================================== ///
    /// ======================================================================================================== ///

    @SuppressWarnings("unused")
    public void onEventMainThread(SubQuestionsSubmitEvent event) {
        answersMap.get(event.productId).getTickAnswer().setChecked(true);
        answersMap.get(event.productId).getCrossAnswer().setChecked(false);
        adapter.notifyDataSetChanged();
    }

    private View.OnClickListener crossListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Product itemProduct = (Product) v.getTag();
            TickCrossAnswerPair pair = answersMap.get(itemProduct.getId());

            pair.getTickAnswer().setChecked(false);
            pair.getCrossAnswer().setChecked(true);
        }
    };

    private View.OnTouchListener tickListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Product itemProduct = (Product) v.getTag();

                Question mainSubQuestion = QuestionsBL.getMainSubQuestion(question);
                String title = mainSubQuestion != null ? mainSubQuestion.getQuestion() : "";

                Fragment f = SubQuestionsMassAuditFragment.makeInstance(question.getChildQuestions(), title,
                        itemProduct);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.subquestionsLayout, f).addToBackStack(null).commit();

                return true;
            }

            return false;
        }
    };

    public static class TickCrossAnswerPair {
        private Answer tickAnswer;
        private Answer crossAnswer;

        public Answer getTickAnswer() {
            return tickAnswer;
        }

        public void setTickAnswer(Answer tickAnswer) {
            this.tickAnswer = tickAnswer;
        }

        public Answer getCrossAnswer() {
            return crossAnswer;
        }

        public void setCrossAnswer(Answer crossAnswer) {
            this.crossAnswer = crossAnswer;
        }
    }
}
