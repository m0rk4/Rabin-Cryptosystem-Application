package by.bsuir.m0rk4.it.task.third.data.validation;

import by.bsuir.m0rk4.it.task.third.data.BaseRabinValidation;
import by.bsuir.m0rk4.it.task.third.data.RabinInvalidDataException;

import java.math.BigInteger;

public class RabinBRangeValidation extends BaseRabinValidation {
    @Override
    protected boolean execute(BigInteger p, BigInteger q, BigInteger b) throws RabinInvalidDataException {
        BigInteger n = p.multiply(q);
        if (b.compareTo(n) > -1) {
            throw new RabinInvalidDataException("Impossible to get unique message. Crypto-level is low. n must be < 255.");
        }
        return true;
    }
}
