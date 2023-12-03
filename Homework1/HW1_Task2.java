import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
public class HW1_Task2 implements PlugInFilter {

    @Override
    public int setup(String arg, ImagePlus imp) {
        return DOES_8G + DOES_RGB;
    }
    @Override
    public void run(ImageProcessor ip) {
        int width = ip.getWidth();
        int height = ip.getHeight();

        // horizontal
        int halfWidth = width / 2;
        ImageProcessor leftPanel = ip.crop();
        ImageProcessor rightPanel = ip.crop();

        leftPanel.setRoi(0, 0, halfWidth, height);
        rightPanel.setRoi(halfWidth, 0, width - halfWidth, height);

        ip.insert(leftPanel.crop(), halfWidth, 0);
        ip.insert(rightPanel.crop(), 0, 0);

        // vertical
        int halfHeight = height / 2;
        ImageProcessor topPanel = ip.crop();
        ImageProcessor bottomPanel = ip.crop();

        topPanel.setRoi(0, 0, width, halfHeight);
        bottomPanel.setRoi(0, halfHeight, width, height - halfHeight);

        ip.insert(topPanel.crop(), 0, halfHeight);
        ip.insert(bottomPanel.crop(), 0, 0);
    }
}
