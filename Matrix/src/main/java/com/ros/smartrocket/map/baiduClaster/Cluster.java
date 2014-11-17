package com.ros.smartrocket.map.baiduClaster;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.List;

public class Cluster {

    private static final String TAG = "Cluster";
    private static final String TAG_ADD_Cluster = "AddCluster_method";
    private Activity context;
    private MapView mMapView;
    private int mMinClusterSize;// 没有使用
    private Boolean isAverageCenter;
    private int mGridSize;
    private double mDistance;

    private List<ClusterMarker> mClusterMarkers;


    public Cluster(Activity context, MapView mapView
            , int minClusterSize, Boolean isAverageCenter
            , int mGridSize, double mDistance) {
        this.context = context;
        this.mMapView = mapView;
        this.mMinClusterSize = minClusterSize;
        this.isAverageCenter = isAverageCenter;
        this.mGridSize = mGridSize;
        this.mDistance = mDistance;
        mClusterMarkers = new ArrayList<ClusterMarker>();
    }

    public ArrayList<Marker> createCluster(List<Marker> markerList) {
//		Log.d("CreateCluster", "markerList.size()"+markerList.size());
        this.mClusterMarkers.clear();
        ArrayList<Marker> itemList = new ArrayList<Marker>();
//		Log.e(TAG, "createCluster, markerList.size()"+itemList.size());
        for (int i = 0; i < markerList.size(); i++) {
            addCluster(markerList.get(i));
        }
        for (int i = 0; i < mClusterMarkers.size(); i++) {
            ClusterMarker cm = mClusterMarkers.get(i);
            setClusterDrawable(cm);
            Marker oi = new Marker(cm.getmCenter(), cm.getTitle(), cm.getSnippet());
            oi.setMarker(cm.getMarker());
            itemList.add(oi);
        }

        Log.e(TAG, "itemList.size:" + itemList.size());
        return itemList;
    }

    private void addCluster(Marker marker) {
        LatLng markGeo = marker.getPosition();
        // 没有ClusterMarkers
        if (mClusterMarkers.size() == 0) {
            ClusterMarker clusterMarker = new ClusterMarker(marker.getPosition(), marker.getTitle(), marker.getExtraInfo());
            //clusterMarker.setMarker(marker.getMarker());
            clusterMarker.AddMarker(marker, isAverageCenter);
            MBound bound = new MBound(markGeo.latitude, markGeo.longitude, markGeo.latitude, markGeo.longitude);
            bound = MapUtils.getExtendedBounds(mMapView, bound, mGridSize);
            clusterMarker.setmGridBounds(bound);
            mClusterMarkers.add(clusterMarker);
        } else {
            ClusterMarker clusterContain = null;
            double distance = mDistance;

            for (int i = 0; i < mClusterMarkers.size(); i++) {
                ClusterMarker clusterMarker = mClusterMarkers.get(i);
                Log.e(TAG_ADD_Cluster, "in mClusterMarker.size  size = = " + mClusterMarkers.size());
                LatLng center = clusterMarker.getmCenter();
                double d = DistanceUtil.getDistance(center, marker.getPosition());

                //[]--------选择clusterMarker 中最近的，clusterMarker-------双重循环-----------[]
                if (d < distance) {
//					Log.e(TAG_ADD_Cluster,"d<distence"+"&&&&&&&&&&&&");
                    distance = d;
                    clusterContain = clusterMarker;
                } else {
//					Log.d(TAG_ADD_Cluster, "d>distence,不满足聚合距离");
                }

                //[]------------------------------[]
            }

//			Log.e("clusterContain == null","isMarkersInCluster:"+isMarkersInCluster(markGeo, clusterContain.getmGridBounds()));
            // 现存的clusterMarker 没有符合条件的
            if (clusterContain == null || !isMarkersInCluster(markGeo, clusterContain.getmGridBounds())) {
//				Log.e(TAG_ADD_Cluster, "======clusterContain=======================--------------");
                ClusterMarker clusterMarker = new ClusterMarker(marker.getPoint(), marker.getTitle(), marker.getSnippet());
                clusterMarker.setMarker(marker.getMarker());
                clusterMarker.AddMarker(marker, isAverageCenter);
                MBound bound = new MBound(markGeo.getLatitudeE6(), markGeo.getLongitudeE6(), markGeo.getLatitudeE6(), markGeo.getLongitudeE6());
                bound = MapUtils.getExtendedBounds(mMapView, bound, mGridSize);
                clusterMarker.setmGridBounds(bound);

                mClusterMarkers.add(clusterMarker);

            } else {
                clusterContain.AddMarker(marker, isAverageCenter);
                Log.e(TAG_ADD_Cluster, "添加到选中 clusterMarker:--->clusterContain.size:---->" + clusterContain.getmMarkers().size());
            }
        }
    }

    private void setClusterDrawable(ClusterMarker clusterMarker) {

        View drawableView = LayoutInflater.from(context).inflate(
                R.layout.drawable_mark, null);
        TextView text = (TextView) drawableView.findViewById(R.id.drawble_mark);
        text.setPadding(3, 3, 3, 3);

        int markNum = clusterMarker.getmMarkers().size();
        Log.e("setClusterDrawable", "!!!!!!!!!!!!!!!!!!!!!!!" + markNum);
        if (markNum >= 2) {
            text.setText(markNum + "");
            if (markNum < 11) {
                text.setBackgroundResource(R.drawable.m0);
            } else if (markNum > 10 && markNum < 21) {
                text.setBackgroundResource(R.drawable.m1);
            } else if (markNum > 20 && markNum < 31) {
                text.setBackgroundResource(R.drawable.m2);
            } else if (markNum > 30 && markNum < 41) {
                text.setBackgroundResource(R.drawable.m3);
            } else {
                text.setBackgroundResource(R.drawable.m4);
            }
            Bitmap bitmap = MapUtils.convertViewToBitmap(drawableView);
            clusterMarker.setMarker(new BitmapDrawable(bitmap));
        } else {

            clusterMarker.setMarker(context.getResources().getDrawable(R.drawable.nav_turn_via_1));
        }
    }

    /**
     * 判断坐标点是否在MBound 覆盖区域内
     *
     * @param markerGeo
     * @param bound
     * @return
     */
    private Boolean isMarkersInCluster(LatLng markerGeo, MBound bound) {

        Log.e("isMarkerInCluster", markerGeo.latitude + "----------" + markerGeo.longitude);
        Log.e(TAG, "rightTopLat:" + bound.getRightTopLat());
        Log.e(TAG, "rightTopLng:" + bound.getRightTopLng());
        Log.e(TAG, "leftBottomLat:" + bound.getLeftBottomLat());
        Log.e(TAG, "leftBottomlng:" + bound.getLeftBottomLng());

        if (markerGeo.latitude > bound.getLeftBottomLat()
                && markerGeo.latitude < bound.getRightTopLat()
                && markerGeo.longitude > bound.getLeftBottomLng()
                && markerGeo.longitude < bound.getRightTopLng()) {
            return true;
        }
        return false;

    }

}
