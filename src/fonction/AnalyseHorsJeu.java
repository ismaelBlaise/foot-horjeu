package fonction;

import org.opencv.core.Point;
import util.Joueur;
import util.JoueurCouleur;

import java.util.ArrayList;
import java.util.List;

public class AnalyseHorsJeu {

    public static List<Joueur> analyserJoueurs(List<Joueur> joueurs, Joueur joueurProcheBallon, Point positionBallon, String couleur, List<Joueur> joueursOpposants, int tailleTerrain) {
        List<Joueur> resultats = new ArrayList<>();
        List<Joueur> joueursAll = new ArrayList<>();
        joueursAll.addAll(joueurs);
        joueursAll.addAll(joueursOpposants);
        
        Joueur gardien = DetectionJoueur.detecterGardien(joueursAll, couleur, 0, tailleTerrain);
        Joueur gardienOpposant = DetectionJoueur.detecterGardien(joueursAll, couleur.equals(JoueurCouleur.ROUGE) ? JoueurCouleur.BLEU : JoueurCouleur.ROUGE, 0, tailleTerrain);
        Joueur dernierDefenseurOpposant = DetectionJoueur.detecterDernierDefenseur(joueursOpposants, gardienOpposant);

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
