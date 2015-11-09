package com.ros.smartrocket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.question.QuestionMassAuditBL;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Category;
import com.ros.smartrocket.db.entity.Product;

import java.util.HashMap;

public final class MassAuditExpandableListAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final Category[] categories;
    private final View.OnClickListener tickListener;
    private final View.OnClickListener crossListener;
    private HashMap<Integer, QuestionMassAuditBL.TickCrossAnswerPair> answersMap;

    public MassAuditExpandableListAdapter(Context context, Category[] categories, View.OnClickListener tickListener,
                                          View.OnClickListener crossListener) {
        this.context = context;
        this.categories = categories;
        this.tickListener = tickListener;
        this.crossListener = crossListener;
    }

    @Override
    public int getGroupCount() {
        return categories.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return categories[groupPosition].getProducts().length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return categories[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return categories[groupPosition].getProducts()[childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.mass_audit_list_group, null);
        }

        TextView titleView = (TextView) convertView.findViewById(R.id.massAuditGroupTitle);
        titleView.setText(categories[groupPosition].getCategoryName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.mass_audit_list_item, null);
        }

        Category category = categories[groupPosition];
        Product product = category.getProducts()[childPosition];

        TextView titleView = (TextView) convertView.findViewById(R.id.massAuditItemTitle);
        titleView.setText(product.getName());

        View bg = convertView.findViewById(R.id.massAuditItemBg);
        bg.setBackgroundResource(childPosition % 2 == 0 ? R.color.white : R.color.mass_audit_grey);

        ImageView crossButton = (ImageView) convertView.findViewById(R.id.massAuditCrossButton);
        ImageView tickButton = (ImageView) convertView.findViewById(R.id.massAuditTickButton);

        Answer tickAnswer = answersMap.get(product.getId()).getTickAnswer();
        Answer crossAnswer = answersMap.get(product.getId()).getCrossAnswer();
        if (tickAnswer != null && crossAnswer != null) {
            setButtonsVisibility(tickButton, crossButton, View.VISIBLE);
            // TODO Clarify answers order
            tickButton.setImageResource(tickAnswer.getChecked()
                    ? R.drawable.mass_audit_green_checked
                    : R.drawable.mass_audit_green_unchecked);
            crossButton.setImageResource(crossAnswer.getChecked()
                    ? R.drawable.mass_audit_red_checked
                    : R.drawable.mass_audit_red_unchecked);
        } else {
            setButtonsVisibility(tickButton, crossButton, View.INVISIBLE);
        }

        QuestionMassAuditBL.CategoryProductPair pair = new QuestionMassAuditBL.CategoryProductPair(category, product);
        tickButton.setTag(pair);
        tickButton.setOnClickListener(tickListener);

        crossButton.setTag(pair);
        crossButton.setOnClickListener(crossListener);

        return convertView;
    }

    private void setButtonsVisibility(View tickButton, View crossButton, int visibility) {
        tickButton.setVisibility(visibility);
        crossButton.setVisibility(visibility);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setData(HashMap<Integer, QuestionMassAuditBL.TickCrossAnswerPair> answersMap) {
        this.answersMap = answersMap;
        notifyDataSetChanged();
    }
}