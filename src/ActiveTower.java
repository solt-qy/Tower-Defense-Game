import bagel.Input;
import bagel.util.Point;

import java.util.ArrayList;
import java.util.List;

public class ActiveTower extends Tower{

    private double attackRadius;
    private final List<ActiveProjectile> projectiles;

    public ActiveTower(Point placingPoint, String imageSrc, int price, double coolDown, double attackRadius) {
        super(placingPoint, imageSrc, price, coolDown);
        this.attackRadius = attackRadius;
        this.projectiles = new ArrayList<>();
    }

    public double getAttackRadius() {
        return attackRadius;
    }

    /**
     * This method will instantiate a projectile for the tower,
     * with the chosen target enemy slicer
     * @param tower
     * @param slicer
     */
    public void attack(ActiveTower tower, Slicer slicer) {
        //To ensure that the cooldown is passed
        if( super.getCoolDownCounter() / ShadowDefend.FPS >= super.getCoolDown()/ShadowDefend.getTimescale()) {
            //reset cooldown once a projectile is loaded
            super.setCoolDownCounter(0);
            //load specific type of projectile according to the tower type
            if(tower instanceof Tank) {
                projectiles.add(new TankProjectile(getCenter(), slicer));
            } else {
                projectiles.add(new SuperTankProjectile(getCenter(), slicer));
            }
            Point currentPoint = getCenter();
            Point targetPoint = projectiles.get(0).getTargetEnemy().getCenter();
            setAngle(ShadowDefend.NINETY_DEGREES +
                    Math.atan2(targetPoint.y - currentPoint.y, targetPoint.x - currentPoint.x));
        }
    }

    @Override
    public void update(Input input) {
        //increment the cooldown counter
        super.setCoolDownCounter(super.getCoolDownCounter() + 1);
        //update all the projectiles and remove the ones that hit the enemy
        for(int i = 0;i <projectiles.size();i++){
            //remove projectiles that hit the enemy
            if(projectiles.get(i).isHit()){
                projectiles.remove(i);
                continue;
            }
            projectiles.get(i).update(input);
        }
        super.update(input);
    }

}
