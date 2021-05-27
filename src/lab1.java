/**
 * @author: Chinonso Akujuobi
 * The main program for the orienteering event.
 */

import java.awt.Color;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class lab1 {

    /***
     * Loads colors of each pixel from image into 2d array.
     * @param image: terrain map
     * @param rows: height of image
     * @param cols: width of image
     * @return 2d Color array
     */
    public static Color[][] getColorsFromImg(BufferedImage image, int rows, int cols){
        //this 2D array is for setting the terrain of the pixels and later for when we have to change/set the pixels by
        //passing color[x][y].getRGB() into image.setRGB(x,y, color[x][y].getRGB())

        Color[][] colors = new Color[cols][rows]; //cols -> width (395) and rows -> height (500)

        //rows is the height and cols is the width, which is why we switch them. (image.getRGB() takes (width, height) only,
        //not (height, width). This is col major order.
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                colors[col][row] = new Color(image.getRGB(col, row));
            }
        }
        return colors;
    }

    /***
     * Reads elevations from file into 2d arraylist
     * @param elevationFileName: name of elevation file
     * @param cols: width of image
     * @return 2d elevations list
     */
    public static ArrayList<ArrayList<Double>> readElevationFile(String elevationFileName, int cols){
        ArrayList<ArrayList<Double>> elevations = new ArrayList<>();
        try {
            File file = new File(elevationFileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                String[] values = line.split("\\s+");
                ArrayList<Double> row = new ArrayList<>();
                for(int i = 0; i < cols; i++){
                    double value = Double.parseDouble(values[i]);
                    row.add(value);
                }
                elevations.add(row);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File couldn't be found.");
            e.printStackTrace();
        }
        //System.out.println(elevations.get(0).size());

        //System.out.println(elevations.get(499).get(394)); normally 499 would be i and 394 would be j to
        //correspond to [499][394] (which is the row-major convention) but the coordinates on map are col-major
        // ([394][499]). So here, technically, 499 is "j" and 394 is "i", and the colors grid is [j][i] and hence grabbing
        // a particular elevation would be done using elevations.get(j).get(i).
        return elevations;
    }

    /***
     * Retrieves pixels from image into 2d array
     * @param cols:width of image
     * @param rows: height of image
     * @param colors: colors array corresponding to each pixel
     * @param elevations: elevations list corresponding to each pixel
     * @return the 2d array of Pixels
     */
    public static Pixel[][] getPixels(int cols, int rows, Color[][] colors, ArrayList<ArrayList<Double>> elevations){
        Pixel[][] pixels = new Pixel[cols][rows];
        for (int col = 0; col < cols; col++) { //cols is 395
            for (int row = 0; row < rows; row++) { //rows is 500
                double elevation = elevations.get(row).get(col);
                Color color = colors[col][row];
                Pixel pixel = new Pixel(col, row, elevation, findTerrainType(color), null, 0, Double.POSITIVE_INFINITY, 0);
                pixels[col][row] = pixel;
            }
        }
        return pixels;
    }

    /**
     * Retrieves sequence of points to visit during orienteering event
     * @param pathFileName: name of path file
     * @param colors: 2d array of colors
     * @param elevations: 2d list of elevations
     * @return list of points to visit during A* search
     */
    public static List<Pixel> readPathFile(String pathFileName, Color[][] colors, ArrayList<ArrayList<Double>> elevations){
        List<Pixel> path = new ArrayList<>();
        try {
            File file = new File(pathFileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                String[] values = line.split("\\s+");
                int x = Integer.parseInt(values[0]);
                int y = Integer.parseInt(values[1]);
                double elevation = elevations.get(y).get(x);
                Color color = colors[x][y];
                Pixel pixel = new Pixel(x, y, elevation, findTerrainType(color), null, 0, Double.POSITIVE_INFINITY, 0);
                path.add(pixel);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File couldn't be found.");
            e.printStackTrace();
        }
        return path;
    }

    /**
     * Gets the terraintype of a Pixel based on its color
     * @param color: color of a Pixel
     * @return the terrain type of a pixel
     */
    public static Pixel.TerrainType findTerrainType(Color color){
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        if(red == 248 && green == 148 && blue == 18){
            return Pixel.TerrainType.OPEN_LAND;
        }
        if(red == 255 && green == 192 && blue == 0){
            return Pixel.TerrainType.ROUGH_MEADOW;
        }
        if(red == 255 && green == 255 && blue == 255){
            return Pixel.TerrainType.EASY_MOVEMENT_FOREST;
        }
        if(red == 2 && green == 208 && blue == 60){
            return Pixel.TerrainType.SLOW_RUN_FOREST;
        }
        if(red == 2 && green == 136 && blue == 40){
            return Pixel.TerrainType.WALK_FOREST;
        }
        if(red == 5 && green == 73 && blue == 24){
            return Pixel.TerrainType.IMPASSIBLE_VEGETATION;
        }
        if(red == 0 && green == 0 && blue == 255){
            return Pixel.TerrainType.LAKE;
        }
        if(red == 71 && green == 51 && blue == 3){
            return Pixel.TerrainType.PAVED_ROAD;
        }
        if(red == 0 && green == 0 && blue == 0){
            return Pixel.TerrainType.FOOTPATH;
        }
        if(red == 255 && green == 69 && blue == 0){
            return Pixel.TerrainType.LEAFY_FOOTPATH;
        }
        if(red == 124 && green == 252 && blue == 252){
            return Pixel.TerrainType.ICE;
        }
        if(red == 141 && green == 76 && blue == 0){
            return Pixel.TerrainType.MUDDY;
        }
        return Pixel.TerrainType.OUT_OF_BOUNDS;
    }

    /**
     * Gets the speed of a particular terrain type
     * @param type: the terrain type of a pixel
     * @return its speed
     */
    public static double getSpeedModifier(Pixel.TerrainType type){
        if(type == Pixel.TerrainType.OPEN_LAND){
            return 1.2;
        }
        if(type == Pixel.TerrainType.ROUGH_MEADOW){
            return 3.0;
        }
        if(type == Pixel.TerrainType.EASY_MOVEMENT_FOREST){
            return 2.4;
        }
        if(type == Pixel.TerrainType.SLOW_RUN_FOREST){
            return 5.0;
        }
        if(type == Pixel.TerrainType.WALK_FOREST){
            return 4.0;
        }
        if(type == Pixel.TerrainType.IMPASSIBLE_VEGETATION){
            return 7.0;
        }
        if(type == Pixel.TerrainType.LAKE){
            //return 6.0;
            return 9.0;
        }
        if(type == Pixel.TerrainType.PAVED_ROAD){
            return 1.0;
        }
        if(type == Pixel.TerrainType.FOOTPATH){
            return 0.8;
        }
        if(type == Pixel.TerrainType.LEAFY_FOOTPATH){
            return 1.5;
        }
        if(type == Pixel.TerrainType.ICE){
            //return 1.1;
            return 3.4;
        }
        if(type == Pixel.TerrainType.MUDDY){
            return 6.2;
        }
        return 8.0;
    }

    /**
     * Calculates the distance between one Pixel and another
     * @param p1 - pixel 1
     * @param p2 - pixel 2
     * @return the distance
     */
    public static double distance(Pixel p1, Pixel p2){
        return Math.sqrt( Math.pow((p1.getX() - p2.getX()), 2) +
                          Math.pow((p1.getY() - p2.getY()), 2) +
                        Math.pow((p1.getZ() - p2.getZ()), 2)
                );
    }

    /**
     * Calculates the heuristic value of a particular pixel
     * @param curr - the pixel we're calculating the heuristic for
     * @param goal - the destination
     * @return - H(n)
     */
    public static double calculateHn(Pixel curr, Pixel goal){
        double distance = distance(curr, goal);
        return distance * getSpeedModifier(Pixel.TerrainType.FOOTPATH); //lowest speed modifier
    }

    /**
     * Calcaulates the G(n) of a particualr pixel
     * @param parent- parent of pixel
     * @param curr - pixel we are calculating G(n) for
     * @return the G(n) cost
     */
    public static double calculateGn(Pixel parent, Pixel curr){
        double parentG = parent.getG();

        //moving horizontally
        if(parent.getY() == curr.getY()){
            return parentG + (10.29 * getSpeedModifier(curr.getType()));
        }
        //moving vertically
        else if(parent.getX() == curr.getX()){
            return parentG + (7.55 * getSpeedModifier(curr.getType()));
        }
        //moving diagonally
        double hypotenuse = Math.sqrt(Math.pow(10.29, 2) + Math.pow(7.55, 2));
        return parentG + (hypotenuse * getSpeedModifier(curr.getType()));

        //return parentG + (distance(parent, curr) * getSpeedModifier(curr.getType()));

    }

    /**
     * Calculates F(n) of a particular node
     * @param node - pixel we are finding f(n) for
     * @return the F(n)
     */
    public static double calculateFn(Pixel node){
        return node.getG() + node.getH();
    }

    /**
     * Gets all the pixels adjacent to a particular pixel
     * @param node - current pixel
     * @param pixels - pixels 2d array
     * @param cols - width of pixels array
     * @param rows - height of pixels array
     * @return the list of neighbors
     */
    public static List<Pixel> getNeighbors(Pixel node, Pixel[][] pixels, int cols, int rows){
        List<Pixel> neighbors = new ArrayList<>();
        int i = node.getX();
        int j = node.getY();
        if (i < cols && j < rows && i >= 1 && j >= 1) {
            neighbors.add(pixels[i-1][j-1]);
        }
        if (i < cols && j < rows-1 && i >= 1 && j >= 0) {
            neighbors.add(pixels[i-1][j+1]);
        }
        if (i < cols-1 && j < rows && i >= 0 && j >= 1) {
            neighbors.add(pixels[i+1][j-1]);
        }
        if (i < cols-1 && j < rows-1 && i >= 0 && j >= 0) {
            neighbors.add(pixels[i+1][j+1]);
        }
        if (i < cols && j < rows && i >= 1 && j >= 0) {
            neighbors.add(pixels[i-1][j]);
        }
        if (i < cols && j < rows && i >= 0 && j >= 1) {
            neighbors.add(pixels[i][j-1]);
        }
        if (i < cols && j < rows-1 && i >= 0 && j >= 0) {
            neighbors.add(pixels[i][j+1]);
        }
        if (i < cols-1 && j < rows && i >= 0 && j >= 0) {
            neighbors.add(pixels[i+1][j]);
        }
        return neighbors;
    }

    /**
     * Builds path from A* search
     * @param node the last node that was visited in the path.
     * @return a newly built path
     */
    public static List<Pixel> constructPath(Pixel node) {
        LinkedList<Pixel> path = new LinkedList<>();
        while(node != null){
            path.addFirst(node);
            node = node.getParent();
        }
        List<Pixel> list = new ArrayList<>(path);
        return list;
    }

    /**
     * The fucntion that carries out A* search
     * @param start: the start pixel
     * @param goal: the destination pixel
     * @param pixels: 2d array of pixels
     * @param cols: the width of board
     * @param rows: the height of board
     * @return the shortest path found during search
     */
    public static List<Pixel> aStar(Pixel start, Pixel goal, Pixel[][] pixels, int cols, int rows){
        int startX = start.getX();
        int startY = start.getY();

        int goalX = goal.getX();
        int goalY = goal.getY();

        Pixel startNode = pixels[startX][startY];
        Pixel goalNode = pixels[goalX][goalY];
        Queue<Pixel> openList = new PriorityQueue<>();
        Set<Pixel> explored = new HashSet<>();

        startNode.setH(distance(startNode, goalNode));
        startNode.setG(0.0);
        startNode.setF(startNode.getH());
        openList.add(startNode);
        List<Pixel> route = new ArrayList<>();

        while(!openList.isEmpty()){
            Pixel node = openList.remove();
            explored.add(node);
            if(node.equals(goalNode)){
                List<Pixel> list = constructPath(goalNode);
                route = new ArrayList<>(list);
                break;
            }
            List<Pixel> neighbors = getNeighbors(node, pixels, cols, rows);
            for(Pixel neighbor: neighbors){
                double tempG = calculateGn(node, neighbor);
                if(explored.contains(neighbor) && tempG >= neighbor.getG()){
                    continue;
                }
                if(!openList.contains(neighbor) || tempG < neighbor.getG()){
                    neighbor.setParent(node);
                    neighbor.setG(tempG);
                    neighbor.setH(calculateHn(neighbor, goal));
                    neighbor.setF(calculateFn(neighbor));

                    if(openList.contains(neighbor)) {
                        openList.remove(neighbor);
                    }

                    openList.add(neighbor);
                }
            }
        }

        for(Pixel pixel: explored){
            int x = pixel.getX();
            int y = pixel.getY();
            Pixel p = pixels[x][y];
            p.setParent(null);
            p.setH(0.0);
            p.setG(Double.POSITIVE_INFINITY);
            p.setF(0.0);
        }

        return route;
    }

    /**
     * Handles what occurs during the season of fall
     * @param pixels: 2d board of pixels
     * @param colors: 2d array of colors
     * @param cols: width of pixels array
     * @param rows: height of pixels array
     * @return list of pixels that are changed during fall
     */
    public static HashSet<Pixel> fall(Pixel[][] pixels, Color[][] colors, int cols, int rows){
        HashSet<Pixel> changedPixels = new HashSet<>();
        for(int col = 0; col < cols; col++) {
            for(int row = 0; row < rows; row++) {
                if(pixels[col][row].getType() == Pixel.TerrainType.FOOTPATH){
                    Pixel pixel = pixels[col][row];
                    List<Pixel> neighbors = getNeighbors(pixel, pixels, cols, rows);
                    for(Pixel neighbor: neighbors){
                        if(neighbor.getType() == Pixel.TerrainType.EASY_MOVEMENT_FOREST){
                            pixel.setType(Pixel.TerrainType.LEAFY_FOOTPATH);
                            colors[col][row] = new Color(255, 69, 0);
                            changedPixels.add(pixel);
                            break;
                        }
                    }
                }
            }
        }
        return changedPixels;
    }

    /**
     * Handles what occurs during the season of winter
     * @param pixels: 2d board of pixels
     * @param cols: width of pixels array
     * @param rows: height of pixels array
     * @return list of pixels that are changed during fall
     */
    public static HashSet<Pixel> winter(Pixel[][] pixels, int cols, int rows) {
        List<Pixel> waterEdgePixels = new ArrayList<>();
        for(int col = 0; col < cols; col++) {
            for(int row = 0; row < rows; row++) {
                if(pixels[col][row].getType() == Pixel.TerrainType.LAKE) {
                    Pixel pixel = pixels[col][row];
                    List<Pixel> neighbors = getNeighbors(pixel, pixels, cols, rows);
                    for(Pixel neighbor : neighbors) {
                        if(neighbor.getType() != Pixel.TerrainType.LAKE) { //get edges of water
                            waterEdgePixels.add(pixel);
                            break;
                        }
                    }
                }
            }
        }

        //BFS: we explore all neighbors before moving down a depth
        //Queue<Pair<Pixel, Integer>>  queue = new LinkedList<>();
        Queue<PixelDepthTuple> queue = new LinkedList<>();
        for(Pixel pixel: waterEdgePixels){
            PixelDepthTuple tuple = new PixelDepthTuple(pixel, 0);
            queue.add(tuple);
        }
        HashSet<Pixel> visited = new HashSet<>(waterEdgePixels);
        while(!queue.isEmpty()){
            PixelDepthTuple tuple = queue.remove();
            Pixel pixel = tuple.getPixel();
            int depth = tuple.getDepth();
            if(depth == 6){
                break;
            }
            List<Pixel> neighbors = getNeighbors(pixel, pixels, cols, rows);
            for(Pixel neighbor: neighbors){
                if(neighbor.getType() == Pixel.TerrainType.LAKE){
                    if(!visited.contains(neighbor)){
                        int neighborDepth = depth + 1;
                        PixelDepthTuple neighborTuple = new PixelDepthTuple(neighbor, neighborDepth);
                        queue.add(neighborTuple);
                        visited.add(neighbor);
                    }
                }
            }
        }
        return visited;
    }

    /**
     * Handles what occurs during the season of spring
     * @param pixels: 2d board of pixels
     * @param cols: width of pixels array
     * @param rows: height of pixels array
     * @return list of pixels that are changed during spring
     */
    public static HashSet<Pixel> spring(Pixel[][] pixels, int cols, int rows){
        HashSet<Pixel> shorePixels = new HashSet<>();
        HashMap<Pixel, Pixel> neighborMap = new HashMap<>();
        for(int col = 0; col < cols; col++) {
            for(int row = 0; row < rows; row++) {
                if(pixels[col][row].getType() == Pixel.TerrainType.LAKE) {
                    Pixel pixel = pixels[col][row];
                    List<Pixel> neighbors = getNeighbors(pixel, pixels, cols, rows);
                    for(Pixel neighbor : neighbors) {
                        if(neighbor.getType() != Pixel.TerrainType.LAKE) {//get shore surrounding water
                            if(!shorePixels.contains(neighbor)){
                                shorePixels.add(neighbor);
                                neighborMap.put(neighbor, pixel);
                            }

                        }
                    }
                }
            }
        }
        //BFS: we explore all neighbors before moving down a depth
        Queue<PixelDepthTuple> queue = new LinkedList<>();
        HashSet<Pixel> affected = new HashSet<>();
        for(Pixel pixel: shorePixels){
            PixelDepthTuple tuple = new PixelDepthTuple(pixel, 0);
            queue.add(tuple);
        }
        HashSet<Pixel> visited = new HashSet<>(shorePixels);
        while(!queue.isEmpty()) {
            PixelDepthTuple tuple = queue.remove();
            Pixel pixel = tuple.getPixel();
            int depth = tuple.getDepth();
            visited.add(pixel);
            Pixel predecessor = neighborMap.get(pixel);
            double elevation = pixel.getZ();
            double waterElevation = predecessor.getZ();
            double difference = Math.abs(elevation - waterElevation);
            if (depth == 15) {
                break;
            }
            if (difference <= 8.0) {
                affected.add(pixel);
            }

            List<Pixel> neighbors = getNeighbors(pixel, pixels, cols, rows);
            for (Pixel neighbor : neighbors) {
                if (neighbor.getType() != Pixel.TerrainType.LAKE) {
                    if (Math.abs(neighbor.getZ() - waterElevation) <= 8.0) {
                        affected.add(neighbor);
                    }
                    if (!visited.contains(neighbor)) {
                        int neighborDepth = depth + 1;
                        PixelDepthTuple neighborTuple = new PixelDepthTuple(neighbor, neighborDepth);
                        queue.add(neighborTuple);
                        visited.add(neighbor);
                    }
                    neighborMap.put(neighbor, predecessor);
                }
            }
        }
        return affected;
    }

    /**
     * Main function
     * @param args command line arguments
     */
    public static void main(String[] args){
        if(args.length != 5){
            System.out.println("Missing args");
            return;
        }
        String terrainImgName = args[0];
        String elevationFileName = args[1];
        String pathFileName = args[2];
        String season = args[3];
        String outputImgName = args[4];

        File inputFile = new File(terrainImgName);

        BufferedImage image = null;
        try {
            image = ImageIO.read(inputFile);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        assert image != null;

        int rows = image.getHeight();
        int cols = image.getWidth();

        Color[][] colors = getColorsFromImg(image, rows, cols);
        //Be careful. The colors grid is based on the map grid, which is 395 width x 500 height. In row-major order,
        // this is [500][395]. But we wrote them as [395][500] because the coordinates on the map are col-major. Keep
        //in mind though that the elevations still are row-major.
        ArrayList<ArrayList<Double>> elevations = readElevationFile(elevationFileName, cols);
        List<Pixel> path = readPathFile(pathFileName, colors, elevations);
        Pixel[][] pixels = getPixels(cols, rows, colors, elevations);
        HashSet<Pixel> affectedPixels = new HashSet<>();
        switch (season) {
            case "fall":
                fall(pixels, colors, cols, rows);
                break;
            case "winter": {
                HashSet<Pixel> affected = winter(pixels, cols, rows);
                affectedPixels.addAll(affected);
                for (Pixel pixel : affectedPixels) {
                    int x = pixel.getX();
                    int y = pixel.getY();
                    Pixel p = pixels[x][y];
                    p.setType(Pixel.TerrainType.ICE);
                    colors[x][y] = new Color(124, 252, 252);
                }
                break;
            }
            case "spring": {
                HashSet<Pixel> affected = spring(pixels, cols, rows);
                affectedPixels.addAll(affected);
                for (Pixel pixel : affectedPixels) {
                    int x = pixel.getX();
                    int y = pixel.getY();
                    Pixel p = pixels[x][y];
                    p.setType(Pixel.TerrainType.MUDDY);
                    colors[x][y] = new Color(141, 76, 0);
                }
                break;
            }
        }
        List<List<Pixel>> event = new ArrayList<>();
        double pathLength = 0.0;
        for(int i = 0; i < path.size() - 1; i++){
            List<Pixel> route = aStar(path.get(i), path.get(i+1), pixels, cols, rows);
            double length = distance(path.get(i), path.get(i+1));
            pathLength += length;
            if(route != null){
                event.add(route);
            }
        }
        System.out.println("Total path length in meters: " + pathLength);


        try {
            BufferedImage img = null;
            try {
                img = ImageIO.read(inputFile);
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }

            if(season.equals("winter")){
                for(Pixel pixel: affectedPixels){
                    int x = pixel.getX();
                    int y = pixel.getY();
                    assert img != null;
                    img.setRGB(x, y, colors[x][y].getRGB());
                }
            }
            else if(season.equals("spring")){
                for(Pixel pixel: affectedPixels){
                    int x = pixel.getX();
                    int y = pixel.getY();
                    assert img != null;
                    img.setRGB(x, y, colors[x][y].getRGB());
                }
            }

            //path drawing
            Color red = new Color(255, 0, 0);
            int rgb = red.getRGB();

            for (List<Pixel> pixelList : event) {
                for (Pixel pixel : pixelList) {
                    int x = pixel.getX();
                    int y = pixel.getY();
                    assert img != null;
                    img.setRGB(x, y, rgb);
                }
            }

            // retrieve image
            File outputFile = new File(outputImgName);
            assert img != null;
            ImageIO.write(img, "png", outputFile);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
