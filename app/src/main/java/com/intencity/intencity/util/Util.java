package com.intencity.intencity.util;

import java.util.Random;

/**
 * Created by nickpiscopio on 12/17/15.
 */
public class Util
{
    private static final String ALLOWED_CHARACTERS = "0123456789";

    public static int getRandomId()
    {
        return new Random().nextInt(1000 + 1);
    }
}
