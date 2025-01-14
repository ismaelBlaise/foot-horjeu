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

    public List<String> detectHorsJeu() {
        List<String> resultats = new ArrayList<>();
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);

        // Détection des joueurs et du ballon
        List<Joueur> joueursRouges = DetectionJoueur.detecterJoueurs(hsvImage, new Scalar(0, 100, 100), new Scalar(237, 28,36), "Rouge");
        List<Joueur> joueursBleus = DetectionJoueur.detecterJoueurs(hsvImage, new Scalar(100, 100, 100), new Scalar(140, 255, 255), "Bleu");
        Ballon ballon = DetectionBallon.detecterBallon(hsvImage, new Scalar(0, 0, 0), new Scalar(180, 255, 50));
        System.out.println(joueursRouges.size());
        if (ballon == null) {
            throw new IllegalArgumentException("Ballon non détecté.");
        }
        // Imgproc.putText(image, "Ballon", new Point(ballon.getPosition().x - 20, ballon.getPosition().y - 10),
        //             Imgproc.FONT_HERSHEY_SIMPLEX, 0.7,  new Scalar(0, 0, 255), 2);

        Point positionBallon = ballon.getPosition();
        Joueur joueurProcheBallon = DetectionJoueur.detecterJoueurProcheDuBallon(joueursRouges, joueursBleus, positionBallon);
        // joueurProcheBallon.setStatut("Dernier def");
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
