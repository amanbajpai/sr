package com.ros.smartrocket.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.adapter.SubQuestionsMassAuditAdapter;
import com.ros.smartrocket.db.entity.Product;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.eventbus.SubQuestionsSubmitEvent;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Subquestions fragment
 */
public class SubQuestionsMassAuditFragment extends Fragment implements
        OnAnswerSelectedListener, OnAnswerPageLoadingFinishedListener {
    public static final String KEY_QUES = "com.ros.smartrocket.fragment.SubQuestionsMassAuditFragment.KEY_QUESTIONS";
    public static final String KEY_TITLE = "com.ros.smartrocket.fragment.SubQuestionsMassAuditFragment.KEY_TITLE";
    public static final String KEY_PRODUCT = "com.ros.smartrocket.fragment.SubQuestionsMassAuditFragment.KEY_PRODUCT";
    public static final String KEY_PRODUCT_POS = "com.ros.smartrocket.fragment.SubQuestionsMassAuditFragment.KEY_PRODUCT_POS";

    @Bind(R.id.massAuditSubquestionsLayout)
    LinearLayout subQuestionsLayout;
    @Bind(R.id.massAuditSubQuestionsTitle)
    TextView titleTextView;
    @Bind(R.id.massAuditSubQuestionsSubtitle)
    TextView subtitleTextView;
    @Bind(R.id.bottomSubQuestionsButtons)
    View bottomSubQuestions;
    @Bind(R.id.submitSubQuestionsButton)
    Button submitButton;

    private LayoutInflater inflater;
    private SubQuestionsMassAuditAdapter adapter;
    private Product product;
    private boolean isRedo;
    private boolean isPreview;
    private int loadedSubQuestionsCount;
    private int productPosition;
    private Set<Integer> set = new HashSet<>();
    private Set<Integer> requiredQuestionsSet = new HashSet<>();

    public static Fragment makeInstance(Question[] questions, String title, Product product,
                                        boolean isRedo, boolean isPreview, int productPosition) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_QUES, questions);
        bundle.putString(KEY_TITLE, title);
        bundle.putInt(KEY_PRODUCT_POS, productPosition);
        bundle.putSerializable(KEY_PRODUCT, product);
        bundle.putBoolean(QuestionMassAuditFragment.KEY_IS_REDO, isRedo);
        bundle.putBoolean(QuestionMassAuditFragment.KEY_IS_PREVIEW, isPreview);
        Fragment fragment = new SubQuestionsMassAuditFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mass_audit_subquestions, null);
        ButterKnife.bind(this, view);
        this.inflater = LayoutInflater.from(getContext());
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        product = (Product) getArguments().getSerializable(KEY_PRODUCT);
        isRedo = getArguments().getBoolean(QuestionMassAuditFragment.KEY_IS_REDO, false);
        isPreview = getArguments().getBoolean(QuestionMassAuditFragment.KEY_IS_PREVIEW, false);
        submitButton.setEnabled(isPreview);
        productPosition = getArguments().getInt(KEY_PRODUCT_POS);

        titleTextView.setText(getArguments().getString(KEY_TITLE));
        subtitleTextView.setText(product != null ? product.getName() : "");

        Question[] questions = (Question[]) getArguments().getSerializable(KEY_QUES);
        ArrayList<Question> childQuestions = new ArrayList<>();
        if (questions != null) {
            int pos = 0;
            for (Question question : questions) {
                if (question.getType() != Question.QuestionType.MAIN_SUB_QUESTION.getTypeId()) {
                    pos++;
                    question.setSubQuestionNumber(getSubQuestionNumber(pos));
                    if (isRedo) {
                        if (question.getProductId() != null && question.getProductId().equals(product.getId())) {
                            childQuestions.add(question);
                        }
                    } else {
                        childQuestions.add(question);
                    }

                }
            }
        }

        for (Question question : childQuestions) {
            if (question.isRequired()) {
                requiredQuestionsSet.add(question.getId());
            }
        }

        adapter = new SubQuestionsMassAuditAdapter(getActivity(), this,
                childQuestions.toArray(new Question[childQuestions.size()]), product);
        int pos = 0;
        int itemsSize = adapter.getCount() - 1;
        for (int i = 0; i < adapter.getCount(); i++) {
            View item = adapter.getView(i, null, subQuestionsLayout, savedInstanceState);
            if (item != null && item.getParent() == null) {
                subQuestionsLayout.addView(item);
                if (pos < itemsSize) {
                    subQuestionsLayout.addView(getDivider());
                }
                pos++;
            }
        }
    }

    private String getSubQuestionNumber(int pos) {
        StringBuffer sb = new StringBuffer("<b>Q");
        sb.append(productPosition);
        sb.append(".");
        sb.append(pos);
        sb.append("</b> ");
        return sb.toString();
    }

    /**
     * @return divider view
     */
    protected View getDivider() {
        LinearLayout div = (LinearLayout) inflater.inflate(R.layout.divider, null, false);
        int height = UIUtils.getPxFromDp(getContext(), 1);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        int margin = UIUtils.getPxFromDp(getContext(), 15);
        lp.setMargins(0, margin, 0, 0);
        div.setLayoutParams(lp);
        return div;
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
        super.onStop();
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

    @SuppressWarnings("unused")
    @OnClick(R.id.submitSubQuestionsButton)
    void submitClick() {
        if (!isPreview) {
            if (adapter.saveQuestions()) {
                EventBus.getDefault().post(new SubQuestionsSubmitEvent(product != null ? product.getId() : 0));
                getFragmentManager().popBackStack();
            }
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onAnswerPageLoadingFinished() {
        loadedSubQuestionsCount++;
        if (loadedSubQuestionsCount == adapter.getCount()) {
            bottomSubQuestions.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAnswerSelected(Boolean selected, int questionId) {
        if (!isPreview) {
            if (selected) {
                set.add(questionId);
            } else {
                set.remove(questionId);
            }

            submitButton.setEnabled(set.containsAll(requiredQuestionsSet));
        }
    }
}