package com.ros.smartrocket.map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.UIUtils;
import com.twotoasters.clusterkraf.ClusterPoint;
import com.twotoasters.clusterkraf.MarkerOptionsChooser;

import java.lang.ref.WeakReference;

public class TaskOptionsChooser extends MarkerOptionsChooser {
    private static final String TAG = TaskOptionsChooser.class.getSimpleName();
    private static final float ANCHOR_MARKER_U = 0.5f;
    private static final float ANCHOR_MARKER_V = 1.0f;

    private final WeakReference<Context> contextRef;
    private final Paint clusterPaintLarge;
    private final Paint clusterPaintMedium;
    private final Paint clusterPaintSmall;

    public TaskOptionsChooser(Context context) {
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
                markerOptions.snippet(data.getId() + "_" + data.getWaveId() + "_" + data.getStatusId());
            }
            markerOptions.icon(icon);
            markerOptions.title(title);
            markerOptions.anchor(ANCHOR_MARKER_U, ANCHOR_MARKER_V);
            L.d(TAG, "choose() [size=" + clusterPoint.size() + ", isCluster="
                    + isCluster + ", " + "title=" + title + "]");
        }
    }
}