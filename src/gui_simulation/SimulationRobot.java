package gui_simulation;

import javafx.scene.paint.Color;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class SimulationRobot implements Roboter {
    private static final String PREFIX_ROBO_NAME = "Robo_";
    private static final String SELECTED_TEXT = "ja";
    private static final String NOT_SELECTED_TEXT = "";
    private static final Color DEFAULT_ROBOT_BODY_COLOR = Color.rgb(55, 109, 19);
    private static final Color DEFAULT_ROBOT_HEAD_COLOR = Color.rgb(255, 0, 0);
    private static final Color DEFAULT_MEASURE_DISTANCE = Color.rgb(255, 255, 0);
    private static final int MIN_DISTANCE = 1;

    // Roboter Bewegung
    public static final int DRIVE_FORWARD = 0;
    public static final int DRIVE_ROTATE_LEFT = 1;
    public static final int DRIVE_ROTATE_RIGHT = 2;
    public static final int DRIVE_BACKWARD = 3;
    public static String movementHistorie = "";

    //Rewards - Learning Algorithmus
    private static final double REWARD_BUMPED = -1;
    private static final double REWARD_DRIVE_BACK = 0;
    private static final double REWARD_DRIVE_FORWARD = 0;
    private static final double REWARD_DRIVE_ROTATE_RIGHT = 0;
    private static final double REWARD_DRIVE_ROTATE_LEFT = 0;
    private static final double REWARD_DRIVE_TARGET = 1;

    // Bewegungskontrolle
    private boolean isBumped = false;

    private int drived_forward = 0;
    private int drived_backward = 0;
    private int drived_rotateLeft = 0;
    private int drived_rotateRight = 0;
    private int drived_look = 0;

    private int try_drived_forward = 0;
    private int try_drived_backward = 0;
    private int try_drived_rotateLeft = 0;
    private int try_drived_rotateRight = 0;

    // Zustände - Mögliche Bewegungen
    // vorne | hinten | Rotation links | Rotation rechts
    private int state0000 = 0;
    private int state0001 = 0;
    private int state0010 = 0;
    private int state0011 = 0;
    private int state0100 = 0;
    private int state0101 = 0;
    private int state0110 = 0;
    private int state0111 = 0;
    private int state1000 = 0;
    private int state1001 = 0;
    private int state1010 = 0;
    private int state1011 = 0;
    private int state1100 = 0;
    private int state1101 = 0;
    private int state1110 = 0;
    private int state1111 = 0;

//    private int stateCanRotateRight = 0;
//    private int stateCanRotateLeft = 0;
//    private int stateCanRotateLeftRight = 0;
//    private int stateFront = 0;
//    private int stateFrontLeft = 0;
//    private int stateFrontRight = 0;
//    private int stateLeft = 0;
//    private int stateRight = 0;
//    private int stateLeftRight = 0;
//    private int stateFrontLeftRight = 0;
//    private int stateBumped = 0;
//    private int stateNoBarrier = 0;
//    private int stateBumpedFront = 0;
//    private int stateBumpedLeft = 0;
//    private int stateBumpedRight = 0;
//    private int stateBumpedFrontLeft = 0;
//    private int stateBumpedFrontRight = 0;
//    private int stateBumpedFrontLeftRight = 0;
//    private int stateBumpedLeftRight = 0;
//    private int stateBumpedCanRotateRight = 0;
//    private int stateBumpedCanRotateLeft = 0;
//    private int stateBumpedCanRotateLeftRight = 0;

    // Roboter Table
    private final String robotName;
    private final int robotNumber;
    private String selectedText = "";

    // Roboter Werte
    private int sizeX;
    private int sizeY;
    private int[] position;
    private int[] startPosition;
    private Color robotBodyColor = null;
    private Color robotHeadColor = null;
    private Color measureDistanceColor = null;
    private Integer headDirection; // 0 = Nord, im Uhrzeigersinn
    private Integer startHeadDirection;
    private ArrayList<Integer> headPosition = new ArrayList<>();
    private final int headSize;
    private final int uniqueIndexNumberOfMazeRobot;
    private final int robotMazeIndexNumber;
    private int[] distanceData = new int[3]; // distanceData[EntfernungLinks, EntfernungVorne, EntfernungRechts]
    private ArrayList<Integer> distanceDataFieldsLeft = new ArrayList<>();
    private ArrayList<Integer> distanceDataFieldsFront = new ArrayList<>();
    private ArrayList<Integer> distanceDataFieldsRight = new ArrayList<>();
    // QLearningAgent
    private QLearningAgentMovement lerningAlgorithmus = new QLearningAgentMovement();
    private double reward = 0;

    private SimulationRobot(int robotPixelX, int robotPixelY, int[] position) {
        this.sizeX = robotPixelX;
        this.sizeY = robotPixelY;
        this.position = position;

        ArrayList<Integer> tmpPositions = new ArrayList<>();
        for (int i : this.position) {
            tmpPositions.add(i);
        }

        this.startPosition = OwnUtils.convertArrayListToIntArray(tmpPositions);
        this.headDirection = robotPixelX > robotPixelY ? 1 : 0;

        int headDircetionTmp = this.headDirection;
        this.startHeadDirection = headDircetionTmp;

        this.uniqueIndexNumberOfMazeRobot = SimulationMaze.getSelectedMaze().getAndSetUniqueIndexNumberOfMazeRobot();
        this.robotNumber = this.uniqueIndexNumberOfMazeRobot + 1;
        this.robotName = PREFIX_ROBO_NAME + this.robotNumber;
        this.robotMazeIndexNumber = SimulationMaze.getSelectedMazeIndexNumber();
        this.headSize = 1;

        changeHeadPosition();
    }

    public static SimulationRobot addRobot(int robotPixelX, int robotPixelY, int[] position) {
        return new SimulationRobot(robotPixelX, robotPixelY, position);
    }

    public void callGuiUpdateFunction() {
        if (SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getNr() == SimulationMaze.getSelectedMaze().getNr()) {
            if (this.getUniqueIndexNumberOfMazeRobot() == SimulationMaze.getSelectedMaze().getSelectedRobot().getUniqueIndexNumberOfMazeRobot()) {
                SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getFXML_MAIN_CONTROLLER().updateMaze(true);
            }
        }
    }

    public ArrayList<Integer> getDistanceDataFieldsLeft() {
        return this.distanceDataFieldsLeft;
    }

    public ArrayList<Integer> getDistanceDataFieldsFront() {
        return this.distanceDataFieldsFront;
    }

    public ArrayList<Integer> getDistanceDataFieldsRight() {
        return this.distanceDataFieldsRight;
    }

    public ArrayList<Integer> getHeadPosition() {
        return this.headPosition;
    }

    public int getUniqueIndexNumberOfMazeRobot() {
        return this.uniqueIndexNumberOfMazeRobot;
    }

    public Color getRobotBodyColor() {
        if (this.robotBodyColor == null) {
            return DEFAULT_ROBOT_BODY_COLOR;
        } else {
            return this.robotBodyColor;
        }
    }

    public Color getRobotHeadColor() {
        if (this.robotHeadColor == null) {
            return DEFAULT_ROBOT_HEAD_COLOR;
        } else {
            return this.robotHeadColor;
        }
    }

    public Color getMeasureDistanceColor() {
        if (this.measureDistanceColor == null) {
            return DEFAULT_MEASURE_DISTANCE;
        } else {
            return this.measureDistanceColor;
        }
    }

    public int getRobotMazeIndexNumber() {
        return this.robotMazeIndexNumber;
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

    public void setSelectedText() {
        this.selectedText = SELECTED_TEXT;
    }

    public void setDeselectedText() {
        this.selectedText = NOT_SELECTED_TEXT;
    }

    private void changeHeadPosition() {
        this.headPosition.clear();
        int[] sortedPos = this.position;
        Arrays.sort(sortedPos);

        if (this.sizeX > 0 && this.sizeY > 0) {
            switch (this.headDirection) {
                case 0:
//                    if (this.headSize > 0) {
//                        if (this.sizeX == 1) {
//                            this.headPosition.add(sortedPos[0]);
//                        } else if (this.sizeX == 2) {
//                            this.headPosition.add(sortedPos[1]);
//                        } else {
//                            for (int x = 0; x < this.headSize; x++) {
//                                this.headPosition.add(sortedPos[this.sizeX - this.headSize - 1 + x]);
//                            }
//                        }
//                    }
                    this.headPosition.add(sortedPos[1]);
                    break;
                case 1:
                    if (this.headSize > 0) {
                        if (this.sizeY == 1 || this.sizeY == 2) {
                            this.headPosition.add(sortedPos[this.sizeX * this.sizeY - 1]);
                        } else {
                            for (int y = 2; y < this.headSize + 2; y++) {
                                this.headPosition.add(sortedPos[y * this.sizeX - 1]);
                            }
                        }
                    }
                    break;
                case 2:
                    if (this.headSize > 0) {
                        if (this.sizeX == 1) {
                            this.headPosition.add(sortedPos[this.sizeY - 1]);
                        } else if (this.sizeX == 2) {
                            this.headPosition.add(sortedPos[this.sizeX * this.sizeY - 1 - 1]);
                        } else {
                            for (int x = 0; x < this.headSize; x++) {
                                this.headPosition.add(sortedPos[this.sizeX * this.sizeY - 1 - this.headSize + x]);
                            }
                        }
                    }
                    break;
                case 3:
                    if (this.headSize > 0) {
                        if (this.sizeY == 1 || this.sizeY == 2) {
                            this.headPosition.add(sortedPos[0]);
                        } else {
                            for (int y = 1; y < this.headSize + 1; y++) {
                                this.headPosition.add(sortedPos[y * this.sizeX]);
                            }
                        }
                    }
                    break;
            }
        }
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
        clearDistanceData();
        for (int i = 0; i < this.position.length; i++) {
            this.position[i] = this.position[i] - SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY();
        }

        changeHeadPosition();
    }

    private void moveRight() {
        clearDistanceData();
        for (int i = 0; i < this.position.length; i++) {
            this.position[i] = this.position[i] + 1;
        }

        changeHeadPosition();
    }

    private void moveDown() {
        clearDistanceData();
        for (int i = 0; i < this.position.length; i++) {
            this.position[i] = this.position[i] + SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY();
        }

        changeHeadPosition();
    }

    private void moveLeft() {
        clearDistanceData();
        for (int i = 0; i < this.position.length; i++) {
            this.position[i] = this.position[i] - 1;
        }

        changeHeadPosition();
    }

    public Integer getHeadDirection() {
        return this.headDirection;
    }

    public void keyboardMoveUp() {
        if (SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).aboveFree(this)) {
            moveUp();
        }
    }

    public void keyboardMoveDown() {
        if (SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).belowFree(this)) {
            moveDown();
        }
    }

    public void keyboardMoveRight() {
        if (SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).rightFree(this)) {
            moveRight();
        }
    }

    public void keyboardMoveLeft() {
        if (SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).leftFree(this)) {
            moveLeft();
        }
    }

    public void keyboardRotateForwardLeft() {
        left();
    }

    public void keyboardRotateForwardRight() {
        right();
    }

    public void keyboardLook() {
        look();
    }

    private ArrayList<Integer> getDistanceDataAbove(int startValue) {
        ArrayList<Integer> distanceDataAbove = new ArrayList<>();

        boolean freeFields = true;
        int y = startValue;
        while (freeFields) {

            if (SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getIndexMazeFreeFields().contains(this.headPosition.get(0) + y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY())) {
                distanceDataAbove.add(this.headPosition.get(0) + y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY());
            } else {
                freeFields = false;
            }
            y--;
        }

        return distanceDataAbove;
    }

    private ArrayList<Integer> getDistanceDataRight(int startValue) {
        ArrayList<Integer> distanceDataRight = new ArrayList<>();

        boolean freeField = true;
        int x = startValue;
        while (freeField) {
            if (SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getIndexMazeFreeFields().contains(this.headPosition.get(0) + x)) {
                distanceDataRight.add(this.headPosition.get(0) + x);
            } else {
                freeField = false;
            }
            x++;
        }

        return distanceDataRight;
    }

    private ArrayList<Integer> getDistanceDataBelow(int startValue) {
        ArrayList<Integer> distanceDataBelow = new ArrayList<>();

        boolean freeFields = true;
        int y = startValue;
        while (freeFields) {
            if (SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getIndexMazeFreeFields().contains(this.headPosition.get(0) - y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY())) {
                distanceDataBelow.add(this.headPosition.get(0) - y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY());
            } else {
                freeFields = false;
            }
            y--;
        }

        return distanceDataBelow;
    }

    private ArrayList<Integer> getDistanceDataLeft(int starValue) {
        ArrayList<Integer> distanceDataLeft = new ArrayList<>();

        boolean freeFields = true;
        int x = starValue;
        while (freeFields) {
            if (SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getIndexMazeFreeFields().contains(this.headPosition.get(0) + x)) {
                distanceDataLeft.add(this.headPosition.get(0) + x);
            } else {
                freeFields = false;
            }
            x--;
        }

        return distanceDataLeft;
    }

    public void clearDistanceData() {
        this.distanceData[0] = -1;
        this.distanceData[1] = -1;
        this.distanceData[2] = -1;

        this.distanceDataFieldsLeft.clear();
        this.distanceDataFieldsFront.clear();
        this.distanceDataFieldsRight.clear();
    }

    public boolean targetReached() {
        boolean reachedTarget = false;

        for (int i = 0; i < this.position.length; i++) {
            for (int j = 0; j < SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getIndexMazeTargetFields().size(); j++) {
                if (this.position[i] == SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getIndexMazeTargetFields().get(j)) {
                    reachedTarget = true;
                }
            }
        }

        return reachedTarget;
    }

    private void setRobotBackToStartPosition(){
        for(int i = 0; i < this.startPosition.length; i++){
            this.position[i] = this.startPosition[i];
        }

        int tmpHeadDirection = this.startHeadDirection;
        this.headDirection = tmpHeadDirection;

        changeHeadPosition();

        switch(this.headDirection){
            case 0:
            case 2:
                this.sizeX = 3;
                this.sizeY = 4;
                break;
            case 1:
            case 3:
                this.sizeX = 4;
                this.sizeY = 3;
                break;
        }
    }

    public void start() {
        // Setze aktuelle Position zu Startpositon, so wird Position nach Tastaturbewegung zu Startposition
        for(int i = 0; i < this.position.length; i++){
            this.startPosition[i] = this.position[i];
        }

        this.drived_look = 0;
        this.drived_rotateLeft = 0;
        this.drived_backward = 0;
        this.drived_rotateRight = 0;
        this.drived_forward = 0;
        this.try_drived_forward = 0;
        this.try_drived_backward = 0;
        this.try_drived_rotateLeft = 0;
        this.try_drived_rotateRight = 0;

        this.state0000 = 0;
        this.state0001 = 0;
        this.state0010 = 0;
        this.state0011 = 0;
        this.state0100 = 0;
        this.state0101 = 0;
        this.state0110 = 0;
        this.state0111 = 0;
        this.state1000 = 0;
        this.state1001 = 0;
        this.state1010 = 0;
        this.state1011 = 0;
        this.state1100 = 0;
        this.state1101 = 0;
        this.state1110 = 0;
        this.state1111 = 0;

        // Test QTabel
        System.out.println("---------------------------Lernphase 1 - Epsilon Lernen--------------------------");
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("---------------------------------------------------------------------------------");
        this.lerningAlgorithmus.epsilon = 100;
        for(int j = 0; j < 100; j++) {
            System.out.println("Iteration: " + j);
            setRobotBackToStartPosition();

            boolean foundTarget = false;
            for (int i = 0; i < 10000 && !foundTarget; i++) {
                look();
                int s = findBarrier();
                int a = this.lerningAlgorithmus.chooseAction(s);
                doAction(a);

                foundTarget = targetReached();
                if (foundTarget) {
                    this.reward = SimulationRobot.REWARD_DRIVE_TARGET;
                }

                look();
                int sNext = findBarrier();
                this.lerningAlgorithmus.learn(s, sNext, a, reward);
            }

            if(foundTarget){
                System.out.println("Target found!");
            } else{
                System.out.println("Target -not- found");
            }
        }

        lerningAlgorithmus.printQTable();

        System.out.println("---------------------------Lernphase 2 - QTable  Lernen--------------------------");
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("---------------------------------------------------------------------------------");
        this.lerningAlgorithmus.epsilon = 0;
        for(int j = 0, e = 0; j < 15; j++, e++) {
            System.out.println("Iteration: " + j);
            setRobotBackToStartPosition();
            this.lerningAlgorithmus.epsilon -= e;
            if(this.lerningAlgorithmus.epsilon < 0){
                this.lerningAlgorithmus.epsilon = 0;
            }

            boolean foundTarget = false;
            for (int i = 0; i < 10000 && !foundTarget; i++) {
                look();
                int s = findBarrier();
                int a = this.lerningAlgorithmus.chooseAction(s);
                doAction(a);

                foundTarget = targetReached();
                if (foundTarget) {
                    this.reward = SimulationRobot.REWARD_DRIVE_TARGET;
                }

                look();
                int sNext = findBarrier();
                this.lerningAlgorithmus.learn(s, sNext, a, reward);
            }

            if(foundTarget){
                System.out.println("Target found!");
            } else{
                System.out.println("Target -not- found");
            }
        }

        //Probedurchlauf
        System.out.println("--------------------------Probedurchlauf------------------------------");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("----------------------------------------------------------------------");
        this.drived_look = 0;
        this.drived_rotateLeft = 0;
        this.drived_backward = 0;
        this.drived_rotateRight = 0;
        this.drived_forward = 0;
        this.try_drived_forward = 0;
        this.try_drived_backward = 0;
        this.try_drived_rotateLeft = 0;
        this.try_drived_rotateRight = 0;

        this.state0000 = 0;
        this.state0001 = 0;
        this.state0010 = 0;
        this.state0011 = 0;
        this.state0100 = 0;
        this.state0101 = 0;
        this.state0110 = 0;
        this.state0111 = 0;
        this.state1000 = 0;
        this.state1001 = 0;
        this.state1010 = 0;
        this.state1011 = 0;
        this.state1100 = 0;
        this.state1101 = 0;
        this.state1110 = 0;
        this.state1111 = 0;


        setRobotBackToStartPosition();
        this.lerningAlgorithmus.epsilon = 0;

        boolean foundTarget = false;
        int j = 0;
        for(int i = 0; i < 10000 && !foundTarget; i++, j++){
            look();
            int s = findBarrier();
            int a = this.lerningAlgorithmus.chooseAction(s);
            doAction(a);

            foundTarget = targetReached();
            if (foundTarget) {
                this.reward = SimulationRobot.REWARD_DRIVE_TARGET;
            }

            look();
            int sNext = findBarrier();
            this.lerningAlgorithmus.learn(s, sNext, a, reward);
        }
        callGuiUpdateFunction();

        System.out.println("Bewegungsmuster:");
        System.out.println("vorwärts: " + this.drived_forward);
        System.out.println("vorwärts angestoßen: " + this.try_drived_forward);
        System.out.println("Zurück:" + this.drived_backward);
        System.out.println("hinten angestoßen: " + this.try_drived_backward);
        System.out.println("Rechtsrotation: " + this.drived_rotateRight);
        System.out.println("Bei Rechtsrotation angestoßen: " + this.try_drived_rotateRight);
        System.out.println("Linksrotation: " + this.drived_rotateLeft);
        System.out.println("Bei Linksrotation angestoßen: " + this.try_drived_rotateLeft);
        System.out.println("Iterationen: " + j);
        System.out.println("Angestoßen: " + (this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateRight + this.try_drived_rotateLeft));
        System.out.println();
        System.out.println("Statusmuster:");
        System.out.println("0000: " + this.state0000);
        System.out.println("0001: " + this.state0001);
        System.out.println("0010: " + this.state0010);
        System.out.println("0011: " + this.state0011);
        System.out.println("0100: " + this.state0100);
        System.out.println("0101: " + this.state0101);
        System.out.println("0110: " + this.state0110);
        System.out.println("0111: " + this.state0111);
        System.out.println("1000: " + this.state1000);
        System.out.println("1001: " + this.state1001);
        System.out.println("1010: " + this.state1010);
        System.out.println("1011: " + this.state1011);
        System.out.println("1100: " + this.state1100);
        System.out.println("1101: " + this.state1101);
        System.out.println("1110: " + this.state1110);
        System.out.println("1111: " + this.state1111);
        System.out.println("Angestoßen: " + (this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateRight + this.try_drived_rotateLeft));
        System.out.println("Fehlerquote: " + ((this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateRight + this.try_drived_rotateLeft) * 100 / j) + "%");

//        System.out.println("----------------------------------------------------------");
//        foundTarget = false;
//        for (int i = 0; i < 10000 && !foundTarget; i++) {
//            //System.out.println("i: " + i);
//            look();
//            int s = findBarrier();
//            int a = this.lerningAlgorithmus.chooseAction(s);
//            doAction(a);
//
//            foundTarget = targetReached();
//            if(foundTarget){
//                this.reward = SimulationRobot.REWARD_DRIVE_TARGET;
//            }
//
//            look();
//            int sNext = findBarrier();
//            this.lerningAlgorithmus.learn(s, sNext, a, reward);
//        }
//
//        this.lerningAlgorithmus.printQTable();
//        callGuiUpdateFunction();



        // Probedurchlauf
        //------------------------------------------------------------------------
//        this.drived_look = 0;
//        this.drived_rotateLeft = 0;
//        this.drived_backward = 0;
//        this.drived_rotateRight = 0;
//        this.drived_forward = 0;
//        this.try_drived_forward = 0;
//        this.try_drived_backward = 0;
//        this.try_drived_rotateLeft = 0;
//        this.try_drived_rotateRight = 0;
//
//        this.state0000 = 0;
//        this.state0001 = 0;
//        this.state0010 = 0;
//        this.state0011 = 0;
//        this.state0100 = 0;
//        this.state0101 = 0;
//        this.state0110 = 0;
//        this.state0111 = 0;
//        this.state1000 = 0;
//        this.state1001 = 0;
//        this.state1010 = 0;
//        this.state1011 = 0;
//        this.state1100 = 0;
//        this.state1101 = 0;
//        this.state1110 = 0;
//        this.state1111 = 0;
//
//        boolean targetReached = false;
//        int i = 0;
//
//        ArrayList<Integer> tmpPositions = new ArrayList<>();
//        for(int z : this.startPosition){
//            tmpPositions.add(z);
//        }
//        this.position = OwnUtils.convertArrayListToIntArray(tmpPositions);
//
//        int j = 0;
//        while (j < 1000) {
//            j++;
//            look();
//            int s = findBarrier();
//            int a = this.lerningAlgorithmus.chooseAction(s);
//            doAction(a);
//
//            targetReached = targetReached();
//            if(targetReached){
//                this.reward = SimulationRobot.REWARD_DRIVE_TARGET;
//            }
//
//            look();
//            int sNext = findBarrier();
//            this.lerningAlgorithmus.learn(s, sNext, a, reward);
//            i++;
//
//            String roboPos = "";
//            for (int pos : this.position) {
//                roboPos += pos + "-";
//            }
//            String targetPos = "";
//            for (int tPos : SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getIndexMazeTargetFields()) {
//                targetPos += tPos + "-";
//            }
//            System.out.println("I: " + i + " rPos: " + roboPos + " tPos: " + targetPos);
//        }
//        System.out.println("Iterationen: " + i);
//
//        System.out.println("Bewegungsmuster:");
//        System.out.println("vorwärts: " + this.drived_forward);
//        System.out.println("vorwärts angestoßen: " + this.try_drived_forward);
//        System.out.println("Zurück:" + this.drived_backward);
//        System.out.println("hinten angestoßen: " + this.try_drived_backward);
//        System.out.println("Rechtsrotation: " + this.drived_rotateRight);
//        System.out.println("Bei Rechtsrotation angestoßen: " + this.try_drived_rotateRight);
//        System.out.println("Linksrotation: " + this.drived_rotateLeft);
//        System.out.println("Bei Linksrotation angestoßen: " + this.try_drived_rotateLeft);
//        System.out.println("Geschaut: " + this.drived_look);
//        System.out.println("Angestoßen: " + (this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateRight + this.try_drived_rotateLeft));
//        System.out.println();
//        System.out.println("Statusmuster:");
//        System.out.println("0000: " + this.state0000);
//        System.out.println("0001: " + this.state0001);
//        System.out.println("0010: " + this.state0010);
//        System.out.println("0011: " + this.state0011);
//        System.out.println("0100: " + this.state0100);
//        System.out.println("0101: " + this.state0101);
//        System.out.println("0110: " + this.state0110);
//        System.out.println("0111: " + this.state0111);
//        System.out.println("1000: " + this.state1000);
//        System.out.println("1001: " + this.state1001);
//        System.out.println("1010: " + this.state1010);
//        System.out.println("1011: " + this.state1011);
//        System.out.println("1100: " + this.state1100);
//        System.out.println("1101: " + this.state1101);
//        System.out.println("1110: " + this.state1110);
//        System.out.println("1111: " + this.state1111);
//        System.out.println("Angestoßen: " + (this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateRight + this.try_drived_rotateLeft));
//        System.out.println("Durchschnittliche Anstöße: " + this.averageImpulses);
//
//        this.lerningAlgorithmus.printQTable();
//        callGuiUpdateFunction();
        //-------------------------------------------------------------------------------------------------
        // Ende Probedurchlauf

        //OneKlick Bewegung

//        System.err.println("-------------------------------------------------------------------------");
//        System.err.println("-------------------------------------------------------------------------");
//        for (int k = 0; k < 100; k++) {
//            SimulationRobot.movementHistorie += "--------------------------------- " + k + " ---------------------------------";
//            System.err.println(lerningAlgorithmus.epsilon);
//            long startTime = System.currentTimeMillis();
//
//            ArrayList<Integer> tmpPositions = new ArrayList<>();
//            for (int i : this.startPosition) {
//                tmpPositions.add(i);
//            }
//            this.position = OwnUtils.convertArrayListToIntArray(tmpPositions);
//
//            int i = 0;
//            boolean targetReached = false;
//            while (!targetReached) {
//                i++;
//                if (i % 100000 == 0) {
//                    System.out.println("I: " + i);
//                    System.out.println("Statusmuster:");
//                    System.out.println("0000: " + this.state0000);
//                    System.out.println("0001: " + this.state0001);
//                    System.out.println("0010: " + this.state0010);
//                    System.out.println("0011: " + this.state0011);
//                    System.out.println("0100: " + this.state0100);
//                    System.out.println("0101: " + this.state0101);
//                    System.out.println("0110: " + this.state0110);
//                    System.out.println("0111: " + this.state0111);
//                    System.out.println("1000: " + this.state1000);
//                    System.out.println("1001: " + this.state1001);
//                    System.out.println("1010: " + this.state1010);
//                    System.out.println("1011: " + this.state1011);
//                    System.out.println("1100: " + this.state1100);
//                    System.out.println("1101: " + this.state1101);
//                    System.out.println("1110: " + this.state1110);
//                    System.out.println("1111: " + this.state1111);
//                    lerningAlgorithmus.printQTable();
//                    System.out.println("-----------------------------------------------------");
//                }
//                look();
//                int s = findBarrier();
//                int a = this.lerningAlgorithmus.chooseAction(s);
//                doAction(a);
//
//                targetReached = targetReached();
//                if (targetReached) {
//                    this.reward = SimulationRobot.REWARD_DRIVE_TARGET;
//                }
//
//                look();
//                int sNext = findBarrier();
//                this.lerningAlgorithmus.learn(s, sNext, a, reward);
//
//                String tableAfterAction = " \t => | ";
//                for (double row : lerningAlgorithmus.q[s]) {
//                    tableAfterAction += row + " | ";
//                }
//
//                SimulationRobot.movementHistorie += tableAfterAction;
//                SimulationRobot.movementHistorie += " ---- " + this.state0000;
//                SimulationRobot.movementHistorie += System.lineSeparator();
//
//                ////
//                FileWriter fw;
//                BufferedWriter bw;
//                try {
//                    fw = new FileWriter(System.getProperty("user.dir") + "\\src\\gui_simulation\\" + "learning_variables2");
//                    bw = new BufferedWriter(fw);
//
//                    bw.write(SimulationRobot.movementHistorie);
//
//                    bw.close();
//                    fw.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                ////
//            }
//
//            System.out.println("Ziel erreicht bei Iteration: " + k + " in " + ((System.currentTimeMillis() - startTime) / 1000) + "s");
//            lerningAlgorithmus.printQTable();
//        }
//        callGuiUpdateFunction();
//
//        System.out.println("Fertig!");
//        System.out.println("forward \t backward \t rotLeft \t rotRight");
//        lerningAlgorithmus.printQTable();
//
//        // Probedurchlauf
//        System.out.println("Probedurchlauf wird gestartet");
//        this.drived_look = 0;
//        this.drived_rotateLeft = 0;
//        this.drived_backward = 0;
//        this.drived_rotateRight = 0;
//        this.drived_forward = 0;
//        this.try_drived_forward = 0;
//        this.try_drived_backward = 0;
//        this.try_drived_rotateLeft = 0;
//        this.try_drived_rotateRight = 0;
//
//        this.state0000 = 0;
//        this.state0001 = 0;
//        this.state0010 = 0;
//        this.state0011 = 0;
//        this.state0100 = 0;
//        this.state0101 = 0;
//        this.state0110 = 0;
//        this.state0111 = 0;
//        this.state1000 = 0;
//        this.state1001 = 0;
//        this.state1010 = 0;
//        this.state1011 = 0;
//        this.state1100 = 0;
//        this.state1101 = 0;
//        this.state1110 = 0;
//        this.state1111 = 0;
//
//        ArrayList<Integer> tmpPositions = new ArrayList<>();
//        for (int i : this.startPosition) {
//            tmpPositions.add(i);
//        }
//        this.position = OwnUtils.convertArrayListToIntArray(tmpPositions);
//
//        boolean targetReached = false;
//        while (!targetReached) {
//            look();
//            int s = findBarrier();
//            int a = this.lerningAlgorithmus.chooseAction(s);
//            doAction(a);
//
//            targetReached = targetReached();
//            if (targetReached) {
//                this.reward = SimulationRobot.REWARD_DRIVE_TARGET;
//            }
//
//            look();
//            int sNext = findBarrier();
//            this.lerningAlgorithmus.learn(s, sNext, a, reward);
//
//            String tableAfterAction = " \t => | ";
//            for (double row : lerningAlgorithmus.q[s]) {
//                tableAfterAction += row + " | ";
//            }
//
//            // 0 Forward, 1 RLeft, 2 RRight, 3 Backward
//            System.out.println(tableAfterAction);
//        }
//
//        callGuiUpdateFunction();
//
//        System.out.println("Ziel erreicht");
//        System.out.println("forward \t backward \t rotLeft \t rotRight");
//        lerningAlgorithmus.printQTable();
//
//        System.out.println("Bewegungsmuster:");
//        System.out.println("vorwärts: " + this.drived_forward);
//        System.out.println("vorwärts angestoßen: " + this.try_drived_forward);
//        System.out.println("Zurück:" + this.drived_backward);
//        System.out.println("hinten angestoßen: " + this.try_drived_backward);
//        System.out.println("Rechtsrotation: " + this.drived_rotateRight);
//        System.out.println("Bei Rechtsrotation angestoßen: " + this.try_drived_rotateRight);
//        System.out.println("Linksrotation: " + this.drived_rotateLeft);
//        System.out.println("Bei Linksrotation angestoßen: " + this.try_drived_rotateLeft);
//        System.out.println("Geschaut: " + this.drived_look);
//        System.out.println("Angestoßen: " + (this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateRight + this.try_drived_rotateLeft));
//        System.out.println();
//        System.out.println("Statusmuster:");
//        System.out.println("0000: " + this.state0000);
//        System.out.println("0001: " + this.state0001);
//        System.out.println("0010: " + this.state0010);
//        System.out.println("0011: " + this.state0011);
//        System.out.println("0100: " + this.state0100);
//        System.out.println("0101: " + this.state0101);
//        System.out.println("0110: " + this.state0110);
//        System.out.println("0111: " + this.state0111);
//        System.out.println("1000: " + this.state1000);
//        System.out.println("1001: " + this.state1001);
//        System.out.println("1010: " + this.state1010);
//        System.out.println("1011: " + this.state1011);
//        System.out.println("1100: " + this.state1100);
//        System.out.println("1101: " + this.state1101);
//        System.out.println("1110: " + this.state1110);
//        System.out.println("1111: " + this.state1111);
//        System.out.println("Angestoßen: " + (this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateRight + this.try_drived_rotateLeft));
//        System.out.println("Durchschnittliche Anstöße: " + this.averageImpulses);


        // Lerndurchlauf
//        long startTime = System.currentTimeMillis();
//        double deltaE = 0.00;
//        int runs = 100;
//        for (int j = 0; j < runs; j++, deltaE += 0.01) {
//            ArrayList<Integer> tmpPositions = new ArrayList<>();
//            for(int i : this.startPosition){
//                tmpPositions.add(i);
//            }
//            this.position = OwnUtils.convertArrayListToIntArray(tmpPositions);
//
//            lerningAlgorithmus.epsilon -= deltaE;
//
//            if (lerningAlgorithmus.epsilon < 0) {
//                lerningAlgorithmus.epsilon = 0;
//            }
//
//            boolean targetReached = false;
//            int i = 0;
//            while (!targetReached) {
//                targetReached = targetReached();
//                look();
//                int s = findBarrier();
//                int a = this.lerningAlgorithmus.chooseAction(s);
//                doAction(a);
//                // 0 Forward, 1 RLeft, 2 RRight, 3 Backward
//                System.out.print(" I: " + i);
//                System.out.println();
//                look();
//                int sNext = findBarrier();
//                this.lerningAlgorithmus.learn(s, sNext, a, reward);
//                i++;
//            }
//            System.out.println("I: " + i + " Target reached! In " + ((System.currentTimeMillis() - startTime) / 1000) + "s");
//        }

//        this.drived_look = 0;
//        this.drived_rotateLeft = 0;
//        this.drived_backward = 0;
//        this.drived_rotateRight = 0;
//        this.drived_forward = 0;
//        this.try_drived_forward = 0;
//        this.try_drived_backward = 0;
//        this.try_drived_rotateLeft = 0;
//        this.try_drived_rotateRight = 0;
//
//        this.state0000 = 0;
//        this.state0001 = 0;
//        this.state0010 = 0;
//        this.state0011 = 0;
//        this.state0100 = 0;
//        this.state0101 = 0;
//        this.state0110 = 0;
//        this.state0111 = 0;
//        this.state1000 = 0;
//        this.state1001 = 0;
//        this.state1010 = 0;
//        this.state1011 = 0;
//        this.state1100 = 0;
//        this.state1101 = 0;
//        this.state1110 = 0;
//        this.state1111 = 0;
//
//        // Probedurchlauf
//        boolean targetReached = false;
//        int i = 0;
//
//        ArrayList<Integer> tmpPositions = new ArrayList<>();
//        for(int z : this.startPosition){
//            tmpPositions.add(z);
//        }
//        this.position = OwnUtils.convertArrayListToIntArray(tmpPositions);
//
//        sp = "";
//        for(int startPos : this.startPosition){
//            sp += startPos + "-";
//        }
//        System.out.println("Vor Probedurchlauf - StartPos: " + sp);
//
//        while (!targetReached) {
//            targetReached = targetReached();
//            look();
//            int s = findBarrier();
//            int a = this.lerningAlgorithmus.chooseAction(s);
//            doAction(a);
//            look();
//            int sNext = findBarrier();
//            this.lerningAlgorithmus.learn(s, sNext, a, reward);
//            i++;
//
//            String roboPos = "";
//            for (int pos : this.position) {
//                roboPos += pos + "-";
//            }
//            String targetPos = "";
//            for (int tPos : SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getIndexMazeTargetFields()) {
//                targetPos += tPos + "-";
//            }
//            System.out.println("I: " + i + " rPos: " + roboPos + " tPos: " + targetPos);
//        }
//        System.out.println("Target reached! In " + ((System.currentTimeMillis() - startTime) / 1000) + "s");
//        System.out.println("Iterationen: " + i);
//
//        System.out.println("Bewegungsmuster:");
//        System.out.println("vorwärts: " + this.drived_forward);
//        System.out.println("vorwärts angestoßen: " + this.try_drived_forward);
//        System.out.println("Zurück:" + this.drived_backward);
//        System.out.println("hinten angestoßen: " + this.try_drived_backward);
//        System.out.println("Rechtsrotation: " + this.drived_rotateRight);
//        System.out.println("Bei Rechtsrotation angestoßen: " + this.try_drived_rotateRight);
//        System.out.println("Linksrotation: " + this.drived_rotateLeft);
//        System.out.println("Bei Linksrotation angestoßen: " + this.try_drived_rotateLeft);
//        System.out.println("Geschaut: " + this.drived_look);
//        System.out.println("Angestoßen: " + (this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateRight + this.try_drived_rotateLeft));
//        System.out.println();
//        System.out.println("Statusmuster:");
//        System.out.println("0000: " + this.state0000);
//        System.out.println("0001: " + this.state0001);
//        System.out.println("0010: " + this.state0010);
//        System.out.println("0011: " + this.state0011);
//        System.out.println("0100: " + this.state0100);
//        System.out.println("0101: " + this.state0101);
//        System.out.println("0110: " + this.state0110);
//        System.out.println("0111: " + this.state0111);
//        System.out.println("1000: " + this.state1000);
//        System.out.println("1001: " + this.state1001);
//        System.out.println("1010: " + this.state1010);
//        System.out.println("1011: " + this.state1011);
//        System.out.println("1100: " + this.state1100);
//        System.out.println("1101: " + this.state1101);
//        System.out.println("1110: " + this.state1110);
//        System.out.println("1111: " + this.state1111);
//        System.out.println("Angestoßen: " + (this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateRight + this.try_drived_rotateLeft));
//        System.out.println("Durchschnittliche Anstöße: " + this.averageImpulses);
//
//        this.lerningAlgorithmus.printQTable();
//        callGuiUpdateFunction();

        // Bestimme beste Wertebelegung für alpha, gamma, epsilon
//        ArrayList<Double[]> bestValues75 = new ArrayList<>();
//        int testRun = 1000;
//        long startTime = System.currentTimeMillis();
//        for (double k = 0, g = 0; k < 100; k++, g += 0.01) {
//            System.out.println("Durchlauf: " + k + " aktuell verstrichene Zeit: " + ((System.currentTimeMillis() - startTime) / 1000) + "s");
//            System.out.println("Aktuell beste Werte: epsilon[" + this.bestEpsilon + "] alpha[" + this.bestAlpha + "] gamma[" + this.bestGamma + "] Fehler: " + (this.lowestImpulses * 100 / testRun) + "%");
//            lerningAlgorithmus.gamma = g;
//            for (double j = 0, a = 0; j < 100; j++, a += 0.01) {
//                lerningAlgorithmus.alpha = a;
//                for (double i = 0, e = 0; i < 100; i++, e += 0.01) {
//                    this.try_drived_forward = 0;
//                    this.try_drived_backward = 0;
//                    this.try_drived_rotateRight = 0;
//                    this.try_drived_rotateLeft = 0;
//                    lerningAlgorithmus.epsilon = e;
//
//                    this.reward = 0;
//                    for (int l = 0; l < testRun; l++) {
//                        look();
//                        int s = findBarrier();
//                        int az = this.lerningAlgorithmus.chooseAction(s);
//                        doAction(az);
//                        look();
//                        int sNext = findBarrier();
//                        this.lerningAlgorithmus.learn(s, sNext, az, this.reward);
//                    }
//
//                    if((this.try_drived_backward + this.try_drived_forward + this.try_drived_rotateRight + this.try_drived_rotateLeft) < 750){
//                        System.out.println("< 75 - Value added: " + (this.try_drived_backward + this.try_drived_forward + this.try_drived_rotateRight + this.try_drived_rotateLeft) + " a[" + a + "] e[" + e + "]" + " g[" + g + "]");
//                        Double[] values = {a,e,g};
//                        bestValues75.add(values);
//
//                        if((this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateLeft + this.try_drived_rotateLeft) < this.lowestImpulses){
//                            this.bestAlpha = a;
//                            this.bestEpsilon = e;
//                            this.bestGamma = g;
//                            this.lowestImpulses = (this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateLeft + this.try_drived_rotateLeft);
//                            System.out.println("Neue beste Fehlerrate: " + (this.lowestImpulses * 100 / testRun));
//                        }
//                    }
//                }
//            }
//        }
//        System.out.println("75er Durchlaufzeit: " + ((System.currentTimeMillis() - startTime) / 1000) + "s");
//
//        ArrayList<Double[]> bestValues50 = new ArrayList<>();
//        testRun = 2000;
//        for (int k = 0; k < bestValues75.size(); k++) {
//            System.out.println("Durchlauf: " + k + " aktuell verstrichene Zeit: " + ((System.currentTimeMillis() - startTime) / 1000) + "s");
//            System.out.println("Aktuell beste Werte: epsilon[" + this.bestEpsilon + "] alpha[" + this.bestAlpha + "] gamma[" + this.bestGamma + "] Fehler: " + (this.lowestImpulses * 100 / testRun) + "%");
//            lerningAlgorithmus.gamma = bestValues75.get(k)[2];
//            for (int j = 0; j < bestValues75.size(); j++) {
//                lerningAlgorithmus.alpha = bestValues75.get(j)[0];
//                for (int i = 0; i < bestValues75.size(); i++) {
//                    this.try_drived_forward = 0;
//                    this.try_drived_backward = 0;
//                    this.try_drived_rotateRight = 0;
//                    this.try_drived_rotateLeft = 0;
//                    lerningAlgorithmus.epsilon = bestValues75.get(i)[1];
//
//                    this.reward = 0;
//                    for (int l = 0; l < testRun; l++) {
//                        look();
//                        int s = findBarrier();
//                        int az = this.lerningAlgorithmus.chooseAction(s);
//                        doAction(az);
//                        look();
//                        int sNext = findBarrier();
//                        this.lerningAlgorithmus.learn(s, sNext, az, this.reward);
//                    }
//
//                    if((this.try_drived_backward + this.try_drived_forward + this.try_drived_rotateRight + this.try_drived_rotateLeft) < 1000){
//                        System.out.println("< 50 - Value added: " + (this.try_drived_backward + this.try_drived_forward + this.try_drived_rotateRight + this.try_drived_rotateLeft) + " a[" + bestValues75.get(j)[0] + "] e[" + bestValues75.get(i)[1] + "]" + " g[" + bestValues75.get(k)[2] + "]");
//                        Double[] values = {bestValues75.get(j)[0], bestValues75.get(i)[1], bestValues75.get(k)[2]};
//                        bestValues50.add(values);
//
//                        if((this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateLeft + this.try_drived_rotateLeft) < this.lowestImpulses){
//                            this.bestAlpha = bestValues75.get(j)[0];
//                            this.bestEpsilon = bestValues75.get(i)[1];
//                            this.bestGamma = bestValues75.get(k)[2];
//                            this.lowestImpulses = (this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateLeft + this.try_drived_rotateLeft);
//                            System.out.println("Neue beste Fehlerrate: " + (this.lowestImpulses * 100 / testRun));
//                        }
//                    }
//                }
//            }
//        }
//        System.out.println("50er Durchlaufzeit: " + ((System.currentTimeMillis() - startTime) / 1000) + "s");
//
//        ArrayList<Double[]> bestValues25 = new ArrayList<>();
//        testRun = 4000;
//        for (int k = 0; k < bestValues50.size(); k++) {
//            System.out.println("Durchlauf: " + k + " aktuell verstrichene Zeit: " + ((System.currentTimeMillis() - startTime) / 1000) + "s");
//            System.out.println("Aktuell beste Werte: epsilon[" + this.bestEpsilon + "] alpha[" + this.bestAlpha + "] gamma[" + this.bestGamma + "] Fehler: " + (this.lowestImpulses * 100 / testRun) + "%");
//            lerningAlgorithmus.gamma = bestValues50.get(k)[2];
//            for (int j = 0; j < bestValues50.size(); j++) {
//                lerningAlgorithmus.alpha = bestValues50.get(j)[0];
//                for (int i = 0; i < bestValues50.size(); i++) {
//                    this.try_drived_forward = 0;
//                    this.try_drived_backward = 0;
//                    this.try_drived_rotateRight = 0;
//                    this.try_drived_rotateLeft = 0;
//                    lerningAlgorithmus.epsilon = bestValues50.get(i)[1];
//
//                    this.reward = 0;
//                    for (int l = 0; l < testRun; l++) {
//                        look();
//                        int s = findBarrier();
//                        int az = this.lerningAlgorithmus.chooseAction(s);
//                        doAction(az);
//                        look();
//                        int sNext = findBarrier();
//                        this.lerningAlgorithmus.learn(s, sNext, az, this.reward);
//                    }
//
//                    if((this.try_drived_backward + this.try_drived_forward + this.try_drived_rotateRight + this.try_drived_rotateLeft) < 1000){
//                        System.out.println("< 50 - Value added: " + (this.try_drived_backward + this.try_drived_forward + this.try_drived_rotateRight + this.try_drived_rotateLeft) + " a[" + bestValues50.get(j)[0] + "] e[" + bestValues50.get(i)[1] + "]" + " g[" + bestValues50.get(k)[2] + "]");
//                        Double[] values = {bestValues50.get(j)[0], bestValues50.get(i)[1], bestValues50.get(k)[2]};
//                        bestValues25.add(values);
//
//                        if((this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateLeft + this.try_drived_rotateLeft) < this.lowestImpulses){
//                            this.bestAlpha = bestValues50.get(j)[0];
//                            this.bestEpsilon = bestValues50.get(i)[1];
//                            this.bestGamma = bestValues50.get(k)[2];
//                            this.lowestImpulses = (this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateLeft + this.try_drived_rotateLeft);
//                            System.out.println("Neue beste Fehlerrate: " + (this.lowestImpulses * 100 / testRun));
//                        }
//                    }
//                }
//            }
//        }
//
//        System.out.println("25er Durchlaufzeit: " + ((System.currentTimeMillis() - startTime) / 1000) + "s");

//        ArrayList<Double[]> bestValues10 = new ArrayList<>();
//        testRun = 10000;
//        for (int k = 0; k < bestValues25.size(); k++) {
//            System.out.println("Durchlauf: " + k + " aktuell verstrichene Zeit: " + ((System.currentTimeMillis() - startTime) / 1000) + "s");
//            System.out.println("Aktuell beste Werte: epsilon[" + this.bestEpsilon + "] alpha[" + this.bestAlpha + "] gamma[" + this.bestGamma + "] Fehler: " + (this.lowestImpulses * 100 / testRun) + "%");
//            lerningAlgorithmus.gamma = bestValues25.get(k)[2];
//            for (int j = 0; j < bestValues25.size(); j++) {
//                lerningAlgorithmus.alpha = bestValues25.get(j)[0];
//                for (int i = 0; i < bestValues25.size(); i++) {
//                    this.try_drived_forward = 0;
//                    this.try_drived_backward = 0;
//                    this.try_drived_rotateRight = 0;
//                    this.try_drived_rotateLeft = 0;
//                    lerningAlgorithmus.epsilon = bestValues25.get(i)[1];
//
//                    this.reward = 0;
//                    for (int l = 0; l < testRun; l++) {
//                        look();
//                        int s = findBarrier();
//                        int az = this.lerningAlgorithmus.chooseAction(s);
//                        doAction(az);
//                        look();
//                        int sNext = findBarrier();
//                        this.lerningAlgorithmus.learn(s, sNext, az, this.reward);
//                    }
//
//                    if((this.try_drived_backward + this.try_drived_forward + this.try_drived_rotateRight + this.try_drived_rotateLeft) < 1000){
//                        System.out.println("< 50 - Value added: " + (this.try_drived_backward + this.try_drived_forward + this.try_drived_rotateRight + this.try_drived_rotateLeft) + " a[" + bestValues25.get(j)[0] + "] e[" + bestValues25.get(i)[1] + "]" + " g[" + bestValues25.get(k)[2] + "]");
//                        Double[] values = {bestValues25.get(j)[0], bestValues25.get(i)[1], bestValues25.get(k)[2]};
//                        bestValues10.add(values);
//
//                        if((this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateLeft + this.try_drived_rotateLeft) < this.lowestImpulses){
//                            this.bestAlpha = bestValues25.get(j)[0];
//                            this.bestEpsilon = bestValues25.get(i)[1];
//                            this.bestGamma = bestValues25.get(k)[2];
//                            this.lowestImpulses = (this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateLeft + this.try_drived_rotateLeft);
//                            System.out.println("Neue beste Fehlerrate: " + (this.lowestImpulses * 100 / testRun));
//                        }
//                    }
//                }
//            }
//        }
//
//        System.out.println("10er Durchlaufzeit: " + ((System.currentTimeMillis() - startTime) / 1000) + "s");

        ////
//        FileWriter fw;
//        BufferedWriter bw;
//        try {
//            fw = new FileWriter(System.getProperty("user.dir") + "\\src\\gui_simulation\\" + "learning_variables2");
//            bw = new BufferedWriter(fw);
//
//            String toWrite = "";
//            if(bestValues10.size() > 0){
//                for(Double[] i : bestValues10){
//                    toWrite += "aplha: " + i[0] + " epsilon: " + i[1] + " gamma: " + i[2];
//                    bw.write(toWrite);
//                    bw.newLine();
//                }
//            } else {
//                for(Double[] i : bestValues25){
//                    toWrite += "aplha: " + i[0] + " epsilon: " + i[1] + " gamma: " + i[2];
//                    bw.write(toWrite);
//                    bw.newLine();
//                }
//            }
//
//            bw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        ////
//        System.out.println("Beste Variablenbelegungen: epsilon[" + this.bestEpsilon + "] alpha[" + this.bestAlpha + "] gamma[" + this.bestGamma + "] Anstöße: " + this.lowestImpulses + " Bester Fehler: " + (this.lowestImpulses * 100 / testRun));
//        System.out.println("Durchlaufzeit: " + ((System.currentTimeMillis() - startTime) / 1000) + "s");


//        callGuiUpdateFunction();
//
//        // Berechnen der Anstöße im Durchschnitt
//        this.iterations++;
//        this.averageImpulses = (this.averageImpulses * (this.iterations - 1) + (this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateRight + this.try_drived_rotateLeft)) / this.iterations;
//
//        //
//        System.out.println("Bewegungsmuster:");
//        System.out.println("vorwärts: " + this.drived_forward);
//        System.out.println("vorwärts angestoßen: " + this.try_drived_forward);
//        System.out.println("Zurück:" + this.drived_backward);
//        System.out.println("hinten angestoßen: " + this.try_drived_backward);
//        System.out.println("Rechtsrotation: " + this.drived_rotateRight);
//        System.out.println("Bei Rechtsrotation angestoßen: " + this.try_drived_rotateRight);
//        System.out.println("Linksrotation: " + this.drived_rotateLeft);
//        System.out.println("Bei Linksrotation angestoßen: " + this.try_drived_rotateLeft);
//        System.out.println("Geschaut: " + this.drived_look);
//        System.out.println("Angestoßen: " + (this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateRight + this.try_drived_rotateLeft));
//        System.out.println();
//        System.out.println("Statusmuster:");
//        System.out.println("0000: " + this.state0000);
//        System.out.println("0001: " + this.state0001);
//        System.out.println("0010: " + this.state0010);
//        System.out.println("0011: " + this.state0011);
//        System.out.println("0100: " + this.state0100);
//        System.out.println("0101: " + this.state0101);
//        System.out.println("0110: " + this.state0110);
//        System.out.println("0111: " + this.state0111);
//        System.out.println("1000: " + this.state1000);
//        System.out.println("1001: " + this.state1001);
//        System.out.println("1010: " + this.state1010);
//        System.out.println("1011: " + this.state1011);
//        System.out.println("1100: " + this.state1100);
//        System.out.println("1101: " + this.state1101);
//        System.out.println("1110: " + this.state1110);
//        System.out.println("1111: " + this.state1111);
//        System.out.println("Angestoßen: " + (this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateRight + this.try_drived_rotateLeft));
//        System.out.println("Durchschnittliche Anstöße: " + this.averageImpulses);

//        this.lerningAlgorithmus.printQTable();
    }

    // Roboter Interface Methods
    @Override
    public void doAction(int action) {
        this.isBumped = false;
        this.reward = 0;
        switch (action) {
            case DRIVE_FORWARD:
                this.forward();
                if (!isBumped) {
                    this.reward = REWARD_DRIVE_FORWARD;
                    this.drived_forward++;
                } else {
                    this.reward = REWARD_BUMPED;
                    this.try_drived_forward++;
                }

                break;
            case DRIVE_ROTATE_RIGHT:
                this.right();
                if (!isBumped) {
                    this.reward = REWARD_DRIVE_ROTATE_RIGHT;
                    this.drived_rotateRight++;
                } else {
                    this.reward = REWARD_BUMPED;
                    this.try_drived_rotateRight++;
                }

                break;
            case DRIVE_BACKWARD:
                this.backward();
                if (!isBumped) {
                    this.reward = REWARD_DRIVE_BACK;
                    this.drived_backward++;
                } else {
                    this.reward = REWARD_BUMPED;
                    this.try_drived_backward++;
                }

                break;
            case DRIVE_ROTATE_LEFT:
                this.left();
                if (!isBumped) {
                    this.reward = REWARD_DRIVE_ROTATE_LEFT;
                    this.drived_rotateLeft++;
                } else {
                    this.reward = REWARD_BUMPED;
                    this.try_drived_rotateLeft++;
                }

                break;
        }
    }

    @Override
    public void fetchData(int pos) {

    }

    @Override
    public int findBarrier() {
        if (!forwardFree() && !backwardFree() && !rotationLeftFree() && !rotationRightFree()) {
            this.state0000++;
            return 0;
        }

        if (!forwardFree() && !backwardFree() && !rotationLeftFree() && rotationRightFree()) {
            this.state0001++;
            return 1;
        }

        if (!forwardFree() && !backwardFree() && rotationLeftFree() && !rotationRightFree()) {
            this.state0010++;
            return 2;
        }

        if (!forwardFree() && !backwardFree() && rotationLeftFree() && rotationRightFree()) {
            this.state0011++;
            return 3;
        }

        if (!forwardFree() && backwardFree() && !rotationLeftFree() && !rotationRightFree()) {
            this.state0100++;
            return 4;
        }

        if (!forwardFree() && backwardFree() && !rotationLeftFree() && rotationRightFree()) {
            this.state0101++;
            return 5;
        }

        if (!forwardFree() && backwardFree() && rotationLeftFree() && !rotationRightFree()) {
            this.state0110++;
            return 6;
        }

        if (!forwardFree() && backwardFree() && rotationLeftFree() && rotationRightFree()) {
            this.state0111++;
            return 7;
        }

        if (forwardFree() && !backwardFree() && !rotationLeftFree() && !rotationRightFree()) {
            this.state1000++;
            return 8;
        }

        if (forwardFree() && !backwardFree() && !rotationLeftFree() && rotationRightFree()) {
            this.state1001++;
            return 9;
        }

        if (forwardFree() && !backwardFree() && rotationLeftFree() && !rotationRightFree()) {
            this.state1010++;
            return 10;
        }

        if (forwardFree() && !backwardFree() && rotationLeftFree() && rotationRightFree()) {
            this.state1011++;
            return 11;
        }

        if (forwardFree() && backwardFree() && !rotationLeftFree() && !rotationRightFree()) {
            this.state1100++;
            return 12;
        }

        if (forwardFree() && backwardFree() && !rotationLeftFree() && rotationRightFree()) {
            this.state1101++;
            return 13;
        }

        if (forwardFree() && backwardFree() && rotationLeftFree() && !rotationRightFree()) {
            this.state1110++;
            return 14;
        }

        if (forwardFree() && backwardFree() && rotationLeftFree() && rotationRightFree()) {
            this.state1111++;
            return 15;
        }

        throw new IllegalStateException("Roboter ist in keinem gültigen Zustand");
    }
