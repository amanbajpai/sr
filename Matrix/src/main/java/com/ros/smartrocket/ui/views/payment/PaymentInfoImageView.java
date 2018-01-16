package com.ros.smartrocket.ui.views.payment;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.payment.PaymentField;
import com.ros.smartrocket.interfaces.PhotoActionsListener;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentInfoImageView extends LinearLayout {
    @BindView(R.id.icon)
    AppCompatImageView icon;
    @BindView(R.id.photo)
    AppCompatImageView photo;
    @BindView(R.id.photoActionsLayout)
    LinearLayout photoActionsLayout;
    @BindView(R.id.description)
    CustomTextView description;
    private PaymentField paymentField;
    private PhotoActionsListener listener;

    public PaymentInfoImageView(Context context) {
        super(context);
    }

    public PaymentInfoImageView(Context context, PaymentField paymentField, PhotoActionsListener listener) {
        super(context);
        this.paymentField = paymentField;
        init(context);
    }

    private void init(Context c) {
        inflate(c, R.layout.view_payment_field_image, this);
        ButterKnife.bind(this);
        fillData();
    }

    private void fillData() {
        setIcon();
        setImage();
        description.setText(paymentField.getInstructions());
    }

    private void setIcon() {
        if (!TextUtils.isEmpty(paymentField.getIcon()))
            Picasso.with(getContext())
                    .load(paymentField.getIcon())
                    .error(R.drawable.cam)
                    .into(icon);
    }

    public void setImage() {
        if (!TextUtils.isEmpty(paymentField.getValue())) {
            Picasso.with(getContext())
                    .load(paymentField.getValue())
                    .error(R.drawable.cam)
                    .into(photo);
            photo.setVisibility(VISIBLE);
            photoActionsLayout.setVisibility(VISIBLE);
        } else {
            photoActionsLayout.setVisibility(GONE);
            photo.setImageResource(R.drawable.camera_icon);
        }
    }

    @OnClick({R.id.icon, R.id.photo, R.id.rePhotoButton, R.id.deletePhotoButton})
    public void onViewClicked(View view) {
        if (listener != null)
            switch (view.getId()) {
                case R.id.icon:
                    break;
                case R.id.photo:
                    listener.addPhoto();
                    break;
                case R.id.rePhotoButton:
                    listener.addPhoto();
                    break;
                case R.id.deletePhotoButton:
                    listener.deletePhoto();
                    break;
            }
    }

    public void setPhoto(Bitmap image) {
        paymentField.setValue(null);
        if (image != null) {
            photo.setImageBitmap(image);
            photo.setVisibility(VISIBLE);
            photoActionsLayout.setVisibility(VISIBLE);
        } else {
            photoActionsLayout.setVisibility(GONE);
            photo.setVisibility(GONE);
        }
    }

    public void setListener(PhotoActionsListener listener) {
        this.listener = listener;
    }
}
