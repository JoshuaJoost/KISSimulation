package gui_simulation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

// TODO
// Dokumentation: Overleaf -> Beide den selben Account (auch gleichzeitig zugreifen geht)
// Nach wissenschaftlichen Arbeiten bezüglich Roboter und Labyrinth suchen
// Wie wird Simulation und Realität aufeinander abgestimmt?
// An Merkzettel für schriftliche Arbeiten orientieren
// KIS Prüfung: 20-25min Projekt vorstellen, anschließend Fragen

public class Controller_MainGUI implements Initializable {
    // Initialisierungprobleme
    private static final int mazePaneX = 570;
    private static final int mazePaneY = 570;

    private final Color mazeWallColor = Color.rgb(0, 0, 0);
    private final Color mazeVoidColor = Color.rgb(255, 255, 255);
    private final Color mazeGroundTarget = Color.rgb(255, 0, 0);
    private final Color mazeErrorColor = Color.rgb(0, 255, 246);

    // TODO in Maze bzw. Robot verlagern?
    public static final File DIRECTORY_MAZE_FILES = new File((System.getProperty("user.dir") + "\\src\\gui_simulation\\mazeFiles"));
    private final String MAZE_LABEL_PREFIX = "Labyrinth: ";
    private final String ROBOT_LABEL_PREFIX = "Roboter: ";
    private final String ROBOT_LABEL_NO_ROBOT_SELECTED = "<keiner ausgewählt>";

    private static ObservableList<Rectangle> mazeFields = FXCollections.observableArrayList();
    private ObservableList<SimulationMaze> mazefileTableData = FXCollections.observableArrayList();
    private ObservableList<SimulationRobot> robotTableData = FXCollections.observableArrayList();

    private int mazePixelX = 0;
    private int mazePixelY = 0;

    @FXML
    private Label mazeLable;

    @FXML
    private Pane mazePane;

    @FXML
    private Label robotSelectedLable;

    @FXML
    private Pane robotPane;

    @FXML
    private TableView<SimulationRobot> robotTable;

    @FXML
    private TableColumn<SimulationRobot, Integer> robotTableNr;

    @FXML
    private TableColumn<SimulationRobot, String> robotTableName;

    @FXML
    private TableColumn<SimulationRobot, String> robotTableSelected;

    @FXML
    private Button robotStartStop;

    @FXML
    private Button robotGetMatrix;

    @FXML
    private Button robotDelete;

    @FXML
    private Button addNewRobot;

    @FXML
    private Pane historyPane;

    @FXML
    private TableView<?> historyTable;

    @FXML
    private TableColumn<?, ?> historyTableNr;

    @FXML
    private TableColumn<?, ?> historyTableAction;

    @FXML
    private TableColumn<?, ?> historyTableFeedback;

    @FXML
    private TableView<SimulationMaze> mazefileTable;

    @FXML
    private TableColumn<SimulationMaze, Integer> mazefileTableNr;

    @FXML
    private TableColumn<SimulationMaze, String> mazefileTableFilename;

    @FXML
    private TableColumn<SimulationMaze, String> mazefileTableSelected;

    @FXML
    private Label mazeSelectedLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisiere MazeTable
        mazefileTableNr.setCellValueFactory(new PropertyValueFactory<>("nr"));
        mazefileTableFilename.setCellValueFactory(new PropertyValueFactory<>("FILE_NAME"));
        mazefileTableSelected.setCellValueFactory(new PropertyValueFactory<>("selectedMazeText"));
        mazefileTable.setItems(mazefileTableData);

        // Initialisiere RobotTable
        robotTableNr.setCellValueFactory(new PropertyValueFactory<>("robotNumber"));
        robotTableName.setCellValueFactory(new PropertyValueFactory<>("robotName"));
        robotTableSelected.setCellValueFactory(new PropertyValueFactory<>("selectedText"));
        robotTable.setItems(robotTableData);

