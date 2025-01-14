package fonction;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import util.Ballon;
import util.But;
import util.Joueur;
import util.JoueurCouleur;

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
                JoueurCouleur.ROUGE
        );
        
        List<Joueur> joueursBleus = DetectionJoueur.detecterJoueurs(
                hsvImage,
                new Scalar(100, 100, 100), new Scalar(140, 255, 255), 
                null, null,  
                JoueurCouleur.BLEU
        );
    
         
        Ballon ballon = DetectionBallon.detecterBallon(hsvImage, new Scalar(0, 0, 0), new Scalar(180, 255, 50));
        if (ballon == null) {
            throw new IllegalArgumentException("Ballon non détecté.");
        }
        System.out.println(image.cols());
        Imgproc.rectangle(image, ballon.getDimension().tl(), ballon.getDimension().br(),new Scalar(255, 255, 255), 2);
    
         
        Point positionBallon = ballon.getPosition();
        Joueur joueurProcheBallon = DetectionJoueur.detecterJoueurProcheDuBallon(joueursRouges, joueursBleus, positionBallon);
        // boolean ballonDansLeCampDesRouges = positionBallon.x < image.width() / 2;
        if(joueurProcheBallon.getCouleur().equals(JoueurCouleur.BLEU)){
            resultats.addAll(AnalyseHorsJeu.analyserJoueurs(joueursBleus, joueurProcheBallon, positionBallon, JoueurCouleur.BLEU, joueursRouges,   image.cols()));
        }
        else{
            resultats.addAll(AnalyseHorsJeu.analyserJoueurs(joueursRouges, joueurProcheBallon, positionBallon, JoueurCouleur.ROUGE, joueursBleus, image.cols()));
        }
        

        But[] buts = DetectionBut.detecterBut(image);  

        
        for (But but : buts) {
            if (but != null) {
                System.out.println("AA");
                Imgproc.rectangle(image, but.getDimension().tl(), but.getDimension().br(), new Scalar(0, 255, 255), 2);
            }
        }
    
        DessinerImage.dessinerSurImage(image, joueursRouges, joueursBleus, image.cols(), positionBallon, JoueurCouleur.ROUGE, joueurProcheBallon);
        DessinerImage.dessinerSurImage(image, joueursBleus, joueursRouges,  image.cols(), positionBallon, JoueurCouleur.BLEU, joueurProcheBallon);
    
        Imgcodecs.imwrite("image_modifiee.png", image);
    
        return resultats;
    }
    
}
