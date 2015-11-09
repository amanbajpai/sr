package com.ros.smartrocket.bl.question;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
    public static final int TICK = 1;
    public static final int CROSS = 2;


    @Bind(R.id.massAuditMainSubQuestionText)
    TextView mainSubQuestionTextView;
    @Bind(R.id.massAuditExpandableListView)
    ExpandableListView listView;

    private MassAuditExpandableListAdapter adapter;
    private HashMap<Integer, TickCrossAnswerPair> answersMap;
    private int buttonClicked;
    private Question mainSub;

    @Override
    public void configureView() {
        adapter = new MassAuditExpandableListAdapter(activity, question.getCategoriesArray(),
                tickListener, crossListener);
        listView.setAdapter(adapter);
    }

    @Override
    protected void fillViewWithAnswers(Answer[] answers) {
        question.setAnswers(answers);
        answersMap = convertToMap(answers);
        adapter.setData(answersMap);
        refreshNextButton();
    }

    @Override
    public boolean saveQuestion() {
        if (question != null && question.getAnswers() != null && question.getAnswers().length > 0) {
            AnswersBL.updateAnswersToDB(handler, question.getAnswers());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void refreshNextButton() {
        if (answerSelectedListener != null) {
            boolean selected = true;
            for (TickCrossAnswerPair pair : answersMap.values()) {
                if (!pair.getTickAnswer().getChecked() && !pair.getCrossAnswer().getChecked()) {
                    selected = false;
                    break;
                }
            }
            answerSelectedListener.onAnswerSelected(selected, question.getId());
        }

        if (answerPageLoadingFinishedListener != null) {
            answerPageLoadingFinishedListener.onAnswerPageLoadingFinished();
        }
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
        mainSub = QuestionsBL.getMainSubQuestion(question);
        if (mainSub != null) {
            mainSubQuestionTextView.setText(mainSub.getQuestion());
            AnswersBL.getAnswersListFromDB(handler, mainSub.getTaskId(), mainSub.getMissionId(), mainSub.getId());
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
        updateTickCrossState(event.productId);
    }

    private View.OnClickListener crossListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClicked = CROSS;
            handleTickCrossTick((Product) v.getTag());
        }
    };

    private View.OnClickListener tickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClicked = TICK;
            handleTickCrossTick((Product) v.getTag());
        }
    };

    private void startSubQuestionsFragment(Product item) {
        String title = mainSub != null ? mainSub.getQuestion() : "";
        Fragment f = SubQuestionsMassAuditFragment.makeInstance(question.getChildQuestions(), title, item);
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.subquestionsLayout, f).addToBackStack(null).commit();
    }

    private void handleTickCrossTick(Product itemProduct) {
        if ((buttonClicked == TICK && mainSub.getAction() == Question.ACTION_TICK)
                || (buttonClicked == CROSS && mainSub.getAction() == Question.ACTION_CROSS)
                || (mainSub.getAction() == Question.ACTION_BOTH)) {
            startSubQuestionsFragment(itemProduct);
        } else {
            updateTickCrossState(itemProduct.getId());
        }
    }

    private void updateTickCrossState(int productId) {
        TickCrossAnswerPair pair = answersMap.get(productId);
        pair.getTickAnswer().setChecked(buttonClicked == TICK);
        pair.getCrossAnswer().setChecked(buttonClicked == CROSS);
        adapter.notifyDataSetChanged();
        refreshNextButton();
    }

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
