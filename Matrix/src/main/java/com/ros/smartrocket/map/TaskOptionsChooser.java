package com.ros.smartrocket.map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.fragment.TasksMapFragment;
import com.ros.smartrocket.utils.FontUtils;
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
        clusterPaintMedium.setColor(res.getColor(R.color.green));
        clusterPaintMedium.setAlpha(CLUSTER_PAINT_ALPHA);
        clusterPaintMedium.setTextAlign(Paint.Align.CENTER);
        clusterPaintMedium.setTypeface(FontUtils.loadFontFromAsset(context.getAssets(),
                FontUtils.getFontAssetPath(3)));
        clusterPaintMedium.setTextSize(res.getDimension(R.dimen.text_size_22sp));

        clusterPaintSmall = new Paint(clusterPaintMedium);
        clusterPaintSmall.setTextSize(res.getDimension(R.dimen.text_size_18sp));

        clusterPaintLarge = new Paint(clusterPaintMedium);
        clusterPaintLarge.setTextSize(res.getDimension(R.dimen.text_size_26sp));
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

                icon = getPinBitmap(data);
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
            originY = bitmap.getHeight() * 0.6f;
        } else if (clusterSize < CLUSTER_SIZE_1000) {
            paint = clusterPaintMedium;
            originY = bitmap.getHeight() * 0.56f;
        } else {
            paint = clusterPaintSmall;
            originY = bitmap.getHeight() * 0.52f;
        }

        canvas.drawText(String.valueOf(clusterSize), bitmap.getWidth() * 0.5f, originY, paint);
        return bitmap;
    }

    private BitmapDescriptor getPinBitmap(Task task) {
        BitmapDescriptor icon;
        switch (TasksBL.getTaskStatusType(task.getStatusId())) {
            case none:
            case claimed:
            case started:
                if (!task.getIsHide()) {
                    if (task.getDistance() <= TasksMapFragment.taskRadius) {
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.pin_green);
                    } else {
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.pin_light_green);
                    }
                } else {
                    if (task.getDistance() <= TasksMapFragment.taskRadius) {
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.pin_green_hidden);
                    } else {
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.pin_light_green_hidden);
                    }
                }
                break;
            case scheduled:
            case pending:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.pin_blue);
                break;
            case completed:
            case validation:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.pin_grey);
                break;
            case reDoTask:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.pin_red);
                break;

            default:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin);
                break;
        }

        return icon;
    }
}