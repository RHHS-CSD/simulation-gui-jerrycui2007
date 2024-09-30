// for BFS algorithm
import java.util.LinkedList;
import java.util.Queue;

/**
 * Main engine class which controls the game
 * Running this program's <code></code>main method starts the simulation in text mode
 *
 * @author Jerry Cui
 * @version %I%, %G%
 * @since 1.0
 */
public class AntColonyEngine {
    // Initialize variables for the engine
    // Constant integers that represent something
    final public int COLONY = -2;    // -2 represents colony
    final public int OBSTACLE = -1;  // -1 represents an obstacle

    // Directions
    final public int UP = 0;
    final public int RIGHT = 1;
    final public int DOWN = 2;
    final public int LEFT = 3;

    final public int[][] DIRECTIONS = {
            {-1, 0},  // up
            {0, 1},   // right
            {1, 0},   // down
            {0, -1}   // left
    };
    // These values are chosen because when added to a {row, column}, the sum is the tile in the chosen direction

    private int turn;  // current turn of the simulation

    final private int numRows;     // final because size of simulation won't be allowed to change
    final private int numColumns;

    private int colonyRow;
    private int colonyColumn;

    private int[] antRow;            // antRow[i] contains the row of the ith ant
    private int[] antColumn;         // same thing
    private boolean[] antFoundFood;  // contains whether each ant has currently found food

    private int[][] pheromoneGrid;  // contains the strength of ant pheromones on each tile

    private int[][] terrainGrid;
    /* If the value on terrainGrid[row][column]
       - == -2: the ant colony is on this tile
       - == -1: an obstacle is on this tile
       - == 0:  tile is empty
       - > 0:   there is food on this tile, and the amount of food is equal to the number
    */

    private int[][] antGrid;  // contains the number of ants on each tile (there can be more than one tile)

    // Values for the simulation (not final because user can edit them)
    int pheromoneStrength = 1000;  // default value of pheromones left behind
    int pheromoneDecay = 1;        // how much pheromones decay per turn
    int minimumPheromone  = 1;     // minimum pheromone level on any tile

    /**
     * Constructor to initialize another instance of the simulation
     * Simulation always starts from the very beginning
     *
     * @param numRows      number of rows in the simulation
     * @param numColumns   number of columns in the simulation
     * @param antRow       <code>antRow[i]</code> contains the row of the ith ant
     * @param antColumn    same thing
     * @param terrainGrid  contains the terrain of each tile
     */
    AntColonyEngine(int numRows, int numColumns, int colonyRow, int colonyColumn, int[] antRow, int[] antColumn, int[][] terrainGrid) {
        this.turn = 0;

        this.numRows = numRows;
        this.numColumns = numColumns;

        this.colonyRow = colonyRow;
        this.colonyColumn = colonyColumn;

        this.antRow = antRow;
        this.antColumn = antColumn;
        this.antFoundFood = new boolean[antRow.length];  // default is false, because none of the ants have found food yet

        this.pheromoneGrid = new int[numRows][numColumns];
        // Fill pheromone grid with the minimum pheromone level
        for (int row = 0; row < numRows; row++) {
            for (int column = 0; column < numColumns; column++) {
                this.pheromoneGrid[row][column] = this.minimumPheromone;
            }
        }

        this.terrainGrid = terrainGrid;

        this.antGrid = new int[numRows][numColumns];
        // Loop through all ants and "place" them on the grid
        for (int i = 0; i < this.antRow.length; i++) {
            this.antGrid[antRow[i]][antColumn[i]]++;
        }
    }

    /**
     * Checks if a given tile is valid for an ant to be on
     * The tile must be within the boundaries of the simulation, and cannot have an obstacle on it
     *
     * @param row    row of tile to check
     * @param column column of tile to check
     * @return       whether the tile is valid
     */
    public boolean isValidTileForAnt(int row, int column) {
        if (row < 0) {
            return false;
        } else if (row >= numRows) {
            return false;
        }

        if (column < 0) {
            return false;
        } else if (column >= numColumns) {
            return false;
        }

        if (terrainGrid[row][column] == OBSTACLE) {
            return false;
        }

        return true;
    }

