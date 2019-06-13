package gui_simulation;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;

public class Controller {

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
    private TableView<?> mazeDateTable;

    @FXML
    private TableColumn<?, ?> mazefileTableNr;

    @FXML
    private TableColumn<?, ?> mazefileTableFilename;

    @FXML
    private TableColumn<?, ?> mazefileTableSelected;

    @FXML
    private Label mazeLableSelectedFile;

    @FXML
    void addNewRobot(ActionEvent event) {

    }

    @FXML
    void mazeMoveRobot(KeyEvent event) {

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

}
