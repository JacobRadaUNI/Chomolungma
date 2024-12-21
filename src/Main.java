import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        int width = 400;
        int height = 600;
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        Font font = new Font("Comic Sans MS", 0, 18);
        String dataDir = "C:\\Users\\jacob\\Downloads\\ChomolungmaData.tsv";


        //make canvas white
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                bi.setRGB(w, h, 0xFFFFFF);
            }
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setFont(font);
        g.setColor(new Color(0));


        saveImage(bi, "image1");

        //Spreadsheet Data
        String line = "";
        String tsvSplitBy = "\t";

        ArrayList<String[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(dataDir))) {
            while ((line = br.readLine()) != null) {
                String[] row = line.split(tsvSplitBy);

                data.add(row);
            }

            // Convert List to 2D array
            String[][] dataArray = new String[data.size()][];
            for (int i = 0; i < data.size(); i++) {
                dataArray[i] = data.get(i);
            }

            // Print the 2D array
            for (String[] row : dataArray) {
                String name = row[0];
                String cost = row[1];
                String type = row[2];
                String effect = row[3];

                //make canvas white
                for (int w = 0; w < width; w++) {
                    for (int h = 0; h < height; h++) {
                        int color = switch (type) {
                            case "Basic" -> 0xC3DDE3;
                            case "Checkpoint" -> 0x70CC83;
                            case "Special" -> 0xF6F797;
                            default -> 0xFFFFFF;
                        };
                        bi.setRGB(w, h, color);
                    }
                }
                //Cost
                font = font.deriveFont( 60f);
                g.setFont(font);
                g.drawString(cost, 20, 60);

                //Name
                font = font.deriveFont( 30f);
                g.setFont(font);
                g.drawString(name, 78, 48);

                font = font.deriveFont( 24f);
                g.setFont(font);

                FontMetrics ruler = g.getFontMetrics();
                int availableWidth = width - 20;

                //Start values
                int x = 10;
                int y = 350;

                //Build String Array
                drawEffect(effect, ruler, availableWidth, g, x, y);

                //Draw Template
                BufferedImage template = null;
                template = ImageIO.read(new File("template.png"));
                g.drawImage(template, null, 0, 0 );

                //End of card
                saveImage(bi, name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void drawEffect(String effect, FontMetrics ruler, int availableWidth, Graphics2D g, int x, int y) {
        String toPrint = effect;
        ArrayList<String> stringArr = new ArrayList<>();
        boolean done = true;
        while (done) {
            StringBuilder tempString = new StringBuilder();
            String nextWord = "";
            boolean n = true;
            String extra = "";
            while (n && ruler.stringWidth(tempString + " " + nextWord) < availableWidth) {
                //Add next word to tempString
                tempString.append(nextWord).append(" ");
                //Remove the current word and set the next word
                    toPrint = toPrint.replaceFirst(nextWord, "");
                    if (toPrint.charAt(0) == ' ') toPrint = toPrint.replaceFirst(" ", "");
                if (toPrint.indexOf(' ') != -1) {
                    nextWord = toPrint.substring(0, toPrint.indexOf(' '));
                } else {    //last word
                    nextWord = toPrint;
                    n = false;
                    done = false;

                    if (ruler.stringWidth(tempString + " " + nextWord) < availableWidth) {
                        tempString.append(nextWord);
                    } else {
                        extra = nextWord;
                    }
                }
            }
            stringArr.add(tempString.toString());
            if (!extra.isEmpty()) {
                stringArr.add(extra);
            }
        }

        for (String s: stringArr) {
            g.drawString(s, x, y);
            y += ruler.getHeight();
        }
    }

    private static BufferedImage getImageFromURL(String urlString) {
        BufferedImage webImg;
        URL url;
        try {
            url = new URL(urlString);
            webImg = ImageIO.read(url);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return webImg;
    }

    private static void saveImage(BufferedImage bi, String name) {
        File outputFile = new File(name + ".png");
        try {
            ImageIO.write(bi, "png", outputFile);
        } catch (IOException e) {throw new RuntimeException(e);}
    }
}