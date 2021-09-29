import bagel.util.Point;

import java.util.List;

public class MegaSlicer extends Slicer{

    private static final int NUMBER_OF_CHILD = 2;
    private static final String IMAGE_FILE = "res/images/megaslicer.png";
    public static final double SPEED = SuperSlicer.SPEED;
    public static final int HEALTH = SuperSlicer.HEALTH*2;
    public static final int REWARD = 10;
    public static final int PENALTY = SuperSlicer.PENALTY*NUMBER_OF_CHILD;


    public MegaSlicer(List<Point> polyline) {
        super(polyline.get(0), IMAGE_FILE, polyline, SPEED, HEALTH, REWARD, PENALTY, NUMBER_OF_CHILD);
    }

    /**
     * create a new child slicer (mega)
     * spawns at the location given instead of the start of the polyline
     * @param point spawning location
     * @param polyline the traversing line
     * @param targetPointIndex the point in the polyline that the slicer needs to approach
     */
    public MegaSlicer(Point point, List<Point> polyline, int targetPointIndex) {
        super(point, IMAGE_FILE, polyline, targetPointIndex, SPEED, HEALTH, REWARD, PENALTY, NUMBER_OF_CHILD);
    }

    /**
     * create a copy of the given slicer
     * @param megaSlicer the slicer that needs to be copied
     */
    public MegaSlicer(Slicer megaSlicer){
        super(megaSlicer.getCenter(), IMAGE_FILE, megaSlicer.getPolyline(), megaSlicer.getTargetPointIndex(),
                SPEED,HEALTH,REWARD,PENALTY,NUMBER_OF_CHILD);
    }


    /**
     * set the child slicer to be super slicer and use the
     * parent class method to load the child slicers
     */
    @Override
    public void spawnChild(List<Point> polyline) {
        super.setChildSlicer(new SuperSlicer(getRect().centre(),polyline,getTargetPointIndex()));
        super.spawnChild(polyline);
    }
}
