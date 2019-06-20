package gui_simulation;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;

public class SimulationMaze {

    // File
    // TODO Controller umbauen, soll nur noch hier drauf zugreifen
    public static final String mazeWallSymbol = "#";
    public static final String mazeVoidSymbol = " ";

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
    private Integer mazeSizeX = null;
    private Integer mazeSizeY = null;
    private ArrayList<Integer> mazeFreeFields = new ArrayList<>();

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

    public static SimulationMaze whatLabyrinthDoIBelongTo(int robotNumber){
        for(int i = 0; i < mazeFiles.size(); i++){
            for(int j = 0; j < mazeFiles.get(i).getMazeRobots().size(); j++){
                if(mazeFiles.get(i).getMazeRobots().get(j).getRobotNumber() == robotNumber){
                    return mazeFiles.get(i);
                }
            }
        }
        // Roboter gehört keinem Labyrinth an
        return null;
    }

    @Override
    public String toString() {
        return "Nr. <" + nr + "> Dateiname: <" + FILE_NAME.getValue() + "> Ausgewählt Text: <" + selected.getValue() + ">";
    }

    public void addFreeField(Integer positionNumber){
        this.mazeFreeFields.add(positionNumber);
    }

    public ArrayList<Integer> getMazeFreeFields(){
        return this.mazeFreeFields;
    }

    public String getMazeFreeFieldsToString(){
        String mazeFreeFieldsString = "";

        String newField;
        String fieldWidth = "" + (mazeSizeX * mazeSizeY) + " ";
        for(int i = 0, y = 0; i < mazeSizeY * mazeSizeX; i++){
            newField = "";
            if(y < this.mazeFreeFields.size() && this.mazeFreeFields.get(y) == i){
                y++;
                newField += i;
            } else {
                newField += SimulationMaze.mazeWallSymbol;
            }

            if(newField.length() < fieldWidth.length()) {
                for (int c = 0; c < fieldWidth.length() && newField.length() < fieldWidth.length(); c++) {
                    newField += " ";
                }
            }

            if((i + 1) % mazeSizeX == 0){
                newField += "\n";
            }

            mazeFreeFieldsString += newField;
        }

        return mazeFreeFieldsString;
    }

    private void setSelected(SimpleStringProperty selectionText) {
        this.selected = selectionText;
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

    public void setMazeSizeX(int mazeSizeX){
        this.mazeSizeX = mazeSizeX;
    }

    public Integer getMazeSizeX(){
        return this.mazeSizeX;
    }

    public void setMazeSizeY(int mazeSizeY){
        this.mazeSizeY = mazeSizeY;
    }

    public Integer getMazeSizeY(){
        return this.mazeSizeY;
    }

}
