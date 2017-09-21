package com.ros.smartrocket.ui.dialog;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.presentation.details.claim.MediaDownloader;
import com.ros.smartrocket.utils.FileProcessingManager;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.MyLog;
import com.ros.smartrocket.utils.image.SelectImageManager;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class ProductImageDialog extends DialogFragment {
    public static final String KEY = "com.ros.smartrocket.ui.dialog.ProductImageDialog.KEY";

    @BindView(R.id.dialogProductImageView)
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
        ((BaseActivity) getActivity()).showLoading(true);
        if (url != null) {
            if (url.startsWith("http")) {
                MediaDownloader md = new MediaDownloader(FileProcessingManager.FileType.IMAGE, completeListener);
                md.getMediaFileAsync(url);
            } else {
                setImageInstructionFile(new File(url));
            }
        }

        return view;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.dialogProductImageCloseButton)
    void closeClick() {
        dismiss();
    }

    private MediaDownloader.OnFileLoadCompleteListener completeListener = new MediaDownloader.OnFileLoadCompleteListener() {
        @Override
        public void onFileLoadComplete(File result) {
            setImageInstructionFile(result);
        }

        @Override
        public void onFileLoadError() {
            hideLoading();
        }
    };

    public void setImageInstructionFile(final File file) {
        MyLog.v("ProductImageDialog.setImageInstructionFile", file);
        Bitmap bitmap = SelectImageManager.prepareBitmap(file, SelectImageManager.SIZE_IN_PX_2_MP, 0);
        imageView.setImageBitmap(bitmap);
        imageView.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(file.getPath())) {
                Activity activity = getActivity();
                activity.startActivity(IntentUtils.getFullScreenImageIntent(activity, file.getPath()));
            }
        });
        hideLoading();
    }

    private void hideLoading() {
        if (getActivity() != null) ((BaseActivity) getActivity()).hideLoading();
    }

}