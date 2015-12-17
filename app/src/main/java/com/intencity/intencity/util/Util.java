package com.intencity.intencity.util;

import java.util.Random;

/**
 * Created by nickpiscopio on 12/17/15.
 */
public class Util
{
    public static int getRandomId()
    {
        return new Random().nextInt(1000 + 1);
    }
}
