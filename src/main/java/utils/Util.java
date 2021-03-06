package main.java.utils;

import javafx.scene.paint.Color;

import java.util.*;

public class Util {

    public static final Random random = new Random();

    /**
     * @param list list to pick 2 elements from
     * @param <E> type of the list
     * @return Symmetric pair of 2 random elements from list
     */
    public static <E> SymmetricPair<E> randomPair(final List<E> list) {
        E first = list.get(random.nextInt(list.size()));
        E second = list.get(random.nextInt(list.size()));
        while (first.equals(second)) {
            second = list.get(random.nextInt(list.size()));
        }
        return new SymmetricPair<>(first, second);
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
