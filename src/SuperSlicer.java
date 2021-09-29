import bagel.util.Point;

import java.util.List;

public class SuperSlicer extends Slicer {

    private static final int NUMBER_OF_CHILD = 2;
    private static final String IMAGE_FILE = "res/images/superslicer.png";

    public static final double SPEED = (3.0/4)*RegularSlicer.SPEED;
    public static final int HEALTH = RegularSlicer.HEALTH;
    public static final int REWARD = 15;
    public static final int PENALTY = RegularSlicer.PENALTY*NUMBER_OF_CHILD;


    public SuperSlicer(List<Point> polyline) {
        super(polyline.get(0), IMAGE_FILE, polyline, SPEED,HEALTH,REWARD,PENALTY,NUMBER_OF_CHILD);
    }

    /**
     * create a new child slicer (super)
     * spawns at the location given instead of the start of the polyline
     * @param point spawning location
     * @param polyline the traversing line
     * @param targetPointIndex the point in the polyline that the slicer needs to approach
     */
    public SuperSlicer(Point point, List<Point> polyline, int targetPointIndex) {
        super(point, IMAGE_FILE, polyline, targetPointIndex, SPEED, HEALTH, REWARD, PENALTY, NUMBER_OF_CHILD);
    }

    /**
     * create a copy of the given slicer
     * @param superSlicer the slicer that needs to be copied
     */
    public SuperSlicer(Slicer superSlicer){
        super(superSlicer.getCenter(), IMAGE_FILE, superSlicer.getPolyline(), superSlicer.getTargetPointIndex(),
                SPEED,HEALTH,REWARD,PENALTY, NUMBER_OF_CHILD );
    }

    /**
     * set the child slicer to be regular slicer and use the
     * parent class method to load the child slicers
     */
    @Override
    public void spawnChild(List<Point> polyline) {
       super.setChildSlicer(new RegularSlicer(getRect().centre(),polyline,getTargetPointIndex()));
       super.spawnChild(polyline);
    }
}
