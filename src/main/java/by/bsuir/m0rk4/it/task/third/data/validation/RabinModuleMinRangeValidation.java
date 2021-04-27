package by.bsuir.m0rk4.it.task.third.data.validation;

import by.bsuir.m0rk4.it.task.third.data.BaseRabinValidation;
import by.bsuir.m0rk4.it.task.third.data.exception.RabinInvalidDataException;

import java.math.BigInteger;

public class RabinModuleMinRangeValidation extends BaseRabinValidation {
    private static final BigInteger BYTE_MAXVALUE = BigInteger.valueOf(255);

    @Override
    protected boolean execute(BigInteger p, BigInteger q) throws RabinInvalidDataException {
        BigInteger n = p.multiply(q);
        if (n.compareTo(BYTE_MAXVALUE) < 1) {
            throw new RabinInvalidDataException("b value is >= n.");
        }
        return true;
    }
}
