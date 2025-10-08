package com.shchff;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;

import static com.shchff.Constants.BLOCK_SIZE;
import static com.shchff.Utils.readFileToByteArray;

public class Encode
{
    public static void encode(String input, String output) throws IOException
    {
        byte[] plaintext = readFileToByteArray(input);

        if (plaintext.length % BLOCK_SIZE != 0)
        {
            throw new IllegalArgumentException(
                    String.format("Длина файла должна быть кратна %s", BLOCK_SIZE));
        }

        byte[] cipheredText = cipherText(plaintext);

        try
        {
            Files.write(Path.of(output), cipheredText);
        }
        catch (IOException e)
        {
            throw new IOException(String.format("Ошибка в записи в файл %s", output), e);
        }

        System.out.printf("Зашифрованный файл сохранён %s%n", output);
    }

    private static byte[] cipherText(byte[] plaintext)
    {
        try
        {
            byte[] key = new byte[BLOCK_SIZE];
            new SecureRandom().nextBytes(key);

            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
            return cipher.doFinal(plaintext);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Ошибка шифрования", e);
        }
    }
}
