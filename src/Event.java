public abstract class Event {
    private int waveNumber;

    public Event(int waveNumber) {
        this.waveNumber = waveNumber;
    }

    public int getWaveNumber() {
        return waveNumber;
    }
}
