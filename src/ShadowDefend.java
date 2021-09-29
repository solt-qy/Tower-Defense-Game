import bagel.*;
import bagel.map.TiledMap;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


public class ShadowDefend extends AbstractGame {
    
    public static final int HEIGHT = 768;
    public static final int WIDTH = 1024;
    private static final String BLOCKED_TILE_PROPERTY = "blocked";
    private static final int NUMBER_OF_MAP = 2;
    private static final String WAVE_FILE = "res/levels/waves.txt";
    private static final int WAVE_NUMBER_POSITION = 0;
    private static final int EVENT_TYPE_POSITION = 1;
    private static final String SPAWN_EVENT_TYPE = "spawn";
    private static final String WAVE_IN_PROGRESS = "Wave In Progress";
    private static final String AWAITING_START = "Awaiting Start";
    private static final String PLACING = "Placing";
    private static final String WINNER = "Winner!";
    private static final String WAVE_INFO_SPLITTER = ",";
    private static final int SLICER_NUMBER_POSITION = 2;
    private static final int SLICER_TYPE_POSITION = 3;
    private static final int SLICER_DELAY_POSITION = 4;
    private static final int WAVE_DELAY_POSITION = 2;
    //rewards for completing a wave
    private static final int WAVE_INDEPENDENT_REWARD = 150;
    private static final int WAVE_DEPENDENT_REWARD = 100;
    //90 degrees and 180 degrees in radians (turning angles)
    public static final double NINETY_DEGREES = Math.PI/2;
    public static final double HUNDRED_EIGHTY_DEGREES = Math.PI;
    // Change to suit system specifications. This could be
    // dynamically determined but that is out of scope.
    public static final double FPS = 60;
    private static final double MILLISECOND = 1000;
    //Constant to find the attack range
    private static final int RECTANGLE_CONSTANT = 2;
    // Timescale is made static because it is a universal property of the game and the specification
    // says everything in the game is affected by this
    private static int timescale = 1;
    private final List<TiledMap> map;
    private BuyPanel buyPanel;
    private StatusPanel statusPanel;
    private List<Point> polyline;
    private List<Tower> placedTowers;
    private List<Slicer> slicers;
    private final List<Tower> towersForSale;
    private final List<Event> waveEvent;
    private int currentMapIndex;
    private int currentWaveIndex;
    private double frameCount;
    private int spawnedSlicers;
    //whether the next plane will fly horizontally or vertically
    private boolean isPlaneHorizontal;
    //whether the wave has started
    private boolean isWaveStarted;
    //isLastWave is true when the game is executing the last wave event
    private boolean isLastWave;
    private Tower selectedTower;


    /**
     * Creates a new instance of the ShadowDefend game
     */
    public ShadowDefend() {
        super(WIDTH, HEIGHT, "ShadowDefend");
        this.map = new ArrayList<>();
        //add all the map files to the map list
        for(int i = 1; i <= NUMBER_OF_MAP; i++){
            map.add(new TiledMap(String.format("res/levels/%d.tmx", i)));
        }
        this.currentMapIndex = 0;
        this.buyPanel = new BuyPanel();
        this.statusPanel = new StatusPanel();
        this.towersForSale = buyPanel.getTowersForSale();
        this.placedTowers = new ArrayList<>();
        this.polyline = map.get(currentMapIndex).getAllPolylines().get(0);
        this.slicers = new ArrayList<>();
        this.waveEvent = new ArrayList<>();
        this.currentWaveIndex = 0;
        this.spawnedSlicers = 0;
        this.isPlaneHorizontal = true;
        this.isWaveStarted = false;
        this.isLastWave = false;
        this.frameCount = Integer.MAX_VALUE;
        this.selectedTower = null;
        getWaveInfo();
    }

    /**
     * The entry-point for the game
     *
     * @param args Optional command-line arguments
     */
    public static void main(String[] args) {
        new ShadowDefend().run();
    }

