package pis03_2016.savealltherobots.model;


import java.util.ArrayList;

/**
 * Class that provides help to the Grid with Math functions
 */
public class MathLibrary {

    /**
     * Prints the passed matrix
     *
     * @param matrix matrix
     */
    public static void printMatrix(LevelSquare[][] matrix) {

        int rowNum = matrix.length;

        if (rowNum == 0) {
            return;
        }

        for (LevelSquare[] m : matrix) {
            for (LevelSquare square: m) {
                System.out.print(square.toString() + " ");
            }
            System.out.println("");
        }
    }

    /**
     * Returns the transpose matrix of the passed matrix
     *
     * @param matrix matrix
     * @return the transpose matrix
     */
    public static <Element> ArrayList<ArrayList<Element>> obtainTransposeMatrix
    (ArrayList<ArrayList<Element>> matrix) {

        int rowNum = matrix.size();

        if (rowNum == 0) {
            return matrix;
        }

        int columnNum = matrix.get(0).size();

        ArrayList<ArrayList<Element>> transposeMatrix = new ArrayList<>(columnNum);
        for (int i = 0; i < columnNum; i++) {
            transposeMatrix.add(new ArrayList<Element>(rowNum));
            for (int j = 0; j < rowNum; j++) {
                transposeMatrix.get(i).add(null);
            }
        }

        for (int m = 0; m < rowNum; m++) {
            for (int n = 0; n < columnNum; n++) {
                transposeMatrix.get(n).set(m, matrix.get(m).get(n));
            }
        }

        return transposeMatrix;
    }

    /**
     * Returns a matrix with the same rows of the passed one but in inverted order
     *
     * @param matrix matrix
     * @return the inverted rows matrix
     */
    public static <Element> ArrayList<ArrayList<Element>> obtainInvertedRowsMatrix(
            ArrayList<ArrayList<Element>> matrix) {

        int rowNum = matrix.size();

        if (rowNum < 2) {
            return matrix;
        }

        int columnNum = matrix.get(0).size();

        ArrayList<ArrayList<Element>> invertedRowsMatrix = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            invertedRowsMatrix.add(new ArrayList<Element>(columnNum));
        }


        for (int m = 0; m < rowNum; m++) {
            invertedRowsMatrix.set(m, matrix.get(rowNum - 1 - m));
        }

        return invertedRowsMatrix;
    }


}
