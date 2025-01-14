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
}
