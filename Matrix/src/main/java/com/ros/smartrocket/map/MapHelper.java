package com.ros.smartrocket.map;

import android.support.v4.app.FragmentActivity;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.R;
import com.ros.smartrocket.fragment.TransparentSupportBaiduMapFragment;
import com.ros.smartrocket.fragment.TransparentSupportMapFragment;

public class MapHelper {

    public static BaiduMap getBaiduMap(FragmentActivity activity, float zoomLevel) {
        BaiduMap baiduMap = null;
        MapStatus ms = new MapStatus.Builder().build();

        BaiduMapOptions uiSettings = new BaiduMapOptions();
        uiSettings.mapStatus(ms);
        uiSettings.compassEnabled(false);
        uiSettings.zoomControlsEnabled(false);

        /*SupportMapFragment mapFragment = SupportMapFragment.newInstance(uiSettings);

        FragmentManager manager = activity.getSupportFragmentManager();
        manager.beginTransaction().add(R.id.map, mapFragment, "map").commit();*/

        /*SupportMapFragment mapFragment = (SupportMapFragment) (activity.getSupportFragmentManager()
                .findFragmentById(R.id.map));*/

        TransparentSupportBaiduMapFragment mapFragment = (TransparentSupportBaiduMapFragment) activity
                .getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            baiduMap = mapFragment.getBaiduMap();
                    /*baiduMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition pos) {
                            zoomLevel = pos.zoom;
                        }
                    });*/

            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.location_icon);
            baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(LocationMode.NORMAL, true, mCurrentMarker));
            baiduMap.setMyLocationEnabled(true);
        }

        return baiduMap;
    }

    public static GoogleMap getGoogleMap(FragmentActivity activity, GoogleMap.OnCameraChangeListener cameraChangeListener) {
        GoogleMap googleMap = null;
        TransparentSupportMapFragment mapFragment = (TransparentSupportMapFragment) activity
                .getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            googleMap = mapFragment.getMap();
            if (googleMap != null) {
                UiSettings uiSettings = googleMap.getUiSettings();
                uiSettings.setAllGesturesEnabled(false);
                uiSettings.setScrollGesturesEnabled(true);
                uiSettings.setZoomGesturesEnabled(true);
                uiSettings.setIndoorLevelPickerEnabled(false);
                googleMap.setIndoorEnabled(false);
                googleMap.setOnCameraChangeListener(cameraChangeListener);
            }
        }

        return googleMap;
    }

    public static boolean isMapNotNull(GoogleMap googleMap, BaiduMap baiduMap) {
        boolean result;
        if (Config.USE_BAIDU) {
            result = baiduMap != null;
        } else {
            result = googleMap != null;
        }
        return result;
    }

    public static void mapChooser(GoogleMap googleMap, BaiduMap baiduMap, SelectMapInterface selectMapInterface) {
        if (isMapNotNull(googleMap, baiduMap)) {
            if (Config.USE_BAIDU) {
                selectMapInterface.useBaiduMap(baiduMap);
            } else {
                selectMapInterface.useGoogleMap(googleMap);
            }
        }
    }

    public interface SelectMapInterface {
        void useGoogleMap(GoogleMap googleMap);

        void useBaiduMap(BaiduMap baiduMap);
    }
}
