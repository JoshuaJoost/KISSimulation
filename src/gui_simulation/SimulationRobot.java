package gui_simulation;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public class SimulationRobot {

    private static ArrayList<SimulationRobot> robots = new ArrayList<>();
    private static final String prefixRoboName = "Robo";
    private static int numberOfRobots = 0;
    private static final Color mazeRobotColor = Color.rgb(0,0,255);

    private final String roboName;
    private final int roboNumber;
    private final int sizeX;
    private final int sizeY;
    private int[] position;

    private SimulationRobot(int robotPixelX, int robotPixelY, int[] position){
        roboName = prefixRoboName + (++numberOfRobots);
        roboNumber = numberOfRobots;
        this.sizeX = robotPixelX;
        this.sizeY = robotPixelY;
        this.position = position;
    }

    public String getRoboName(){
        return roboName;
    }

    public static ArrayList<SimulationRobot> getRobots(){
        return robots;
    }

    public Color getMazeRobotColor(){
        return mazeRobotColor;
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

    public static Color getColor(){
        return mazeRobotColor;
    }

    public static ArrayList<SimulationRobot> addRobot(int robotPixelX, int robotPixelY, int[] position){
        robots.add(new SimulationRobot(robotPixelX, robotPixelY, position));

        return robots;
    }

    @Override
    public String toString(){
        return "Nr. " + roboNumber + " Name: " + roboName;
    }
}
