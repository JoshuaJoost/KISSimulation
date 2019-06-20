package gui_simulation;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;

public class SimulationRobot implements Roboter {
    private static final String PREFIX_ROBO_NAME = "Robo_";
    private static final String SELECTED_TEXT = "yep";
    private static final String NOT_SELECTED_TEXT = "";
    private static final Color DEFAULT_ROBOT_COLOR = Color.rgb(0, 0, 255);

    private static ArrayList<SimulationRobot> robots = new ArrayList<>();
    private static int numberOfRobots = 0;
    private static Integer indexSelectedRobot = null;

    private final String roboName;
    private final int roboNumber;
    private final int sizeX;
    private final int sizeY;
    private int[] position;
    private String selected = "";
    private Color robotColor = null;

    private SimulationRobot(int robotPixelX, int robotPixelY) {
        roboName = PREFIX_ROBO_NAME + (++numberOfRobots);
        roboNumber = numberOfRobots;
        this.sizeX = robotPixelX;
        this.sizeY = robotPixelY;
    }

    private SimulationRobot(int robotPixelX, int robotPixelY, Color robotColor) {
        roboName = PREFIX_ROBO_NAME + (++numberOfRobots);
        roboNumber = numberOfRobots;
        this.sizeX = robotPixelX;
        this.sizeY = robotPixelY;
        this.robotColor = robotColor;
    }

    private SimulationRobot(int robotPixelX, int robotPixelY, Color robotColor, int[] position) {
        roboName = PREFIX_ROBO_NAME + (++numberOfRobots);
        roboNumber = numberOfRobots;
        this.sizeX = robotPixelX;
        this.sizeY = robotPixelY;
        this.robotColor = robotColor;
        this.position = position;
    }

    public static Color getDefaultRobotColor() {
        return DEFAULT_ROBOT_COLOR;
    }

    public static ArrayList<SimulationRobot> addRobot(int robotPixelX, int robotPixelY) {
        robots.add(new SimulationRobot(robotPixelX, robotPixelY));

        return robots;
    }

    public static ArrayList<SimulationRobot> addRobot(int robotPixelX, int robotPixelY, Color robotColor, int[] position) {
        robots.add(new SimulationRobot(robotPixelX, robotPixelY, robotColor, position));
        return robots;
    }

    public static ArrayList<SimulationRobot> getRobots() {
        return robots;
    }

    public static SimulationRobot getSelectedRobot() {
        return robots.get(indexSelectedRobot);
    }

    public static boolean changeSelectedRobot(int indexNewSelectedRobot) {
        if (indexNewSelectedRobot <= robots.size() - 1) {
            if (indexSelectedRobot == null) {
                indexSelectedRobot = indexNewSelectedRobot;
                robots.get(indexSelectedRobot).setSelected(SELECTED_TEXT);
                return true;
            } else {
                if (indexSelectedRobot != indexNewSelectedRobot) {
                    robots.get(indexSelectedRobot).setSelected(NOT_SELECTED_TEXT);
                    indexSelectedRobot = indexNewSelectedRobot;
                    robots.get(indexSelectedRobot).setSelected(SELECTED_TEXT);
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static void deleteAllRobots() {
        robots.clear();
        numberOfRobots = 0;
        indexSelectedRobot = null;
    }

    public static Integer getIndexSelectedRobot() {
//        if(indexSelectedRobot == null) {
//            return 0;
//        }

        return indexSelectedRobot;
    }

    public String getRoboName() {
        return roboName;
    }

    public Color getRobotColor() {
        if (this.robotColor == null) {
            return DEFAULT_ROBOT_COLOR;
        } else {
            return this.robotColor;
        }
    }

    public int getRobotNumber() {
        return roboNumber;
    }

    public int[] getPosition() {
        return position;
    }

    public void setPosition(int[] newPosition) {
        position = newPosition;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selectionText) {
        this.selected = selectionText;
    }

    @Override
    public String toString() {
        String stringPosition = "";

        for (int i = 0; i < position.length; i++) {
            stringPosition += position[i];

            if (i < position.length - 1) {
                stringPosition += ", ";
            }
        }

        return "Nr. " + roboNumber + " Name: " + roboName + " Pos: " + stringPosition;
    }

    // Roboter Interface Methods
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
        System.out.println("bumped");
        return false;
    }

    @Override
    public boolean isGoal() {
        return false;
    }

    @Override
    public void forward() {
        // TODO an Headposition anpassen
        int[] sortedPosition = this.position;
        Arrays.sort(sortedPosition);
//        for(int y = 1; y < this.getSizeY(); y++){
//            for(int x = 0; x < this.getSizeX(); x++){
//                sortedPosition[x + (y - 1)]
//            }
//        }
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
