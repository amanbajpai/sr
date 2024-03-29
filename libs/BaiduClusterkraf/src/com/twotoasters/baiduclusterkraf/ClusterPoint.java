package com.twotoasters.baiduclusterkraf;

import java.util.ArrayList;
import java.util.HashSet;

import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;

/**
 * Represents one or more InputPoint objects that have been clustered together
 * due to pixel proximity
 */
public class ClusterPoint extends BasePoint {

	private final ArrayList<InputPoint> pointsInClusterList = new ArrayList<InputPoint>();
	private final HashSet<InputPoint> pointsInClusterSet = new HashSet<InputPoint>();

	private final boolean transition;

	private LatLngBounds boundsOfInputPoints;

	ClusterPoint(InputPoint initialPoint, Projection projection, boolean transition) {
		this.mapPosition = initialPoint.getMapPosition();
		this.transition = transition;
		add(initialPoint);
		buildScreenPosition(projection);
	}

	ClusterPoint(InputPoint initialPoint, Projection projection, boolean transition, LatLng overridePosition) {
		this(initialPoint, projection, transition);
		this.mapPosition = overridePosition;
	}

	void add(InputPoint point) {
		pointsInClusterList.add(point);
		pointsInClusterSet.add(point);

		boundsOfInputPoints = null;
	}

	ArrayList<InputPoint> getPointsInCluster() {
		return pointsInClusterList;
	}

	/**
	 * @param index
	 * @return the InputPoint at the given index
	 */
	public InputPoint getPointAtOffset(int index) {
		return pointsInClusterList.get(index);
	}

	/**
	 * @return the number of InputPoint objects in this ClusterPoint
	 */
	public int size() {
		return pointsInClusterList.size();
	}

	/**
	 * @param point
	 * @return true if the InputPoint is in this ClusterPoint, otherwise false
	 */
	public boolean containsInputPoint(InputPoint point) {
		return pointsInClusterSet.contains(point);
	}

	@Override
	void clearScreenPosition() {
		super.clearScreenPosition();
		for (InputPoint inputPoint : pointsInClusterList) {
			inputPoint.clearScreenPosition();
		}
	}

	/**
	 * @return true if this object is part of a transition, otherwise false
	 */
	public boolean isTransition() {
		return transition;
	}

	LatLngBounds getBoundsOfInputPoints() {
		if (boundsOfInputPoints == null) {
			LatLngBounds.Builder builder = new Builder();
			for (InputPoint inputPoint : pointsInClusterList) {
				builder.include(inputPoint.getMapPosition());
			}
			boundsOfInputPoints = builder.build();
		}
		return boundsOfInputPoints;
	}

}
