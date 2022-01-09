package src.server.auth;

import java.util.Random;

public class HandShake {
    Random random = new Random();

    private final String expression;
    private final int solution;

    public HandShake(){
        int number1, number2, number3;
        int result;
        do{
            number1 = random.nextInt(20);
            number2 = random.nextInt(20);
            number3 = random.nextInt(20);
            result = number1 + number2 - number3;
        }while (result < 0);

        this.expression = "" + number1 + " + " + number2 + " - " + number3;
        this.solution = result;
    }

    public String getExpression() {
        return expression;
    }

    public int getSolution() {
        return solution;
    }
}
