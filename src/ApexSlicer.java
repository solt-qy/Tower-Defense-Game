import bagel.util.Point;

import java.util.List;

public class ApexSlicer extends Slicer {

    private static final int NUMBER_OF_CHILD = 4;
    private static final String IMAGE_FILE = "res/images/apexslicer.png";
    public static final double SPEED = (1.0/2)*MegaSlicer.SPEED;
    public static final int HEALTH = RegularSlicer.HEALTH*25;
    public static final int REWARD = 150;
    public static final int PENALTY = MegaSlicer.PENALTY*NUMBER_OF_CHILD;


    public ApexSlicer(List<Point> polyline) {
        super(polyline.get(0), IMAGE_FILE, polyline,SPEED, HEALTH, REWARD, PENALTY, NUMBER_OF_CHILD);
    }


    /**
     * set the child slicer to be mega slicer and use the
     * parent class method to load the child slicers
     */
    @Override
    public void spawnChild(List<Point> polyline) {
        super.setChildSlicer(new MegaSlicer(getRect().centre(),polyline,getTargetPointIndex()));
        super.spawnChild(polyline);
    }
}
