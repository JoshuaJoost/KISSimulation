package gui_simulation;

import javafx.scene.paint.Color;

import java.util.ArrayList;

import static gui_simulation.SimulationMaze.whatLabyrinthDoIBelongTo;

public class SimulationRobot implements Roboter {
    private static final String PREFIX_ROBO_NAME = "Robo_";
    private static final String SELECTED_TEXT = "yep";
    private static final String NOT_SELECTED_TEXT = "";
    private static final Color DEFAULT_ROBOT_COLOR = Color.rgb(55, 109, 19);

    private static ArrayList<SimulationRobot> robots = new ArrayList<>();
    private static int numberOfRobots = 0;
    private static Integer indexSelectedRobot = null;

    private final String roboName;
    private final int roboNumber;
    private int sizeX;
    private int sizeY;
    private int[] position;
    private String selected = "";
    private Color robotColor = null;
    // TODO Headposition setzen
    // 0 = Nord, im Uhrzeigersinn
    private Integer headDirection = null; //1

    private SimulationRobot(int robotPixelX, int robotPixelY, int headDirection) {
        roboName = PREFIX_ROBO_NAME + (++numberOfRobots);
        roboNumber = numberOfRobots;
        this.sizeX = robotPixelX;
        this.sizeY = robotPixelY;
        this.headDirection = headDirection;
    }

    private SimulationRobot(int robotPixelX, int robotPixelY, int headDirection, Color robotColor) {
        roboName = PREFIX_ROBO_NAME + (++numberOfRobots);
        roboNumber = numberOfRobots;
        this.sizeX = robotPixelX;
        this.sizeY = robotPixelY;
        this.headDirection = headDirection;
        this.robotColor = robotColor;
    }

    private SimulationRobot(int robotPixelX, int robotPixelY, int headDirection, Color robotColor, int[] position) {
        roboName = PREFIX_ROBO_NAME + (++numberOfRobots);
        roboNumber = numberOfRobots;
        this.sizeX = robotPixelX;
        this.sizeY = robotPixelY;
        this.headDirection = headDirection;
        this.robotColor = robotColor;
        this.position = position;
    }

    public static Color getDefaultRobotColor() {
        return DEFAULT_ROBOT_COLOR;
    }

    public static ArrayList<SimulationRobot> addRobot(int robotPixelX, int robotPixelY, int headDirection) {
        robots.add(new SimulationRobot(robotPixelX, robotPixelY, headDirection));

        return robots;
    }

    public static ArrayList<SimulationRobot> addRobot(int robotPixelX, int robotPixelY, int headDirection, Color robotColor, int[] position) {
        robots.add(new SimulationRobot(robotPixelX, robotPixelY, headDirection, robotColor, position));
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

    private void moveUp() {
        System.out.println("UP");
        if (Controller_MainGUI.mazeFreeFieldsUp(whatLabyrinthDoIBelongTo(this.roboNumber).getNr() - 1, this.getRobotNumber() - 1)) {
            for (int i = 0; i < this.position.length; i++) {
                this.position[i] = this.position[i] - SimulationMaze.getSelectedMaze().getMazeSizeY();
            }
        } else {
            isBumped();
        }
    }

    private void moveRight() {
        System.out.println("RIGHT");
        if (Controller_MainGUI.mazeFreeFieldsRight(whatLabyrinthDoIBelongTo(this.roboNumber).getNr() - 1, this.getRobotNumber() - 1)) {
            for (int i = 0; i < this.position.length; i++) {
                this.position[i] = this.position[i] + 1;
            }
        } else {
            isBumped();
        }
    }

    private void moveDown() {
        System.out.println("DOWN");
        if (Controller_MainGUI.mazeFreeFieldsDown(whatLabyrinthDoIBelongTo(this.roboNumber).getNr() - 1, this.getRobotNumber() - 1)) {
            for (int i = 0; i < this.position.length; i++) {
                this.position[i] = this.position[i] + SimulationMaze.getSelectedMaze().getMazeSizeY();
            }
        } else {
            isBumped();
        }
    }

    private void moveLeft() {
        System.out.println("LEFT");
        if (Controller_MainGUI.mazeFreeFieldsLeft(whatLabyrinthDoIBelongTo(this.roboNumber).getNr() - 1, this.getRobotNumber() - 1)) {
            for (int i = 0; i < this.position.length; i++) {
                this.position[i] = this.position[i] - 1;
            }
        } else {
            isBumped();
        }
    }

    public Integer getHeadDirection(){
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
    // Nur für 3x4 oder 4x3 Roboter ausgelegt
    public void left() {
        System.out.println("ROTATE LEFT");
        int mazeNumber = whatLabyrinthDoIBelongTo(this.roboNumber).getNr() - 1;
        if(Controller_MainGUI.mazeFreeFieldsRotateLeftForward(mazeNumber, this.getRobotNumber() - 1)){
            int x = 0;
            int y = 0;
            switch(this.headDirection){
                case 0:
                    x = -4;
                    y = 3;
                    break;
                case 1:
                    x = 0;
                    y = -1;
                    break;
                case 2:
                    x = 3;
                    y = 2;
                    break;
                case 3:
                    x = 1;
                    y = 6;
                    break;
            }

            this.headDirection--;
            if(this.headDirection < 0){
                this.headDirection = 3;
            }

            for(int i = 0; i < this.position.length; i++, x--, y--){
                this.position[i] = this.position[i] + x + y * SimulationMaze.getMazeFiles().get(mazeNumber).getMazeSizeY();

                if(i > 0 && (i + 1) % this.getSizeX() == 0){
                    x = x + this.sizeX + 1;
                    y = y + this.sizeX - 2 + 1;
                }
            }


            int tmpSizeX = this.sizeX;
            this.sizeX = sizeY;
            this.sizeY = tmpSizeX;
        } else{
            isBumped();
        }
    }

    @Override
    public void right() {

    }
}
