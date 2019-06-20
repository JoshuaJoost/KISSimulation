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
import java.lang.reflect.Array;
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

    private final char mazeWallSymbol = '#';
    private final char mazeVoidSymbol = ' ';

    private final Color mazeWallColor = Color.rgb(0, 0, 0);
    private final Color mazeVoidColor = Color.rgb(255, 255, 255);
    private final Color mazeGroundTarget = Color.rgb(255, 0, 0);
    private final Color mazeErrorColor = Color.rgb(0, 255, 246);

    // TODO in Maze bzw. Robot verlagern?
    private static final File DIRECTORY_MAZE_FILES = new File((System.getProperty("user.dir") + "\\src\\gui_simulation\\mazeFiles"));
    private final String MAZE_LABEL_PREFIX = "Labyrinth: ";
    private final String ROBOT_LABEL_PREFIX = "Roboter: ";

    private ObservableList<Rectangle> mazeFields = FXCollections.observableArrayList();
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
    private Label mazeLableSelectedFile;

    private ArrayList<Integer> getIndexPositionOfFreeMazeFields() {
        ArrayList<Integer> indexPositon = new ArrayList<>();

        for (int i = 0; i < mazeFields.size(); i++) {
            if (mazeFields.get(i).getFill() == mazeVoidColor) {
                indexPositon.add(i);
            }
        }

        return indexPositon;
    }

    private void outMazeFreeFields() {
        for (int i = 0; i < mazeFields.size(); i++) {
            if (mazeFields.get(i).getFill() == mazeVoidColor) {
                System.out.println(i + ": Frei");
            }
        }
    }

    private void outCompareFreeFields(ArrayList<Integer> possibleFreeFields) {
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

    @FXML
    void addNewRobot(ActionEvent event) {
        // Erstelle Roboter
        if(SimulationMaze.getSelectedMazeNumber() != null) {
            SimulationRobot.addRobot(4, 3);
            SimulationRobot.changeSelectedRobot(SimulationRobot.getRobots().size() - 1);
            robotTableData.add(SimulationRobot.getRobots().get(SimulationRobot.getRobots().size() - 1));
            SimulationRobot selectedRobot = SimulationRobot.getSelectedRobot();

            // Finde Zufällige Startposition
            ArrayList<Integer> robotPositions = new ArrayList<>();
            ArrayList<Integer> freeMazeFields = getIndexPositionOfFreeMazeFields();

            // Finde geeigenete Position um Breite des Roboters setzen zu können und speichere gefundene Felder in robotPositions
            boolean robotSuccessfullySet = false;
            while (freeMazeFields.size() > 0 && !robotSuccessfullySet) {

                int startPosition = 0;
                boolean robotSuccessfullySetXPosition = false;
                while (freeMazeFields.size() > 0 && !robotSuccessfullySetXPosition) {
                    startPosition = (int) (Math.random() * freeMazeFields.size());
                    robotPositions.add(freeMazeFields.get(startPosition));

                    if (robotPositions.size() == 0) {
                        // TODO in HistorieTabelle eintragen
                        throw new IllegalStateException("Keine gültige Startposition gefunden");
                    }

                    // Finde benachbarte freie Felder X Richtung

                    boolean lookRight = true;
                    boolean lookLeft = true;
                    for (int i = 1; i < selectedRobot.getSizeX() && robotPositions.size() < selectedRobot.getSizeX(); i++) {
                        if (mazeFields.get(robotPositions.get(0) + i).getFill() == mazeVoidColor && lookRight) {
                            robotPositions.add(robotPositions.get(0) + i);
                        } else {
                            lookRight = false;
                        }

                        if (robotPositions.size() < selectedRobot.getSizeX()) {
                            if (mazeFields.get(robotPositions.get(0) - i).getFill() == mazeVoidColor && lookLeft) {
                                robotPositions.add(robotPositions.get(0) - i);
                            } else {
                                lookLeft = false;
                            }
                        }
                    }

                    // Prüfe ob Roboter gesetzt werden konnte
                    if (robotPositions.size() != selectedRobot.getSizeX()) {
                        robotPositions.clear();
                        freeMazeFields.remove(startPosition);
                    } else {
                        robotSuccessfullySetXPosition = true;
                    }
                }
                if (robotPositions.size() < selectedRobot.getSizeX()) {
                    // TODO in Historie eintragen
                    throw new IllegalStateException("Roboter konnte nicht gesetzt werden. Kein geeigneter Platz gefunden");
                }

                // Finde benachbarte freie Felder Y Richtung
                boolean lookAbove = true;
                boolean lookBelow = true;
                for (int y = 1; y < selectedRobot.getSizeY() && robotPositions.size() < (selectedRobot.getSizeX() * selectedRobot.getSizeY()) && (lookAbove || lookBelow); y++) {
                    for (int x = 0; x < selectedRobot.getSizeX(); x++) {
                        if (lookAbove && !(mazeFields.get(robotPositions.get(x) + (y * SimulationMaze.getSelectedMaze().getMazeSizeY())).getFill() == mazeVoidColor)) {
                            lookAbove = false;
                        }
                        if (lookBelow && !(mazeFields.get(robotPositions.get(x) - (y * SimulationMaze.getSelectedMaze().getMazeSizeY())).getFill() == mazeVoidColor)) {
                            lookBelow = false;
                        }
                    }
                    if (lookAbove && robotPositions.size() < (selectedRobot.getSizeX() * selectedRobot.getSizeY())) {
                        for (int x = 0; x < selectedRobot.getSizeX(); x++) {
                            robotPositions.add(robotPositions.get(x) + (y * SimulationMaze.getSelectedMaze().getMazeSizeY()));
                        }
                    }
                    if (lookBelow && robotPositions.size() < (selectedRobot.getSizeX() * selectedRobot.getSizeY())) {
                        for (int x = 0; x < selectedRobot.getSizeX(); x++) {
                            robotPositions.add(robotPositions.get(x) - (y * SimulationMaze.getSelectedMaze().getMazeSizeY()));
                        }
                    }
                }
                // Prüfe ob Roboter vollständig gesetzt werden konnte
                if (robotPositions.size() != (selectedRobot.getSizeX() * selectedRobot.getSizeY())) {
                    robotPositions.clear();
                    freeMazeFields.remove(startPosition);
                } else {
                    robotSuccessfullySet = true;
                }
            }
            if (robotPositions.size() != (selectedRobot.getSizeX() * selectedRobot.getSizeY())) {
                throw new IllegalStateException("Roboter konnte nicht gesetzt werden");
            }

            // Übermittle Roboter seine Positionen
            int[] robotPositionsArray = OwnUtils.convertArrayListToIntArray(robotPositions);

            selectedRobot.setPosition(robotPositionsArray);

            // Setze Roboter aufs Feld
            updateMaze(true);
        }
    }

    @FXML
    void mazeMoveRobot(KeyEvent event) {
        System.out.println(":" + event.getCode());
        if(SimulationRobot.getIndexSelectedRobot() != null) {
            // TODO switch Kopfteil
            boolean freeFields = true;
            int[] sortedPositions = SimulationRobot.getSelectedRobot().getPosition();
            Arrays.sort(sortedPositions);
            switch (event.getCode().toString()) {
                case "RIGHT":
                    // TODO Roboter nach rechts bewegen
                    System.out.println("Right");
                    break;
                case "LEFT":
                    // TODO Roboter nach links bewegen
                    System.out.println("left");
                    break;
                case "DOWN":
                    for(int x = 0; x < SimulationRobot.getSelectedRobot().getSizeX(); x++){
                        if(!(mazeFields.get(sortedPositions[sortedPositions.length - 1 - x] + SimulationMaze.getSelectedMaze().getMazeSizeY()).getFill() == mazeVoidColor)){
                            freeFields = false;
                        }
                    }
                    if(freeFields){
                        SimulationRobot.getSelectedRobot().keyboardMoveDown();
                        updateMaze(true);
                    } else {
                        SimulationRobot.getSelectedRobot().isBumped();
                    }
                    break;
                case "UP":
                    // TODO an Position des Kopfes anpassen
                    for(int x = 0; x < SimulationRobot.getSelectedRobot().getSizeX(); x++){
                        if(!(mazeFields.get(sortedPositions[x] - SimulationMaze.getSelectedMaze().getMazeSizeY()).getFill() == mazeVoidColor)){
                            freeFields = false;
                        }
                    }
                    if(freeFields){
                        SimulationRobot.getSelectedRobot().keyboardMoveUp();
                        updateMaze(true);
                    } else {
                        // TODO in Historie eintragen
                        SimulationRobot.getSelectedRobot().isBumped();
                    }
                    break;
                default: // TODO noch auf andere Tastatureingaben reagieren? z.B. zum Drehen
                    break;
            }
        }
    }

    @FXML
    void robotDelete(ActionEvent event) {

    }

    @FXML
    void robotGetMatrix(ActionEvent event) {

    }

    @FXML
    void robotStartStop(ActionEvent event) {

    }

    @FXML
    void selectNewMaze(MouseEvent event) {
        if (mazefileTable.getSelectionModel().getSelectedItem() != null) {
            int selectedRowNumber = mazefileTable.getSelectionModel().getSelectedItem().getNr();

            // setzte selectedRobot in SimulationMaze
            if(SimulationRobot.getRobots().size() > 0){
                SimulationMaze.getSelectedMaze().getMazeRobots().clear();
                SimulationMaze.getSelectedMaze().getMazeRobots().addAll(SimulationRobot.getRobots());
                SimulationMaze.getSelectedMaze().setChangeMazeSelectedRobot(SimulationRobot.getIndexSelectedRobot());
            }

            // Index Position im mazefile Array eines geringer als mazefile
            boolean tableDataChanged = SimulationMaze.changeSelectedMaze(selectedRowNumber - 1);

            if(tableDataChanged) {
                mazefileTableData.clear();
                mazefileTableData.addAll(SimulationMaze.getMazeFiles());
                mazefileTable.sort();

                // Lösche Roboter
                SimulationRobot.deleteAllRobots();
                robotTableData.clear();

                // Füge Maze eigene Roboter hinzu
                if(SimulationMaze.getSelectedMaze().getMazeRobots().size() > 0) {
                    for (int i = 0; i < SimulationMaze.getSelectedMaze().getMazeRobots().size(); i++) {
                        int robotPixelX = SimulationMaze.getSelectedMaze().getMazeRobots().get(i).getSizeX();
                        int robotPixelY = SimulationMaze.getSelectedMaze().getMazeRobots().get(i).getSizeY();
                        Color robotColor = SimulationMaze.getSelectedMaze().getMazeRobots().get(i).getRobotColor();
                        int[] position = SimulationMaze.getSelectedMaze().getMazeRobots().get(i).getPosition();
                        SimulationRobot.addRobot(robotPixelX, robotPixelY, robotColor, position);
                    }
                    SimulationRobot.changeSelectedRobot(SimulationMaze.getSelectedMaze().getChangeMazeSelectedRobot());
                    robotTableData.addAll(SimulationRobot.getRobots());
                    robotTable.sort();
                }

                // Setze Label
                mazeLable.setText(MAZE_LABEL_PREFIX + SimulationMaze.getSelectedMaze().getFILE_NAME() + " Größe: " + SimulationMaze.getSelectedMaze().getMazeSizeX() + "x" + SimulationMaze.getSelectedMaze().getMazeSizeY());

                updateMaze(SimulationMaze.getSelectedMaze().getMazeRobots().size() > 0);
            }
        }
    }

    @FXML
    void selectNewRobot(MouseEvent event) {
        if(robotTable.getSelectionModel().getSelectedItem() != null){
            int selectedRowNumber = robotTable.getSelectionModel().getSelectedItem().getRobotNumber();
            // Index Position im robots Array eines geringer als RoboNumber
            boolean tableDataChanged = SimulationRobot.changeSelectedRobot(selectedRowNumber - 1);

            if(tableDataChanged) {
                robotTableData.clear();
                robotTableData.addAll(SimulationRobot.getRobots());
                robotTable.sort();

                robotSelectedLable.setText(ROBOT_LABEL_PREFIX + SimulationRobot.getSelectedRobot().getRoboName());

                updateMaze(true);
            }
        }
    }

    private void updateMaze(boolean drawRobot){
        drawMaze(DIRECTORY_MAZE_FILES + "\\" + SimulationMaze.getSelectedMaze().getFILE_NAME());

        if(drawRobot) {
            for (int robotPosition : SimulationRobot.getSelectedRobot().getPosition()) {
                Rectangle robotField = mazeFields.get(robotPosition);
                robotField.setFill(SimulationRobot.getSelectedRobot().getRobotColor());
                mazeFields.set(robotPosition, robotField);
            }
        }
    }

    private void drawMaze(String filePath) {
        mazeFields.clear();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(filePath));

            StringBuilder sb = new StringBuilder();
            String line = "";

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }
            br.close();

