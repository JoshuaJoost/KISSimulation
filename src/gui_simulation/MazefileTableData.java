package gui_simulation;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;

public class MazefileTableData {
    private static int numberOfMazeFiles = 0;
    private static ArrayList<MazefileTableData> mazeFiles = new ArrayList<>();
    private static Integer indexPositionOfActualSelectedMazeFile = null;
    private static final SimpleStringProperty SELECTED = new SimpleStringProperty("yep");

    private final Integer nr;
    private final SimpleStringProperty FILE_NAME;
    private SimpleStringProperty selected;

    private MazefileTableData(String filename){
        nr = (++numberOfMazeFiles);
        FILE_NAME = new SimpleStringProperty(filename);
        selected = new SimpleStringProperty("");
    }

    public static ArrayList<MazefileTableData> addMazefileTableData(String filename){
        mazeFiles.add(new MazefileTableData(filename));
        return mazeFiles;
    }

    public static ArrayList<MazefileTableData> selectMaze(int rowNumber){
        if(rowNumber <= mazeFiles.size()) {
            if (indexPositionOfActualSelectedMazeFile == null) {
                MazefileTableData updatedData = mazeFiles.get(rowNumber);
                updatedData.setSelected(true);
                mazeFiles.set(rowNumber, updatedData);
            } else {
                // deselect current selected row
                MazefileTableData deselectCurrentSelected = mazeFiles.get(indexPositionOfActualSelectedMazeFile);
                deselectCurrentSelected.setSelected(false);
                mazeFiles.set(indexPositionOfActualSelectedMazeFile, deselectCurrentSelected);

                // select new selected row
                MazefileTableData selectNew = mazeFiles.get(rowNumber);
                selectNew.setSelected(true);
                mazeFiles.set(rowNumber, selectNew);
            }

            indexPositionOfActualSelectedMazeFile = rowNumber;
        }

        return mazeFiles;
    }

    private void setSelected(boolean select){
        if(select){
            this.selected = SELECTED;
        }
        else{
            this.selected = new SimpleStringProperty("");
        }
    }

    public static MazefileTableData getMazefileTableDataN (int index){
        return mazeFiles.get(index);
    }

    public static ArrayList<MazefileTableData> getMazefileTableData(){
        return mazeFiles;
    }

    @Override
    public String toString(){
        return "Nr. <" + nr + "> Dateiname: <" + FILE_NAME.getValue() + "> Ausgewählt Text: <" + selected.getValue() + ">";
    }

    public Integer getNr(){
        return nr;
    }

    public String getFILE_NAME(){
        return FILE_NAME.getValue();
    }

    public String getSelected(){
        return selected.getValue();
    }

}
