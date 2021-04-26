package by.bsuir.m0rk4.it.task.third.entity;

import java.math.BigInteger;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtGcdResult that = (ExtGcdResult) o;
        return Objects.equals(x1, that.x1) &&
                Objects.equals(y1, that.y1) &&
                Objects.equals(gcd, that.gcd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x1, y1, gcd);
    }

    @Override
    public String toString() {
        return "ExtGcdResult{" +
                "x1=" + x1 +
                ", y1=" + y1 +
                ", gcd=" + gcd +
                '}';
    }
}
