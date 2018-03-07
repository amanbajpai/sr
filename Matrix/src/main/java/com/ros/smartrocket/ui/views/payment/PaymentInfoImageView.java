package com.ros.smartrocket.ui.views.payment;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.payment.PaymentField;
import com.ros.smartrocket.db.entity.payment.PaymentInfo;
import com.ros.smartrocket.interfaces.PhotoActionsListener;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentInfoImageView extends RelativeLayout {
    @BindView(R.id.icon)
    AppCompatImageView icon;
    @BindView(R.id.photo)
    ImageView photo;
    @BindView(R.id.description)
    CustomTextView description;
    @BindView(R.id.rePhotoButton)
    ImageButton rePhotoButton;

    private PaymentField paymentField;
    private PhotoActionsListener listener;

    public PaymentInfoImageView(Context context) {
        super(context);
    }

    public PaymentInfoImageView(Context context, PaymentField paymentField, PhotoActionsListener listener) {
        super(context);
        this.listener = listener;
        this.paymentField = paymentField;
        init(context);
    }

    private void init(Context c) {
        inflate(c, R.layout.view_payment_field_image, this);
        setBackgroundResource(R.color.grey_light);
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
            rePhotoButton.setVisibility(GONE);
            photo.setVisibility(VISIBLE);
            Picasso.with(getContext())
                    .load(paymentField.getValue())
                    .resize(800, 0)
                    .onlyScaleDown()
                    .placeholder(R.drawable.loading_normal)
                    .error(R.drawable.cam)
                    .into(photo, new Callback() {
                        @Override
                        public void onSuccess() {
                            rePhotoButton.setVisibility(VISIBLE);
                        }

                        @Override
                        public void onError() {
                            rePhotoButton.setVisibility(GONE);
                        }
                    });
        } else {
            rePhotoButton.setVisibility(GONE);
            photo.setImageResource(R.drawable.camera_icon);
        }
    }

    @OnClick({R.id.photo, R.id.rePhotoButton})
    public void onViewClicked(View view) {
        if (listener != null)
            switch (view.getId()) {
                case R.id.photo:
                    listener.onPhotoClicked(paymentField.getValue(), paymentField.getId());
                    break;
                case R.id.rePhotoButton:
                    listener.addPhoto(paymentField.getId());
                    break;
            }
    }

    public void setPhoto(Bitmap image) {
        paymentField.setValue(null);
        if (image != null) {
            photo.setImageBitmap(image);
            photo.setVisibility(VISIBLE);
            rePhotoButton.setVisibility(VISIBLE);
        } else {
            rePhotoButton.setVisibility(GONE);
            photo.setVisibility(GONE);
        }
    }

    public int getFieldId() {
        return paymentField.getId();
    }

    public PaymentInfo getPaymentInfo() {
        return new PaymentInfo(paymentField);
    }

}
