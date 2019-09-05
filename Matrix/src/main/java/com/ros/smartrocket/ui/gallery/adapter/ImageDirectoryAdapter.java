package com.ros.smartrocket.ui.gallery.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ros.smartrocket.R;
import com.ros.smartrocket.ui.gallery.listner.BucketClick;
import com.ros.smartrocket.ui.gallery.model.BucketInfo;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class ImageDirectoryAdapter extends RecyclerView.Adapter <ImageDirectoryAdapter.ViewHolder>{

    private  List<BucketInfo> imageBucketList;
    private  Context mContext;
    private BucketClick bucketClick;

    public ImageDirectoryAdapter(Context mContext, List<BucketInfo> imageBucketList,BucketClick bucketClick) {
        this.mContext = mContext;
        this.imageBucketList = imageBucketList;
        this.bucketClick = bucketClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_bucket,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BucketInfo bucketInfo = imageBucketList.get(position);
        Glide.with(mContext).load(new File(imageBucketList.get(position).getFirstImageContainedPath())).into(holder.iv_gallery);
        holder.tv_bucket_name.setText(bucketInfo.getName());
        holder.tv_count.setText(bucketInfo.getMediaCount()+"");

    }

    @Override
    public int getItemCount() {
        return imageBucketList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_gallery;
        TextView tv_bucket_name,tv_count;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_gallery = itemView.findViewById(R.id.iv_gallery);
            tv_bucket_name = itemView.findViewById(R.id.tv_bucket_name);
            tv_count = itemView.findViewById(R.id.tv_count);

            itemView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            bucketClick.onBucketClickListner(imageBucketList.get(getAdapterPosition()));
        }
    }
}
