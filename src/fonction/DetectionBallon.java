package fonction;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import util.Ballon;

import java.util.ArrayList;
import java.util.List;

public class DetectionBallon {


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
