package com.ros.smartrocket.presentation.question.audit;

import com.ros.smartrocket.presentation.question.audit.additional.CategoryProductPair;
import com.ros.smartrocket.presentation.question.base.BaseQuestionMvpPresenter;
import com.ros.smartrocket.utils.eventbus.SubQuestionsSubmitEvent;

public interface MassAuditMvpPresenter<V extends MassAuditMvpView> extends BaseQuestionMvpPresenter<V> {
    void getChildQuestionsListFromDB();

    void refreshNextButton();

    void handleTickCrossTick(CategoryProductPair pair, int buttonType);

    void openThumbnail(String path);

    void onEventReceived(SubQuestionsSubmitEvent event, int buttonClicked);

}
