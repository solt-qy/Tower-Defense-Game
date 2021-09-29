

import bagel.DrawOptions;
import bagel.Image;
import bagel.Input;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.util.Vector2;

/**
 * Represents a game entity
 * Code reused from Project 1 Solution (Sprite)
 */
public abstract class GameObject {

    private final Image image;
    private Rectangle boundingBox;
    private double angle;

    /**
     * Creates a new Sprite (game entity)
     *
     * @param point    The starting point for the entity
     * @param imageSrc The image which will be rendered at the entity's point
     */
    public GameObject(Point point, String imageSrc) {
        this.image = new Image(imageSrc);
        this.boundingBox = image.getBoundingBoxAt(point);
        this.angle = 0;
    }

    public GameObject(Point point, Image image) {
        this.image = image;
        this.boundingBox = image.getBoundingBoxAt(point);
        this.angle = 0;
    }

    public double getHeight(){
        return image.getHeight();
    }

    public Image getImage() {
        return image;
    }

    public Rectangle getRect() {
        return new Rectangle(boundingBox);
    }

    public void setBoundingBox(Point point) {
        this.boundingBox = image.getBoundingBoxAt(point);
    }

    /**
     * Moves the Sprite by a specified delta
     *
     * @param dx The move delta vector
     */
    public void move(Vector2 dx) {
        boundingBox.moveTo(boundingBox.topLeft().asVector().add(dx).asPoint());
    }

    public Point getCenter() {
        return getRect().centre();
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }



    /**
     * Updates the Sprite. Default behaviour is to render the Sprite at its current position.
     */
    public void update(Input input) {
        image.draw(getCenter().x, getCenter().y, new DrawOptions().setRotation(angle));
    }
}