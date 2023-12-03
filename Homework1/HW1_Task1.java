import ij.IJ;
import ij.ImagePlus;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringTokenizer;

public class HW1_Task1 implements PlugIn {

    public void run(String var1) {
        processFiles();
    }
    private void processFiles() {
        try (OpenDialog crsDialog = new OpenDialog("Select your .crs file", "");
             OpenDialog stuDialog = new OpenDialog("Select your .stu file", "")) {

            String crsPath = crsDialog.getPath();
            String stuPath = stuDialog.getPath();

            int size = readLastValueFromCRSFile(crsPath);
            BinaryProcessor bp = createBinaryProcessor(size);
            readCoordinatesFromStuFile(stuPath, bp);

            displayImage(bp);
        } catch (IOException e) {
            IJ.error("Error reading file.");
        }
    }
    private static BinaryProcessor createBinaryProcessor(int size) {
        BinaryProcessor bp = new BinaryProcessor(new ByteProcessor(size, size));
        bp.invert();
        return bp;
    }
    private static void displayImage(BinaryProcessor bp) {
        ImagePlus imp = new ImagePlus("Course Conflicts", bp);
        imp.show();
    }
    private static int readLastValueFromCRSFile(String filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
            String lastLine = null;
            String line;
            while ((line = reader.readLine()) != null) {
                lastLine = line;
            }
            if (lastLine != null) {
                return Integer.parseInt(lastLine.split("\\s")[0]);
            }
        }
        return 30;
    }
    private static void readCoordinatesFromStuFile(String filePath, BinaryProcessor bp) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processStuFileLine(line, bp);
            }
        }
    }
    private static void processStuFileLine(String line, BinaryProcessor bp) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        int tokenCount = tokenizer.countTokens();

        if (tokenCount <= 1) {
            return;
        }
        while (tokenizer.hasMoreTokens()) {
            int x = Integer.parseInt(tokenizer.nextToken()) - 1;

            if (!tokenizer.hasMoreTokens()) {
                break;
            }
            int y = Integer.parseInt(tokenizer.nextToken()) - 1;
            bp.putPixel(x, y, 0);
        }
    }

    public static class Task1_1 implements PlugIn {

        @Override
        public void run(String arg) {
            processFiles();
        }
        private void processFiles() {
            try (
                    OpenDialog crsDialog = new OpenDialog("Select your .crs file", "");
                    OpenDialog stuDialog = new OpenDialog("Select your .stu file", "")
            ) {

                String crsPath = crsDialog.getPath();
                String stuPath = stuDialog.getPath();

                int N = readLastValueFromCRSFile(crsPath);
                BinaryProcessor bp = createBinaryProcessor(N);
                readCoordinatesFromStuFile(stuPath, bp);

                displayImage(bp);
            } catch (IOException e) {
                IJ.error("Error reading file.");
            }
        }
    }
}
