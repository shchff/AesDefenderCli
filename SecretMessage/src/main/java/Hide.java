import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Hide {

    public static void hide(String inputDocx, String secretTxt, String intensityStr) throws IOException {
        checkParams(inputDocx, secretTxt, intensityStr);

        int intensity = Integer.parseInt(intensityStr) & 0xFF;
        String secret = Files.readString(Path.of(secretTxt)).trim();

        if (secret.isEmpty())
        {
            throw new IllegalArgumentException("Секрет пустой");
        }

        Path inputPath = Path.of(inputDocx);

        XWPFDocument doc;
        try (InputStream in = Files.newInputStream(inputPath))
        {
            doc = new XWPFDocument(in);
        }

        int secretIndex = 0;
        final int secretLen = secret.length();

        for (XWPFParagraph para : doc.getParagraphs())
        {
            List<CharPos> charPositions = collectCharPositions(para);

            if (charPositions.isEmpty()) continue;

            List<Seg> newSegs = new ArrayList<>();

            StringBuilder normalBuf = new StringBuilder();
            XWPFRun normalSrc = null;

            for (CharPos cp : charPositions)
            {
                char ch = cp.ch;
                if (secretIndex < secretLen && ch == secret.charAt(secretIndex))
                {
                    if (!normalBuf.isEmpty())
                    {
                        newSegs.add(new Seg(normalSrc, normalBuf.toString(), false));
                        normalBuf.setLength(0);
                        normalSrc = null;
                    }

                    newSegs.add(new Seg(cp.src, String.valueOf(ch), true));
                    secretIndex++;
                }
                else
                {
                    if (normalSrc == null)
                    {
                        normalSrc = cp.src;
                        normalBuf.append(ch);
                    }
                    else if (normalSrc == cp.src)
                    {
                        normalBuf.append(ch);
                    }
                    else
                    {
                        newSegs.add(new Seg(normalSrc, normalBuf.toString(), false));
                        normalBuf.setLength(0);
                        normalSrc = cp.src;
                        normalBuf.append(ch);
                    }
                }
            }

            if (!normalBuf.isEmpty())
            {
                newSegs.add(new Seg(normalSrc, normalBuf.toString(), false));
            }

            boolean hasColored = newSegs.stream().anyMatch(s -> s.colored);
            if (!hasColored)
            {
                if (secretIndex >= secretLen) break;
                else continue;
            }

            for (int i = para.getRuns().size() - 1; i >= 0; i--)
            {
                para.removeRun(i);
            }

            for (Seg s : newSegs)
            {
                XWPFRun newRun = para.createRun();
                if (s.src != null)
                {
                    copyRunFormattingBestEffort(s.src, newRun);
                }

                if (s.text != null && !s.text.isEmpty())
                {
                    String text = s.text;
                    StringBuilder buf = new StringBuilder();

                    for (int k = 0; k < text.length(); k++)
                    {
                        char c = text.charAt(k);

                        if (c == '\r')
                        {
                            continue;
                        }
                        else if (c == '\n')
                        {
                            if (!buf.isEmpty())
                            {
                                newRun.setText(buf.toString()); // append
                                buf.setLength(0);
                            }
                            newRun.addBreak();
                        }
                        else if (c == '\t')
                        {
                            if (!buf.isEmpty())
                            {
                                newRun.setText(buf.toString());
                                buf.setLength(0);
                            }
                            newRun.addTab();
                        }
                        else
                        {
                            buf.append(c);
                        }
                    }

                    if (!buf.isEmpty()) {
                        newRun.setText(buf.toString());
                    }
                }

                if (s.colored)
                {
                    String hex = String.format("%02x0000", intensity);
                    newRun.setColor(hex);
                }
            }

            if (secretIndex >= secretLen) break;
        }

        if (secretIndex < secretLen)
        {
            throw new IllegalArgumentException(
                    "Недостаточно подходящих символов в документе для сокрытия всего сообщения (встретилось "
                            + secretIndex + " из " + secretLen + ").");
        }

        saveResult(inputPath, doc);

        System.out.println("Сообщение успешно скрыто (окрашено " + secretLen + " символов).");
    }

    private static void checkParams(String inputDocx, String secretTxt, String intensityStr) throws FileNotFoundException
    {
        if (!Files.exists(Path.of(inputDocx)))
        {
            throw new FileNotFoundException("Файл " + inputDocx + " не найден");
        }
        if (!Files.exists(Path.of(secretTxt)))
        {
            throw new FileNotFoundException("Файл " + secretTxt + " не найден");
        }
        try
        {
            int v = Integer.parseInt(intensityStr);
            if (v < 0 || v > 255) throw new IllegalArgumentException("intensity должен быть 0..255");
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("intensity должен быть целым числом");
        }
    }

    private record CharPos(XWPFRun src, char ch) {}

    private record Seg(XWPFRun src, String text, boolean colored) { }

    private static List<CharPos> collectCharPositions(XWPFParagraph para)
    {
        List<CharPos> charPositions = new ArrayList<>();
        List<XWPFRun> origRuns = new ArrayList<>(para.getRuns());
        for (XWPFRun r : origRuns)
        {
            String t = r.getText(0);

            if (t == null || t.isEmpty())
            {
                continue;
            }
            for (int i = 0; i < t.length(); i++)
            {
                charPositions.add(new CharPos(r, t.charAt(i)));
            }
        }

        return charPositions;
    }

    private static void copyRunFormattingBestEffort(XWPFRun src, XWPFRun dst)
    {
        try
        {
            dst.setBold(src.isBold());
            dst.setItalic(src.isItalic());
            dst.setUnderline(src.getUnderline());
            if (src.getFontFamily() != null) dst.setFontFamily(src.getFontFamily());
            if (src.getFontSize() > 0) dst.setFontSize(src.getFontSize());
            dst.setTextPosition(src.getTextPosition());
            String color = src.getColor();
            if (color != null) dst.setColor(color);

            if (src.getCTR() != null && src.getCTR().getRPr() != null)
            {
                dst.getCTR().setRPr((CTRPr) src.getCTR().getRPr().copy());
            }
        }
        catch (Throwable ignored)
        {

        }
    }

    private static void saveResult(Path inputPath, XWPFDocument doc) throws IOException
    {
        Path parent = inputPath.getParent();
        if (parent == null) parent = Path.of(".");

        Path temp = Files.createTempFile(parent, "stego-", ".docx");
        try (doc; OutputStream out = Files.newOutputStream(temp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))
        {
            doc.write(out);
        }

        try
        {
            Files.move(temp, inputPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        }
        catch (AtomicMoveNotSupportedException ex)
        {
            Files.move(temp, inputPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
