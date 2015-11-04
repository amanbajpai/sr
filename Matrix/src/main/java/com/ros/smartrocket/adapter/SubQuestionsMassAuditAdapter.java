package com.ros.smartrocket.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.question.*;
import com.ros.smartrocket.db.entity.Product;
import com.ros.smartrocket.db.entity.Question;

import java.util.ArrayList;
import java.util.List;

public class SubQuestionsMassAuditAdapter extends BaseAdapter {
    private final FragmentActivity activity;
    private final Fragment fragment;
    private final Question[] items;
    private final Product product;
    private List<QuestionBaseBL> blList;

    public SubQuestionsMassAuditAdapter(FragmentActivity activity, Fragment fragment, Question[] items,
                                        Product product) {
        this.activity = activity;
        this.fragment = fragment;
        this.items = items;
        this.product = product;
        this.blList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int type = getItemViewType(position);

        QuestionBaseBL bl = null;
        if (type == Question.QuestionType.NUMBER.getTypeId()) {
            convertView = inflater.inflate(R.layout.item_question_number, parent, false);
            bl = new QuestionNumberBL();
        } else if (type == Question.QuestionType.OPEN_COMMENT.getTypeId()) {
            convertView = inflater.inflate(R.layout.item_question_open_comment, parent, false);
            bl = new QuestionOpenCommentBL();
        } else if (type == Question.QuestionType.INSTRUCTION.getTypeId()) {
            convertView = inflater.inflate(R.layout.item_question_instruction, parent, false);
            bl = new QuestionInstructionBL();
        } else if (type == Question.QuestionType.SINGLE_CHOICE.getTypeId()) {
            convertView = inflater.inflate(R.layout.item_question_choose, parent, false);
            bl = new QuestionSingleChooseBL();
        } else if (type == Question.QuestionType.MULTIPLE_CHOICE.getTypeId()) {
            convertView = inflater.inflate(R.layout.item_question_choose, parent, false);
            bl = new QuestionMultipleChooseBL();
        } else if (type == Question.QuestionType.VIDEO.getTypeId()) {
            convertView = inflater.inflate(R.layout.item_question_video, parent, false);
            bl = new QuestionVideoBL();
        } else if (type == Question.QuestionType.PHOTO.getTypeId()) {
            convertView = inflater.inflate(R.layout.item_question_photo, parent, false);
            bl = new QuestionPhotoBL();
        }

        if (bl != null) {
            bl.initView(convertView, items[position], null, activity, fragment, product);
            bl.loadAnswers();
            blList.add(bl);
        }
        return convertView;
    }

    public boolean saveQuestions() {
        boolean success = true;

        for (QuestionBaseBL bl : blList) {
            success = bl.saveQuestion() && success;
        }

        return success;
    }

    public void onPause() {
        for (QuestionBaseBL bl : blList) {
            bl.onPause();
        }
    }

    public void onStart() {
        for (QuestionBaseBL bl : blList) {
            bl.onStart();
        }
    }

    public void onStop() {
        for (QuestionBaseBL bl : blList) {
            bl.onStop();
        }
    }

    public void onDestroy() {
        for (QuestionBaseBL bl : blList) {
            bl.destroyView();
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        for (QuestionBaseBL bl : blList) {
            bl.onSaveInstanceState(outState);
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean handled = false;
        for (QuestionBaseBL bl : blList) {
            handled = bl.onActivityResult(requestCode, resultCode, data);
        }

        return handled;
    }

    @Override
    public int getItemViewType(int position) {
        return items[position].getType();
    }

    @Override
    public int getViewTypeCount() {
        return items.length;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Question getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
