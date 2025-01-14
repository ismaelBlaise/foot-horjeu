package fonction;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import util.Joueur;

import java.util.List;

public class DessinerImage {

    public static void dessinerSurImage(Mat image, List<Joueur> joueurs, Point positionBallon, String couleur, Joueur joueurProcheBallon) {
        Scalar couleurTexte = couleur.equals("Rouge") ? new Scalar(0, 0, 255) : new Scalar(255, 0, 0);
        Scalar couleurRect = couleur.equals("Rouge") ? new Scalar(0, 255, 255) : new Scalar(255, 255, 0);

        Imgproc.putText(image, "Ballon", new Point(positionBallon.x - 20, positionBallon.y - 10),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, couleurTexte, 2);

        for (Joueur joueur : joueurs) {
            String statut = joueur.getStatut();
            Point position = joueur.getPosition();
            if (DetectionJoueur.detecterGardien(joueurs, 0, 0).equals(joueur)) {
                statut = "Gardien";
            }

            Imgproc.rectangle(image, joueur.getDimension().tl(), joueur.getDimension().br(), couleurRect, 2);

            Imgproc.putText(image, statut, new Point(position.x - 20, position.y - 10),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, couleurTexte, 2);

            if (joueur.equals(joueurProcheBallon)) {
                Imgproc.putText(image, "Leader", new Point(position.x - 20, position.y - 30),
                        Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(0, 255, 0), 2);
            }
        }
    }

    public static void dessinerZonesHorsJeu(Mat image, List<ZoneHorsJeu> zonesHorsJeu) {
        for (ZoneHorsJeu zone : zonesHorsJeu) {
            Imgproc.circle(image, zone.getCentre(), zone.getRayon(), new Scalar(0, 255, 255), 2);
        }
    }
}
