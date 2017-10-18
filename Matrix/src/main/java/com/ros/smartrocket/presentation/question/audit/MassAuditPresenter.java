package com.ros.smartrocket.presentation.question.audit;

import android.support.annotation.NonNull;
import android.util.Log;

import com.annimon.stream.Stream;
import com.ros.smartrocket.db.bl.AnswersBL;
import com.ros.smartrocket.db.bl.QuestionsBL;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.presentation.question.audit.additional.CategoryProductPair;
import com.ros.smartrocket.presentation.question.audit.additional.Navigator;
import com.ros.smartrocket.presentation.question.audit.additional.TickCrossAnswerPair;
import com.ros.smartrocket.presentation.question.base.BaseQuestionPresenter;

import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MassAuditPresenter<V extends MassAuditMvpView> extends BaseQuestionPresenter<V> implements MassAuditMvpPresenter<V> {
    private Question mainSub;
    private List<Question> mainSubList;
    private HashMap<Integer, Boolean> answersReDoMap = new HashMap<>();
    private HashMap<Integer, TickCrossAnswerPair> answersMap;
    private Navigator navigator;

    public MassAuditPresenter(Question question, Navigator navigator) {
        super(question);
        this.navigator = navigator;
    }

    @Override
    public void loadAnswers() {
        addDisposable(AnswersBL.getAnswersListFromDBObservable(mainSub, product)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onAnswersLoadedFromDb, this::onSubQuestionsNotLoaded));
    }

    @Override
    public void getChildQuestionsListFromDB() {
        addDisposable(QuestionsBL.childQuestionsListObservable(question)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::subQuestionsLoaded, this::onSubQuestionsNotLoaded));
    }

    private void subQuestionsLoaded(List<Question> subQuestions) {
        question.setChildQuestions(subQuestions);
        mainSub = QuestionsBL.getMainSubQuestion(subQuestions);
        if (isRedo()) mainSubList = QuestionsBL.getReDoMainSubQuestionList(subQuestions);
        if (mainSub != null) {
            getMvpView().showMainSubQuestionText(mainSub.getQuestion());
            loadAnswers();
        } else {
            loadSubQuestionsList();
        }
    }

    private void loadSubQuestionsList() {
        addDisposable(AnswersBL.subQuestionsAnswersListObservable(question)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onAnswersLoadedFromDb, this::onSubQuestionsNotLoaded));
    }

    private void onSubQuestionsNotLoaded(Throwable t) {
        Log.e("MassAuditPresenter", "SubQuestions not loaded", t);
    }

    @Override
    public void onAnswersLoadedFromDb(List<Answer> answers) {
        question.setAnswers(answers);
        if (!isRedo()) {
            answersMap = convertToMap(answers);
            getMvpView().showAnswersList(answersMap);
        } else {
            answersReDoMap.putAll(convertToReDoMap(answers));
            getMvpView().showRedoAnswersList(answersMap, answersReDoMap, mainSubList);
        }
        refreshNextButton();
    }

    @NonNull
    private HashMap<Integer, TickCrossAnswerPair> convertToMap(List<Answer> answers) {
        HashMap<Integer, TickCrossAnswerPair> map = new HashMap<>();
        for (Answer answer : answers) {
            if (map.get(answer.getProductId()) == null)
                map.put(answer.getProductId(), new TickCrossAnswerPair());
            TickCrossAnswerPair pair = map.get(answer.getProductId());
            if (answer.getValue().equals("1"))
                pair.setTickAnswer(answer);
            else
                pair.setCrossAnswer(answer);
        }
        return map;
    }

    @NonNull
    private HashMap<Integer, Boolean> convertToReDoMap(List<Answer> answers) {
        HashMap<Integer, Boolean> map = new HashMap<>();
        Stream.of(answers)
                .filter(answer -> map.get(answer.getProductId()) == null)
                .forEach(answer -> {
                    prepareAnswer(answer);
                    map.put(answer.getProductId(), answer.getChecked());
                });
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
    public void refreshNextButton() {
        boolean selected = true;
        for (TickCrossAnswerPair pair : answersMap.values()) {
            if (!pair.getTickAnswer().getChecked() && !pair.getCrossAnswer().getChecked()) {
                selected = false;
                break;
            }
        }
        refreshNextButton(selected);
    }

    @Override
    public void handleTickCrossTick(CategoryProductPair pair, int buttonClicked) {
        if ((buttonClicked == MassAuditView.TICK && mainSub.getAction() == Question.ACTION_TICK)
                || (buttonClicked == MassAuditView.CROSS && mainSub.getAction() == Question.ACTION_CROSS)
                || (mainSub.getAction() == Question.ACTION_BOTH)) {
            if (isRedo() && pair.product.getId() != null && question.getChildQuestions() != null) {
                if (QuestionsBL.hasReDoNotMainSub(question.getChildQuestions(), pair.product.getId())) {
                    navigator.startSubQuestionsFragment(pair, question, isRedo(), isPreview());
                } else {
                    updateRedoAnswers(pair.product.getId());
                    saveQuestion();
                    updateTickCrossState(pair.product.getId(), buttonClicked);
                }
            } else {
                navigator.startSubQuestionsFragment(pair, question, isRedo(), isPreview());
            }
        } else {
            if (isRedo()) {
                saveQuestion();
                updateRedoAnswers(pair.product.getId());
            }
            updateTickCrossState(pair.product.getId(), buttonClicked);
        }
    }

    @Override
    public void openThumbnail(String path) {
        navigator.openThumbnailDialog(path);
    }

    private void updateRedoAnswers(Integer productId) {
        answersReDoMap.put(productId, true);
        getMvpView().setRedoData(answersReDoMap);
    }


    private void updateTickCrossState(int productId, int buttonClicked) {
        TickCrossAnswerPair pair = answersMap.get(productId);
        if (pair != null) {
            pair.getTickAnswer().setChecked(buttonClicked == MassAuditView.TICK);
            pair.getCrossAnswer().setChecked(buttonClicked == MassAuditView.CROSS);
            if (buttonClicked == MassAuditView.CROSS && mainSub.getAction() != Question.ACTION_BOTH && mainSub.getAction() != Question.ACTION_CROSS) {
                AnswersBL.clearSubAnswersInDB(mainSub.getTaskId(), mainSub.getMissionId(), productId, question.getChildQuestions());
            }
            getMvpView().refreshAdapter();
            refreshNextButton();
        }
    }
}
