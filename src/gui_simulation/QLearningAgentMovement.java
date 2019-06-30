package gui_simulation;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class QLearningAgentMovement {

    public double epsilon = 100; // Zufällige Bewegung
    public double alpha = 0.1; // Lernrate (0..1)
    public double gamma = 0.90; // Bewertungsfaktor (0..1)
    public double q[][]; // Q-Learning-Array
    private static final int POSSIBLE_ACTIONS = 4; // 2^4 = 16 Mögliche Zustände

    public QLearningAgentMovement() {
        System.out.println("Initialisiere QTable");
        this.q = new double[(int) Math.pow(2, POSSIBLE_ACTIONS)][POSSIBLE_ACTIONS];
        // initalize q
        for (int i = 0; i < this.q.length; i++) {
            for (int j = 0; j < this.q[i].length; j++) {
                // values between 0 and 0.1 without 0.1
                this.q[i][j] = 0;//Math.random() / 10;
            }
        }
        printQTable();
    }

    public QLearningAgentMovement(double[][] array) {
        this.q = array;
        printQTable();
    }

    public void printQTable() {
        for (int i = 0; i < (int) Math.pow(2, POSSIBLE_ACTIONS); i++) {
            for (int j = 0; j < POSSIBLE_ACTIONS; j++) {
                System.out.print(this.q[i][j] + " \t");
            }
            System.out.println();
        }
    }

    /**
     * Lernt durch die übergebenen Zustände, ob die Aktion erfolgreich war und
     * speichert die Werte in das q-array.
     *
     * @param actualState:      Aktueller Zustand
     * @param nextState: Nächster Zustand
     * @param action:      Aktion
     * @param reward:      Belohnung
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
     * Wählt die Aktion mit der besten Rate aus
     *
     * @param s: Zustand s
     * @return: Gibt die Aktion als int zurück.
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
     * Wählt eine zufällige Aktion anhand des Zustands aus
     *
     * @param s: Zustand
     * @return: Gibt die Aktion als int zurück.
     */
    public int chooseAction(int s) {
        int a = 0;
        int rand = (int) (Math.random() * 101);
        if (rand < this.epsilon) {
            // + 1 to have the last inclusive
            // a = ThreadLocalRandom.current().nextInt(0, POSSIBLE_ACTIONS);
            a = (int) (Math.random() * 4);
//            System.out.print("Zufälliger Zug: " + a + " ");
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

//        String table = "| ";
//        for(double i : this.q[s]){
//            table += i + " | ";
//        }
//        SimulationRobot.movementHistorie += table;
//        switch (a) {
//            case SimulationRobot.DRIVE_FORWARD:
////                System.out.print(table + "\t -> " + a + " Forward");
//                SimulationRobot.movementHistorie += "\t -> " + a + " Forward";
//                break;
//            case SimulationRobot.DRIVE_BACKWARD:
////                System.out.print(table + "\t -> " + a + " Backward");
//                SimulationRobot.movementHistorie += "\t -> " + a + " Backward";
//                break;
//            case SimulationRobot.DRIVE_ROTATE_LEFT:
////                System.out.print(table + "\t -> " + a + " RLeft");
//                SimulationRobot.movementHistorie += "\t -> " + a + " RLeft";
//                break;
//            case SimulationRobot.DRIVE_ROTATE_RIGHT:
////                System.out.print(table + "\t -> " + a + " RRight");
//                SimulationRobot.movementHistorie += "\t -> " + a + " RRight";
//                break;
//            default:
////                System.out.print("Ungültige Aktion");
//        }

        return a;
    }
}
