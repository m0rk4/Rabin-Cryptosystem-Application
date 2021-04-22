package by.bsuir.m0rk4.it.task.third.crypto;

import by.bsuir.m0rk4.it.task.third.algorithm.Algorithms;
import by.bsuir.m0rk4.it.task.third.algorithm.ByteUtils;
import javafx.concurrent.Task;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static by.bsuir.m0rk4.it.task.third.algorithm.Algorithms.*;

public class RabinCryptoSystem {
    public Task<List<String>> encrypt(File fileInput, File fileOutput, BigInteger b, BigInteger module) {
        return new Task<>() {
            @Override
            protected List<String> call() {
                int modBytesCount = module.toByteArray().length;
                byte[] buffer = new byte[modBytesCount + 1];
                int maxAvailableToRead = modBytesCount - 1;
                int bytesRead, totalRead = 0;
                long length = fileInput.length();
                System.out.println(length);
                List<String> numbers = new ArrayList<>(10);
                try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fileInput));
                     BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileOutput))) {
                    // writing filesize
                    byte[] bytes = ByteUtils.longToBytes(length);
                    bufferedOutputStream.write(bytes, 0, bytes.length);
                    // processing contents
                    while ((bytesRead = bufferedInputStream.read(buffer, 1, maxAvailableToRead)) != -1) {
                        processByteBlockEncrypt(buffer, bytesRead, maxAvailableToRead, b, module, numbers);
                        bufferedOutputStream.write(buffer, 0, buffer.length);
                        Arrays.fill(buffer, (byte) 0);
                        totalRead += bytesRead;
                        updateProgress(totalRead, length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return numbers;
            }
        };
    }

    private void processByteBlockEncrypt(
            byte[] buffer,
            int bytesRead,
            int maxAvailableToRead,
            BigInteger b,
            BigInteger module,
            List<String> numbers
    ) {
        if (bytesRead != maxAvailableToRead) {
            for (int i = bytesRead; i > 0; i--) {
                buffer[maxAvailableToRead - (bytesRead - i)] = buffer[i];
                buffer[i] = 0;
            }
        }
        BigInteger m = new BigInteger(buffer, 0, maxAvailableToRead + 1);
        BigInteger encrypted = encryptNumber(m, b, module);
        if (numbers.size() < 10) {
            System.out.println("Message: " + m);
            System.out.println("Encrypted: " + encrypted);
            numbers.add(encrypted.toString());
        }

        Arrays.fill(buffer, (byte) 0);
        buffer[buffer.length - 1] = getMetaByte(b, module, m);
        byte[] bytes = encrypted.toByteArray();
        System.arraycopy(bytes, 0, buffer, buffer.length - bytes.length - 1, bytes.length);
    }

    private byte getMetaByte(BigInteger b, BigInteger module, BigInteger m) {
        BigInteger futureRoot = m.multiply(TWO).mod(module);
        futureRoot = futureRoot.add(b).mod(module);

        byte meta = 0;
        int jacobi = (Algorithms.getJacobi(futureRoot, module).intValue() + 1) >> 1;
        int evenOdd = futureRoot.and(ONE).intValue();
        meta ^= (-jacobi ^ meta) & 2;
        meta ^= (-evenOdd ^ meta) & 1;
        return meta;
    }

    private BigInteger encryptNumber(BigInteger m, BigInteger b, BigInteger n) {
        BigInteger mPlusB = m.add(b).mod(n);
        BigInteger mMultVal = m.multiply(mPlusB);
        return mMultVal.mod(n);
    }

    public Task<List<String>> decrypt(
            File fileInput,
            File fileOutput,
            BigInteger b,
            BigInteger module,
            BigInteger p,
            BigInteger q
    ) {
        return new Task<>() {
            @Override
            protected List<String> call() {
                System.out.println();
                int modBytesCount = module.toByteArray().length;
                byte[] buffer = new byte[modBytesCount + 1];
                int bytesRead, totalRead = 0, maxToWrite = modBytesCount - 1;
                long length = fileInput.length();
                List<String> numbers = new ArrayList<>(10);
                try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fileInput));
                     BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileOutput))) {
                    // reading origin len
                    long originLen = deserializeSize(bufferedInputStream);
                    // process contents
                    boolean lastBlock = false;
                    long originTotal = 0;
                    while ((bytesRead = bufferedInputStream.read(buffer, 0, buffer.length)) != -1) {
                        processByteBlockDecrypt(
                                buffer, bytesRead, p, q, b, module, numbers, maxToWrite, length, lastBlock,
                                originTotal, originLen
                        );
                        if (!lastBlock) {
                            bufferedOutputStream.write(buffer, 0, maxToWrite);
                        } else {
                            bufferedOutputStream.write(buffer, 0, (int) (originLen - originTotal));
                        }
                        originTotal += maxToWrite;
                        Arrays.fill(buffer, (byte) 0);
                        totalRead += bytesRead;
                        if (totalRead + buffer.length == length) {
                            lastBlock = true;
                        }
                        updateProgress(totalRead, length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return numbers;
            }
        };
    }

    private long deserializeSize(BufferedInputStream bufferedInputStream) throws IOException {
        byte[] size = new byte[Long.BYTES];
        int readLen = bufferedInputStream.read(size, 0, size.length);
        if (readLen != Long.BYTES) {
            System.exit(1);

        }
        return ByteUtils.bytesToLong(size);
    }

    private void processByteBlockDecrypt(
            byte[] buffer,
            int bytesRead,
            BigInteger p,
            BigInteger q,
            BigInteger b,
            BigInteger n,
            List<String> numbers,
            int maxToWrite,
            long inputFileLen,
            boolean isLastBlock,
            long originTotal,
            long originLen
    ) {
        if (buffer.length != bytesRead) {
            System.exit(1);
        }
        byte meta = buffer[buffer.length - 1];
        int jacobi = (meta >> 1) & 1;
        int evenOdd = meta & 1;

        BigInteger c = new BigInteger(buffer, 0, buffer.length - 1);
        BigInteger c4 = c.multiply(FOUR).mod(n);
        BigInteger bSqr = b.multiply(b).mod(n);
        BigInteger discriminant = bSqr.add(c4).mod(n);

        BigInteger pPow = p.add(ONE).divide(FOUR);
        BigInteger qPow = q.add(ONE).divide(FOUR);
        BigInteger mP = discriminant.modPow(pPow, p);
        BigInteger mQ = discriminant.modPow(qPow, q);

        BigInteger mPNeg = mP.negate();
        BigInteger mQNeg = mQ.negate();

        BigInteger root1 = CRT(mP, mQ, p, q);
        BigInteger root2 = CRT(mPNeg, mQ, p, q);
        BigInteger root3 = CRT(mP, mQNeg, p, q);
        BigInteger root4 = CRT(mPNeg, mQNeg, p, q);

        BigInteger message;
        if (jacobi == 1) {
            if (evenOdd == root1.and(ONE).intValue()) {
                message = getPossibleRoot(b, root1, n);
            } else {
                message = getPossibleRoot(b, root4, n);
            }
        } else {
            if (evenOdd == root2.and(ONE).intValue()) {
                message = getPossibleRoot(b, root2, n);
            } else {
                message = getPossibleRoot(b, root3, n);
            }
        }
        if (numbers.size() < 10) {
            System.out.println("Recieved crypto: " + c);
            System.out.println("Decrypted: " + message);
            numbers.add(message.toString());
        }
        byte[] bytes = message.toByteArray();
        if (!isLastBlock) {
            if (bytes.length == maxToWrite) {
                System.arraycopy(bytes, 0, buffer, 0, bytes.length);
            } else if (bytes.length < maxToWrite) {
                // TODO if difference is bigger then 1 leave as it is
                int j = maxToWrite - bytes.length;
                int i = 0;
                while (j > 0) {
                    buffer[i] = 0;
                    j--;
                    i++;
                }
                System.arraycopy(bytes, 0, buffer, i, bytes.length);
            } else {
                System.arraycopy(bytes, 1, buffer, 0, bytes.length - 1);
            }
        } else {
            long leftToWrite = originLen - originTotal;
            if (bytes.length == leftToWrite) {
                System.arraycopy(bytes, 0, buffer, 0, bytes.length);
            } else if (bytes.length < leftToWrite) {
                long j = leftToWrite - bytes.length;
                int i = 0;
                while (j > 0) {
                    buffer[i] = 0;
                    j--;
                    i++;
                }
                System.arraycopy(bytes, 0, buffer, i, bytes.length);
            } else {
                System.arraycopy(bytes, 1, buffer, 0, bytes.length - 1);
            }
        }
    }

    private BigInteger getPossibleRoot(BigInteger b, BigInteger discriminantSqrt, BigInteger n) {
        BigInteger subtraction = discriminantSqrt.subtract(b);
        if (subtraction.and(ONE).compareTo(ZERO) != 0) {
            subtraction = subtraction.add(n);
        }
        return subtraction.divide(TWO).mod(n);
    }
}
