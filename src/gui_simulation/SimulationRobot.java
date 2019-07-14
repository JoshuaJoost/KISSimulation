package gui_simulation;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;

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

    //Rewards - Learning Algorithmus
    private static final double REWARD_BUMPED = -1;
    private static final double REWARD_DRIVE_BACK = 0;
    private static final double REWARD_DRIVE_FORWARD = 0;
    private static final double REWARD_DRIVE_ROTATE_RIGHT = 0;
    private static final double REWARD_DRIVE_ROTATE_LEFT = 0;
    private static final double REWARD_DRIVE_TARGET = 2;

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

    private int stateCanRotateRight = 0;
    private int stateCanRotateLeft = 0;
    private int stateCanRotateLeftRight = 0;
    private int stateFront = 0;
    private int stateFrontLeft = 0;
    private int stateFrontRight = 0;
    private int stateLeft = 0;
    private int stateRight = 0;
    private int stateLeftRight = 0;
    private int stateFrontLeftRight = 0;
    private int stateBumped = 0;
    private int stateNoBarrier = 0;
    private int stateBumpedFront = 0;
    private int stateBumpedLeft = 0;
    private int stateBumpedRight = 0;
    private int stateBumpedFrontLeft = 0;
    private int stateBumpedFrontRight = 0;
    private int stateBumpedFrontLeftRight = 0;
    private int stateBumpedLeftRight = 0;
    private int stateBumpedCanRotateRight = 0;
    private int stateBumpedCanRotateLeft = 0;
    private int stateBumpedCanRotateLeftRight = 0;

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
    private QLearningAgent lerningAlgorithmus = new QLearningAgent();
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

    public Integer getHeadDirection() {
        return this.headDirection;
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
    //*//

    private void changeHeadPosition() {
        this.headPosition.clear();
        int[] sortedPos = this.position;
        Arrays.sort(sortedPos);

//        System.out.println("HD: " + this.headDirection);
//        System.out.println("SizeX: " + this.sizeX);
//        System.out.println("SizeY: " + this.sizeY);
        int headPosition;
        int x;
        int y;
        switch(this.headDirection){
            case 0:
                headPosition = (int) Math.floor((double) this.sizeX / 2);
                this.headPosition.add(sortedPos[headPosition]);
                break;
            case 1:
                y = (int) Math.ceil((double) this.sizeY / 2);
                headPosition = y * this.sizeX - 1;
                this.headPosition.add(sortedPos[headPosition]);
                break;
            case 2:
                x = (int) Math.floor((double) this.sizeX / 2);
                headPosition = this.sizeX * this.sizeY - 1 - x; // -x
                this.headPosition.add(sortedPos[headPosition]);
                break;
            case 3:
                y = (int) Math.ceil((double) this.sizeY / 2);
                headPosition = y * this.sizeX - this.sizeX;
                this.headPosition.add(sortedPos[headPosition]);
                break;
        }
//        if (this.sizeX > 0 && this.sizeY > 0) {
//            switch (this.headDirection) {
//                case 0:
////                    if (this.headSize > 0) {
////                        if (this.sizeX == 1) {
////                            this.headPosition.add(sortedPos[0]);
////                        } else if (this.sizeX == 2) {
////                            this.headPosition.add(sortedPos[1]);
////                        } else {
////                            for (int x = 0; x < this.headSize; x++) {
////                                this.headPosition.add(sortedPos[this.sizeX - this.headSize - 1 + x]);
////                            }
////                        }
////                    }
//                    this.headPosition.add(sortedPos[1]);
//                    break;
//                case 1:
//                    if (this.headSize > 0) {
//                        if (this.sizeY == 1 || this.sizeY == 2) {
//                            this.headPosition.add(sortedPos[this.sizeX * this.sizeY - 1]);
//                        } else {
//                            for (int y = 2; y < this.headSize + 2; y++) {
//                                this.headPosition.add(sortedPos[y * this.sizeX - 1]);
//                            }
//                        }
//                    }
//                    break;
//                case 2:
//                    if (this.headSize > 0) {
//                        if (this.sizeX == 1) {
//                            this.headPosition.add(sortedPos[this.sizeY - 1]);
//                        } else if (this.sizeX == 2) {
//                            this.headPosition.add(sortedPos[this.sizeX * this.sizeY - 1 - 1]);
//                        } else {
//                            for (int x = 0; x < this.headSize; x++) {
//                                this.headPosition.add(sortedPos[this.sizeX * this.sizeY - 1 - this.headSize + x]);
//                            }
//                        }
//                    }
//                    break;
//                case 3:
//                    if (this.headSize > 0) {
//                        if (this.sizeY == 1 || this.sizeY == 2) {
//                            this.headPosition.add(sortedPos[0]);
//                        } else {
//                            for (int y = 1; y < this.headSize + 1; y++) {
//                                this.headPosition.add(sortedPos[y * this.sizeX]);
//                            }
//                        }
//                    }
//                    break;
//            }
//        }
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

    @Override
    public void left(){
        clearDistanceData();
        if(rotationLeftFree()){
            ArrayList<Integer> newPosition = new ArrayList<>();
            switch(this.headDirection){
                case 0:
                    printRobotArrayIndexNumbers();
                    for(int y = 0, i = this.sizeX * this.sizeY - this.sizeX - y; y / this.sizeX < this.sizeX; y += this.sizeX, i = this.sizeX * this.sizeY - this.sizeX - y){
                        for(int xi = this.sizeY; xi > 0; xi--){
                            newPosition.add(this.position[i] - xi);
                        }
                    }
                    break;
                case 1:
                    printRobotArrayIndexNumbers();
                    for(int x = 0, i = x; x < this.sizeY; x++, i = x){
                        for(int y = this.sizeX; y > 0; y--){
                            newPosition.add(this.position[i] - y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY());
                        }
                    }
                    break;
                case 2:
                    printRobotArrayIndexNumbers();
                    for(int y = 1, i = y * this.sizeX - 1; y - 1 < this.sizeX; y++, i = y * this.sizeX - 1){
                        for(int x = this.sizeY; x > 0; x--){
                            newPosition.add(this.position[i] + x);
                        }
                    }
                    break;
                case 3:
                    printRobotArrayIndexNumbers();
                    for(int x = 0, i = this.sizeX * this.sizeY - 1 - x; x < this.sizeY; x++, i = this.sizeX * this.sizeY - 1 - x){
                        for(int y = this.sizeX; y > 0; y--){
                            newPosition.add(this.position[i] + y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY());
                        }
                    }
                    break;
            }
            this.position = OwnUtils.convertArrayListToIntArray(newPosition);

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
    public void right(){
        clearDistanceData();
        if(rotationRightFree()){
            ArrayList<Integer> newPosition = new ArrayList<>();
            switch(this.headDirection){
                case 0:
                    for(int y = 0, i = this.sizeX * this.sizeY - 1 - y; y / this.sizeX < this.sizeX; y += this.sizeX, i = this.sizeX * this.sizeY - 1 - y){
                        for(int x = 1; x < this.sizeY + 1; x++){
                            newPosition.add(this.position[i] + x);
                        }
                    }
                    break;
                case 1:
                    for(int x = 0, i = this.sizeX * this.sizeY - this.sizeX + x; x < this.sizeY; x++, i = this.sizeX * this.sizeY - this.sizeX + x){
                        for(int y = 1; y < this.sizeX + 1; y++){
                            newPosition.add(this.position[i] + y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY());
                        }
                    }
                    break;
                case 2:
                    for(int y = 0, i = y * this.sizeX; y < this.sizeX; y++, i = y * this.sizeX){
                        for(int x = 1; x < this.sizeY + 1; x++){
                            newPosition.add(this.position[i] - x);
                        }
                    }
                    break;
                case 3:
                    for(int x = 0, i = this.sizeX - this.sizeY + x; x < this.sizeY; x++, i = this.sizeX - this.sizeY + x){
                        for(int y = 1; y < this.sizeX + 1; y++){
                            newPosition.add(this.position[i] - y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY());
                        }
                    }
                    break;
            }
            this.position = OwnUtils.convertArrayListToIntArray(newPosition);

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
//    @Override
//    public void right() {
//        clearDistanceData();
//        if (rotationRightFree()) {
//            switch (this.headDirection) {
//                case 0:
//                    for (int y = 0, x = -1, xv = 1, yv = 0; y < this.sizeY; y++, x = -1, xv = xv - this.sizeX + 2 - 1, yv = yv + this.sizeX + 1) {
//                        for (int xi = 0; xi < this.sizeX; xi++, x--, xv++, yv--) {
//                            this.position[this.sizeX * this.sizeY - y * this.sizeX + x] = this.position[this.sizeX * this.sizeY - y * this.sizeX + x] + xv + yv * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY();
//                        }
//                    }
//                    break;
//                case 1:
//                    for (int y = 1, x = 0, xv = 0, yv = 1; y < this.sizeY + 1; y++, x = 0, xv = xv + this.sizeX + 1, yv = yv - this.sizeX + 2 - 1) {
//                        for (int xi = 0; xi < this.sizeX; xi++, x++, xv--, yv++) {
//                            this.position[this.sizeX * this.sizeY - y * this.sizeX + x] = this.position[this.sizeX * this.sizeY - y * this.sizeX + x] + xv + yv * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY();
//                        }
//                    }
//                    break;
//                case 2:
//                    for (int y = 0, x = 0, xv = -1, yv = 0; y < this.sizeY; y++, x = 0, xv = xv + this.sizeX - 2 + 1, yv = yv - this.sizeX - 1) {
//                        for (int xi = 0; xi < this.sizeX; xi++, x++, xv--, yv++) {
//                            this.position[y * (this.sizeY - 1) + x] = this.position[y * (this.sizeY - 1) + x] + xv + yv * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY();
//                        }
//                    }
//                    break;
//                case 3:
//                    for (int y = 1, x = 0, xv = 0, yv = -1; y < this.sizeY + 1; y++, x = 0, xv = xv - this.sizeX - 1, yv = yv + this.sizeX - 2 + 1) {
//                        for (int xi = 0; xi < this.sizeX; xi++, x--, xv++, yv--) {
//                            this.position[y * this.sizeX - 1 + x] = this.position[y * this.sizeX - 1 + x] + xv + yv * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY();
//                        }
//                    }
//                    break;
//            }
//
//            this.headDirection++;
//            if (this.headDirection > 3) {
//                this.headDirection = 0;
//            }
//
//            int tmpSizeX = this.sizeX;
//            this.sizeX = this.sizeY;
//            this.sizeY = tmpSizeX;
//
//            changeHeadPosition();
//            this.isBumped = false;
//        } else {
//            isBumped();
//        }
//    }

    /*
     * Bewegungen prüfen
     * */
    private boolean forwardFree() {
        switch (this.headDirection) {
            case 0:
                return SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).aboveFree(this);
            case 1:
                //TODO
                return true;
            //return SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).rightFree(this);
            case 2:
                return SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).belowFree(this);
            case 3:
                // TODO
                return true;
            //return SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).leftFree(this);
        }
        throw new IllegalStateException("Fehler in Kopfstellung: " + this.headDirection);
    }

    private boolean backwardFree() {
        switch (this.headDirection) {
            case 0:
                return SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).belowFree(this);
            case 1:
                //TODO
                return true;
            //return SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).leftFree(this);
            case 2:
                return SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).aboveFree(this);
            case 3:
                //TODO
                return true;
            //return SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).rightFree(this);
        }
        throw new IllegalStateException("Fehler in Kopfstellung: " + this.headDirection);
    }

    private boolean rotationLeftFree() {
        //TODO
        return true;
        //return SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).rotationForwardLeftFree(this);
    }

    private boolean rotationRightFree() {
        //TODO
        return true;
        //return SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).rotationForwardRightFree(this);
    }

    @Override
    public boolean isBumped() {
        this.isBumped = true;
        return true;
    }

    // TODO
    @Override
    public boolean isGoal() {
        return false;
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
//        if (SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).rightFree(this)) {
//            moveRight();
//        }
        //TODO
        moveRight();
    }

    public void keyboardMoveLeft() {
//        if (SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).leftFree(this)) {
//            moveLeft();
//        }
        //TODO
        moveLeft();
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

    private void setRobotBackToStartPosition() {
        for (int i = 0; i < this.startPosition.length; i++) {
            this.position[i] = this.startPosition[i];
        }

        int tmpHeadDirection = this.startHeadDirection;
        this.headDirection = tmpHeadDirection;

        changeHeadPosition();

        switch (this.headDirection) {
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
        for (int i = 0; i < this.position.length; i++) {
            this.startPosition[i] = this.position[i];
        }

        // Bei langen Rechenoperationen blockiert der GUI-Thread, um auf dieser wieder Code
        // zum Laufen zu bekommen muss man einem Task ein Platform.runlater() übergeben
        // dieser dann gethreated wird
        Task<Void> updateGui = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> SimulationMaze.getSelectedMaze().getSelectedRobot().callGuiUpdateFunction());
                return null;
            }
        };

        System.out.println("----------------------------------- Lerndurchlauf 1 -----------------------------------");
        System.out.println("----------------------------------- Epsilon-lernen ------------------------------------");
        System.out.println("---------------------------------------------------------------------------------------");
        this.lerningAlgorithmus.setEpsilon(1);
        for (int j = 0; j < 100; j++) {
            setRobotBackToStartPosition();
            for (int i = 0; i < 1000000 && !targetReached(); i++) {
                look();
                int s = findBarrier();
                int a = this.lerningAlgorithmus.chooseAction(s);
                doAction(a);

                if (targetReached()) {
                    this.reward = SimulationRobot.REWARD_DRIVE_TARGET;
                }

                look();
                int sNext = findBarrier();
                this.lerningAlgorithmus.learn(s, sNext, a, this.reward);
            }
            System.out.print("I: " + (j + 1) + " ");
            if (targetReached()) {
                System.out.print("Ziel gefunden!");
            } else {
                System.out.print("Ziel -nicht- gefunden!");
            }
            System.out.println();
        }

        System.out.println("----------------------------------- Lerndurchlauf 2 -----------------------------------");
        System.out.println("----------------------------------- Degradierendes-Epsilon-lernen ---------------------");
        System.out.println("---------------------------------------------------------------------------------------");
        this.lerningAlgorithmus.setEpsilon(1);
        for (int j = 0, e = 0; j < 100; j++, e += 0.01) {
            setRobotBackToStartPosition();

            double newEpsilon = this.lerningAlgorithmus.getEpsilon() - e;
            this.lerningAlgorithmus.setEpsilon(newEpsilon);
            if (this.lerningAlgorithmus.getEpsilon() < 0) {
                this.lerningAlgorithmus.setEpsilon(1);
                e = 0;
            }

            for (int i = 0; i < 1000000 && !targetReached(); i++) {
                look();
                int s = findBarrier();
                int a = this.lerningAlgorithmus.chooseAction(s);
                doAction(a);

                if (targetReached()) {
                    this.reward = SimulationRobot.REWARD_DRIVE_TARGET;
                }

                look();
                int sNext = findBarrier();
                this.lerningAlgorithmus.learn(s, sNext, a, this.reward);
            }

            System.out.print("I: " + (j + 1) + " ");
            if (targetReached()) {
                System.out.print("Ziel gefunden!");
            } else {
                System.out.println("Ziel -nicht- gefunden!");
            }
            System.out.println();
        }

        System.out.println("----------------------------------- Lerndurchlauf 3 -----------------------------------");
        System.out.println("----------------------------------- Kleines-Epsilon-lernen ----------------------------");
        System.out.println("---------------------------------------------------------------------------------------");
        this.lerningAlgorithmus.setEpsilon(0.2);
        for (int j = 0; j < 100; j++) {
            setRobotBackToStartPosition();

            for (int i = 0; i < 1000000 && !targetReached(); i++) {
                look();
                int s = findBarrier();
                int a = this.lerningAlgorithmus.chooseAction(s);
                doAction(a);

                if (targetReached()) {
                    this.reward = SimulationRobot.REWARD_DRIVE_TARGET;
                }

                look();
                int sNext = findBarrier();
                this.lerningAlgorithmus.learn(s, sNext, a, this.reward);
            }

            System.out.print("I: " + (j + 1) + " ");
            if (targetReached()) {
                System.out.print("Ziel gefunden!");
            } else {
                System.out.println("Ziel -nicht- gefunden!");
            }
            System.out.println();
        }

        System.out.println("----------------------------------- Lerndurchlauf 4 -----------------------------------");
        System.out.println("----------------------------------- Q-Tabelle lernen ----------------------------------");
        System.out.println("---------------------------------------------------------------------------------------");
        this.lerningAlgorithmus.setEpsilon(0.1);
        for (int j = 0; j < 100; j++) {
            setRobotBackToStartPosition();

            for (int i = 0; i < 1000000 && !targetReached(); i++) {
                look();
                int s = findBarrier();
                int a = this.lerningAlgorithmus.chooseAction(s);
                doAction(a);

                if (targetReached()) {
                    this.reward = SimulationRobot.REWARD_DRIVE_TARGET;
                }

                look();
                int sNext = findBarrier();
                this.lerningAlgorithmus.learn(s, sNext, a, this.reward);
            }

            System.out.print("I: " + (j + 1) + " ");
            if (targetReached()) {
                System.out.print("Ziel gefunden!");
            } else {
                System.out.print("Ziel -nicht- gefunden!");
            }
            System.out.println();
        }

        System.out.println("----------------------------------- Probedurchlauf ------------------------------------");
        System.out.println("---------------------------------------------------------------------------------------");

        this.drived_look = 0;
        this.drived_rotateLeft = 0;
        this.drived_backward = 0;
        this.drived_rotateRight = 0;
        this.drived_forward = 0;
        this.try_drived_forward = 0;
        this.try_drived_backward = 0;
        this.try_drived_rotateLeft = 0;
        this.try_drived_rotateRight = 0;

        this.stateBumped = 0;
        this.stateFront = 0;
        this.stateLeft = 0;
        this.stateRight = 0;
        this.stateFrontLeft = 0;
        this.stateFrontRight = 0;
        this.stateLeftRight = 0;
        this.stateFrontLeftRight = 0;
        this.stateNoBarrier = 0;

        this.lerningAlgorithmus.setEpsilon(0);
        int j = 0;
        setRobotBackToStartPosition();
        for (int i = 0; i < 1000000 && !targetReached(); i++, j++) {
            look();
            int s = findBarrier();
            int a = this.lerningAlgorithmus.chooseAction(s);
            doAction(a);

            if (targetReached()) {
                this.reward = SimulationRobot.REWARD_DRIVE_TARGET;
            }
//            System.out.println("s:" + s);
            switch (s){
                case 0: this.stateNoBarrier++;
                break;
                case 1: this.stateFront++;
                break;
                case 2: this.stateLeft++;
                break;
                case 3: this.stateRight++;
                break;
                case 4: this.stateFrontLeft++;
                break;
                case 5: this.stateFrontRight++;
                break;
                case 6: this.stateLeftRight++;
                break;
                case 7: this.stateFrontLeftRight++;
                break;
                case 8: this.stateBumped++;
                break;
            }

            look();
            int sNext = findBarrier();
            this.lerningAlgorithmus.learn(s, sNext, a, this.reward);
        }

        if (!targetReached()) {
            System.out.println("Ziel -nicht- gefunden!");
        } else {
            System.out.println("Ziel gefunden");
        }

        new Thread(updateGui).start();
        this.lerningAlgorithmus.printQTable();
        System.out.println("Bewegungsmuster:");
        System.out.println("vorwärts: " + this.drived_forward);
        System.out.println("vorwärts angestoßen: " + this.try_drived_forward);
        System.out.println("Zurück:" + this.drived_backward);
        System.out.println("hinten angestoßen: " + this.try_drived_backward);
        System.out.println("Rechtsrotation: " + this.drived_rotateRight);
        System.out.println("Bei Rechtsrotation angestoßen: " + this.try_drived_rotateRight);
        System.out.println("Linksrotation: " + this.drived_rotateLeft);
        System.out.println("Bei Linksrotation angestoßen: " + this.try_drived_rotateLeft);
        System.out.println("Iterationen: " + (j + 1));
        System.out.println("Angestoßen: " + (this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateRight + this.try_drived_rotateLeft));
        if (j > 0) {
            System.out.println("Fehlerquote: " + ((this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateRight + this.try_drived_rotateLeft) * 100 / j) + "%");
        } else {
            System.out.println("Fehlerquote: " + ((this.try_drived_forward + this.try_drived_backward + this.try_drived_rotateRight + this.try_drived_rotateLeft) * 100) + "%");
        }
        System.out.println();
        System.out.println("Zustandsmuster:");
        System.out.println("Barriere Vorne: " + this.stateFront);
        System.out.println("Barriere Links: " + this.stateLeft);
        System.out.println("Barriere Rechts: " + this.stateRight);
        System.out.println("Barriere Vorne&Links: " + this.stateFrontLeft);
        System.out.println("Barriere Vorne&Rechts: " + this.stateFrontRight);
        System.out.println("Barriere Links&Rechts: " + this.stateLeftRight);
        System.out.println("Barriere Vorne&Links&Rechts: " + this.stateFrontLeftRight);
        System.out.println("Zustand keine Barriere: " + this.stateNoBarrier);
        System.out.println("Zustand Angestoßen: " + this.stateBumped);
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
        // bumped
        if (isBumped) {
//            this.stateBumped++;
            return 8;
        }
        // front = 1
        if (distanceData[0] >= MIN_DISTANCE && distanceData[1] < MIN_DISTANCE && distanceData[2] >= MIN_DISTANCE) {
            // front + bumped
//			if (isBumped()) {
//				return 8;
//			}
//            System.out.println("Vorne");
//            this.stateFront++;
            return 1;
        }
        // left = 2
        if (distanceData[0] < MIN_DISTANCE && distanceData[1] >= MIN_DISTANCE && distanceData[2] >= MIN_DISTANCE) {
            // left + bumped
//			if (isBumped()) {
//				return 9;
//			}
//            this.stateLeft++;
            return 2;
        }
        // right = 3
        if (distanceData[0] >= MIN_DISTANCE && distanceData[1] >= MIN_DISTANCE && distanceData[2] < MIN_DISTANCE) {
            // right + bumped
//			if (isBumped()) {
//				return 10;
//			}
//            this.stateRight++;
            return 3;
        }
        // front + left = 4
        if (distanceData[0] < MIN_DISTANCE && distanceData[1] < MIN_DISTANCE && distanceData[2] >= MIN_DISTANCE) {
            // front + left + bumped
//			if (isBumped()) {
//				return 11;
//			}
//            this.stateFrontLeft++;
            return 4;
        }
        // front + right = 5
        if (distanceData[0] >= MIN_DISTANCE && distanceData[1] < MIN_DISTANCE && distanceData[2] < MIN_DISTANCE) {
            // front + right + bumped
//			if (isBumped()) {
//				return 12;
//			}
//            this.stateFrontRight++;
            return 5;
        }
        // left + right = 6
        if (distanceData[0] < MIN_DISTANCE && distanceData[1] >= MIN_DISTANCE && distanceData[2] < MIN_DISTANCE) {
            // left + right + bumped
//			if (isBumped()) {
//				return 13;
//			}
//            this.stateLeftRight++;
            return 6;
        }
        // front + left + right = 7
        if (distanceData[0] < MIN_DISTANCE && distanceData[1] < MIN_DISTANCE && distanceData[2] < MIN_DISTANCE) {
            // front + left + right + bumped
//			if (isBumped()) {
//				return 14;
//			}
//            this.stateFrontLeftRight++;
            return 7;
        }
//        this.stateNoBarrier++;
        return 0;
    }

    /*
    * Debugg Funktionen
    * */
    private void printRobotArrayIndexNumbers(){
        int maxLength = ("" + this.position.length).length();

        for(int i = 0; i < this.position.length; i++) {
            String indexNumber = "" + i;

            while(indexNumber.length() < maxLength){
                indexNumber = " " + indexNumber;
            }

            System.out.print(indexNumber + " | ");

            if(i > 0) {
                if((i + 1) % this.sizeX == 0){
                    System.out.println();
                }
            }
        }
        System.out.println();
    }

}
