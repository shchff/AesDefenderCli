package com.shchff;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;

import static com.shchff.Constants.BLOCK_SIZE;

public class Encode
{
    public static void encode(String input, String output) throws Exception
    {
        byte[] plaintext = Files.readAllBytes(Path.of(input));
        if (plaintext.length % BLOCK_SIZE != 0)
        {
            throw new IllegalArgumentException(
                    String.format("Длина файла долнжна быть кратна %s", BLOCK_SIZE));
        }

        byte[] key = new byte[BLOCK_SIZE];
        new SecureRandom().nextBytes(key);
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
        byte[] cipherText = cipher.doFinal(plaintext);
        Files.write(Path.of(output), cipherText);
        System.out.printf("Зашифрованный файл сохранён %s%n", output);
    }
}
