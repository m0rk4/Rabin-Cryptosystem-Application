package by.bsuir.m0rk4.it.task.third.crypto;

import by.bsuir.m0rk4.it.task.third.entity.ResultBuilder;
import by.bsuir.m0rk4.it.task.third.entity.ResultModel;
import javafx.concurrent.Task;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

public class RabinNumberProcessor {
    private final RabinCryptoSystem rabinCryptoSystem;
    private final ResultBuilder resultBuilder;

    public RabinNumberProcessor(RabinCryptoSystem rabinCryptoSystem) {
        this.rabinCryptoSystem = rabinCryptoSystem;
        this.resultBuilder = new ResultBuilder();
    }

    public Task<List<ResultModel>> encrypt(BigInteger message, BigInteger b, BigInteger module) {
        return new Task<>() {
            @Override
            protected List<ResultModel> call() {
                resultBuilder.reset();
                BigInteger encryptedNumber = rabinCryptoSystem.getEncryptedMessage(message, b, module);
                byte metaByte = rabinCryptoSystem.getMetaByte(b, module, message);
                int jacobi = (metaByte >> 1) & 1;
                int evenOdd = metaByte & 1;
                ResultModel resultModel = resultBuilder
                        .buildHexSource(message.toString(16))
                        .buildDecimalSource(message.toString())
                        .buildOperationType("Encryption")
                        .buildDecimalResult(encryptedNumber.toString())
                        .buildHexResult(encryptedNumber.toString(16))
                        .buildMeta(String.format("root discriminant - jacobi: %d; %s", jacobi, evenOdd == 1 ? "Odd" : "Even"))
                        .getResultModel();
                return Collections.singletonList(resultModel);
            }
        };
    }

    public Task<List<ResultModel>> decrypt(
            BigInteger p,
            BigInteger q,
            BigInteger b,
            BigInteger n,
            int jacobi,
            int evenOdd,
            BigInteger c
    ) {
        return new Task<>() {
            @Override
            protected List<ResultModel> call() {
                resultBuilder.reset();
                BigInteger decryptedMessage = rabinCryptoSystem.getDecryptedMessage(p, q, b, n, jacobi, evenOdd, c);
                ResultModel resultModel = resultBuilder
                        .buildHexSource(c.toString(16))
                        .buildDecimalSource(c.toString())
                        .buildOperationType("Decryption")
                        .buildDecimalResult(decryptedMessage.toString())
                        .buildHexResult(decryptedMessage.toString(16))
                        .buildMeta(String.format("root discriminant - jacobi: %d; %s", jacobi, evenOdd == 1 ? "Odd" : "Even"))
                        .getResultModel();
                return Collections.singletonList(resultModel);
            }
        };
    }

}
