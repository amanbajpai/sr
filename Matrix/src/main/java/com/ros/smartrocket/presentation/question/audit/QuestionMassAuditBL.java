package com.ros.smartrocket.presentation.question.audit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.QuestionBaseBL;
import com.ros.smartrocket.ui.adapter.MassAuditExpandableListAdapter;
import com.ros.smartrocket.db.bl.AnswersBL;
import com.ros.smartrocket.db.bl.QuestionsBL;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Category;
import com.ros.smartrocket.db.entity.Product;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.ui.dialog.ProductImageDialog;
import com.ros.smartrocket.utils.eventbus.SubQuestionsSubmitEvent;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.EventBus;

public final class QuestionMassAuditBL extends QuestionBaseBL {
    public static final String STATE_BUTTON_CLICKED = "QuestionMassAuditBL.STATE_BUTTON_CLICKED";
    public static final int TICK = 1;
    public static final int CROSS = 2;

    TextView mainSubQuestionTextView;
    @BindView(R.id.massAuditExpandableListView)
    ExpandableListView listView;

    private MassAuditExpandableListAdapter adapter;
    private HashMap<Integer, TickCrossAnswerPair> answersMap;
    private Question mainSub;
    private List<Question> mainSubList;
    private int buttonClicked;
    private boolean isRedo = false;
    private boolean isPreview = false;
    private HashMap<Integer, Boolean> answersReDoMap = new HashMap<>();

