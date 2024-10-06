import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;

import static java.awt.Component.CENTER_ALIGNMENT;

/**
 * Object that handles all the GUI for a simulation
 *
 * @author Jerry Cui
 * @version %I%, %G%
 * @since 1.0
 */
public class GUISimulation {
    private static JFrame frame;

    // Constants
    final int SCREENWIDTH = 1920;
    final int SCREENHEIGHT = 1080;

    final private int MINROWS = 10;
    final private int MINCOLUMNS = 10;
    final private int MAXROWS = 25;
    final private int MAXCOLUMNS = 25;

    final private int MAXSLIDERWIDTH = 300;
    final private int MAXSLIDERHEIGHT = 50;
    final private int MAXCOMBOBOXWIDTH = 300;
    final private int MAXCOMBOBOXHEIGHT = 20;

    final private double gridPanelPercentage = 0.7;  // percent of the screen that contains the grid (other percentage is for the control panel)

    // Default values
    private int numRows = 10;
    private int numColumns = 10;
    private int colonyRow = 0;
    private int colonyColumn = 0;

    // Default colours
    private Color colonyColor = Color.GREEN;
    private Color obstacleColor = Color.RED;
    private Color emptyColor = Color.WHITE;

    // Used to store information from setup before passing into engine class
    private int[][] pheromoneGrid;
    private int[][] terrainGrid;
    private int[][] antGrid;
    int[] antRow = {};
    int[] antColumn = {};
    boolean[] antFoundFood = {};

    // valid possibilities of a location based on the number of rows and columns
    private String[] rowOptions = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private String[] columnOptions = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    private AntColonyEngine engine;

    private int selectedRow = 0;
    private int selectedColumn = 0;

