package com.hello.hu.ex3;

import android.support.annotation.NonNull;

import org.opencv.core.Point;

/**
 * [Class Overview]
 * A data store class.
 * It stores 2 points that can exactly draw a hough line.
 * And it calculate and store the square of the distance between 2 points.
 * @author Chengzhi Hu
 * Compiler used: Android Studio 2.3.1
 * Language used: Java SE 8
 * min Android SDK version: API level 21 (Android 5.0)
 * target Android SDK version: API level 25 (Android 7.1)
 * OpenCV version: 3.1.0
 */

public class LineInformation implements Comparable<LineInformation>{
    private Point pt1;
    private Point pt2;
    private int length;

    /**
     * Store points, and calculate it's distance.
     * @param pt1 Point 1.
     * @param pt2 Point 2.
     */
    public LineInformation(Point pt1, Point pt2) {
        this.pt1 = pt1;
        this.pt2 = pt2;
        this.calculateLength();
    }

    /**
     * Getter of point 1
     * @return Point 1
     */
    public Point getPt1() {
        return pt1;
    }


    /**
     * Getter of point 2
     * @return Point 2
     */
    public Point getPt2() {
        return pt2;
    }

    /**
     * calculate and store the square of the distance between 2 points.
     * Call this method in constructor.
     */
    private void calculateLength()
    {
        length = ((int)((pt1.x - pt2.x)*(pt1.x - pt2.x))+(int)((pt1.y-pt2.y)*(pt1.y-pt2.y)));
    }


    /**
     * Sort this class by variable length in descending order.
     * Thus, we can use Collections.sort(List<LineInformation>) to sort directly.
     * @param o Another LineInformation class will be compared with.
     * @return 1, if this class is smaller. 0 if equals. -1, if this class is bigger.
     */

    @Override
    public int compareTo(@NonNull LineInformation o) {
        return (this.length < o.length ? 1 : (this.length == o.length) ? 0 : -1);
    }
}
