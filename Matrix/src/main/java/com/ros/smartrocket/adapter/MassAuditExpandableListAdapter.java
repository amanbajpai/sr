package com.ros.smartrocket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Category;

public final class MassAuditExpandableListAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final Category[] categories;

    public MassAuditExpandableListAdapter(Context context, Category[] categories) {
        this.context = context;
        this.categories = categories;
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
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup
            parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.mass_audit_list_item, null);
        }

        TextView titleView = (TextView) convertView.findViewById(R.id.massAuditItemTitle);
        titleView.setText(categories[groupPosition].getProducts()[childPosition].getName());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}