/**
 * Class containing helpful static methods
 *
 * @author Jerry Cui
 * @version %I%, %G%
 * @since 1.0
 */
public class Utils {
    /**
     * Appends a value to the end of an array
     * For integers only
     *
     * @param array array to append to
     * @param value the value to append
     * @return      the array with the value appended
     */
    public static int[] appendToArray(int[] array, int value) {
        int[] newArray = new int[array.length + 1];

        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }

        newArray[array.length] = value;

        return newArray;
    }

    /**
     * Deletes an index from an array
     * For integers only
     *
     * @param array array to delete from
     * @param index index of item to delete
     * @return      array with the value deleted
     */
    public static int[] deleteFromArray(int[] array, int index) {
        int[] newArray = new int[array.length - 1];

        for (int i = 0; i < index; i++) {
            newArray[i] = array[i];
        }

        for (int i = index + 1; i < array.length; i++) {
            newArray[i - 1] = array[i];
        }

        return newArray;
    }

    /**
     * Appends a value to the end of an array
     * For booleans only
     *
     * @param array array to append to
     * @param value the value to append
     * @return      the array with the value appended
     */
    public static boolean[] appendToArray(boolean[] array, boolean value) {
        boolean[] newArray = new boolean[array.length + 1];

        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }

        newArray[array.length] = value;

        return newArray;
    }

    /**
     * Deletes an index from an array
     * For booleans only
     *
     * @param array array to delete from
     * @param index index of item to delete
     * @return      array with the value deleted
     */
    public static boolean[] deleteFromArray(boolean[] array, int index) {
        boolean[] newArray = new boolean[array.length - 1];

        for (int i = 0; i < index; i++) {
            newArray[i] = array[i];
        }

        for (int i = index + 1; i < array.length; i++) {
            newArray[i - 1] = array[i];
        }

        return newArray;
    }

    /**
     * Generate a random integer between two points, inclusive
     *
     * @param start minimum value
     * @param end   maximum value
     * @return      integer between start and end, inclusive
     */
    public static int randomInteger(int start, int end) {
        return (int) (Math.random() * (end - start + 1)) + start;
    }

    /**
     * Returns a random choice from the array, taking into account the weight each item is given in the weights array
     * Let S be the sum of all the weights, then the chance of choices[i] being selected is weights[i] / S
     *
     * @param choices choices to choose from
     * @param weights weights[i] is the weight of choices[i]
     * @return        randomly chosen integer
     */
    public static int weightedRandomChoice(int[] choices, int[] weights) {
        // Special cases:
        if (choices.length == 1) {  // only one item in array
            return choices[0];
        } else if (choices.length == 0) {  // empty array
            return -1;
        }

        // Create a new weights array where each item increases by the sum of the elements before it
        int[] prefixWeights = new int[weights.length];
        prefixWeights[0] = weights[0];  // hard code first index
        for (int i = 1; i < weights.length; i++) {
            prefixWeights[i] = weights[i] + prefixWeights[i - 1];
        }

        // Pick a random integer x between 0 and the last number in prefixWeights - 1inclusive
        // Loop through all values in prefixWeights; if x < prefixWeight[i], then choose the corresponding option in choices
        int index = randomInteger(0, prefixWeights[prefixWeights.length - 1] - 1);
        for (int i = 0; i < prefixWeights.length; i++) {
            if (index < prefixWeights[i]) {
                return choices[i];
            }
        }

        return -1;  // in case no option was picked
    }


}
