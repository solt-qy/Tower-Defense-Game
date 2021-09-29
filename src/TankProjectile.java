import bagel.util.Point;

public class TankProjectile extends ActiveProjectile {

    private static final String IMAGE_FILE = "res/images/tank_projectile.png";
    public static final int DAMAGE = 1;


    public TankProjectile(Point startingPoint, Slicer targetEnemy) {
        super(startingPoint, IMAGE_FILE, DAMAGE, targetEnemy);
    }

}
