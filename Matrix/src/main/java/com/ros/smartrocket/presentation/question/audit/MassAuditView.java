package com.ros.smartrocket.presentation.question.audit;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.question.CustomFieldImageUrls;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.presentation.question.audit.additional.CategoryProductPair;
import com.ros.smartrocket.presentation.question.audit.additional.TickCrossAnswerPair;
import com.ros.smartrocket.presentation.question.base.BaseQuestionView;
import com.ros.smartrocket.ui.adapter.MassAuditExpandableListAdapter;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.utils.eventbus.SubQuestionsSubmitEvent;

import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MassAuditView extends BaseQuestionView<MassAuditMvpPresenter<MassAuditMvpView>> implements MassAuditMvpView {
    public static final int TICK = 1;
    public static final int CROSS = 2;
    public static final String STATE_BUTTON_CLICKED = "QuestionMassAuditBL.STATE_BUTTON_CLICKED";
    private ExpandableListView listView;
    private CustomTextView massAuditMainSubQuestionText;
    private MassAuditExpandableListAdapter adapter;
    private int buttonClicked;

    public MassAuditView(Context context) {
        super(context);
    }

    public MassAuditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MassAuditView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        LayoutInflater.from(getContext()).inflate(getLayoutResId(), this, true);
        listView = (ExpandableListView) findViewById(R.id.massAuditExpandableListView);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.view_mass_question;
    }

    @Override
    public void configureView(Question question) {
        if (state != null && state.containsKey(STATE_BUTTON_CLICKED))
            buttonClicked = state.getInt(STATE_BUTTON_CLICKED);
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.include_mass_audit_question_header, listView, false);
        massAuditMainSubQuestionText = (CustomTextView) headerView.findViewById(R.id.massAuditMainSubQuestionText);
        presetValidationComment = (TextView) headerView.findViewById(R.id.presetValidationComment);
        validationComment = (TextView) headerView.findViewById(R.id.validationComment);
        questionText = (TextView) headerView.findViewById(R.id.questionText);
        listView.addHeaderView(headerView);
        if (presenter.isRedo())
            presenter.refreshNextButton();
    }

    @Override
    public void fillViewWithAnswers(List<Answer> answers) {
        // not needed
    }

    @Override
    public void fillViewWithCustomFieldImageUrls(List<CustomFieldImageUrls> customFieldImageUrlsList) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_BUTTON_CLICKED, buttonClicked);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void showMainSubQuestionText(String text) {
        massAuditMainSubQuestionText.setMovementMethod(LinkMovementMethod.getInstance());
        massAuditMainSubQuestionText.setText(Html.fromHtml(text));
    }

    @Override
    public void showAnswersList(HashMap<Integer, TickCrossAnswerPair> answersMap) {
        adapter = new MassAuditExpandableListAdapter(getContext(), presenter.getQuestion(),
                tickListener, crossListener, thumbListener);
        adapter.setData(answersMap);
        fillList();
    }

    @Override
    public void showRedoAnswersList(HashMap<Integer, TickCrossAnswerPair> answersMap, HashMap<Integer, Boolean> answersReDoMap,
                                    List<Question> mainSubList) {
        adapter = new MassAuditExpandableListAdapter(getContext(), presenter.getQuestion(),
                tickListener, crossListener, thumbListener, mainSubList);
        adapter.setData(answersMap, answersReDoMap);
        fillList();
    }

    @Override
    public void refreshAdapter() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setRedoData(HashMap<Integer, Boolean> answersReDoMap) {
        adapter.setReDoData(answersReDoMap);
    }

    private void fillList() {
        listView.setAdapter(adapter);
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            listView.expandGroup(i);
        }
    }

    private View.OnClickListener crossListener = v -> {
        buttonClicked = CROSS;
        handleTickCrossTick((CategoryProductPair) v.getTag());
    };

    private View.OnClickListener tickListener = v -> {
        buttonClicked = TICK;
        handleTickCrossTick((CategoryProductPair) v.getTag());
    };

    private View.OnClickListener thumbListener = v -> {
        presenter.openThumbnail((String) v.getTag());
    };

    private void handleTickCrossTick(CategoryProductPair pair) {
        presenter.handleTickCrossTick(pair, buttonClicked);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(SubQuestionsSubmitEvent event) {
        if (presenter != null) presenter.onEventReceived(event, buttonClicked);
    }


    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
    }

}
