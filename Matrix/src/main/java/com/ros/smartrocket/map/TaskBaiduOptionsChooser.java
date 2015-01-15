package com.ros.smartrocket.map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.utils.UIUtils;
import com.twotoasters.baiduclusterkraf.ClusterPoint;
import com.twotoasters.baiduclusterkraf.MarkerOptionsChooser;

import java.lang.ref.WeakReference;

public class TaskBaiduOptionsChooser extends MarkerOptionsChooser {
    private static final String TAG = TaskBaiduOptionsChooser.class.getSimpleName();
    private static final float ANCHOR_MARKER_U = 0.5f;
    private static final float ANCHOR_MARKER_V = 1.0f;

    private final WeakReference<Context> contextRef;
    private final Paint clusterPaintLarge;
    private final Paint clusterPaintMedium;
    private final Paint clusterPaintSmall;

    public TaskBaiduOptionsChooser(Context context) {
        this.contextRef = new WeakReference<Context>(context);

        clusterPaintMedium = MapHelper.getMediumClasterPaint(context);
        clusterPaintSmall = MapHelper.getSmallClasterPaint(context);
        clusterPaintLarge = MapHelper.getLargeClasterPaint(context);
    }

    @Override
    public void choose(MarkerOptions markerOptions, ClusterPoint clusterPoint) {
        Context context = contextRef.get();
        if (context != null) {
            Resources res = context.getResources();
            boolean isCluster = clusterPoint.size() > 1;
            BitmapDescriptor icon;
            String title;
            if (isCluster) {
                int clusterSize = clusterPoint.size();
                icon = BitmapDescriptorFactory.fromBitmap(MapHelper.getClusterBitmap(res, R.drawable.ic_map_cluster_pin,
                        clusterSize, clusterPaintLarge, clusterPaintMedium, clusterPaintSmall));
                title = "" + clusterSize;
            } else {
                Task data = (Task) clusterPoint.getPointAtOffset(0).getTag();

                icon = BitmapDescriptorFactory.fromResource(UIUtils.getPinResId(data));
                title = data.getName();

                Bundle bundle = new Bundle();
                bundle.putSerializable(Keys.TASK, data);
                markerOptions.extraInfo(bundle);
            }
            markerOptions.icon(icon);
            markerOptions.title(title);
            markerOptions.anchor(ANCHOR_MARKER_U, ANCHOR_MARKER_V);
        }
    }
}