import bagel.Input;
import bagel.util.Point;


public class PlaneExplosive extends Projectile {

    private double detonateCounter;
    private boolean isDetonated;
    private static final String IMAGE_FILE = "res/images/explosive.png";
    private static final double DETONATE_TIME = 2.0;
    private static final double EFFECT_RADIUS = 200;
    private static final int DAMAGE = 500;

    public PlaneExplosive(Point startingPoint) {
        super(startingPoint, IMAGE_FILE, DAMAGE);
        this.detonateCounter = 0;
    }

    public boolean isDetonated() {
        return isDetonated;
    }

    public static double getEffectRadius() {
        return EFFECT_RADIUS;
    }

    @Override
    public void update(Input input) {
        detonateCounter++;
        //detonate the explosive after it has passed the detonate time
        if(detonateCounter/ShadowDefend.FPS >= DETONATE_TIME/ShadowDefend.getTimescale()){
            isDetonated = true;
            return;
        }
        super.update(input);
    }
}
