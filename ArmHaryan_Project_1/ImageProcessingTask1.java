import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageProcessingTask1 {

    public static void main(String[] args) {
        String imagePath = "C:\\Users\\User1\\Desktop\\ImageProcessing-ArmHaryan\\ArmHaryan_Project_1\\H047\\OOP.MT.170317.H047_1.1.jpg";
        Mat originalImage = Imgcodecs.imread(imagePath);

        // Analyze color distribution
        MatOfFloat ranges = new MatOfFloat(0, 256);
        MatOfInt histSize = new MatOfInt(256);
        boolean accumulate = false;

        Mat hist = new Mat();
        Imgproc.calcHist(
                (List<Mat>) originalImage,
                new MatOfInt(0, 1, 2),
                new Mat(),
                hist,
                histSize,
                ranges,
                accumulate
        );
        Mat imageWithoutText = removeText(originalImage);
        Mat imageWithoutMarks = removeMarks(imageWithoutText);
        Mat handwritingImage = extractHandwriting(imageWithoutMarks);

        String dateStr = new SimpleDateFormat("ddMMyy").format(new Date());
        String outputFilename = "CRS.TST." + dateStr + ".C001_p1_bin.png";

        cropAndSave(handwritingImage, outputFilename);
    }

    private static Mat removeText(Mat image) {
        Mat grayImage = new Mat();
        Mat result = new Mat();
        Mat thresh = new Mat();

        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(grayImage, thresh, 200, 255, Imgproc.THRESH_BINARY_INV);
        Core.bitwise_and(image, image, result, thresh);

        return result;
    }

    private static Mat removeMarks(Mat image) {
        Scalar lowerRed = new Scalar(0, 0, 100);
        Scalar upperRed = new Scalar(100, 100, 255);
        Mat mask = new Mat();
        Mat result = new Mat();
        Core.inRange(image, lowerRed, upperRed, mask);
        Core.bitwise_and(image, image, result, mask);
        return result;
    }

    private static Mat extractHandwriting(Mat image) {
        Mat edges = new Mat();
        Mat result = new Mat();
        Imgproc.Canny(image, edges, 30, 100);
        Core.bitwise_and(image, image, result, edges);
        return result;
    }

    private static void cropAndSave(Mat image, String filename) {
        Mat grayImage = new Mat();
        Mat thresh = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(grayImage, thresh, 127, 255, Imgproc.THRESH_BINARY);
        List<MatOfPoint> contours =  new ArrayList<>();
        Imgproc.findContours(thresh, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        contours.sort((contour1, contour2) -> Double.compare(Imgproc.contourArea(contour2), Imgproc.contourArea(contour1)));

        int numContoursToConsider = 1;
        for (int i = 0; i < Math.min(numContoursToConsider, contours.size()); i++) {
            Mat contour = contours.get(i);
            double area = Imgproc.contourArea(contour);

            if (area > 100) {
                Rect boundingRect = Imgproc.boundingRect(contour);
                Mat croppedImage = new Mat(image, boundingRect);
                Mat binaryImage = new Mat();
                Imgproc.cvtColor(croppedImage, binaryImage, Imgproc.COLOR_BGR2GRAY);
                Imgproc.threshold(binaryImage, binaryImage, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

                Imgcodecs.imwrite(filename, binaryImage);
                break;
            }
        }
    }
}

