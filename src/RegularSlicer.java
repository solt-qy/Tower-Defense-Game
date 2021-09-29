

import bagel.util.Point;
import java.util.List;

/**
 * The most common type of slicer
 * Code Partially Reused from Project 1 Solution
 */
public class RegularSlicer extends Slicer  {

    private static final String IMAGE_FILE = "res/images/slicer.png";
    public static final double SPEED = 2;
    public static final int HEALTH = 1;
    public static final int REWARD = 2;
    public static final int PENALTY = 1;
    private static final int NUMBER_OF_CHILD = 0;


    /**
     * Creates a new Slicer
     *
     * @param polyline The polyline that the slicer must traverse (must have at least 1 point)
     */
    public RegularSlicer(List<Point> polyline) {
        super(polyline.get(0), IMAGE_FILE, polyline,SPEED,HEALTH,REWARD,PENALTY,NUMBER_OF_CHILD);
    }

    /**
     * create a new child slicer (regular)
     * spawns at the location given instead of the start of the polyline
     * @param point spawning location
     * @param polyline the traversing line
     * @param targetPointIndex the point in the polyline that the slicer needs to approach
     */
    public RegularSlicer(Point point, List<Point> polyline, int targetPointIndex) {
        super(point, IMAGE_FILE, polyline, targetPointIndex, SPEED, HEALTH, REWARD, PENALTY, NUMBER_OF_CHILD);
    }

    /**
     * create a copy of the given slicer
     * @param regularSlicer the slicer that needs to be copied
     */
    public RegularSlicer(Slicer regularSlicer){
        super(regularSlicer.getCenter(), IMAGE_FILE, regularSlicer.getPolyline(), regularSlicer.getTargetPointIndex(),
                SPEED,HEALTH,REWARD,PENALTY, NUMBER_OF_CHILD );
    }

}