//          System.out.println(sb.toString());
            String[] mazeStringParts = sb.toString().split(System.lineSeparator());

            // Setze Pixelgröße einzelner Labyrinthbausteine
            mazePixelX = (int) mazePane.getWidth() / mazeStringParts[0].length();
            mazePixelY = (int) mazePane.getHeight() / mazeStringParts.length;

            // Initialisiere MazeLabel, wenn Maze das erste mal geladen wird
            if(SimulationMaze.getSelectedMaze().getMazeSizeY() == null && SimulationMaze.getSelectedMaze().getMazeSizeX() == null){
                mazeLable.setText(MAZE_LABEL_PREFIX + SimulationMaze.getSelectedMaze().getFILE_NAME() + " Größe: " + mazeStringParts.length + "x" + mazeStringParts[0].length());
            }

            // Setzt die Größe des Labyrinths
            SimulationMaze.getSelectedMaze().setMazeSizeX(mazeStringParts.length);
            SimulationMaze.getSelectedMaze().setMazeSizeY(mazeStringParts[0].length());

            // System.out.println("x: " + mazePixelX + " y: " + mazePixelY);
            for (int y = 0; y < mazeStringParts.length; y++) {
//                System.out.print(y + ": ");
                for (int x = 0; x < mazeStringParts[0].length(); x++) {
                    Rectangle mazeField = new Rectangle(x * mazePixelX, y * mazePixelY, mazePixelX, mazePixelY);
                    if (mazeStringParts[y].charAt(x) == mazeWallSymbol) {
                        mazeField.setFill(mazeWallColor);
                    } else if (mazeStringParts[y].charAt(x) == mazeVoidSymbol) {
                        mazeField.setFill(mazeVoidColor);
                    } else {
                        mazeField.setFill(mazeErrorColor);
                    }
//                    System.out.print(mazeStringParts[y].charAt(x));
                    mazeFields.add(mazeField);
                }
//                System.out.println();
            }
            mazePane.getChildren().setAll(mazeFields);

        } catch (FileNotFoundException e) {
            System.err.println("Datei: " + filePath + " nicht gefunden!");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getMazeFiles(DIRECTORY_MAZE_FILES);

        // Initialisiere MazeTable
        mazefileTableNr.setCellValueFactory(new PropertyValueFactory<>("nr"));
        mazefileTableFilename.setCellValueFactory(new PropertyValueFactory<>("FILE_NAME"));
        mazefileTableSelected.setCellValueFactory(new PropertyValueFactory<>("selected"));
        mazefileTable.setItems(mazefileTableData);

        // Initialisiere RobotTable
        robotTableNr.setCellValueFactory(new PropertyValueFactory<>("robotNumber"));
        robotTableName.setCellValueFactory(new PropertyValueFactory<>("roboName"));
        robotTableSelected.setCellValueFactory(new PropertyValueFactory<>("selected"));
        robotTable.setItems(robotTableData);
    }

    private void getMazeFiles(File dir) {
        File files[] = dir.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile() && files[i].getName().startsWith("maze")) {
                SimulationMaze.addMazefileTableData(files[i].getName());
            }
        }

        mazefileTableData.addAll(SimulationMaze.getMazeFiles());

        // Debugg-Code: Zeige Mazefiles auf Konsole an
//        for(SimulationMaze i : mazefileTableData){
//            System.out.println(i.toString());
//        }

        if (mazefileTableData.size() == 0) {
            throw new IllegalArgumentException("Keine gültigen Labyrinthdateien bei " + DIRECTORY_MAZE_FILES + " gefunden");
        }

    }

}
