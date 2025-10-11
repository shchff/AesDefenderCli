import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class Extract
{
    public static void extract(String inputDocx) throws IOException {
        Path inputPath = Path.of(inputDocx);

        if (!Files.exists(inputPath))
        {
            throw new FileNotFoundException("Файл " + inputDocx + " не найден");
        }

        StringBuilder secret = new StringBuilder();

        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(inputPath)))
        {
            for (XWPFParagraph paragraph : doc.getParagraphs())
            {
                for (XWPFRun run : paragraph.getRuns())
                {
                    String color = run.getColor();
                    if (color == null) continue;

                    color = color.toLowerCase(Locale.ROOT).replace("#", "").trim();

                    if (isRedTint(color))
                    {
                        String text = run.getText(0);
                        if (text != null) secret.append(text);
                    }
                }
            }
        }

        if (secret.isEmpty())
        {
            System.out.println("Не удалось извлечь сообщение — ничего не найдено.");
        }
        else
        {
            System.out.println("Извлечённое сообщение:");
            System.out.println(secret);
        }
    }

    private static boolean isRedTint(String color)
    {
        if (color.length() != 6) return false;
        return color.endsWith("0000") && !color.startsWith("00");
    }
}
