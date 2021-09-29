import bagel.Input;
import bagel.util.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Plane extends Tower {

    //generate random drop time
    private static final Random rand = new Random();

    //determines if plane will fly horizontally or vertically
    private final boolean isHorizontal;
    //determines if the plane is displaying in the shop
    private boolean beingDisplayed;
    //determines if the plane has been bought and is currently flying
    private boolean isFlying;
    private List<PlaneExplosive> explosives;

    private static final String IMAGE_FILE = "res/images/airsupport.png";
    private static final double MAX_DROP_TIME = 1.0;
    private static final double MOVING_SPEED = 3.0;
    private static final int PRICE = 500;
    //These constants will be used to determine when the plane
    //has flied out of boundary (plane will be unable to
    //drop explosives outside boundary)
    private static final double TOP_BOUND = 110;
    private static final double BOTTOM_BOUND = 768;
    private static final double LEFT_BOUND = 0;
    private static final double RIGHT_BOUND = 1024;
    //To ensure the plane is completely out of boundary
    private static final double BOUNDARY_OFFSET = 32;


    /**
     * this constructor is for planes that
     * are on display on the buy panel
     * @param placingPoint location on the display panel
     */
    public Plane(Point placingPoint) {
        super(placingPoint, IMAGE_FILE, PRICE, rand.nextDouble()*MAX_DROP_TIME);
        this.isHorizontal = true;
        this.beingDisplayed = true;
        this.isFlying = false;
    }

    /**
     * Planes that will be spawned in the game
     * @param placingPoint the location where the player places the plane
     * @param isHorizontal if the plane will fly horizontally or vertically
     */
    public Plane(Point placingPoint, boolean isHorizontal) {
        super(placingPoint, IMAGE_FILE, PRICE, 1 + rand.nextDouble()*MAX_DROP_TIME,0);
        this.isHorizontal = isHorizontal;
        this.beingDisplayed = true;
        this.isFlying = false;
        this.explosives = new ArrayList<>();

    }

    public List<PlaneExplosive> getExplosives() {
        return explosives;
    }

    public void setBeingDisplayed(boolean beingDisplayed) {
        this.beingDisplayed = beingDisplayed;
    }

    public boolean isFlying() {
        return isFlying;
    }

    public void setFlying(boolean flying) { isFlying = flying; }

    /**
     * load a explosive in the explosive list
     * the drop time is randomized by rand.nextDouble()*MAX_DROP_TIME
     */
    public void dropBomb() {
        //plane drops an explosive once the drop time has passed
        if(super.getCoolDownCounter()/ShadowDefend.FPS >= super.getCoolDown()/ShadowDefend.getTimescale()){
            //plane center out of bounds
            //hence won't be able to load more explosives
            if(getCenter().x <= LEFT_BOUND ||
                    getCenter().x >= RIGHT_BOUND ||
                    getCenter().y <= TOP_BOUND ||
                    getCenter().y >= BOTTOM_BOUND){
                return;
            }
            explosives.add(new PlaneExplosive(getCenter()));
            //choose a new random drop time
            super.setCoolDown(1 + rand.nextDouble()*MAX_DROP_TIME);
            //reset the timer
            super.setCoolDownCounter(0);
        }
    }

    @Override
    public void update(Input input) {
        super.update(input);
        //This is when the plane is displayed in the menu
        //prevent plane from moving
        if(beingDisplayed){
            return;
        }
        initiatePlane();
        movePlane();
        setCoolDownCounter(getCoolDownCounter() + 1);
        removeExplosives();
        updateExplosives(input);
    }

    /**
     * set the plane at its corresponding starting location
     */
    private void initiatePlane(){
        //plane will fly horizontally
        //set plane on the left side of the window
        if(isHorizontal && !isFlying){
            setBoundingBox(new Point(-BOUNDARY_OFFSET,getCenter().y));
            setAngle(ShadowDefend.NINETY_DEGREES);
            setFlying(true);
        }
        //plane will fly vertically
        //set plane on the top of the window
        else if (!isHorizontal && !isFlying) {
            setBoundingBox(new Point(getCenter().x,new BuyPanel().getHeight()));
            setAngle(ShadowDefend.HUNDRED_EIGHTY_DEGREES);
            setFlying(true);
        }
    }


    //move the plane depending on it moves horizontally or vertically
    private void movePlane(){
        if(isHorizontal && getCenter().x + MOVING_SPEED <= (RIGHT_BOUND + BOUNDARY_OFFSET)){
            super.setBoundingBox(new Point(getCenter().x +
                                 MOVING_SPEED*ShadowDefend.getTimescale(), getCenter().y));
        } else if(!isHorizontal && getCenter().y + MOVING_SPEED <= (BOTTOM_BOUND + BOUNDARY_OFFSET)){
            super.setBoundingBox(new Point(getCenter().x, getCenter().y +
                                 MOVING_SPEED*ShadowDefend.getTimescale()));
        }
    }

    //remove all the detonated explosives
    private void removeExplosives(){
        explosives.removeIf(PlaneExplosive::isDetonated);
    }

    //update the explosives
    private void updateExplosives(Input input){
        for(PlaneExplosive explosive: explosives){
            explosive.update(input);
        }
    }

}
