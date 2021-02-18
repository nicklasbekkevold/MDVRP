package main.java;

import java.util.*;

public class Util {

    private static final Random random = new Random();


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

}
