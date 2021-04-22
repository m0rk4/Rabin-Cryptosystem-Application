package by.bsuir.m0rk4.it.task.third.primetesting;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static by.bsuir.m0rk4.it.task.third.algorithm.Algorithms.*;

public class PrimeTester {
    public boolean test(BigInteger value, int certainty) {
        if (value.equals(TWO))
            return true;
        if (!value.testBit(0) || value.equals(ONE))
            return false;

        int rounds = 0;
        int n = (Math.min(certainty, Integer.MAX_VALUE - 1) + 1) / 2;

        // The relationship between the certainty and the number of rounds
        // we perform is given in the draft standard ANSI X9.80, "PRIME
        // NUMBER GENERATION, PRIMALITY TESTING, AND PRIMALITY CERTIFICATES".
        int sizeInBits = value.bitLength();
        if (sizeInBits < 100) {
            rounds = 50;
            rounds = Math.min(n, rounds);
            return passesMillerRabin(value, rounds);
        }

        if (sizeInBits < 256) {
            rounds = 27;
        } else if (sizeInBits < 512) {
            rounds = 15;
        } else if (sizeInBits < 768) {
            rounds = 8;
        } else if (sizeInBits < 1024) {
            rounds = 4;
        } else {
            rounds = 2;
        }
        rounds = Math.min(n, rounds);

        return passesMillerRabin(value, rounds);
    }

    private boolean passesMillerRabin(BigInteger value, int iterations) {
        // Find a and m such that m is odd and this == 1 + 2**a * m
        BigInteger thisMinusOne = value.subtract(ONE);
        BigInteger m = thisMinusOne;
        int a = m.getLowestSetBit();
        m = m.shiftRight(a);

        // Do the tests
        Random rnd = ThreadLocalRandom.current();
        for (int i = 0; i < iterations; i++) {
            // Generate a uniform random on (1, this)
            BigInteger b;
            do {
                b = new BigInteger(value.bitLength(), rnd);
            } while (b.compareTo(ONE) <= 0 || b.compareTo(value) >= 0);

            int j = 0;
            BigInteger z = b.modPow(m, value);
            while (!((j == 0 && z.equals(ONE)) || z.equals(thisMinusOne))) {
                if (j > 0 && z.equals(ONE) || ++j == a)
                    return false;
                z = z.modPow(TWO, value);
            }
        }
        return true;
    }


    public boolean testRemainderMod4(BigInteger value) {
        return value.mod(FOUR).equals(THREE);
    }
}
