package com.ros.smartrocket.map;

public class BaiduMapCluster {

   /* private static final String TAG = "Cluster";
    private static final String TAG_ADD_Cluster = "AddCluster_method";
    private Context context;
    private MapView mMapView;
    private Boolean isAverageCenter;
    private int mGridSize;
    private double mDistance;

    private List<ClusterMarker> mClusterMarkers;

    private final Paint clusterPaintLarge;
    private final Paint clusterPaintMedium;
    private final Paint clusterPaintSmall;


    public Cluster(Context context, MapView mapView, int minClusterSize, Boolean isAverageCenter,
                   int mGridSize, double mDistance) {
        this.context = context;
        this.mMapView = mapView;
        this.isAverageCenter = isAverageCenter;
        this.mGridSize = mGridSize;
        this.mDistance = mDistance;
        mClusterMarkers = new ArrayList<ClusterMarker>();

        clusterPaintMedium = MapHelper.getMediumClasterPaint(context);
        clusterPaintSmall = MapHelper.getSmallClasterPaint(context);
        clusterPaintLarge = MapHelper.getLargeClasterPaint(context);
    }

    public ArrayList<MyOverlayItem> addClusteredPinToBaiduMap(BaiduMap baiduMap, List<InputPoint> markerList) {
//		Log.d("CreateCluster", "markerList.size()"+markerList.size());
        this.mClusterMarkers.clear();
        ArrayList<MyOverlayItem> itemList = new ArrayList<MyOverlayItem>();
//		Log.e(TAG, "createCluster, markerList.size()"+itemList.size());
        for (int i = 0; i < markerList.size(); i++) {
            addCluster(markerList.get(i));
        }
        for (int i = 0; i < mClusterMarkers.size(); i++) {
            ClusterMarker cm = mClusterMarkers.get(i);
            setClusterDrawable(cm);
            MyOverlayItem oi = new MyOverlayItem(cm.getmCenter(), cm.getTitle(), cm.getSnippet());
            oi.setMarker(cm.getMarker());
            itemList.add(oi);
        }

        Log.e(TAG, "itemList.size:" + itemList.size());
        return itemList;
    }

    private void addCluster(MyOverlayItem marker) {
        GeoPoint markGeo = marker.getPoint();

        if (mClusterMarkers.size() == 0) {
            ClusterMarker clusterMarker = new ClusterMarker(marker.getPoint(), marker.getTitle(), marker.getSnippet());
            clusterMarker.setMarker(marker.getMarker());
            clusterMarker.AddMarker(marker, isAverageCenter);
            MBound bound = new MBound(markGeo.getLatitudeE6(), markGeo.getLongitudeE6(), markGeo.getLatitudeE6(), markGeo.getLongitudeE6());
            bound = MapHelper.getExtendedBounds(mMapView, bound, mGridSize);
            clusterMarker.setmGridBounds(bound);
            mClusterMarkers.add(clusterMarker);
        } else {
            ClusterMarker clusterContain = null;
            double distance = mDistance;

            for (int i = 0; i < mClusterMarkers.size(); i++) {
                ClusterMarker clusterMarker = mClusterMarkers.get(i);
                GeoPoint center = clusterMarker.getmCenter();
                double d = DistanceUtil.getDistance(center, marker.getPoint());

                if (d < distance) {
                    distance = d;
                    clusterContain = clusterMarker;
                } else {
//					Log.d(TAG_ADD_Cluster, "d>distence,不满足聚合距离");
                }
            }

//			Log.e("clusterContain == null","isMarkersInCluster:"+isMarkersInCluster(markGeo, clusterContain.getmGridBounds()));
            if (clusterContain == null || !isMarkersInCluster(markGeo, clusterContain.getmGridBounds())) {
                Log.e(TAG_ADD_Cluster, "======clusterContain=======================--------------");
                ClusterMarker clusterMarker = new ClusterMarker(marker.getPoint(), marker.getTitle(), marker.getSnippet());
                clusterMarker.setMarker(marker.getMarker());
                clusterMarker.AddMarker(marker, isAverageCenter);
                MBound bound = new MBound(markGeo.getLatitudeE6(), markGeo.getLongitudeE6(), markGeo.getLatitudeE6(), markGeo.getLongitudeE6());
                bound = MapHelper.getExtendedBounds(mMapView, bound, mGridSize);
                clusterMarker.setmGridBounds(bound);

                mClusterMarkers.add(clusterMarker);

            } else {
                clusterContain.AddMarker(marker, isAverageCenter);
                Log.e(TAG_ADD_Cluster, "添加到选中 clusterMarker:--->clusterContain.size:---->" + clusterContain.getmMarkers().size());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void setClusterDrawable(ClusterMarker clusterMarker) {
        View drawableView = LayoutInflater.from(context).inflate(R.layout.view_baidu_map_cluster, null);
        TextView text = (TextView) drawableView.findViewById(R.id.marker);
        text.setPadding(3, 3, 3, 3);

        Resources res = context.getResources();
        int clusterSize = clusterMarker.getmMarkers().size();
        boolean isCluster = clusterSize > 1;
        Drawable icon;
        String title;

        if (isCluster) {
            icon = new BitmapDrawable(MapHelper.getClusterBitmap(res, R.drawable.ic_map_cluster_pin,
                    clusterSize, clusterPaintLarge, clusterPaintMedium, clusterPaintSmall));
            title = "" + clusterSize;
        } else {
            MyOverlayItem item = clusterMarker.getmMarkers().get(0);

            Task task = (Task) item.getTag();
            icon = context.getResources().getDrawable(UIUtils.getPinResId(task));
            title = "";
            clusterMarker.setSnippet(task.getId() + "_" + task.getWaveId() + "_" + task.getStatusId());
        }

        text.setText(title);

        if (UIUtils.getSdkVersion() < 16) {
            text.setBackgroundDrawable(icon);
        } else {
            text.setBackground(icon);
        }

        Bitmap bitmap = MapHelper.convertViewToBitmap(drawableView);
        clusterMarker.setMarker(new BitmapDrawable(bitmap));
    }

    *//**
     * 判断坐标点是否在MBound 覆盖区域内
     *
     * @param markerGeo
     * @param bound
     * @return
     *//*
    private Boolean isMarkersInCluster(GeoPoint markerGeo, MBound bound) {

        Log.e("isMarkerInCluster", markerGeo.getLatitudeE6() + "----------" + markerGeo.getLongitudeE6());
        Log.e(TAG, "rightTopLat:" + bound.getRightTopLat());
        Log.e(TAG, "rightTopLng:" + bound.getRightTopLng());
        Log.e(TAG, "leftBottomLat:" + bound.getLeftBottomLat());
        Log.e(TAG, "leftBottomlng:" + bound.getLeftBottomLng());

        if (markerGeo.getLatitudeE6() > bound.getLeftBottomLat()
                && markerGeo.getLatitudeE6() < bound.getRightTopLat()
                && markerGeo.getLongitudeE6() > bound.getLeftBottomLng()
                && markerGeo.getLongitudeE6() < bound.getRightTopLng()) {
            return true;
        }
        return false;

    }*/

}
