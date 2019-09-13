package com.ros.smartrocket.presentation.question.photo;

import android.app.Activity;
import android.content.Context;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.question.CustomFieldImageUrls;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.presentation.question.adapter.HorizonalImgAdapter;
import com.ros.smartrocket.presentation.question.base.BaseQuestionView;
import com.ros.smartrocket.ui.gallery.model.GalleryInfo;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.eventbus.PhotoEvent;
import com.ros.smartrocket.utils.image.SelectImageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class PhotoView extends BaseQuestionView<PhotoMvpPresenter<PhotoMvpView>> implements PhotoMvpView, HorizonalImgAdapter.OnClickImage {

    @BindView(R.id.galleryLayout)
    LinearLayout galleryLayout;
    @BindView(R.id.photo)
    ImageView photo;
    @BindView(R.id.rePhotoButton)
    ImageButton rePhotoButton;
    @BindView(R.id.deletePhotoButton)
    ImageButton deletePhotoButton;
    @BindView(R.id.confirmButton)
    ImageButton confirmButton;
    @BindView(R.id.recyclerview_gallery)
    RecyclerView recyclerview_gallery;

    private HorizonalImgAdapter horizonalImgAdapter;
    private List<GalleryInfo> imageList = new ArrayList<>();

    private int currentSelectedPhoto = 0;
    private int selectedImgIndex = 0;

    public PhotoView(Context context) {
        super(context);
    }

    public PhotoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void validateView(Question question) {
        super.validateView(question);
        questionText.setMovementMethod(LinkMovementMethod.getInstance());
        String subQuestionNumber = TextUtils.isEmpty(question.getSubQuestionNumber())
                ? "" : question.getSubQuestionNumber();
        if (question.getMaximumPhotos() > 1) {
            String string = getContext().getString(R.string.maximum_photo, question.getMaximumPhotos());
            questionText.setText(Html.fromHtml(subQuestionNumber + question.getQuestion() + string));
        } else {
            questionText.setText(Html.fromHtml(subQuestionNumber + question.getQuestion()));
        }
        presenter.loadAnswers();
    }

    @Override
    public void configureView(Question question) {
        showLoading(false);
    }

    @Override
    public void fillViewWithAnswers(List<Answer> answers) {
        if (answers.isEmpty()) presenter.addEmptyAnswer();
        refreshPhotoGallery(answers);
        hideLoading();
    }

    @Override
    public void fillViewWithCustomFieldImageUrls(List<CustomFieldImageUrls> customFieldImageUrlsList) {

    }

    @SuppressWarnings("unused")
    public void onEventMainThread(PhotoEvent event) {
        presenter.onPhotoEvent(event);
    }

    @OnClick({R.id.photo, R.id.rePhotoButton, R.id.deletePhotoButton, R.id.confirmButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.photo:
                 presenter.onPhotoClicked(currentSelectedPhoto);
                break;
            case R.id.rePhotoButton:
                presenter.onPhotoRequested(currentSelectedPhoto);
                break;
            case R.id.deletePhotoButton:
                //anil
                /*try {


                    if (imageList.size() > 0) {
                        imageList.remove(selectedImgIndex);
                        horizonalImgAdapter.notifyDataSetChanged();

                        if (imageList.size() != 0) {
                            Glide.with(getContext()).load(imageList.get(selectedImgIndex).imagePath).into(photo);
                        }else {
                            deletePhotoButton.setVisibility(GONE);
                            photo.setImageResource(0);
                            photo.setBackgroundResource(R.drawable.camera_icon);
                        }

                        if (selectedImgIndex == 0) {

                        } else selectedImgIndex = selectedImgIndex - 1;


                    }

                }catch (Exception e){

                }*/
                 presenter.onPhotoDeleted(currentSelectedPhoto);
                break;
            case R.id.confirmButton:
                presenter.onPhotoConfirmed(currentSelectedPhoto);
                break;
        }
    }


    @Override
    public int getLayoutResId() {
        return R.layout.view_photo_question;
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPause() {
        hideLoading();
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void showPhotoCanNotBeAddDialog() {
        DialogUtils.showPhotoCanNotBeAddDialog(getContext());
    }

    @Override
    public void refreshPhotoGallery(List<Answer> answers) {
        galleryLayout.removeAllViews();
        for (int i = 0; i < answers.size(); i++) {
            addItemToGallery(i, answers.get(i));
        }
    }

    private void addItemToGallery(final int position, Answer answer) {
        View convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_photo_gallery, null);
        ImageView photo = convertView.findViewById(R.id.image);
        ImageView imageFrame = convertView.findViewById(R.id.imageFrame);
        if (!TextUtils.isEmpty(answer.getFileUri()) && answer.getChecked()) {
            Bitmap bitmap = SelectImageManager.prepareBitmap(new File(answer.getFileUri()), 100);
            photo.setImageBitmap(bitmap);

            //anil
            //photo.setBackgroundResource(R.drawable.camera_icon);

        } else {
            photo.setBackgroundResource(R.drawable.camera_icon);
        }

        //anil
        /*photo.setOnClickListener(v -> {
            if (imageList.size() >= 10) {
                Toast.makeText(getContext(), R.string.cant_select_more_then_10_img, Toast.LENGTH_SHORT).show();
                return;
            }
            presenter.onGalleryPhotoRequested(currentSelectedPhoto, imageList.size());

        });*/

        if (position == currentSelectedPhoto) imageFrame.setVisibility(View.VISIBLE);
        convertView.setOnClickListener(v -> {
            currentSelectedPhoto = position;
            presenter.selectGalleryPhoto(position);
        });

        galleryLayout.addView(convertView);
    }

    @Override
    public void selectGalleryPhoto(int position) {
        for (int i = 0; i < galleryLayout.getChildCount(); i++) {
            View convertView = galleryLayout.getChildAt(i);
            ImageView imageFrame = (ImageView) convertView.findViewById(R.id.imageFrame);
            if (i == currentSelectedPhoto)
                imageFrame.setVisibility(View.VISIBLE);
            else
                imageFrame.setVisibility(View.GONE);
        }
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        photo.setImageBitmap(bitmap);
        if (bitmap == null) photo.setBackgroundResource(R.drawable.camera_icon);
    }

    @Override
    public void setImagePath(String commaSeperator) {
        String[] elements = commaSeperator.split(",");
        List<String> list = Arrays.asList(elements);

        for(int i=0;i<list.size();i++){
            GalleryInfo galleryInfo = new GalleryInfo();
            galleryInfo.isSelected = true;
            galleryInfo.id = i;
            galleryInfo.imagePath = list.get(i);
            imageList.add(galleryInfo);
        }


        horizonalImgAdapter = new HorizonalImgAdapter(imageList, getContext());
        recyclerview_gallery.setLayoutManager(new LinearLayoutManager(getContext()
                , LinearLayoutManager.HORIZONTAL, true));
        recyclerview_gallery.setAdapter(horizonalImgAdapter);

        //anil
        /*if (imageList.size() > 0) {
            deletePhotoButton.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(imageList.get(0).imagePath).into(photo);
        } else {
            deletePhotoButton.setVisibility(View.GONE);
        }*/

        horizonalImgAdapter.setListner(this::onItemClick);
    }


    @Override
    public int getCurrentPos() {
        return currentSelectedPhoto;
    }

    @Override
    public void setCurrentPos(int pos) {
        currentSelectedPhoto = pos;
    }

    @Override
    public void refreshConfirmButton(boolean isPhotoAdded) {
        if (isPhotoAdded) {
            confirmButton.setVisibility(View.VISIBLE);
            confirmButton.setEnabled(!isPhotoAdded);
            if (isPhotoAdded) {
                confirmButton.setBackgroundResource(R.drawable.btn_square_green);
                confirmButton.setImageResource(R.drawable.check_square_white);
            } else {
                confirmButton.setBackgroundResource(R.drawable.btn_square_active);
                confirmButton.setImageResource(R.drawable.check_square_green);
            }
        } else {
            confirmButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void refreshRePhotoButton(boolean isPhotoAdded) {
        if (isPhotoAdded) {
            rePhotoButton.setVisibility(View.VISIBLE);
        } else {
            rePhotoButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void refreshDeletePhotoButton(boolean isPhotoAdded) {
        if (isPhotoAdded) {
            deletePhotoButton.setVisibility(View.VISIBLE);
        } else {
            deletePhotoButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void getSelectedImgPath(HashMap<String, GalleryInfo> selectedPath) {
        List<GalleryInfo> values = new ArrayList<>(selectedPath.values());

        imageList.addAll(values);

        horizonalImgAdapter = new HorizonalImgAdapter(imageList, getContext());
        recyclerview_gallery.setLayoutManager(new LinearLayoutManager(getContext()
                , LinearLayoutManager.HORIZONTAL, true));
        recyclerview_gallery.setAdapter(horizonalImgAdapter);

        if (selectedPath.size() > 0) {
            deletePhotoButton.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(imageList.get(0).imagePath).into(photo);
        } else {
            deletePhotoButton.setVisibility(View.GONE);
        }

        horizonalImgAdapter.setListner(this::onItemClick);
    }


    @Override
    public void onItemClick(GalleryInfo galleryInfo, int pos) {
        selectedImgIndex = pos;
        Glide.with(getContext()).load(galleryInfo.imagePath).into(photo);
    }
}
