public class Pixel implements Comparable<Pixel>{
    /**
     * Pixel representation.
     */

    public enum TerrainType{
        OPEN_LAND,
        ROUGH_MEADOW,
        EASY_MOVEMENT_FOREST,
        SLOW_RUN_FOREST,
        WALK_FOREST,
        IMPASSIBLE_VEGETATION,
        LAKE,
        PAVED_ROAD,
        FOOTPATH,
        LEAFY_FOOTPATH,
        ICE,
        MUDDY,
        OUT_OF_BOUNDS
    }

    private TerrainType type;

    private Pixel parent;

    private double h;

    private double g;

    private double f;

    private int x;

    private int y;

    private double z;

    public Pixel(int x, int y, double z, TerrainType type, Pixel parent, double h, double g, double f){
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.parent = parent;
        this.h = h;
        this.g = g;
        this.f = f;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public TerrainType getType() {
        return type;
    }

    public void setType(TerrainType type) {
        this.type = type;
    }

    public Pixel getParent() {
        return parent;
    }

    public void setParent(Pixel parent) {
        this.parent = parent;
    }

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    @Override
    public int compareTo(Pixel p) {
        return (int)this.f - (int)p.f;
    }





}
