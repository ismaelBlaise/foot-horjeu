package fonction;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import util.Joueur;
import util.JoueurCouleur;

import java.util.ArrayList;
import java.util.List;

public class AnalyseHorsJeu {

    public static List<Joueur> analyserJoueurs(Mat terrainImage,List<Joueur> joueurs, Joueur joueurProcheBallon, Point positionBallon, List<Joueur> joueursOpposants, int tailleTerrain) {
        List<Joueur> resultats = new ArrayList<>();
        List<Joueur> joueursAll = new ArrayList<>();
        joueursAll.addAll(joueurs);
        joueursAll.addAll(joueursOpposants);
        
        Joueur gardien = DetectionJoueur.detecterGardien(joueursAll, joueurProcheBallon.getCouleur(), 0, tailleTerrain);
        Joueur gardienOpposant = DetectionJoueur.detecterGardien(joueursAll, joueurProcheBallon.getCouleur().equals(JoueurCouleur.ROUGE) ? JoueurCouleur.BLEU : JoueurCouleur.ROUGE, 0, tailleTerrain);
        Joueur dernierDefenseurOpposant = DetectionJoueur.detecterDernierDefenseur(joueursOpposants, gardienOpposant);

        if (dernierDefenseurOpposant != null) {
            tracerLigneDefenseur(terrainImage, dernierDefenseurOpposant);
        }

        for (Joueur joueur : joueurs) {
            
            if (joueur.equals(gardien) || joueur.equals(joueurProcheBallon)) {
                continue;
            }

            if (gardien.getPosition().x > gardienOpposant.getPosition().x) {
                analyserHorsJeuPourGardienAdvantage(resultats, joueur, positionBallon, dernierDefenseurOpposant);
            } else {
                analyserHorsJeuPourGardienAdverseAdvantage(resultats, joueur, positionBallon, dernierDefenseurOpposant);
            }
        }
        
        if (dernierDefenseurOpposant != null) {
            dernierDefenseurOpposant.setStatut("def");
        }
        
        return resultats;
    }

    private static void tracerLigneDefenseur(Mat terrainImage, Joueur dernierDefenseurOpposant) {
         
        if (terrainImage != null && dernierDefenseurOpposant != null) {
 
            Point pointDebut = new Point(dernierDefenseurOpposant.getPosition().x, 0);   
            Point pointFin = new Point(dernierDefenseurOpposant.getPosition().x, terrainImage.rows());  

             
            Imgproc.line(terrainImage, pointDebut, pointFin, new Scalar(0, 0, 255), 2);  
        }
    }

    private static void analyserHorsJeuPourGardienAdvantage(List<Joueur> resultats, Joueur joueur, Point positionBallon, Joueur dernierDefenseurOpposant) {
        if (joueur.getPosition().x < positionBallon.x && dernierDefenseurOpposant != null &&
                   joueur.getPosition().x < dernierDefenseurOpposant.getPosition().x) {
            joueur.setStatut("HJ");
            resultats.add(joueur);
        } else if (joueur.getPosition().x < positionBallon.x && dernierDefenseurOpposant != null &&
                   joueur.getPosition().x > dernierDefenseurOpposant.getPosition().x) {
            joueur.setStatut("EJ");
            resultats.add(joueur);
        }
    }


    private static void analyserHorsJeuPourGardienAdverseAdvantage(List<Joueur> resultats, Joueur joueur, Point positionBallon, Joueur dernierDefenseurOpposant) {
        if (joueur.getPosition().x > positionBallon.x && dernierDefenseurOpposant != null &&
            joueur.getPosition().x > dernierDefenseurOpposant.getPosition().x) {
            joueur.setStatut("HJ");
            resultats.add(joueur);
        } else if (joueur.getPosition().x > positionBallon.x && dernierDefenseurOpposant != null &&
                   joueur.getPosition().x < dernierDefenseurOpposant.getPosition().x) {
            joueur.setStatut("EJ");
            resultats.add(joueur);
        } 
    }
}
