package com.shchff;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.shchff.Decode.decode;
import static com.shchff.Prepare.prepare;
import static com.shchff.Encode.encode;
import static com.shchff.Translate.translate;

public class App
{

    public static void main( String[] args )
    {
        if (args.length < 1)
        {
            System.out.println();
            printUsage();
            System.exit(1);
        }

        Command command = null;

        try
        {
            command = Command.fromCommandName(args[0]);
        }
        catch (IllegalArgumentException e)
        {
            System.out.println(e.getMessage());
            System.out.println();
            printUsage();
            System.exit(1);
        }

        Map<String, String> opts = parseArgs(Arrays.copyOfRange(args, 1, args.length));

        try
        {
            switch (command)
            {
                case PREPARE:
                    prepare(opts.get("-i"), opts.get("-o"));
                    break;
                case ENCODE:
                    encode(opts.get("-i"), opts.get("-o"));
                    break;
                case TRANSLATE:
                    translate(opts.get("-i"), opts.get("-m"));
                    break;
                case DECODE:
                    decode(opts.get("-i"), opts.get("-m"), opts.get("-o"));
                    break;
            }
        }
        catch (IOException | IllegalArgumentException e)
        {
            System.out.println(e.getMessage());
            System.out.println();
            printUsage();
        }
    }

    private static void printUsage() {
        System.out.println("Использование: java -jar .\\target\\AesDefenderCli-1.0-SNAPSHOT.jar <command> [options]");
        System.out.println("Команды:");
        System.out.println(" prepare -i input -o output");
        System.out.println(" encode -i input -o output");
        System.out.println(" translate -i input -m mapping");
        System.out.println(" decode -i input -m mapping -o output");
    }

    private static Map<String, String> parseArgs(String[] args)
    {
        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < args.length; i++)
        {
            String key = args[i];
            if (i + 1 < args.length)
            {
                map.put(key, args[++i]);
            }
        }

        return map;
    }
}
