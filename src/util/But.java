package util;

import org.opencv.core.Point;
import org.opencv.core.Rect;

public class But {

     
    private Point position;
    private Rect dimmension;
    public But(Point position, Rect dimmension) {
        this.position = position;
        this.dimmension = dimmension;
    }
    public Point getPosition() {
        return position;
    }
    public void setPosition(Point position) {
        this.position = position;
    }
    public Rect getDimmension() {
        return dimmension;
    }
    public void setDimmension(Rect dimmension) {
        this.dimmension = dimmension;
    }
}
