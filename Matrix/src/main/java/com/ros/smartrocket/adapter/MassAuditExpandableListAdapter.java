package com.ros.smartrocket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Category;
import com.ros.smartrocket.db.entity.Product;

public final class MassAuditExpandableListAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final Category[] categories;
    private final View.OnClickListener tickListener;

    public MassAuditExpandableListAdapter(Context context, Category[] categories, View.OnClickListener tickListener) {
        this.context = context;
        this.categories = categories;
        this.tickListener = tickListener;
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

        Product product = categories[groupPosition].getProducts()[childPosition];

        TextView titleView = (TextView) convertView.findViewById(R.id.massAuditItemTitle);
        titleView.setText(product.getName());

        View bg = convertView.findViewById(R.id.massAuditItemBg);
        bg.setBackgroundResource(childPosition % 2 == 0 ? R.color.white : R.color.mass_audit_grey);

        RadioButton tickButton = (RadioButton) convertView.findViewById(R.id.massAuditTickButton);
        tickButton.setTag(product);
        tickButton.setOnClickListener(tickListener);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}