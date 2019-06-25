package gui_simulation;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;

public class SimulationRobot implements Roboter {
    private static final String PREFIX_ROBO_NAME = "Robo_";
    private static final String SELECTED_TEXT = "yep";
    private static final String NOT_SELECTED_TEXT = "";
    private static final Color DEFAULT_ROBOT_BODY_COLOR = Color.rgb(55, 109, 19);
    private static final Color DEFAULT_ROBOT_HEAD_COLOR = Color.rgb(255, 0, 0);
    private static final Color DEFAULT_MEASURE_DISTANCE = Color.rgb(255,255,0);
    private static final int MIN_DISTANCE = 1;

    // Roboter Bewegung
    private static final int DRIVE_FORWARD = 0;
    private static final int DRIVE_ROTATE_LEFT = 1;
    private static final int DRIVE_ROTATE_RIGHT = 2;
    private static final int DRIVE_BACKWARD = 3;

    //Rewards - Learning Algorithmus
    private static final double REWARD_BUMPED = -1;
    private static final double REWARD_NOT_BUMPED = 0.5;
    private static final double REWARD_DRIVE_BACK = - 0.5;

    // Bewegungskontrolle
    private int drived_isBumped = 0;
    private int drived_forward = 0;
    private int drived_backward = 0;
    private int drived_rotateLeft = 0;
    private int drived_rotateRight = 0;
    private int drived_look = 0;
    private boolean isBumped = false;

    // Zustände Barrieren
    private int stateFront = 0;
    private int stateFrontLeft = 0;
    private int stateFrontRight = 0;
    private int stateLeft = 0;
    private int stateRight = 0;
    private int stateLeftRight = 0;
    private int stateFrontLeftRight = 0;
    private int stateBumped = 0;
    private int stateNoBarrier = 0;

    // Roboter Table
    private final String robotName;
    private final int robotNumber;
    private String selectedText = "";
    // Roboter Werte
    private int sizeX;
    private int sizeY;
    // TODO position in ArrayList<Integer> konvertieren
    private int[] position;
    private Color robotBodyColor = null;
    private Color robotHeadColor = null;
    private Color measureDistanceColor = null;
    private Integer headDirection; // 0 = Nord, im Uhrzeigersinn
    private ArrayList<Integer> headPosition = new ArrayList<>();
    private final int headSize;
    private final int uniqueIndexNumberOfMazeRobot;
    private final int robotMazeIndexNumber;
    private int[] distanceData = new int[3]; // distanceData[EntfernungLinks, EntfernungVorne, EntfernungRechts]
    private ArrayList<Integer> distanceDataFieldsLeft = new ArrayList<>();
    private ArrayList<Integer> distanceDataFieldsFront = new ArrayList<>();
    private ArrayList<Integer> distanceDataFieldsRight = new ArrayList<>();
    // QLearningAgent
    private QLearningAgent learningAlgorithmus = new QLearningAgent();
    double reward = 0;

    private SimulationRobot(int robotPixelX, int robotPixelY, int[] position) {
        this.sizeX = robotPixelX;
        this.sizeY = robotPixelY;
        this.position = position;
        this.headDirection = robotPixelX > robotPixelY ? 1 : 0;
        this.uniqueIndexNumberOfMazeRobot = SimulationMaze.getSelectedMaze().getAndSetUniqueIndexNumberOfMazeRobot();
        this.robotNumber = this.uniqueIndexNumberOfMazeRobot + 1;
        this.robotName = PREFIX_ROBO_NAME + this.robotNumber;
        this.robotMazeIndexNumber = SimulationMaze.getSelectedMazeIndexNumber();
        this.headSize = 1;
        // Größeren Head setzen
//        switch (this.headDirection) {
//            case 0:
//                if (this.sizeX < 1) {
//                    this.headSize = 0;
//                } else if (this.sizeX == 1 || this.sizeX == 2) {
//                    this.headSize = 1;
//                } else {
//                    this.headSize = this.sizeX - 2;
//                }
//                break;
//            case 1:
//                if (this.sizeY < 0) {
//                    this.headSize = 0;
//                } else if (this.sizeY == 1 || this.sizeY == 2) {
//                    this.headSize = 1;
//                } else {
//                    this.headSize = this.sizeY - 2;
//                }
//                break;
//            default:
//                this.headSize = 0;
//                break;
//        }

        changeHeadPosition();
    }

    public static SimulationRobot addRobot(int robotPixelX, int robotPixelY, int[] position) {
        return new SimulationRobot(robotPixelX, robotPixelY, position);
    }

    public void callGuiUpdateFunction(){
        if(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getNr() == SimulationMaze.getSelectedMaze().getNr()){
            if(this.getUniqueIndexNumberOfMazeRobot() == SimulationMaze.getSelectedMaze().getSelectedRobot().getUniqueIndexNumberOfMazeRobot()){
                SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getFXML_MAIN_CONTROLLER().updateMaze(true);
            }
        }
    }

