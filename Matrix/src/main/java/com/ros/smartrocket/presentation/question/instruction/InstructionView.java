package com.ros.smartrocket.presentation.question.instruction;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.presentation.question.base.BaseQuestionView;
import com.ros.smartrocket.presentation.question.comment.CommentView;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class InstructionView extends BaseQuestionView<InstructionMvpPresenter<InstructionMvpView>> implements InstructionMvpView {
    @BindView(R.id.photo)
    ImageView photoView;
    @BindView(R.id.video)
    VideoView video;
    @BindView(R.id.tv_show_images)
    CustomTextView tvShowImages;
    private ArrayList<String> gallery_images_list = new ArrayList<>();

    public InstructionView(Context context) {
        super(context);
    }

    public InstructionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InstructionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.view_question_instruction;
    }

    @Override
    public void configureView(Question question) {
        tvShowImages.setVisibility(GONE);
        presenter.showInstructions();
        presenter.refreshNextButton(true);
        gallery_images_list = presenter.getDialogGalleryImages();
        if (gallery_images_list != null && gallery_images_list.size() > 0) {
            tvShowImages.setVisibility(VISIBLE);
            setShowImagesClickListeners(gallery_images_list);
        } else {
            tvShowImages.setVisibility(GONE);
        }

    }

    private void setShowImagesClickListeners(ArrayList<String> list) {
        tvShowImages.setOnClickListener(v -> {
            showImagesGalleryDialog(list);
        });
    }

    @Override
    public void fillViewWithAnswers(List<Answer> answers) {
    }

    @Override
    public void setImageInstruction(Bitmap bitmap, String filePath) {
        photoView.setVisibility(VISIBLE);
        photoView.setImageBitmap(bitmap);
        setImageClickListeners(filePath);
    }

    private void setImageClickListeners(String path) {
        photoView.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(path))
                getContext().startActivity(IntentUtils.getFullScreenImageIntent(getContext(), path));
        });
    }

    @Override
    public void setVideoInstructionFile(final File file) {
        video.setOnTouchListener((v, event) -> {
            getContext().startActivity(IntentUtils.getFullScreenVideoIntent(getContext(), file.getPath()));
            return false;
        });
        playVideo(file.getPath());
    }

    private void playVideo(String videoPath) {
        video.setVisibility(View.VISIBLE);
        video.setVideoPath(videoPath);
        video.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            video.start();
            video.setBackgroundColor(Color.TRANSPARENT);
            hideLoading();
        });
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
