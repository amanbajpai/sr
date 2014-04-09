package com.ros.smartrocket.map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.utils.L;
import com.twotoasters.clusterkraf.ClusterPoint;
import com.twotoasters.clusterkraf.MarkerOptionsChooser;

import java.lang.ref.WeakReference;

public class TaskOptionsChooser extends MarkerOptionsChooser {
    private static final String TAG = TaskOptionsChooser.class.getSimpleName();
    private static final int CLUSTER_PAINT_ALPHA = 255;
    private static final int CLUSTER_SIZE_100 = 100;
    private static final int CLUSTER_SIZE_1000 = 1000;
    private static final float ANCHOR_MARKER_U = 0.5f;
    private static final float ANCHOR_MARKER_V = 1.0f;

    private final WeakReference<Context> contextRef;
    private final Paint clusterPaintLarge;
    private final Paint clusterPaintMedium;
    private final Paint clusterPaintSmall;

    public TaskOptionsChooser(Context context) {
        this.contextRef = new WeakReference<Context>(context);

        Resources res = context.getResources();

        clusterPaintMedium = new Paint();
        clusterPaintMedium.setColor(Color.WHITE);
        clusterPaintMedium.setAlpha(CLUSTER_PAINT_ALPHA);
        clusterPaintMedium.setTextAlign(Paint.Align.CENTER);
        clusterPaintMedium.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC));
        clusterPaintMedium.setTextSize(res.getDimension(R.dimen.text_size_medium));

        clusterPaintSmall = new Paint(clusterPaintMedium);
        clusterPaintSmall.setTextSize(res.getDimension(R.dimen.text_size_small));

        clusterPaintLarge = new Paint(clusterPaintMedium);
        clusterPaintLarge.setTextSize(res.getDimension(R.dimen.text_size_large));
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
                icon = BitmapDescriptorFactory.fromBitmap(getClusterBitmap(res, R.drawable.ic_map_cluster_pin,
                        clusterSize));
                title = "" + clusterSize;
            } else {
                Task data = (Task) clusterPoint.getPointAtOffset(0).getTag();
                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin);
                title = data.getName();
                markerOptions.snippet("" + data.getId());
            }
            markerOptions.icon(icon);
            markerOptions.title(title);
            markerOptions.anchor(ANCHOR_MARKER_U, ANCHOR_MARKER_V);
            L.d(TAG, "choose() [size=" + clusterPoint.size() + ", isCluster="
                    + isCluster + ", " + "title=" + title + "]");

        }
    }

    private Bitmap getClusterBitmap(Resources res, int resourceId, int clusterSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true;
        }
        Bitmap bitmap = BitmapFactory.decodeResource(res, resourceId, options);
        if (!bitmap.isMutable()) {
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        }

        Canvas canvas = new Canvas(bitmap);

        Paint paint;
        float originY;
        if (clusterSize < CLUSTER_SIZE_100) {
            paint = clusterPaintLarge;
            originY = bitmap.getHeight() * 0.64f;
        } else if (clusterSize < CLUSTER_SIZE_1000) {
            paint = clusterPaintMedium;
            originY = bitmap.getHeight() * 0.6f;
        } else {
            paint = clusterPaintSmall;
            originY = bitmap.getHeight() * 0.56f;
        }

        canvas.drawText(String.valueOf(clusterSize), bitmap.getWidth() * 0.5f, originY, paint);
        return bitmap;
    }
}