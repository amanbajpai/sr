package com.ros.smartrocket.map.polygon;

/**
 * Line is defined by starting point and ending point on 2D dimension.<br>
 *
 * @author Roman Kushnarenko (sromku@gmail.com)
 */
public class Line {
    private final Point startPoint;
    private final Point endPoint;
    private double aPoint = Double.NaN;
    private double bPoint = Double.NaN;
    private boolean vertical = false;

    public Line(Point start, Point end) {
        startPoint = start;
        endPoint = end;

        if (endPoint.x - startPoint.x != 0) {
            aPoint = ((endPoint.y - startPoint.y) / (endPoint.x - startPoint.x));
            bPoint = startPoint.y - aPoint * startPoint.x;
        } else {
            vertical = true;
        }
    }

    /**
     * Indicate whereas the point lays on the line.
     *
     * @param point - The point to check
     * @return <code>True</code> if the point lays on the line, otherwise return <code>False</code>
     */
    public boolean isInside(Point point) {
        double maxX = startPoint.x > endPoint.x ? startPoint.x : endPoint.x;
        double minX = startPoint.x < endPoint.x ? startPoint.x : endPoint.x;
        double maxY = startPoint.y > endPoint.y ? startPoint.y : endPoint.y;
        double minY = startPoint.y < endPoint.y ? startPoint.y : endPoint.y;

        if ((point.x >= minX && point.x <= maxX) && (point.y >= minY && point.y <= maxY)) {
            return true;
        }
        return false;
    }

    /**
     * Indicate whereas the line is vertical. <br>
     * For example, line like x=1 is vertical, in other words parallel to axis Y. <br>
     * In this case the A is (+/-)infinite.
     *
     * @return <code>True</code> if the line is vertical, otherwise return <code>False</code>
     */
    public boolean isVertical() {
        return vertical;
    }

    /**
     * y = <b>A</b>x + B
     *
     * @return The <b>A</b>
     */
    public double getA() {
        return aPoint;
    }

    /**
     * y = Ax + <b>B</b>
     *
     * @return The <b>B</b>
     */
    public double getB() {
        return bPoint;
    }

    /**
     * Get start point
     *
     * @return The start point
     */
    public Point getStart() {
        return startPoint;
    }

    /**
     * Get end point
     *
     * @return The end point
     */
    public Point getEnd() {
        return endPoint;
    }

    @Override
    public String toString() {
        return String.format("%s-%s", startPoint.toString(), endPoint.toString());
    }
}
