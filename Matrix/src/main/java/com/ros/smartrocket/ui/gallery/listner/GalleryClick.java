package com.ros.smartrocket.ui.gallery.listner;

import com.ros.smartrocket.ui.gallery.model.GalleryInfo;

public interface GalleryClick {
    void onNormalClick(int adapterPosition, GalleryInfo galleryInfo);
    void onLongPress(int adapterPosition, GalleryInfo galleryInfo);
}
