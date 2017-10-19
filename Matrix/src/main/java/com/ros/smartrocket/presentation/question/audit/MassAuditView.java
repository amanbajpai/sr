package com.ros.smartrocket.presentation.question.audit;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.presentation.question.audit.additional.CategoryProductPair;
import com.ros.smartrocket.presentation.question.audit.additional.TickCrossAnswerPair;
import com.ros.smartrocket.presentation.question.base.BaseQuestionView;
import com.ros.smartrocket.ui.adapter.MassAuditExpandableListAdapter;
import com.ros.smartrocket.ui.views.CustomTextView;

import java.util.HashMap;
import java.util.List;

public class MassAuditView extends BaseQuestionView<MassAuditMvpPresenter<MassAuditMvpView>> implements MassAuditMvpView {
    ExpandableListView listView;
    CustomTextView massAuditMainSubQuestionText;
    private MassAuditExpandableListAdapter adapter;
    public static final int TICK = 1;
    public static final int CROSS = 2;
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
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.include_mass_audit_question_header, listView, false);
        massAuditMainSubQuestionText = (CustomTextView) headerView.findViewById(R.id.massAuditMainSubQuestionText);
        presetValidationComment = (TextView) headerView.findViewById(R.id.presetValidationComment);
        validationComment = (TextView) headerView.findViewById(R.id.validationComment);
        questionText = (TextView) headerView.findViewById(R.id.questionText);
        listView.addHeaderView(headerView);
//        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_BUTTON_CLICKED))
//            buttonClicked = savedInstanceState.getInt(STATE_BUTTON_CLICKED);
        if (presenter.isRedo())
            presenter.refreshNextButton();
    }

    @Override
    public void fillViewWithAnswers(List<Answer> answers) {
        // not needed
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
}
