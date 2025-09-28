package com.shchff;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.shchff.Constants.BLOCK_SIZE;
import static com.shchff.Constants.DICT_SIZE;
import static com.shchff.Utils.bytesToHex;
import static com.shchff.Utils.readBlocksFromFile;

public class Decode
{
    public static void decode(String input, String mapping, String output) throws IOException
    {
        Map<String, Integer> map = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(mapping)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                if (line.trim().isEmpty())
                {
                    continue;
                }
                String[] parts = line.split(";");
                map.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
            }
        }
        catch (IOException e)
        {
            throw new IOException(String.format("Ошибка в чтении файла %s", input), e);
        }

        List<byte[]> blocks = readBlocksFromFile(input);
        List<byte[]> expandedPlainBlocks = new ArrayList<>();
        int unknown = 0;

        for (byte[] cblock : blocks)
        {
            String key = bytesToHex(cblock);
            if (map.containsKey(key))
            {
                int val = map.get(key);
                byte[] pblock = new byte[BLOCK_SIZE];
                pblock[0] = (byte) val;
                expandedPlainBlocks.add(pblock);
            }
            else
            {
                unknown++;
                expandedPlainBlocks.add(new byte[BLOCK_SIZE]);
            }
        }
        if (unknown > 0)
        {
            System.out.printf("Предупреждение: %d блоков не могут быть расшифрованы%n", unknown);
        }

        try
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            for (byte[] b : expandedPlainBlocks)
            {
                bos.write(b);
            }
            ByteArrayOutputStream recovered = getRecovered(bos);
            Files.write(Path.of(output), recovered.toByteArray());
        }
        catch (IOException e)
        {
            throw new IOException(String.format("Ошибка в записи в файл %s", output), e);
        }

        System.out.printf("Декодированный файл %s сохранён%n", output);
    }

    private static ByteArrayOutputStream getRecovered(ByteArrayOutputStream bos)
    {
        byte[] expandedPlain = bos.toByteArray();

        int start = DICT_SIZE * BLOCK_SIZE;
        if (expandedPlain.length < start)
        {
            start = expandedPlain.length;
        }

        byte[] afterDict = Arrays.copyOfRange(expandedPlain, start, expandedPlain.length);

        ByteArrayOutputStream recovered = new ByteArrayOutputStream();
        for (int i = 0; i < afterDict.length; i += BLOCK_SIZE)
        {
            recovered.write(afterDict[i]);
        }
        return recovered;
    }
}
