package com.example.research2slidesweb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

public class TextSegmenter {

    // Minimum number of words required in a section to create a slide
    private static final int MIN_WORDS_PER_SECTION = 50;

    // Method to divide the extracted text into slides
    public static ArrayList<Slide> divide(String extractedText) throws JsonProcessingException, InterruptedException {

        ArrayList<Slide> presentation = new ArrayList<>();
        int sectionCount = 1; // Slide number in the presentation
        int pageCount = 0; // Page number in the original text

        // Split the extracted text into sections using double newlines as the separator
        String[] sections = extractedText.split("\\n\\n");

        // Loop through each section and process them to create slides
        for (String section : sections) {
            // Split the section into individual lines
            String[] lines = section.trim().split("\\n");

            // Get the first line of the section that will be used as the slide title
            String firstLine = getFirstLine(lines);

            // Determine if the page ends during the paragraph and if so, add 1 to the page count
            for (String line : lines) {
                if (line.startsWith("***START OF PAGE")) {
                    pageCount++;
                }
            }

            // Count the words in the current paragraph
            int paragraphWordCount = section.split("\\s+").length;

            // Check if the word count in the section meets the minimum requirement
            if (paragraphWordCount >= MIN_WORDS_PER_SECTION) {
                // Create a new Slide object and add it to the presentation list
                presentation.add(new Slide(sectionCount, pageCount, firstLine, section));
                sectionCount++;
            }
        }

        // Save the entire presentation as JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(presentation);
        String fileName = System.getProperty("user.dir") + "/src/main/resources/content/output/presentation.json";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Match images with corresponding slides
        matchImages(presentation);

        // Return the list of Slide objects, representing the presentation
        return presentation;
    }

    // Helper method to get the first meaningful line from an array of lines
    private static String getFirstLine(String[] lines) {
        for (String line : lines) {
            // Remove numbers, colons, periods, etc., and leading spaces
            String firstLine = line.replaceAll("[0-9:.*]", "").replaceAll("^\\s+", "").trim();
            if (!firstLine.isEmpty()) {
                // Ensure it doesn't use page start or end for the first title
                if (firstLine.startsWith("START OF PAGE") || firstLine.startsWith("END OF PAGE")) {
                    // If the first line is not meaningful, use the second line as the title
                    return lines[1].replaceAll("[^a-zA-Z\\s]", "").replaceAll("^\\s+", "").trim();
                }
                return firstLine;
            }
        }
        // Return an empty string if no meaningful line is found
        return "";
    }

    // Helper method to match images with corresponding slides
    public static void matchImages(ArrayList<Slide> presentation) {
        // Get the array of image numbers from TextExtraction class
        ArrayList<Integer> imageArray = TextExtraction.getImageArray();
        int temp = 1; // Temporary variable to keep track of the page number
        int i = 0; // Index variable for the presentation list

        // Loop through each slide in the presentation
        for (Slide slide : presentation) {
            // Check if the page number of the current slide is greater than temp (page number)
            if (slide.PageNum > temp) {
                temp++; // Increment temp to match with the next page number
                // Loop through the imageArray to find images that belong to the previous slide
                for (int x = 0; x < imageArray.size(); x++) {
                    if (imageArray.get(x) == (temp - 1)) {
                        // Add the index of the image to the previous slide's Image list
                        presentation.get(i - 1).Image.add(x);
                    }
                }
            }
            i++; // Move to the next slide in the presentation list
        }
    }
}
