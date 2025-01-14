package fonction;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import util.Joueur;

import java.util.List;

public class DessinerImage {

    public static void dessinerSurImage(Mat image, List<Joueur> joueurs, Point positionBallon, String couleur, Joueur joueurProcheBallon) {
        Scalar couleurTexte = couleur.equals("Rouge") ? new Scalar(0, 0, 255) : new Scalar(255, 0, 0);
        Scalar couleurRect = couleur.equals("Rouge") ? new Scalar(0, 255, 255) : new Scalar(255, 255, 0);

         
                    
        for (Joueur joueur : joueurs) {
            String statut = joueur.getStatut();
            Point position = joueur.getPosition();
            if (DetectionJoueur.detecterGardien(joueurs, 0, 0).equals(joueur)) {
                statut = "Goal";
            }

            Imgproc.rectangle(image, joueur.getDimension().tl(), joueur.getDimension().br(), couleurRect, 2);

            Imgproc.putText(image, statut, new Point(position.x - 15, position.y - 15),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, couleurTexte, 2);

            if (joueur.equals(joueurProcheBallon)) {
                        Imgproc.rectangle(image, joueur.getDimension().tl(), joueur.getDimension().br(),  new Scalar(0, 255, 0), 2);
            }
        }
    }

}
