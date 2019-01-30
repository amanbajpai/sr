package com.ros.smartrocket.presentation.question.comment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.glide.GlideApp;
import com.ros.smartrocket.presentation.question.base.BaseQuestionView;
import com.ros.smartrocket.ui.views.CustomEditTextView;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CommentView extends BaseQuestionView<CommentMvpPresenter<CommentMvpView>> implements CommentMvpView {
    public static final int TIMEOUT = 100;
    @BindView(R.id.answerEditText)
    CustomEditTextView answerEditText;
    @BindView(R.id.tv_show_images)
    CustomTextView tvShowImages;
    private Disposable commentDisposable;
    private ArrayList<String> gallery_images_list = new ArrayList<>();

    public CommentView(Context context) {
        super(context);
    }

    public CommentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void setShowImagesClickListeners(ArrayList<String> list) {
        tvShowImages.setOnClickListener(v -> {
            showImagesGalleryDialog(list);
        });
    }

    @Override
    public int getLayoutResId() {
        return R.layout.view_question_comment;
    }

    @Override
    public void configureView(Question question) {
        setEditTextWatcher();
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(question.getMaximumCharacters());
        answerEditText.setFilters(filterArray);

        gallery_images_list = presenter.getDialogGalleryImages();
        if (gallery_images_list != null && gallery_images_list.size() > 0) {
            tvShowImages.setVisibility(VISIBLE);
            setShowImagesClickListeners(gallery_images_list);
        } else {
            tvShowImages.setVisibility(GONE);
        }

        presenter.loadAnswers();
    }

    @Override
    public void fillViewWithAnswers(List<Answer> answers) {
        if (answers.get(0).getChecked()) answerEditText.setText(answers.get(0).getValue());
        presenter.onCommentEntered(answerEditText.getText().toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (commentDisposable != null && !commentDisposable.isDisposed())
            commentDisposable.dispose();
    }

    @Override
    public String getAnswerValue() {
        return answerEditText.getText().toString();
    }

    private void setEditTextWatcher() {
        commentDisposable = RxTextView.textChanges(answerEditText)
                .debounce(TIMEOUT, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> presenter.onCommentEntered(s.toString()), this::onError);
    }

    private void onError(Throwable t) {
        Log.e("Comment view RxError", t.getMessage());
    }

    private void showImagesGalleryDialog(ArrayList<String> imagesList) {
        DialogGalleryAdapter dialogGalleryAdapter;
        ViewPager pager;
        Dialog dialog;
        dialog = UIUtils.showImageGalleryDialog(getContext(), R.layout.dialog_commentview_images_layout);
        ImageView leftIcon = dialog.findViewById(R.id.leftIcon);
        ImageView rightIcon = dialog.findViewById(R.id.rightIcon);
        ImageView cancel = dialog.findViewById(R.id.cancel);
        pager = dialog.findViewById(R.id.overlap_pager);
        dialogGalleryAdapter = new DialogGalleryAdapter(imagesList);
        pager.setAdapter(dialogGalleryAdapter);
        pager.setClipChildren(false);
        leftIcon.setVisibility(View.GONE);
        dialog.show();
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    leftIcon.setVisibility(View.GONE);
                    rightIcon.setVisibility(View.VISIBLE);
                } else if (position == pager.getAdapter().getCount() - 1) {
                    leftIcon.setVisibility(View.VISIBLE);
                    rightIcon.setVisibility(View.GONE);

                } else {
                    leftIcon.setVisibility(View.VISIBLE);
                    rightIcon.setVisibility(View.VISIBLE);


                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //
        pager.setOffscreenPageLimit(1);
        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(pager.getCurrentItem() + 1);
            }
        });
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(pager.getCurrentItem() - 1);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                dialog.dismiss();
            }
        });
    }

    private class DialogGalleryAdapter extends PagerAdapter {
        ArrayList<String> list = null;

        public DialogGalleryAdapter(ArrayList<String> imagesList) {
            list = imagesList;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View view = LayoutInflater.from(getContext()).inflate(R.layout.dailog_gallery_item_cover, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.image_cover);
            imageView.setOnClickListener(v -> getContext().startActivity(IntentUtils.getFullScreenImageIntent(getContext(), list.get(position))));
            loadImage(imageView, list.get(position));
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        public void loadImage(ImageView imageView, String imageUrl) {
            Log.e("ImageUrl", "" + imageUrl);
            Picasso.get()
                    .load(imageUrl)
//                    .load("https://gongchausa.s3.us-east-2.amazonaws.com/menu_pic/15456561786800430645c20d772482a3.jpg")
//                    .load("https://wallpaperbrowse.com/media/images/image-1635747_960_720.jpg")
                    .placeholder(R.drawable.round_progress)
                    .error(R.drawable.mass_audit_image)
//                    .centerInside()
//                    .resizeDimen(R.dimen.list_detail_image_size, R.dimen.list_detail_image_size)
                    .tag(getContext())
                    .into(imageView);


        }
    }


}
