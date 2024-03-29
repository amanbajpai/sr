package com.twotoasters.baiduclusterkraf;

import com.baidu.mapapi.map.Marker;

/**
 * Because Clusterkraf must set its own OnInfoWindowClickListener on the
 * GoogleMap it is managing, and because the GoogleMap can only have one
 * OnInfoWindowClickListener, Clusterkraf passes the event downstream to its
 * users.
 */
public interface OnInfoWindowClickListener {
	/**
	 * @param marker
	 *            The Marker object of the info window that was clicked.
	 * @param clusterPoint
	 *            The ClusterPoint object representing the Marker whose info
	 *            window was clicked. In case you have manually added Marker
	 *            objects directly to the map, bypassing Clusterkraf, this will
	 *            be null if the clicked info window belongs to a Marker object
	 *            that was added manually.
	 * @return true if you have fully consumed the event, or false if
	 *         Clusterkraf still needs to carry out the configured action.
	 */
	boolean onInfoWindowClick(Marker marker, ClusterPoint clusterPoint);
}
