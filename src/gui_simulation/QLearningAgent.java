package gui_simulation;

import java.util.concurrent.ThreadLocalRandom;

public class QLearningAgent {
    private double epsilon = 0.2;
    private double alpha = 0.1; // Lernrate (0..1)
    private double gamma = 0.9; // Bewertungsfaktor (0..1)
    private double q[][]; // Q-Learning-Array
    private static final int POSSIBLE_ACTIONS = 4;
    /*
     * possible actions: DRIVE_FORWARD = 0, DRIVE_LEFT = 1,
     * DRIVE_RIGHT = 2, DRIVE_BACKWARD = 3
     */
    private static final int BARRIER_LOCATIONS = 8;
    // is the robot bumped or not? 1 state for the barrier bumped and one state for the location not bumped
    // dhort of no barrier
    private static final int BUMPED = 10;
    /*
     * 8 barrier states: no barrier, front, left, right, front+left, front+right, right+left, front+right+left
     */
    private static final int CAN_ROTATE = 3; //left, right, right & Left


    public QLearningAgent() {
        System.out.println("Initialisiere QTable");
        this.q = new double[BARRIER_LOCATIONS+BUMPED+CAN_ROTATE][POSSIBLE_ACTIONS];
        // initalize q
        for(int i = 0; i < this.q.length; i++) {
            for(int j=0; j < this.q[i].length; j++) {
                // values between 0 and 0.1 without 0.1
                this.q[i][j] = Math.random() / 10;
            }
        }
        printQTable();
    }

    public QLearningAgent(double [][] array) {
        this.q = array;
        printQTable();
    }

    public void printQTable() {
        for (int i = 0; i < BARRIER_LOCATIONS+BUMPED+CAN_ROTATE; i++) {
            for (int j=0; j < POSSIBLE_ACTIONS; j++) {
                System.out.print(this.q[i][j] + ",");
            }
            System.out.println();
        }
    }

    /**
     * Lernt durch die übergebenen Zustände, ob die Aktion erfolgreich war und
     * speichert die Werte in das q-array.
     * @param s: Aktueller Zustand
     * @param s_next: Nächster Zustand
     * @param a: Aktion
     * @param r: Belohnung
     */
    public void learn(int s, int s_next, int a, double r) {
        this.q[s][a] += this.alpha * (r + this.gamma * (this.q[s_next][actionWithBestRating(s_next)]) - q[s][a]);
    }

    /**
     * Wählt die Aktion mit der besten Rate aus
     * @param s: Zustand s
     * @return: Gibt die Aktion als int zurück.
     */
    public int actionWithBestRating(int s) {
        double max = 0;
        int index = 0;
        for (int i = 0; i < POSSIBLE_ACTIONS; i++) {
            if (this.q[s][i] > max) {
                max = this.q[s][i];
                index = i;
            }
        }
        return index;
    }

    /**
     * Wählt eine zufällige Aktion anhand des Zustands aus
     * @param s: Zustand
     * @return: Gibt die Aktion als int zurück.
     */
    public int chooseAction(int s) {
        int a = 0;
        if (Math.random() < this.epsilon) {
            // + 1 to have the last inclusive
            a = ThreadLocalRandom.current().nextInt(0, POSSIBLE_ACTIONS);
        } else {
            a = actionWithBestRating(s);
        }
        return a;
    }
}
