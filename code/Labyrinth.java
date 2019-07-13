import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

// TODO: Roboterposition von Labyrinth trennen
// TODO: Ausgabe überladene Funktion -> Ohne Parameter nur Lab, mit Param -> Roboter (Param = Roboposition Array)
// TODO: Lab kennt Pos von Roboter
// TODO: Lab braucht nur Methode isBumped und getDistanze (entspr. findBarrier von LegoRoboter)
public class Labyrinth {
    private String mazeFilePath;
    private String maze;
    private int mazeX;
    private int mazeY;
    private int[] numericMaze;

    // Stringwerte von Labyrinth
    private final String stringWallValue = "#";
    private final String stringVoidValue = " ";
    private final String stringRoboBodyValue = "B";
    private final String stringRoboHeadValue = "H";

    // Arraywerte für Simulation
    private final int arrayWallValue = 0;
    private final int arrayVoidValue = 1;
    private final int arrayRoboBodyValue = 2;
    private final int arrayRoboHeadValue = 3;

    private final int arrayUnknownSign = -1;

    // Robotermaße (Originalmaße im cm: 20x15 -> auf 4x3 reduziert)
    private final int robotXDimension = 4;
    private final int robotYDimension = 3;
    private final int robotHeadDimension = 1; // Kopf-Schulterproportion passt nicht ganz

    private final int robotSmallerDirection = this.robotXDimension < this.robotYDimension ? this.robotXDimension : this.robotYDimension;
    private final int robotBiggerDirection = this.robotXDimension > this.robotYDimension ? this.robotXDimension : this.robotYDimension;

