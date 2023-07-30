package com.example.research2slidesweb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class TextExtraction extends PDFStreamEngine {

    private int imageNumber = 1; // used in naming img's extracted from pdf
    private int pageNum = 0;

    private static ArrayList<Integer> array;
    private String outputFolder; // New instance variable to hold the output folder path

    // Constructor to set the output folder path
    public TextExtraction(String resourcesDirectory) {
        this.outputFolder = resourcesDirectory;
        this.array = new ArrayList<>();
    }

    /**
     * Calls every method needed to extract text, pageNum, and images
     *
     * @param document
     * @param projectRoot
     * @throws IOException
     */
    public void runExtraction(PDDocument document, String projectRoot) throws IOException {

        SaveImagesInPdf(document);
        getNumImages();
        getPage(1);
        extractText(document, projectRoot);

    }

    /**
     * finds all images and saves them into designated directory
     *
     * @param document
     * @throws IOException
     */
    public void SaveImagesInPdf(PDDocument document) throws IOException {
        try {
            for (PDPage page : document.getPages()) {
                pageNum++;
                System.out.println("Processing page: " + pageNum);
                this.processPage(page);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extracts text from a given PDDocument and writes it to a json file.
     *
     * @param document The PDDocument from which text needs to be extracted.
     * @throws IOException If an I/O error occurs while reading the document or writing the output.
     */
    public void extractText(PDDocument document, String resourcesDirectory) throws IOException {
        // Creating PDFTextStripper object
        PDFTextStripper pdfStripper = new PDFTextStripper();
        int totalPages = document.getNumberOfPages();

        String sPage = ""; // start page
        String ePage = ""; // end page
        String text = ""; // main string (entire pdf is in this one string)
        String text2 = ""; //
        List<String> lines;

        for (int pageNumber = 1; pageNumber <= totalPages; pageNumber++) {

            pdfStripper.setStartPage(pageNumber);
            pdfStripper.setEndPage(pageNumber);

            sPage = "***START OF PAGE " + pageNumber + "***\n";
            ePage = "***END OF PAGE " + pageNumber + "***\n";

            text2 = pdfStripper.getText(document); // converts entire pdf to a string and store into text2
            lines = detectLines(text2); // returns the entire pdf as an ArrayList, each line is an item

            text += sPage; // append starting page number

            // if the lines extracted are readable and long enough, append them to text variable
            for (String line : lines) {
                if (!junkTest(line) && line.length() > 3) {
                    text += line + "\n";
                }
            }
            text += ePage + "\n"; // append ending page number
        }

        // Create a PdfContent object with the extracted text
        PdfContent pdfContent = new PdfContent(text);

        // Convert the PdfContent object to JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Configure Gson to preserve new lines and white spaces
        Gson gsonWithEscapeHtml = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping() // This will prevent escaping of new lines and other special characters
                .create();

        // Convert the PdfContent object to JSON
        String json = gsonWithEscapeHtml.toJson(pdfContent);

        // Create the output file path using Paths
        Path outputFile = Paths.get(resourcesDirectory, "parsedPDF.json");

        // Save the JSON string to a file
        try (FileWriter fileWriter = new FileWriter(outputFile.toFile())) {
            fileWriter.write(json);
            System.out.println("JSON data has been written to 'parsedPDF.json' successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Detects and extracts lines from the input text.
     *
     * @param text The input text from which paragraphs need to be detected.
     * @return A list of lines extracted from the input text.
     */
    private static List<String> detectLines(String text) {

        // create an Arraylist of Strings, each string represents a line
        List<String> lines = new ArrayList<>();

        // regex to match line boundaries based on two or more consecutive line breaks.
        String linePattern = "(?m)(?s)(^.*?)(?:\\r?\\n\\r?\\n|$)";

        Pattern pattern = Pattern.compile(linePattern);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {

            // Trim the line to remove leading and trailing whitespaces or line breaks.
            String line = matcher.group(1).trim();

            // Add the line to the list.
            lines.add(line);
        }

        return lines;
    }

    public void addItem(int item) {
        array.add(item);
    }

    public int getNumImages() {
        return array.size();
    }

    public int getPage(int imageNum) {
        if(imageNum > getNumImages()) {
            return -1;
        }
        return array.get(imageNum-1);
    }

    // Return array with image page numbers
    public static ArrayList<Integer> getImageArray() {
        return array;
    }

    /**
     * Helper Function for the Image extraction method
     *
     * @param operator The operation to perform.
     * @param operands The list of arguments.
     *
     * @throws IOException If there is an error processing the operation.
     */
    @Override
    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
        String operation = operator.getName();

        // Do is the PDF operator for xobjects (like images)
        // there are a bunch of PDF operators for things like bolded text, titles,
        // diagrams, etc.
        if ("Do".equals(operation)) {
            COSName objectName = (COSName) operands.get(0);
            PDXObject xobject = getResources().getXObject(objectName);

            if (xobject instanceof PDImageXObject) {
                PDImageXObject image = (PDImageXObject) xobject;

                // same image to output folder
                BufferedImage bImage = image.getImage();
                // you can change the name of the image here
                ImageIO.write(bImage, "PNG", new File(outputFolder + "/image_" + imageNumber + ".png"));
                System.out.println("Image saved.");
                this.addItem(pageNum);
                imageNumber++;

            } else if (xobject instanceof PDFormXObject) {
                PDFormXObject form = (PDFormXObject) xobject;
                showForm(form);
            }
        } else {
            super.processOperator(operator, operands);
        }
    }

    /**
     * Checks if the input string contains more letters than numbers. to filter out non-image tables as we are unable
     * to extract them (no library in java exists for this)
     * @param s The input string to be tested.
     * @return True if the input string contains more letters than numbers, otherwise false.
     */
    private boolean junkTest(String s) {
        int letterCount = 0;
        int numberCount = 0;

        // Iterate through each character in the input string
        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) {
                letterCount++;
            } else if (Character.isDigit(c)) {
                numberCount++;
            }
        }
        return letterCount < numberCount;
    }

}