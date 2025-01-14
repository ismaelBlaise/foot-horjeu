package fonction;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import util.Joueur;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DetectionJoueur {

    public static List<Joueur> detecterJoueurs(Mat hsvImage, Scalar lowerBound1, Scalar upperBound1, Scalar lowerBound2, Scalar upperBound2, String couleur) {
        // Masque pour la première plage
        Mat mask1 = new Mat();
        Core.inRange(hsvImage, lowerBound1, upperBound1, mask1);
    
        Mat mask = mask1.clone(); // Initialisation du masque final
    
        // Appliquer la deuxième plage si elle n'est pas nulle
        if (lowerBound2 != null && upperBound2 != null) {
            Mat mask2 = new Mat();
            Core.inRange(hsvImage, lowerBound2, upperBound2, mask2);
            Core.add(mask1, mask2, mask); // Fusion des deux masques
        }
    
        // Détection des contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
    
        // Filtrage des petits contours et conversion en objets Joueur
        return contours.stream()
                .filter(contour -> Imgproc.contourArea(contour) > 200) // Filtrer les petits objets
                .map(Imgproc::boundingRect)
                .map(rect -> new Joueur(new Point(rect.x + rect.width / 2, rect.y + rect.height / 2), couleur, rect))
                .collect(Collectors.toList());
    }
    
    

    public static Joueur detecterGardien(List<Joueur> joueurs, double positionButGauche , double positionButDroit) {
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

    public static Joueur detecterDernierDefenseur(List<Joueur> joueursOpposants, Joueur gardienOpposant) {
        if (gardienOpposant == null) {
            return null;
        }

        return joueursOpposants.stream()
                .filter(joueur -> !joueur.equals(gardienOpposant))
                .min(Comparator.comparingDouble(j -> calculerDistance(j.getPosition(), gardienOpposant.getPosition())))
                .orElse(null);
    }

    private static double calculerDistance(Point p1, Point p2) {
        return Math.hypot(p1.x - p2.x, p1.y - p2.y);
    }
}
