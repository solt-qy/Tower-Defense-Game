import bagel.DrawOptions;
import bagel.Font;
import bagel.Image;
import bagel.Input;
import bagel.util.Colour;
import bagel.util.Point;

public class StatusPanel extends GameObject {

    private int playerLives;
    private int waveNumber;
    private String waveStatus;
    //this will ensure the "placing" status being shown with priority
    private boolean prioritized;

    private static final String FONT_FILE = "res/fonts/DejaVuSans-Bold.ttf";
    private static final String STATUS_PANEL_FILE = "res/images/statuspanel.png";
    private static final Font STATUS_INFO_FONT = new Font(FONT_FILE,18);
    private static final Image STATUS_PANEL = new Image(STATUS_PANEL_FILE);
    private static final int INITIAL_LIVES = 25;
    //location of all the elements of the buy panel
    private static final Point PANEL_CENTER = new Point(512, 768 - STATUS_PANEL.getHeight()/2);
    private static final Point WAVE_NUMBER_LOCATION = new Point(5,PANEL_CENTER.y+5);
    private static final Point TIME_SCALE_LOCATION = new Point(200,PANEL_CENTER.y+5);
    private static final Point STATUS_LOCATION = new Point(450,PANEL_CENTER.y+5);
    private static final Point LIVES_LOCATION = new Point(930,PANEL_CENTER.y+5);

    public StatusPanel() {
        super(PANEL_CENTER, STATUS_PANEL);
        this.playerLives = INITIAL_LIVES;
        this.waveNumber = 1;
        this.waveStatus = "Awaiting Start";
        this.prioritized = false;
    }


    public int getPlayerLives() {
        return playerLives;
    }

    public void setPlayerLives(int playerLives) {
        this.playerLives = playerLives;
    }

    public void setWaveNumber(int waveNumber) {
        this.waveNumber = waveNumber;
    }

    /**
     * if the status is "placing", it will be displayed with priority
     * only when the player is not placing, prioritized will be set to false
     * and other status can be displayed
     * @param waveStatus the status displaying in the status panel
     */
    public void setWaveStatus(String waveStatus) {
        if(waveStatus.equals("Placing") || waveStatus.equals("Winner!")){
            this.prioritized = true;
            this.waveStatus = waveStatus;
        }
        if(prioritized){
            return;
        }
        this.waveStatus = waveStatus;
    }

    public void setPrioritized() {
        this.prioritized = false;
    }

    @Override
    public void update(Input input) {
        super.update(input);
        drawWaveNumber();
        drawTimeScale();
        drawStatus();
        drawLives();
    }

    private void drawWaveNumber(){
        STATUS_INFO_FONT.drawString(String.format("Wave: %d", waveNumber),
                WAVE_NUMBER_LOCATION.x, WAVE_NUMBER_LOCATION.y);
    }

    private void drawTimeScale(){
        double timeScale = ShadowDefend.getTimescale();
        if(timeScale > 1){
            STATUS_INFO_FONT.drawString(String.format("Time Scale: %.1f", timeScale),
                    TIME_SCALE_LOCATION.x, TIME_SCALE_LOCATION.y, new DrawOptions().setBlendColour(Colour.GREEN));
        } else {
            STATUS_INFO_FONT.drawString(String.format("Time Scale: %.1f", timeScale),
                    TIME_SCALE_LOCATION.x, TIME_SCALE_LOCATION.y);
        }
    }

    private void drawStatus(){
        STATUS_INFO_FONT.drawString(String.format("Status: %s", waveStatus),
                STATUS_LOCATION.x, STATUS_LOCATION.y);
    }

    private void drawLives(){
        STATUS_INFO_FONT.drawString(String.format("Lives: %d", playerLives),
                LIVES_LOCATION.x, LIVES_LOCATION.y);
    }
}
