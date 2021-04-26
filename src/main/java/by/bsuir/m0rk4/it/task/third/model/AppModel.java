package by.bsuir.m0rk4.it.task.third.model;

import by.bsuir.m0rk4.it.task.third.crypto.RabinCryptoSystem;
import by.bsuir.m0rk4.it.task.third.crypto.RabinProcessor;
import by.bsuir.m0rk4.it.task.third.data.RabinDataDirector;
import by.bsuir.m0rk4.it.task.third.data.RabinInvalidDataException;
import by.bsuir.m0rk4.it.task.third.data.parser.RabinDataParser;
import by.bsuir.m0rk4.it.task.third.data.primetesting.PrimeTester;
import javafx.concurrent.Task;
import javafx.scene.layout.BackgroundImage;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

public class AppModel {
    private final PrimeTester primeTester;
    private final RabinDataParser rabinDataParser;
    private final RabinDataDirector rabinDataDirector;
    private final RabinCryptoSystem rabinCryptoSystem;
    private final RabinProcessor rabinProcessor;

    public AppModel(PrimeTester primeTester, RabinDataParser rabinDataParser,
                    RabinCryptoSystem rabinCryptoSystem, RabinProcessor rabinProcessor) {
        this.primeTester = primeTester;
        this.rabinDataParser = rabinDataParser;
        this.rabinDataDirector = new RabinDataDirector(rabinDataParser, primeTester);
        this.rabinCryptoSystem = rabinCryptoSystem;
        this.rabinProcessor = rabinProcessor;
    }

    public boolean validate(String pStr, String qStr, String bStr) throws RabinInvalidDataException {
        return rabinDataDirector.processData(pStr, qStr, bStr);
    }

    public Task<List<String>> encryptFile(File inputFile, File outputFile, BigInteger b, BigInteger n) {
        return rabinProcessor.encrypt(inputFile, outputFile, b, n);
    }
}
