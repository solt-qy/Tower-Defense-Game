import bagel.Input;
import bagel.util.Point;
import bagel.util.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the enemy in the game: slicer
 */
public abstract class Slicer extends GameObject {

    private final List<Point> polyline;
    private int targetPointIndex;
    private boolean finished;
    private final double speed;
    private int health;
    private final int reward;
    private int penalty;
    private final List<Slicer> childSlicers;
    private final int numberOfChildSlicer;
    private Slicer childSlicer;

    /**
     * This is the standard constructor to instantiate a
     * slicer at the starting point of the polyline
     */
    public Slicer(Point point, String imageSrc, List<Point> polyline,
                        double speed, int health, int reward, int penalty, int numberOfChildSlicer) {
        super(point, imageSrc);
        this.polyline = polyline;
        this.targetPointIndex = 1;
        this.finished = false;
        this.speed = speed;
        this.reward = reward;
        this.health = health;
        this.penalty = penalty;
        this.childSlicers = new ArrayList<>();
        this.numberOfChildSlicer = numberOfChildSlicer;
        this.childSlicer = null;
    }

    /**
     *This constructor is for slicer that is spawning due to the
     * death of its parent slicer, hence the target point it needs
     * to approach is no longer the first point in the polyline
     */
    public Slicer(Point point, String imageSrc, List<Point> polyline, int targetPointIndex,
                                        double speed, int health, int reward, int penalty, int numberOfChildSlicer) {
        super(point, imageSrc);
        this.polyline = polyline;
        this.targetPointIndex = targetPointIndex;
        this.finished = false;
        this.speed = speed;
        this.health = health;
        this.reward = reward;
        this.penalty = penalty;
        this.childSlicers = new ArrayList<>();
        this.numberOfChildSlicer = numberOfChildSlicer;
        this.childSlicer = null;
    }

    public boolean isFinished() {
        return finished;
    }

    public List<Point> getPolyline() {
        return polyline;
    }

    public int getHealth() {
        return health;
    }

    public int getReward() {
        return reward;
    }

    public int getPenalty() {
        return penalty;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getTargetPointIndex() {
        return targetPointIndex;
    }

    public List<Slicer> getChildSlicers() {
        return childSlicers;
    }

    public void setChildSlicer(Slicer childSlicer) {
        this.childSlicer = childSlicer;
    }

    @Override

    //movement methods use code from project 1 solution
    public void update(Input input) {
        if (finished) {
            return;
        }
        // Obtain where we currently are, and where we want to be
        Point currentPoint = getCenter();
        Point targetPoint = polyline.get(targetPointIndex);
        // Convert them to vectors to perform some very basic vector math
        Vector2 target = targetPoint.asVector();
        Vector2 current = currentPoint.asVector();
        Vector2 distance = target.sub(current);
        // Distance we are (in pixels) away from our target point
        double magnitude = distance.length();
        // Check if we are close to the target point
        if (magnitude < speed * ShadowDefend.getTimescale()) {
            // Check if we have reached the end
            if (targetPointIndex == polyline.size() - 1) {
                finished = true;
                return;
            } else {
                // Make our focus the next point in the polyline
                targetPointIndex += 1;
            }
        }
        // Move towards the target point
        // We do this by getting a unit vector in the direction of our target, and multiplying it
        // by the speed of the slicer (accounting for the timescale)
        super.move(distance.normalised().mul(speed * ShadowDefend.getTimescale()));
        // Update current rotation angle to face target point
        setAngle(Math.atan2(targetPoint.y - currentPoint.y, targetPoint.x - currentPoint.x));
        super.update(input);
    }

    /**
     * load all the childSlicers into the arraylist
     * which will be used by the main game
     */
    public void spawnChild(List<Point> polyline) {
        for(int i = 0;i<numberOfChildSlicer; i++){
            if(childSlicer instanceof RegularSlicer){
                childSlicers.add(new RegularSlicer(childSlicer));
            } else if(childSlicer instanceof SuperSlicer){
                childSlicers.add(new SuperSlicer(childSlicer));
            } else {
                childSlicers.add(new MegaSlicer(childSlicer));
            }
        }
    }

}
