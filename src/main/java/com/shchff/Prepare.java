package com.shchff;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.shchff.Constants.BLOCK_SIZE;
import static com.shchff.Constants.DICT_SIZE;
import static com.shchff.Utils.readFileToByteArray;

public class Prepare
{

    public static void prepare(String input, String output) throws IOException
    {
        byte[] data = readFileToByteArray(input);
        byte[] dict = createDictionary();
        byte[] expandedData = expandWithZeros(data);

        try (FileOutputStream os = new FileOutputStream(output))
        {
            os.write(dict);
            os.write(expandedData);
        }
        catch (IOException e)
        {
            throw new IOException(String.format("Ошибка в записи в файл %s", output), e);
        }

        System.out.printf("Подготовленный файл %s сохранён (размер=%d байт)%n",
                output,
                dict.length + expandedData.length);
    }

    private static byte[] createDictionary() throws IOException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] padding = new byte[BLOCK_SIZE - 1];
        for (int i = 0; i < DICT_SIZE; i++)
        {
            os.write((byte) i);
            os.write(padding);
        }
        return os.toByteArray();
    }

    private static byte[] expandWithZeros(byte[] data) throws IOException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] padding = new byte[BLOCK_SIZE - 1];
        for (byte b : data)
        {
            os.write(b);
            os.write(padding);
        }
        return os.toByteArray();
    }
}
