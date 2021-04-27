package by.bsuir.m0rk4.it.task.third.data;

import by.bsuir.m0rk4.it.task.third.data.exception.RabinInvalidDataException;
import by.bsuir.m0rk4.it.task.third.data.parser.RabinDataParser;
import by.bsuir.m0rk4.it.task.third.data.primetesting.PrimeTester;
import by.bsuir.m0rk4.it.task.third.data.validation.PrimalityValidation;
import by.bsuir.m0rk4.it.task.third.data.validation.RabinMessageRangeValidation;
import by.bsuir.m0rk4.it.task.third.data.validation.RabinMod4Validation;
import by.bsuir.m0rk4.it.task.third.data.validation.RabinModuleMinRangeValidation;

import java.math.BigInteger;

public class RabinNumSourceDataValidator {
    private final RabinDataParser dataParser;
    private final PrimeTester primeTester;

    public RabinNumSourceDataValidator(RabinDataParser dataParser, PrimeTester primeTester) {
        this.dataParser = dataParser;
        this.primeTester = primeTester;
    }

    public boolean processData(String pStr, String qStr, String bStr, String mStr) throws RabinInvalidDataException {
        BigInteger p = dataParser.parse(pStr);
        BigInteger q = dataParser.parse(qStr);
        BigInteger b = dataParser.parse(bStr);
        BigInteger m = dataParser.parse(mStr);

        BaseRabinValidation primalityValidation = new PrimalityValidation(primeTester);
        BaseRabinValidation rabinModValidation = new RabinMod4Validation(primeTester);
        BaseRabinValidation rabinModRangeValidation = new RabinModuleMinRangeValidation();
        BaseRabinValidation rabinBRangeValidation = new RabinMessageRangeValidation(b, "b");
        BaseRabinValidation rabinMessageRangeValidation = new RabinMessageRangeValidation(m, "m");

        primalityValidation.setSuccessor(rabinModValidation);
        rabinModValidation.setSuccessor(rabinModRangeValidation);
        rabinModRangeValidation.setSuccessor(rabinBRangeValidation);
        rabinBRangeValidation.setSuccessor(rabinMessageRangeValidation);

        return primalityValidation.validate(p, q);
    }
}