    public ArrayList<Integer> getDistanceDataFieldsLeft(){
        return this.distanceDataFieldsLeft;
    }

    public ArrayList<Integer> getDistanceDataFieldsFront(){
        return this.distanceDataFieldsFront;
    }

    public ArrayList<Integer> getDistanceDataFieldsRight(){
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

    public Color getMeasureDistanceColor(){
        if(this.measureDistanceColor == null){
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
                    if (this.headSize > 0) {
                        if (this.sizeX == 1) {
                            this.headPosition.add(sortedPos[0]);
                        } else if (this.sizeX == 2) {
                            this.headPosition.add(sortedPos[1]);
                        } else {
                            for (int x = 0; x < this.headSize; x++) {
                                this.headPosition.add(sortedPos[this.sizeX - this.headSize - 1 + x]);
                            }
                        }
                    }
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
        if (Controller_MainGUI.mazeFreeFieldsUp(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber), SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeRobots().get(this.uniqueIndexNumberOfMazeRobot))) {
            for (int i = 0; i < this.position.length; i++) {
                this.position[i] = this.position[i] - SimulationMaze.getSelectedMaze().getMazeSizeY();
            }

            changeHeadPosition();
            callGuiUpdateFunction();
            this.reward = this.reward + SimulationRobot.REWARD_NOT_BUMPED;
        } else {
            isBumped();
        }
    }

    private void moveRight() {
        clearDistanceData();
        if (Controller_MainGUI.mazeFreeFieldsRight(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber), SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeRobots().get(this.uniqueIndexNumberOfMazeRobot))) {
            for (int i = 0; i < this.position.length; i++) {
                this.position[i] = this.position[i] + 1;
            }

            changeHeadPosition();
            callGuiUpdateFunction();
            this.reward = this.reward + SimulationRobot.REWARD_NOT_BUMPED;
        } else {
            isBumped();
        }
    }

