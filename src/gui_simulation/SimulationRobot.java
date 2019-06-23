package gui_simulation;

import javafx.scene.Parent;
import javafx.scene.paint.Color;

import java.util.ArrayList;

import static gui_simulation.SimulationMaze.whatLabyrinthDoIBelongTo;

public class SimulationRobot implements Roboter {
    private static final String PREFIX_ROBO_NAME = "Robo_";
    private static final String SELECTED_TEXT = "yep";
    private static final String NOT_SELECTED_TEXT = "";
    private static final Color DEFAULT_ROBOT_COLOR = Color.rgb(55, 109, 19);

    private static ArrayList<SimulationRobot> robots = new ArrayList<>();
    // private static int numberOfRobots = 0;
    private static Integer indexSelectedRobot = null;

    // Roboter Table
    private final String robotName;
    private final int robotNumber;
    private String selectedText = "";
    // Roboter Werte
    private int sizeX;
    private int sizeY;
    private int[] position;
    private Color robotColor = null;
    // TODO Headposition setzen
    // 0 = Nord, im Uhrzeigersinn
    private Integer headDirection;
    private final int uniqueIndexNumberOfMazeRobot;

    private SimulationRobot(int robotPixelX, int robotPixelY) {
        this.sizeX = robotPixelX;
        this.sizeY = robotPixelY;
        this.headDirection = robotPixelX > robotPixelY ? 1 : 0;
        this.uniqueIndexNumberOfMazeRobot = SimulationMaze.getSelectedMaze().getAndSetUniqueIndexNumberOfMazeRobot();
        this.robotNumber = this.uniqueIndexNumberOfMazeRobot + 1;
        this.robotName = PREFIX_ROBO_NAME + this.robotNumber;
    }

    private SimulationRobot(int robotPixelX, int robotPixelY, Color robotColor) {
        this.sizeX = robotPixelX;
        this.sizeY = robotPixelY;
        this.headDirection = robotPixelX > robotPixelY ? 1 : 0;
        this.robotColor = robotColor;
        this.uniqueIndexNumberOfMazeRobot = SimulationMaze.getSelectedMaze().getAndSetUniqueIndexNumberOfMazeRobot();
        this.robotNumber = this.uniqueIndexNumberOfMazeRobot + 1;
        this.robotName = PREFIX_ROBO_NAME + this.robotNumber;
    }

    private SimulationRobot(int robotPixelX, int robotPixelY, Color robotColor, int[] position) {
        this.sizeX = robotPixelX;
        this.sizeY = robotPixelY;
        this.headDirection = robotPixelX > robotPixelY ? 1 : 0;
        this.robotColor = robotColor;
        this.position = position;
        this.uniqueIndexNumberOfMazeRobot = SimulationMaze.getSelectedMaze().getAndSetUniqueIndexNumberOfMazeRobot();
        this.robotNumber = this.uniqueIndexNumberOfMazeRobot + 1;
        this.robotName = PREFIX_ROBO_NAME + this.robotNumber;
    }

    public static Color getDefaultRobotColor() {
        return DEFAULT_ROBOT_COLOR;
    }

    public static SimulationRobot addRobot(int robotPixelX, int robotPixelY) {
        SimulationRobot newRobot = new SimulationRobot(robotPixelX, robotPixelY);
        robots.add(newRobot);

        return newRobot;
    }

