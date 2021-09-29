import bagel.Input;
import bagel.util.Point;

public abstract class Projectile extends GameObject {

    private int damage;

    public Projectile(Point startingPoint, String imageSrc, int damage) {
        super(startingPoint, imageSrc);
        this.damage = damage;
    }


    public int getDamage() {
        return damage;
    }

}
