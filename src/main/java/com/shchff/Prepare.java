package com.shchff;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.shchff.Constants.BLOCK_SIZE;
import static com.shchff.Constants.DICT_SIZE;

public class Prepare
{
    public static void prepare(String inputPath, String outputPath) throws IOException
    {
        byte[] data = Files.readAllBytes(Path.of(inputPath));
        byte[] dict = createDictionary();
        byte[] expandedData = expandWithZeros(data);
        try (FileOutputStream os = new FileOutputStream(outputPath))
        {
            os.write(dict);
            os.write(expandedData);
        }
        System.out.printf("Подготовленный файл %s сохранён (размер=%d байт)%n",
                outputPath,
                dict.length + expandedData.length);
    }
    private static byte[] createDictionary()
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] padding = new byte[BLOCK_SIZE - 1];
        for (int i = 0; i < DICT_SIZE; i++)
        {
            os.write((byte) i);
            try
            {
                os.write(padding);
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return os.toByteArray();
    }

    private static byte[] expandWithZeros(byte[] data)
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] padding = new byte[BLOCK_SIZE - 1];
        for (byte b : data)
        {
            os.write(b);
            try
            {
                os.write(padding);
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        return os.toByteArray();
    }
}
