package fonction;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import util.Ballon;
import util.Equipe;
import util.Joueur;
import util.JoueurCouleur;

import java.util.ArrayList;
import java.util.Arrays;
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

    public List<Joueur> detectHorsJeu() throws Exception {
        List<Joueur> resultats = new ArrayList<>();
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_BGR2HSV);
    
         
        Equipe joueursRouges = DetectionJoueur.detecterJoueurs(
                hsvImage,
                new Scalar(0, 100, 100), new Scalar(10, 255, 255),  
                new Scalar(170, 100, 100), new Scalar(180, 255, 255),  
                JoueurCouleur.ROUGE
        );

        if(joueursRouges==null || joueursRouges.getJoueurs().size()<0){
            throw new Exception("Aucun joueurs rouges detecter");
        }

        Equipe joueursBleus = DetectionJoueur.detecterJoueurs(
                hsvImage,
                new Scalar(90, 50, 0), new Scalar(130, 255, 255), 
                null, null,  
                JoueurCouleur.BLEU
        );

        if(joueursRouges==null || joueursRouges.getJoueurs().size()<0){
            throw new Exception("Aucun joueurs rouges detecter");
        }
    
         
        Ballon ballon = DetectionBallon.detecterBallon(hsvImage, Arrays.asList(new Scalar(0, 0, 0), // Plage 1 - Noir
                                                                                                    // pur
                new Scalar(0, 0, 30), // Plage 2 - Noir avec un peu plus de luminosité
                new Scalar(0, 50, 0), // Plage 3 - Noir avec un peu plus de saturation
                new Scalar(0, 50, 30) // Plage 4 - Noir légèrement saturé et lumineux
        ), Arrays.asList(new Scalar(180, 50, 50), // Limite supérieure correspondante pour Plage 1
                new Scalar(180, 50, 80), // Limite supérieure correspondante pour Plage 2
                new Scalar(180, 100, 50), // Limite supérieure correspondante pour Plage 3
                new Scalar(180, 100, 80) // Limite supérieure correspondante pour Plage 4
        ));
         
        Imgproc.rectangle(image, ballon.getDimension().tl(), ballon.getDimension().br(),new Scalar(255, 255, 255), 2);
    
         
        Point positionBallon = ballon.getPosition();
        Joueur joueurProcheBallon = DetectionJoueur.detecterJoueurProcheDuBallon(joueursRouges, joueursBleus, positionBallon);
         
        if(joueurProcheBallon.getCouleur().equals(JoueurCouleur.BLEU)){
            resultats.addAll(AnalyseHorsJeu.analyserJoueurs(image, joueursBleus, joueurProcheBallon, ballon, joueursRouges,   image.cols()));
        }
        else{
            resultats.addAll(AnalyseHorsJeu.analyserJoueurs(image, joueursRouges, joueurProcheBallon, ballon,  joueursBleus, image.cols()));
        }
    
        DessinerImage.dessinerSurImage(image, joueursRouges.getJoueurs(), joueursBleus.getJoueurs(), image.cols(), positionBallon, JoueurCouleur.ROUGE, joueurProcheBallon);
        DessinerImage.dessinerSurImage(image, joueursBleus.getJoueurs(), joueursRouges.getJoueurs(),  image.cols(), positionBallon, JoueurCouleur.BLEU, joueurProcheBallon);
    
        Imgcodecs.imwrite("image_modifiee.png", image);
    
        return resultats;
    }
    
}
