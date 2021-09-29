public class SpawnEvent extends Event{

    private int slicerNumber;
    private String SlicerType;
    private double spawnDelay;

    public SpawnEvent(int waveNumber, int slicerNumber, String slicerType, double spawnDelay) {
        super(waveNumber);
        this.slicerNumber = slicerNumber;
        SlicerType = slicerType;
        this.spawnDelay = spawnDelay;
    }

    public int getSlicerNumber() {
        return slicerNumber;
    }

    public String getSlicerType() {
        return SlicerType;
    }

    public double getSpawnDelay() {
        return spawnDelay;
    }

}
