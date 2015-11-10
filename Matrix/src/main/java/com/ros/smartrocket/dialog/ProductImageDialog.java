package com.ros.smartrocket.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.L;
import com.squareup.picasso.Picasso;

public final class ProductImageDialog extends DialogFragment {
    public static final String KEY = "com.ros.smartrocket.dialog.ProductImageDialog.KEY";

    @Bind(R.id.dialogProductImageView)
    ImageView imageView;

    public static void showDialog(FragmentManager fm, String url) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY, url);

        DialogFragment dialog = new ProductImageDialog();
        dialog.setArguments(bundle);
        dialog.show(fm, "product_image_dialog");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_product_image, container);
        ButterKnife.bind(this, view);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        String url = getArguments().getString(KEY);
        L.v("IMAGE", url);
        Picasso.with(getActivity()).load(url).into(imageView);

        return view;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.dialogProductImageCloseButton)
    void closeClick() {
        dismiss();
    }
}