import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ImageProcessingTask2 {

    public static void main(String[] args) {
        String imagePath = "C:\\Users\\User1\\Desktop\\ImageProcessing-ArmHaryan\\ArmHaryan_Project_1\\H047\\OOP.MT.170317.H047_1.1.jpg";
        Mat image = Imgcodecs.imread(imagePath);
        Mat grayImage = new Mat();
        Mat edges = new Mat();

        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(grayImage, grayImage, new Size(5, 5), 0);
        Imgproc.Canny(grayImage, edges, 50, 150);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        double maxArea = -1;
        int maxAreaIdx = -1;
        for (int i = 0; i < contours.size(); i++) {
            double area = Imgproc.contourArea(contours.get(i));
            if (area > maxArea) {
                maxArea = area;
                maxAreaIdx = i;
            }
        }

        Rect boundingBox = Imgproc.boundingRect(contours.get(maxAreaIdx));
        Imgproc.rectangle(image, boundingBox.tl(), boundingBox.br(), new Scalar(0, 255, 0), 2);

        MatOfPoint2f approxCurve = new MatOfPoint2f();
        MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(maxAreaIdx).toArray());
        double epsilon = 0.02 * Imgproc.arcLength(contour2f, true);
        Imgproc.approxPolyDP(contour2f, approxCurve, epsilon, true);
        Point[] points = approxCurve.toArray();
        double angle = Math.atan2(points[1].y - points[0].y, points[1].x - points[0].x);

        System.out.println("Orientation Angle: " + Math.toDegrees(angle));
        System.out.println("Bounding Box Size: " + boundingBox.size());

        Imgcodecs.imwrite("C:\\Users\\User1\\Desktop\\ImageProcessing-ArmHaryan\\ArmHaryan_Project_1\\H047\\OOP.MT.170317.H047_1.1.jpg", image);
    }
}

