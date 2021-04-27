package by.bsuir.m0rk4.it.task.third.data.validation;

import by.bsuir.m0rk4.it.task.third.data.BaseRabinValidation;
import by.bsuir.m0rk4.it.task.third.data.exception.RabinInvalidDataException;

import java.math.BigInteger;

public class RabinMessageRangeValidation extends BaseRabinValidation {
    private final BigInteger message;
    private final String id;

    public RabinMessageRangeValidation(BigInteger message, String id) {
        this.message = message;
        this.id = id;
    }

    @Override
    protected boolean execute(BigInteger p, BigInteger q) throws RabinInvalidDataException {
        BigInteger n = p.multiply(q);
        if (message.compareTo(n) > -1) {
            throw new RabinInvalidDataException(String.format("%s value is >= n.", id));
        }
        return true;
    }
}
