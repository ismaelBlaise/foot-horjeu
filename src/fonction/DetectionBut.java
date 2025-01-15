package fonction;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import util.But;

import java.util.ArrayList;
import java.util.List;

public class DetectionBut {

    public static But[] detecterBut(Mat image) {
        But[] buts = new But[2];   
        Mat gris = new Mat();
        Imgproc.cvtColor(image, gris, Imgproc.COLOR_BGR2GRAY);

        Mat flou = new Mat();
        Imgproc.GaussianBlur(gris, flou, new Size(5, 5), 0);

        Mat bords = new Mat();
        Imgproc.Canny(flou, bords, 100, 200);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(bords, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        int index = 0;   
        for (MatOfPoint contour : contours) {
            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
            MatOfPoint2f approx = new MatOfPoint2f();
            double epsilon = 0.02 * Imgproc.arcLength(contour2f, true);
            Imgproc.approxPolyDP(contour2f, approx, epsilon, true);

            if (approx.toArray().length == 4) {
                Rect rect = Imgproc.boundingRect(new MatOfPoint(approx.toArray()));

                if (rect.width < image.cols() && rect.height < image.rows()) {
                    if (estBordureBlanche(image, rect)) {
                        System.out.println("AA");
                        buts[index] = new But(new Point(rect.x + rect.width / 2, rect.y + rect.height / 2), rect);
                        index++;   
                        if (index >= 2) {
                            break;   
                        }
                    }
                }
            }
        }
        return buts;   
    }

    private static boolean estBordureBlanche(Mat image, Rect rect) {
        Mat roi = image.submat(rect);

        Mat roiGris = new Mat();
        Imgproc.cvtColor(roi, roiGris, Imgproc.COLOR_BGR2GRAY);

        for (int i = 0; i < roiGris.rows(); i++) {
            for (int j = 0; j < roiGris.cols(); j++) {
                if (roiGris.get(i, j)[0] < 200) {  
                    return false;  
                }
            }
        }
        return true; 
    }
}
