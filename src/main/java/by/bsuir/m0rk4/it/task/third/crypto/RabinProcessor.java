package by.bsuir.m0rk4.it.task.third.crypto;

import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class RabinProcessor {

    private static final int BUFFER_SIZE = 4096 * 4096;
    private static final ByteBuffer BUFFER_INPUT = ByteBuffer.allocate(BUFFER_SIZE);
    private static final ByteBuffer BUFFER_OUTPUT = ByteBuffer.allocate(BUFFER_SIZE);

    private final RabinCryptoSystem rabinCryptoSystem;

    public RabinProcessor(RabinCryptoSystem rabinCryptoSystem) {
        this.rabinCryptoSystem = rabinCryptoSystem;
    }

    public Task<List<String>> encrypt(File fileInput, File fileOutput, BigInteger b, BigInteger module) {
        return new Task<>() {
            @Override
            protected List<String> call() {
                BUFFER_INPUT.clear();
                BUFFER_OUTPUT.clear();

                int modBytesCount = module.toByteArray().length;
                int readBytesCount = modBytesCount - 1;
                List<String> numbers = new ArrayList<>(10);
                try (
                        RandomAccessFile rafInput = new RandomAccessFile(fileInput, "r");
                        FileChannel inputChannel = rafInput.getChannel();
                        RandomAccessFile rafOutput = new RandomAccessFile(fileOutput, "rw");
                        FileChannel outputChannel = rafOutput.getChannel()
                ) {
                    long bytesRead = 0;
                    long length = fileInput.length();
                    // writing filesize
                    BUFFER_OUTPUT.putLong(length);
                    // processing contents
                    byte[] messageHolder = new byte[readBytesCount + 1];
                    while (inputChannel.read(BUFFER_INPUT) > 0) {
                        BUFFER_INPUT.flip();
                        while (BUFFER_INPUT.limit() - BUFFER_INPUT.position() >= readBytesCount) {
                            BUFFER_INPUT.get(messageHolder, 1, readBytesCount);
                            writeEncryptedMessage(modBytesCount, outputChannel, messageHolder, b, module);
                            // progress
                            bytesRead += readBytesCount;
                            updateProgress(bytesRead, length);
                        }
                        if (BUFFER_INPUT.hasRemaining()) {
                            BUFFER_INPUT.compact();
                        } else {
                            BUFFER_INPUT.clear();
                        }
                    }
                    if (BUFFER_INPUT.position() != 0) {
                        BUFFER_INPUT.flip();
                        int i = 0;
                        for (int zerosCount = readBytesCount - BUFFER_INPUT.limit(); zerosCount > 0; zerosCount--)
                            messageHolder[i++] = 0;
                        BUFFER_INPUT.get(messageHolder, i, BUFFER_INPUT.limit());
                        writeEncryptedMessage(modBytesCount, outputChannel, messageHolder, b, module);
                        // progress
                        bytesRead += readBytesCount;
                        updateProgress(bytesRead, length);
                    }
                    if (BUFFER_OUTPUT.hasRemaining()) {
                        BUFFER_OUTPUT.flip();
                        outputChannel.write(BUFFER_OUTPUT);
                        BUFFER_OUTPUT.clear();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return numbers;
            }
        };
    }

    private void writeEncryptedMessage(
            int modBytesCount,
            FileChannel outputChannel,
            byte[] messageHolder,
            BigInteger b,
            BigInteger module
    ) throws IOException {
        BigInteger message = new BigInteger(messageHolder);
        BigInteger encrypted = rabinCryptoSystem.encryptNumber(message, b, module);
        byte[] encryptedBytes = encrypted.toByteArray();
        byte metaInfo = rabinCryptoSystem.getMetaByte(b, module, message);
        if (BUFFER_OUTPUT.position() + modBytesCount + 1 > BUFFER_OUTPUT.capacity()) {
            BUFFER_OUTPUT.flip();
            outputChannel.write(BUFFER_OUTPUT);
            BUFFER_OUTPUT.clear();
        }
        for (int destPos = modBytesCount - encryptedBytes.length; destPos > 0; destPos--)
            BUFFER_OUTPUT.put((byte) 0);
        BUFFER_OUTPUT.put(encryptedBytes);
        BUFFER_OUTPUT.put(metaInfo);
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
                BUFFER_INPUT.clear();
                BUFFER_OUTPUT.clear();

                int modBytesCount = module.toByteArray().length;
                int readBlockSize = modBytesCount + 1;
                long totalRead = 0, maxToWrite = modBytesCount - 1;
                long length = fileInput.length();
                List<String> numbers = new ArrayList<>(10);
                try (
                        RandomAccessFile rafInput = new RandomAccessFile(fileInput, "r");
                        FileChannel inputChannel = rafInput.getChannel();
                        RandomAccessFile rafOutput = new RandomAccessFile(fileOutput, "rw");
                        FileChannel outputChannel = rafOutput.getChannel()
                ) {
                    // reading origin len
                    ByteBuffer tmp = ByteBuffer.allocate(Long.BYTES);
                    int read = inputChannel.read(tmp);
                    assert read == Long.BYTES;
                    tmp.flip();
                    long originalFileSize = tmp.getLong();
                    length -= Long.BYTES;

                    long blocksTotalCount = length / readBlockSize;
                    long currBlocks = 0;
                    boolean isLastBlock;
                    // process file contents
                    byte[] encryptedMessageHolder = new byte[readBlockSize];
                    while (inputChannel.read(BUFFER_INPUT) > 0) {
                        BUFFER_INPUT.flip();
                        while (BUFFER_INPUT.limit() - BUFFER_INPUT.position() >= readBlockSize) {

                            currBlocks++;
                            isLastBlock = currBlocks == blocksTotalCount;

                            BUFFER_INPUT.get(encryptedMessageHolder, 0, readBlockSize);
                            byte metaInfo = encryptedMessageHolder[readBlockSize - 1];

                            int jacobi = (metaInfo >> 1) & 1;
                            int evenOdd = metaInfo & 1;
                            BigInteger c = new BigInteger(encryptedMessageHolder, 0, readBlockSize - 1);

                            BigInteger decryptedMessage = rabinCryptoSystem.getDecryptedMessage(p, q, b, module, jacobi, evenOdd, c);
                            byte[] decryptedBytes = decryptedMessage.toByteArray();
                            int decryptedLen = decryptedBytes.length;

                            if (BUFFER_OUTPUT.position() + maxToWrite > BUFFER_OUTPUT.capacity()) {
                                BUFFER_OUTPUT.flip();
                                outputChannel.write(BUFFER_OUTPUT);
                                BUFFER_OUTPUT.clear();
                            }

                            if (decryptedLen == maxToWrite) {
                                BUFFER_OUTPUT.put(decryptedBytes);
                            } else if (decryptedLen < maxToWrite) {
                                if (isLastBlock) {
                                    // need to know original filesize and how much we wrote
                                    long readSoFar = (blocksTotalCount - 1) * maxToWrite;
                                    long left = originalFileSize - readSoFar;
                                    if (decryptedLen == left) {
                                        BUFFER_OUTPUT.put(decryptedBytes);
                                    } else if (decryptedLen < left) {
                                        for (long l = left - decryptedLen; l > 0; l--)
                                            BUFFER_OUTPUT.put((byte) 0);
                                        BUFFER_OUTPUT.put(decryptedBytes);
                                    } else {
                                        BUFFER_OUTPUT.put(decryptedBytes, 0, (int) left);
                                    }
                                } else {
                                    for (long l = maxToWrite - decryptedLen; l > 0; l--)
                                        BUFFER_OUTPUT.put((byte) 0);
                                    BUFFER_OUTPUT.put(decryptedBytes);
                                }
                            } else {
                                BUFFER_OUTPUT.put(decryptedBytes, 1, decryptedLen - 1);
                            }

                            totalRead += readBlockSize;
                            updateProgress(totalRead, length);
                        }
                        if (BUFFER_INPUT.hasRemaining()) {
                            BUFFER_INPUT.compact();
                        } else {
                            BUFFER_INPUT.clear();
                        }
                    }
                    if (BUFFER_OUTPUT.hasRemaining()) {
                        BUFFER_OUTPUT.flip();
                        outputChannel.write(BUFFER_OUTPUT);
                        BUFFER_OUTPUT.clear();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return numbers;
            }
        };
    }
}
