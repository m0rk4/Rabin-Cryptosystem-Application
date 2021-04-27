package by.bsuir.m0rk4.it.task.third.data.parser;

import by.bsuir.m0rk4.it.task.third.data.exception.RabinInvalidDataException;

import java.math.BigInteger;

public class RabinDataParser {
    public BigInteger parse(String pStr) throws RabinInvalidDataException {
        try {
            return new BigInteger(pStr);
        } catch (NumberFormatException e) {
            throw new RabinInvalidDataException(String.format("Invalid number representation. (%s)", pStr), e.getCause());
        }
    }
}
