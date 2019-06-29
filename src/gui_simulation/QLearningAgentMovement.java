package gui_simulation;

import java.util.concurrent.ThreadLocalRandom;

public class QLearningAgentMovement {

    public double epsilon = 1; // Zufällige Bewegung
    public double alpha = 0.1; // Lernrate (0..1)
    public double gamma = 0.90; // Bewertungsfaktor (0..1)
    private double q[][]; // Q-Learning-Array
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
                System.out.print(this.q[i][j] + ",");
            }
            System.out.println();
        }
    }

    /**
     * Lernt durch die übergebenen Zustände, ob die Aktion erfolgreich war und
     * speichert die Werte in das q-array.
     *
     * @param s:      Aktueller Zustand
     * @param s_next: Nächster Zustand
     * @param a:      Aktion
     * @param r:      Belohnung
     */
    public void learn(int s, int s_next, int a, double r) {
        this.q[s][a] += this.alpha * (r + this.gamma * (this.q[s_next][actionWithBestRating(s_next)]) - q[s][a]);
    }

    /**
     * Wählt die Aktion mit der besten Rate aus
     *
     * @param s: Zustand s
     * @return: Gibt die Aktion als int zurück.
     */
    public int actionWithBestRating(int s) {
        double max = Integer.MIN_VALUE;
        int index = 0;

        for (int i = 0; i < POSSIBLE_ACTIONS; i++) {
            if (this.q[s][i] >= max) {
                if(Math.random()>=0.5) {
                    max = this.q[s][i];
                    index = i;
                }
            }
        }
        return index;
    }

    /**
     * Wählt eine zufällige Aktion anhand des Zustands aus
     *
     * @param s: Zustand
     * @return: Gibt die Aktion als int zurück.
     */
    public int chooseAction(int s) {
        int a = 0;
        double rand = Math.random();
        if (rand < this.epsilon) {
            // + 1 to have the last inclusive
            a = ThreadLocalRandom.current().nextInt(0, POSSIBLE_ACTIONS);
        } else {
            //TODO wenn er die auswahlt zwischen verschiedenen Aktionen hat random zwischen diesen auswählen
            a = actionWithBestRating(s);

            //String table = "";
            /*for(double i : this.q[s]){
                table += i + " | ";
            }*/
            /*switch (a) {
                case SimulationRobot.DRIVE_FORWARD:
                    System.out.print("T:" + table + " -> Forward ");
                    break;
                case SimulationRobot.DRIVE_BACKWARD:
                    System.out.print("T:" + table + " -> Backward");
                    break;
                case SimulationRobot.DRIVE_ROTATE_LEFT:
                    System.out.print("T:" + table + " -> RLeft");
                    break;
                case SimulationRobot.DRIVE_ROTATE_RIGHT:
                    System.out.print("T:" + table + " -> RRight");
                    break;
                default:
                    System.out.print("Ungültige Aktion");
            }*/
        }
        return a;
    }
}
