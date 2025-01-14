package fonction;

import org.opencv.core.Point;

import util.Joueur;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AnalyseHorsJeu {

    public static List<String> analyserJoueurs(List<Joueur> joueurs, Point positionBallon, String couleur, List<Joueur> joueursOpposants, boolean ballonDansLeCamp) {
        List<String> resultats = new ArrayList<>();
        Joueur gardien = DetectionJoueur.detecterGardien(joueurs, 0, 0);

        Joueur gardienOpposant = DetectionJoueur.detecterGardien(joueursOpposants, 0, 0);
        Joueur dernierDefenseurOpposant = detecterDernierDefenseur(joueursOpposants, gardienOpposant);
        dernierDefenseurOpposant.setStatut("Dernier def");
        for (Joueur joueur : joueurs) {
            String statut;

            if (joueur.equals(gardien)) {
                resultats.add("Joueur " + couleur + " en position " + joueur.getPosition() + " : Gardien");
                continue;
            }

            if (ballonDansLeCamp && joueur.getPosition().x > positionBallon.x && dernierDefenseurOpposant != null &&
                    joueur.getPosition().x > dernierDefenseurOpposant.getPosition().x) {
                joueur.setStatut("HJ");
                statut = "HJ";
            } else if (!ballonDansLeCamp && joueur.getPosition().x < positionBallon.x && dernierDefenseurOpposant != null &&
                    joueur.getPosition().x < dernierDefenseurOpposant.getPosition().x) {
                joueur.setStatut("HJ");
                statut = "HJ";
            } else {
                joueur.setStatut("EJ");
                statut = "EJ";
            }

            resultats.add("Joueur " + couleur + " en position " + joueur.getPosition() + " : " + statut);
        }
        return resultats;
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