    /**
     * Create a new ant
     *
     * @param row       row of new ant
     * @param column    column of ant
     * @param foundFood food status of ant
     */
    public void addAnt(int row, int column, boolean foundFood) {
        // Add the new ant's attributes to the end of each respective array
        antRow = Utils.appendToArray(antRow, row);
        antColumn = Utils.appendToArray(antColumn, column);
        antFoundFood = Utils.appendToArray(antFoundFood, foundFood);
    }

    /**
     * Delete an ant
     *
     * @param index index of the ant's data in the arrays
     */
    public void deleteAnt(int index) {
        // Delete the ant's attributes from each respective array
        antRow = Utils.deleteFromArray(antRow, index);
        antColumn = Utils.deleteFromArray(antColumn, index);
        antFoundFood = Utils.deleteFromArray(antFoundFood, index);
    }

    /**
     * Given a coordinate and direction, return the new coordinate after moving into that direction
     * If the direction is invalid, return the original tile
     *
     * @param row       current row
     * @param column    current column
     * @param direction integer, 0 = up, 1 = right, 2 = down, 3 = left
     * @return          2D array of length 2 {row, column}
     */
    public  int[] convertToMove(int row, int column, int direction) {
        int[] nextMove = {row, column};

        nextMove[0] += DIRECTIONS[direction][0];
        nextMove[1] += DIRECTIONS[direction][1];

        return nextMove;
    }

    /**
     * Finds the best move for ants that found food and are returning to the colony
     * Uses a BFS algorithm to find if there is a path, but only returns the first move
     *
     * @param antIndex index of the ant to generate move for
     * @return         coordinates {row, column} of where the ant should move to
     */
    public int[] foundFoodNextMove(int antIndex) {
        int[] nextMove = {antRow[antIndex], antColumn[antIndex]};  // ant stays in place if no moves found

        int targetRow;
        int targetColumn;
        int currentRow;
        int currentColumn;

        int firstMove;
        int newFirstMove;

        boolean[][] visitedTiles = new boolean[numRows][numColumns];  // visited array for the BFS

        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{antRow[antIndex], antColumn[antIndex], -1});  // third item is the direction of the first move;

        visitedTiles[antRow[antIndex]][antColumn[antIndex]] = true;

        while (!queue.isEmpty()) {
            int[] currentTile = queue.poll();
            currentRow = currentTile[0];
            currentColumn = currentTile[1];
            firstMove = currentTile[2];

            // see if we reached the end destination
            if (currentRow == colonyRow && currentColumn == colonyColumn) {
                return convertToMove(antRow[antIndex], antColumn[antIndex], firstMove);
            }

            // explore the next four DIRECTIONS
            for (int i = 0; i < 4; i++) {
                targetRow = currentRow + DIRECTIONS[i][0];
                targetColumn = currentColumn + DIRECTIONS[i][1];

                if (isValidTileForAnt(targetRow, targetColumn) && !visitedTiles[targetRow][targetColumn]) {
                    visitedTiles[targetRow][targetColumn] = true;

                    // the direction of the first move in this branch, update it if not set yet
                    if (firstMove == -1) {
                        newFirstMove = i;
                    } else {
                        newFirstMove = firstMove;
                    }

                    queue.add(new int[]{targetRow, targetColumn, newFirstMove});
                }
            }
        }


