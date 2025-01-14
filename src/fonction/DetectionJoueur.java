package fonction;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import util.Joueur;

import java.util.ArrayList;
import java.util.List;

public class DetectionJoueur {

    public static List<Joueur> detecterJoueurs(Mat hsvImage, Scalar lowerBound1, Scalar upperBound1, Scalar lowerBound2, Scalar upperBound2, String couleur) {
        Mat mask1 = new Mat();
        Core.inRange(hsvImage, lowerBound1, upperBound1, mask1);
    
        Mat mask = mask1.clone();
    
        if (lowerBound2 != null && upperBound2 != null) {
            Mat mask2 = new Mat();
            Core.inRange(hsvImage, lowerBound2, upperBound2, mask2);
            Core.add(mask1, mask2, mask);
        }
    
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
    
        List<Joueur> joueursDetectes = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            if (Imgproc.contourArea(contour) > 200) {
                Rect rect = Imgproc.boundingRect(contour);
                Joueur joueur = new Joueur(new Point(rect.x + rect.width / 2, rect.y + rect.height / 2), couleur, rect);
                joueursDetectes.add(joueur);
            }
        }
        return joueursDetectes;
    }
    
    
    

    public static Joueur detecterGardien(List<Joueur> joueurs, String couleur, double positionButGauche, double positionButDroit) {
        Joueur gardienDetecte = null;
        double distanceMin = Double.MAX_VALUE;
    
         
        for (Joueur joueur : joueurs) {
            
            if (joueur.getCouleur().equals(couleur)) {
    
                
                double distanceGauche = Math.abs(joueur.getPosition().x - positionButGauche);
                double distanceDroit = Math.abs(joueur.getPosition().x - positionButDroit);
    
                 
                double distance = Math.min(distanceGauche, distanceDroit);
    
                 
                if (distance < distanceMin) {
                    distanceMin = distance;
                    gardienDetecte = joueur;
                }
            }
        }
    
        return gardienDetecte;
    }
     
    
    
    

    public static Joueur detecterJoueurProcheDuBallon(List<Joueur> joueursRouges, List<Joueur> joueursBleus, Point positionBallon) {
        List<Joueur> tousLesJoueurs = new ArrayList<>();
        tousLesJoueurs.addAll(joueursRouges);
        tousLesJoueurs.addAll(joueursBleus);
    
        Joueur joueurProche = null;
        double distanceMin = Double.MAX_VALUE;
    
        for (Joueur joueur : tousLesJoueurs) {
            double distance = calculerDistance(joueur.getPosition(), positionBallon);
            if (distance < distanceMin) {
                distanceMin = distance;
                joueurProche = joueur;
            }
        }
        return joueurProche;
    }
    

    public static Joueur detecterDernierDefenseur(List<Joueur> joueursOpposants, Joueur gardienOpposant) {
        if (gardienOpposant == null) {
            return null;
        }
    
        Joueur dernierDefenseur = null;
        double distanceMin = Double.MAX_VALUE;
    
        for (Joueur joueur : joueursOpposants) {
            if (!joueur.equals(gardienOpposant)) {
                double distance = calculerDistance(joueur.getPosition(), gardienOpposant.getPosition());
                if (distance < distanceMin) {
                    distanceMin = distance;
                    dernierDefenseur = joueur;
                }
            }
        }
        return dernierDefenseur;
    }
    

    private static double calculerDistance(Point p1, Point p2) {
        return Math.hypot(p1.x - p2.x, p1.y - p2.y);
    }
}
