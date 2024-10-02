import java.util.Scanner;
/**
 * This class' main method runs the simulation using text to display information and accept input
 *
 * @author Jerry Cui
 * @version %I%, %G%
 * @since 1.0
 */
public class TextSimulation {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // Temporary variables
        int targetRow;
        int targetColumn;
        int targetValue;

        // Ask input
        System.out.println("Enter number of rows: ");
        int numRows = input.nextInt();
        System.out.println("Enter number of columns: ");
        int numColumns = input.nextInt();

        int[][] terrainGrid = new int[numRows][numColumns];

        System.out.println("How many obstacles do you want to place? ");
        int numObstacles = input.nextInt();  // don't worry, Java allows arrays of size 0

        for (int i = 0; i < numObstacles; i++) {
            System.out.println("Obstacle #" + i + " row number: ");
            targetRow = input.nextInt();
            System.out.println("Obstacle #" + i + " column number: ");
            targetColumn = input.nextInt();

            terrainGrid[targetRow][targetColumn] = AntColonyEngine.OBSTACLE;
        }

        System.out.println("Ant colony row number: ");
        int colonyRow = input.nextInt();
        System.out.println("Ant colony column number: ");
        int colonyColumn = input.nextInt();
        terrainGrid[colonyRow][colonyColumn] = AntColonyEngine.COLONY;

        System.out.println("How many pieces of food do you want to place?");
        int numFood = input.nextInt();
        for (int i = 0; i < numFood; i++) {
            System.out.println("Food item #" + i + " row number: ");
            targetRow = input.nextInt();
            System.out.println("Food item #" + i + " column number: ");
            targetColumn = input.nextInt();
            System.out.println("Amount of food at row " + targetRow + ", column " + targetColumn + ": ");
            targetValue = input.nextInt();

            terrainGrid[targetRow][targetColumn] = targetValue;
        }

        System.out.println("How many ants do you want to place?");
        int numAnts = input.nextInt();
        int[] antRow = new int[numAnts];
        int[] antColumn = new int[numAnts];
        for (int i = 0; i < numAnts; i++) {
            System.out.println("Ant #" + i + " row number: ");
            antRow[i] = input.nextInt();
            System.out.println("Ant #" + i + " column number: ");
            antColumn[i] = input.nextInt();
        }

        AntColonyEngine engine = new AntColonyEngine(numRows, numColumns, colonyRow, colonyColumn, antRow, antColumn, terrainGrid);

        input.nextLine();  // clear next line character