        return nextMove;  // return the exact same position
    }

    /**
     * Using pheromones and map analysis, choose the next move for an ant at a given spot
     * This method is for ants that are still searching for food
     *
     * @param antIndex index of the ant that is moving
     * @return         coordinates {row, column} of where the ant should move to
     */
    public int[] findFoodNextMove(int antIndex) {
        int[] nextMove = {antRow[antIndex], antColumn[antIndex]};  // nextMove[0] is the new row, nextMove[1] is the new column
        // default move is to stay in the same tile if there are no legal moves

        int legalMoves = 4;  // number of legal moves

        int[] possibleMoves = {UP, RIGHT, LEFT, DOWN};
        int[] weights = new int[4];  // the weight (how likely to choose) for each move (weight is 0 for invalid moves, otherwise it is the pheromone value)

        for (int i = 0; i < 4; i++) {  // i is the direction we are checking
            if (!isValidTileForAnt(antRow[antIndex] + DIRECTIONS[i][0], antColumn[antIndex] + DIRECTIONS[i][1])) {
                weights[i] = 0;  // remove the chance of this move being picked
                legalMoves--;
            } else if (terrainGrid[antRow[antIndex] + DIRECTIONS[i][0]][antColumn[antIndex] + DIRECTIONS[i][1]] > 0) {  // if there is food on an adjacent tile, move there automatically
                return convertToMove(antRow[antIndex] + DIRECTIONS[i][0], antColumn[antIndex] + DIRECTIONS[i][1], i);
            } else {
                weights[i] = pheromoneGrid[antRow[antIndex] + DIRECTIONS[i][0]][antRow[antIndex] + DIRECTIONS[i][0]];  // set the weighting equal to the pheromone strength
            }
        }

        if (legalMoves > 0) {
            nextMove = convertToMove(antRow[antIndex], antColumn[antIndex], Utils.weightedRandomChoice(possibleMoves, weights));  // now choose one of the directions to move in

            return nextMove;
        } else {
            return nextMove;  // stay in place if there are no legal moves
        }

    }

    /**
     * Finds the average value in the pheromone grid of a tile and its (up to four) adjacent tiles
     * Excludes obstacles from count
     *
     * @return            the average value
     */
    public double areaAverageValue(int row, int column) {
        double sum = pheromoneGrid[row][column];
        int counter = 1;

        for (int i = 0; i < 4; i++) {
            if (isValidTileForAnt(row + DIRECTIONS[i][0], column + DIRECTIONS[i][1])) {
                sum += pheromoneGrid[row + DIRECTIONS[i][0]][column + DIRECTIONS[i][1]];
                counter++;
            }
        }


        return sum / (double) counter;
    }

    // Getter and setter methods
    // Arrays don't have literal getters/setters; only a specific index can be accessed/changed at a time
    // These methods are not responsible for ensuring the validity of parameters

    /**
     * Returns the current turn of the simulation
     *
     * @return the current turn
     */
    public int getTurn() {
        return turn;
    }

    /**
     * Sets the turn of the simulation
     *
     * @param turn the turn to set it to
     */
    public void setTurn(int turn) {
        this.turn = turn;
    }

    /**
     * Increases the current turn by one
     */
    public void incrementTurn() {
        this.turn++;
    }

    /**
     * Returns the number of rows
     *
     * @return the number of rows
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * Returns the number of columns
     *
     * @return the number of columns
     */
    public int getNumColumns() {
        return numColumns;
    }

    /**
     * Returns the row of the colony
     *
     * @return the row of the colony
     */
    public int getColonyRow() {
        return colonyRow;
    }

    /**
     * Sets the row of the colony
     *
     * @param colonyRow the new row of the colony
     */
    public void setColonyRow(int colonyRow) {
        this.colonyRow = colonyRow;
    }

    /**
     * Returns the column of the colony
     *
     * @return the column of the colony
     */
    public int getColonyColumn() {
        return colonyColumn;
    }

    /**
     * Sets the column of the colony
     *
     * @param colonyColumn the new row of the colony
     */
    public void setColonyColumn(int colonyColumn) {
        this.colonyColumn = colonyColumn;
    }

    /**
     * Returns the row of an ant
     *
     * @param index index of the ant to get row of
     * @return      row of the selected ant
     */
    public int getAntRow(int index) {
        return antRow[index];
    }

    /**
     * Updates the row of an ant
     *
     * @param index index of the ant to update
     * @param row   the new row
     */
    public void setAntRow(int index, int row) {
        antRow[index] = row;
    }

    /**
     * Returns the column of an ant
     *
     * @param index index of the ant to get column of
     * @return      column of the selected ant
     */
    public int getAntColumn(int index) {
        return antColumn[index];
    }

    /**
     * Updates the column of an ant
     *
     * @param index  index of the ant to update
     * @param column the new column
     */
    public void setAntColumn(int index, int column) {
        antColumn[index] = column;
    }

    /**
     * Returns whether an ant has found food yet
     *
     * @param index index of the ant to look up
     * @return      if the ant has found food yet
     */
    public boolean getAntFoundFood(int index) {
        return antFoundFood[index];
    }

    /**
     * Updates the food status of an ant
     *
     * @param index     index of the ant to update
     * @param foundFood the new food status
     */
    public void setAntFoundFood(int index, boolean foundFood) {
        antFoundFood[index] = foundFood;
    }

    /**
     * Returns the current pheromone strength at a specific tile
     *
     * @param row    row of tile to access
     * @param column column of tile to access
     * @return       pheromone strength at that tile
     */
    public int getPheromoneGrid(int row, int column) {
        return pheromoneGrid[row][column];
    }

    /**
     * Sets the pheromone strength at a specific tile
     *
     * @param row    row of tile to change
     * @param column column of tile to change
     * @param value  new pheromone strength
     */
    public void setPheromoneGrid(int row, int column, int value) {
        pheromoneGrid[row][column] = value;
    }

    /**
     * Returns the current terrain type at a specific tile
     *
     * @param row    row of tile to access
     * @param column column of tile to access
     * @return       terrain type at that tile
     */
    public int getTerrainGrid(int row, int column) {
        return terrainGrid[row][column];
    }

    /**
     * Sets the terrain type at a specific tile
     *
     * @param row    row of tile to change
     * @param column column of tile to change
     * @param value  new terrain type
     */
    public void setTerrainGrid(int row, int column, int value) {
        terrainGrid[row][column] = value;
    }

    /**
     * Decrease the value of the terrain grid at a specific tile by a certain amount
     * Should only be used on tiles with food and should not bring a tile below 0
     *
     * @param row     row of tile
     * @param column  column of tile
     * @param amount  amount of food to decrease
     */
    public void decreaseFood(int row, int column, int amount) {
        terrainGrid[row][column] -= amount;
    }

    /**
     * Returns the number of ants at a specific tile
     *
     * @param row    row of tile to access
     * @param column column of tile to access
     * @return       number of ants at that tile
     */
    public int getAntGrid(int row, int column) {
        return antGrid[row][column];
    }

    /**
     * Sets the number of ants at a specific tile
     *
     * @param row    row of tile to change
     * @param column column of tile to change
     * @param value  new number of ants
     */
    public void setAntGrid(int row, int column, int value) {
        antGrid[row][column] = value;
    }

    /**
     * Increase the number of ants on a tile by one
     *
     * @param row    row of tile
     * @param column column of tile
     */
    public void incrementAnts(int row, int column) {
        antGrid[row][column]++;
    }

    /**
     * Decrease the number of ants on a tile by one
     * Should not bring the number below 0
     *
     * @param row    row of tile
     * @param column column of tile
     */
    public void decrementAnts(int row, int column) {
        antGrid[row][column]--;
    }

    /**
     * Returns the default pheromone strength left behind by an ant
     *
     * @return the default pheromone strength
     */
    public int getPheromoneStrength() {
        return pheromoneStrength;
    }

    /**
     * Sets the default pheromone strength left behind by an ant
     *
     * @param pheromoneStrength the new default pheromone strength
     */
    public void setPheromoneStrength(int pheromoneStrength) {
        this.pheromoneStrength = pheromoneStrength;
    }

    /**
     * Returns the pheromone decay rate
     *
     * @return the pheromone decay rate
     */
    public int getPheromoneDecay() {
        return pheromoneDecay;
    }

    /**
     * Sets the pheromone decay rate
     *
     * @param pheromoneDecay the new pheromone decay rate
     */
    public void setPheromoneDecay(int pheromoneDecay) {
        this.pheromoneDecay = pheromoneDecay;
    }

    /**
     * Returns the minimum pheromone level of a tile
     *
     * @return the minimum pheromone level of a tile
     */
    public int getMinimumPheromone() {
        return minimumPheromone;
    }

    /**
     * Sets the minimum pheromone level of all tiles
     *
     * @param minimumPheromone the new minimum pheromone level
     */
    public void setMinimumPheromone(int minimumPheromone) {
        this.minimumPheromone = minimumPheromone;
    }


}