    private void moveDown() {
        clearDistanceData();
        if (Controller_MainGUI.mazeFreeFieldsDown(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber), SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeRobots().get(this.uniqueIndexNumberOfMazeRobot))) {
            for (int i = 0; i < this.position.length; i++) {
                this.position[i] = this.position[i] + SimulationMaze.getSelectedMaze().getMazeSizeY();
            }

            changeHeadPosition();
            callGuiUpdateFunction();
            this.reward = this.reward + SimulationRobot.REWARD_DRIVE_BACK;
        } else {
            isBumped();
        }
    }

    private void moveLeft() {
        clearDistanceData();
        if (Controller_MainGUI.mazeFreeFieldsLeft(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber), SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeRobots().get(this.uniqueIndexNumberOfMazeRobot))) {
            for (int i = 0; i < this.position.length; i++) {
                this.position[i] = this.position[i] - 1;
            }

            changeHeadPosition();
            callGuiUpdateFunction();
            this.reward = this.reward + SimulationRobot.REWARD_NOT_BUMPED;
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

    public void keyboardRotateForwardLeft() {
        left();
    }

    public void keyboardRotateForwardRight() {
        right();
    }

    public void keyboardLook(){
        look();
    }

    private ArrayList<Integer> getDistanceDataAbove(int startValue){
        ArrayList<Integer> distanceDataAbove = new ArrayList<>();

        boolean freeFields = true;
        int y = startValue;
        while(freeFields){

            if(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getIndexMazeFreeFields().contains(this.headPosition.get(0) + y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY())){
                distanceDataAbove.add(this.headPosition.get(0) + y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY());
            } else {
                freeFields = false;
            }
            y--;
        }

        return distanceDataAbove;
    }

    private ArrayList<Integer> getDistanceDataRight(int startValue){
        ArrayList<Integer> distanceDataRight = new ArrayList<>();
//
        boolean freeField = true;
        int x = startValue; //1;
        while(freeField){
            if(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getIndexMazeFreeFields().contains(this.headPosition.get(0) + x)){
                distanceDataRight.add(this.headPosition.get(0) + x);
            } else{
                freeField = false;
            }
            x++;
        }

        return distanceDataRight;
    }

    private ArrayList<Integer> getDistanceDataBelow(int startValue){
        ArrayList<Integer> distanceDataBelow = new ArrayList<>();

//        boolean freeField = true;
//        for(int y = 1; freeField && this.headPosition.get(0) + y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY() <= SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY() * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeX(); y++){
//            if(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getIndexMazeFreeFields().contains(this.headPosition.get(0) + y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY())){
//                distanceDataBelow.add(this.headPosition.get(0) + y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY());
//            } else {
//                freeField = false;
//            }
//        }
        boolean freeFields = true;
        int y = startValue; // -1
        while(freeFields){
            if(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getIndexMazeFreeFields().contains(this.headPosition.get(0) + y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY())){
                distanceDataBelow.add(this.headPosition.get(0) + y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY());
            } else {
                freeFields = false;
            }
            y--;
        }

        return distanceDataBelow;
    }

    private ArrayList<Integer> getDistanceDataLeft(int starValue){
        ArrayList<Integer> distanceDataLeft = new ArrayList<>();

        boolean freeFields = true;
        int x = starValue; // -1
        while(freeFields){
            if(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getIndexMazeFreeFields().contains(this.headPosition.get(0) + x)){
                distanceDataLeft.add(this.headPosition.get(0) + x);
            } else {
                freeFields = false;
            }
            x--;
        }

        return distanceDataLeft;
    }

    public void clearDistanceData(){
        this.distanceData[0] = -1;
        this.distanceData[1] = -1;
        this.distanceData[2] = -1;

        this.distanceDataFieldsLeft.clear();
        this.distanceDataFieldsFront.clear();
        this.distanceDataFieldsRight.clear();
    }

    public void start(){
        this.drived_isBumped = 0;
        this.drived_look = 0;
        this.drived_rotateLeft = 0;
        this.drived_backward = 0;
        this.drived_rotateRight = 0;
        this.drived_forward = 0;
        this.stateFront = 0;
        this.stateLeft = 0;
        this.stateRight = 0;
        this.stateNoBarrier = 0;
        this.stateBumped = 0;
        this.stateFrontLeftRight = 0;
        this.stateFrontLeft = 0;
        this.stateFrontRight = 0;
        this.stateLeftRight = 0;
        for(int i = 0; i < 1000; i++) {
            look();
            int s = findBarrier();
            int a = learningAlgorithmus.chooseAction(s);
            doAction(a);
            int sNext = findBarrier();
            learningAlgorithmus.learn(s, sNext, a, reward);
        }

        System.out.println("Bewegungsmuster:");
        System.out.println("vorwärts: " + this.drived_forward);
        System.out.println("Rechtsrotation: " + this.drived_rotateRight);
        System.out.println("Zurück:" + this.drived_backward);
        System.out.println("Linksrotation: " + this.drived_rotateLeft);
        System.out.println("Geschaut: " + this.drived_look);
        System.out.println("Angestoßen: " + this.drived_isBumped);
        System.out.println();
        System.out.println("Statusmuster:");
        System.out.println("Bumped: " + this.stateBumped);
        System.out.println("Keine Barriere: " + this.stateNoBarrier);
        System.out.println("Barriere vorne: " + this.stateFront);
        System.out.println("Barriere links: " + this.stateLeft);
        System.out.println("Barriere rechts: " + this.stateRight);
        System.out.println("Barriere vorne & links: " + this.stateFrontLeft);
        System.out.println("Barriere vorne & rechts: " + this.stateFrontRight);
        System.out.println("Barriere links & rechts: " + this.stateLeftRight);
        System.out.println("Barriere vorne & links & rechts: " + this.stateFrontLeftRight);

        learningAlgorithmus.printQTable();
    }

    // Roboter Interface Methods
    @Override
    public void doAction(int action) {
        this.isBumped = false;
        switch(action){
            case DRIVE_FORWARD:
                this.forward();
                this.drived_forward++;
                break;
            case DRIVE_ROTATE_RIGHT:
                this.right();
                this.drived_rotateRight++;
                break;
            case DRIVE_BACKWARD:
                this.backward();
                this.drived_backward++;
                break;
            case DRIVE_ROTATE_LEFT:
                this.left();
                this.drived_rotateLeft++;
                break;
        }
    }

    @Override
    public void fetchData(int pos) {

    }

    @Override
    public int findBarrier() {
        // bumped
        if(isBumped){
            this.stateBumped++;
            return 8;
        }
        // front = 1
        if (distanceData[0] > MIN_DISTANCE && distanceData[1] < MIN_DISTANCE && distanceData[2] > MIN_DISTANCE) {
            // front + bumped
//			if (isBumped()) {
//				return 8;
//			}
            this.stateFront++;
            return 1;
        }
        // left = 2
        if (distanceData[0] < MIN_DISTANCE && distanceData[1] > MIN_DISTANCE && distanceData[2] > MIN_DISTANCE) {
            // left + bumped
//			if (isBumped()) {
//				return 9;
//			}
            this.stateLeft++;
            return 2;
        }
        // right = 3
        if (distanceData[0] > MIN_DISTANCE && distanceData[1] > MIN_DISTANCE && distanceData[2] < MIN_DISTANCE) {
            // right + bumped
//			if (isBumped()) {
//				return 10;
//			}
            this.stateRight++;
            return 3;
        }
        // front + left = 4
        if (distanceData[0] < MIN_DISTANCE && distanceData[1] < MIN_DISTANCE && distanceData[2] > MIN_DISTANCE) {
            // front + left + bumped
//			if (isBumped()) {
//				return 11;
//			}
            this.stateFrontLeft++;
            return 4;
        }
        // front + right = 5
        if (distanceData[0] > MIN_DISTANCE && distanceData[1] < MIN_DISTANCE && distanceData[2] < MIN_DISTANCE) {
            // front + right + bumped
//			if (isBumped()) {
//				return 12;
//			}
            this.stateFrontRight++;
            return 5;
        }
        // left + right = 6
        if (distanceData[0] < MIN_DISTANCE && distanceData[1] > MIN_DISTANCE && distanceData[2] < MIN_DISTANCE) {
            // left + right + bumped
//			if (isBumped()) {
//				return 13;
//			}
            this.stateLeftRight++;
            return 6;
        }
        // front + left + right = 7
        if (distanceData[0] < MIN_DISTANCE && distanceData[1] < MIN_DISTANCE && distanceData[2] < MIN_DISTANCE) {
            // front + left + right + bumped
//			if (isBumped()) {
//				return 14;
//			}
            this.stateFrontLeftRight++;
            return 7;
        }
        this.stateNoBarrier++;
        return 0;
    }

    @Override
    public void look() {
        this.distanceDataFieldsLeft.clear();
        this.distanceDataFieldsFront.clear();
        this.distanceDataFieldsRight.clear();

        switch(this.headDirection){
            case 0:
                this.distanceDataFieldsLeft.addAll(getDistanceDataLeft(-2));
                this.distanceData[0] = this.distanceDataFieldsLeft.size();
                this.distanceDataFieldsFront.addAll(getDistanceDataAbove(-1));
                this.distanceData[1] = this.distanceDataFieldsFront.size();
                this.distanceDataFieldsRight.addAll(getDistanceDataRight(2));
                this.distanceData[2] = this.distanceDataFieldsRight.size();
                break;
            case 1:
                System.out.println("hd: " + this.headDirection);
                this.distanceDataFieldsLeft.addAll(getDistanceDataAbove(0));
                this.distanceData[0] = this.distanceDataFieldsLeft.size();
                this.distanceDataFieldsFront.addAll(getDistanceDataRight(1));
                this.distanceData[1] = this.distanceDataFieldsFront.size();
                this.distanceDataFieldsRight.addAll(getDistanceDataBelow(-2));
                this.distanceData[2] = this.distanceDataFieldsRight.size();
                break;
            case 2:
                this.distanceDataFieldsLeft.addAll(getDistanceDataRight(2));
                this.distanceData[0] = this.distanceDataFieldsLeft.size();
                this.distanceDataFieldsFront.addAll(getDistanceDataBelow(1));
                this.distanceData[1] = this.distanceDataFieldsFront.size();
                this.distanceDataFieldsRight.addAll(getDistanceDataLeft(-2));
                this.distanceData[2] = this.distanceDataFieldsRight.size();
                break;
            case 3:
                this.distanceDataFieldsLeft.addAll(getDistanceDataBelow(2));
                this.distanceData[0] = this.distanceDataFieldsLeft.size();
                this.distanceDataFieldsFront.addAll(getDistanceDataLeft(-1));
                this.distanceData[1] = this.distanceDataFieldsFront.size();
                this.distanceDataFieldsRight.addAll(getDistanceDataAbove(-2));
                this.distanceData[2] = this.distanceDataFieldsRight.size();
                break;
        }

        this.drived_look++;
        callGuiUpdateFunction();
    }

    @Override
    public boolean isBumped() {
        this.reward = this.reward + SimulationRobot.REWARD_BUMPED;
        this.drived_isBumped++;
        this.isBumped = true;
        return true;
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
        clearDistanceData();
        if (Controller_MainGUI.mazeFreeFieldsRotateLeftForward(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber), SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeRobots().get(this.uniqueIndexNumberOfMazeRobot))) {
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
            callGuiUpdateFunction();
            this.reward = this.reward + SimulationRobot.REWARD_NOT_BUMPED;
        } else {
            isBumped();
        }
    }

    @Override
    public void right() {
        clearDistanceData();
        if (Controller_MainGUI.mazeFreeFieldsRotateRightForward(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber), SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeRobots().get(this.uniqueIndexNumberOfMazeRobot))) {
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
            callGuiUpdateFunction();
            this.reward = this.reward + SimulationRobot.REWARD_NOT_BUMPED;
        } else {
            isBumped();
        }
    }

    // Robot Table
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