        // Game loop
        boolean exitGame = false;
        String command;
        while (true) {
            // Ask for user input (user can enter multiple commands per turn)
            while (true) {

                engine.printInfo();  // this will show input options and explain things to the user
                command = input.nextLine();

                if (command.equals("QUIT")) {
                    exitGame = true;
                    break;
                } else if (command.equals("HELP")) {
                    engine.printHelp();
                } else if (command.equals("PHEROMONE")) {
                    System.out.println("Row of pheromone value to change: ");
                    targetRow = input.nextInt();
                    System.out.println("Column of pheromone value to change: ");
                    targetColumn = input.nextInt();
                    System.out.println("New pheromone value: ");
                    targetValue = input.nextInt();

                    engine.setPheromoneGrid(targetRow, targetColumn, targetValue);

                    input.nextLine();  // skip new line character
                } else if (command.equals("PHEROMONE STRENGTH")) {  // change pheromone default strength
                    System.out.println("New pheromone strength value: ");
                    targetValue = input.nextInt();

                    engine.setPheromoneStrength(targetValue);

                    input.nextLine();
                } else if (command.equals("PHEROMONE DECAY")) {  // change pheromone decay value
                    System.out.println("New pheromone decay value: ");
                    targetValue = input.nextInt();

                    engine.setPheromoneDecay(targetValue);

                    input.nextLine();
                } else if (command.equals("COLONY")) {  // change colony location
                    engine.setTerrainGrid(engine.getColonyRow(), engine.getColonyColumn(), AntColonyEngine.EMPTY); // delete old location

                    System.out.println("New row of colony: ");
                    colonyRow = input.nextInt();
                    System.out.println("New column of colony: ");
                    colonyColumn = input.nextInt();

                    engine.setTerrainGrid(colonyRow, colonyColumn, AntColonyEngine.COLONY);  // new colony location

                    input.nextLine();
                } else if (command.equals("OBSTACLE")) {  // delete/add obstacle
                    System.out.println("If you enter an empty tile, it turns into an obstacle, if you enter an obstacle, it turns into an empty tile");
                    System.out.println("Row of tile: ");
                    targetRow = input.nextInt();
                    System.out.println("Column of tile: ");
                    targetColumn = input.nextInt();

                    if (engine.getTerrainGrid(targetRow, targetColumn) != AntColonyEngine.COLONY) {  // can't delete the colony
                        if (engine.getTerrainGrid(targetRow, targetColumn) == AntColonyEngine.OBSTACLE) {
                            engine.setTerrainGrid(targetRow, targetColumn, AntColonyEngine.EMPTY);
                        } else {
                            engine.setTerrainGrid(targetRow, targetColumn, AntColonyEngine.OBSTACLE);
                        }
                    } else {
                        System.out.println("Error: the specified tile is the colony location");
                    }

                    input.nextLine();
                } else if (command.equals("FOOD")) {  // edit food value
                    System.out.println("Note: Cannot add food to colony/obstacle tiles");
                    System.out.println("Row of tile: ");
                    targetRow = input.nextInt();
                    System.out.println("Column of tile: ");
                    targetColumn = input.nextInt();

                    if (engine.getTerrainGrid(targetRow, targetColumn) >= 0) {  // checks for a valid tile
                        System.out.println("Amount of food to add: ");
                        targetValue = input.nextInt();
                        engine.setTerrainGrid(targetRow, targetColumn, targetValue);
                    } else {
                        System.out.println("Error: cannot add food to this tile");
                    }

                    input.nextLine();
                } else if (command.equals("EDIT ANT")) {  // change if an ant has food or not
                    // Output ant data to help user make a decision
                    for (int i = 0; i < numAnts; i++) {
                        engine.printAnt(i);
                    }
                    System.out.println("Enter index of ant to flip (if it has food, it will no longer have food, if it has food, it will lose it.");
                    targetValue = input.nextInt();

                    // Reverse boolean
                    if (engine.getAntFoundFood(targetValue)) {
                        engine.setAntFoundFood(targetValue, false);
                    } else {
                        engine.setAntFoundFood(targetValue, true);
                    }

                    input.nextLine();
                } else if (command.equals("ADD ANT")) {
                    System.out.println("Row of new ant: ");
                    targetRow = input.nextInt();
                    System.out.println("Column of new ant: ");
                    targetColumn = input.nextInt();

                    if (engine.getTerrainGrid(targetRow, targetColumn) == AntColonyEngine.OBSTACLE) {  // obstacle
                        System.out.println("Error: Cannot place ant on an obstacle");
                    } else {
                        // Add the ant
                        engine.addAnt(targetRow, targetColumn, false);  // default is ant has not found food yet

                        System.out.println("Successfully added ant");
                    }

                    input.nextLine();
                } else if (command.equals("DELETE ANT")) {
                    // Output ant data to help user make a decision
                    for (int i = 0; i < engine.getNumAnts(); i++) {
                        engine.printAnt(i);
                    }

                    System.out.println("Enter index of ant to delete: ");
                    targetValue = input.nextInt();

                    engine.deleteAnt(targetValue);

                    System.out.println("Successfully deleted ant");

                    input.nextLine();
                } else {
                    break;
                }
            }

            if (exitGame) {
                break;
            }

            // update simulation
            engine.update();

        }

        // Close scanner
        input.close();
    }


}
