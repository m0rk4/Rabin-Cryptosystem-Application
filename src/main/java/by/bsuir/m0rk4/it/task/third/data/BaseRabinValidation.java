package by.bsuir.m0rk4.it.task.third.data;

import java.math.BigInteger;

public abstract class BaseRabinValidation {

    private BaseRabinValidation successor;

    public boolean validate(BigInteger p, BigInteger q, BigInteger b) throws RabinInvalidDataException {
        return execute(p, q, b) && (successor == null || successor.validate(p, q, b));
    }

    protected abstract boolean execute(BigInteger p, BigInteger q, BigInteger b) throws RabinInvalidDataException;

    public void setSuccessor(BaseRabinValidation successor) {
        this.successor = successor;
    }

    public BaseRabinValidation getSuccessor() {
        return successor;
    }
}
