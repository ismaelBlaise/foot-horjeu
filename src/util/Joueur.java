package util;

import org.opencv.core.Point;
import org.opencv.core.Rect;

public class Joueur {
    Point position;
    String couleur;
    Rect dimension;

    public Joueur(Point position, String couleur, Rect dimension) {
        this.position = position;
        this.couleur = couleur;
        this.dimension = dimension;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public Rect getDimension() {
        return dimension;
    }

    public void setDimension(Rect dimension) {
        this.dimension = dimension;
    }
}
