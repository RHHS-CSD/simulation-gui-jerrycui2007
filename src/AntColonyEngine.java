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
    private int turn;  // current turn of the simulation

    private int numRows;
    private int numColumns;

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

    // Getter and setter methods

    /**
     * Returns the current turn of the simulation
     * @return the current turn
     */
    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    /**
     * Returns the current turn of the simulation
     * @return the current turn
     */
    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    /**
     * Returns the current turn of the simulation
     * @return the current turn
     */
    public int getNumColumns() {
        return numColumns;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    /**
     * Returns the current turn of the simulation
     * @return the current turn
     */
    public int getColonyRow() {
        return colonyRow;
    }

    public void setColonyRow(int colonyRow) {
        this.colonyRow = colonyRow;
    }

    /**
     * Returns the current turn of the simulation
     * @return the current turn
     */
    public int getColonyColumn() {
        return colonyColumn;
    }

    public void setColonyColumn(int colonyColumn) {
        this.colonyColumn = colonyColumn;
    }

    /**
     * Returns the current turn of the simulation
     * @return the current turn
     */
    public int[] getAntRow() {
        return antRow;
    }

    public void setAntRow(int[] antRow) {
        this.antRow = antRow;
    }

    /**
     * Returns the current turn of the simulation
     * @return the current turn
     */
    public int[] getAntColumn() {
        return antColumn;
    }

    public void setAntColumn(int[] antColumn) {
        this.antColumn = antColumn;
    }

    /**
     * Returns the current turn of the simulation
     * @return the current turn
     */
    public boolean[] getAntFoundFood() {
        return antFoundFood;
    }

    public void setAntFoundFood(boolean[] antFoundFood) {
        this.antFoundFood = antFoundFood;
    }

    /**
     * Returns the current turn of the simulation
     * @return the current turn
     */
    public int[][] getPheromoneGrid() {
        return pheromoneGrid;
    }

    public void setPheromoneGrid(int[][] pheromoneGrid) {
        this.pheromoneGrid = pheromoneGrid;
    }

    /**
     * Returns the current turn of the simulation
     * @return the current turn
     */
    public int[][] getTerrainGrid() {
        return terrainGrid;
    }

    public void setTerrainGrid(int[][] terrainGrid) {
        this.terrainGrid = terrainGrid;
    }

    /**
     * Returns the current turn of the simulation
     * @return the current turn
     */
    public int[][] getAntGrid() {
        return antGrid;
    }

    public void setAntGrid(int[][] antGrid) {
        this.antGrid = antGrid;
    }

    /**
     * Returns the current turn of the simulation
     * @return the current turn
     */
    public int getPheromoneStrength() {
        return pheromoneStrength;
    }

    public void setPheromoneStrength(int pheromoneStrength) {
        this.pheromoneStrength = pheromoneStrength;
    }

    /**
     * Returns the current turn of the simulation
     * @return the current turn
     */
    public int getPheromoneDecay() {
        return pheromoneDecay;
    }

    public void setPheromoneDecay(int pheromoneDecay) {
        this.pheromoneDecay = pheromoneDecay;
    }

    /**
     * Returns the current turn of the simulation
     * @return the current turn
     */
    public int getMinimumPheromone() {
        return minimumPheromone;
    }

    public void setMinimumPheromone(int minimumPheromone) {
        this.minimumPheromone = minimumPheromone;
    }


}
