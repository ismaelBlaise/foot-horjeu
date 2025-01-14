package fonction;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import util.Ballon;
import util.Joueur;

import java.util.ArrayList;
import java.util.List;

public class AnalyseImage {
    private final Mat image;
    private final List<ZoneHorsJeu> zonesHorsJeu = new ArrayList<>();

    public AnalyseImage(String imagePath) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        this.image = Imgcodecs.imread(imagePath);
        if (image.empty()) {
            throw new IllegalArgumentException("Image non chargée : " + imagePath);
        }
    }

    public List<Joueur> detectHorsJeu() {
        List<Joueur> resultats = new ArrayList<>();
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);
    
        // Détection des joueurs rouges avec deux plages HSV
        List<Joueur> joueursRouges = DetectionJoueur.detecterJoueurs(
                hsvImage,
                new Scalar(0, 100, 100), new Scalar(10, 255, 255), // Première plage de rouge
                new Scalar(170, 100, 100), new Scalar(180, 255, 255), // Deuxième plage de rouge
                "Rouge"
        );
        System.out.println(joueursRouges.size());
        // Détection des joueurs bleus
        List<Joueur> joueursBleus = DetectionJoueur.detecterJoueurs(
                hsvImage,
                new Scalar(100, 100, 100), new Scalar(140, 255, 255), // Plage de bleu
                null, null, // Pas de deuxième plage nécessaire pour le bleu
                "Bleu"
        );
    
        // Détection du ballon
        Ballon ballon = DetectionBallon.detecterBallon(hsvImage, new Scalar(0, 0, 0), new Scalar(180, 255, 50));
        if (ballon == null) {
            throw new IllegalArgumentException("Ballon non détecté.");
        }

        Imgproc.rectangle(image, ballon.getDimension().tl(), ballon.getDimension().br(),new Scalar(255, 255, 255), 2);
    
        // Analyse et dessin sur l'image
        Point positionBallon = ballon.getPosition();
        Joueur joueurProcheBallon = DetectionJoueur.detecterJoueurProcheDuBallon(joueursRouges, joueursBleus, positionBallon);
        boolean ballonDansLeCampDesRouges = positionBallon.x < image.width() / 2;
    
        resultats.addAll(AnalyseHorsJeu.analyserJoueurs(joueursRouges, positionBallon, "Rouge", joueursBleus, ballonDansLeCampDesRouges));
        resultats.addAll(AnalyseHorsJeu.analyserJoueurs(joueursBleus, positionBallon, "Bleu", joueursRouges, !ballonDansLeCampDesRouges));
    
        DessinerImage.dessinerSurImage(image, joueursRouges, positionBallon, "Rouge", joueurProcheBallon);
        DessinerImage.dessinerSurImage(image, joueursBleus, positionBallon, "Bleu", joueurProcheBallon);
        DessinerImage.dessinerZonesHorsJeu(image, zonesHorsJeu);
    
        Imgcodecs.imwrite("image_modifiee.png", image);
    
        return resultats;
    }
    
}
