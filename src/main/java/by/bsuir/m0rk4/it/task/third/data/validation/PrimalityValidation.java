package by.bsuir.m0rk4.it.task.third.data.validation;

import by.bsuir.m0rk4.it.task.third.data.BaseRabinValidation;
import by.bsuir.m0rk4.it.task.third.data.exception.RabinInvalidDataException;
import by.bsuir.m0rk4.it.task.third.data.primetesting.PrimeTester;

import java.math.BigInteger;

public class PrimalityValidation extends BaseRabinValidation {
    private final PrimeTester primeTester;

    public PrimalityValidation(PrimeTester primeTester) {
        this.primeTester = primeTester;
    }

    @Override
    protected boolean execute(BigInteger p, BigInteger q) throws RabinInvalidDataException {
        boolean isPPrime = primeTester.test(p);
        boolean isQPrime = primeTester.test(q);
        if (isPPrime && isQPrime) {
            return true;
        }
        throw new RabinInvalidDataException(
                (isPPrime ? "" : "p failed primality test.\n") +
                        (isQPrime ? "" : "q failed primality test.")
        );
    }
}
