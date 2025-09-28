package com.shchff;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.shchff.Constants.DICT_SIZE;
import static com.shchff.Utils.bytesToHex;
import static com.shchff.Utils.readBlocksFromFile;

public class Translate
{
    public static void translate(String input, String mapping) throws IOException
    {
        List<byte[]> blocks = readBlocksFromFile(input);
        if (blocks.size() < DICT_SIZE)
        {
            throw new IllegalArgumentException("Размер зашифрованного файла слишком мал для наличия словаря в нём");
        }

        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(mapping)))
        {
            for (int i = 0; i < DICT_SIZE; i++)
            {
                String hex = bytesToHex(blocks.get(i));
                bw.write(hex + ";" + i + "\n");
            }
        }

        System.out.printf("Таблица сохранена %s%n", mapping);
    }
}