//    @Override
//    public int findBarrier() {
//        // Kann nach links rotieren
//        if(Controller_MainGUI.mazeFreeFieldsRotateLeftForward(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber), SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeRobots().get(this.uniqueIndexNumberOfMazeRobot))){
//            if(isBumped){
//                this.stateBumpedCanRotateLeft++;
//                return 15;
//            }
//            this.stateCanRotateLeft++;
//            return 16;
//        }
//
//        // Kann nach rechts rotieren
//        if(Controller_MainGUI.mazeFreeFieldsRotateRightForward(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber), SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeRobots().get(this.uniqueIndexNumberOfMazeRobot))){
//            if(isBumped){
//                this.stateBumpedCanRotateRight++;
//                return 17;
//            }
//            this.stateCanRotateRight++;
//            return 18;
//        }
//
//        // Kann nach links und nach rechts rotieren
//        if(Controller_MainGUI.mazeFreeFieldsRotateLeftForward(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber), SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeRobots().get(this.uniqueIndexNumberOfMazeRobot)) && Controller_MainGUI.mazeFreeFieldsRotateRightForward(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber), SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeRobots().get(this.uniqueIndexNumberOfMazeRobot))){
//            if(isBumped){
//                this.stateBumpedCanRotateLeftRight++;
//                return 19;
//            }
//            this.stateCanRotateLeftRight++;
//            return 20;
//        }
//
//        // Barriere vorne
//        if (distanceData[1] < MIN_DISTANCE && distanceData[0] >= MIN_DISTANCE && distanceData[2] >= MIN_DISTANCE) {
//            if (isBumped) {
//                this.stateBumpedFront++;
//                return 8;
//            }
//
//            this.stateFront++;
//            return 1;
//        }
//        // Barriere links
//        if (distanceData[0] < MIN_DISTANCE && distanceData[1] >= MIN_DISTANCE && distanceData[2] >= MIN_DISTANCE) {
//            if(isBumped){
//                this.stateBumpedLeft++;
//                return 9;
//            }
//
//            this.stateLeft++;
//            return 2;
//        }
//        // Barriere rechts
//        if (distanceData[2] < MIN_DISTANCE && distanceData[0] >= MIN_DISTANCE && distanceData[1] >= MIN_DISTANCE) {
//            if(isBumped){
//                this.stateBumpedRight++;
//                return 10;
//            }
//
//            this.stateFront++;
//            return 3;
//        }
//        // Barriere vorne & links
//        if (distanceData[0] < MIN_DISTANCE && distanceData[1] < MIN_DISTANCE && distanceData[2] >= MIN_DISTANCE) {
//            if(isBumped){
//                this.stateBumpedFrontLeft++;
//                return 11;
//            }
//
//            this.stateFrontLeft++;
//            return 4;
//        }
//        // Barriere vorne & rechts
//        if (distanceData[1] < MIN_DISTANCE && distanceData[2] < MIN_DISTANCE && distanceData[0] >= MIN_DISTANCE) {
//            if(isBumped){
//                this.stateBumpedFrontRight++;
//                return 12;
//            }
//
//            this.stateFrontRight++;
//            return 5;
//        }
//        // Barriere links & rechts
//        if (distanceData[0] < MIN_DISTANCE && distanceData[2] < MIN_DISTANCE && distanceData[1] >= MIN_DISTANCE) {
//            if(isBumped){
//                this.stateBumpedLeftRight++;
//                return 13;
//            }
//
//            this.stateLeftRight++;
//            return 6;
//        }
//        // Barriere links & vorne & rechts
//        if (distanceData[0] < MIN_DISTANCE && distanceData[1] < MIN_DISTANCE && distanceData[2] < MIN_DISTANCE) {
//            if(isBumped){
//                this.stateBumpedFrontLeftRight++;
//                return 14;
//            }
//
//            this.stateFrontLeftRight++;
//            return 7;
//        }
//        // Keine Barriere
//        this.stateNoBarrier++;
//        return 0;
//
//        // bumped
////        if(isBumped){
////            this.stateBumped++;
////            System.out.println(8);
////            return 8;
////        }
////        // front = 1
////        if (distanceData[0] > MIN_DISTANCE && distanceData[1] < MIN_DISTANCE && distanceData[2] > MIN_DISTANCE) {
////            // front + bumped
//////			if (isBumped()) {
//////				return 8;
//////			}
////            System.out.println(1);
////            this.stateFront++;
////            return 1;
////        }
////        // left = 2
////        if (distanceData[0] < MIN_DISTANCE && distanceData[1] > MIN_DISTANCE && distanceData[2] > MIN_DISTANCE) {
////            // left + bumped
//////			if (isBumped()) {
//////				return 9;
//////			}
////            System.out.println(2);
////            this.stateLeft++;
////            return 2;
////        }
////        // right = 3
////        if (distanceData[0] > MIN_DISTANCE && distanceData[1] > MIN_DISTANCE && distanceData[2] < MIN_DISTANCE) {
////            // right + bumped
//////			if (isBumped()) {
//////				return 10;
//////			}
////            System.out.println(3);
////            this.stateRight++;
////            return 3;
////        }
////        // front + left = 4
////        if (distanceData[0] < MIN_DISTANCE && distanceData[1] < MIN_DISTANCE && distanceData[2] > MIN_DISTANCE) {
////            // front + left + bumped
//////			if (isBumped()) {
//////				return 11;
//////			}
////            System.out.println(4);
////            this.stateFrontLeft++;
////            return 4;
////        }
////        // front + right = 5
////        if (distanceData[0] > MIN_DISTANCE && distanceData[1] < MIN_DISTANCE && distanceData[2] < MIN_DISTANCE) {
////            // front + right + bumped
//////			if (isBumped()) {
//////				return 12;
//////			}
////            System.out.println(5);
////            this.stateFrontRight++;
////            return 5;
////        }
////        // left + right = 6
////        if (distanceData[0] < MIN_DISTANCE && distanceData[1] > MIN_DISTANCE && distanceData[2] < MIN_DISTANCE) {
////            // left + right + bumped
//////			if (isBumped()) {
//////				return 13;
//////			}
////            System.out.println(6);
////            this.stateLeftRight++;
////            return 6;
////        }
////        // front + left + right = 7
////        if (distanceData[0] < MIN_DISTANCE && distanceData[1] < MIN_DISTANCE && distanceData[2] < MIN_DISTANCE) {
////            // front + left + right + bumped
//////			if (isBumped()) {
//////				return 14;
//////			}
////            System.out.println(7);
////            this.stateFrontLeftRight++;
////            return 7;
////        }
////        System.out.println(0);
////        this.stateNoBarrier++;
////        return 0;
//    }

    @Override
    public void look() {
        this.distanceDataFieldsLeft.clear();
        this.distanceDataFieldsFront.clear();
        this.distanceDataFieldsRight.clear();

        switch (this.headDirection) {
            case 0:
                this.distanceDataFieldsLeft.addAll(getDistanceDataLeft(-2));
                this.distanceData[0] = this.distanceDataFieldsLeft.size();
                this.distanceDataFieldsFront.addAll(getDistanceDataAbove(-1));
                this.distanceData[1] = this.distanceDataFieldsFront.size();
                this.distanceDataFieldsRight.addAll(getDistanceDataRight(2));
                this.distanceData[2] = this.distanceDataFieldsRight.size();
                break;
            case 1:
                this.distanceDataFieldsLeft.addAll(getDistanceDataAbove(-2));
                this.distanceData[0] = this.distanceDataFieldsLeft.size();
                this.distanceDataFieldsFront.addAll(getDistanceDataRight(1));
                this.distanceData[1] = this.distanceDataFieldsFront.size();
                this.distanceDataFieldsRight.addAll(getDistanceDataBelow(-2));
                this.distanceData[2] = this.distanceDataFieldsRight.size();
                break;
            case 2:
                this.distanceDataFieldsLeft.addAll(getDistanceDataRight(2));
                this.distanceData[0] = this.distanceDataFieldsLeft.size();
                this.distanceDataFieldsFront.addAll(getDistanceDataBelow(-1));
                this.distanceData[1] = this.distanceDataFieldsFront.size();
                this.distanceDataFieldsRight.addAll(getDistanceDataLeft(-2));
                this.distanceData[2] = this.distanceDataFieldsRight.size();
                break;
            case 3:
                this.distanceDataFieldsLeft.addAll(getDistanceDataBelow(-2));
                this.distanceData[0] = this.distanceDataFieldsLeft.size();
                this.distanceDataFieldsFront.addAll(getDistanceDataLeft(-1));
                this.distanceData[1] = this.distanceDataFieldsFront.size();
                this.distanceDataFieldsRight.addAll(getDistanceDataAbove(-2));
                this.distanceData[2] = this.distanceDataFieldsRight.size();
                break;
        }

        this.drived_look++;
    }

    @Override
    public boolean isBumped() {
        this.isBumped = true;
        return true;
    }

    @Override
    public boolean isGoal() {
        return false;
    }

    @Override
    public void forward() {
        if (forwardFree()) {
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
        } else {
            this.isBumped = true;
        }
    }

    @Override
    public void backward() {
        if (backwardFree()) {
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
        } else {
            this.isBumped = true;
        }
    }

    @Override
    public void left() {
        clearDistanceData();
        if (rotationLeftFree()) {
            switch (this.headDirection) {
                case 0:
                    for (int y = 1, x = 0, xd = -1, yd = 0; y < this.sizeY + 1; y++, x = 0, xd = xd + this.sizeX - 2 + 1, yd = yd + this.sizeX + 2 - 1) {
                        for (int xi = 0; xi < this.sizeX; xi++, x++, xd--, yd--) {
                            this.position[this.sizeY * this.sizeX - y * this.sizeX + x] = this.position[this.sizeY * this.sizeX - y * this.sizeX + x] + xd + yd * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY();
                        }
                    }
                    break;
                case 1:
                    for (int y = 0, x = 0, xd = 0, yd = -1; y < this.sizeY; y++, x = 0, xd = xd + this.sizeX + 1, yd = yd + this.sizeX - 1) {
                        for (int xi = 0; xi < this.sizeX; xi++, x++, xd--, yd--) {
                            this.position[y * (this.sizeY + 1) + x] = this.position[y * (this.sizeY + 1) + x] + xd + yd * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY();
                        }
                    }
                    break;
                case 2:
                    for (int y = 1, x = 0, xd = 1, yd = 0; y < this.sizeY + 1; y++, x = 0, xd = xd - this.sizeX + 2 - 1, yd = yd - this.sizeX - 1) {
                        for (int xi = 0; xi < this.sizeX; xi++, x--, yd++, xd++) {
                            this.position[y * this.sizeX - 1 + x] = this.position[y * this.sizeX - 1 + x] + xd + yd * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY();
                        }
                    }
                    break;
                case 3:
                    for (int y = 0, x = 0, xd = 0, yd = 1; y < this.sizeY; y++, x = 0, xd = xd - this.sizeX - 1, yd = yd - this.sizeX + 1) {
                        for (int xi = 0; xi < this.sizeX; xi++, x--, xd++, yd++) {
                            this.position[this.sizeY * this.sizeX - 1 - y * this.sizeX + x] = this.position[this.sizeY * this.sizeX - 1 - y * this.sizeX + x] + xd + yd * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY();
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

            changeHeadPosition();
            this.isBumped = false;
        } else {
            isBumped();
        }
    }

    @Override
    public void right() {
        clearDistanceData();
        if (rotationRightFree()) {
            switch (this.headDirection) {
                case 0:
                    for (int y = 0, x = -1, xv = 1, yv = 0; y < this.sizeY; y++, x = -1, xv = xv - this.sizeX + 2 - 1, yv = yv + this.sizeX + 1) {
                        for (int xi = 0; xi < this.sizeX; xi++, x--, xv++, yv--) {
                            this.position[this.sizeX * this.sizeY - y * this.sizeX + x] = this.position[this.sizeX * this.sizeY - y * this.sizeX + x] + xv + yv * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY();
                        }
                    }
                    break;
                case 1:
                    for (int y = 1, x = 0, xv = 0, yv = 1; y < this.sizeY + 1; y++, x = 0, xv = xv + this.sizeX + 1, yv = yv - this.sizeX + 2 - 1) {
                        for (int xi = 0; xi < this.sizeX; xi++, x++, xv--, yv++) {
                            this.position[this.sizeX * this.sizeY - y * this.sizeX + x] = this.position[this.sizeX * this.sizeY - y * this.sizeX + x] + xv + yv * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY();
                        }
                    }
                    break;
                case 2:
                    for (int y = 0, x = 0, xv = -1, yv = 0; y < this.sizeY; y++, x = 0, xv = xv + this.sizeX - 2 + 1, yv = yv - this.sizeX - 1) {
                        for (int xi = 0; xi < this.sizeX; xi++, x++, xv--, yv++) {
                            this.position[y * (this.sizeY - 1) + x] = this.position[y * (this.sizeY - 1) + x] + xv + yv * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY();
                        }
                    }
                    break;
                case 3:
                    for (int y = 1, x = 0, xv = 0, yv = -1; y < this.sizeY + 1; y++, x = 0, xv = xv - this.sizeX - 1, yv = yv + this.sizeX - 2 + 1) {
                        for (int xi = 0; xi < this.sizeX; xi++, x--, xv++, yv--) {
                            this.position[y * this.sizeX - 1 + x] = this.position[y * this.sizeX - 1 + x] + xv + yv * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY();
                        }
                    }
                    break;
            }

            this.headDirection++;
            if (this.headDirection > 3) {
                this.headDirection = 0;
            }

            int tmpSizeX = this.sizeX;
            this.sizeX = this.sizeY;
            this.sizeY = tmpSizeX;

            changeHeadPosition();
            this.isBumped = false;
        } else {
            isBumped();
        }
    }

    /*
     * Bewegungen prüfen
     * */
    private boolean forwardFree() {
        switch (this.headDirection) {
            case 0:
                return SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).aboveFree(this);
            case 1:
                return SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).rightFree(this);
            case 2:
                return SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).belowFree(this);
            case 3:
                return SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).leftFree(this);
        }
        throw new IllegalStateException("Fehler in Kopfstellung: " + this.headDirection);
    }

    private boolean backwardFree() {
        switch (this.headDirection) {
            case 0:
                return SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).belowFree(this);
            case 1:
                return SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).leftFree(this);
            case 2:
                return SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).aboveFree(this);
            case 3:
                return SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).rightFree(this);
        }
        throw new IllegalStateException("Fehler in Kopfstellung: " + this.headDirection);
    }

    private boolean rotationLeftFree() {
        return SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).rotationForwardLeftFree(this);
    }

    private boolean rotationRightFree() {
        return SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).rotationForwardRightFree(this);
    }

    /*
     * Robot Table
     * */
    public String getSelectedText() {
        return selectedText;
    }

    public int getRobotNumber() {
        return robotNumber;
    }

    public String getRobotName() {
        return robotName;
    }
}
