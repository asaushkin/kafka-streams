package utils;

public class SplitWords {
    public static String[] toWords(String input) {
        if (input == null || input.trim().length() == 0)
            return null;

        return input.split(",");
    }
}
