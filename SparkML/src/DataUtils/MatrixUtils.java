package DataUtils;

import java.text.DecimalFormat;

/**
 * Created by zrm22 on 10/23/15.
 */
public class MatrixUtils {
    //Borrowed from Weka
    public static String matrixToString(double[][] matrix ) {

        StringBuffer text = new StringBuffer();
        char[] IDChars = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
        int IDWidth;
        boolean fractional = false;

        // Find the maximum value in the matrix
        // and check for fractional display requirement
        double maxval = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                double current = matrix[i][j];
                if (current < 0) {
                    current *= -10;
                }
                if (current > maxval) {
                    maxval = current;
                }
                double fract = current - Math.rint(current);
                if (!fractional && ((Math.log(fract) / Math.log(10)) >= -2)) {
                    fractional = true;
                }
            }
        }

        IDWidth = 1 + Math.max(
                (int) (Math.log(maxval) / Math.log(10) + (fractional ? 3 : 0)),
                (int) (Math.log(matrix.length) / Math.log(IDChars.length)));
        text.append("=== Confusion Matrix ===\n").append("\n");
        for (int i = 0; i < matrix.length; i++) {
            if (fractional) {
                text.append(" ").append(num2ShortID(i, IDChars, IDWidth - 3))
                        .append("   ");
            } else {
                text.append(" ").append(num2ShortID(i, IDChars, IDWidth));
            }
        }
        text.append("     actual class\n");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                text.append(" ").//append(""+matrix[i][j]);
                        append(doubleToString(matrix[i][j], IDWidth, (fractional ? 2 : 0)));
            }
            text.append(" | ").append(num2ShortID(i, IDChars, IDWidth)).append(" = ").append("Val of class\n");

            //       num2ShortID(i, IDChars, IDWidth)).append(" = ")
            //       .append(m_ClassNames[i]).append("\n");
        }
        return text.toString();

    }

    /**
     * Method for generating indices for the confusion matrix.
     * Borrowed from weka
     * @param num integer to format
     * @return the formatted integer as a string
     */
    private static String num2ShortID(int num, char[] IDChars, int IDWidth) {

        char ID[] = new char[IDWidth];
        int i;

        for (i = IDWidth - 1; i >= 0; i--) {
            ID[i] = IDChars[num % IDChars.length];
            num = num / IDChars.length - 1;
            if (num < 0) {
                break;
            }
        }
        for (i--; i >= 0; i--) {
            ID[i] = ' ';
        }

        return new String(ID);
    }

    //Borrowed From Weka
    public static String doubleToString(double value,
                                 int afterDecimalPoint) {
        DecimalFormat DF = new DecimalFormat();
        DF.setMaximumFractionDigits(afterDecimalPoint);
        return DF.format(value);
    }
    //Borrowed From Weka
    public static String doubleToString(double value, int width,
                                 int afterDecimalPoint) {

        String tempString = doubleToString(value, afterDecimalPoint);
        char[] result;
        int dotPosition;

        if (afterDecimalPoint >= width) {
            return tempString;
        }

        // Initialize result
        result = new char[width];
        for (int i = 0; i < result.length; i++) {
            result[i] = ' ';
        }

        if (afterDecimalPoint > 0) {
            // Get position of decimal point and insert decimal point
            dotPosition = tempString.indexOf('.');
            if (dotPosition == -1) {
                dotPosition = tempString.length();
            } else {
                result[width - afterDecimalPoint - 1] = '.';
            }
        } else {
            dotPosition = tempString.length();
        }

        int offset = width - afterDecimalPoint - dotPosition;
        if (afterDecimalPoint > 0) {
            offset--;
        }

        // Not enough room to decimal align within the supplied width
        if (offset < 0) {
            return tempString;
        }

        // Copy characters before decimal point
        for (int i = 0; i < dotPosition; i++) {
            result[offset + i] = tempString.charAt(i);
        }

        // Copy characters after decimal point
        for (int i = dotPosition + 1; i < tempString.length(); i++) {
            result[offset + i] = tempString.charAt(i);
        }

        return new String(result);
    }
}
