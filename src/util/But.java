package util;

import org.opencv.core.Point;
import org.opencv.core.Rect;

public class But {

     
    private Point position;
    private Rect dimension;
    public But(Point position, Rect dimension) {
        this.position = position;
        this.dimension = dimension;
    }
    public Point getPosition() {
        return position;
    }
    public void setPosition(Point position) {
        this.position = position;
    }
    public Rect getDimension() {
        return dimension;
    }
    public void setDimension(Rect dimension) {
        this.dimension = dimension;
    }
    
}
