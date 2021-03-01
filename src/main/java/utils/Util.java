package main.java.utils;

import javafx.scene.paint.Color;

import java.util.*;

public class Util {

    public static final Random random = new Random();

    /**
     * @param list list to pick n elements from
     * @param n number of elements to choose from
     * @param <E> type of the list
     * @return list of n random elements from list
     * This code was grabbed from a StackOverflow thread:
     * @see <a href="https://stackoverflow.com/questions/4702036/take-n-random-elements-from-a-liste">https://stackoverflow.com/</a>
     */
    public static <E> List<E> randomChoice(final List<E> list, int n) {
        int length = list.size();

        if (length < n) return null;

        for (int i = length - 1; i >= length - n; --i) {
            Collections.swap(list, i , random.nextInt(i + 1));
        }
        return list.subList(length - n, length);
    }

    /**
     * @param numberOfColors the number of distinct colors to produce
     * @return color iterator object with numberOfColors distinct colors
     * This is based on a linear change in hue with alternating dark and light colors.
     */
    public static Iterator<Color> distinctColors(int numberOfColors) {
        List<Color> colors = new ArrayList<>();
        double step = 180;
        double offset = 360/numberOfColors;
        for (double i = 0; i < numberOfColors; i++) {
            if (i / 2 % 2 == 0.0) {
                colors.add(Color.hsb((i * (step + offset) % 360), 1, 1));
            } else {
                colors.add(Color.hsb((i * (step + offset) % 360), 1, 0.5));
            }
        }
        return colors.iterator();
    }

}
