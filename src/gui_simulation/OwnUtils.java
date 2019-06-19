package gui_simulation;

import java.util.ArrayList;

public class OwnUtils {

    public static int[] convertArrayListToIntArray(ArrayList<Integer> arrayList){
        int[] intArray;
        if(arrayList == null){
            throw new IllegalArgumentException("ArrayList darf nicht null sein");
        } else {
            intArray = new int[arrayList.size()];

            for(int i = 0; i < arrayList.size(); i++){
                intArray[i] = arrayList.get(i);
            }
        }

        return intArray;
    }
}
