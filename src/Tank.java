import bagel.util.Point;


public class Tank extends ActiveTower {

    private static final String IMAGE_FILE = "res/images/tank.png";
    private static final int PRICE = 250;
    private static final int ATTACK_RADIUS = 100;
    private static final double COOlDOWN = 1;

    public Tank(Point placingPoint) {
        super(placingPoint, IMAGE_FILE, PRICE, COOlDOWN, ATTACK_RADIUS);
    }

}
