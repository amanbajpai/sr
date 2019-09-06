package com.ros.smartrocket.presentation.question.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ros.smartrocket.R;
import com.ros.smartrocket.ui.gallery.GalleryActivity;
import com.ros.smartrocket.ui.gallery.model.GalleryInfo;

import java.util.List;

import butterknife.OnClick;

public class HorizonalImgAdapter extends RecyclerView.Adapter<HorizonalImgAdapter.ViewHolder> {

    List<GalleryInfo> galleryinfoList;
    Context mContext;
    OnClickImage onClickImage;

    public HorizonalImgAdapter(List<GalleryInfo> values, Context context) {
        this.galleryinfoList = values;
        this.mContext = context;
    }

    public void setListner(OnClickImage onClickImage){
        this.onClickImage = onClickImage;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_img,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GalleryInfo bean = galleryinfoList.get(position);
        Glide.with(mContext).load(bean.imagePath).into(holder.iv_horizontal_img);
    }

    @Override
    public int getItemCount() {
        return galleryinfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_horizontal_img;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_horizontal_img = itemView.findViewById(R.id.iv_horizontal_img);
            itemView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            onClickImage.onItemClick(galleryinfoList.get(getAdapterPosition()),getAdapterPosition());
        }
    }

    public interface OnClickImage{
        void onItemClick(GalleryInfo galleryInfo,int pos);
    }

}