    public static int getTimescale() {
        return timescale;
    }

    /**
     * Increases the timescale
     */
    private void increaseTimescale() {
        timescale++;
    }

    /**
     * Decreases the timescale but doesn't go below the base timescale
     */
    private void decreaseTimescale() {
        if (timescale > 1) {
            timescale--;
        }
    }

    /**
     * Update the state of the game, potentially reading from input
     *
     * @param input The current mouse/keyboard state
     */
    @Override
    protected void update(Input input) {
        frameCount += getTimescale();
        drawMap();
        buyTowers(input);
        keyBindsLogic(input);
        executeWaveEvent();
        towerAttackingLogic();
        moveSlicer(input);
        drawTowers(input);
        buyPanel.update(input);
        statusPanel.update(input);
        gameEnding();
        resetGame();
    }

    /**
     * draw the map on the window
     */
    private void drawMap(){
        map.get(currentMapIndex).draw(0, 0, 0, 0, WIDTH, HEIGHT);
    }

    /**
     * implements the key presses logic
     * S - start wave
     * L - increase timescale
     * K - decrease timescale
     */
    private void keyBindsLogic(Input input){
        if (input.wasPressed(Keys.S)) {
            isWaveStarted = true;
        }

        if (input.wasPressed(Keys.L)) {
            increaseTimescale();
        }

        if (input.wasPressed(Keys.K)) {
            decreaseTimescale();
        }
    }

    /**
     * For passive tower, the method will load explosives
     * into it and check if any slicer enters the explosives'
     * effect radius after it has detonated
     *
     * For active tower, any slicer that enters the attack range
     * of the existing towers will be attacked with projectiles
     */
    private void towerAttackingLogic(){
        //passive tower attack logic
        for(Tower tower: placedTowers){
            if(tower instanceof Plane){
                //load explosives into the plane
                ((Plane) tower).dropBomb();
                //check if slicer enters the effect radius of all the detonated explosives
                for(Slicer slicer: slicers) {
                    for (PlaneExplosive explosive : ((Plane) tower).getExplosives()) {
                        //check whether the explosive has detonated and whether the slicer
                        //has entered the range of the explosive
                        if (explosive.isDetonated() &&
                                slicer.getRect().intersects(getAttackRange(PlaneExplosive.getEffectRadius(),
                                                            explosive.getCenter()))){
                            slicer.setHealth(slicer.getHealth()-explosive.getDamage());
                        }
                    }
                }
            }
        }
        //active tower attack logic
        for(Slicer slicer: slicers){
            for (Tower tower : placedTowers) {
                //check if slicer enters the attack range of the tower
                if (tower instanceof ActiveTower
                        && slicer.getRect().intersects(
                                getAttackRange(((ActiveTower) tower).getAttackRadius(),
                                                tower.getCenter()))){
                    //load projectiles against the target enemy
                    ((ActiveTower) tower).attack(((ActiveTower) tower), slicer);
                }
            }
        }
    }

    /**
     * get the attack range in a rectangle
     */
    private Rectangle getAttackRange(double radius, Point center){
        return new Rectangle(new Point(center.x-radius,center.y-radius),
                radius*RECTANGLE_CONSTANT,radius*RECTANGLE_CONSTANT);
    }

    /**
     * get the current wave event, and execute different actions
     * (generate slicer/delay time) according to the event type
     * if all events are finished, close the game
     */
    private void executeWaveEvent(){
        if(!slicers.isEmpty()){
            statusPanel.setWaveStatus(WAVE_IN_PROGRESS);
        } else {
            statusPanel.setWaveStatus(AWAITING_START);
        }
        //this is the situation where the game is finishing up the last wave
        //no more generation of slicers and waiting for the slicers from the last
        //wave events to be eliminated
        if(isLastWave){
            return;
        }
        //execute different actions according to the event type
        if(currentWaveIndex != waveEvent.size()){
            Event event = waveEvent.get(currentWaveIndex);
            if (event instanceof SpawnEvent) {
                slicerSpawningLogic(((SpawnEvent) event).getSlicerNumber(),
                        ((SpawnEvent) event).getSpawnDelay() / MILLISECOND,
                        ((SpawnEvent) event).getSlicerType());
            } else {
                delayTimeLogic(((DelayEvent) event).getDelayTime() / MILLISECOND);
            }
        }
    }

