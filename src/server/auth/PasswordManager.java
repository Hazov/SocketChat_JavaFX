package src.server.auth;

public class PasswordManager {
    static int MATRIX_SIDE = 4;
    static int[] key = {1, 3, 0, 2};


    public static String encodePassword(String pass) {
        return encodeTMatrix(tMatrix(passToMatrix(pass)), key);
    }

    public static String decodePassword(String pass) {
        return matrixToPass(tMatrix(decodeTMatrix(pass, key)));
    }

    private static char[][] passToMatrix(String hash) {
        String hashString = String.valueOf(hash);
        char[][] matrix = new char[MATRIX_SIDE][MATRIX_SIDE];
        int count = 0;
        char currentChar;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (count < hashString.length()) {
                    currentChar = hashString.charAt(count);
                    count++;
                } else {
                    currentChar = '#';
                }
                matrix[i][j] = currentChar;
            }
        }
        return matrix;
    }

    private static char[][] tMatrix(char[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i + 1; j < matrix[0].length; j++) {
                char temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }
        return matrix;
    }

    private static String encodeTMatrix(char[][] matrix, int[] key) {
        StringBuilder encodePass = new StringBuilder();
        for (int k : key) {
            for (int j = 0; j < matrix.length; j++) {
                encodePass.append(matrix[k][j]);
            }
        }
        return encodePass.toString();
    }


    private static char[][] decodeTMatrix(String encodePass, int[] key) {
        char[][] matrix = new char[MATRIX_SIDE][MATRIX_SIDE];
        int count = 0;
        for (int k : key) {
            for (int j = 0; j < MATRIX_SIDE; j++) {
                matrix[k][j] = encodePass.charAt(count);
                count++;
            }
        }
        return matrix;
    }

    private static String matrixToPass(char[][] matrix) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < MATRIX_SIDE; i++) {
            for (int j = 0; j < MATRIX_SIDE; j++) {
                s.append(matrix[i][j]);
            }
        }
        return s.toString();
    }
}