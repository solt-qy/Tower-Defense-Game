import bagel.util.Point;

public class SuperTankProjectile extends ActiveProjectile {

    private static final String IMAGE_FILE = "res/images/supertank_projectile.png";
    private static final int DAMAGE = TankProjectile.DAMAGE*3;

    public SuperTankProjectile(Point startingPoint, Slicer targetEnemy) {
        super(startingPoint, IMAGE_FILE, DAMAGE, targetEnemy);
    }
}
