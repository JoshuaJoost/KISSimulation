package gui_simulation;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;

public class SimulationMaze {
    private static int numberOfMazeFiles = 0;
    private static ArrayList<SimulationMaze> mazeFiles = new ArrayList<>();
    private static Integer selectedMazeNumber = null;
    private static final SimpleStringProperty SELECTED_TEXT = new SimpleStringProperty("yep");
    private static final SimpleStringProperty NOT_SELECTED_TEXT = new SimpleStringProperty("");

    private final Integer nr;
    private final SimpleStringProperty FILE_NAME;
    private SimpleStringProperty selected;
    private ArrayList<SimulationRobot> mazeRobots = new ArrayList<>();
    private int changeMazeSelectedRobot = 0;

    private SimulationMaze(String filename) {
        nr = (++numberOfMazeFiles);
        FILE_NAME = new SimpleStringProperty(filename);
        selected = new SimpleStringProperty("");
    }

    public static ArrayList<SimulationMaze> addMazefileTableData(String filename) {
        mazeFiles.add(new SimulationMaze(filename));
        return mazeFiles;
    }

    public static boolean changeSelectedMaze(int indexNewSelectedMaze) {
        if (indexNewSelectedMaze <= mazeFiles.size() - 1) {
            if (selectedMazeNumber == null) {
                selectedMazeNumber = indexNewSelectedMaze;
                mazeFiles.get(selectedMazeNumber).setSelected(SELECTED_TEXT);
                return true;
            } else {
                if (selectedMazeNumber != indexNewSelectedMaze) {
                    mazeFiles.get(selectedMazeNumber).setSelected(NOT_SELECTED_TEXT);
                    selectedMazeNumber = indexNewSelectedMaze;
                    mazeFiles.get(selectedMazeNumber).setSelected(SELECTED_TEXT);
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }

    private void setSelected(SimpleStringProperty selectionText) {
        this.selected = selectionText;
    }

    public static SimulationMaze getMazefileTableDataN(int index) {
        return mazeFiles.get(index);
    }

    public static SimulationMaze getSelectedMaze() {
        return mazeFiles.get(selectedMazeNumber);
    }

    public static Integer getSelectedMazeNumber(){
        return selectedMazeNumber;
    }

    public static ArrayList<SimulationMaze> getMazeFiles() {
        return mazeFiles;
    }

    @Override
    public String toString() {
        return "Nr. <" + nr + "> Dateiname: <" + FILE_NAME.getValue() + "> Ausgewählt Text: <" + selected.getValue() + ">";
    }

    public Integer getNr() {
        return nr;
    }

    public String getFILE_NAME() {
        return FILE_NAME.getValue();
    }

    public String getSelected() {
        return selected.getValue();
    }

    public void addRobotToMaze(SimulationRobot newMazeRobot){
        this.mazeRobots.add(newMazeRobot);
    }

    public ArrayList<SimulationRobot> getMazeRobots(){
        return mazeRobots;
    }

    public void setChangeMazeSelectedRobot(int newSelectedRobot){
        this.changeMazeSelectedRobot = newSelectedRobot;
    }

    public int getChangeMazeSelectedRobot() {
        return this.changeMazeSelectedRobot;
    }

}
