package com.ros.smartrocket.presentation.question.comment;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.presentation.question.base.BaseQuestionPresenter;
import com.ros.smartrocket.utils.PhotoLoader;
import com.ros.smartrocket.utils.image.SelectImageManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentPresenter<V extends CommentMvpView> extends BaseQuestionPresenter<V> implements CommentMvpPresenter<V> {

    public CommentPresenter(Question question) {
        super(question);

    }

//    File file = null;

    @Override
    public boolean saveQuestion() {
        if (question != null) question.setFirstAnswer(getMvpView().getAnswerValue());
        return super.saveQuestion();
    }

    @Override
    public void onCommentEntered(String s) {
        refreshNextButton(!TextUtils.isEmpty(s.trim()));
    }

}
