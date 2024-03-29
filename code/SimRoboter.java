

public class SimRoboter implements Roboter {
    private static int numberOfRoboters = 0;

    private final String roboName;
    private final int roboNumber;
    private int[] position;
    private int headPosition;
    private String selected = "";

    SimRoboter(int headPosition, int[] position){
        this.roboName = "SimRobo_" + (++this.numberOfRoboters);
        this.position = position;
        this.headPosition = headPosition;
        this.roboNumber = numberOfRoboters;
    }

    public void setHeadPosition(int headPosition){
        this.headPosition = headPosition;
    }

    public int getHeadPosition(){
        return this.headPosition;
    }

    public void setPosition(int[] position){
        this.position = position;
    }

    public int[] getPosition(){
        return this.position;
    }

    public String getSelected(){
        return this.selected;
    }

    public String getRoboName(){
        return this.roboName;
    }

    public int getRoboNumber(){
        return this.roboNumber;
    }

    @Override
    public void doAction(int action) {

    }

    @Override
    public void fetchData(int pos) {

    }

    @Override
    public int findBarrier() {
        return 0;
    }

    @Override
    public void look() {

    }

    @Override
    public boolean isBumped() {
        return false;
    }

    @Override
    public boolean isGoal() {
        return false;
    }

    @Override
    public void forward() {

    }

    @Override
    public void backward() {

    }

    @Override
    public void left() {

    }

    @Override
    public void right() {

    }

}