    /**
     * logic to spawn slicers according to a spawn event,
     * if event is finished, start the next event
     * @param maxSlicer the number of slicers to spawn
     * @param slicerDelay delay between the spawning of each slicer
     * @param slicerType the type of slicer to spawn
     */
    private void slicerSpawningLogic(int maxSlicer, double slicerDelay, String slicerType){
        if(isWaveStarted && frameCount / FPS >= slicerDelay && spawnedSlicers != maxSlicer) {
            generateSlicer(slicerType);
            spawnedSlicers += 1;
            frameCount = 0;
        }
        if(spawnedSlicers == maxSlicer){
                spawnedSlicers = 0;
            //start the next event
           finishWaveLogic();
        }
    }

    /**
     * delay time according to the delay event
     * @param delayTime delay time in seconds
     */
    private void delayTimeLogic(double delayTime){
        if(frameCount / FPS >= delayTime){
            finishWaveLogic();
        }
    }

    /**
     * pause the game once a wave is finished
     * and reward the player, if there is no 
     * wave left, the game will either end or
     * load the next level.
     */
    private void finishWaveLogic(){
        if(isWaveIndexWithinBound()) {
            int currentWaveNumber = waveEvent.get(currentWaveIndex).getWaveNumber();
            int futureWaveNumber = waveEvent.get(currentWaveIndex + 1).getWaveNumber();
            if (currentWaveNumber != futureWaveNumber) {
                statusPanel.setWaveNumber(futureWaveNumber);
                buyPanel.setMoney(buyPanel.getMoney() +
                        WAVE_INDEPENDENT_REWARD +
                        WAVE_DEPENDENT_REWARD * currentWaveNumber);
                isWaveStarted = false;
            }
            currentWaveIndex++;
        } else {
            currentWaveIndex++;
            isLastWave = true;
        }
    }

    /**
     * check if the current wave index is out of bound
     */
    private boolean isWaveIndexWithinBound(){
        return currentWaveIndex + 1 < waveEvent.size();
    }

    /**
     * generate a slicer according to the specified type
     * @param slicerType the type of slicer in String
     */
    private void generateSlicer(String slicerType){
        switch (slicerType) {
            case "apexslicer":
                slicers.add(new ApexSlicer(polyline));
                break;
            case "superslicer":
                slicers.add(new SuperSlicer(polyline));
                break;
            case "megaslicer":
                slicers.add(new MegaSlicer(polyline));
                break;
            default:
                slicers.add(new RegularSlicer(polyline));
                break;
        }
    }

    /**
     * move each slicer on the polyline
     * slicer is removed if health below 0,
     * or have finished polyline
     */
    private void moveSlicer(Input input){
        for (int i = slicers.size() - 1; i >= 0; i--) {
            Slicer s = slicers.get(i);
            s.update(input);
            //slicer is eliminated by a tower or an explosive
            if (s.getHealth()<=0) {
                buyPanel.setMoney(buyPanel.getMoney()+slicers.get(i).getReward());
                //load all the child slicers
                slicers.get(i).spawnChild(polyline);
                //add the child slicers to the existing slicers list on the map
                if(!slicers.get(i).getChildSlicers().isEmpty()) {
                    slicers.addAll(slicers.get(i).getChildSlicers());
                }
                slicers.remove(i);
            }
            //slicer moves out of the map
            else if(s.isFinished()){
                statusPanel.setPlayerLives(statusPanel.getPlayerLives()-slicers.get(i).getPenalty());
                slicers.remove(i);
            }
        }
    }
    /**
     * draw all the existing towers that have been purchased and placed
     */
    private void drawTowers(Input input){
        //remove the plane from the game if it has been out of the map
        // and all the explosives have detonated
        placedTowers.removeIf(tower -> tower instanceof Plane &&
                ((Plane) tower).isFlying() &&
                ((Plane) tower).getExplosives().isEmpty() &&
                tower.getCenter().x > WIDTH &&
                tower.getCenter().y > HEIGHT-statusPanel.getHeight());
        for(Tower tower: placedTowers){
           tower.update(input);
        }
    }

