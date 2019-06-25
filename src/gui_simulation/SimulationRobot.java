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

    private void callGUIUpdateMethod(){
        if((int)SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getNr() == SimulationMaze.getSelectedMaze().getNr()){
            if(this.uniqueIndexNumberOfMazeRobot == SimulationMaze.getSelectedMaze().getSelectedRobot().getUniqueIndexNumberOfMazeRobot()) {
                SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getFxmlMainController().updateMaze(true);
            }
        }
    }

    private void moveUp() {
        clearDistanceData();
        System.out.println("UP");
        if (Controller_MainGUI.mazeFreeFieldsUp(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber), SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeRobots().get(this.uniqueIndexNumberOfMazeRobot))) {
            for (int i = 0; i < this.position.length; i++) {
                this.position[i] = this.position[i] - SimulationMaze.getSelectedMaze().getMazeSizeY();
            }

            changeHeadPosition();
            callGUIUpdateMethod();
        } else {
            isBumped();
        }
    }

    private void moveRight() {
        clearDistanceData();
        System.out.println("RIGHT");
        if (Controller_MainGUI.mazeFreeFieldsRight(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber), SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeRobots().get(this.uniqueIndexNumberOfMazeRobot))) {
            for (int i = 0; i < this.position.length; i++) {
                this.position[i] = this.position[i] + 1;
            }

            changeHeadPosition();
            callGUIUpdateMethod();
        } else {
            isBumped();
        }
    }

    private void moveDown() {
        clearDistanceData();
        System.out.println("DOWN");
        if (Controller_MainGUI.mazeFreeFieldsDown(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber), SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeRobots().get(this.uniqueIndexNumberOfMazeRobot))) {
            for (int i = 0; i < this.position.length; i++) {
                this.position[i] = this.position[i] + SimulationMaze.getSelectedMaze().getMazeSizeY();
            }

            changeHeadPosition();
            callGUIUpdateMethod();
        } else {
            isBumped();
        }
    }

    private void moveLeft() {
        clearDistanceData();
        System.out.println("LEFT");
        if (Controller_MainGUI.mazeFreeFieldsLeft(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber), SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeRobots().get(this.uniqueIndexNumberOfMazeRobot))) {
            for (int i = 0; i < this.position.length; i++) {
                this.position[i] = this.position[i] - 1;
            }

            changeHeadPosition();
            callGUIUpdateMethod();
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

    private ArrayList<Integer> getDistanceDataAbove(){
        ArrayList<Integer> distanceDataAbove = new ArrayList<>();

        boolean freeField = true;
        for(int y = -1; freeField && this.headPosition.get(0) + y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY() >= 0; y--){
            if(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getIndexMazeFreeFields().contains(this.headPosition.get(0) + y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY())) {
                distanceDataAbove.add(this.headPosition.get(0) + y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY());
            } else {
                freeField = false;
            }
        }

        return distanceDataAbove;
    }

    private ArrayList<Integer> getDistanceDataRight(){
        ArrayList<Integer> distanceDataRight = new ArrayList<>();

        boolean freeField = true;
        int x = 1;
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

    private ArrayList<Integer> getDistanceDataBelow(){
        ArrayList<Integer> distanceDataBelow = new ArrayList<>();

        boolean freeField = true;
        for(int y = 1; freeField && this.headPosition.get(0) + y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY() <= SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY() * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeX(); y++){
            if(SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getIndexMazeFreeFields().contains(this.headPosition.get(0) + y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY())){
                distanceDataBelow.add(this.headPosition.get(0) + y * SimulationMaze.getMazeFiles().get(this.robotMazeIndexNumber).getMazeSizeY());
            } else {
                freeField = false;
            }
        }

        return distanceDataBelow;
    }

    private ArrayList<Integer> getDistanceDataLeft(){
        ArrayList<Integer> distanceDataLeft = new ArrayList<>();

        boolean freeFields = true;
        int x = -1;
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
        this.distanceDataFieldsLeft.clear();
        this.distanceDataFieldsFront.clear();
        this.distanceDataFieldsRight.clear();

        switch(this.headDirection){
            case 0:
                this.distanceDataFieldsLeft.addAll(getDistanceDataLeft());
                this.distanceData[0] = this.distanceDataFieldsLeft.size();
                this.distanceDataFieldsFront.addAll(getDistanceDataAbove());
                this.distanceData[1] = this.distanceDataFieldsFront.size();
                this.distanceDataFieldsRight.addAll(getDistanceDataRight());
                this.distanceData[2] = this.distanceDataFieldsRight.size();
                break;
            case 1:
                this.distanceDataFieldsLeft.addAll(getDistanceDataAbove());
                this.distanceData[0] = this.distanceDataFieldsLeft.size();
                this.distanceDataFieldsFront.addAll(getDistanceDataRight());
                this.distanceData[1] = this.distanceDataFieldsFront.size();
                this.distanceDataFieldsRight.addAll(getDistanceDataBelow());
                this.distanceData[2] = this.distanceDataFieldsRight.size();
                break;
            case 2:
                this.distanceDataFieldsLeft.addAll(getDistanceDataRight());
                this.distanceData[0] = this.distanceDataFieldsLeft.size();
                this.distanceDataFieldsFront.addAll(getDistanceDataBelow());
                this.distanceData[1] = this.distanceDataFieldsFront.size();
                this.distanceDataFieldsRight.addAll(getDistanceDataLeft());
                this.distanceData[2] = this.distanceDataFieldsRight.size();
                break;
            case 3:
                this.distanceDataFieldsLeft.addAll(getDistanceDataBelow());
                this.distanceData[0] = this.distanceDataFieldsLeft.size();
                this.distanceDataFieldsFront.addAll(getDistanceDataLeft());
                this.distanceData[1] = this.distanceDataFieldsFront.size();
                this.distanceDataFieldsRight.addAll(getDistanceDataAbove());
                this.distanceData[2] = this.distanceDataFieldsRight.size();
                break;
        }

        callGUIUpdateMethod();
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
        clearDistanceData();
        System.out.println("Linksvorwärtsrotation");
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
            callGUIUpdateMethod();
        } else {
            isBumped();
        }
    }

    @Override
    public void right() {
        clearDistanceData();
        System.out.println("Rechtsvorwärtsrotation");
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
            callGUIUpdateMethod();
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
