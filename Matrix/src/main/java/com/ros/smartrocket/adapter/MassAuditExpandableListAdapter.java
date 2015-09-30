package com.ros.smartrocket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.ros.smartrocket.R;

import java.util.Arrays;
import java.util.List;

public final class MassAuditExpandableListAdapter extends BaseExpandableListAdapter {
    private final Context context;
    MAObj obj;

    public MassAuditExpandableListAdapter(Context context) {
        this.context = context;
        obj = new MAObj(Arrays.asList(
                new MAObj.MACategory("category1", Arrays.asList(
                        new MAObj.MATask("title1.1"),
                        new MAObj.MATask("title1.2"),
                        new MAObj.MATask("title1.3")
                )),
                new MAObj.MACategory("category2", Arrays.asList(
                        new MAObj.MATask("title2.1"),
                        new MAObj.MATask("title2.2"),
                        new MAObj.MATask("title2.3")
                )),
                new MAObj.MACategory("category3", Arrays.asList(
                        new MAObj.MATask("title3.1"),
                        new MAObj.MATask("title3.2"),
                        new MAObj.MATask("title3.3"),
                        new MAObj.MATask("title3.4"),
                        new MAObj.MATask("title3.5"),
                        new MAObj.MATask("title3.6")
                )),
                new MAObj.MACategory("category4", Arrays.asList(
                        new MAObj.MATask("title4.1"),
                        new MAObj.MATask("title4.2")
                ))
        ));
    }

    @Override
    public int getGroupCount() {
        return obj.categories.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return obj.categories.get(groupPosition).maTasks.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return obj.categories.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return obj.categories.get(groupPosition).maTasks.get(childPosition);
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
            convertView = inflater.inflate(R.layout.mass_audit_list_item, null);
        }

        TextView titleView = (TextView) convertView.findViewById(R.id.massAuditGroupTitle);
        titleView.setText(obj.categories.get(groupPosition).title);

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
        titleView.setText(obj.categories.get(groupPosition).maTasks.get(childPosition).title);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class MAObj {
        final List<MACategory> categories;

        public MAObj(List<MACategory> categories) {
            this.categories = categories;
        }

        static class MACategory {
            final String title;
            final List<MATask> maTasks;

            public MACategory(String title, List<MATask> maTasks) {
                this.title = title;
                this.maTasks = maTasks;
            }
        }

        static class MATask {
            final String title;

            public MATask(String title) {
                this.title = title;
            }
        }
    }
}
