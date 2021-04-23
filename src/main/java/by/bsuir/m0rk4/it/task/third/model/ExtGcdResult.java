package by.bsuir.m0rk4.it.task.third.model;

import java.math.BigInteger;

public class ExtGcdResult {
    private final BigInteger x1;
    private final BigInteger y1;
    private final BigInteger gcd;

    public ExtGcdResult(BigInteger x1, BigInteger y1, BigInteger gcd) {
        this.x1 = x1;
        this.y1 = y1;
        this.gcd = gcd;
    }

    public BigInteger getGcd() {
        return gcd;
    }

    public BigInteger getX1() {
        return x1;
    }

    public BigInteger getY1() {
        return y1;
    }
}
