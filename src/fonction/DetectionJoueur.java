package fonction;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import util.Equipe;
import util.Joueur;

import java.util.ArrayList;
import java.util.List;

public class DetectionJoueur {

    // public static Equipe detecterJoueurs(Mat hsvImage, Scalar lowerBound1, Scalar upperBound1, Scalar lowerBound2, Scalar upperBound2, String couleur) {
    //     Mat mask1 = new Mat();
    //     Core.inRange(hsvImage, lowerBound1, upperBound1, mask1);
    //     Equipe equipe=new Equipe();
    //     Mat mask = mask1.clone();
    
    //     if (lowerBound2 != null && upperBound2 != null) {
    //         Mat mask2 = new Mat();
    //         Core.inRange(hsvImage, lowerBound2, upperBound2, mask2);
    //         Core.add(mask1, mask2, mask);
    //     }
    
    //     List<MatOfPoint> contours = new ArrayList<>();
    //     Mat hierarchy = new Mat();
    //     Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
    
    //     List<Joueur> joueursDetectes = new ArrayList<>();
    //     for (MatOfPoint contour : contours) {
    //         if (Imgproc.contourArea(contour) > 200) {
    //             Rect rect = Imgproc.boundingRect(contour);
    //             Joueur joueur = new Joueur(new Point(rect.x + rect.width / 2, rect.y + rect.height / 2), couleur, rect);
    //             joueursDetectes.add(joueur);
    //         }
    //     }
    //     equipe.setJoueurs(joueursDetectes);
    //     return equipe;
    // }


    public static Equipe detecterJoueurs(Mat hsvImage, Scalar lowerBound1, Scalar upperBound1, Scalar lowerBound2, Scalar upperBound2, String couleur) {
        // Masque pour détecter la première plage de couleur
        Mat mask1 = new Mat();
        Core.inRange(hsvImage, lowerBound1, upperBound1, mask1);
    
        // Initialiser le masque final
        Mat mask = mask1.clone();
    
        // Si une seconde plage est fournie, fusionner les masques
        if (lowerBound2 != null && upperBound2 != null) {
            Mat mask2 = new Mat();
            Core.inRange(hsvImage, lowerBound2, upperBound2, mask2);
            Core.add(mask1, mask2, mask); // Fusion des deux masques
        }
    
        // Prétraitement pour améliorer la détection des petites formes
        Imgproc.GaussianBlur(mask, mask, new Size(5, 5), 0); // Lisser l'image pour réduire le bruit
        Imgproc.dilate(mask, mask, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3))); // Dilatation
    
        // Détection des contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
    
        // Liste des joueurs détectés
        List<Joueur> joueursDetectes = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
    
            // Réduction du seuil minimal pour inclure de plus petites formes
            if (area > 50) { // Ajuster ce seuil si nécessaire
                Rect rect = Imgproc.boundingRect(contour);
    
                // Créer un joueur à partir du contour détecté
                Joueur joueur = new Joueur(new Point(rect.x + rect.width / 2, rect.y + rect.height / 2), couleur, rect);
                joueursDetectes.add(joueur);
    
                // Optionnel : Dessiner le rectangle autour du joueur détecté
                Imgproc.rectangle(hsvImage, rect.tl(), rect.br(), new Scalar(255, 255, 255), 2);
            }
        }
    
        // Créer l'équipe et y assigner les joueurs détectés
        Equipe equipe = new Equipe();
        equipe.setJoueurs(joueursDetectes);
    
        return equipe;
    }
    
    
    
    public static Joueur gardienPresent(List<Joueur> joueurs){
        for (Joueur joueur : joueurs) {
            if(joueur.isGardien()){
                return joueur;
            }
        }
        return null;
    }
    

    public static Joueur detecterGardien(List<Joueur> joueurs, String couleur, double positionButGauche, double positionButDroit) {
        Joueur gardienDetecte = null;
        double distanceMin = Double.MAX_VALUE;
    
        
        Joueur gardienAdverse = null;
        for (Joueur joueur : joueurs) {
            if (!joueur.getCouleur().equals(couleur) && joueur.isGardien()) {
                gardienAdverse = joueur;
                break;
            }
        }
    
        for (Joueur joueur : joueurs) {
            if (joueur.getCouleur().equals(couleur)) {
                 
                double distanceGauche = Math.abs(joueur.getPosition().x - positionButGauche);
                double distanceDroit = Math.abs(joueur.getPosition().x - positionButDroit);
    
                 
                if (gardienAdverse != null) {
                    if (gardienAdverse.getPartie() == positionButGauche) {
                         
                        distanceGauche = Double.MAX_VALUE;
                    } else if (gardienAdverse.getPartie() == positionButDroit) {
                         
                        distanceDroit = Double.MAX_VALUE;
                    }
                }
    
                 
                double distance = Math.min(distanceGauche, distanceDroit);
                if (distance < distanceMin) {
                    distanceMin = distance;
                    gardienDetecte = joueur;
    
                     
                    if (distanceGauche < distanceDroit) {
                        joueur.setPartie(positionButGauche);
                    } else {
                        joueur.setPartie(positionButDroit);
                    }
                }
            }
        }
    
        
        if (gardienDetecte != null) {
           
            for (Joueur joueur : joueurs) {
                if (joueur.getCouleur().equals(couleur)) {
                    joueur.setGardien(false);
                }
            }
            gardienDetecte.setGardien(true);
        }
    
        return gardienDetecte;
    }
    
    
     
    
    
    

    public static Joueur detecterJoueurProcheDuBallon(Equipe joueursRouges, Equipe joueursBleus, Point positionBallon) {
        List<Joueur> tousLesJoueurs = new ArrayList<>();
        tousLesJoueurs.addAll(joueursRouges.getJoueurs());
        tousLesJoueurs.addAll(joueursBleus.getJoueurs());
    
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
                // double distance = calculerDistance(joueur.getPosition(), gardienOpposant.getPosition());
                double distance = Math.abs(joueur.getPosition().x-gardienOpposant.getPosition().x);

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
