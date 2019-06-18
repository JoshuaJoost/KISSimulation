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
import org.w3c.dom.css.Rect;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

// TODO
// 0 - Sortieralgorithmus für MazefileTable schreiben
// 9 - Klick Event: Roboter hinzufügen - Startposition in Maze selbst?#
// Dokumentation: Overleaf -> Beide den selben Account (auch gleichzeitig zugreifen geht)
// Nach wissenschaftlichen Arbeiten bezüglich Roboter und Labyrinth suchen
// Wie wird Simulation und Realität aufeinander abgestimmt?
// An Merkzettel für schriftliche Arbeiten orientieren
// KIS Prüfung: 20-25min Projekt vorstellen, anschließend Fragen

public class Controller_MainGUI implements Initializable {

    private final char mazeWallSymbol = '#';
    private final char mazeVoidSymbol = ' ';

    private final Color mazeWallColor = Color.rgb(0,0,0);
    private final Color mazeVoidColor = Color.rgb(255,255,255);
    private final Color mazeGroundTarget = Color.rgb(255,0,0);
    private final Color mazeErrorColor = Color.rgb(0,255,246);

    private static final File DIRECTORY_MAZE_FILES = new File((System.getProperty("user.dir") + "\\src\\gui_simulation\\mazeFiles"));
    private final String MAZE_LABEL_PREFIX = "Labyrinth: ";

    private ObservableList<Rectangle> mazeFields = FXCollections.observableArrayList();
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
    private TableView<?> robotTable;

    @FXML
    private TableColumn<?, ?> robotTableNr;

    @FXML
    private TableColumn<?, ?> robotTableName;

    @FXML
    private TableColumn<?, ?> robotTableSelected;

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
    private TableView<MazefileTableData> mazefileTable;

    @FXML
    private TableColumn<MazefileTableData, Integer> mazefileTableNr;

    @FXML
    private TableColumn<MazefileTableData, String> mazefileTableFilename;

    @FXML
    private TableColumn<MazefileTableData, String> mazefileTableSelected;

    private ObservableList<MazefileTableData> mazefileTableData = FXCollections.observableArrayList();

    @FXML
    private Label mazeLableSelectedFile;

    @FXML
    void addNewRobot(ActionEvent event) {
        System.out.println("x <" + mazePixelX + "> y <" + mazePixelY + ">");
        SimulationRobot.addRobot(4, 3);

        int[] roboPositions;
        ArrayList<Integer> foundPositions = new ArrayList<>();

        // Suche nach zufälligem freiem Feld im Labyrinth
        ArrayList<Rectangle> tmpFields = new ArrayList<>();
        tmpFields.addAll(mazeFields);
        int possibleRoboPosition = (int)(Math.random() * tmpFields.size()) + 1;
        boolean foundFreeField = false;

        while(foundPositions.size() == 0 && tmpFields.size() > 0){
            if(tmpFields.get(possibleRoboPosition).getFill() == mazeVoidColor){
                foundPositions.add(possibleRoboPosition);
            } else {
                tmpFields.remove(foundPositions);
                possibleRoboPosition = (int)(Math.random() * tmpFields.size()) + 1;
            }
        }

        roboPositions = new int[foundPositions.size()];
        for(int i = 0; i < foundPositions.size(); i++){
            roboPositions[i] = foundPositions.get(i);
        }

        SimulationRobot robot1 = SimulationRobot.getRobots().get(0);
        robot1.setPosition(roboPositions);

        // Setze Roboter ins Labyrinth
        for(int i = 0; i < robot1.getPosition().length; i++){
            Rectangle newRobotField = mazeFields.get(robot1.getPosition()[i]);
            newRobotField.setFill(SimulationRobot.getColor());
            mazeFields.set(robot1.getPosition()[i], newRobotField);
        }
    }

