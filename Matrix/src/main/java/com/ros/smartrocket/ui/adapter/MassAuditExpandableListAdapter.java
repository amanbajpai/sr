package com.ros.smartrocket.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.question.Category;
import com.ros.smartrocket.db.entity.question.Product;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.presentation.question.audit.additional.CategoryProductPair;
import com.ros.smartrocket.presentation.question.audit.additional.TickCrossAnswerPair;
import com.ros.smartrocket.utils.image.SelectImageManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class MassAuditExpandableListAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final Category[] categories;
    private HashMap<Integer, Boolean> answersReDoMap;
    private final View.OnClickListener tickListener;
    private final View.OnClickListener crossListener;
    private HashMap<Integer, TickCrossAnswerPair> answersMap = new HashMap<>();
    private boolean isRedo;
    private View.OnClickListener thumbListener;
    private int questionNumber;
    private List<Question> reDoMainSubList = new ArrayList<>();

    public MassAuditExpandableListAdapter(Context context, Question question, View.OnClickListener tickListener,
                                          View.OnClickListener crossListener, View.OnClickListener thumbListener) {
        this.context = context;
        this.categories = question.getCategoriesArray();
        this.tickListener = tickListener;
        this.crossListener = crossListener;
        this.thumbListener = thumbListener;
        this.isRedo = false;
        this.questionNumber = question.getOrderId();
    }

    public MassAuditExpandableListAdapter(Context context, Question question, View.OnClickListener tickListener,
                                          View.OnClickListener crossListener, View.OnClickListener thumbListener,
                                          List<Question> reDoMainSubList) {
        this(context, question, tickListener, crossListener, thumbListener);
        this.isRedo = true;
        this.reDoMainSubList = reDoMainSubList;
    }

    @Override
    public int getGroupCount() {
        return categories != null ? categories.length : 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Product[] products = categories[groupPosition].getProducts();
        return products != null ? products.length : 0;
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
        Category category = categories[groupPosition];
        titleView.setText(category.getCategoryName());
        ImageView catImage = (ImageView) convertView.findViewById(R.id.massAuditCatImageThumb);
        catImage.setVisibility(TextUtils.isEmpty(category.getImage()) ? View.INVISIBLE : View.VISIBLE);
        String image = TextUtils.isEmpty(category.getCachedImage()) ? category.getImage() : category.getCachedImage();
        catImage.setTag(image);
        catImage.setOnClickListener(TextUtils.isEmpty(category.getImage()) ? null : thumbListener);
        if (!TextUtils.isEmpty(image)) {
            if (image.startsWith("http")) {
                Picasso.get()
                        .load(image)
                        .error(R.color.mass_audit_grey)
                        .into(catImage);
            } else {
                Bitmap bitmap = SelectImageManager.prepareBitmap(new File(image), SelectImageManager.SIZE_THUMB);
                catImage.setImageBitmap(bitmap);
            }
        }
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

        TickCrossAnswerPair answerPair = answersMap.get(product.getId());
        if (answerPair != null) {
            Answer tickAnswer = answerPair.getTickAnswer();
            Answer crossAnswer = answerPair.getCrossAnswer();
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
        }
        if (isRedo && !isRedoMain(product.getId())) {
            boolean isTickAction = isTickAction();
            setButtonsVisibility(isTickAction ? tickButton : crossButton,
                    isTickAction ? crossButton : tickButton, View.GONE);
            if (answersReDoMap == null) {
                setIcon(isTickAction ? tickButton : crossButton, R.drawable.mass_audit_green_edit);
            } else {
                try {
                    int resId = answersReDoMap.get(product.getId())
                            ? R.drawable.mass_audit_green_checked
                            : R.drawable.mass_audit_green_edit;
                    setIcon(isTickAction ? tickButton : crossButton, resId);
                } catch (Exception e) {
                    setIcon(isTickAction ? tickButton : crossButton, R.drawable.mass_audit_green_edit);
                }
            }
        }

        CategoryProductPair pair = new CategoryProductPair(category, product, questionNumber);
        tickButton.setTag(pair);
        tickButton.setOnClickListener(tickListener);

        crossButton.setTag(pair);
        crossButton.setOnClickListener(crossListener);

        ImageView thumb = (ImageView) convertView.findViewById(R.id.massAuditImageThumb);
        thumb.setVisibility(TextUtils.isEmpty(product.getImage()) ? View.INVISIBLE : View.VISIBLE);
        String image = TextUtils.isEmpty(product.getCachedImage()) ? product.getImage() : product.getCachedImage();
        thumb.setTag(image);
        thumb.setOnClickListener(TextUtils.isEmpty(product.getImage()) ? null : thumbListener);
        if (!TextUtils.isEmpty(image)) {
            if (image.startsWith("http")) {
                Picasso.get()
                        .load(image)
                        .error(childPosition % 2 == 0 ? R.color.white : R.color.mass_audit_grey)
                        .into(thumb);
            } else {
                Bitmap bitmap = SelectImageManager.prepareBitmap(new File(image), SelectImageManager.SIZE_THUMB);
                thumb.setImageBitmap(bitmap);
            }
        }

        return convertView;
    }

    private boolean isTickAction() {
        return !(!reDoMainSubList.isEmpty() && reDoMainSubList.get(0).getAction() == Question.ACTION_CROSS);
    }

    private void setIcon(ImageView button, int resId) {
        button.setImageResource(resId);
    }

    private boolean isRedoMain(Integer productId) {
        for (Question question : reDoMainSubList) {
            if (productId.equals(question.getProductId()) && question.isRedo()) return true;
        }
        return false;
    }

    private void setButtonsVisibility(ImageView firstButton, ImageView secondButton, int visibility) {
        firstButton.setVisibility(View.VISIBLE);
        secondButton.setVisibility(visibility);
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setData(HashMap<Integer, TickCrossAnswerPair> answersMap) {
        this.answersMap = answersMap;
        notifyDataSetChanged();
    }

    public void setData(HashMap<Integer, TickCrossAnswerPair> answersMap, HashMap<Integer, Boolean> answersReDoMap) {
        this.answersMap = answersMap;
        this.answersReDoMap = answersReDoMap;
        notifyDataSetChanged();
    }

    public void setReDoData(HashMap<Integer, Boolean> answersMap) {
        this.answersReDoMap = answersMap;
        notifyDataSetChanged();
    }
}