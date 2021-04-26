package by.bsuir.m0rk4.it.task.third.data;

import by.bsuir.m0rk4.it.task.third.data.parser.RabinDataParser;
import by.bsuir.m0rk4.it.task.third.data.primetesting.PrimeTester;
import by.bsuir.m0rk4.it.task.third.data.validation.PrimalityValidation;
import by.bsuir.m0rk4.it.task.third.data.validation.RabinBRangeValidation;
import by.bsuir.m0rk4.it.task.third.data.validation.RabinModRangeValidation;
import by.bsuir.m0rk4.it.task.third.data.validation.RabinModValidation;

import java.math.BigInteger;

public class RabinDataDirector {
    private final RabinDataParser dataParser;
    private final PrimeTester primeTester;

    public RabinDataDirector(RabinDataParser dataParser, PrimeTester primeTester) {
        this.dataParser = dataParser;
        this.primeTester = primeTester;
    }

    public boolean processData(String pStr, String qStr, String bStr) throws RabinInvalidDataException {
        BigInteger p = dataParser.parse(pStr);
        BigInteger q = dataParser.parse(qStr);
        BigInteger b = dataParser.parse(bStr);

        BaseRabinValidation primalityValidation = new PrimalityValidation(primeTester);
        BaseRabinValidation rabinModValidation = new RabinModValidation(primeTester);
        BaseRabinValidation rabinModRangeValidation = new RabinModRangeValidation();
        BaseRabinValidation rabinBRangeValidation = new RabinBRangeValidation();

        primalityValidation.setSuccessor(rabinModValidation);
        rabinModValidation.setSuccessor(rabinModRangeValidation);
        rabinModRangeValidation.setSuccessor(rabinBRangeValidation);

        return primalityValidation.validate(p, q, b);
    }
}