    @FXML
    void mazeMoveRobot(KeyEvent event) {
        System.out.println(":" + event.getCode());
        switch(event.getCode().toString()){
            case "RIGHT":
                // TODO Roboter nach rechts bewegen
                System.out.println("Right");
                break;
            case "LEFT":
                // TODO Roboter nach links bewegen
                System.out.println("left");
                break;
            case "DOWN":
                // TODO Roboter nach oben bewegen
                System.out.println("down");
                break;
            case "UP":
                // TODO Roboter nach unten bewegen
                System.out.println("up");
                break;
            default: // TODO noch auf andere Tastatureingaben reagieren? z.B. zum Drehen
                break;
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
    void mazefileTableSelectRow(MouseEvent event) {
        // Line number one smaller than index position
        if(mazefileTable.getSelectionModel().getSelectedItem() != null) {
            int selectedRowIndexNumber = mazefileTable.getSelectionModel().getSelectedItem().getNr() - 1;
            MazefileTableData.selectMaze(selectedRowIndexNumber);

            mazefileTableData.set(selectedRowIndexNumber, MazefileTableData.getMazefileTableDataN(selectedRowIndexNumber));
            mazeLable.setText(MAZE_LABEL_PREFIX + mazefileTable.getSelectionModel().getSelectedItem().getFILE_NAME());

            // TODO Labyrinth einfügen
            drawMaze(DIRECTORY_MAZE_FILES + "\\" + mazefileTable.getSelectionModel().getSelectedItem().getFILE_NAME());
        }
    }

    private void drawMaze(String filePath){
        mazeFields.clear();
        BufferedReader br;
        try{
            br = new BufferedReader(new FileReader(filePath));

            StringBuilder sb = new StringBuilder();
            String line = "";

            while((line = br.readLine()) != null){
                sb.append(line);
                sb.append(System.lineSeparator());
            }
            br.close();

//          System.out.println(sb.toString());
            String[] mazeStringParts = sb.toString().split(System.lineSeparator());

            // Setze Pixelgröße einzelner Labyrinthbausteine
            mazePixelX = (int) mazePane.getWidth() / mazeStringParts[0].length();
            mazePixelY = (int) mazePane.getHeight() / mazeStringParts.length;

            // Setze Größenangabe in mazeLable
            String newMazeLabelText = mazeLable.getText() + " Größe: " + mazeStringParts[0].length() + "x" + mazeStringParts.length;
            mazeLable.setText(newMazeLabelText);

            System.out.println("x: " + mazePixelX + " y: " + mazePixelY);
            for(int y = 0; y < mazeStringParts.length; y++){
                System.out.print(y + ": ");
                for (int x = 0; x < mazeStringParts[0].length(); x++){
                    Rectangle mazeField = new Rectangle(x * mazePixelX,y * mazePixelY, mazePixelX, mazePixelY);
                    if(mazeStringParts[y].charAt(x) == mazeWallSymbol){
                        mazeField.setFill(mazeWallColor);
                    }
                    else if(mazeStringParts[y].charAt(x) == mazeVoidSymbol){
                        mazeField.setFill(mazeVoidColor);
                    } else {
                        mazeField.setFill(mazeErrorColor);
                    }
                    System.out.print(mazeStringParts[y].charAt(x));
                    mazeFields.add(mazeField);
                }
                System.out.println();
            }
            mazePane.getChildren().setAll(mazeFields);

            // TODO: Code zeigt wie Maze manipulierbar ist
//            Rectangle a = mazeFields.get(10);
//            a.setFill(Color.rgb(255,0,0));
//            mazeFields.set(10, a);
//            mazePane.getChildren().setAll(mazeFields);

        } catch (FileNotFoundException e){
            System.err.println("Datei: " + filePath + " nicht gefunden!");
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getMazeFiles(DIRECTORY_MAZE_FILES);

        mazefileTableNr.setCellValueFactory(new PropertyValueFactory<>("nr"));
        mazefileTableFilename.setCellValueFactory(new PropertyValueFactory<>("FILE_NAME"));
        mazefileTableSelected.setCellValueFactory(new PropertyValueFactory<>("selected"));

        mazefileTable.setItems(mazefileTableData);
    }

    private void getMazeFiles(File dir) {
        File files[] = dir.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile() && files[i].getName().startsWith("maze")) {
                MazefileTableData.addMazefileTableData(files[i].getName());
            }
        }

        mazefileTableData.addAll(MazefileTableData.getMazefileTableData());

        // Debugg-Code: Zeige Mazefiles auf Konsole an
//        for(MazefileTableData i : mazefileTableData){
//            System.out.println(i.toString());
//        }

        if(mazefileTableData.size() == 0){
            throw new IllegalArgumentException("Keine gültigen Labyrinthdateien bei " + DIRECTORY_MAZE_FILES + " gefunden");
        }

    }

}
