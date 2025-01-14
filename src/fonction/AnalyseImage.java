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
    
         
        List<Joueur> joueursRouges = DetectionJoueur.detecterJoueurs(
                hsvImage,
                new Scalar(0, 100, 100), new Scalar(10, 255, 255),  
                new Scalar(170, 100, 100), new Scalar(180, 255, 255),  
                "Rouge"
        );
        
        List<Joueur> joueursBleus = DetectionJoueur.detecterJoueurs(
                hsvImage,
                new Scalar(100, 100, 100), new Scalar(140, 255, 255), 
                null, null,  
                "Bleu"
        );
    
         
        Ballon ballon = DetectionBallon.detecterBallon(hsvImage, new Scalar(0, 0, 0), new Scalar(180, 255, 50));
        if (ballon == null) {
            throw new IllegalArgumentException("Ballon non détecté.");
        }

        Imgproc.rectangle(image, ballon.getDimension().tl(), ballon.getDimension().br(),new Scalar(255, 255, 255), 2);
    
         
        Point positionBallon = ballon.getPosition();
        Joueur joueurProcheBallon = DetectionJoueur.detecterJoueurProcheDuBallon(joueursRouges, joueursBleus, positionBallon);
        boolean ballonDansLeCampDesRouges = positionBallon.x < image.width() / 2;
        if(joueurProcheBallon.getCouleur().equals("Bleu")){
            resultats.addAll(AnalyseHorsJeu.analyserJoueurs(joueursBleus, joueurProcheBallon, positionBallon, "Bleu", joueursRouges, !ballonDansLeCampDesRouges));
        }
        else{
            resultats.addAll(AnalyseHorsJeu.analyserJoueurs(joueursRouges, joueurProcheBallon, positionBallon, "Rouge", joueursBleus, ballonDansLeCampDesRouges));
        }
        
    
        DessinerImage.dessinerSurImage(image, joueursRouges, positionBallon, "Rouge", joueurProcheBallon);
        DessinerImage.dessinerSurImage(image, joueursBleus, positionBallon, "Bleu", joueurProcheBallon);
    
        Imgcodecs.imwrite("image_modifiee.png", image);
    
        return resultats;
    }
    
}
