package com.ros.smartrocket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.question.QuestionInstructionBL;
import com.ros.smartrocket.bl.question.QuestionNumberBL;
import com.ros.smartrocket.bl.question.QuestionOpenCommentBL;
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
            QuestionNumberBL bl = new QuestionNumberBL();
            bl.initView(convertView, items[position], null);
        } else if (type == Question.QuestionType.OPEN_COMMENT.getTypeId()) {
            convertView = inflater.inflate(R.layout.item_question_type_open_comment, null);
            QuestionOpenCommentBL bl = new QuestionOpenCommentBL();
            bl.initView(convertView, items[position], savedInstanceState);
        } else if (type == Question.QuestionType.INSTRUCTION.getTypeId()) {
            convertView = inflater.inflate(R.layout.item_question_type_instruction, null);
            QuestionInstructionBL bl = new QuestionInstructionBL();
            bl.initView(convertView, items[position], savedInstanceState);
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return items[position].getType();
    }

    @Override
    public int getViewTypeCount() {
        return 3;
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
