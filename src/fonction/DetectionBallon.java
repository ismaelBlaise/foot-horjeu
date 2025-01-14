package fonction;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import util.Ballon;

import java.util.ArrayList;
import java.util.List;

public class DetectionBallon {

    // public static Ballon detecterBallon(Mat hsvImage, Scalar lowerBound, Scalar upperBound) {
    //     // Créer un masque pour la couleur cible (par exemple, la couleur du ballon)
    //     Mat mask = new Mat();
    //     Core.inRange(hsvImage, lowerBound, upperBound, mask);

    //     // Appliquer un flou pour réduire le bruit (flou médian pour mieux traiter les petits bruits)
    //     Imgproc.medianBlur(mask, mask, 15);

    //     // Liste pour stocker les contours trouvés
    //     List<MatOfPoint> contours = new ArrayList<>();
    //     Mat hierarchy = new Mat();
    //     Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

    //     // Vérifier chaque contour et détecter la forme circulaire
    //     for (MatOfPoint contour : contours) {
    //         // Calculer le périmètre du contour
    //         double perimeter = Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true);

    //         // Approximer le contour pour obtenir un polygone
    //         MatOfPoint2f approx = new MatOfPoint2f();
    //         Imgproc.approxPolyDP(new MatOfPoint2f(contour.toArray()), approx, 0.02 * perimeter, true);

    //         // Vérifier si le contour est circulaire (approximativement un cercle)
    //         if (approx.total() > 5) {  // Un cercle a plus de 5 points après approximation
    //             // Calculer le centre et le rayon du cercle
    //             Point center = new Point();
    //             float[] radius = new float[1];

    //             // Convertir le contour en MatOfPoint2f avant d'utiliser minEnclosingCircle
    //             MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
    //             Imgproc.minEnclosingCircle(contour2f, center, radius);

    //             // Vérifier la circularité et la forme approximativement ronde
    //             if (isCloseToCircle(contour, radius[0])) {
    //                 // Créer un objet Ballon avec le centre et le rayon détectés
    //                 Rect rect = Imgproc.boundingRect(contour); // Rectangle englobant
    //                 // Vérification additionnelle : taille du rectangle pour éliminer les faux positifs
    //                 if (rect.width > 20 && rect.height > 20 && rect.width < 100) {
    //                     // Vérification du rapport de forme pour éliminer les objets non circulaires
    //                     double aspectRatio = (double) rect.width / rect.height;
    //                     if (aspectRatio > 0.8 && aspectRatio < 1.2) {
    //                         // Filtrer les contours situés sur les bords du terrain ou près des buts
    //                         if (!isInBordures(rect)) {
    //                             return new Ballon(center, rect);
    //                         }
    //                     }
    //                 }
    //             }
    //         }
    //     }

    //     // Retourner null si aucun ballon n'est trouvé
    //     return null;
    // }

    public static Ballon detecterBallon(Mat hsvImage, Scalar lowerBound, Scalar upperBound) {
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

    // Fonction pour vérifier si un contour est suffisamment proche d'un cercle
    private static boolean isCloseToCircle(MatOfPoint contour, float radius) {
        // Calculer la zone du contour
        double contourArea = Imgproc.contourArea(contour);
        double expectedArea = Math.PI * Math.pow(radius, 2);

        // Calculer le périmètre du contour
        double perimeter = Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true);

        // Calculer le rapport de circularité : plus proche de 1 est un cercle parfait
        double circularity = (4 * Math.PI * contourArea) / (Math.pow(perimeter, 2));

        // Si la différence entre la zone et la zone attendue est faible et le rapport de circularité est proche de 1
        return Math.abs(contourArea - expectedArea) < 0.2 * expectedArea && circularity > 0.6; // Réduit la contrainte de circularité pour plus de flexibilité
    }

    // Fonction pour filtrer les contours situés dans les bords ou près des buts
    private static boolean isInBordures(Rect rect) {
        int width = rect.width;
        int height = rect.height;

        // Supposons que les bords et les zones des buts sont dans des zones spécifiques (par exemple, en bas ou en haut de l'image)
        // À adapter selon la taille de l'image et la position des buts
        int imageWidth = 640;  // Largeur de l'image
        int imageHeight = 480; // Hauteur de l'image

        // Définir des marges autour des bords (par exemple, 10% de la largeur/hauteur de l'image)
        int margin = 50;

        // Si le rectangle est proche du bord de l'image ou dans une zone considérée comme un but, on le filtre
        if (rect.x < margin || rect.x + width > imageWidth - margin || rect.y < margin || rect.y + height > imageHeight - margin) {
            return true; // C'est un contour près des bords
        }

        return false; // Le contour n'est pas dans une zone indésirable
    }
}
