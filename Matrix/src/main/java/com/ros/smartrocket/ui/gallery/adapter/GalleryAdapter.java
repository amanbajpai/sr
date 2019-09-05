package com.ros.smartrocket.ui.gallery.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.ros.smartrocket.R;
import com.ros.smartrocket.ui.gallery.listner.GalleryClick;
import com.ros.smartrocket.ui.gallery.model.GalleryInfo;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class GalleryAdapter  extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private List<GalleryInfo> galleryList;
    private Context mContext;
    private boolean isAtleastoneSelected;
    private GalleryClick galleryClick;

    public GalleryAdapter(List<GalleryInfo> galleryList, Context mContext,GalleryClick galleryClick) {
        this.galleryList = galleryList;
        this.mContext = mContext;
        this.galleryClick = galleryClick;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_gallery,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       // holder.iv_gallery.setImageURI(Uri.parse(galleryList.get(position).imagePath));


        Glide.with(mContext).load(new File(galleryList.get(position).imagePath)).into(holder.iv_gallery);


        if(galleryList.get(position).isSelected){
            holder.rl_selected.setVisibility(View.VISIBLE);
        }else holder.rl_selected.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        ImageView iv_gallery;
        RelativeLayout rl_selected;

        public ViewHolder(View itemView) {
            super(itemView);

            rl_selected = itemView.findViewById(R.id.rl_selected);
            iv_gallery = itemView.findViewById(R.id.iv_gallery);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this::onClick);
        }


        @Override
        public boolean onLongClick(View v) {
            galleryClick.onLongPress(getAdapterPosition(),galleryList.get(getAdapterPosition()));
            return true;
        }

        @Override
        public void onClick(View v) {
            galleryClick.onNormalClick(getAdapterPosition(),galleryList.get(getAdapterPosition()));

        }
    }
}
