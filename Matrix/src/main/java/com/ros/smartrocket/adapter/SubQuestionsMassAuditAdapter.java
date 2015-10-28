package com.ros.smartrocket.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.question.*;
import com.ros.smartrocket.db.entity.Question;

public class SubQuestionsMassAuditAdapter extends BaseAdapter {
    private final FragmentActivity activity;
    private final Question[] items;

    public SubQuestionsMassAuditAdapter(FragmentActivity activity, Question[] items) {
        this.activity = activity;
        this.items = items;
    }

    @Nullable
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int type = getItemViewType(position);

        QuestionBaseBL bl = null;
        if (type == Question.QuestionType.NUMBER.getTypeId()) {
            convertView = inflater.inflate(R.layout.item_question_type_number, null);
            bl = new QuestionNumberBL();
        } else if (type == Question.QuestionType.OPEN_COMMENT.getTypeId()) {
            convertView = inflater.inflate(R.layout.item_question_type_open_comment, null);
            bl = new QuestionOpenCommentBL();
        } else if (type == Question.QuestionType.INSTRUCTION.getTypeId()) {
            convertView = inflater.inflate(R.layout.item_question_type_instruction, null);
            bl = new QuestionInstructionBL();
        } else if (type == Question.QuestionType.SINGLE_CHOICE.getTypeId()) {
            convertView = inflater.inflate(R.layout.item_question_choose, null);
            bl = new QuestionSingleChooseBL();
        } else if (type == Question.QuestionType.MULTIPLE_CHOICE.getTypeId()) {
            convertView = inflater.inflate(R.layout.item_question_choose, null);
            bl = new QuestionMultipleChooseBL();
        } else if (type == Question.QuestionType.VIDEO.getTypeId()) {
            convertView = inflater.inflate(R.layout.fragment_question_video, null);
            bl = new QuestionVideoBL();
        } else if (type == Question.QuestionType.PHOTO.getTypeId()) {
            convertView = inflater.inflate(R.layout.fragment_question_photo, null);
            bl = new QuestionPhotoBL();
        }

        if (bl != null) {
            bl.initView(convertView, items[position], null, activity);
            bl.loadAnswers();
        }
        return convertView;
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
