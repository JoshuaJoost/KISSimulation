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

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private static final File DIRECTORY_MAZE_FILES = new File((System.getProperty("user.dir") + "\\src\\gui_simulation\\mazeFiles"));

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

    @FXML
    private Label mazeLableSelectedFile;

    @FXML
    void addNewRobot(ActionEvent event) {

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
        // TODO ausgewählte Reihe selectieren -> entspr. Befehl in MazefileTableData
        System.out.println("Table klicked");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mazefileTableNr.setCellValueFactory(new PropertyValueFactory<>("nr"));
        mazefileTableFilename.setCellValueFactory(new PropertyValueFactory<>("FILE_NAME"));
        mazefileTableSelected.setCellValueFactory(new PropertyValueFactory<>("selected"));

        mazefileTable.setItems(getMazeFiles(DIRECTORY_MAZE_FILES));
    }

    private static ObservableList<MazefileTableData> getMazeFiles(File dir) {
        File files[] = dir.listFiles();
        ObservableList<MazefileTableData> mazeFiles = FXCollections.observableArrayList();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile() && files[i].getName().startsWith("maze")) {
                MazefileTableData.addMazefileTableData(files[i].getName());
            }
        }

        mazeFiles.addAll(MazefileTableData.getMazefileTableData());

        for(MazefileTableData i : mazeFiles){
            System.out.println(i.toString());
        }

        // TODO DebuggCode zeige Mazefiles auf Konsole an
//        for (int i = 0; i < mazeFiles.size(); i++) {
//            System.out.println(mazeFiles.get(i).getName());
//        }
        // TODO END

        if(mazeFiles.size() == 0){
            throw new IllegalArgumentException("Keine gültigen Labyrinthdateien bei " + DIRECTORY_MAZE_FILES + " gefunden");
        }

        return mazeFiles;
    }
}
