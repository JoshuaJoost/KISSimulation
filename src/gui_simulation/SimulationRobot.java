package gui_simulation;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public class SimulationRobot {
    private static final String PREFIX_ROBO_NAME = "Robo_";
    private static final String SELECTED_TEXT = "yep";
    private static final String NOT_SELECTED_TEXT = "";
    private static final Color MAZE_ROBOT_COLOR = Color.rgb(0,0,255);

    private static ArrayList<SimulationRobot> robots = new ArrayList<>();
    private static int numberOfRobots = 0;
    private static Integer indexSelectedRobot = null;

    private final String roboName;
    private final int roboNumber;
    private final int sizeX;
    private final int sizeY;
    private int[] position;
    private String selected = "";

    private SimulationRobot(int robotPixelX, int robotPixelY){
        roboName = PREFIX_ROBO_NAME + (++numberOfRobots);
        roboNumber = numberOfRobots;
        this.sizeX = robotPixelX;
        this.sizeY = robotPixelY;
    }

    public static Color getColor(){
        return MAZE_ROBOT_COLOR;
    }

    public static ArrayList<SimulationRobot> addRobot(int robotPixelX, int robotPixelY){
        robots.add(new SimulationRobot(robotPixelX, robotPixelY));

        return robots;
    }

    public static ArrayList<SimulationRobot> getRobots(){
        return robots;
    }

    public static SimulationRobot getSelectedRobot(){
        return robots.get(indexSelectedRobot);
    }

    public static boolean changeSelectedRobot(int indexNewSelectedRobot){
        if(indexNewSelectedRobot <= robots.size() - 1){
            if(indexSelectedRobot == null){
                indexSelectedRobot = indexNewSelectedRobot;
                robots.get(indexSelectedRobot).setSelected(SELECTED_TEXT);
                return true;
            } else {
                if(indexSelectedRobot != indexNewSelectedRobot) {
                    robots.get(indexSelectedRobot).setSelected(NOT_SELECTED_TEXT);
                    indexSelectedRobot = indexNewSelectedRobot;
                    robots.get(indexSelectedRobot).setSelected(SELECTED_TEXT);
                    return true;
                } else{
                    return false;
                }
            }
        }

        return false;
    }

    public String getRoboName(){
        return roboName;
    }

    public Color getMazeRobotColor(){
        return MAZE_ROBOT_COLOR;
    }

    public int getRoboNumber(){
        return roboNumber;
    }

    public int[] getPosition(){
        return position;
    }

    public void setPosition(int[] newPosition){
        position = newPosition;
    }

    public int getSizeX(){
        return sizeX;
    }

    public int getSizeY(){
        return sizeY;
    }

    public String getSelected(){
        return selected;
    }

    public void setSelected(String selectionText){
        this.selected = selectionText;
    }

    @Override
    public String toString(){
        return "Nr. " + roboNumber + " Name: " + roboName;
    }
}
