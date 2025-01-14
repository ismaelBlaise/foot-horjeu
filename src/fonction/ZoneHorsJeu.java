package fonction;

import org.opencv.core.Point;

public class ZoneHorsJeu {
    private final Point centre;
    private final int rayon;

    public ZoneHorsJeu(Point centre, int rayon) {
        this.centre = centre;
        this.rayon = rayon;
    }

    public Point getCentre() {
        return centre;
    }

    public int getRayon() {
        return rayon;
    }
}
