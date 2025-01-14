package fonction;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import util.Ballon;
import util.Joueur;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class Analyse {
    private Mat image;
    private Point positionBallonPrecedente;

    public Analyse(String imagePath) {
        this.image = opencv_imgcodecs.imread(imagePath);
        if (image.empty()) {
            throw new IllegalArgumentException("Image non chargée : " + imagePath);
        }
        this.positionBallonPrecedente = null;  
    }

    public List<String> detectHorsJeu() {
        List<String> resultats = new ArrayList<>();
        Mat hsvImage = new Mat();
        opencv_imgproc.cvtColor(image, hsvImage, opencv_imgproc.COLOR_BGR2HSV);

        List<Joueur> joueursRouges = detecterJoueurs(hsvImage, new Scalar(0.0, 100.0, 100.0, 0), new Scalar(10.0, 255.0, 255.0, 0), "Rouge");
        List<Joueur> joueursBleus = detecterJoueurs(hsvImage, new Scalar(100.0, 100.0, 100.0, 0), new Scalar(140.0, 255.0, 255.0, 0), "Bleu");

        Ballon ballon = detecterBallon(hsvImage, new Scalar(0.0, 0.0, 0.0, 0), new Scalar(180.0, 255.0, 50.0, 0));
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

        opencv_imgcodecs.imwrite("image_modifiee.png", image);
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

        if (distanceMinRouge < distanceMinBleu) {
            return "Rouge: " + joueurRougeProche.getPosition();
        } else {
            return "Bleu: " + joueurBleuProche.getPosition();
        }
    }

    private double calculerDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x() - p2.x(), 2) + Math.pow(p1.y() - p2.y(), 2));
    }

    private String analyserPasse(Point anciennePosition, Point nouvellePosition, String joueurAvecBallon) {
        double distance = calculerDistance(anciennePosition, nouvellePosition);
        String direction = nouvellePosition.x() > anciennePosition.x() ? "vers l'avant" : "vers l'arrière";
        return "Passe de " + joueurAvecBallon + " : " + direction + " sur une distance de " + distance + " pixels.";
    }

    private List<String> analyserJoueurs(List<Joueur> joueurs, Point positionBallon, String couleur, List<Joueur> joueursOpposants) {
        List<String> resultats = new ArrayList<>();
        
        Joueur gardien = detecterGardien(joueurs);
        Joueur dernierDefenseurOpposant = detecterDernierDefenseur(joueursOpposants, couleur);

        for (Joueur joueur : joueurs) {
            if (joueur.equals(gardien)) {
                resultats.add("Joueur " + couleur + " en position " + joueur.getPosition() + " : Gardien");
                continue;
            }

            String statut = (joueur.getPosition().x() > dernierDefenseurOpposant.getPosition().x()) ? "HJ (hors-jeu)" : "EJ (en-jeu)";
            resultats.add("Joueur " + couleur + " en position " + joueur.getPosition() + " : " + statut);
        }
        
        return resultats;
    }

    private Joueur detecterGardien(List<Joueur> joueurs) {
        return joueurs.stream().min(Comparator.comparingInt(j -> j.getPosition().x())).orElse(null);
    }

    private Joueur detecterDernierDefenseur(List<Joueur> joueursOpposants, String couleur) {
        return joueursOpposants.stream().filter(j -> !j.equals(detecterGardien(joueursOpposants))).max(Comparator.comparingInt(j -> j.getPosition().x())).orElse(null);
    }

    private void dessinerSurImage(List<Joueur> joueurs, Point positionBallon, String couleur) {
        Scalar couleurTexte = (couleur.equals("Rouge")) ? new Scalar(0.0, 0.0, 255.0, 0) : new Scalar(255.0, 0.0, 0.0, 0);

        for (Joueur joueur : joueurs) {
            String statut = (joueur.getPosition().x() > positionBallon.x()) ? "EJ" : "HJ";
            opencv_imgproc.putText(image, statut, joueur.getPosition(), opencv_imgproc.FONT_HERSHEY_SIMPLEX, 1.0, couleurTexte, 2, 8, false);
        }
    }

    private List<Joueur> detecterJoueurs(Mat hsvImage, Scalar lowerBound, Scalar upperBound, String couleur) {
        Mat lowerMat = new Mat(1, 1, CvType.CV_8UC3, lowerBound);
        Mat upperMat = new Mat(1, 1, CvType.CV_8UC3, upperBound);
        Mat mask = new Mat();
        opencv_core.inRange(hsvImage, lowerMat, upperMat, mask);

        List<Joueur> joueurs = new ArrayList<>();
        MatVector contours = new MatVector();
        Mat hierarchy = new Mat();
        opencv_imgproc.findContours(mask, contours, hierarchy, opencv_imgproc.RETR_EXTERNAL, opencv_imgproc.CHAIN_APPROX_SIMPLE);

        for (int i = 0; i < contours.size(); i++) {
            Rect rect = opencv_imgproc.boundingRect(contours.get(i));
            Point position = new Point(rect.x() + rect.width() / 2, rect.y() + rect.height() / 2);
            joueurs.add(new Joueur(position, couleur, rect));
        }

        return joueurs;
    }

    private Ballon detecterBallon(Mat hsvImage, Scalar lowerBound, Scalar upperBound) {
        Mat lowerMat = new Mat(1, 1, CvType.CV_8UC3, lowerBound);
        Mat upperMat = new Mat(1, 1, CvType.CV_8UC3, upperBound);
        Mat mask = new Mat();
        opencv_core.inRange(hsvImage, lowerMat, upperMat, mask);

        MatVector contours = new MatVector();
        Mat hierarchy = new Mat();
        opencv_imgproc.findContours(mask, contours, hierarchy, opencv_imgproc.RETR_EXTERNAL, opencv_imgproc.CHAIN_APPROX_SIMPLE);

        if (contours.size() > 0) {
            Rect rect = opencv_imgproc.boundingRect(contours.get(0));
            Point position = new Point(rect.x() + rect.width() / 2, rect.y() + rect.height() / 2);
            return new Ballon(position, rect);
        }
        return null;
    }
}
