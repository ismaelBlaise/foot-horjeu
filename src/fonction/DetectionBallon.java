package fonction;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import util.Ballon;

import java.util.ArrayList;
import java.util.List;

public class DetectionBallon {


    // public static Ballon detecterBallon(Mat hsvImage, Scalar lowerBound, Scalar upperBound) {
    //     Mat mask = new Mat();
    //     Core.inRange(hsvImage, lowerBound, upperBound, mask);
    
    //     List<MatOfPoint> contours = new ArrayList<>();
    //     Mat hierarchy = new Mat();
    //     Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
    
         
    //     Ballon ballonDetecte = null;
    
         
    //     for (MatOfPoint contour : contours) {
    //         Rect rect = Imgproc.boundingRect(contour);
            
    //         ballonDetecte = new Ballon(new Point(rect.x + rect.width / 2, rect.y + rect.height / 2), rect);
    //         break;   
    //     }
    
    //     return ballonDetecte;  
    // }

    public static Ballon detecterBallon(Mat hsvImage, List<Scalar> lowerBounds, List<Scalar> upperBounds) {
        Mat mask = new Mat(hsvImage.size(), CvType.CV_8UC1, new Scalar(0));

        for (int i = 0; i < lowerBounds.size(); i++) {
            Mat tempMask = new Mat();
            Core.inRange(hsvImage, lowerBounds.get(i), upperBounds.get(i), tempMask);
            if (!tempMask.empty()) {
                Core.add(mask, tempMask, mask);
            }
        }

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        Ballon ballonDetecte = null;
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            ballonDetecte = new Ballon(new Point(rect.x + rect.width / 2, rect.y + rect.height / 2), rect);
            break;
        }

        return ballonDetecte;
    }
    
    //Mety
    // public static Ballon detecterBallon(Mat hsvImage, Scalar lowerBound, Scalar upperBound) {
    //     Mat mask = new Mat();
    //     Core.inRange(hsvImage, lowerBound, upperBound, mask);
    
    //     List<MatOfPoint> contours = new ArrayList<>();
    //     Mat hierarchy = new Mat();
    //     Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
    
         
    //     Ballon ballonDetecte = null;
    
         
    //     for (MatOfPoint contour : contours) {
    //         Rect rect = Imgproc.boundingRect(contour);
            
    //         ballonDetecte = new Ballon(new Point(rect.x + rect.width / 2, rect.y + rect.height / 2), rect);
    //         Imgproc.rectangle(hsvImage, ballonDetecte.getDimension().tl(), ballonDetecte.getDimension().br(),new Scalar(255, 255, 255), 2);
    //         break;   
    //     }
    
    //     return ballonDetecte;  
    // }



    // public static Ballon detecterBallon(Mat hsvImage, Scalar lowerBound, Scalar upperBound) {
    //     // Créer un masque basé sur les limites HSV
    //     Mat mask = new Mat();
    //     Core.inRange(hsvImage, lowerBound, upperBound, mask);
    
    //     // Appliquer un flou pour réduire le bruit
    //     Imgproc.GaussianBlur(mask, mask, new Size(5, 5), 0);
    
    //     // Détecter les contours dans le masque
    //     List<MatOfPoint> contours = new ArrayList<>();
    //     Mat hierarchy = new Mat();
    //     Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
    
    //     Ballon ballonDetecte = null;
    
    //     // Parcourir les contours détectés
    //     for (MatOfPoint contour : contours) {
    //         double area = Imgproc.contourArea(contour);
    
    //         // Filtrer par aire minimale
    //         if (area > 200) { // Ajuster ce seuil si nécessaire
    //             Rect rect = Imgproc.boundingRect(contour);
    
    //             // Vérifier la compacité pour détecter des formes circulaires
    //             double aspectRatio = (double) rect.width / rect.height;
    //             if (aspectRatio > 0.8 && aspectRatio < 1.2) { // Approximativement carré ou circulaire
    
    //                 // Vérifier le remplissage noir (proportion de pixels noirs dans le masque)
    //                 Mat roi = mask.submat(rect);
    //                 double blackPixels = Core.countNonZero(roi);
    //                 double totalPixels = rect.width * rect.height;
    //                 double blackProportion = blackPixels / totalPixels;
    
    //                 if (blackProportion > 0.7) { // Si l'objet est majoritairement rempli
    //                     // Créer un objet Ballon pour la détection
    //                     ballonDetecte = new Ballon(new Point(rect.x + rect.width / 2, rect.y + rect.height / 2), rect);
    
    //                     // Dessiner un rectangle autour du ballon détecté
    //                     Imgproc.rectangle(hsvImage, rect.tl(), rect.br(), new Scalar(255, 255, 255), 2);
    
    //                     break; // On s'arrête à la première détection valide
    //                 }
    //             }
    //         }
    //     }
    
    //     return ballonDetecte;
    // }
    


    // public static Ballon detecterBallon(Mat hsvImage, Scalar lowerBound, Scalar upperBound) {
    //     Mat mask = new Mat();
    //     Core.inRange(hsvImage, lowerBound, upperBound, mask);

    //     // Trouver les contours
    //     List<MatOfPoint> contours = new ArrayList<>();
    //     Mat hierarchy = new Mat();
    //     Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

    //     Ballon ballonDetecte = null;

    //     // Parcourir les contours pour trouver un objet correspondant à un ballon
    //     for (MatOfPoint contour : contours) {
    //         // Calculer le rectangle englobant
    //         Rect rect = Imgproc.boundingRect(contour);

    //         // Vérifier les critères pour qu'il soit un ballon
    //         double area = Imgproc.contourArea(contour);
    //         double rectArea = rect.width * rect.height;

    //         // Vérifier que l'objet est plein et suffisamment grand
    //         if (area / rectArea > 0.8 && area > 500) { // Aire significative et forme pleine
    //             ballonDetecte = new Ballon(new Point(rect.x + rect.width / 2, rect.y + rect.height / 2), rect);
    //             Imgproc.rectangle(hsvImage, ballonDetecte.getDimension().tl(), ballonDetecte.getDimension().br(), new Scalar(255, 255, 255), 2);
    //             break; // On arrête après le premier ballon détecté
    //         }
    //     }

    //     return ballonDetecte;
    // }
    
}
