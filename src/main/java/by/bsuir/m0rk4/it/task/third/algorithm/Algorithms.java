package by.bsuir.m0rk4.it.task.third.algorithm;

import by.bsuir.m0rk4.it.task.third.model.BigIntegerRef;

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

    public static BigInteger extEuclid(BigInteger p, BigInteger q, BigIntegerRef yPRef, BigIntegerRef yQRef) {
        if (p.equals(ZERO)) {
            yPRef.setVal(ZERO);
            yQRef.setVal(ONE);
            return ONE;
        }
        BigIntegerRef yP1Ref = new BigIntegerRef();
        BigIntegerRef yQ1Ref = new BigIntegerRef();
        BigInteger gcd = extEuclid(q.mod(p), p, yP1Ref, yQ1Ref);
        BigInteger tmp = q.divide(p).multiply(yP1Ref.getVal());
        yPRef.setVal(yQ1Ref.getVal().subtract(tmp));
        yQRef.setVal(yP1Ref.getVal());
        return gcd;
    }

    public static BigInteger CRT(BigInteger aP, BigInteger aQ, BigInteger p, BigInteger q) {
        BigIntegerRef y1Inv = new BigIntegerRef();
        BigIntegerRef ignored = new BigIntegerRef();
        extEuclid(q, p, y1Inv, ignored);
        BigInteger val1 = y1Inv.getVal();
        BigInteger z1 = val1.mod(p).add(p).mod(p);
        BigIntegerRef y2Inv = new BigIntegerRef();
        ignored = new BigIntegerRef();
        extEuclid(p, q, y2Inv, ignored);
        BigInteger val2 = y2Inv.getVal();
        BigInteger z2 = val2.mod(q).add(q).mod(q);
        return (aP.multiply(q).multiply(z1)).add(aQ.multiply(p).multiply(z2)).mod(p.multiply(q));
    }
}
