

public interface Roboter {

    void doAction(int action);
    void fetchData(int pos);
    int findBarrier();
    void look();
    boolean isBumped();
    boolean isGoal();
    void forward();
    void backward();
    void left();
    void right();

}
