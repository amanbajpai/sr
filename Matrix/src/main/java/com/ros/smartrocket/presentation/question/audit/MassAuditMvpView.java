package com.ros.smartrocket.presentation.question.audit;

import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.presentation.question.audit.additional.TickCrossAnswerPair;
import com.ros.smartrocket.presentation.question.base.BaseQuestionMvpView;

import java.util.HashMap;
import java.util.List;

public interface MassAuditMvpView extends BaseQuestionMvpView {
    void showMainSubQuestionText(String text);

    void showAnswersList(HashMap<Integer, TickCrossAnswerPair> answersMap);

    void showRedoAnswersList(HashMap<Integer, TickCrossAnswerPair> answersMap, HashMap<Integer, Boolean> answersReDoMap, List<Question> subQuestions);

    void refreshAdapter();

    void setRedoData(HashMap<Integer, Boolean> answersReDoMap);

}
