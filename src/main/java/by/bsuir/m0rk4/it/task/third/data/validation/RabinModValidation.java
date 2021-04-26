package by.bsuir.m0rk4.it.task.third.data.validation;

import by.bsuir.m0rk4.it.task.third.data.BaseRabinValidation;
import by.bsuir.m0rk4.it.task.third.data.RabinInvalidDataException;
import by.bsuir.m0rk4.it.task.third.data.primetesting.PrimeTester;

import java.math.BigInteger;

public class RabinModValidation extends BaseRabinValidation {
    private final PrimeTester primeTester;

    public RabinModValidation(PrimeTester primeTester) {
        this.primeTester = primeTester;
    }

    @Override
    protected boolean execute(BigInteger p, BigInteger q, BigInteger b) throws RabinInvalidDataException {
        boolean isPModRabin = primeTester.testRemainderMod4(p);
        boolean isQModRabin = primeTester.testRemainderMod4(q);
        if (isPModRabin && isQModRabin) {
            return true;
        }
        throw new RabinInvalidDataException(
                (isPModRabin ? "" : "p failed Rabin mod 4 test.\n") +
                        (isQModRabin ? "" : "q failed Rabin mod 4 test.")
        );
    }
}
