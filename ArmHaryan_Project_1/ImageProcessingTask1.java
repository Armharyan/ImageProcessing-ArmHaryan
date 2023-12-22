import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageProcessingTask1 {

    public static void main(String[] args) {
        // Load the image
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

        // Remove text
        Mat imageWithoutText = removeText(originalImage);

        // Remove marks
        Mat imageWithoutMarks = removeMarks(imageWithoutText);

        // Extract handwriting
        Mat handwritingImage = extractHandwriting(imageWithoutMarks);

        // Generate output filename
        String dateStr = new SimpleDateFormat("ddMMyy").format(new Date());
        String outputFilename = "CRS.TST." + dateStr + ".C001_p1_bin.png";

        // Crop handwriting and save as binary image
        cropAndSave(handwritingImage, outputFilename);
    }

    private static Mat removeText(Mat image) {
        // Implement a method to remove text (you might use a combination of techniques like thresholding, morphology, etc.)
        // Example: Using adaptive thresholding
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        Mat thresh = new Mat();
        Imgproc.threshold(grayImage, thresh, 200, 255, Imgproc.THRESH_BINARY_INV);

        Mat result = new Mat();
        Core.bitwise_and(image, image, result, thresh);

        return result;
    }

    private static Mat removeMarks(Mat image) {
        // Implement a method to remove marks (you might use color-based segmentation)
        // Example: Using inRange for red shades
        Scalar lowerRed = new Scalar(0, 0, 100);
        Scalar upperRed = new Scalar(100, 100, 255);
        Mat mask = new Mat();
        Core.inRange(image, lowerRed, upperRed, mask);

        Mat result = new Mat();
        Core.bitwise_and(image, image, result, mask);

        return result;
    }

    private static Mat extractHandwriting(Mat image) {
        // Implement a method to extract handwriting (you might use edge detection, contour analysis, etc.)
        // Example: Using Canny edge detection
        Mat edges = new Mat();
        Imgproc.Canny(image, edges, 30, 100);

        Mat result = new Mat();
        Core.bitwise_and(image, image, result, edges);

        return result;
    }

    private static void cropAndSave(Mat image, String filename) {
        // Convert to grayscale
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        // Thresholding
        Mat thresh = new Mat();
        Imgproc.threshold(grayImage, thresh, 127, 255, Imgproc.THRESH_BINARY);

        // Find contours
        List<MatOfPoint> contours =  new ArrayList<>();
        Imgproc.findContours(thresh, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Sort contours by area
        contours.sort((contour1, contour2) -> Double.compare(Imgproc.contourArea(contour2), Imgproc.contourArea(contour1)));

        // Choose the top contour (you may adjust the number based on your needs)
        int numContoursToConsider = 1;
        for (int i = 0; i < Math.min(numContoursToConsider, contours.size()); i++) {
            Mat contour = contours.get(i);
            double area = Imgproc.contourArea(contour);

            // Filter out small contours
            if (area > 100) {  // Adjust the threshold as needed
                Rect boundingRect = Imgproc.boundingRect(contour);
                Mat croppedImage = new Mat(image, boundingRect);

                // Convert to binary format
                Mat binaryImage = new Mat();
                Imgproc.cvtColor(croppedImage, binaryImage, Imgproc.COLOR_BGR2GRAY);
                Imgproc.threshold(binaryImage, binaryImage, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

                // Save the cropped binary image
                Imgcodecs.imwrite(filename, binaryImage);
                break;  // Only process the top contour
            }
        }
    }
}

