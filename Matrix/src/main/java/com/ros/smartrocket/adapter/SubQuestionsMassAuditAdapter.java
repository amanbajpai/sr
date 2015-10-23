package com.ros.smartrocket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.question.QuestionTypeInstructionBL;
import com.ros.smartrocket.bl.question.QuestionTypeNumberBL;
import com.ros.smartrocket.bl.question.QuestionTypeOpenCommentBL;
import com.ros.smartrocket.db.entity.Question;

public class SubQuestionsMassAuditAdapter extends BaseAdapter {
    private final Context context;
    private final Question[] items;

    public SubQuestionsMassAuditAdapter(Context context, Question[] items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int type = getItemViewType(position);

        if (type == Question.QuestionType.NUMBER.getTypeId()) {
            convertView = inflater.inflate(R.layout.item_question_type_number, null);
            new QuestionTypeNumberBL(convertView, items[position], null);
        } else if (type == Question.QuestionType.OPEN_COMMENT.getTypeId()) {
            convertView = inflater.inflate(R.layout.item_question_type_instruction, null);
            new QuestionTypeOpenCommentBL(convertView, items[position]);
        } else if (type == Question.QuestionType.INSTRUCTION.getTypeId()) {
            convertView = inflater.inflate(R.layout.item_question_type_instruction, null);
            new QuestionTypeInstructionBL(convertView, items[position]);
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return items[position].getType();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
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
