package com.shchff;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.shchff.Constants.BLOCK_SIZE;

public class Utils
{
    public static byte[] readFileToByteArray(String input) throws IOException
    {
        try
        {
            return Files.readAllBytes(Path.of(input));
        }
        catch (IOException e)
        {
            throw new IOException(String.format("Ошибка в чтении файла %s", input), e);
        }
    }

    public static List<byte[]> readBlocksFromFile(String input) throws IOException
    {
        byte[] data = null;
        try
        {
            data = Files.readAllBytes(Path.of(input));
        } catch (IOException e)
        {
            throw new IOException(String.format("Ошибка в чтении файла %s", input), e);
        }
        if (data.length % BLOCK_SIZE != 0)
        {
            throw new IllegalArgumentException(
                    String.format("Длина файла должна быть кратна размеру блока %d", BLOCK_SIZE));
        }

        List<byte[]> blocks = new ArrayList<>();

        for (int i = 0; i < data.length; i += BLOCK_SIZE)
        {
            blocks.add(Arrays.copyOfRange(data, i, i + BLOCK_SIZE));
        }

        return blocks;
    }

    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    public static String bytesToHex(byte[] bytes)
    {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++)
        {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