    /**
     * This method allows uses to click an item in the
     * menu and place it on the map
     */
    private void buyTowers(Input input){
        //check if an tower is selected
        selectTower(input);
        //deselect the tower buy right clicking it
        if(input.wasPressed(MouseButtons.RIGHT)){
            //reset flight direction due to cancellation of selection
            if(selectedTower instanceof Plane){
                isPlaneHorizontal = !isPlaneHorizontal;
            }
            selectedTower = null;
            statusPanel.setPrioritized();
        }
        //if item is selected check if the area is allowed to place a tower
        if(selectedTower != null){
            if(canTowerBePlaced(input, selectedTower)) {
                selectedTower.setPlacingPoint(input.getMousePosition());
                selectedTower.update(input);
            }
        }
        //place the tower on the map after player confirms location
        if(input.wasPressed(MouseButtons.LEFT)){
            placeTower(input,selectedTower);
        }
    }

    /**
     * This method detects user mouse click and
     * whether a Tower for sale is selected
     */
    private void selectTower(Input input){
        Point mousePoint = input.getMousePosition();
        for(Tower tower: towersForSale){
            if(buyPanel.getMoney()>= tower.getPrice() &&
                    input.wasPressed(MouseButtons.LEFT) && isSelected(mousePoint,tower)){

                statusPanel.setWaveStatus(PLACING);
                if(tower instanceof Tank){
                    selectedTower = new Tank(input.getMousePosition());
                } else if (tower instanceof SuperTank){
                    selectedTower = new SuperTank(input.getMousePosition());
                } else {
                    selectedTower = new Plane(input.getMousePosition(),isPlaneHorizontal);
                    if(isPlaneHorizontal) {
                        selectedTower.setAngle(NINETY_DEGREES);
                    }else {
                        selectedTower.setAngle(HUNDRED_EIGHTY_DEGREES);
                    }
                    isPlaneHorizontal = !isPlaneHorizontal;
                }
            }
        }
    }

    /**
     *This method will place the tower on where the mouse
     * button clicks with the condition that the
     * point is not blocked. After placing the tower,
     * the selectedTower attribute is back to null again
     * and the user is ready to buy the next tower
     */
    private void placeTower(Input input, Tower tower){
        if( selectedTower != null && input.wasPressed(MouseButtons.LEFT) && canTowerBePlaced(input, tower)){
            Point placedPoint = input.getMousePosition();
            selectedTower.setPlacingPoint(placedPoint);
            if(selectedTower instanceof Plane){
                ((Plane) selectedTower).setBeingDisplayed(false);
            }
            placedTowers.add(selectedTower);
            buyPanel.setMoney(buyPanel.getMoney()-tower.getPrice());
            selectedTower = null;
            statusPanel.setPrioritized();
        }
    }

    //return whether the mouse is in the bounding box of a tower
    private boolean isSelected(Point mousePoint, Tower tower){
        return tower.getRect().intersects(mousePoint);
    }


