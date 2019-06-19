package gui_simulation;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;

public class MazefileTableData {
    private static int numberOfMazeFiles = 0;
    private static ArrayList<MazefileTableData> mazeFiles = new ArrayList<>();
    private static Integer selectedMaze = null;
    private static final SimpleStringProperty SELECTED_TEXT = new SimpleStringProperty("yep");
    private static final SimpleStringProperty NOT_SELECTED_TEXT = new SimpleStringProperty("");

    private final Integer nr;
    private final SimpleStringProperty FILE_NAME;
    private SimpleStringProperty selected;

    private MazefileTableData(String filename) {
        nr = (++numberOfMazeFiles);
        FILE_NAME = new SimpleStringProperty(filename);
        selected = new SimpleStringProperty("");
    }

    public static ArrayList<MazefileTableData> addMazefileTableData(String filename) {
        mazeFiles.add(new MazefileTableData(filename));
        return mazeFiles;
    }

    public static boolean changeSelectedMaze(int indexNewSelectedMaze) {
        if (indexNewSelectedMaze <= mazeFiles.size() - 1) {
            if (selectedMaze == null) {
                selectedMaze = indexNewSelectedMaze;
                mazeFiles.get(selectedMaze).setSelected(SELECTED_TEXT);
                return true;
            } else {
                if (selectedMaze != indexNewSelectedMaze) {
                    mazeFiles.get(selectedMaze).setSelected(NOT_SELECTED_TEXT);
                    selectedMaze = indexNewSelectedMaze;
                    mazeFiles.get(selectedMaze).setSelected(SELECTED_TEXT);
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

    public static MazefileTableData getMazefileTableDataN(int index) {
        return mazeFiles.get(index);
    }

    public static MazefileTableData getSelectedMaze() {
        return mazeFiles.get(selectedMaze);
    }

    public static ArrayList<MazefileTableData> getMazefileTableData() {
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

}
