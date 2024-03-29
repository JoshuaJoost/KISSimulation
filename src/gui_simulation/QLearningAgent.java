package gui_simulation;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class QLearningAgent {
    private double epsilon = 0.2;
    private double alpha = 0.1; // Lernrate (0..1)
    private double gamma = 0.9; // Bewertungsfaktor (0..1)
    private double q[][]; // Q-Learning-Array
    private static final int POSSIBLE_ACTIONS = 4;

    private static final String[] stateText = {
            "Keine Barriere", "Barriere vorne", "Barriere links",
            "Barriere rechts", "Barriere vorne + links", "Barriere vorne + rechts", "Barriere links + rechts",
            "Barriere links + vorne + rechts", "Angestoßen"
    };
    /*
     * possible actions: DRIVE_FORWARD = 0, DRIVE_LEFT = 1,
     * DRIVE_RIGHT = 2, DRIVE_BACKWARD = 3
     */
    private static final int BARRIER_LOCATIONS = 8;
    // is the robot bumped or not? 1 state for the barrier bumped and one state for the location not bumped
    // dhort of no barrier
    private static final int BUMPED = 1;
    /*
     * 8 barrier states: no barrier, front, left, right, front+left, front+right, right+left, front+right+left
     */

    public QLearningAgent() {
        this.q = new double[BARRIER_LOCATIONS+BUMPED][POSSIBLE_ACTIONS];
        // initalize q
        for(int i = 0; i < this.q.length; i++) {
            for(int j=0; j < this.q[i].length; j++) {
                // values between 0 and 0.1 without 0.1
                this.q[i][j] = Math.random() / 10;
            }
        }
//		printQTable();
    }

    public QLearningAgent(double [][] array) {
        this.q = array;
//		printQTable();
    }

    public void printQTable() {
        int longestStateText = 0;
        for(int i = 0; i < QLearningAgent.stateText.length; i++){
            if(QLearningAgent.stateText[i].length() > longestStateText){
                longestStateText = QLearningAgent.stateText[i].length();
            }
        }

        String distanceBetween = "     ";

        String distance = "";
        for(int i = 0; i < longestStateText + 5; i++){
            distance += " ";
        }
        System.out.println(distance + "vorwärts" + "\t" + "links" + "\t\t" + "rechts" + "\t\t" + "rückwärts");
        for (int i = 0; i < BARRIER_LOCATIONS+BUMPED; i++) {
            String stateString = QLearningAgent.stateText[i];
            for(int j = 0; j < longestStateText - QLearningAgent.stateText[i].length() + 5; j++){
                stateString += " ";
            }
            for (int j=0; j < POSSIBLE_ACTIONS; j++) {
                stateString += ("" + this.q[i][j]).substring(0, 5) + "\t\t";
            }
            System.out.println(stateString);
        }
    }

    /**
     * Lernt durch die Ã¼bergebenen ZustÃ¤nde, ob die Aktion erfolgreich war und
     * speichert die Werte in das q-array.
     * @param actualState: Aktueller Zustand
     * @param nextState: NÃ¤chster Zustand
     * @param action: Aktion
     * @param reward: Belohnung
     */
    public void learn(int actualState, int nextState, int action, double reward) {
        ArrayList<Integer> nextActions = new ArrayList<>();
        nextActions.addAll(actionWithBestRating(nextState));

        Integer nextAction = null;
        if(nextActions.size() > 1){
            int rnd = (int)(Math.random() * nextActions.size());
            nextAction = nextActions.get(rnd);
        } else {
            nextAction = nextActions.get(0);
        }


        this.q[actualState][action] += this.alpha * (reward + this.gamma * (this.q[nextState][nextAction]) - q[actualState][action]);
    }

    /**
     * WÃ¤hlt die Aktion mit der besten Rate aus
     * @param s: Zustand s
     * @return: Gibt die Aktion als int zurÃ¼ck.
     */
    public ArrayList<Integer> actionWithBestRating(int s) {
        ArrayList<Integer> indexActionsWithBestRating = new ArrayList<>();
        double max = Integer.MIN_VALUE;

        for (int i = 0; i < POSSIBLE_ACTIONS; i++) {
            if (this.q[s][i] >= max) {
                max = this.q[s][i];
                indexActionsWithBestRating.add(i);
            }
        }
//        return index;
        return indexActionsWithBestRating;
    }

    /**
     * WÃ¤hlt eine zufÃ¤llige Aktion anhand des Zustands aus
     * @param s: Zustand
     * @return: Gibt die Aktion als int zurÃ¼ck.
     */
    public int chooseAction(int s) {
        int a = 0;
        if (Math.random() < this.epsilon) {
            // + 1 to have the last inclusive
            a = ThreadLocalRandom.current().nextInt(0, POSSIBLE_ACTIONS);
        } else {
            ArrayList<Integer> indexActionsWithBestRating = new ArrayList<>(actionWithBestRating(s));
            //indexActionsWithBestRating.addAll(actionWithBestRating(s));
            //a = actionWithBestRating(s);
            if(indexActionsWithBestRating.size() > 1){
                int rnd = (int)(Math.random() * indexActionsWithBestRating.size());
                a = indexActionsWithBestRating.get(rnd);
//                System.out.print("Auswahl: \t\t" + a + " ");
            } else {
                a = indexActionsWithBestRating.get(0);
            }
        }

        return a;
    }

    public void setEpsilon(double newEpsilonValue){
        this.epsilon = newEpsilonValue;
    }

    public double getEpsilon(){
        return this.epsilon;
    }
}
