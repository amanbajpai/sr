package com.twotoasters.baiduclusterkraf;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;

import java.lang.ref.WeakReference;

/**
 * An OnCameraChangeListener that calls back to its host only when appropriate
 */
class ClusteringOnCameraChangeListener implements BaiduMap.OnMapStatusChangeListener {

    private final Options options;
    private final WeakReference<Host> hostRef;

    private long dirty = 0;

    public ClusteringOnCameraChangeListener(Host host, Options options) {
        this.hostRef = new WeakReference<Host>(host);
        this.options = options;
    }

    public void setDirty(long when) {
        this.dirty = when;
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {
        onCameraChange();
    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        onCameraChange();
    }

    public void onCameraChange() {
        long now = System.currentTimeMillis();
        long notDirtyAfter = now - options.getClusteringOnCameraChangeListenerDirtyLifetimeMillis();
        if (dirty < notDirtyAfter) {
            Host host = hostRef.get();
            if (host != null) {
                dirty = now;
                host.onClusteringCameraChange();
            }
        }
    }

    interface Host {
        void onClusteringCameraChange();
    }

}
