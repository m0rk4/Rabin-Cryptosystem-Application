package by.bsuir.m0rk4.it.task.third.model;

import by.bsuir.m0rk4.it.task.third.crypto.RabinFileProcessor;
import by.bsuir.m0rk4.it.task.third.crypto.RabinNumberProcessor;
import by.bsuir.m0rk4.it.task.third.data.RabinFileSourceDataValidator;
import by.bsuir.m0rk4.it.task.third.data.RabinNumSourceDataValidator;
import by.bsuir.m0rk4.it.task.third.data.exception.RabinInvalidDataException;
import by.bsuir.m0rk4.it.task.third.entity.ResultModel;
import javafx.concurrent.Task;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

public class AppModel {
    private final RabinFileSourceDataValidator rabinFileSourceDataValidator;
    private final RabinNumSourceDataValidator rabinNumSourceDataValidator;
    private final RabinFileProcessor rabinFileProcessor;
    private final RabinNumberProcessor rabinNumberProcessor;

    public AppModel(
            RabinFileSourceDataValidator rabinFileSourceDataValidator,
            RabinNumSourceDataValidator rabinNumSourceDataValidator,
            RabinFileProcessor rabinFileProcessor,
            RabinNumberProcessor rabinNumberProcessor) {
        this.rabinFileSourceDataValidator = rabinFileSourceDataValidator;
        this.rabinNumSourceDataValidator = rabinNumSourceDataValidator;
        this.rabinFileProcessor = rabinFileProcessor;
        this.rabinNumberProcessor = rabinNumberProcessor;
    }

    public void validateFileSource(String pStr, String qStr, String bStr) throws RabinInvalidDataException {
        rabinFileSourceDataValidator.processData(pStr, qStr, bStr);
    }

    public void validateNumberSource(String pStr, String qStr, String bStr, String mStr) throws RabinInvalidDataException {
        rabinNumSourceDataValidator.processData(pStr, qStr, bStr, mStr);
    }

    public Task<List<ResultModel>> encryptFile(File inputFile, File outputFile, BigInteger b, BigInteger n) {
        return rabinFileProcessor.encrypt(inputFile, outputFile, b, n);
    }

    public Task<List<ResultModel>> decryptFile(
            File inputFile, File outputFile, BigInteger b, BigInteger module, BigInteger p, BigInteger q) {
        return rabinFileProcessor.decrypt(inputFile, outputFile, b, module, p, q);
    }

    public Task<List<ResultModel>> encryptNumber(BigInteger m, BigInteger b, BigInteger n) {
        return rabinNumberProcessor.encrypt(m, b, n);
    }

    public Task<List<ResultModel>> decryptNumber(
            BigInteger p,
            BigInteger q,
            BigInteger b,
            BigInteger n,
            int jacobi,
            int evenOdd,
            BigInteger c
    ) {
        return rabinNumberProcessor.decrypt(p, q, b, n, jacobi, evenOdd, c);
    }
}
