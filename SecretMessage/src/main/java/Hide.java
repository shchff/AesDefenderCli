import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Hide
{
    private static final int INTENSITY = 1;
    public static void hide(String inputDocx, String secretTxt) throws IOException
    {
        Path inputPath = Paths.get(inputDocx);

        if (!Files.exists(inputPath))
        {
            throw new FileNotFoundException("Файл " + inputDocx + " не найден");
        }

        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(inputPath))) {

            String secret = Files.readString(Paths.get(secretTxt));

            List<XWPFRun> runs = doc.getParagraphs().stream()
                    .flatMap(p -> p.getRuns().stream())
                    .toList();

            if (secret.length() > runs.size())
            {
                throw new IllegalArgumentException("Недостаточно символов в документе для сокрытия сообщения!");
            }

            for (int i = 0; i < secret.length(); i++)
            {
                XWPFRun run = runs.get(i);
                run.setText(String.valueOf(secret.charAt(i)), 0);
                String hexColor = String.format("%02x0000", INTENSITY);
                run.setColor(hexColor);
            }

            try (FileOutputStream out = new FileOutputStream(inputPath.toFile(), false))
            {
                doc.write(out);
            }

            System.out.println("Сообщение успешно скрыто");
        }
    }
}
