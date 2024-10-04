import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    // Default values
    private int numRows = 10;
    private int numColumns = 10;
    private int colonyRow = 0;
    private int colonyColumn = 0;

    private int[][] pheromoneGrid;
    private int[][] terrainGrid;
    private int[][] antGrid;

    // valid possibilities of a location based on the number of rows and columns
    private String[] rowOptions = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private String[] columnOptions = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    private AntColonyEngine engine;

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
                pheromoneGrid = new int[numRows][numColumns];
                terrainGrid = new int[numRows][numColumns];
                antGrid = new int[numRows][numColumns];

                inputColony();
            }
        });
        nextButton.setAlignmentX(CENTER_ALIGNMENT);
        initializationPanel.add(nextButton);
    }

    public void inputColony() {

    }
}
