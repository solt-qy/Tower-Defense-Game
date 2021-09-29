import bagel.Input;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.util.Vector2;


public class ActiveProjectile extends Projectile {

    private final double speed;
    private final Slicer targetEnemy;
    //whether the projectile has hit the enemy
    private boolean isHit;

    private static final double SPEED = 10 ;

    //constant used to create bounding box
    private static final double HIT_BOX_CONSTANT = 15;
    private static final double HIT_BOX_CONSTANT_WIDTH = HIT_BOX_CONSTANT * 2;

    public ActiveProjectile(Point startingPoint, String imageSrc, int damage, Slicer targetEnemy) {
        super(startingPoint, imageSrc, damage);
        this.speed = SPEED;
        this.targetEnemy = targetEnemy;
        this.isHit = false;
    }

    public Slicer getTargetEnemy() {
        return targetEnemy;
    }

    //determine whether the projectile has hit the enemy
    public boolean isHit() {
        return isHit;
    }

    @Override
    public void update(Input input) {
        Point currentPoint = getCenter();
        Point targetPoint = targetEnemy.getCenter();
        Vector2 target = targetPoint.asVector();
        Vector2 current = currentPoint.asVector();
        Vector2 distance = target.sub(current);
        //The following declaration are used to reduce the size of the bounding box
        // of the projectile and slicer
        Rectangle projectileHitBox = new Rectangle(
                new Point(currentPoint.x-HIT_BOX_CONSTANT ,currentPoint.y-HIT_BOX_CONSTANT ),
                HIT_BOX_CONSTANT_WIDTH,HIT_BOX_CONSTANT_WIDTH);
        Rectangle enemyHitBox = new Rectangle(
                new Point(targetPoint.x-HIT_BOX_CONSTANT,targetPoint.y-HIT_BOX_CONSTANT),
                HIT_BOX_CONSTANT_WIDTH,HIT_BOX_CONSTANT_WIDTH);
        //if bounding box intersects, the enemy is hit
        //and the projectile will be marked as isHit and will be subsequently removed
        if (projectileHitBox.intersects(enemyHitBox)) {
            isHit = true;
            targetEnemy.setHealth(targetEnemy.getHealth()-super.getDamage());
            return;
        }
        //move the projectile towards the target enemy
        super.move(distance.normalised().mul(speed*ShadowDefend.getTimescale()));
        super.update(input);
    }
}
