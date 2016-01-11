package com.intencity.intencity.util;

import java.util.Random;

/**
 * A static class of Utils.
 *
 * Created by Nick Piscopio on 12/17/15.
 */
public class Util
{
    /**
     * Generate a random number between two values.
     *
     * @param min   The minimum number.
     * @param max   The maximum number.
     *
     * @return  The random number.
     */
    public static int getRandom(int min, int max)
    {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return new Random().nextInt((max - min) + 1) + min;
    }
}
