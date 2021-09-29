import bagel.util.Point;

public abstract class Tower extends GameObject {

    private final int price;
    private double coolDown;
    private double coolDownCounter;

    /**
     *  Active tower constructor, cooldown counter is set to max
     *  due to the first projectile it shoots doesn't require cooldown
     */
    public Tower(Point placingPoint, String imageSrc, int price, double coolDown) {
        super(placingPoint, imageSrc);
        this.price = price;
        this.coolDown = coolDown;
        this.coolDownCounter = coolDown*ShadowDefend.FPS;
    }

    /**
     * Constructor for the plane, plane needs the
     * cooldown counter initialized to 0 because it
     * needs to wait the first cooldown to drop the
     * firs bomb
     */
    public Tower(Point placingPoint, String imageSrc, int price, double coolDown, double coolDownCounter) {
        super(placingPoint, imageSrc);
        this.price = price;
        this.coolDown = coolDown;
        this.coolDownCounter = coolDownCounter;
    }


    public int getPrice() {
        return price;
    }

    public double getCoolDown() {
        return coolDown;
    }

    public double getCoolDownCounter() {
        return coolDownCounter;
    }

    public void setCoolDown(double coolDown) {
        this.coolDown = coolDown;
    }

    public void setCoolDownCounter(double coolDownCounter) {
        this.coolDownCounter = coolDownCounter;
    }

    public void setPlacingPoint(Point placingPoint) {
        super.setBoundingBox(placingPoint);
    }

}
