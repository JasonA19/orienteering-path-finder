public class PixelDepthTuple {
    /**
     * Custom class for a tuple that holds a Pixel and an integer.
     */

    private Pixel pixel;

    private int depth;

    public PixelDepthTuple(Pixel pixel, int depth){
        this.pixel = pixel;
        this.depth = depth;
    }

    public Pixel getPixel() {
        return pixel;
    }

    public int getDepth() {
        return depth;
    }

}
