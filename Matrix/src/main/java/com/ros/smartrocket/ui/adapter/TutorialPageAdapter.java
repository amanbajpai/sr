package com.ros.smartrocket.ui.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class TutorialPageAdapter extends PagerAdapter {
    private List<View> items;

    public TutorialPageAdapter(List<View> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(items.get(position));
        return items.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }
}
