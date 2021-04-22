package by.bsuir.m0rk4.it.task.third.primetesting;

import java.math.BigInteger;

import static by.bsuir.m0rk4.it.task.third.algorithm.Algorithms.*;

public class PrimeTester {
    public boolean test(BigInteger value) {
        return true;
    }

    public boolean testRemainderMod4(BigInteger value) {
        return value.mod(FOUR).compareTo(THREE) == 0;
    }
}
