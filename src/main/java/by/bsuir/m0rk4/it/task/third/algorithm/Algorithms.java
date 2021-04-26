package by.bsuir.m0rk4.it.task.third.algorithm;

import by.bsuir.m0rk4.it.task.third.entity.ExtGcdResult;

import java.math.BigInteger;

public class Algorithms {

    public static final BigInteger ZERO = BigInteger.ZERO;
    public static final BigInteger ONE = BigInteger.ONE;
    public static final BigInteger TWO = BigInteger.TWO;
    public static final BigInteger THREE = BigInteger.valueOf(3);
    public static final BigInteger FOUR = BigInteger.valueOf(4);
    public static final BigInteger FIVE = BigInteger.valueOf(5);
    public static final BigInteger EIGHT = BigInteger.valueOf(8);

    public static BigInteger binPow(BigInteger base, BigInteger exp, BigInteger mod) {
        base = base.mod(mod);
        BigInteger result = ONE;
        while (!exp.equals(ZERO)) {
            while (exp.mod(TWO).equals(ZERO)) {
                exp = exp.shiftRight(1);
                base = base.multiply(base).mod(mod);
            }
            exp = exp.subtract(ONE);
            result = result.multiply(base).mod(mod);
        }
        return result;
    }

    public static BigInteger getJacobi(BigInteger a, BigInteger n) {
        a = a.mod(n);
        BigInteger t = ONE;
        while (!a.equals(ZERO)) {
            while (a.mod(TWO).equals(ZERO)) {
                a = a.divide(TWO);
                BigInteger r = n.mod(EIGHT);
                if (r.equals(THREE) || r.equals(FIVE)) {
                    t = t.negate();
                }
            }
            BigInteger tmp = n;
            n = a;
            a = tmp;
            if (n.mod(FOUR).equals(THREE) && a.mod(FOUR).equals(THREE)) {
                t = t.negate();
            }
            a = a.mod(n);
        }
        if (n.equals(ONE)) {
            return t;
        }
        return ZERO;
    }

    public static ExtGcdResult extGcd(BigInteger a, BigInteger b) {
        BigInteger x = ZERO, y = ONE;
        BigInteger lastX = ONE, lastY = ZERO;
        BigInteger tmp;
        while (!b.equals(ZERO)) {
            BigInteger q = a.divide(b);
            BigInteger r = a.mod(b);

            a = b;
            b = r;

            tmp = x;
            x = lastX.subtract(q.multiply(x));
            lastX = tmp;

            tmp = y;
            y = lastY.subtract(q.multiply(y));
            lastY = tmp;
        }
        return new ExtGcdResult(lastX, lastY, a);
    }

    public static BigInteger CRT(BigInteger aP, BigInteger aQ, BigInteger p, BigInteger q) {
        BigInteger z1 = ((extGcd(q, p).getX1().mod(p)).add(p)).mod(p);
        BigInteger z2 = ((extGcd(p, q).getX1().mod(q)).add(q)).mod(q);
        return (aP.multiply(q).multiply(z1)).add(aQ.multiply(p).multiply(z2)).mod(p.multiply(q));
    }
}
