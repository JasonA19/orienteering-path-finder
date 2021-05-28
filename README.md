# orienteering-path-finder
This assignment required me to generate optimal paths for orienteering during different seasons. In the sport (or "activity", depending on your level of fitness/competitiveness) of orienteering, you are given a map with terrain information, elevation contours, and a set or sequence of locations to visit ("controls"). The algorithm used to accomplish this wass an A* search, which uses heuristics to determine best paths.

Running the program, it should take 5 aruguments: terrain-image, elevation-file, path-file, season (summer,fall,winter,or spring), and output-image-filename.

The map contains different background colors that show the type of terrain. The first two inputs provided are a 395x500 simplified color-only terrain map and a text representation of the elevations within an area (500 lines of 400 double values, each representing an elevation in meters). As for the points you will need to go visit, those will come in a simple text file, two integers per line, representing the (x,y) pixel (origin at upper left) in the terrain map containing the location. This is the third input file.

As for the fourth input, this is a string indicating the season. In summer, things proceed normally. However, for fall, fallen leaves cover the trails, slowing you down. The same can be said for snow/ice in the winter, and mud in the spring. I handled these events separately, since they may affect the heuristic cost of a path.

Finally, the output-image-filename is just the terrain-image with the optimal path drawn on top. For simplicity sake, just make a copy of the terrain image, naming it differently of course when you run the program.
