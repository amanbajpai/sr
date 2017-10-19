package com.ros.smartrocket.ui.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Stream;
import com.ros.smartrocket.db.entity.Product;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.presentation.base.MvpPresenter;
import com.ros.smartrocket.presentation.question.audit.subquestion.SubQuestionsMassAuditFragment;
import com.ros.smartrocket.presentation.question.base.BaseQuestionMvpPresenter;
import com.ros.smartrocket.presentation.question.base.BaseQuestionView;
import com.ros.smartrocket.presentation.question.choose.ChoosePresenter;
import com.ros.smartrocket.presentation.question.choose.multiple.MultipleChooseView;
import com.ros.smartrocket.presentation.question.choose.single.SingleChooseView;
import com.ros.smartrocket.presentation.question.comment.CommentPresenter;
import com.ros.smartrocket.presentation.question.comment.CommentView;
import com.ros.smartrocket.presentation.question.instruction.InstructionPresenter;
import com.ros.smartrocket.presentation.question.instruction.InstructionView;
import com.ros.smartrocket.presentation.question.number.NumberPresenter;
import com.ros.smartrocket.presentation.question.number.NumberView;
import com.ros.smartrocket.presentation.question.photo.PhotoPresenter;
import com.ros.smartrocket.presentation.question.photo.PhotoView;
import com.ros.smartrocket.presentation.question.photo.helper.PhotoQuestionHelper;
import com.ros.smartrocket.presentation.question.video.VideoPresenter;
import com.ros.smartrocket.presentation.question.video.VideoQuestionView;
import com.ros.smartrocket.presentation.question.video.helper.VideoQuestionHelper;

import java.util.ArrayList;
import java.util.List;

public class SubQuestionsMassAuditAdapter {
    private final SubQuestionsMassAuditFragment fragment;
    private final Question[] items;
    private final Product product;
    private List<BaseQuestionMvpPresenter> presenters;
    private List<BaseQuestionView> views;
    private boolean isPreview;
    private boolean isRedo;

    public SubQuestionsMassAuditAdapter(SubQuestionsMassAuditFragment fragment, Question[] items, Product product) {
        this.fragment = fragment;
        this.items = items;
        this.product = product;
        this.presenters = new ArrayList<>();
        this.views = new ArrayList<>();
    }

    public View getView(int position, Bundle savedInstanceState) {
        int type = getItemViewType(position);
        BaseQuestionMvpPresenter presenter = null;
        BaseQuestionView mvpView = null;
        if (type == Question.QuestionType.NUMBER.getTypeId()) {
            mvpView = new NumberView(fragment.getContext());
            presenter = new NumberPresenter(items[position]);
        } else if (type == Question.QuestionType.OPEN_COMMENT.getTypeId()) {
            mvpView = new CommentView(fragment.getContext());
            presenter = new CommentPresenter(items[position]);
        } else if (type == Question.QuestionType.INSTRUCTION.getTypeId()) {
            mvpView = new InstructionView(fragment.getContext());
            presenter = new InstructionPresenter(items[position]);
        } else if (type == Question.QuestionType.SINGLE_CHOICE.getTypeId()) {
            mvpView = new SingleChooseView(fragment.getContext());
            presenter = new ChoosePresenter(items[position]);
        } else if (type == Question.QuestionType.MULTIPLE_CHOICE.getTypeId()) {
            mvpView = new MultipleChooseView(fragment.getContext());
            presenter = new ChoosePresenter(items[position]);
        } else if (type == Question.QuestionType.VIDEO.getTypeId()) {
            mvpView = new VideoQuestionView(fragment.getContext());
            presenter = new VideoPresenter(items[position], new VideoQuestionHelper(fragment.getActivity()));
        } else if (type == Question.QuestionType.PHOTO.getTypeId()) {
            mvpView = new PhotoView(fragment.getContext());
            presenter = new PhotoPresenter(items[position], new PhotoQuestionHelper(fragment));
        }

        if (presenter != null) {
            presenter.setPreview(isPreview);
            presenter.setRedo(isRedo);
            presenter.setProduct(product);
            presenter.setAnswerPageLoadingFinishedListener(fragment);
            presenter.setAnswerSelectedListener(fragment);
            mvpView.setPresenter(presenter);
            presenter.attachView(mvpView);
            presenters.add(presenter);
            views.add(mvpView);
        }
        return mvpView;
    }

    public boolean saveQuestions() {
        boolean success = true;
        for (BaseQuestionMvpPresenter p : presenters)
            success = p.saveQuestion() && success;
        return success;
    }

    public void onPause() {
        Stream.of(views)
                .forEach(BaseQuestionView::onPause);
    }

    public void onStart() {
        for (int i = 0; i < presenters.size(); i++) {
            if (!presenters.get(i).isViewAttached())
                presenters.get(i).attachView(views.get(i));
        }
        Stream.of(views)
                .forEach(BaseQuestionView::onStart);
    }

    public void onStop() {
        Stream.of(views)
                .forEach(BaseQuestionView::onStop);
        Stream.of(presenters)
                .forEach(MvpPresenter::detachView);
    }

    public void onDestroy() {
        Stream.of(views)
                .forEach(BaseQuestionView::onDestroy);
    }

    public void onSaveInstanceState(Bundle outState) {

    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        Stream.of(presenters)
                .forEach(p -> p.onActivityResult(requestCode, resultCode, data));
        return true;
    }

    private int getItemViewType(int position) {
        return items[position].getType();
    }

    public int getCount() {
        return items.length;
    }

    public void setRedo(boolean redo) {
        isRedo = redo;
    }

    public void setPreview(boolean preview) {
        isPreview = preview;
    }
}
