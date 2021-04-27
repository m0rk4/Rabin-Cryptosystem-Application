package by.bsuir.m0rk4.it.task.third.data;

import by.bsuir.m0rk4.it.task.third.data.exception.RabinInvalidDataException;

import java.math.BigInteger;

public abstract class BaseRabinValidation {

    private BaseRabinValidation successor;

    public boolean validate(BigInteger p, BigInteger q) throws RabinInvalidDataException {
        return execute(p, q) && (successor == null || successor.validate(p, q));
    }

    protected abstract boolean execute(BigInteger p, BigInteger q) throws RabinInvalidDataException;

    public void setSuccessor(BaseRabinValidation successor) {
        this.successor = successor;
    }
}
