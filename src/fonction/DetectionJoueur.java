package fonction;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import util.Joueur;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DetectionJoueur {

    public static List<Joueur> detecterJoueurs(Mat hsvImage, Scalar lowerBound, Scalar upperBound, String couleur) {
        Mat mask = new Mat();
        Core.inRange(hsvImage, lowerBound, upperBound, mask);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        return contours.stream()
                .map(Imgproc::boundingRect)
                .map(rect -> new Joueur(new Point(rect.x + rect.width / 2, rect.y + rect.height / 2), couleur, rect))
                .collect(Collectors.toList());
    }

    public static Joueur detecterGardien(List<Joueur> joueurs, double positionButGauche, double positionButDroit) {
        return joueurs.stream()
                .filter(joueur -> joueur.getPosition().x < positionButGauche || joueur.getPosition().x > positionButDroit)
                .min(Comparator.comparingDouble(joueur -> Math.abs(joueur.getPosition().x - (joueur.getPosition().x < positionButGauche ? positionButGauche : positionButDroit))))
                .orElse(null);
    }

    public  static Joueur detecterJoueurProcheDuBallon(List<Joueur> joueursRouges, List<Joueur> joueursBleus, Point positionBallon) {
        List<Joueur> tousLesJoueurs = new ArrayList<>();
        tousLesJoueurs.addAll(joueursRouges);
        tousLesJoueurs.addAll(joueursBleus);

        return tousLesJoueurs.stream()
                .min(Comparator.comparingDouble(j -> CalculDistance.calculerDistance(j.getPosition(), positionBallon)))
                .orElse(null);
    }
}
