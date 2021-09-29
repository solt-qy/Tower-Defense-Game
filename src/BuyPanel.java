import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;

import java.util.ArrayList;

public class BuyPanel extends GameObject {

    private final ArrayList<Tower> towersForSale = new ArrayList<>();
    private int money;


    private static final String FONT_FILE = "res/fonts/DejaVuSans-Bold.ttf";
    private static final String BUY_PANEL_FILE = "res/images/buypanel.png";
    private static final Image BUY_PANEL = new Image(BUY_PANEL_FILE);
    private static final Font KEY_INFO_FONT = new Font(FONT_FILE, 15);
    private static final Font PRICE_FONT = new Font(FONT_FILE,24);
    private static final Font MONEY_FONT = new Font(FONT_FILE,48);
    private static final int PRICE_FONT_X_OFFSET = 30;
    private static final int PRICE_FONT_y_OFFSET = 50;
    private static final int INITIAL_MONEY = 500;
    //Constant for the location of all the items to be drawn
    private static final Point PANEL_CENTER = new Point(512, BUY_PANEL.getHeight()/2);
    private static final Point TANK_LOCATION = new Point(64,PANEL_CENTER.y-10);
    private static final Point SUPER_TANK_LOCATION = new Point(184,PANEL_CENTER.y-10);
    private static final Point PLANE_LOCATION = new Point(304,PANEL_CENTER.y-10);
    private static final Point KEY_INFO_LOCATION = new Point( PANEL_CENTER.x,25);
    private static final Point MONEY_LOCATION = new Point(824,65);


    public BuyPanel() {
        super(PANEL_CENTER, BUY_PANEL);
        this.money = INITIAL_MONEY;
        this.towersForSale.add(new Tank(TANK_LOCATION));
        this.towersForSale.add(new SuperTank(SUPER_TANK_LOCATION));
        this.towersForSale.add(new Plane(PLANE_LOCATION));
    }


    public ArrayList<Tower> getTowersForSale() {
        return towersForSale;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    @Override
    public void update(Input input) {
        super.update(input);
        for(Tower tower: towersForSale){
            tower.update(input);
            drawPrice(tower);
        }
        drawKeyBind();
        drawMoney();
    }

    //draw the price tag of each tower for sale
    private void drawPrice(Tower tower){
        if(this.money < tower.getPrice()){
            PRICE_FONT.drawString(String.format("$%d",tower.getPrice()),
                    tower.getCenter().x-PRICE_FONT_X_OFFSET,
                    tower.getCenter().y + PRICE_FONT_y_OFFSET,
                    new DrawOptions().setBlendColour(Colour.RED));
        } else {
            PRICE_FONT.drawString(String.format("$%d",tower.getPrice()),
                    tower.getCenter().x-PRICE_FONT_X_OFFSET,
                    tower.getCenter().y + PRICE_FONT_y_OFFSET,
                    new DrawOptions().setBlendColour(Colour.GREEN));
        }
    }

    //draws the key binds information
    private void drawKeyBind() {
        KEY_INFO_FONT.drawString("KEY binds:\n\n" +
                "S - Start Wave\n" +
                "L - Increase Timescale\n" +
                "K - Decrease Timescale", KEY_INFO_LOCATION.x, KEY_INFO_LOCATION.y);
    }

    //draws the amount of money the player owns
    private void drawMoney(){
        MONEY_FONT.drawString(String.format("$%d",this.money),MONEY_LOCATION.x,MONEY_LOCATION.y);
    }



}
