import bagel.util.Point;

public class SuperTank extends ActiveTower{
    private static final String IMAGE_FILE = "res/images/supertank.png";
    private static final int PRICE = 600;
    private static final int ATTACK_RADIUS = 150;
    private static final double COOlDOWN = 0.5;

    public SuperTank(Point placingPoint) {
        super(placingPoint, IMAGE_FILE, PRICE, COOlDOWN, ATTACK_RADIUS);
    }


}
