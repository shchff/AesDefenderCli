import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Extract
{
    public static void extract(String inputDocx) throws IOException
    {
        int threshold = 0;
        StringBuilder result = new StringBuilder();

        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(Paths.get(inputDocx))))
        {
            for (XWPFParagraph p : doc.getParagraphs())
            {
                for (XWPFRun run : p.getRuns())
                {
                    String color = run.getColor();

                    if (color != null && color.length() >= 2)
                    {
                        int red = Integer.parseInt(color.substring(0, 2), 16);

                        if (red > threshold) {
                            result.append(run.text());
                        }
                    }
                }
            }
        }

        System.out.println("Расшифрованное сообщение: " + result);

    }
}
