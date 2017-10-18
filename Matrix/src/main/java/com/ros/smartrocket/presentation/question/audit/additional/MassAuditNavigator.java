package com.ros.smartrocket.presentation.question.audit.additional;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.presentation.question.audit.subquestion.SubQuestionsMassAuditFragment;
import com.ros.smartrocket.ui.dialog.ProductImageDialog;

public class MassAuditNavigator implements Navigator {
    private AppCompatActivity activity;

    public MassAuditNavigator(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Override
    public void openThumbnailDialog(String path) {
        ProductImageDialog.showDialog(activity.getSupportFragmentManager(), path);
    }

    @Override
    public void startSubQuestionsFragment(CategoryProductPair item, Question question, boolean isRedo, boolean isPreview) {
        Question[] qArr = new Question[question.getChildQuestions().size()];
        qArr = question.getChildQuestions().toArray(qArr);
        Fragment f = SubQuestionsMassAuditFragment.makeInstance(qArr,
                item.category.getCategoryName(),
                item.product,
                isRedo,
                isPreview,
                item.productPosition);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.subquestionsLayout, f)
                .addToBackStack(null).commit();
    }
}
