package fonction;

import org.opencv.core.Point;

import util.Joueur;

import java.util.ArrayList;
import java.util.List;

public class AnalyseHorsJeu {

    public static List<Joueur> analyserJoueurs(List<Joueur> joueurs,Joueur joueurProcheBallon, Point positionBallon, String couleur, List<Joueur> joueursOpposants, boolean ballonDansLeCamp) {
        List<Joueur> resultats = new ArrayList<>();
        Joueur gardien = DetectionJoueur.detecterGardien(joueurs, 600, 100);
        
        Joueur gardienOpposant = DetectionJoueur.detecterGardien(joueursOpposants, 100, 100);
        Joueur dernierDefenseurOpposant = DetectionJoueur.detecterDernierDefenseur(joueursOpposants, gardienOpposant);
        for (Joueur joueur : joueurs) {
            
           
            if (joueur.equals(gardien) || joueur.equals(joueurProcheBallon)) {
                
                continue;
            }

            if (ballonDansLeCamp && joueur.getPosition().x > positionBallon.x && dernierDefenseurOpposant != null &&
                    joueur.getPosition().x > dernierDefenseurOpposant.getPosition().x) {
                joueur.setStatut("HJ");
                
                resultats.add(joueur);

            } 
            else if(ballonDansLeCamp && joueur.getPosition().x > positionBallon.x && dernierDefenseurOpposant != null &&
                joueur.getPosition().x < dernierDefenseurOpposant.getPosition().x){
                joueur.setStatut("EJ");
                
                resultats.add(joueur);

            }else if (!ballonDansLeCamp && joueur.getPosition().x < positionBallon.x && dernierDefenseurOpposant != null &&
                    joueur.getPosition().x < dernierDefenseurOpposant.getPosition().x) {
                joueur.setStatut("HJ");
                
                resultats.add(joueur);

            } else if(!ballonDansLeCamp && joueur.getPosition().x < positionBallon.x && dernierDefenseurOpposant != null &&
            joueur.getPosition().x > dernierDefenseurOpposant.getPosition().x){
                joueur.setStatut("EJ");
                
                resultats.add(joueur);

            }

           
        }
        dernierDefenseurOpposant.setStatut("def");
        return resultats;
    }

    
}
