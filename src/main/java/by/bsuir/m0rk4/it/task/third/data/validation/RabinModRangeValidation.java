package by.bsuir.m0rk4.it.task.third.data.validation;

import by.bsuir.m0rk4.it.task.third.data.BaseRabinValidation;
import by.bsuir.m0rk4.it.task.third.data.RabinInvalidDataException;

import java.math.BigInteger;

public class RabinModRangeValidation extends BaseRabinValidation {
    private static final BigInteger BYTE_MAXVALUE = BigInteger.valueOf(255);

    @Override
    protected boolean execute(BigInteger p, BigInteger q, BigInteger b) throws RabinInvalidDataException {
        BigInteger n = p.multiply(q);
        if (n.compareTo(BYTE_MAXVALUE) < 1) {
            throw new RabinInvalidDataException("b value is >= n.");
        }
        return true;
    }
}
