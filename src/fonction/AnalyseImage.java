package fonction;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import util.Ballon;
import util.Joueur;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AnalyseImage {
    private Mat image;
    private Point positionBallonPrecedente;

    public AnalyseImage(String imagePath) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        this.image = Imgcodecs.imread(imagePath);
        if (image.empty()) {
            throw new IllegalArgumentException("Image non chargée : " + imagePath);
        }
        this.positionBallonPrecedente = null;
    }

    public List<String> detectHorsJeu() {
        List<String> resultats = new ArrayList<>();
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);

        List<Joueur> joueursRouges = detecterJoueurs(hsvImage, new Scalar(0.0, 100.0, 100.0), new Scalar(10.0, 255.0, 255.0), "Rouge");
        List<Joueur> joueursBleus = detecterJoueurs(hsvImage, new Scalar(100.0, 100.0, 100.0), new Scalar(140.0, 255.0, 255.0), "Bleu");

        Ballon ballon = detecterBallon(hsvImage, new Scalar(0.0, 0.0, 0.0), new Scalar(180.0, 255.0, 50.0));
        if (ballon == null) {
            throw new IllegalArgumentException("Ballon non détecté.");
        }

        String joueurAvecBallon = detecterJoueurAvecBallon(joueursRouges, joueursBleus, ballon);
        Point positionBallon = ballon.getPosition();
        resultats.addAll(analyserJoueurs(joueursRouges, positionBallon, "Rouge", joueursBleus));
        resultats.addAll(analyserJoueurs(joueursBleus, positionBallon, "Bleu", joueursRouges));

        if (positionBallonPrecedente != null) {
            resultats.add(analyserPasse(positionBallonPrecedente, positionBallon, joueurAvecBallon));
        }

        dessinerSurImage(joueursRouges, positionBallon, "Rouge");
        dessinerSurImage(joueursBleus, positionBallon, "Bleu");

        Imgcodecs.imwrite("image_modifiee.png", image);
        positionBallonPrecedente = positionBallon;

        return resultats;
    }

    private String detecterJoueurAvecBallon(List<Joueur> joueursRouges, List<Joueur> joueursBleus, Ballon ballon) {
        double distanceMinRouge = Double.MAX_VALUE;
        double distanceMinBleu = Double.MAX_VALUE;
        Joueur joueurRougeProche = null;
        Joueur joueurBleuProche = null;

        for (Joueur joueur : joueursRouges) {
            double distance = calculerDistance(joueur.getPosition(), ballon.getPosition());
            if (distance < distanceMinRouge) {
                distanceMinRouge = distance;
                joueurRougeProche = joueur;
            }
        }

        for (Joueur joueur : joueursBleus) {
            double distance = calculerDistance(joueur.getPosition(), ballon.getPosition());
            if (distance < distanceMinBleu) {
                distanceMinBleu = distance;
                joueurBleuProche = joueur;
            }
        }

        return (distanceMinRouge < distanceMinBleu) ? "Rouge: " + joueurRougeProche.getPosition()
                : "Bleu: " + joueurBleuProche.getPosition();
    }

    private double calculerDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    private String analyserPasse(Point anciennePosition, Point nouvellePosition, String joueurAvecBallon) {
        double distance = calculerDistance(anciennePosition, nouvellePosition);
        String direction = (nouvellePosition.x > anciennePosition.x) ? "vers l'avant" : "vers l'arrière";
        return "Passe de " + joueurAvecBallon + " : " + direction + " sur une distance de " + distance + " pixels.";
    }

    private List<String> analyserJoueurs(List<Joueur> joueurs, Point positionBallon, String couleur, List<Joueur> joueursOpposants) {
        List<String> resultats = new ArrayList<>();
    
        Joueur gardien = detecterGardien(joueurs);
        Joueur dernierDefenseurOpposant = detecterDernierDefenseur(joueursOpposants);
    
        for (Joueur joueur : joueurs) {
            if (joueur.equals(gardien)) {
                resultats.add("Joueur " + couleur + " en position " + joueur.getPosition() + " : Gardien");
                continue;
            }
    
            if (dernierDefenseurOpposant == null) {
                resultats.add("Impossible de déterminer le hors-jeu pour le joueur " + couleur + " en position " + joueur.getPosition() + " : Pas de défenseur détecté.");
                continue;
            }
    
            String statut = (joueur.getPosition().x > dernierDefenseurOpposant.getPosition().x) ? "HJ (hors-jeu)" : "EJ (en-jeu)";
            resultats.add("Joueur " + couleur + " en position " + joueur.getPosition() + " : " + statut);
        }
    
        return resultats;
    }
    

    private Joueur detecterGardien(List<Joueur> joueurs) {
        return joueurs.stream().min(Comparator.comparingInt(j -> (int) j.getPosition().x)).orElse(null);
    }

    private Joueur detecterDernierDefenseur(List<Joueur> joueursOpposants) {
        return joueursOpposants.stream().max(Comparator.comparingInt(j -> (int) j.getPosition().x)).orElse(null);
    }

    private void dessinerSurImage(List<Joueur> joueurs, Point positionBallon, String couleur) {
        Scalar couleurTexte = (couleur.equals("Rouge")) ? new Scalar(0, 0, 255) : new Scalar(255, 0, 0);

        for (Joueur joueur : joueurs) {
            String statut = (joueur.getPosition().x > positionBallon.x) ? "EJ" : "HJ";
            Imgproc.putText(image, statut, joueur.getPosition(), Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, couleurTexte, 2);
        }
    }

    private List<Joueur> detecterJoueurs(Mat hsvImage, Scalar lowerBound, Scalar upperBound, String couleur) {
        Mat mask = new Mat();
        Core.inRange(hsvImage, lowerBound, upperBound, mask);

        List<Joueur> joueurs = new ArrayList<>();
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            Point position = new Point(rect.x + rect.width / 2, rect.y + rect.height / 2);
            joueurs.add(new Joueur(position, couleur, rect));
        }

        return joueurs;
    }

    private Ballon detecterBallon(Mat hsvImage, Scalar lowerBound, Scalar upperBound) {
        Mat mask = new Mat();
        Core.inRange(hsvImage, lowerBound, upperBound, mask);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        if (!contours.isEmpty()) {
            Rect rect = Imgproc.boundingRect(contours.get(0));
            Point position = new Point(rect.x + rect.width / 2, rect.y + rect.height / 2);
            return new Ballon(position, rect);
        }
        return null;
    }
}
 