    @Override
    public void configureView() {
        View headerView = getActivity().getLayoutInflater().inflate(
                R.layout.include_mass_audit_question_header, listView, false);
        listView.addHeaderView(headerView);

        mainSubQuestionTextView = (TextView) view.findViewById(R.id.massAuditMainSubQuestionText);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_BUTTON_CLICKED)) {
            buttonClicked = savedInstanceState.getInt(STATE_BUTTON_CLICKED);
        }
        if (isRedo) {
            refreshNextButton();
        }
    }

    @Override
    protected void fillViewWithAnswers(Answer[] answers) {
        question.setAnswers(answers);
        answersMap = convertToMap(answers);
        if (!isRedo) {
            adapter = new MassAuditExpandableListAdapter(activity, question.getCategoriesArray(),
                    tickListener, crossListener, thumbListener, question.getOrderId());
            adapter.setData(answersMap);
        } else {
            answersReDoMap.putAll(convertToReDoMap(answers));
            adapter = new MassAuditExpandableListAdapter(activity, question.getCategoriesArray(),
                    tickListener, crossListener, thumbListener, mainSubList, question.getOrderId());
            adapter.setData(answersMap, answersReDoMap);
        }

        listView.setAdapter(adapter);
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            listView.expandGroup(i);
        }
        refreshNextButton();
    }


    @NonNull
    private HashMap<Integer, Boolean> convertToReDoMap(Answer[] answers) {
        HashMap<Integer, Boolean> map = new HashMap<>();

        for (Answer answer : answers) {
            if (map.get(answer.getProductId()) == null) {
                prepareAnswer(answer);
                map.put(answer.getProductId(), answer.getChecked() ? true : false);
            }
        }

        return map;
    }

    private void prepareAnswer(Answer answer) {
        for (Question question : mainSubList) {
            if (answer.getQuestionId().equals(question.getId()) && !question.isRedo()) {
                answer.setChecked(false);
                return;
            }
        }
    }

    @Override
    public boolean saveQuestion() {
        if (question != null && question.getAnswers() != null && question.getAnswers().length > 0) {
            AnswersBL.updateAnswersToDB(handler, question.getAnswers());
            return true;
        } else {
            return question != null && answersReDoMap != null && answersReDoMap.size() > 0;
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_BUTTON_CLICKED, buttonClicked);
        super.onSaveInstanceState(outState);
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
        Question[] subQuestions = new Question[questions.size()];
        subQuestions = questions.toArray(subQuestions);
        question.setChildQuestions(subQuestions);
        if (subQuestions != null) {
            mainSub = QuestionsBL.getMainSubQuestion(subQuestions);
            if (isRedo) {
                mainSubList = QuestionsBL.getReDoMainSubQuestionList(subQuestions);
            }
        }
        if (mainSub != null) {
            mainSubQuestionTextView.setMovementMethod(LinkMovementMethod.getInstance());
            mainSubQuestionTextView.setText(Html.fromHtml(mainSub.getQuestion()));
            AnswersBL.getAnswersListFromDB(handler, mainSub.getTaskId(), mainSub.getMissionId(), mainSub.getId());
        } else {
            AnswersBL.getSubQuestionsAnswersListFromDB(handler, question.getTaskId(), question.getMissionId(),
                    question.getChildQuestions());
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
        if (!isRedo) {
            saveQuestion();
            updateTickCrossState(event.productId);
        } else {
            updateTickCrossState(event.productId);
            answersReDoMap.put(event.productId, true);
            adapter.setReDoData(answersReDoMap);
            refreshNextButton();
        }
    }

    private View.OnClickListener crossListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClicked = CROSS;
            handleTickCrossTick((CategoryProductPair) v.getTag());
        }
    };

    private View.OnClickListener tickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClicked = TICK;
            handleTickCrossTick((CategoryProductPair) v.getTag());
        }
    };

    private View.OnClickListener thumbListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String url = (String) v.getTag();
            ProductImageDialog.showDialog(activity.getSupportFragmentManager(), url);
        }
    };

    private void startSubQuestionsFragment(CategoryProductPair item) {
        Fragment f = SubQuestionsMassAuditFragment.makeInstance(question.getChildQuestions(),
                item.category.getCategoryName(), item.product, isRedo, isPreview, item.productPosition);
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.subquestionsLayout, f).addToBackStack(null).commit();
    }

    private void handleTickCrossTick(CategoryProductPair pair) {
        if ((buttonClicked == TICK && mainSub.getAction() == Question.ACTION_TICK)
                || (buttonClicked == CROSS && mainSub.getAction() == Question.ACTION_CROSS)
                || (mainSub.getAction() == Question.ACTION_BOTH)) {
            if (isRedo && pair.product.getId() != null && question.getChildQuestions() != null) {
                if (QuestionsBL.hasReDoNotMainSub(question.getChildQuestions(), pair.product.getId())) {
                    startSubQuestionsFragment(pair);
                } else {
                    updateRedoAnswers(pair.product.getId());
                    saveQuestion();
                    updateTickCrossState(pair.product.getId());
                }
            } else {
                startSubQuestionsFragment(pair);
            }
        } else {
            if (isRedo) {
                saveQuestion();
                updateRedoAnswers(pair.product.getId());
            }
            updateTickCrossState(pair.product.getId());
        }
    }

    private void updateRedoAnswers(Integer productId) {
        answersReDoMap.put(productId, true);
        adapter.setReDoData(answersReDoMap);
    }

    private void updateTickCrossState(int productId) {
        TickCrossAnswerPair pair = answersMap.get(productId);
        if (pair != null) {
            pair.getTickAnswer().setChecked(buttonClicked == TICK);
            pair.getCrossAnswer().setChecked(buttonClicked == CROSS);
            if (buttonClicked == CROSS && mainSub.getAction() != Question.ACTION_BOTH && mainSub.getAction() != Question.ACTION_CROSS) {
                AnswersBL.clearSubAnswersInDB(mainSub.getTaskId(), mainSub.getMissionId(), productId, question.getChildQuestions());
            }
            adapter.notifyDataSetChanged();
            refreshNextButton();
        }
    }

    public void setIsRedo(boolean b) {
        isRedo = b;
    }

    public void setIsPreview(boolean preview) {
        isPreview = preview;
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

    public static class CategoryProductPair {
        public final Category category;
        public final Product product;
        public final int productPosition;

        public CategoryProductPair(Category category, Product product, int productPos) {
            this.category = category;
            this.product = product;
            this.productPosition = productPos;
        }
    }
}
