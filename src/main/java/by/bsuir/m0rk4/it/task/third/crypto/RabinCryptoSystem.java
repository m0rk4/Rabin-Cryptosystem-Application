package by.bsuir.m0rk4.it.task.third.crypto;

import by.bsuir.m0rk4.it.task.third.algorithm.Algorithms;
import by.bsuir.m0rk4.it.task.third.entity.ExtGcdResult;

import java.math.BigInteger;

import static by.bsuir.m0rk4.it.task.third.algorithm.Algorithms.*;

public class RabinCryptoSystem {

    public BigInteger encryptNumber(BigInteger m, BigInteger b, BigInteger n) {
        BigInteger mPlusB = m.add(b).mod(n);
        BigInteger mMultVal = m.multiply(mPlusB);
        return mMultVal.mod(n);
    }

    public BigInteger getDecryptedMessage(
            BigInteger p,
            BigInteger q,
            BigInteger b,
            BigInteger n,
            int jacobi,
            int evenOdd,
            BigInteger c
    ) {
        BigInteger c4 = c.multiply(FOUR).mod(n);
        BigInteger bSqr = b.multiply(b).mod(n);
        BigInteger discriminant = bSqr.add(c4).mod(n);

        BigInteger pPow = p.add(ONE).divide(FOUR);
        BigInteger qPow = q.add(ONE).divide(FOUR);
        BigInteger mP = binPow(discriminant, pPow, p);
        BigInteger mQ = binPow(discriminant, qPow, q);

        if (!(mP.multiply(mP).mod(p)).equals(discriminant.mod(p))
                || !(mQ.multiply(mQ).mod(q)).equals(discriminant.mod(q))) {
            System.out.println("Failed check p");
            System.exit(1);
        }

        ExtGcdResult extGcdResult = extGcd(p, q);
        BigInteger t1 = extGcdResult.getX1().multiply(p).mod(n).multiply(mQ).mod(n);
        BigInteger t2 = extGcdResult.getY1().multiply(q).mod(n).multiply(mP).mod(n);

        BigInteger root1 = t1.add(t2).mod(n);
        BigInteger root4 = n.subtract(root1);
        BigInteger root2 = t1.subtract(t2).mod(n);
        BigInteger root3 = n.subtract(root2);

        BigInteger message;
        if (jacobi == 1) {
            if (evenOdd == root1.and(ONE).intValue()) {
                message = getPossibleRoot(b, root1, n);
            } else {
                message = getPossibleRoot(b, root4, n);
            }
        } else {
            if (evenOdd == root2.and(ONE).intValue()) {
                message = getPossibleRoot(b, root2, n);
            } else {
                message = getPossibleRoot(b, root3, n);
            }
        }
        return message;
    }

    public byte getMetaByte(BigInteger b, BigInteger module, BigInteger m) {
        BigInteger futureRoot = m.multiply(TWO).mod(module);
        futureRoot = futureRoot.add(b).mod(module);

        byte meta = 0;
        int jacobi = (Algorithms.getJacobi(futureRoot, module).intValue() + 1) >> 1;
        int evenOdd = futureRoot.and(ONE).intValue();
        meta ^= (-jacobi ^ meta) & 2;
        meta ^= (-evenOdd ^ meta) & 1;
        return meta;
    }

    private BigInteger getPossibleRoot(BigInteger b, BigInteger discriminantSqrt, BigInteger n) {
        BigInteger subtraction = discriminantSqrt.subtract(b);
        if (!subtraction.and(ONE).equals(ZERO)) {
            subtraction = subtraction.add(n);
        }
        return subtraction.divide(TWO).mod(n);
    }
}
