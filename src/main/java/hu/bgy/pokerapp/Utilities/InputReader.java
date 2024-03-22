package hu.bgy.pokerapp.Utilities;
import java.util.Scanner;

public class InputReader {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static Integer readInteger() {
        return Integer.parseInt(readString());
    }

    public static String readString() {
        return SCANNER.nextLine();
    }

    public static String readString(String message) {
        System.out.println(message);
        return SCANNER.nextLine();
    }

    public static Integer getBet() {
        Integer bet = readInteger();
        return bet;
    }

}