    public Labyrinth(String mazeFile) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(mazeFile));
            this.mazeFilePath = mazeFile;

            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }
            br.close();

            this.maze = sb.toString();

            String[] mazeStringParts = this.maze.split(System.lineSeparator());
            this.mazeX = mazeStringParts[0].length();
            this.mazeY = mazeStringParts.length;
        } catch (FileNotFoundException e) {
            System.err.println("Datei: " + mazeFile + " nicht gefunden!");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mazeToArray();
    }

    public void mazeToArray() {
        String[] mazeStringParts = this.maze.split(System.lineSeparator());
        int[] mazeArray = new int[this.mazeX * this.mazeY];

        for (int i = 0; i < mazeStringParts.length; i++) {
            for (int j = 0; j < mazeStringParts[i].length(); j++) {
                if (("" + mazeStringParts[i].charAt(j)).equals(this.stringWallValue)) {
                    mazeArray[i * this.mazeY + j] = this.arrayWallValue;
                } else if (("" + mazeStringParts[i].charAt(j)).equals(this.stringVoidValue)) {
                    mazeArray[i * this.mazeY + j] = this.arrayVoidValue;
                } else if (("" + mazeStringParts[i].charAt(j)).equals(this.stringRoboBodyValue)) {
                    mazeArray[i * this.mazeY + j] = this.arrayRoboBodyValue;
                } else if (("" + mazeStringParts[i].charAt(j)).equals(this.stringRoboHeadValue)) {
                    mazeArray[i * this.mazeY + j] = this.arrayRoboHeadValue;
                } else {
                    throw new IllegalArgumentException("Ungültiges Zeichen <" + mazeStringParts[i].charAt(j) + ">");
                }
            }
        }

        this.numericMaze = mazeArray;
    }

    public void printArrayMaze() {
        for (int i = 0; i < numericMaze.length; i++) {
            System.out.print(numericMaze[i]);
            if (i > 0 && (i + 1) % this.mazeX == 0) {
                System.out.println("");
            }
        }
    }

    // TODO Deprecated
    public void updateStringMaze() {
        String newStringMaze = "";
        for (int i = 0; i < this.numericMaze.length; i++) {
            if (this.numericMaze[i] == this.arrayWallValue) {
                newStringMaze += this.stringWallValue;
            } else if (this.numericMaze[i] == this.arrayVoidValue) {
                newStringMaze += this.stringVoidValue;
            } else if (this.numericMaze[i] == this.arrayRoboBodyValue) {
                newStringMaze += this.stringRoboBodyValue;
            } else if (this.numericMaze[i] == this.arrayRoboHeadValue) {
                newStringMaze += this.stringRoboHeadValue;
            } else {
                throw new IllegalArgumentException("Kein entsprechenes Zeichen für: <" + this.numericMaze[i] + "> verfügbar!");
            }

            if (i > 0 && (i + 1) % this.mazeX == 0) {
                newStringMaze += System.lineSeparator();
            }
        }

        this.maze = newStringMaze;
    }

    // TODO View vom Model trennen
    public int[][] getRobotStartingPosition(int... position) {
        int puttingPosition = position.length > 0 ? position[0] : 0;
        // 0 = Norden, weiter im Uhrzeigersinn
        int headDirection = position.length > 1 ? position[1] : 0;

        List<Integer> possiblePositions = new ArrayList<>();
        for (int i = 0; i < this.numericMaze.length; i++) {
            if (this.numericMaze[i] == this.arrayVoidValue) {
                possiblePositions.add(i);
            }
        }

        // Bestimmen der Position und Ausrichtung
        if (position.length == 0) {
            puttingPosition = possiblePositions.get(new Random().nextInt(possiblePositions.size()));
            headDirection = new Random().nextInt(4);
        } else if (position.length > 0) {
            Scanner sc = new Scanner(System.in);

            if (!possiblePositions.contains(puttingPosition)) {
                String input = "";
                while (!possiblePositions.contains(puttingPosition)) {
                    robotPositionMatrix();
                    System.out.println(puttingPosition + " keine gültige Position, wähle eine gültige.");
                    if (sc.hasNextLine()) {
                        input = sc.nextLine();
                    }
                    try {
                        puttingPosition = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        System.out.println("Ungültige Eingabe");
                    }
                }
            }

            if (position.length > 1) {
                if (headDirection < 0 || headDirection > 3) {
                    String input = "";
                    System.out.println("Wähle eine geeignete Ausrichtung");
                    System.out.println("0 = Norden");
                    System.out.println("1 = Osten");
                    System.out.println("2 = Süden");
                    System.out.println("3 = Westen");

                    int i = 0;
                    while (headDirection < 0 || headDirection > 3) {
                        if (++i > 1) {
                            System.out.println("Ungültige Ausrichtung");
                        }
                        if (sc.hasNextLine()) {
                            input = sc.nextLine();
                        }
                        try {
                            headDirection = Integer.parseInt(input);
                        } catch (NumberFormatException e) {
                            System.out.println("Ungültige Eingabe");
                        }
                    }
                }
            }
            sc.close();
        }

        System.out.println("Position: " + puttingPosition);
        System.out.print("Ausrichtung: ");
        switch (headDirection) {
            case 0:
                System.out.print("Nord");
                break;
            case 1:
                System.out.print("Ost");
                break;
            case 2:
                System.out.print("Süd");
                break;
            case 3:
                System.out.println("West");
                break;
            default:
                throw new IllegalArgumentException("Fehler bei Ausrichtung des Roboters");
        }
        System.out.println("");

        // Roboter in Labyrinth setzen
        List<Integer> possiblePositionsAroundPuttingPosition = new ArrayList<>();
        possiblePositionsAroundPuttingPosition.add(puttingPosition);
        boolean wall = false;

        // Nord und Süd (Horizontal ausrichten)
        if (headDirection == 0 || headDirection == 2) {
            // Prüfe linke Felder
            for (int x = 1; !wall && x < this.robotSmallerDirection; x++) {
                if (this.numericMaze[puttingPosition - x] == this.arrayVoidValue) {
                    possiblePositionsAroundPuttingPosition.add(puttingPosition - x);
                } else {
                    wall = true;
                }
            }
            // Prüfe rechte Felder
            if (possiblePositionsAroundPuttingPosition.size() < this.robotSmallerDirection) {
                wall = false;
                for (int x = 1; !wall && x < this.robotSmallerDirection && possiblePositionsAroundPuttingPosition.size() < this.robotSmallerDirection; x++) {
                    if (this.numericMaze[puttingPosition + x] == this.arrayVoidValue) {
                        possiblePositionsAroundPuttingPosition.add(puttingPosition + x);
                    } else {
                        wall = true;
                    }
                }
            }
        } else { // Ost und West (Vertikal aufstellen)
            // Prüfe oben
            for (int x = 1; !wall && x < this.robotSmallerDirection; x++) {
                if (this.numericMaze[puttingPosition - (x * this.mazeX)] == this.arrayVoidValue) {
                    possiblePositionsAroundPuttingPosition.add(puttingPosition - (x * this.mazeX));
                } else {
                    wall = true;
                }
            }

            // Prüfe unten
            if (possiblePositionsAroundPuttingPosition.size() < this.robotSmallerDirection) {
                wall = false;
                for (int x = 1; !wall && x < this.robotSmallerDirection && possiblePositionsAroundPuttingPosition.size() < this.robotSmallerDirection; x++) {
                    if (this.numericMaze[puttingPosition + (x * this.mazeX)] == this.arrayVoidValue) {
                        possiblePositionsAroundPuttingPosition.add(puttingPosition + (x * this.mazeX));
                    } else {
                        wall = true;
                    }
                }
            }
        }

        if (possiblePositionsAroundPuttingPosition.size() < this.robotSmallerDirection) {
            throw new IllegalArgumentException("Setzen des Roboters auf diese Position nicht möglich");
        }

        // Bestimme weitere Felder
        // Nord und Süd (Vertikal suchen)
        if (headDirection == 0 || headDirection == 2) {
            wall = false;
            int i = 0;
            // Nach oben
            for (int y = 1; !wall && y < this.robotBiggerDirection; y++) {
                if (this.numericMaze[possiblePositionsAroundPuttingPosition.get(0) - (y * this.mazeX)] == this.arrayVoidValue) {
                    i++;
                    possiblePositionsAroundPuttingPosition.add(possiblePositionsAroundPuttingPosition.get(0) - (y * this.mazeX));
                    for (int x = 1; !wall && x < this.robotSmallerDirection; x++) {
                        if (this.numericMaze[possiblePositionsAroundPuttingPosition.get(x) - (y * this.mazeX)] == this.arrayVoidValue) {
                            i++;
                            // Mögliche Reihe gefunden
                            if (i == 3) {
                                i = 0;
                            }
                            possiblePositionsAroundPuttingPosition.add(possiblePositionsAroundPuttingPosition.get(x) - (y * this.mazeX));
                        } else {
                            wall = true;
                            while (i > 0) {
                                possiblePositionsAroundPuttingPosition.remove(possiblePositionsAroundPuttingPosition.size() - 1);
                                i--;
                            }
                        }
                    }
                } else {
                    wall = true;
                    while (i > 0) {
                        possiblePositionsAroundPuttingPosition.remove(possiblePositionsAroundPuttingPosition.size() - 1);
                        i--;
                    }
                }
            }

            // Nach unten
            if (possiblePositionsAroundPuttingPosition.size() < this.robotXDimension * this.robotYDimension) {
                wall = false;
                i = 0;

                for (int y = 1; !wall && y < this.robotBiggerDirection && possiblePositionsAroundPuttingPosition.size() < this.robotXDimension * this.robotYDimension; y++) {
                    if (this.numericMaze[possiblePositionsAroundPuttingPosition.get(0) + (y * this.mazeX)] == this.arrayVoidValue) {
                        i++;
                        possiblePositionsAroundPuttingPosition.add(possiblePositionsAroundPuttingPosition.get(0) + (y * this.mazeX));
                        for (int x = 1; !wall && x < this.robotSmallerDirection; x++) {
                            if (this.numericMaze[possiblePositionsAroundPuttingPosition.get(x) + (y * this.mazeX)] == this.arrayVoidValue) {
                                i++;
                                possiblePositionsAroundPuttingPosition.add(possiblePositionsAroundPuttingPosition.get(x) + (y * this.mazeX));
                            } else {
                                wall = true;
                                while (i > 0) {
                                    possiblePositionsAroundPuttingPosition.remove(possiblePositionsAroundPuttingPosition.size() - 1);
                                    i--;
                                }
                            }
                        }
                    } else {
                        wall = true;
                        while (i > 0) {
                            possiblePositionsAroundPuttingPosition.remove(possiblePositionsAroundPuttingPosition.size() - 1);
                            i--;
                        }
                    }
                }
            }

            if (possiblePositionsAroundPuttingPosition.size() < this.robotXDimension * this.robotYDimension) {
                throw new IllegalArgumentException("Roboter konnte nicht gesetzt werden");
            }

        } else { // Ost und West (Horizontal suchen)
            wall = false;
            int i = 0;

            // Links nach freien Plätzen suchen
            for (int x = 1; !wall && x < this.robotBiggerDirection; x++) {
                if (this.numericMaze[possiblePositionsAroundPuttingPosition.get(0) - x] == this.arrayVoidValue) {
                    i++;
                    possiblePositionsAroundPuttingPosition.add(possiblePositionsAroundPuttingPosition.get(0) - x);
                    for (int y = 1; !wall && y < this.robotSmallerDirection; y++) {
                        if (this.numericMaze[possiblePositionsAroundPuttingPosition.get(y) - x] == this.arrayVoidValue) {
                            i++;
                            possiblePositionsAroundPuttingPosition.add(possiblePositionsAroundPuttingPosition.get(y) - x);
                            // Passende 3er Gruppe gefunden
                            if (i == 3) {
                                i = 0;
                            }
                        } else {
                            wall = true;
                            while (i > 0) {
                                possiblePositionsAroundPuttingPosition.remove(possiblePositionsAroundPuttingPosition.size() - 1);
                                i--;
                            }
                        }
                    }
                } else {
                    wall = true;
                    while (i > 0) {
                        possiblePositionsAroundPuttingPosition.remove(possiblePositionsAroundPuttingPosition.size() - 1);
                        i--;
                    }
                }
            }

            if (possiblePositionsAroundPuttingPosition.size() < this.robotXDimension * this.robotYDimension) {
                // Rechts nach freien Plätzen suchen
                wall = false;
                i = 0;
                for (int x = 1; !wall && x < this.robotBiggerDirection && possiblePositionsAroundPuttingPosition.size() < this.robotXDimension * this.robotYDimension; x++) {
                    if (this.numericMaze[possiblePositionsAroundPuttingPosition.get(0) + x] == this.arrayVoidValue) {
                        i++;
                        possiblePositionsAroundPuttingPosition.add(possiblePositionsAroundPuttingPosition.get(0) + x);
                        for (int y = 1; !wall && y < this.robotSmallerDirection; y++) {
                            if (this.numericMaze[possiblePositionsAroundPuttingPosition.get(y) + x] == this.arrayVoidValue) {
                                i++;
                                possiblePositionsAroundPuttingPosition.add(possiblePositionsAroundPuttingPosition.get(y) + x);
                                // Mögliche 3er Gruppe gefunden
                                if (i == 3) {
                                    i = 0;
                                }
                            } else {
                                wall = true;
                                while (i > 0) {
                                    possiblePositionsAroundPuttingPosition.remove(possiblePositionsAroundPuttingPosition.size() - 1);
                                    i--;
                                }
                            }
                        }
                    } else {
                        wall = true;
                        while (i > 0) {
                            possiblePositionsAroundPuttingPosition.remove(possiblePositionsAroundPuttingPosition.size() - 1);
                            i--;
                        }
                    }
                }
            }
        }

        // Setze Roboter
        // for (int i : possiblePositionsAroundPuttingPosition) {
        // this.numericMaze[i] = this.arrayRoboBodyValue;
        // }

        // Sortieren um leichter Headposition zu finden
        Collections.sort(possiblePositionsAroundPuttingPosition);
        int headPosition = 0;
        switch (headDirection) {
            case 0:
                headPosition = possiblePositionsAroundPuttingPosition.get(1);
                break;
            case 1:
                headPosition = possiblePositionsAroundPuttingPosition.get(this.robotXDimension * this.robotYDimension - this.robotXDimension - 1);
                break;
            case 2:
                headPosition = possiblePositionsAroundPuttingPosition.get(possiblePositionsAroundPuttingPosition.size() - 2);
                break;
            case 3:
                headPosition = possiblePositionsAroundPuttingPosition.get(this.robotXDimension * this.robotYDimension - this.robotXDimension - this.robotYDimension - 1);
                break;
        }

        // Setze Headposition
        // this.numericMaze[headPosition] = this.arrayRoboHeadValue;
        int[][] ret = new int[2][possiblePositionsAroundPuttingPosition.size()];
        ret[0][0] = headPosition;

        int index = 0;
        for (int i : possiblePositionsAroundPuttingPosition) {
            ret[1][index] = i;
            index++;
        }

        return ret;
    }

    public void robotPositionMatrix() {
        for (int i = 0; i < this.numericMaze.length; i++) {
            if (this.numericMaze[i] == this.arrayWallValue) {
                for (int j = 0; j < ("" + this.numericMaze.length).length() + 1; j++) {
                    System.out.print(this.stringWallValue);
                }
            } else {
                System.out.print(i);
                if (("" + i).length() < ("" + this.numericMaze.length).length()) {
                    for (int j = 0; j < ("" + this.numericMaze.length).length() - ("" + i).length(); j++) {
                        System.out.print(" ");
                    }
                }
                System.out.print(" ");
            }

            if (i > 0 && (i + 1) % this.mazeX == 0) {
                System.out.println();
            }
        }
    }

    public void printStringMaze() {
        updateStringMaze();
        System.out.println(this.maze);
    }

    public void printStringMaze(int headPosition, int[] position) {
        int[] numericMazeToPrint = this.numericMaze;

        for (int roboBodyPosition : position) {
            numericMazeToPrint[roboBodyPosition] = this.arrayRoboBodyValue;
        }
        numericMazeToPrint[headPosition] = this.arrayRoboHeadValue;

        String stringMaze = "";
        for (int i = 0; i < numericMazeToPrint.length; i++) {
            if (numericMazeToPrint[i] == this.arrayRoboBodyValue) {
                stringMaze += this.stringRoboBodyValue;
            } else if (numericMazeToPrint[i] == this.arrayRoboHeadValue) {
                stringMaze += this.stringRoboHeadValue;
            } else if (numericMazeToPrint[i] == this.arrayVoidValue) {
                stringMaze += this.stringVoidValue;
            } else if (numericMazeToPrint[i] == this.arrayWallValue) {
                stringMaze += this.stringWallValue;
            } else {
                throw new IllegalArgumentException("Kein entsprechenes Zeichen für: <" + this.numericMaze[i] + "> verfügbar!");
            }

            if (i > 0 && (i + 1) % this.mazeX == 0) {
                stringMaze += System.lineSeparator();
            }
        }

        System.out.println(stringMaze);
    }

    public static void main(String[] args) {
        Labyrinth l = new Labyrinth("./src/simulation/maze_Target-Pos5");
        int[][] position = l.getRobotStartingPosition();
        SimRoboter r = new SimRoboter(position[0][0], position[1]);
        System.out.println("Head: " + r.getHeadPosition());
        System.out.print("Body: ");
        for (int i : position[1]) {
            System.out.print(i + " ");
        }
        System.out.println("");

        l.robotPositionMatrix();

        l.printStringMaze(r.getHeadPosition(), r.getPosition());

        r.left();
        l.printStringMaze(r.getHeadPosition(), r.getPosition());
    }

}