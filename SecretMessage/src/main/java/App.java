import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class App
{
    public static void main(String[] args)
    {

        if (args.length < 2)
        {
            printUsage();
            return;
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
                case HIDE:
                    Hide.hide(opts.get("-d"), opts.get("-s"), opts.getOrDefault("-i", "1"));
                    break;
                case EXTRACT:
                    Extract.extract(opts.get("-d"));
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

    private static void printUsage()
    {
        System.out.println("Использование: java -jar .\\target\\SecretMessage-1.0.jar <command> [options]");
        System.out.println("Команды:");
        System.out.println(" hide -d <file.docx> -s <secret.txt> -i <intensity>");
        System.out.println(" extract -d <file.docx>");
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