    /**
     * this method checks whether the area the tower is
     * currently at is allowed to be placed on
     * @param input user input
     * @param tower selected tower
     * @return whether the tower can be placed
     */
    private boolean canTowerBePlaced(Input input, Tower tower){
        Point mousePoint = input.getMousePosition();
        Rectangle itemBoundingBox = tower.getImage().getBoundingBoxAt(mousePoint);
        //check if the tower is out of the boundary of the map
        if(itemBoundingBox.left() < 0 || itemBoundingBox.right() > WIDTH
                || itemBoundingBox.top() <buyPanel.getHeight()
                || itemBoundingBox.bottom() > (HEIGHT - statusPanel.getHeight())){
            return false;
        }
        else if(tower instanceof Plane){
            return true;
        }
        //check if the tower is on the blocked tile
        else if(map.get(currentMapIndex).hasProperty((int) input.getMousePosition().x,
                                (int) input.getMousePosition().y,
                                BLOCKED_TILE_PROPERTY)){
            return false;
        }
        //check if the tower overlaps the existing towers
        else return !isTowerOverlap(input, tower);
    }

    /**checks if the tower overlaps any of the existing towers
     * @param tower the tower that is going to be placed
     * @return whether the tower overlaps another tower
     */
    private boolean isTowerOverlap(Input input, Tower tower){
        boolean overlap = false;
        for(Tower existingTowers: placedTowers){
            if(existingTowers.getImage().getBoundingBoxAt(existingTowers.getCenter())
                    .intersects(tower.getImage().getBoundingBoxAt(input.getMousePosition()))){
                overlap = true;
            }
        }
        return overlap;
    }

    /**
     * get the wave information and store them
     * in the waveEvent list
     */
    private void getWaveInfo(){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(ShadowDefend.WAVE_FILE));
            String event = reader.readLine();
            while(event != null){
                storeWaveInfo(event);
                event = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * split the wave information string,
     * categorize them and store them in the
     * waveEvent list according to its type
     */
    private void storeWaveInfo(String event){
        String[] eventDetail = event.split(WAVE_INFO_SPLITTER);
        if(eventDetail[EVENT_TYPE_POSITION].equals(SPAWN_EVENT_TYPE)){
            waveEvent.add(new SpawnEvent(Integer.parseInt(eventDetail[WAVE_NUMBER_POSITION]),
                                         Integer.parseInt(eventDetail[SLICER_NUMBER_POSITION]),
                                         eventDetail[SLICER_TYPE_POSITION],
                                         Double.parseDouble(eventDetail[SLICER_DELAY_POSITION])));
        } else {
            waveEvent.add(new DelayEvent(Integer.parseInt(eventDetail[WAVE_NUMBER_POSITION]),
                                         Double.parseDouble(eventDetail[WAVE_DELAY_POSITION])));
        }
    }

    /**
     * the player lose all lives and the game is closed
     */
    private void gameEnding(){
        if(statusPanel.getPlayerLives() <= 0){
            Window.close();
        }
    }

    /**
     * reset the game and use the new map
     */
    private void resetGame(){
        //wave index has reached the end
        if(currentWaveIndex == waveEvent.size()){
            //if the last event is a delay event or it is a spawn event 
            // and the spawned slicers are either eliminated or out of the map
            if(waveEvent.get(currentWaveIndex-1) instanceof DelayEvent || slicers.isEmpty()){
                //when there is no next level left
                if (currentMapIndex >= NUMBER_OF_MAP - 1) {
                    statusPanel.setWaveStatus(WINNER);
                } 
                //load the next level
                else {
                    currentMapIndex++;
                    this.buyPanel = new BuyPanel();
                    this.statusPanel = new StatusPanel();
                    timescale = 1;
                    this.placedTowers = new ArrayList<>();
                    this.polyline = map.get(currentMapIndex).getAllPolylines().get(0);
                    this.slicers = new ArrayList<>();
                    this.currentWaveIndex = 0;
                    this.spawnedSlicers = 0;
                    this.isPlaneHorizontal = true;
                    this.isWaveStarted = false;
                    this.isLastWave = false;
                    this.frameCount = Integer.MAX_VALUE;
                    this.selectedTower = null;
                }
            }
        }
    }
}