    /**
     * Initialize the GUI simulation
     */
    public GUISimulation() {
        // Create the frame
        frame = new JFrame("Ant Colony Optimization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(SCREENWIDTH, SCREENHEIGHT);

        inputDimensions();

        frame.setVisible(true);
    }

    /**
     * Allow user to input the dimensions of the simulation
     */
    public void inputDimensions() {
        JPanel initializationPanel = new JPanel();
        frame.add(initializationPanel);
        initializationPanel.setLayout(new BoxLayout(initializationPanel, BoxLayout.Y_AXIS));

        JLabel rowsLabel = new JLabel("Enter number of rows: ");
        rowsLabel.setAlignmentX(CENTER_ALIGNMENT);
        initializationPanel.add(rowsLabel);

        JSlider numRowsSlider = new JSlider(JSlider.HORIZONTAL, MINROWS, MAXROWS, numRows);
        numRowsSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                numRows = (int) numRowsSlider.getValue();
                // update row options
                rowOptions = new String[numRows];
                for (int i = 0; i < numRows; i++) {
                    rowOptions[i] = String.valueOf(i);
                }
            }
        });
        numRowsSlider.setMajorTickSpacing(MAXROWS - MINROWS);
        numRowsSlider.setMinorTickSpacing(1);
        numRowsSlider.setPaintTicks(true);
        numRowsSlider.setPaintLabels(true);
        numRowsSlider.setMaximumSize(new Dimension(MAXSLIDERWIDTH, MAXSLIDERHEIGHT));
        initializationPanel.add(numRowsSlider);

        JLabel columnsLabel = new JLabel("Enter number of columns: ");
        columnsLabel.setAlignmentX(CENTER_ALIGNMENT);
        initializationPanel.add(columnsLabel);

        JSlider numColumnsSlider = new JSlider(JSlider.HORIZONTAL, MINCOLUMNS, MAXCOLUMNS, numColumns);
        numColumnsSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                numColumns = (int) numColumnsSlider.getValue();

                columnOptions = new String[numColumns];
                for (int i = 0; i < numColumns; i++) {
                    columnOptions[i] = String.valueOf(i);
                }
            }
        });
        numColumnsSlider.setMajorTickSpacing(MAXCOLUMNS - MINCOLUMNS);
        numColumnsSlider.setMinorTickSpacing(1);
        numColumnsSlider.setPaintTicks(true);
        numColumnsSlider.setPaintLabels(true);
        numColumnsSlider.setMaximumSize(new Dimension(MAXSLIDERWIDTH, MAXSLIDERHEIGHT));
        initializationPanel.add(numColumnsSlider);

        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Initialize grids and then move on to the next screen
                antGrid = new int[numRows][numColumns];
                terrainGrid = new int[numRows][numColumns];

                inputColony();
            }
        });
        nextButton.setAlignmentX(CENTER_ALIGNMENT);
        initializationPanel.add(nextButton);
    }

    /**
     * Allow user to enter the location of the colony
     */
    public void inputColony() {
        // Delete previous GUI
        frame.getContentPane().removeAll();

        JPanel colonyPanel = new JPanel();
        frame.add(colonyPanel);
        colonyPanel.setLayout(new BoxLayout(colonyPanel, BoxLayout.Y_AXIS));

        JLabel rowsLabel = new JLabel("Enter row of the colony: ");
        rowsLabel.setAlignmentX(CENTER_ALIGNMENT);
        colonyPanel.add(rowsLabel);

        JComboBox rowComboBox = new JComboBox(rowOptions);
        rowComboBox.setAlignmentX(CENTER_ALIGNMENT);
        rowComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                colonyRow = Integer.valueOf(rowComboBox.getSelectedItem().toString());
            }
        });
        colonyPanel.add(rowComboBox);
        rowComboBox.setMaximumSize(new Dimension(MAXCOMBOBOXWIDTH, MAXCOMBOBOXHEIGHT));

        JLabel columnsLabel = new JLabel("Enter column of the colony: ");
        columnsLabel.setAlignmentX(CENTER_ALIGNMENT);
        colonyPanel.add(columnsLabel);

        JComboBox columnComboBox = new JComboBox(rowOptions);
        columnComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                colonyColumn = Integer.valueOf(columnComboBox.getSelectedItem().toString());
            }
        });
        columnComboBox.setAlignmentX(CENTER_ALIGNMENT);
        colonyPanel.add(columnComboBox);
        columnComboBox.setMaximumSize(new Dimension(MAXCOMBOBOXWIDTH, MAXCOMBOBOXHEIGHT));

        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Save colony location and move on
                terrainGrid[colonyRow][colonyColumn] = AntColonyEngine.COLONY;
                inputObstacles();
            }
        });
        nextButton.setAlignmentX(CENTER_ALIGNMENT);
        colonyPanel.add(nextButton);

        // Update the screen
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Allow the user to input obstacles
     */
    public void inputObstacles() {
        // Delete previous GUI
        frame.getContentPane().removeAll();

        // Create the grid panel (left side)
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(numRows, numColumns));

        // Calculate the size of each cell based on screen dimensions
        int cellWidth = (int) (SCREENWIDTH * gridPanelPercentage / numColumns); // leave space for the control panel
        int cellHeight = SCREENHEIGHT / numRows;

        JLabel hoverLabel = new JLabel("");  // label which shows the coordinates of the current tile mouse is hovering over
        hoverLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Add grid cells to the panel
        for (int row = 0; row < numRows; row++) {
            for (int column = 0; column < numColumns; column++) {
                JPanel cell = new JPanel();
                cell.setPreferredSize(new Dimension(cellWidth, cellHeight));
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // draw grid lines

                if (terrainGrid[row][column] == AntColonyEngine.COLONY) {
                    cell.setBackground(colonyColor);
                } else if (terrainGrid[row][column] == AntColonyEngine.OBSTACLE) {
                    cell.setBackground(obstacleColor);
                }

                final int currentRow = row;  // this creates new variables for each loop, instead of getting overridden
                final int currentColumn = column;
                cell.addMouseMotionListener(new MouseMotionListener() {
                    @Override
                    public void mouseMoved(MouseEvent e) {  // check if the mouse moved onto this tile, then update the hover label
                        hoverLabel.setText("Row: " + currentRow + ", Column: " + currentColumn);
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        // Do nothing because Java requires me to override this method
                    }
                });
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Check if the tile is not a colony, then flip the obstacle state
                        if (terrainGrid[currentRow][currentColumn] != AntColonyEngine.COLONY) {
                            if (terrainGrid[currentRow][currentColumn] == AntColonyEngine.OBSTACLE) {
                                terrainGrid[currentRow][currentColumn] = AntColonyEngine.EMPTY;
                                cell.setBackground(emptyColor);
                            } else if (terrainGrid[currentRow][currentColumn] == AntColonyEngine.EMPTY) {
                                terrainGrid[currentRow][currentColumn] = AntColonyEngine.OBSTACLE;
                                cell.setBackground(obstacleColor);
                            }

                        }
                    }
                });

                gridPanel.add(cell);
            }
        }

        // Create the control panel (right side)
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        JLabel infoLabel1 = new JLabel("Click on a tile to change it to an obstacle, or vice versa.");
        infoLabel1.setAlignmentX(CENTER_ALIGNMENT);
        controlPanel.add(infoLabel1);

        JLabel infoLabel2 = new JLabel("Green tile represents the colony, red tiles are obstacles.");
        infoLabel2.setAlignmentX(CENTER_ALIGNMENT);
        controlPanel.add(infoLabel2);

        controlPanel.add(hoverLabel);

        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputFood();
            }
        });
        nextButton.setAlignmentX(CENTER_ALIGNMENT);
        controlPanel.add(nextButton);

        // Create the split pane, with more space given to the grid
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gridPanel, controlPanel);
        splitPane.setDividerLocation((int) (SCREENWIDTH * gridPanelPercentage));
        frame.add(splitPane);

        // Update the screen
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Allow the user to place food onto the grid
     */
    public void inputFood() {
        // Delete previous GUI
        frame.getContentPane().removeAll();

        // Create the grid panel (left side)
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(numRows, numColumns));

        // Calculate the size of each cell based on screen dimensions
        int cellWidth = (int) (SCREENWIDTH * gridPanelPercentage / numColumns); // leave space for the control panel
        int cellHeight = SCREENHEIGHT / numRows;

        JLabel hoverLabel = new JLabel("");  // label which shows the coordinates of the current tile mouse is hovering over
        hoverLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Add grid cells to the panel
        for (int row = 0; row < numRows; row++) {
            for (int column = 0; column < numColumns; column++) {
                JPanel cell = new JPanel();
                cell.setPreferredSize(new Dimension(cellWidth, cellHeight));
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // draw grid lines

                if (terrainGrid[row][column] == AntColonyEngine.COLONY) {
                    cell.setBackground(colonyColor);
                } else if (terrainGrid[row][column] == AntColonyEngine.OBSTACLE) {
                    cell.setBackground(obstacleColor);
                }

                // Create a layout to display amount of food
                cell.setLayout(new BorderLayout());

                // Only display food on non-obstacle and non-colony tiles
                final JLabel foodLabel = new JLabel("");  // empty string for non-food tiles
                foodLabel.setVerticalAlignment(SwingConstants.TOP);
                foodLabel.setHorizontalAlignment(SwingConstants.LEFT);
                cell.add(foodLabel);
                if (terrainGrid[row][column] != AntColonyEngine.COLONY && terrainGrid[row][column] != AntColonyEngine.OBSTACLE) {
                    foodLabel.setText("" + terrainGrid[row][column]);
                }


                final int currentRow = row;  // this creates new variables for each loop, instead of getting overridden
                final int currentColumn = column;
                cell.addMouseMotionListener(new MouseMotionListener() {
                    @Override
                    public void mouseMoved(MouseEvent e) {  // check if the mouse moved onto this tile, then update the hover label
                        hoverLabel.setText("Row: " + currentRow + ", Column: " + currentColumn);
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        // Do nothing because Java requires me to override this method
                    }
                });
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Check if there is food on this tile, then check if it is right/left click, then increase/decrease food
                        if (terrainGrid[currentRow][currentColumn] != AntColonyEngine.COLONY && terrainGrid[currentRow][currentColumn] != AntColonyEngine.OBSTACLE) {
                            if (e.getButton() == MouseEvent.BUTTON1) {  // left click
                                terrainGrid[currentRow][currentColumn]++;
                                foodLabel.setText("" + terrainGrid[currentRow][currentColumn]);
                            } else if (e.getButton() == MouseEvent.BUTTON3) {  // right click
                                if (terrainGrid[currentRow][currentColumn] > 0) {  // don't allow negative food
                                    terrainGrid[currentRow][currentColumn]--;
                                    foodLabel.setText("" + terrainGrid[currentRow][currentColumn]);
                                }
                            }
                        }
                    }
                });

                gridPanel.add(cell);
            }
        }

        // Create the control panel (right side)
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        JLabel infoLabel1 = new JLabel("Left click on a tile to increase amount of food, right click to decrease amount of food.");
        infoLabel1.setAlignmentX(CENTER_ALIGNMENT);
        controlPanel.add(infoLabel1);

        JLabel infoLabel2 = new JLabel("Amount of food on a tile is displayed on the top left corner");
        infoLabel2.setAlignmentX(CENTER_ALIGNMENT);
        controlPanel.add(infoLabel2);

        JLabel infoLabel3 = new JLabel("Green tile represents the colony, red tiles are obstacles.");
        infoLabel3.setAlignmentX(CENTER_ALIGNMENT);
        controlPanel.add(infoLabel3);

        controlPanel.add(hoverLabel);

        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputAnts();
            }
        });
        nextButton.setAlignmentX(CENTER_ALIGNMENT);
        controlPanel.add(nextButton);

        // Create the split pane, with more space given to the grid
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gridPanel, controlPanel);
        splitPane.setDividerLocation((int) (SCREENWIDTH * gridPanelPercentage));
        frame.add(splitPane);

        // Update the screen
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Allow the user to place starting ants on the simulation
     */
    public void inputAnts() {
        // Delete previous GUI
        frame.getContentPane().removeAll();

        // Create the grid panel (left side)
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(numRows, numColumns));

        // Calculate the size of each cell based on screen dimensions
        int cellWidth = (int) (SCREENWIDTH * gridPanelPercentage / numColumns); // leave space for the control panel
        int cellHeight = SCREENHEIGHT / numRows;

        JLabel hoverLabel = new JLabel("");  // label which shows the coordinates of the current tile mouse is hovering over
        hoverLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Add grid cells to the panel
        for (int row = 0; row < numRows; row++) {
            for (int column = 0; column < numColumns; column++) {
                JPanel cell = new JPanel();
                cell.setPreferredSize(new Dimension(cellWidth, cellHeight));
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // draw grid lines

                if (terrainGrid[row][column] == AntColonyEngine.COLONY) {
                    cell.setBackground(colonyColor);
                } else if (terrainGrid[row][column] == AntColonyEngine.OBSTACLE) {
                    cell.setBackground(obstacleColor);
                }

                // Create a layout to display amount of food
                cell.setLayout(new BorderLayout());

                // Only display food on non-obstacle and non-colony tiles
                final JLabel foodLabel = new JLabel("");  // empty string for non-food tiles
                foodLabel.setVerticalAlignment(SwingConstants.TOP);
                foodLabel.setHorizontalAlignment(SwingConstants.LEFT);
                cell.add(foodLabel, BorderLayout.NORTH);
                if (terrainGrid[row][column] != AntColonyEngine.COLONY && terrainGrid[row][column] != AntColonyEngine.OBSTACLE) {
                    foodLabel.setText("" + terrainGrid[row][column]);
                }

                // Only display ants on non-obstacle tiles
                final JLabel antLabel = new JLabel("");  // empty string for non-food tiles
                antLabel.setVerticalAlignment(SwingConstants.BOTTOM);
                antLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                cell.add(antLabel, BorderLayout.SOUTH);
                if (terrainGrid[row][column] != AntColonyEngine.OBSTACLE) {
                    antLabel.setText("" + antGrid[row][column]);
                }


                final int currentRow = row;  // this creates new variables for each loop, instead of getting overridden
                final int currentColumn = column;
                cell.addMouseMotionListener(new MouseMotionListener() {
                    @Override
                    public void mouseMoved(MouseEvent e) {  // check if the mouse moved onto this tile, then update the hover label
                        hoverLabel.setText("Row: " + currentRow + ", Column: " + currentColumn);
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        // Do nothing because Java requires me to override this method
                    }
                });
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Check if ants can be added to the tile, and then add/decrease the amount
                        // Adding ants to the arrays will be done once the total number of ants is finalized
                        if (terrainGrid[currentRow][currentColumn] != AntColonyEngine.OBSTACLE) {
                            if (e.getButton() == MouseEvent.BUTTON1) {  // left click
                                antGrid[currentRow][currentColumn]++;
                                antLabel.setText("" + antGrid[currentRow][currentColumn]);
                            } else if (e.getButton() == MouseEvent.BUTTON3) {  // right click
                                if (terrainGrid[currentRow][currentColumn] > 0) {  // don't allow negative food
                                    antGrid[currentRow][currentColumn]--;
                                    antLabel.setText("" + antGrid[currentRow][currentColumn]);
                                }
                            }
                        }
                    }
                });

                gridPanel.add(cell);
            }
        }

        // Create the control panel (right side)
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        JLabel infoLabel1 = new JLabel("Left click on a tile to increase amount of ants, right click to decrease amount of ants.");
        infoLabel1.setAlignmentX(CENTER_ALIGNMENT);
        controlPanel.add(infoLabel1);

        JLabel infoLabel2 = new JLabel("Amount of food on a tile is displayed on the top left corner");
        infoLabel2.setAlignmentX(CENTER_ALIGNMENT);
        controlPanel.add(infoLabel2);

        JLabel infoLabel3 = new JLabel("Amount of ants on a tile is displayed on the bottom right corner");
        infoLabel3.setAlignmentX(CENTER_ALIGNMENT);
        controlPanel.add(infoLabel3);

        JLabel infoLabel4 = new JLabel("Green tile represents the colony, red tiles are obstacles.");
        infoLabel4.setAlignmentX(CENTER_ALIGNMENT);
        controlPanel.add(infoLabel4);

        controlPanel.add(hoverLabel);

        JButton nextButton = new JButton("Begin Simulation");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Add all ant data to the arrays
                for (int row = 0; row < numRows; row++) {
                    for (int column = 0; column < numColumns; column++) {
                        for (int i = 0; i < antGrid[row][column]; i++) {
                            antRow = Utils.appendToArray(antRow, row);
                            antColumn = Utils.appendToArray(antColumn, column);
                            antFoundFood = Utils.appendToArray(antFoundFood, false);
                        }
                    }
                }

                // Initialize the engine
                engine = new AntColonyEngine(numRows, numColumns, colonyRow, colonyColumn, antRow, antColumn, terrainGrid);

                simulation();  // start the simulation
            }
        });
        nextButton.setAlignmentX(CENTER_ALIGNMENT);
        controlPanel.add(nextButton);

        // Create the split pane, with more space given to the grid
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gridPanel, controlPanel);
        splitPane.setDividerLocation((int) (SCREENWIDTH * gridPanelPercentage));
        frame.add(splitPane);

        // Update the screen
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Creates the GUI for the simulation. selectedRow and selectedColumn is the tile to display detailed information about in the control panel
     */
    public void simulation() {
        // Delete previous GUI
        frame.getContentPane().removeAll();

        // Create the grid panel (left side)
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(numRows, numColumns));

        // Calculate the size of each cell based on screen dimensions
        int cellWidth = (int) (SCREENWIDTH * gridPanelPercentage / numColumns); // leave space for the control panel
        int cellHeight = SCREENHEIGHT / numRows;

        JLabel hoverLabel = new JLabel("");  // label which shows the coordinates of the current tile mouse is hovering over
        hoverLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Add grid cells to the panel
        for (int row = 0; row < numRows; row++) {
            for (int column = 0; column < numColumns; column++) {
                JPanel cell = new JPanel();
                cell.setPreferredSize(new Dimension(cellWidth, cellHeight));
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // draw grid lines

                if (engine.getTerrainGrid(row, column) == AntColonyEngine.COLONY) {
                    cell.setBackground(colonyColor);
                } else if (engine.getTerrainGrid(row, column) == AntColonyEngine.OBSTACLE) {
                    cell.setBackground(obstacleColor);
                }

                // Create a layout to display amount of food
                cell.setLayout(new BorderLayout());

                // Only display food on non-obstacle and non-colony tiles
                if (engine.getTerrainGrid(row, column) != AntColonyEngine.COLONY && engine.getTerrainGrid(row, column) != AntColonyEngine.OBSTACLE) {
                    if (engine.getTerrainGrid(row, column) > 0) {
                        final JLabel foodLabel = new JLabel("" + engine.getTerrainGrid(row, column));
                        foodLabel.setVerticalAlignment(SwingConstants.TOP);
                        foodLabel.setHorizontalAlignment(SwingConstants.LEFT);
                        cell.add(foodLabel, BorderLayout.NORTH);
                    }
                }

                // Only display ants on non-obstacle tiles
                if (engine.getTerrainGrid(row, column) != AntColonyEngine.OBSTACLE) {
                    if (engine.getAntGrid(row, column) > 0) {
                        final JLabel antLabel = new JLabel("" + engine.getAntGrid(row, column));
                        antLabel.setVerticalAlignment(SwingConstants.BOTTOM);
                        antLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                        cell.add(antLabel, BorderLayout.SOUTH);
                    }

                }


                final int currentRow = row;  // this creates new variables for each loop, instead of getting overridden
                final int currentColumn = column;
                cell.addMouseMotionListener(new MouseMotionListener() {
                    @Override
                    public void mouseMoved(MouseEvent e) {  // check if the mouse moved onto this tile, then update the hover label
                        hoverLabel.setText("Row: " + currentRow + ", Column: " + currentColumn + ", Pheromone strength: " + engine.getPheromoneGrid(currentRow, currentColumn));
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        // Do nothing because Java requires me to override this method
                    }
                });
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        simulation();  // redraw the GUI and display information of the recently targeted tile
                    }
                });

                gridPanel.add(cell);
            }
        }

        // Create the control panel (right side)
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        JLabel infoLabel1 = new JLabel("Click on a tile to access information and commands.");
        infoLabel1.setAlignmentX(CENTER_ALIGNMENT);
        controlPanel.add(infoLabel1);

        controlPanel.add(hoverLabel);

        JButton nextButton = new JButton("Next turn");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call method to update game state
                engine.update();

                simulation();  // start the next turn
            }
        });
        nextButton.setAlignmentX(CENTER_ALIGNMENT);
        controlPanel.add(nextButton);

        // Create the split pane, with more space given to the grid
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gridPanel, controlPanel);
        splitPane.setDividerLocation((int) (SCREENWIDTH * gridPanelPercentage));
        frame.add(splitPane);

        // Update the screen
        frame.revalidate();
        frame.repaint();
    }
}
