package fonction;

import org.opencv.core.Point;

public class CalculDistance {

    public static double calculerDistance(Point p1, Point p2) {
        return Math.hypot(p1.x - p2.x, p1.y - p2.y);
    }
}
