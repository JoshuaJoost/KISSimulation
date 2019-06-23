package gui_simulation;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class SimulationMaze {

    // File
    // TODO Controller umbauen, soll nur noch hier drauf zugreifen
    public static final char mazeWallSymbol = '#';
    public static final char mazeVoidSymbol = ' ';

    public static final Color mazeVoidColor = Color.rgb(255, 255, 255);
    public static final Color mazeWallColor = Color.rgb(0, 0, 0);
    public static final Color mazeTargetColor = Color.rgb(255,0,0);
    public static final Color mazeErrorColor = Color.rgb(0,255,246);

    private static int numberOfMazeFiles = 0;
    private static ArrayList<SimulationMaze> mazeFiles = new ArrayList<>();
    private static Integer selectedMazeNumber = null;
    private static final SimpleStringProperty SELECTED_TEXT = new SimpleStringProperty("yep");
    private static final SimpleStringProperty NOT_SELECTED_TEXT = new SimpleStringProperty("");

    // Labyrinth Table
    private final Integer nr;
    private final SimpleStringProperty FILE_NAME;
    private SimpleStringProperty selectedMazeText;
    // Labyrinth Werte
    private final Integer mazeSizeX;
    private final Integer mazeSizeY;
    private final ArrayList<Integer> indexMazeFreeFields;
    private final ArrayList<Rectangle> mazeDrawFields;
    // Labyrinth Roboter
    private ArrayList<SimulationRobot> mazeRobots = new ArrayList<>();
    private Integer selectedRobotNumber = null;
    public int uniqueIndexNumberOfMazeRobot = 0;

    private SimulationMaze(String filename, int mazePaneX, int mazePaneY) {
        nr = (++numberOfMazeFiles);
        FILE_NAME = new SimpleStringProperty(filename);
        selectedMazeText = new SimpleStringProperty("");

        Integer mazeSizeX = null;
        Integer mazeSizeY = null;
        ArrayList<Rectangle> mazeDrawFields = new ArrayList<>();
        ArrayList<Integer> indexMazeFreeFields = new ArrayList<>();

        BufferedReader br;
        try{
            br = new BufferedReader(new FileReader(Controller_MainGUI.DIRECTORY_MAZE_FILES + "\\" + filename));

            StringBuilder sb = new StringBuilder();
            String line = "";

            while((line = br.readLine()) != null){
                sb.append(line);
                sb.append(System.lineSeparator());
            }
            br.close();

            String[] mazeStringParts = sb.toString().split(System.lineSeparator());

            // Setze Pixelgröße einzelner Labyrinthbausteine
            int mazePixelX = mazePaneX / mazeStringParts.length;
            int mazePixelY = mazePaneY / mazeStringParts[0].length();

            mazeSizeX = mazeStringParts.length;
            mazeSizeY = mazeStringParts[0].length();

            for(int y = 0; y < mazeSizeY; y++){
                for(int x = 0; x < mazeSizeX; x++){
                    Rectangle mazeDrawField = new Rectangle(x * mazePixelX, y * mazePixelY, mazePixelX, mazePixelY);
                    if(mazeStringParts[y].charAt(x) == SimulationMaze.mazeWallSymbol){
                        mazeDrawField.setFill(SimulationMaze.mazeWallColor);
                    } else if(mazeStringParts[y].charAt(x) == SimulationMaze.mazeVoidSymbol){
                        mazeDrawField.setFill(SimulationMaze.mazeVoidColor);
                        indexMazeFreeFields.add(mazeDrawFields.size());
                    } else {
                        mazeDrawField.setFill(SimulationMaze.mazeErrorColor);
                    }

                    mazeDrawFields.add(mazeDrawField);
                }
            }

        } catch(IOException e){
            System.err.println("Labyrinthdatei Fehler:");
            e.printStackTrace();
        }

        this.mazeSizeX = mazeSizeX;
        this.mazeSizeY = mazeSizeY;
        this.indexMazeFreeFields = indexMazeFreeFields;
        this.mazeDrawFields = mazeDrawFields;
    }

    public static ArrayList<SimulationMaze> addMazefileTableData(String filename, int mazePaneX, int mazePaneY) {
        mazeFiles.add(new SimulationMaze(filename, mazePaneX, mazePaneY));
        return mazeFiles;
    }

    public static boolean changeSelectedMaze(int indexNewSelectedMaze) {
        if (indexNewSelectedMaze <= mazeFiles.size() - 1) {
            if (selectedMazeNumber == null) {
                selectedMazeNumber = indexNewSelectedMaze;
                mazeFiles.get(selectedMazeNumber).setSelectedMaze(SELECTED_TEXT);
                return true;
            } else {
                if (selectedMazeNumber != indexNewSelectedMaze) {
                    mazeFiles.get(selectedMazeNumber).setSelectedMaze(NOT_SELECTED_TEXT);
                    selectedMazeNumber = indexNewSelectedMaze;
                    mazeFiles.get(selectedMazeNumber).setSelectedMaze(SELECTED_TEXT);
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }

    public static SimulationMaze getSelectedMaze() {
        return mazeFiles.get(selectedMazeNumber);
    }

    public static Integer getSelectedMazeNumber() {
        return selectedMazeNumber;
    }

    public static ArrayList<SimulationMaze> getMazeFiles() {
        return mazeFiles;
    }

    // TODO deprecated machen jeder Roboter muss selbst sein zugehöriges Labyrinth kennen
    public static SimulationMaze whatLabyrinthDoIBelongTo(int robotNumber) {
        for (int i = 0; i < mazeFiles.size(); i++) {
            for (int j = 0; j < mazeFiles.get(i).getMazeRobots().size(); j++) {
                if (mazeFiles.get(i).getMazeRobots().get(j).getRobotNumber() == robotNumber) {
                    return mazeFiles.get(i);
                }
            }
        }
        // Roboter gehört keinem Labyrinth an
        return null;
    }

    @Override
    public String toString() {
        return "Nr. <" + nr + "> Dateiname: <" + FILE_NAME.getValue() + "> Ausgewählt Text: <" + selectedMazeText.getValue() + ">";
    }

    public int getAndSetUniqueIndexNumberOfMazeRobot(){
        int i = this.uniqueIndexNumberOfMazeRobot++;
        System.out.println(i);
        return i;
    }

    public ArrayList<Integer> getIndexMazeFreeFields() {
        return this.indexMazeFreeFields;
    }

    public ArrayList<Rectangle> getMazeDrawFields(){
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
        if(this.mazeRobots.size() > 0) {
            System.out.println(selectedRobotNumber);
            return this.mazeRobots.get(selectedRobotNumber);
        }

        return null;
    }

    public boolean changeSelectedRobot(int indexNewSelectedRobot) {
        System.out.println("tableIndex: " + indexNewSelectedRobot);
        if (this.mazeRobots.size() >= 1 && indexNewSelectedRobot <= this.mazeRobots.size()) {
            if (!(this.selectedRobotNumber == null)) {
                if(indexNewSelectedRobot == this.selectedRobotNumber){
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

//    public boolean changeSelectedRobot(int indexNewSelectedRobot){
//        if(this.mazeRobots.size() > 0 && indexNewSelectedRobot <= this.mazeRobots.size()){
//
//        }
//    }

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

    public void getMazeDrawFieldsToString(){
        String mazeFreeFieldsString = "";

        String newField;
        String fieldWidth = "" + (mazeSizeX * mazeSizeY) + " ";
        for(int y = 0, i = 0; y < mazeDrawFields.size(); y++){
            newField = "";
            if(mazeDrawFields.get(y).getFill() == SimulationMaze.mazeVoidColor){
                i++;
                newField += i;
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
