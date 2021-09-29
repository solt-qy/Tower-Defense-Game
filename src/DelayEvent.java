public class DelayEvent extends Event {

    //amount of time to be delayed
    private double delayTime;

    public DelayEvent(int waveNumber, double delayTime) {
        super(waveNumber);
        this.delayTime = delayTime;
    }

    public double getDelayTime() {
        return delayTime;
    }

}