    // TODO soll deprecated werden
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
                robots.get(indexSelectedRobot).setSelectedText();
                return true;
            } else {
                if (indexSelectedRobot != indexNewSelectedRobot) {
                    robots.get(indexSelectedRobot).setDeselectedText();
                    indexSelectedRobot = indexNewSelectedRobot;
                    robots.get(indexSelectedRobot).setSelectedText();
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

//    public static void deleteAllRobots() {
//        robots.clear();
//        indexSelectedRobot = null;
//    }

    public static Integer getIndexSelectedRobot() {
        return indexSelectedRobot;
    }

    public int getUniqueIndexNumberOfMazeRobot(){
        return this.uniqueIndexNumberOfMazeRobot;
    }

    public String getRobotName() {
        return robotName;
    }

    public Color getRobotColor() {
        if (this.robotColor == null) {
            return DEFAULT_ROBOT_COLOR;
        } else {
            return this.robotColor;
        }
    }

    public int getRobotNumber() {
        return robotNumber;
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

    public String getSelectedText() {
        return selectedText;
    }

    public void setSelectedText() {
        this.selectedText = SELECTED_TEXT;
    }

    public void setDeselectedText() {
        this.selectedText = NOT_SELECTED_TEXT;
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

        return "Nr. " + robotNumber + " Name: " + robotName + " Pos: " + stringPosition;
    }

    private void moveUp() {
        System.out.println("UP");
        if (Controller_MainGUI.mazeFreeFieldsUp(whatLabyrinthDoIBelongTo(this.robotNumber).getNr() - 1, this.getRobotNumber() - 1)) {
            for (int i = 0; i < this.position.length; i++) {
                this.position[i] = this.position[i] - SimulationMaze.getSelectedMaze().getMazeSizeY();
            }
        } else {
            isBumped();
        }
    }

    private void moveRight() {
        System.out.println("RIGHT");
        if (Controller_MainGUI.mazeFreeFieldsRight(whatLabyrinthDoIBelongTo(this.robotNumber).getNr() - 1, this.getRobotNumber() - 1)) {
            for (int i = 0; i < this.position.length; i++) {
                this.position[i] = this.position[i] + 1;
            }
        } else {
            isBumped();
        }
    }

    private void moveDown() {
        System.out.println("DOWN");
        if (Controller_MainGUI.mazeFreeFieldsDown(whatLabyrinthDoIBelongTo(this.robotNumber).getNr() - 1, this.getRobotNumber() - 1)) {
            for (int i = 0; i < this.position.length; i++) {
                this.position[i] = this.position[i] + SimulationMaze.getSelectedMaze().getMazeSizeY();
            }
        } else {
            isBumped();
        }
    }

    private void moveLeft() {
        System.out.println("LEFT");
        if (Controller_MainGUI.mazeFreeFieldsLeft(whatLabyrinthDoIBelongTo(this.robotNumber).getNr() - 1, this.getRobotNumber() - 1)) {
            for (int i = 0; i < this.position.length; i++) {
                this.position[i] = this.position[i] - 1;
            }
        } else {
            isBumped();
        }
    }

    public Integer getHeadDirection() {
        return this.headDirection;
    }

    public void keyboardMoveUp() {
        moveUp();
    }

    public void keyboardMoveDown() {
        moveDown();
    }

    public void keyboardMoveRight() {
        moveRight();
    }

    public void keyboardMoveLeft() {
        moveLeft();
    }

    public void keyboardRotateLeft() {
        left();
    }

    private void setPositionN(int position, int newValue) {
        this.position[position] = newValue;
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
        // TODO in Historie eintragen
        switch (this.headDirection) {
            case 0:
                moveUp();
                break;
            case 1:
                moveRight();
                break;
            case 2:
                moveDown();
                break;
            case 3:
                moveLeft();
                break;
        }
    }

    @Override
    public void backward() {
        // TODO in Historie eintragen
        switch (this.headDirection) {
            case 0:
                moveDown();
                break;
            case 1:
                moveLeft();
                break;
            case 2:
                moveUp();
                break;
            case 3:
                moveRight();
                break;
        }
    }

    @Override
    public void left() {
        System.out.println("Linksvorwärtsrotation");
        // TODO BUGGED
        int mazeNumber = whatLabyrinthDoIBelongTo(this.robotNumber).getNr() - 1;

        System.out.println("mazeNumber: " + mazeNumber + " robotNumber: " + (this.getRobotNumber() - 1));

        if (Controller_MainGUI.mazeFreeFieldsRotateLeftForward(mazeNumber, this.getRobotNumber() - 1)) {

            switch (headDirection) {
                case 0:
                    for(int y = 1, x = 0, xd = -1, yd = 0; y < this.sizeY + 1; y++, x = 0, xd = xd + this.sizeX - 2 + 1, yd = yd + this.sizeX + 2 - 1){
                        for(int xi = 0; xi < this.sizeX; xi++, x++, xd--, yd--){
                            this.position[this.sizeY * this.sizeX - y * this.sizeX + x] = this.position[this.sizeY * this.sizeX - y * this.sizeX + x] + xd + yd * SimulationMaze.getMazeFiles().get(mazeNumber).getMazeSizeY();
                        }
                    }
                    break;
                case 1:
                    for(int y = 0, x = 0, xd = 0, yd = -1; y < this.sizeY; y++, x = 0, xd = xd + this.sizeX + 1, yd = yd + this.sizeX - 1){
                        for(int xi = 0; xi < this.sizeX; xi++, x++, xd--, yd--){
                            this.position[y * (this.sizeY + 1) + x] = this.position[y * (this.sizeY + 1) + x] + xd + yd * SimulationMaze.getMazeFiles().get(mazeNumber).getMazeSizeY();
                        }
                    }
                    break;
                case 2:
                    for(int y = 1, x = 0, xd = 1, yd = 0; y < this.sizeY + 1; y++, x = 0, xd = xd - this.sizeX + 2 - 1, yd = yd - this.sizeX - 1){
                        for(int xi = 0; xi < this.sizeX; xi++, x--, yd++, xd++){
                            this.position[y * this.sizeX - 1 + x] = this.position[y * this.sizeX - 1 + x] + xd + yd * SimulationMaze.getMazeFiles().get(mazeNumber).getMazeSizeY();
                        }
                    }
                    break;
                case 3:
                    for(int y = 0, x = 0, xd = 0, yd = 1; y < this.sizeY; y++, x = 0, xd = xd - this.sizeX - 1, yd = yd - this.sizeX + 1){
                        for(int xi = 0; xi < this.sizeX; xi++, x--, xd++, yd++){
                            this.position[this.sizeY * this.sizeX - 1 - y * this.sizeX + x] = this.position[this.sizeY * this.sizeX - 1 - y * this.sizeX + x] + xd + yd * SimulationMaze.getMazeFiles().get(mazeNumber).getMazeSizeY();
                        }
                    }
                    break;
            }

            this.headDirection--;
            if (this.headDirection < 0) {
                this.headDirection = 3;
            }

            int tmpSizeX = this.sizeX;
            this.sizeX = sizeY;
            this.sizeY = tmpSizeX;
        } else {
            isBumped();
        }
    }

    @Override
    public void right() {

    }
}