        getMazeFiles(DIRECTORY_MAZE_FILES);
    }

    private void updateRobotTableDataAndLabel() {
        robotTableData.clear();
        robotTableData.addAll(SimulationMaze.getSelectedMaze().getMazeRobots());
        robotTable.sort();

        if (SimulationMaze.getSelectedMaze().getMazeRobots().size() > 0) {
            robotSelectedLable.setText(ROBOT_LABEL_PREFIX + SimulationMaze.getSelectedMaze().getSelectedRobot().getRobotName());
        } else {
            robotSelectedLable.setText(ROBOT_LABEL_PREFIX + ROBOT_LABEL_NO_ROBOT_SELECTED);
        }
    }

    private void updateMazeTableDataAndLabel() {
        mazefileTableData.clear();
        mazefileTableData.addAll(SimulationMaze.getMazeFiles());
        mazefileTable.sort();

        mazeSelectedLabel.setText(MAZE_LABEL_PREFIX + SimulationMaze.getSelectedMaze().getFILE_NAME());
        mazeLable.setText(MAZE_LABEL_PREFIX + SimulationMaze.getSelectedMaze().getFILE_NAME() + " Größe: " + SimulationMaze.getSelectedMaze().getMazeSizeX() + "x" + SimulationMaze.getSelectedMaze().getMazeSizeY());
    }

    private ArrayList<Integer> getIndexPositionOfFreeMazeFields() {
        ArrayList<Integer> indexPositon = new ArrayList<>();

        for (int i = 0; i < SimulationMaze.getSelectedMaze().getMazeDrawFields().size(); i++) {
            if (SimulationMaze.getSelectedMaze().getMazeDrawFields().get(i).getFill() == SimulationMaze.mazeVoidColor) {
                indexPositon.add(i);
            }
        }

        return indexPositon;
    }

    @FXML
    void addNewRobot(ActionEvent event) {
        if (SimulationMaze.getSelectedMazeIndexNumber() != null) {
            // Definiere Roboterwerte
            int robotSizeX = 7;//13 - 3;
            int robotSizeY = 8;//18 - 4;

            // Roboter versuchen auf das Labyrinth zu setzen
            ArrayList<Integer> robotPositions = new ArrayList<>();
            ArrayList<Integer> freeMazeFields = getIndexPositionOfFreeMazeFields();

            boolean robotSuccessfullySet = false;
            while (freeMazeFields.size() > 0 && !robotSuccessfullySet) {

                //// Versuche X-Werte des Roboters zu setzen
                int indexStartPosition = 0;
                boolean robotSuccessfullySetX = false;
                while (freeMazeFields.size() > 0 && !robotSuccessfullySetX) {
                    indexStartPosition = (int) (Math.random() * freeMazeFields.size());
                    robotPositions.add(freeMazeFields.get(indexStartPosition));

                    boolean lookRight = true;
                    boolean lookLeft = true;
                    for (int x = 1; x < robotSizeX && robotPositions.size() < robotSizeX && (lookRight || lookLeft); x++) {
                        if (lookRight && freeMazeFields.contains(robotPositions.get(0) + x)) {
                            robotPositions.add(robotPositions.get(0) + x);
                        } else {
                            lookRight = false;
                        }

                        if (robotPositions.size() < robotSizeX) {
                            if (lookLeft && freeMazeFields.contains(robotPositions.get(0) - x)) {
                                robotPositions.add(robotPositions.get(0) - x);
                            } else {
                                lookLeft = false;
                            }
                        }
                    }
                    System.out.println();
                    if (freeMazeFields.size() - 1 == 0) {
                        if (robotPositions.size() < robotSizeX && robotPositions.size() > 0) {
                            throw new IllegalStateException("Roboter konnte nicht gesetzt werden, keinen freien Platz der Größe " + robotSizeX + " gefunden");
                        } else if (robotPositions.size() == 0) {
                            throw new IllegalStateException("Roboter konnte nicht gesetzt werden, kein einziges freies Feld gefunden");
                        }
                    }

                    // Prüfe, ob Roboter gesetzt werden konnte
                    if (robotPositions.size() == robotSizeX) {
                        robotSuccessfullySetX = true;
                    } else {
                        robotPositions.clear();
                        freeMazeFields.remove(indexStartPosition);
                    }
                }

                //// Versuche Y-Werte des Roboters zu setzen
                boolean lookAbove = true;
                boolean lookBelow = true;
                for (int y = 1; y < robotSizeY && robotPositions.size() < (robotSizeX * robotSizeY) && (lookAbove || lookBelow); y++) {
                    for (int x = 0; x < robotSizeX; x++) {
                        if (lookAbove && !(freeMazeFields.contains(robotPositions.get(x) - y * SimulationMaze.getSelectedMaze().getMazeSizeY()))) {
                            lookAbove = false;
                        }
                        if (lookBelow && !(freeMazeFields.contains(robotPositions.get(x) + y * SimulationMaze.getSelectedMaze().getMazeSizeY()))) {
                            lookBelow = false;
                        }
                    }
                    if (lookAbove && robotPositions.size() < robotSizeX * robotSizeY) {
                        for (int x = 0; x < robotSizeX; x++) {
                            robotPositions.add(robotPositions.get(x) - y * SimulationMaze.getSelectedMaze().getMazeSizeY());
                        }
                    }
                    if (lookBelow && robotPositions.size() < robotSizeX * robotSizeY) {
                        for (int x = 0; x < robotSizeX; x++) {
                            robotPositions.add(robotPositions.get(x) + y * SimulationMaze.getSelectedMaze().getMazeSizeY());
                        }
                    }
                }

                // Prüfe, ob Roboter vollständig gesetzt werden konnte
                if (robotPositions.size() == robotSizeX * robotSizeY) {
                    robotSuccessfullySet = true;
                } else {
                    robotPositions.clear();
                    freeMazeFields.remove(indexStartPosition);
                }
            }

            if (robotSuccessfullySet) {
                // Erstelle Roboter und füge ihm aktuell selektiertem Labyrinth zu, zeige ihn anschließend an
                SimulationRobot newRobot = SimulationRobot.addRobot(robotSizeX, robotSizeY, OwnUtils.convertArrayListToIntArray(robotPositions));
                SimulationMaze.getSelectedMaze().addRobotToMaze(newRobot);
                SimulationMaze.getSelectedMaze().changeSelectedRobot(SimulationMaze.getSelectedMaze().getMazeRobots().size() - 1);
                updateMaze(true);
                updateRobotTableDataAndLabel();
            } else {
                throw new IllegalStateException("Roboter konnte nicht gesetzt werden, keinen freien Platz der Größe " + robotSizeX + "x" + robotSizeY + " gefunden.");
            }
        }
    }

    @FXML
    void mazeMoveRobotKeyboard(KeyEvent event) {
        // System.out.println(":" + event.getCode());

        if (SimulationMaze.getMazeFiles().size() > 0 && SimulationMaze.getSelectedMazeIndexNumber() != null && SimulationMaze.getSelectedMaze().getMazeRobots().size() > 0) {
            switch (event.getCode().toString()) {
                case "RIGHT":
                case "D":
                    SimulationMaze.getSelectedMaze().getSelectedRobot().keyboardMoveRight();
                    break;
                case "LEFT":
                case "A":
                    SimulationMaze.getSelectedMaze().getSelectedRobot().keyboardMoveLeft();
                    break;
                case "DOWN":
                case "S":
                    SimulationMaze.getSelectedMaze().getSelectedRobot().keyboardMoveDown();
                    break;
                case "UP":
                case "W":
                    SimulationMaze.getSelectedMaze().getSelectedRobot().keyboardMoveUp();
                    break;
                case "Q":
                    SimulationMaze.getSelectedMaze().getSelectedRobot().keyboardRotateForwardLeft();
                    break;
                case "E":
                    SimulationMaze.getSelectedMaze().getSelectedRobot().keyboardRotateForwardRight();
                    break;
                case "Y":
                case "Z":
                    SimulationMaze.getSelectedMaze().getSelectedRobot().keyboardLook();
                    break;
            }
            updateMaze(true);
        }
    }

    @FXML
    void focusMazePane(MouseEvent event) {
        mazePane.requestFocus();
    }

    @FXML
    void robotDelete(ActionEvent event) {

    }

    @FXML
    void robotGetMatrix(ActionEvent event) {

    }

    @FXML
    void robotStartStop(ActionEvent event) {
        if(SimulationMaze.getSelectedMaze().getMazeRobots().size() > 0) {
            SimulationMaze.getSelectedMaze().getSelectedRobot().start();
        }
    }

    @FXML
    void selectNewMaze(MouseEvent event) {
        if (mazefileTable.getSelectionModel().getSelectedItem() != null) {
            int selectedRowNumber = mazefileTable.getSelectionModel().getSelectedItem().getNr();

            boolean tableDataChanged = SimulationMaze.changeSelectedMaze(selectedRowNumber - 1);

            if (tableDataChanged) {
                updateMazeTableDataAndLabel();

                if (SimulationMaze.getSelectedMaze().getMazeRobots().size() > 0) {
                    updateMaze(true);
                } else {
                    updateMaze(false);
                }

                updateRobotTableDataAndLabel();
            }
        }
    }

    @FXML
    void selectNewRobot(MouseEvent event) {
        if (robotTable.getSelectionModel().getSelectedItem() != null) {
            int selectedRowNumber = robotTable.getSelectionModel().getSelectedItem().getUniqueIndexNumberOfMazeRobot();
            boolean tableDataChanged = SimulationMaze.getSelectedMaze().changeSelectedRobot(selectedRowNumber);

            if (tableDataChanged) {
                updateRobotTableDataAndLabel();
                updateMaze(true);
            }
        }
    }

    public void updateMaze(boolean drawRobot) {
        drawMaze();

        if (drawRobot) {
            ArrayList<Integer> robotHeadPosition = SimulationMaze.getSelectedMaze().getSelectedRobot().getHeadPosition();

            // Setze Roboter und seinen Kopf
            for (int robotPosition : SimulationMaze.getSelectedMaze().getSelectedRobot().getPosition()) {
                Rectangle valueRect = SimulationMaze.getSelectedMaze().getMazeDrawFields().get(robotPosition);

                Rectangle robotField = new Rectangle(valueRect.getX(), valueRect.getY(), valueRect.getWidth(), valueRect.getHeight());
                if(!(robotHeadPosition.contains(robotPosition))) {
                    robotField.setFill(SimulationMaze.getSelectedMaze().getSelectedRobot().getRobotBodyColor());
                } else {
                    robotField.setFill(SimulationMaze.getSelectedMaze().getSelectedRobot().getRobotHeadColor());
                }
                mazePane.getChildren().set(robotPosition, robotField);
            }

            // Setze gemessene Felder
            if(SimulationMaze.getSelectedMaze().getSelectedRobot().getDistanceDataFieldsLeft().size() > 0){
                for(int measurePos : SimulationMaze.getSelectedMaze().getSelectedRobot().getDistanceDataFieldsLeft()){
                    Rectangle valueRect = SimulationMaze.getSelectedMaze().getMazeDrawFields().get(measurePos);

                    Rectangle measureField = new Rectangle(valueRect.getX(), valueRect.getY(), valueRect.getWidth(), valueRect.getHeight());
                    measureField.setFill(SimulationMaze.getSelectedMaze().getSelectedRobot().getMeasureDistanceColor());
                    mazePane.getChildren().set(measurePos, measureField);
                }
            }

            if(SimulationMaze.getSelectedMaze().getSelectedRobot().getDistanceDataFieldsFront().size() > 0){
                for(int measurePos : SimulationMaze.getSelectedMaze().getSelectedRobot().getDistanceDataFieldsFront()){
                    Rectangle valueRect = SimulationMaze.getSelectedMaze().getMazeDrawFields().get(measurePos);

                    Rectangle measureField = new Rectangle(valueRect.getX(), valueRect.getY(), valueRect.getWidth(), valueRect.getHeight());
                    measureField.setFill(SimulationMaze.getSelectedMaze().getSelectedRobot().getMeasureDistanceColor());
                    mazePane.getChildren().set(measurePos, measureField);
                }
            }

            if(SimulationMaze.getSelectedMaze().getSelectedRobot().getDistanceDataFieldsRight().size() > 0){
                for(int measurePos : SimulationMaze.getSelectedMaze().getSelectedRobot().getDistanceDataFieldsRight()){
                    Rectangle valueRect = SimulationMaze.getSelectedMaze().getMazeDrawFields().get(measurePos);

                    Rectangle measureField = new Rectangle(valueRect.getX(), valueRect.getY(), valueRect.getWidth(), valueRect.getHeight());
                    measureField.setFill(SimulationMaze.getSelectedMaze().getSelectedRobot().getMeasureDistanceColor());
                    mazePane.getChildren().set(measurePos, measureField);
                }
            }
        }
    }

    private void drawMaze() {
        mazePane.getChildren().clear();
        mazePane.getChildren().addAll(SimulationMaze.getSelectedMaze().getMazeDrawFields());
    }

    public void getMazeFiles(File dir) {
        File files[] = dir.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile() && files[i].getName().startsWith("maze")) {
                SimulationMaze.addMazefileTableData(files[i].getName(), Controller_MainGUI.mazePaneX, Controller_MainGUI.mazePaneY, this);
            }
        }

        mazefileTableData.addAll(SimulationMaze.getMazeFiles());

        if (mazefileTableData.size() == 0) {
            throw new IllegalArgumentException("Keine gültigen Labyrinthdateien bei " + DIRECTORY_MAZE_FILES + " gefunden");
        }

    }

    /*
     * DEBUGG Funktionen
     * */
    private void printMazeFreeFields() {
        for (int i = 0; i < mazeFields.size(); i++) {
            if (mazeFields.get(i).getFill() == mazeVoidColor) {
                System.out.println(i + ": Frei");
            }
        }
    }

    private void printCompareFreeFields(ArrayList<Integer> possibleFreeFields) {
        int mazeFreeFields = 0;

        for (int i = 0; i < mazeFields.size(); i++) {
            if (mazeFields.get(i).getFill() == mazeVoidColor) {
                mazeFreeFields++;
            }
        }

        if (mazeFreeFields != possibleFreeFields.size()) {
            System.err.println("Ungleiche Anzahl an freien Feldern!");
        }
        for (int i = 0, y = 0; i < mazeFields.size(); i++) {
            if (mazeFields.get(i).getFill() == mazeVoidColor) {
                if (i == possibleFreeFields.get(y)) {
                    System.out.println("maze: " + i + " compared: " + possibleFreeFields.get(y));
                } else {
                    System.err.println("maze: " + i + " compared: " + possibleFreeFields.get(y));
                }
                y++;
            }
        }

    }

}
