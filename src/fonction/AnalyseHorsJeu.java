package fonction;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import util.Ballon;
import util.Equipe;
import util.Joueur;
import util.JoueurCouleur;
import util.Statut;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class AnalyseHorsJeu {

    public static List<Joueur> analyserJoueurs(Mat terrainImage,Equipe joueurs, Joueur joueurProcheBallon,Ballon ballon, Equipe joueursOpposants, int tailleTerrain) {
        List<Joueur> resultats = new ArrayList<>();
        List<Joueur> joueursAll = new ArrayList<>();
        joueursAll.addAll(joueurs.getJoueurs());
        joueursAll.addAll(joueursOpposants.getJoueurs());
        
        Joueur gardien = DetectionJoueur.detecterGardien(joueursAll, joueurProcheBallon.getCouleur(), 0, tailleTerrain);
        Joueur gardienOpposant = DetectionJoueur.detecterGardien(joueursAll, joueurProcheBallon.getCouleur().equals(JoueurCouleur.ROUGE) ? JoueurCouleur.BLEU : JoueurCouleur.ROUGE, 0, tailleTerrain);
        Joueur dernierDefenseurOpposant = DetectionJoueur.detecterDernierDefenseur(joueursOpposants.getJoueurs(), gardienOpposant);
        for (Joueur joueur : joueursOpposants.getJoueurs()) {
            joueur.setPartie(gardienOpposant.getPartie());
        }
        joueurs.setGardien(gardien);
        
        joueursOpposants.setGardien(gardienOpposant);
        joueursOpposants.setDernierDeffenseur(dernierDefenseurOpposant);


        if (dernierDefenseurOpposant != null) {
            tracerLigneDefenseur(terrainImage, dernierDefenseurOpposant);
        }

        for (Joueur joueur : joueurs.getJoueurs()) {
            
            if (joueur.equals(gardien) || joueur.equals(joueurProcheBallon)) {
                continue;
            }

            if (gardien.getPosition().x > gardienOpposant.getPosition().x) {
                analyserHorsJeuPourGardienAdvantage(resultats, joueur, ballon, dernierDefenseurOpposant);
            } else {
                analyserHorsJeuPourGardienAdverseAdvantage(resultats, joueur, ballon, dernierDefenseurOpposant);
            }
        }
        
        if (dernierDefenseurOpposant != null) {
            dernierDefenseurOpposant.setStatut(Statut.DEF);
        }
        
        return resultats;
    }

    private static void tracerLigneDefenseur(Mat terrainImage, Joueur dernierDefenseurOpposant) {
         
        if (terrainImage != null && dernierDefenseurOpposant != null) {
 
            if(dernierDefenseurOpposant.getPartie()==0){
                Point pointDebut = new Point(dernierDefenseurOpposant.getPosition().x-dernierDefenseurOpposant.getDimension().width/2, 0);   
                Point pointFin = new Point(dernierDefenseurOpposant.getPosition().x-dernierDefenseurOpposant.getDimension().width/2, terrainImage.rows());  

                
                Imgproc.line(terrainImage, pointDebut, pointFin, new Scalar(0, 0, 255), 2);  
            }
            else if(dernierDefenseurOpposant.getPartie()!=0){
                Point pointDebut = new Point(dernierDefenseurOpposant.getPosition().x+dernierDefenseurOpposant.getDimension().width/2, 0);   
                Point pointFin = new Point(dernierDefenseurOpposant.getPosition().x+dernierDefenseurOpposant.getDimension().width/2, terrainImage.rows());  

                
                Imgproc.line(terrainImage, pointDebut, pointFin, new Scalar(0, 0, 255), 2);  
            }
        }
    }

    // private static void analyserHorsJeuPourGardienAdvantage(List<Joueur> resultats, Joueur joueur, Point positionBallon, Joueur dernierDefenseurOpposant) {
    //     if (joueur.getPosition().x < positionBallon.x && dernierDefenseurOpposant != null &&
    //                joueur.getPosition().x < dernierDefenseurOpposant.getPosition().x) {
    //         joueur.setStatut(Statut.HJ);
    //         resultats.add(joueur);
    //     } else if (joueur.getPosition().x < positionBallon.x && dernierDefenseurOpposant != null &&
    //                joueur.getPosition().x > dernierDefenseurOpposant.getPosition().x) {
    //         joueur.setStatut(Statut.EJ);
    //         resultats.add(joueur);
    //     }
    // }


    private static void analyserHorsJeuPourGardienAdvantage(List<Joueur> resultats, Joueur joueur, Ballon ballon, Joueur dernierDefenseurOpposant) {
        if (dernierDefenseurOpposant == null || ballon == null) return;
    
        double ballonBordGauche = ballon.getPosition().x - (ballon.getDimension().width / 2.0);
        double ballonBordDroit = ballon.getPosition().x + (ballon.getDimension().width / 2.0);
        double defenseurX = dernierDefenseurOpposant.getBordDroit();  
    
        // Vérification des hors-jeu avec les dimensions du ballon
        if (joueur.getBordGauche() < ballonBordGauche && joueur.getBordGauche() < defenseurX) {
            joueur.setStatut(Statut.HJ);
            resultats.add(joueur);
        } 
        else if (joueur.getBordGauche() < ballonBordGauche && joueur.getBordDroit() > defenseurX) {
            joueur.setStatut(Statut.EJ);
            resultats.add(joueur);
        }
    }
    

    // private static void analyserHorsJeuPourGardienAdverseAdvantage(List<Joueur> resultats, Joueur joueur, Point positionBallon, Joueur dernierDefenseurOpposant) {
    //     if (joueur.getPosition().x > positionBallon.x && dernierDefenseurOpposant != null &&
    //         joueur.getPosition().x > dernierDefenseurOpposant.getPosition().x) {
    //         joueur.setStatut(Statut.HJ);
    //         resultats.add(joueur);
    //     } else if (joueur.getPosition().x > positionBallon.x && dernierDefenseurOpposant != null &&
    //                joueur.getPosition().x < dernierDefenseurOpposant.getPosition().x) {
    //         joueur.setStatut(Statut.EJ);
    //         resultats.add(joueur);
    //     } 
    // }


    private static void analyserHorsJeuPourGardienAdverseAdvantage(List<Joueur> resultats, Joueur joueur, Ballon ballon, Joueur dernierDefenseurOpposant) {
        if (dernierDefenseurOpposant == null || ballon == null) return;
    
        double ballonBordGauche = ballon.getPosition().x - (ballon.getDimension().width / 2.0);
        double ballonBordDroit = ballon.getPosition().x + (ballon.getDimension().width / 2.0);
        double defenseurX = dernierDefenseurOpposant.getBordGauche();  
    
        // Vérification des hors-jeu avec les dimensions du ballon
        if (joueur.getBordDroit() > ballonBordDroit && joueur.getBordDroit() > defenseurX) {
            joueur.setStatut(Statut.HJ);
            resultats.add(joueur);
        } 
        else if (joueur.getBordDroit() > ballonBordDroit && joueur.getBordGauche() < defenseurX) {
            joueur.setStatut(Statut.EJ);
            resultats.add(joueur);
        }
    }
}
