package archive;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import fonction.ZoneHorsJeu;
import util.Ballon;
import util.Joueur;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AnalyseImageA {
    private final Mat image;
    private final List<ZoneHorsJeu> zonesHorsJeu = new ArrayList<>();

    public AnalyseImageA(String imagePath) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        this.image = Imgcodecs.imread(imagePath);
        if (image.empty()) {
            throw new IllegalArgumentException("Image non chargée : " + imagePath);
        }
    }

    public List<String> detectHorsJeu() {
        List<String> resultats = new ArrayList<>();
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);
    
        // Détection des joueurs et du ballon
        List<Joueur> joueursRouges = detecterJoueurs(hsvImage, new Scalar(0, 100, 100), new Scalar(10, 255, 255), "Rouge");
        List<Joueur> joueursBleus = detecterJoueurs(hsvImage, new Scalar(100, 100, 100), new Scalar(140, 255, 255), "Bleu");
        Ballon ballon = detecterBallon(hsvImage, new Scalar(0, 0, 0), new Scalar(180, 255, 50));
    
        if (ballon == null) {
            throw new IllegalArgumentException("Ballon non détecté.");
        }
    
        Point positionBallon = ballon.getPosition();
        // Identifier le joueur le plus proche du ballon
        Joueur joueurProcheBallon = detecterJoueurProcheDuBallon(joueursRouges, joueursBleus, positionBallon);
    
        // Analyse des joueurs rouges et bleus en fonction du camp du ballon
        resultats.addAll(analyserJoueurs(joueursRouges, positionBallon, "Rouge", joueursBleus));
        resultats.addAll(analyserJoueurs(joueursBleus, positionBallon, "Bleu", joueursRouges));
    
        // Dessiner sur l'image
        dessinerSurImage(joueursRouges, positionBallon, "Rouge", joueurProcheBallon);
        dessinerSurImage(joueursBleus, positionBallon, "Bleu", joueurProcheBallon);
    
        // Ajout des zones de hors-jeu à l'image
        // dessinerZonesHorsJeu();
    
        Imgcodecs.imwrite("image_modifiee.png", image);
    
        return resultats;
    }


    private void dessinerSurImage(List<Joueur> joueurs, Point positionBallon, String couleur, Joueur joueurProcheBallon) {
        Scalar couleurTexte = couleur.equals("Rouge") ? new Scalar(0, 0, 255) : new Scalar(255, 0, 0);
        Scalar couleurRect = couleur.equals("Rouge") ? new Scalar(0, 255, 255) : new Scalar(255, 255, 0);
    
        for (Joueur joueur : joueurs) {
            String statut = (joueur.getPosition().x > positionBallon.x) ? "EJ" : "HJ";
            Point position = joueur.getPosition();
    
            // Rectangle autour du joueur
            Imgproc.rectangle(image, joueur.getDimension().tl(), joueur.getDimension().br(), couleurRect, 2);
    
            // Texte indiquant le statut
            Imgproc.putText(image, statut, new Point(position.x - 20, position.y - 10),
                            Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, couleurTexte, 2);
    
            // Indicateur si c'est le joueur le plus proche du ballon
            if (joueur.equals(joueurProcheBallon)) {
                Imgproc.putText(image, "Leader", new Point(position.x - 20, position.y - 30),
                                Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(0, 255, 0), 2);
            }
        }
    }
    

    private List<String> analyserJoueurs(List<Joueur> joueurs, Point positionBallon, String couleur, List<Joueur> joueursOpposants) {
        List<String> resultats = new ArrayList<>();
    
        // Identifier le gardien de l'équipe actuelle
        Joueur gardien = detecterGardien(joueurs);
    
        // Identifier le dernier défenseur opposant (proche de son gardien)
        Joueur gardienOpposant = detecterGardien(joueursOpposants);
        Joueur dernierDefenseurOpposant = detecterDernierDefenseur(joueursOpposants, gardienOpposant);
    
        // Créer les zones de hors-jeu en fonction du camp du ballon
        boolean ballonDansLeCampDeRouges = positionBallon.x < image.width() / 2; // Ligne médiane pour définir le camp des rouges
    
        // Vérification si le ballon est dans le camp des rouges
        if (ballonDansLeCampDeRouges) {
            // Créer la zone de hors-jeu pour les bleus
            if (dernierDefenseurOpposant != null) {
                zonesHorsJeu.add(new ZoneHorsJeu(dernierDefenseurOpposant.getPosition(), 200)); // Zone après le dernier défenseur
            }
        } else {
            // Créer la zone de hors-jeu pour les rouges
            if (dernierDefenseurOpposant != null) {
                zonesHorsJeu.add(new ZoneHorsJeu(dernierDefenseurOpposant.getPosition(), 200)); // Zone après le dernier défenseur
            }
        }
    
        for (Joueur joueur : joueurs) {
            String statut;
    
            // Ne pas vérifier le gardien pour les hors-jeu
            if (joueur.equals(gardien)) {
                resultats.add("Joueur " + couleur + " en position " + joueur.getPosition() + " : Gardien");
                continue;
            }
    
            // Vérification de la position des joueurs en fonction du camp du ballon
            if (ballonDansLeCampDeRouges && joueur.getPosition().x > positionBallon.x && dernierDefenseurOpposant != null &&
                joueur.getPosition().x > dernierDefenseurOpposant.getPosition().x) {
                statut = "HJ"; // Hors-jeu pour les rouges
                zonesHorsJeu.add(new ZoneHorsJeu(joueur.getPosition(), 50)); // Zone autour du joueur hors-jeu
            } else if (!ballonDansLeCampDeRouges && joueur.getPosition().x < positionBallon.x && dernierDefenseurOpposant != null &&
                    joueur.getPosition().x < dernierDefenseurOpposant.getPosition().x) {
                statut = "HJ"; // Hors-jeu pour les bleus
                zonesHorsJeu.add(new ZoneHorsJeu(joueur.getPosition(), 50)); // Zone autour du joueur hors-jeu
            } else {
                statut = "EJ"; // En-jeu
            }
    
            resultats.add("Joueur " + couleur + " en position " + joueur.getPosition() + " : " + statut);
        }
        return resultats;
    }
    

    private void dessinerZonesHorsJeu() {
        for (ZoneHorsJeu zone : zonesHorsJeu) {
            // Dessiner des cercles ou des rectangles autour des zones de hors-jeu
            Imgproc.circle(image, zone.getCentre(), zone.getRayon(), new Scalar(0, 255, 255), 2);
        }
    }

    private Joueur detecterDernierDefenseur(List<Joueur> joueursOpposants, Joueur gardienOpposant) {
        if (gardienOpposant == null) {
            return null;
        }

        return joueursOpposants.stream()
                .filter(joueur -> !joueur.equals(gardienOpposant))
                .min(Comparator.comparingDouble(j -> calculerDistance(j.getPosition(), gardienOpposant.getPosition())))
                .orElse(null);
    }

    private Joueur detecterGardien(List<Joueur> joueurs) {
        return joueurs.stream()
                .min(Comparator.comparingInt(j -> (int) j.getPosition().x))
                .orElse(null);
    }

    private Joueur detecterJoueurProcheDuBallon(List<Joueur> joueursRouges, List<Joueur> joueursBleus, Point positionBallon) {
        List<Joueur> tousLesJoueurs = new ArrayList<>();
        tousLesJoueurs.addAll(joueursRouges);
        tousLesJoueurs.addAll(joueursBleus);

        return tousLesJoueurs.stream()
                .min(Comparator.comparingDouble(j -> calculerDistance(j.getPosition(), positionBallon)))
                .orElse(null);
    }

    private Ballon detecterBallon(Mat hsvImage, Scalar lowerBound, Scalar upperBound) {
        Mat mask = new Mat();
        Core.inRange(hsvImage, lowerBound, upperBound, mask);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        return contours.stream()
                .map(Imgproc::boundingRect)
                .map(rect -> new Ballon(new Point(rect.x + rect.width / 2, rect.y + rect.height / 2), rect))
                .findFirst()
                .orElse(null);
    }

    private List<Joueur> detecterJoueurs(Mat hsvImage, Scalar lowerBound, Scalar upperBound, String couleur) {
        Mat mask = new Mat();
        Core.inRange(hsvImage, lowerBound, upperBound, mask);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        return contours.stream()
                .map(Imgproc::boundingRect)
                .map(rect -> new Joueur(new Point(rect.x + rect.width / 2, rect.y + rect.height / 2), couleur, rect))
                .collect(Collectors.toList());
    }

    private double calculerDistance(Point p1, Point p2) {
        return Math.hypot(p1.x - p2.x, p1.y - p2.y);
    }
}


