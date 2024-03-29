package gui_simulation;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SimulationMaze {

    // File
    // TODO Controller umbauen, soll nur noch hier drauf zugreifen
    public static final char mazeWallSymbol = '#';
    public static final char mazeVoidSymbol = ' ';
    public static final char mazeTargetSymbol = 't';
    public static final char mazeCornerSymbol = 'e';

    public static final Color mazeVoidColor = Color.rgb(255, 255, 255);
    public static final Color mazeWallColor = Color.rgb(0, 0, 0);
    public static final Color mazeTargetColor = Color.rgb(255, 0, 0);
    public static final Color mazeCornerColor = Color.rgb(166, 175, 189);
    public static final Color mazeErrorColor = Color.rgb(0, 255, 246);
    //Debug Colors
    private static final Color debugMazeRotationTopColor = Color.rgb(155, 155, 0);
    private static final Color debugMazeRotationSideColor = Color.rgb(155, 155, 0);
    private static final Color debugMazeRotationTargetPositionColor = Color.rgb(155, 155, 0);
    private static final Color debugMazeFrontColor = Color.rgb(155, 155, 0);
    private static final Color debugMazeBackwardsColor = Color.rgb(155, 155, 0);
    private static final Color debugMazeRightColor = Color.rgb(155, 155, 0);
    private static final Color debugMazeLeftColor = Color.rgb(155, 155, 0);
    private static final Color debugMazeDistanceAboveColor = Color.rgb(155, 155, 0);
    private static final Color debugMazeDistanceBelowColor = Color.rgb(155, 155, 0);
    private static final Color debugMazeDistanceRightColor = Color.rgb(155, 155, 0);
    private static final Color debugMazeDistanceLeftColor = Color.rgb(155, 155, 0);

    private static int numberOfMazeFiles = 0;
    private static ArrayList<SimulationMaze> mazeFiles = new ArrayList<>();
    private static Integer selectedMazeIndexNumber = null;
    private static final SimpleStringProperty SELECTED_TEXT = new SimpleStringProperty("ja");
    private static final SimpleStringProperty NOT_SELECTED_TEXT = new SimpleStringProperty("");

    // Labyrinth Table
    private final Integer nr;
    private final SimpleStringProperty FILE_NAME;
    private SimpleStringProperty selectedMazeText;
    // Labyrinth Werte
    private final Integer mazeSizeX;
    private final Integer mazeSizeY;
    private final ArrayList<Integer> indexMazeFreeFields;
    private final ArrayList<Integer> indexMazeTargetFields;
    private final ArrayList<Rectangle> mazeDrawFields;
    // Labyrinth Roboter
    private ArrayList<SimulationRobot> mazeRobots = new ArrayList<>();
    private Integer selectedRobotNumber = null;
    public int uniqueIndexNumberOfMazeRobot = 0;
    // Controller
    private final Controller_MainGUI FXML_MAIN_CONTROLLER;
    // Debugg
    private static boolean debugShowLeftRotation = false;
    private static boolean debugShowRightRotation = false;
    private static boolean debugShowFront = false;
    private static boolean debugShowBelow = false;
    private static boolean debugShowRight = false;
    private static boolean debugShowLeft = false;
    private static boolean debugShowDistanceAbove = false;
    private static boolean debugShowDistanceBelow = false;
    private static boolean debugShowDistanceRight = false;
    private static boolean debugShowDistanceLeft = false;

    private SimulationMaze(String filename, int mazePaneX, int mazePaneY, Controller_MainGUI fxmlMainController) {
        nr = (++numberOfMazeFiles);
        FILE_NAME = new SimpleStringProperty(filename);
        selectedMazeText = new SimpleStringProperty("");
        this.FXML_MAIN_CONTROLLER = fxmlMainController;

        Integer mazeSizeX = null;
        Integer mazeSizeY = null;
        ArrayList<Rectangle> mazeDrawFields = new ArrayList<>();
        ArrayList<Integer> indexMazeFreeFields = new ArrayList<>();
        ArrayList<Integer> indexMazeTargetFields = new ArrayList<>();

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(Controller_MainGUI.DIRECTORY_MAZE_FILES + "\\" + filename));

            StringBuilder sb = new StringBuilder();
            String line = "";

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }
            br.close();

            String[] mazeStringParts = sb.toString().split(System.lineSeparator());

            // Setze Pixelgröße einzelner Labyrinthbausteine
            int mazePixelX = (int) Math.floor((double) mazePaneX / (double) mazeStringParts.length);
            int mazePixelY = (int) Math.floor((double) mazePaneY / (double) mazeStringParts[0].length());

            mazeSizeX = mazeStringParts.length;
            mazeSizeY = mazeStringParts[0].length();

            for (int y = 0; y < mazeSizeY; y++) {
                for (int x = 0; x < mazeSizeX; x++) {
                    Rectangle mazeDrawField = new Rectangle(x * mazePixelX, y * mazePixelY, mazePixelX, mazePixelY);
                    if (mazeStringParts[y].charAt(x) == SimulationMaze.mazeWallSymbol) {
                        mazeDrawField.setFill(SimulationMaze.mazeWallColor);
                    } else if (mazeStringParts[y].charAt(x) == SimulationMaze.mazeCornerSymbol) {
                        mazeDrawField.setFill(SimulationMaze.mazeCornerColor);
                    } else if (mazeStringParts[y].charAt(x) == SimulationMaze.mazeVoidSymbol) {
                        mazeDrawField.setFill(SimulationMaze.mazeVoidColor);
                        indexMazeFreeFields.add(mazeDrawFields.size());
                    } else if (mazeStringParts[y].charAt(x) == SimulationMaze.mazeTargetSymbol) {
                        mazeDrawField.setFill(SimulationMaze.mazeTargetColor);
                        indexMazeFreeFields.add(mazeDrawFields.size());
                        indexMazeTargetFields.add(mazeDrawFields.size());
                    } else {
                        mazeDrawField.setFill(SimulationMaze.mazeErrorColor);
                    }

                    mazeDrawFields.add(mazeDrawField);
                }
            }

        } catch (IOException e) {
            System.err.println("Labyrinthdatei Fehler:");
            e.printStackTrace();
        }

        this.mazeSizeX = mazeSizeX;
        this.mazeSizeY = mazeSizeY;
        this.indexMazeFreeFields = indexMazeFreeFields;
        this.indexMazeTargetFields = indexMazeTargetFields;
        this.mazeDrawFields = mazeDrawFields;
    }

    public static ArrayList<SimulationMaze> addMazefileTableData(String filename, int mazePaneX, int mazePaneY, Controller_MainGUI fxmlMainController) {
        mazeFiles.add(new SimulationMaze(filename, mazePaneX, mazePaneY, fxmlMainController));
        return mazeFiles;
    }

    public static boolean changeSelectedMaze(int indexNewSelectedMaze) {
        if (indexNewSelectedMaze <= mazeFiles.size() - 1) {
            if (selectedMazeIndexNumber == null) {
                selectedMazeIndexNumber = indexNewSelectedMaze;
                mazeFiles.get(selectedMazeIndexNumber).setSelectedMaze(SELECTED_TEXT);
                return true;
            } else {
                if (selectedMazeIndexNumber != indexNewSelectedMaze) {
                    mazeFiles.get(selectedMazeIndexNumber).setSelectedMaze(NOT_SELECTED_TEXT);
                    selectedMazeIndexNumber = indexNewSelectedMaze;
                    mazeFiles.get(selectedMazeIndexNumber).setSelectedMaze(SELECTED_TEXT);
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }

    public static SimulationMaze getSelectedMaze() {
        return mazeFiles.get(selectedMazeIndexNumber);
    }

    public static Integer getSelectedMazeIndexNumber() {
        return selectedMazeIndexNumber;
    }

    public static ArrayList<SimulationMaze> getMazeFiles() {
        return mazeFiles;
    }

    @Override
    public String toString() {
        return "Nr. <" + nr + "> Dateiname: <" + FILE_NAME.getValue() + "> Ausgewählt Text: <" + selectedMazeText.getValue() + ">";
    }

    public Controller_MainGUI getFXML_MAIN_CONTROLLER() {
        return this.FXML_MAIN_CONTROLLER;
    }

    public int getAndSetUniqueIndexNumberOfMazeRobot() {
        return this.uniqueIndexNumberOfMazeRobot++;
    }

    public ArrayList<Integer> getIndexMazeTargetFields() {
        return this.indexMazeTargetFields;
    }

    public ArrayList<Integer> getIndexMazeFreeFields() {
        return this.indexMazeFreeFields;
    }

    public ArrayList<Rectangle> getMazeDrawFields() {
        return this.mazeDrawFields;
    }

    private void setSelectedMaze(SimpleStringProperty selectionText) {
        this.selectedMazeText = selectionText;
    }

    public void addRobotToMaze(SimulationRobot newMazeRobot) {
        this.mazeRobots.add(newMazeRobot);
    }

    public ArrayList<SimulationRobot> getMazeRobots() {
        return mazeRobots;
    }

    public SimulationRobot getSelectedRobot() {
        if (this.mazeRobots.size() > 0) {
            return this.mazeRobots.get(selectedRobotNumber);
        }

        return null;
    }

    public boolean changeSelectedRobot(int indexNewSelectedRobot) {
        if (this.mazeRobots.size() >= 1 && indexNewSelectedRobot <= this.mazeRobots.size()) {
            if (!(this.selectedRobotNumber == null)) {
                if (indexNewSelectedRobot == this.selectedRobotNumber) {
                    return false;
                } else {
                    this.getSelectedRobot().setDeselectedText();
                }
            }
            this.selectedRobotNumber = indexNewSelectedRobot;
            this.getSelectedRobot().setSelectedText();
            return true;
        }

        return false;
    }

    public Integer getMazeSizeX() {
        return this.mazeSizeX;
    }

    public Integer getMazeSizeY() {
        return this.mazeSizeY;
    }

    // Maze Table
    public Integer getNr() {
        return nr;
    }

    public String getFILE_NAME() {
        return FILE_NAME.getValue();
    }

    public String getSelectedMazeText() {
        return selectedMazeText.getValue();
    }

    /*
     * Bewegungen prüfen
     * */
    public boolean rotationForwardLeftFree(SimulationRobot robot) {
        // Prüfe, ob Zielposition frei ist
        switch (robot.getHeadDirection()) {
            case 0:
                for (int y = 0, i = robot.getSizeX() * robot.getSizeY() - robot.getSizeX() - y; y / robot.getSizeX() < robot.getSizeX(); y += robot.getSizeX(), i = robot.getSizeX() * robot.getSizeY() - robot.getSizeX() - y) {
                    for (int x = 1; x < robot.getSizeY() + 1; x++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[i] - x))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowLeftRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[i] - x).setFill(SimulationMaze.debugMazeRotationTargetPositionColor);
                            }
                        }
                    }
                }
                break;
            case 1:
                for (int x = 0, i = x; x < robot.getSizeY(); x++, i = x) {
                    for (int y = 1; y < robot.getSizeX() + 1; y++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[i] - y * this.getMazeSizeY()))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowLeftRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[i] - y * this.getMazeSizeY()).setFill(SimulationMaze.debugMazeRotationTargetPositionColor);
                            }
                        }
                    }
                }
                break;
            case 2:
                for (int y = 0, i = robot.getSizeX() - 1 + y; y / robot.getSizeX() < robot.getSizeX(); y += robot.getSizeX(), i = robot.getSizeX() - 1 + y) {
                    for (int x = 1; x < robot.getSizeY() + 1; x++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[i] + x))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowLeftRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[i] + x).setFill(SimulationMaze.debugMazeRotationTargetPositionColor);
                            }
                        }
                    }
                }
                break;
            case 3:
                for (int x = 0, i = robot.getSizeX() * robot.getSizeY() - 1 + x; -x < robot.getSizeY(); x--, i = robot.getSizeX() * robot.getSizeY() - 1 + x) {
                    for (int y = 1; y < robot.getSizeX() + 1; y++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[i] + y * this.getMazeSizeY()))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowLeftRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[i] + y * this.getMazeSizeY()).setFill(SimulationMaze.debugMazeRotationTargetPositionColor);
                            }
                        }
                    }
                }
                break;
        }

        // Prüfe, ob Rotationsradius frei ist
        int radiusRound = (int) Math.ceil(Math.sqrt(Math.pow(robot.getSizeX(), 2) + Math.pow(robot.getSizeY(), 2)));
        double radius = Math.sqrt(Math.pow(robot.getSizeX(), 2) + Math.pow(robot.getSizeY(), 2));
        int delta;
        switch (robot.getHeadDirection()) {
            case 0:
                delta = radiusRound - robot.getSizeY();

                //Prüfe darüberliegende Felder
                for (int y = 1; y < delta + 1; y++) {
                    for (int x = 0; x < robot.getSizeX() * 2 - 2 * y + 2; x++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[robot.getSizeX() - 1] - y * this.getMazeSizeY() - x - y + 1))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowLeftRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[robot.getSizeX() - 1] - y * this.getMazeSizeY() - x - y + 1).setFill(SimulationMaze.debugMazeRotationTopColor);
                            }
                        }
                    }
                }

                // Prüfe darüberliegende Felder
                for (int i = 0, y = 0, index = y * robot.getSizeX(); i < robot.getSizeY() - robot.getSizeX(); i++, y++, index = y * robot.getSizeX()) {
                    // Java Rechenfehler korrigieren, indem letzte Stelle abgeschnitten wird
                    String distanceString = "" + Math.sqrt(Math.pow(radius, 2) - Math.pow(robot.getSizeY() - 1 - i, 2));
                    int distance = (int) Math.ceil(Double.parseDouble(distanceString.substring(0, distanceString.length() - 1)));

                    for (int x = 1; x < distance + 1; x++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[index] - x))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowLeftRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[index] - x).setFill(SimulationMaze.debugMazeRotationSideColor);
                            }
                        }
                    }
                }
                break;
            case 1:
                delta = radiusRound - robot.getSizeX();

                // Prüfe ob Felder rechts frei sind
                for (int x = 1; x < delta + 1; x++) {
                    for (int y = 0; y < robot.getSizeY() * 2 - 2 * x + 2; y++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[robot.getSizeX() * robot.getSizeY() - 1] + x - (y + x - 1) * this.mazeSizeY))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowLeftRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[robot.getSizeX() * robot.getSizeY() - 1] + x - (y + x - 1) * this.mazeSizeY).setFill(SimulationMaze.debugMazeRotationTopColor);
                            }
                        }
                    }
                }

                // Prüfe Felder oberhalb
                for (int i = 0, x = 0, index = robot.getSizeX() - 1 - x; i < robot.getSizeX() - robot.getSizeY(); i++, x++, index = robot.getSizeX() - 1 - x) {
                    // Java Rechenfehler korrigieren, indem letzte Stelle abgeschnitten wird
                    String distanceString = "" + Math.sqrt(Math.pow(radius, 2) - Math.pow(robot.getSizeX() - 1 - i, 2));
                    int distance = (int) Math.ceil(Double.parseDouble(distanceString.substring(0, distanceString.length() - 1)));

                    for (int y = 1; y < distance + 1; y++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[index] - y * this.mazeSizeY))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowLeftRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[index] - y * this.mazeSizeY).setFill(SimulationMaze.debugMazeRotationSideColor);
                            }
                        }
                    }
                }
                break;
            case 2:
                delta = radiusRound - robot.getSizeY();

                // Prüfe Felder darunter
                for (int y = 1; y < delta + 1; y++) {
                    for (int x = 0; x < robot.getSizeX() * 2 - 2 * y + 2; x++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[robot.getSizeX() * robot.getSizeY() - robot.getSizeX()] + y * this.mazeSizeY + x + y - 1))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowLeftRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[robot.getSizeX() * robot.getSizeY() - robot.getSizeX()] + y * this.mazeSizeY + x + y - 1).setFill(SimulationMaze.debugMazeRotationTopColor);
                            }
                        }
                    }
                }

                // Prüfe Felder rechts
                for (int i = 0, y = 0, index = robot.getSizeX() * robot.getSizeY() - 1 - y; i < robot.getSizeY() - robot.getSizeX(); i++, y += robot.getSizeX(), index = robot.getSizeX() * robot.getSizeY() - 1 - y) {
                    // Java Rechenfehler korrigieren, indem letzte Stelle abgeschnitten wird
                    String distanceString = "" + Math.sqrt(Math.pow(radius, 2) - Math.pow(robot.getSizeY() - 1 - i, 2));
                    int distance = (int) Math.ceil(Double.parseDouble(distanceString.substring(0, distanceString.length() - 1)));

                    for (int x = 1; x < distance + 1; x++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[index] + x))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowLeftRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[index] + x).setFill(SimulationMaze.debugMazeRotationSideColor);
                            }
                        }
                    }
                }
                break;
            case 3:
                delta = radiusRound - robot.getSizeX();

                // Prüfe Felder links
                for (int x = 1; x < delta + 1; x++) {
                    for (int y = 0; y < robot.getSizeY() * 2 - 2 * x + 2; y++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[0] - x + (y + x - 1) * this.mazeSizeY))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowLeftRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[0] - x + (y + x - 1) * this.mazeSizeY).setFill(SimulationMaze.debugMazeRotationTopColor);
                            }
                        }
                    }
                }

                // Prüfe Felder unterhalb
                for (int i = 0, x = 0, index = robot.getSizeX() * robot.getSizeY() - robot.getSizeX() + x; i < robot.getSizeX() - robot.getSizeY(); i++, x++, index = robot.getSizeX() * robot.getSizeY() - robot.getSizeX() + x) {
                    // Java Rechenfehler korrigieren, indem letzte Stelle abgeschnitten wird
                    String distanceString = "" + Math.sqrt(Math.pow(radius, 2) - Math.pow(robot.getSizeX() - 1 - i, 2));
                    int distance = (int) Math.ceil(Double.parseDouble(distanceString.substring(0, distanceString.length() - 1)));

                    for (int y = 1; y < distance + 1; y++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[index] + y * this.mazeSizeY))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowLeftRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[index] + y * this.mazeSizeY).setFill(SimulationMaze.debugMazeRotationSideColor);
                            }
                        }
                    }
                }
                break;
        }

        return true;
    }

    public boolean rotationForwardRightFree(SimulationRobot robot) {
        // Prüfe, ob Zielposition frei ist
        switch (robot.getHeadDirection()) {
            case 0:
                for (int y = 0, i = robot.getSizeX() * robot.getSizeY() - 1 - y; y / robot.getSizeX() < robot.getSizeX(); y += robot.getSizeX(), i = robot.getSizeX() * robot.getSizeY() - 1 - y) {
                    for (int x = 1; x < robot.getSizeY() + 1; x++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[i] + x))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowRightRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[i] + x).setFill(SimulationMaze.debugMazeRotationTargetPositionColor);
                            }
                        }
                    }
                }
                break;
            case 1:
                for (int x = 0, i = robot.getSizeX() * robot.getSizeY() - robot.getSizeX() + x; x < robot.getSizeY(); x++, i = robot.getSizeX() * robot.getSizeY() - robot.getSizeX() + x) {
                    for (int y = 1; y < robot.getSizeX() + 1; y++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[i] + y * this.getMazeSizeY()))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowRightRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[i] + y * this.getMazeSizeY()).setFill(SimulationMaze.debugMazeRotationTargetPositionColor);
                            }
                        }
                    }
                }
                break;
            case 2:
                for (int y = 0, i = y * robot.getSizeX(); y < robot.getSizeX(); y++, i = y * robot.getSizeX()) {
                    for (int x = 1; x < robot.getSizeY() + 1; x++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[i] - x))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowRightRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[i] - x).setFill(SimulationMaze.debugMazeRotationTargetPositionColor);
                            }
                        }
                    }
                }
                break;
            case 3:
                for (int x = 0, i = robot.getSizeX() - 1 + x; -x < robot.getSizeY(); x--, i = robot.getSizeX() - 1 + x) {
                    for (int y = 1; y < robot.getSizeX() + 1; y++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[i] - y * this.getMazeSizeY()))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowRightRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[i] - y * this.getMazeSizeY()).setFill(SimulationMaze.debugMazeRotationTargetPositionColor);
                            }
                        }
                    }
                }
                break;
        }

        // Prüfe, ob Rotationsradius frei ist
        int radiusRound = (int) Math.ceil(Math.sqrt(Math.pow(robot.getSizeX(), 2) + Math.pow(robot.getSizeY(), 2)));
        double radius = Math.sqrt(Math.pow(robot.getSizeX(), 2) + Math.pow(robot.getSizeY(), 2));
        int delta;
        switch (robot.getHeadDirection()) {
            case 0:
                delta = radiusRound - robot.getSizeY();

                // Prüfe Felder oberhalb
                for (int y = 1; y < delta + 1; y++) {
                    for (int x = 0; x < robot.getSizeX() * 2 - 2 * y + 2; x++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[0] - y * this.mazeSizeY + x + y - 1))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowRightRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[0] - y * this.mazeSizeY + x + y - 1).setFill(SimulationMaze.debugMazeRotationTopColor);
                            }
                        }
                    }
                }

                // Prüfe Felder rechts
                for (int i = 0, y = 0, index = robot.getSizeX() - 1 + y; i < robot.getSizeY() - robot.getSizeX(); i++, y += robot.getSizeX(), index = robot.getSizeX() - 1 + y) {
                    // Java Rechenfehler korrigieren, indem letzte Stelle abgeschnitten wird
                    String distanceString = "" + Math.sqrt(Math.pow(radius, 2) - Math.pow(robot.getSizeY() - 1 - i, 2));
                    int distance = (int) Math.ceil(Double.parseDouble(distanceString.substring(0, distanceString.length() - 1)));

                    for (int x = 1; x < distance + 1; x++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[index] + x))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowRightRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[index] + x).setFill(SimulationMaze.debugMazeRotationSideColor);
                            }
                        }
                    }
                }

                break;
            case 1:
                delta = radiusRound - robot.getSizeX();

                // Prüfe Felder rechts
                for (int x = 1; x < delta + 1; x++) {
                    for (int y = 0; y < robot.getSizeY() * 2 - 2 * x + 2; y++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[robot.getSizeX() - 1] + x + (y + x - 1) * this.mazeSizeY))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowRightRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[robot.getSizeX() - 1] + x + (y + x - 1) * this.mazeSizeY).setFill(SimulationMaze.debugMazeRotationTopColor);
                            }
                        }
                    }
                }

                // Prüfe Felder unten
                for (int i = 0, x = 0, index = robot.getSizeX() * robot.getSizeY() - 1 - x; i < robot.getSizeX() - robot.getSizeY(); i++, x++, index = robot.getSizeX() * robot.getSizeY() - 1 - x) {
                    // Java Rechenfehler korrigieren, indem letzte Stelle abgeschnitten wird
                    String distanceString = "" + Math.sqrt(Math.pow(radius, 2) - Math.pow(robot.getSizeX() - 1 - i, 2));
                    int distance = (int) Math.ceil(Double.parseDouble(distanceString.substring(0, distanceString.length() - 1)));

                    for (int y = 1; y < distance + 1; y++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[index] + y * this.mazeSizeY))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowRightRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[index] + y * this.mazeSizeY).setFill(SimulationMaze.debugMazeRotationSideColor);
                            }
                        }
                    }
                }

                break;
            case 2:
                delta = radiusRound - robot.getSizeY();

                // Prüfe Felder unten
                for (int y = 1; y < delta + 1; y++) {
                    for (int x = 0; x < robot.getSizeX() * 2 - 2 * y + 2; x++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[robot.getSizeX() * robot.getSizeY() - 1] + y * this.mazeSizeY - x - y + 1))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowRightRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[robot.getSizeX() * robot.getSizeY() - 1] + y * this.mazeSizeY - x - y + 1).setFill(SimulationMaze.debugMazeRotationTopColor);
                            }
                        }
                    }
                }

                // Prüfe Felder links
                for (int i = 0, y = 0, index = robot.getSizeX() * robot.getSizeY() - robot.getSizeX() - y; i < robot.getSizeY() - robot.getSizeX(); i++, y += robot.getSizeX(), index = robot.getSizeX() * robot.getSizeY() - robot.getSizeX() - y) {
                    // Java Rechenfehler korrigieren, indem letzte Stelle abgeschnitten wird
                    String distanceString = "" + Math.sqrt(Math.pow(radius, 2) - Math.pow(robot.getSizeY() - 1 - i, 2));
                    int distance = (int) Math.ceil(Double.parseDouble(distanceString.substring(0, distanceString.length() - 1)));

                    for (int x = 1; x < distance + 1; x++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[index] - x))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowRightRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[index] - x).setFill(SimulationMaze.debugMazeRotationSideColor);
                            }
                        }
                    }
                }

                break;
            case 3:
                delta = radiusRound - robot.getSizeX();

                // Prüfe Felder links
                for (int x = 1; x < delta + 1; x++) {
                    for (int y = 0; y < robot.getSizeY() * 2 - 2 * x + 2; y++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[robot.getSizeX() * robot.getSizeY() - robot.getSizeX()] - x - (y + x - 1) * this.mazeSizeY))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowRightRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[robot.getSizeX() * robot.getSizeY() - robot.getSizeX()] - x - (y + x - 1) * this.mazeSizeY).setFill(SimulationMaze.debugMazeRotationTopColor);
                            }
                        }
                    }
                }

                // Prüfe Felder oben
                for (int i = 0, x = 0, index = x; i < robot.getSizeX() - robot.getSizeY(); i++, x++, index = x) {
                    // Java Rechenfehler korrigieren, indem letzte Stelle abgeschnitten wird
                    String distanceString = "" + Math.sqrt(Math.pow(radius, 2) - Math.pow(robot.getSizeX() - 1 - i, 2));
                    int distance = (int) Math.ceil(Double.parseDouble(distanceString.substring(0, distanceString.length() - 1)));

                    for (int y = 1; y < distance + 1; y++) {
                        if (!(this.getIndexMazeFreeFields().contains(robot.getPosition()[index] - y * this.mazeSizeY))) {
                            return false;
                        } else {
                            if (SimulationMaze.debugShowRightRotation) {
                                this.getMazeDrawFields().get(robot.getPosition()[index] - y * this.mazeSizeY).setFill(SimulationMaze.debugMazeRotationSideColor);
                            }
                        }
                    }
                }

                break;
        }

        return true;
    }

    public boolean leftFree(SimulationRobot robot) {
        int[] sortedPositions = robot.getPosition();
        Arrays.sort(sortedPositions);

        for (int y = 0, x = 0; y < robot.getSizeY(); y++, x += robot.getSizeX()) {
            if (!(this.getIndexMazeFreeFields().contains(sortedPositions[x] - 1))) {
                return false;
            } else {
                if (SimulationMaze.debugShowLeft) {
                    this.getMazeDrawFields().get(sortedPositions[x] - 1).setFill(SimulationMaze.debugMazeLeftColor);
                }
            }
        }

        return true;
    }

    public boolean rightFree(SimulationRobot robot) {
        int[] sortedPositions = robot.getPosition();
        Arrays.sort(sortedPositions);

        for (int x = robot.getSizeX() - 1, y = 0; y < robot.getSizeY(); y++, x += robot.getSizeX()) {
            if (!(this.getIndexMazeFreeFields().contains(sortedPositions[x] + 1))) {
                return false;
            } else {
                if (SimulationMaze.debugShowRight) {
                    this.getMazeDrawFields().get(sortedPositions[x] + 1).setFill(SimulationMaze.debugMazeRightColor);
                }
            }
        }

        return true;
    }

    public boolean belowFree(SimulationRobot robot) {
        int[] sortedPositions = robot.getPosition();
        Arrays.sort(sortedPositions);

        for (int x = 0; x < robot.getSizeX(); x++) {
            if (!(this.getIndexMazeFreeFields().contains(sortedPositions[sortedPositions.length - 1 - x] + this.getMazeSizeY()))) {
                return false;
            } else {
                if (SimulationMaze.debugShowBelow) {
                    this.getMazeDrawFields().get(sortedPositions[sortedPositions.length - 1 - x] + this.mazeSizeY).setFill(SimulationMaze.debugMazeBackwardsColor);
                }
            }
        }

        return true;
    }

    public boolean aboveFree(SimulationRobot robot) {
        int[] sortedPositions = robot.getPosition();
        Arrays.sort(sortedPositions);

        for (int x = 0; x < robot.getSizeX(); x++) {
            if (!(this.getIndexMazeFreeFields().contains(sortedPositions[x] - this.mazeSizeY))) {
                return false;
            } else {
                if (SimulationMaze.debugShowFront) {
                    this.getMazeDrawFields().get(sortedPositions[x] - this.mazeSizeY).setFill(SimulationMaze.debugMazeFrontColor);
                }
            }
        }

        return true;
    }

    public Integer measureMinDistanceAbove(SimulationRobot robot) {
        Integer minDistance = null;
        boolean wall = false;

        switch (robot.getHeadDirection()) {
            case 0:
                for (int i = 0, value = 0; i < robot.getHeadPositionArray().length; i++, value = 0) {
                    for (int y = 1; !wall && y < SimulationRobot.MAXIMAL_MEASURE_DISTANCE; y++) {
                        if (this.getIndexMazeFreeFields().contains(robot.getHeadPositionArray()[i] - y * this.mazeSizeY)) {
                            value++;
                            robot.addDistanceDataFieldsFront(robot.getHeadPositionArray()[i] - y * this.mazeSizeY);

                            if (SimulationMaze.debugShowDistanceAbove) {
                                this.getMazeDrawFields().get(robot.getHeadPositionArray()[i] - y * this.mazeSizeY).setFill(SimulationMaze.debugMazeDistanceAboveColor);
                            }
                        } else {
                            wall = true;

                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }

                        if (y + 1 == SimulationRobot.MAXIMAL_MEASURE_DISTANCE) {
                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }
                    }
                    wall = false;
                }
                break;
            case 1:
                for (int i = 0, value = 0, index = robot.getSizeX() - 1 - i; i < robot.getHeadPositionArray().length; i++, value = 0, index = robot.getSizeX() - 1 - i) {
                    for (int y = 1; !wall && y < SimulationRobot.MAXIMAL_MEASURE_DISTANCE; y++) {
                        if (this.getIndexMazeFreeFields().contains(robot.getPosition()[index] - y * this.mazeSizeY)) {
                            value++;
                            robot.addDistanceDataFieldsLeft(robot.getPosition()[index] - y * this.mazeSizeY);

                            if (SimulationMaze.debugShowDistanceAbove) {
                                this.getMazeDrawFields().get(robot.getPosition()[index] - y * this.mazeSizeY).setFill(SimulationMaze.debugMazeDistanceAboveColor);
                            }
                        } else {
                            wall = true;

                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }

                        if (y + 1 == SimulationRobot.MAXIMAL_MEASURE_DISTANCE) {
                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }
                    }
                    wall = false;
                }
                break;
            case 3:
                for (int i = 0, value = 0, index = i; i < robot.getHeadPositionArray().length; i++, value = 0, index = i) {
                    for (int y = 1; !wall && y < SimulationRobot.MAXIMAL_MEASURE_DISTANCE; y++) {
                        if (this.getIndexMazeFreeFields().contains(robot.getPosition()[index] - y * this.mazeSizeY)) {
                            value++;
                            robot.addDistanceDataFieldsRight(robot.getPosition()[index] - y * this.mazeSizeY);

                            if (SimulationMaze.debugShowDistanceAbove) {
                                this.getMazeDrawFields().get(robot.getPosition()[index] - y * this.mazeSizeY).setFill(SimulationMaze.debugMazeDistanceAboveColor);
                            }
                        } else {
                            wall = true;

                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }

                        if (y + 1 == SimulationRobot.MAXIMAL_MEASURE_DISTANCE) {
                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }
                    }
                    wall = false;
                }
                break;
            default:
                throw new IllegalStateException("Falsche Headdirection hat Messung angefordert HD = " + robot.getHeadDirection());
        }

        return minDistance;
    }

    public Integer measureMinDistanceBelow(SimulationRobot robot) {
        Integer minDistance = null;
        boolean wall = false;

        switch (robot.getHeadDirection()) {
            case 1:
                for (int i = 0, value = 0, x = 0, index = robot.getSizeX() * robot.getSizeY() - 1 - x; i < robot.getHeadPositionArray().length; i++, value = 0, x++, index = robot.getSizeX() * robot.getSizeY() - 1 - x) {
                    for (int y = 1; !wall && y < SimulationRobot.MAXIMAL_MEASURE_DISTANCE; y++) {
                        if (this.getIndexMazeFreeFields().contains(robot.getPosition()[index] + y * this.mazeSizeY)) {
                            value++;
                            robot.addDistanceDataFieldsRight(robot.getPosition()[index] + y * this.mazeSizeY);

                            if (SimulationMaze.debugShowDistanceBelow) {
                                this.getMazeDrawFields().get(robot.getPosition()[index] + y * this.mazeSizeY).setFill(SimulationMaze.debugMazeDistanceBelowColor);
                            }
                        } else {
                            wall = true;

                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }

                        if (y + 1 == SimulationRobot.MAXIMAL_MEASURE_DISTANCE) {
                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }
                    }
                    wall = false;
                }
                break;
            case 2:
                for (int i = 0, value = 0; i < robot.getHeadPositionArray().length; i++, value = 0) {
                    for (int y = 1; !wall && y < SimulationRobot.MAXIMAL_MEASURE_DISTANCE; y++) {
                        if (this.getIndexMazeFreeFields().contains(robot.getHeadPositionArray()[i] + y * this.mazeSizeY)) {
                            value++;
                            robot.addDistanceDataFieldsFront(robot.getHeadPositionArray()[i] + y * this.mazeSizeY);

                            if (SimulationMaze.debugShowDistanceBelow) {
                                this.getMazeDrawFields().get(robot.getHeadPositionArray()[i] + y * this.mazeSizeY).setFill(SimulationMaze.debugMazeDistanceBelowColor);
                            }
                        } else {
                            wall = true;

                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }

                        if (y + 1 == SimulationRobot.MAXIMAL_MEASURE_DISTANCE) {
                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }
                    }
                    wall = false;
                }
                break;
            case 3:
                for (int i = 0, value = 0, index = robot.getSizeX() * robot.getSizeY() - robot.getSizeX() + i; i < robot.getHeadPositionArray().length; i++, value = 0, index = robot.getSizeX() * robot.getSizeY() - robot.getSizeX() + i) {
                    for (int y = 1; !wall && y < SimulationRobot.MAXIMAL_MEASURE_DISTANCE; y++) {
                        if (this.getIndexMazeFreeFields().contains(robot.getPosition()[index] + y * this.mazeSizeY)) {
                            value++;
                            robot.addDistanceDataFieldsLeft(robot.getPosition()[index] + y * this.mazeSizeY);

                            if (SimulationMaze.debugShowDistanceBelow) {
                                this.getMazeDrawFields().get(robot.getPosition()[index] + y * this.mazeSizeY).setFill(SimulationMaze.debugMazeDistanceBelowColor);
                            }
                        } else {
                            wall = true;

                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }

                        if (y + 1 == SimulationRobot.MAXIMAL_MEASURE_DISTANCE) {
                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }
                    }
                    wall = false;
                }
                break;
            default:
                throw new IllegalStateException("Falsche Headdirection hat Messung angefordert HD = " + robot.getHeadDirection());

        }

        return minDistance;
    }

    public Integer measureMinDistanceRight(SimulationRobot robot) {
        Integer minDistance = null;
        boolean wall = false;

        switch (robot.getHeadDirection()) {
            case 0:
                for (int i = 0, value = 0, y = 0, index = robot.getSizeX() - 1 + y; i < robot.getHeadPositionArray().length; i++, value = 0, y += robot.getSizeX(), index = robot.getSizeX() - 1 + y) {
                    for (int x = 1; !wall && x < SimulationRobot.MAXIMAL_MEASURE_DISTANCE; x++) {
                        if (this.getIndexMazeFreeFields().contains(robot.getPosition()[index] + x)) {
                            value++;
                            robot.addDistanceDataFieldsRight(robot.getPosition()[index] + x);

                            if (SimulationMaze.debugShowDistanceRight) {
                                this.getMazeDrawFields().get(robot.getPosition()[index] + x).setFill(SimulationMaze.debugMazeDistanceRightColor);
                            }
                        } else {
                            wall = true;

                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }

                        if (x + 1 == SimulationRobot.MAXIMAL_MEASURE_DISTANCE) {
                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }
                    }
                    wall = false;
                }
                break;
            case 1:
                for (int i = 0, value = 0; i < robot.getHeadPositionArray().length; i++, value = 0) {
                    for (int x = 1; !wall && x < SimulationRobot.MAXIMAL_MEASURE_DISTANCE; x++) {
                        if (this.getIndexMazeFreeFields().contains(robot.getHeadPositionArray()[i] + x)) {
                            value++;
                            robot.addDistanceDataFieldsFront(robot.getHeadPositionArray()[i] + x);

                            if (SimulationMaze.debugShowDistanceRight) {
                                this.getMazeDrawFields().get(robot.getHeadPositionArray()[i] + x).setFill(SimulationMaze.debugMazeDistanceRightColor);
                            }
                        } else {
                            wall = true;

                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }

                        if (x + 1 == SimulationRobot.MAXIMAL_MEASURE_DISTANCE) {
                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }
                    }
                    wall = false;
                }
                break;
            case 2:
                for (int i = 0, value = 0, y = 0, index = robot.getSizeX() * robot.getSizeY() - 1 - y; i < robot.getHeadPositionArray().length; i++, value = 0, y += robot.getSizeX(), index = robot.getSizeX() * robot.getSizeY() - 1 - y) {
                    for (int x = 1; !wall && x < SimulationRobot.MAXIMAL_MEASURE_DISTANCE; x++) {
                        if (this.getIndexMazeFreeFields().contains(robot.getPosition()[index] + x)) {
                            value++;
                            robot.addDistanceDataFieldsLeft(robot.getPosition()[index] + x);

                            if (SimulationMaze.debugShowDistanceRight) {
                                this.getMazeDrawFields().get(robot.getPosition()[index] + x).setFill(SimulationMaze.debugMazeDistanceRightColor);
                            }
                        } else {
                            wall = true;

                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }

                        if (x + 1 == SimulationRobot.MAXIMAL_MEASURE_DISTANCE) {
                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }
                    }
                    wall = false;
                }
                break;
            default:
                throw new IllegalStateException("Falsche Headdirection hat Messung angefordert HD = " + robot.getHeadDirection());
        }

        return minDistance;
    }

    public Integer measureMinDistanceLeft(SimulationRobot robot) {
        Integer minDistance = null;
        boolean wall = false;

        switch (robot.getHeadDirection()) {
            case 0:
                for (int i = 0, value = 0, y = 0, index = y * robot.getSizeX(); i < robot.getHeadPositionArray().length; i++, value = 0, y++, index = y * robot.getSizeX()) {
                    for (int x = 1; !wall && x < SimulationRobot.MAXIMAL_MEASURE_DISTANCE; x++) {
                        if (this.getIndexMazeFreeFields().contains(robot.getPosition()[index] - x)) {
                            value++;
                            robot.addDistanceDataFieldsLeft(robot.getPosition()[index] - x);

                            if (SimulationMaze.debugShowDistanceLeft) {
                                this.getMazeDrawFields().get(robot.getPosition()[index] - x).setFill(SimulationMaze.debugMazeDistanceLeftColor);
                            }
                        } else {
                            wall = true;

                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }

                        if (x + 1 == SimulationRobot.MAXIMAL_MEASURE_DISTANCE) {
                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }
                    }
                    wall = false;
                }
                break;
            case 2:
                for (int i = 0, value = 0, y = 0, index = robot.getSizeX() * robot.getSizeY() - robot.getSizeX() - y; i < robot.getHeadPositionArray().length; i++, value = 0, y += robot.getSizeX(), index = robot.getSizeX() * robot.getSizeY() - robot.getSizeX() - y) {
                    for (int x = 1; !wall && x < SimulationRobot.MAXIMAL_MEASURE_DISTANCE; x++) {
                        if (this.getIndexMazeFreeFields().contains(robot.getPosition()[index] - x)) {
                            value++;
                            robot.addDistanceDataFieldsRight(robot.getPosition()[index] - x);

                            if (SimulationMaze.debugShowDistanceLeft) {
                                this.getMazeDrawFields().get(robot.getPosition()[index] - x).setFill(SimulationMaze.debugMazeDistanceLeftColor);
                            }
                        } else {
                            wall = true;

                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }

                        if (x + 1 == SimulationRobot.MAXIMAL_MEASURE_DISTANCE) {
                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }
                    }
                    wall = false;
                }
                break;
            case 3:
                for (int i = 0, value = 0; i < robot.getHeadPositionArray().length; i++, value = 0) {
                    for (int x = 1; !wall && x < SimulationRobot.MAXIMAL_MEASURE_DISTANCE; x++) {
                        if (this.getIndexMazeFreeFields().contains(robot.getHeadPositionArray()[i] - x)) {
                            value++;
                            robot.addDistanceDataFieldsFront(robot.getHeadPositionArray()[i] - x);

                            if (SimulationMaze.debugShowDistanceLeft) {
                                this.getMazeDrawFields().get(robot.getHeadPositionArray()[i] - x).setFill(SimulationMaze.debugMazeDistanceLeftColor);
                            }
                        } else {
                            wall = true;

                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }

                        if (x + 1 == SimulationRobot.MAXIMAL_MEASURE_DISTANCE) {
                            if (minDistance == null) {
                                minDistance = value;
                            } else {
                                if (minDistance > value) {
                                    minDistance = value;
                                }
                            }
                        }
                    }
                    wall = false;
                }
                break;
            default:
                throw new IllegalStateException("Falsche Headdirection hat Messung angefordert HD = " + robot.getHeadDirection());
        }

        return minDistance;
    }

    /*
     * Debug Funktionen
     * */
    public void getMazeFreeFieldsToString() {
        String mazeFreeFieldsString = "";

        String newField;
        String fieldWidth = "" + (mazeSizeX * mazeSizeY) + " ";
        for (int i = 0, y = 0; i < mazeSizeY * mazeSizeX; i++) {
            newField = "";
            if (y < this.indexMazeFreeFields.size() && this.indexMazeFreeFields.get(y) == i) {
                y++;
                newField += i;
            } else {
                newField += SimulationMaze.mazeWallSymbol;
            }

            if (newField.length() < fieldWidth.length()) {
                for (int c = 0; c < fieldWidth.length() && newField.length() < fieldWidth.length(); c++) {
                    newField += " ";
                }
            }

            if ((i + 1) % mazeSizeX == 0) {
                newField += "\n";
            }

            mazeFreeFieldsString += newField;
        }

        System.out.println(mazeFreeFieldsString);
    }

    public void getMazeDrawFieldsToString() {
        String mazeFreeFieldsString = "";

        String newField;
        String fieldWidth = "" + (mazeSizeX * mazeSizeY) + " ";
        for (int y = 0, i = 0; y < mazeDrawFields.size(); y++) {
            newField = "";
            if (mazeDrawFields.get(y).getFill() == SimulationMaze.mazeVoidColor) {
                i++;
                newField += i;
            } else if (mazeDrawFields.get(y).getFill() == SimulationMaze.mazeTargetColor) {
                i++;
                newField += i;
            } else if (mazeDrawFields.get(y).getFill() == SimulationMaze.mazeCornerColor) {
                newField += SimulationMaze.mazeCornerSymbol;
            } else {
                newField += SimulationMaze.mazeWallSymbol;
            }

            if (newField.length() < fieldWidth.length()) {
                for (int c = 0; c < fieldWidth.length() && newField.length() < fieldWidth.length(); c++) {
                    newField += " ";
                }
            }

            if ((y + 1) % mazeSizeX == 0) {
                newField += "\n";
            }

            mazeFreeFieldsString += newField;
        }

        System.out.println(mazeFreeFieldsString);
    }

}
