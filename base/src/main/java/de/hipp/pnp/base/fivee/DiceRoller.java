package de.hipp.pnp.base.fivee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DiceRoller {

    private static Random random = new Random();
    private DiceRoller(){

    }

    public static int roll(int numberOfDice, int diceSides) {
        int returnValue = 0;
        int min = 1;

        for (int i = 0; i < numberOfDice; i++) {
            returnValue += random.nextInt(diceSides - min + 1) + min;
        }

        return returnValue;
    }

    public static int roll(int numberOfDice, int diceSides, int keep, boolean highest) {
        List<Integer> returnList = new ArrayList<>();
        for (int i = 0; i < numberOfDice; i++) {
            returnList.add(roll(1, diceSides));
        }
        if (highest) {
            Collections.sort(returnList);
        } else {
            returnList.sort(Collections.reverseOrder());
        }

        int returnValue = 0;

        for (int j = 0; j <= keep; j++) {
            returnValue += returnList.get(j);
        }

        return returnValue;
    }
}
