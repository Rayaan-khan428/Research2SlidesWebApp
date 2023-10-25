package com.example.research2slidesweb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringEscapeUtils;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

import java.text.DecimalFormat;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextSummarizer {

    private static final String API_KEY = "q88H9vLvPMgp25ouFSDrRSb3vLL8zwRH9y3jXBXr";
    private static final Logger logger = LoggerFactory.getLogger(TextExtraction.class);
    private static final String REGEX_PAGE_NUMBER = "\\*{3}(?:END|START) OF PAGE \\d+\\*{3}";
    private static final String REGEX_QUOTATIONS = "(?<!\\\\)\"";

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    /**
     * prepare text for summarization, check if it is longer than 250 characters
     * @param presentation
     * @throws InterruptedException
     */
    public static void summarize(ArrayList<Slide> presentation) throws InterruptedException {
        Gson gson = new Gson();

        for (int i = 0; i < presentation.size(); i++) {

            Slide slide = presentation.get(i);
            String text = slide.getParagraph();
            String title = genTitle(slide.getParagraph());
            String summary = summarizeText(text);

            if (summary != null) {

                String[] words = title.split("\\s+");
                if (words.length > 13) {
                    StringBuilder truncatedTitle = new StringBuilder();
                    for (int j = 0; j < 7; j++) {
                        truncatedTitle.append(words[i]);
                        if (j < 6) {
                            truncatedTitle.append(" "); // Add space between words except for the last word
                        }
                    }
                    slide.setTitle(truncatedTitle.toString());
                } else {
                    slide.setTitle(title);
                }


                slide.setParagraph(summary);
                double percentage = ((double) i / presentation.size()) * 100;
                percentage = Double.parseDouble(DECIMAL_FORMAT.format(percentage));
                displayDebugInfo(i, presentation.size(), summary, percentage);
            }
        }
    }

    /**
     * If text is greater than 250 characters we will summarize it here
     * @param text
     * @return
     */
    private static String summarizeText(String text) {

        return "this is a test";

//        String preSummarization = text.replaceAll(REGEX_PAGE_NUMBER, "")
//                .replaceAll("\\s+", " ").trim()
//                .replaceAll(REGEX_QUOTATIONS, "\\\\\"");
//
//        if (preSummarization.length() < 250) {
//            System.out.println("Paragraph has less than 250 characters, summarization not run to save tokens");
//            logger.info("Paragraph has less than 250 characters, summarization not run to save tokens");
//            return preSummarization;
//        } else {
//            try {
//                AsyncHttpClient client = new DefaultAsyncHttpClient();
//                String requestBody = String.format("{\"text\":\"%s\", \"length\":\"short\", \"format\":\"bullets\", \"model\":\"summarize-xlarge\"}", preSummarization);
//                Request request = client.prepare("POST", "https://api.cohere.ai/v1/summarize")
//                        .setHeader("accept", "application/json")
//                        .setHeader("content-type", "application/json")
//                        .setHeader("authorization", "Bearer " + API_KEY)
//                        .setBody(requestBody)
//                        .build();
//
//                Response response = client.executeRequest(request).get();
//                JsonObject jsonObject = new Gson().fromJson(response.getResponseBody(), JsonObject.class);
//
//                if (jsonObject.has("summary") && !jsonObject.get("summary").isJsonNull()) {
//                    String summary = jsonObject.get("summary").getAsString();
//                    return summary.replaceAll("-", "");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
    }

    private static String genTitle(String text) {

        return "this is a test title";

//        String preSummarization = text.replaceAll(REGEX_PAGE_NUMBER, "")
//                .replaceAll("\\s+", " ").trim()
//                .replaceAll(REGEX_QUOTATIONS, "\\\\\"");
//
//        String prompt = "Provide a key phrase key it short: \n\n" + preSummarization;
//        String escapedPrompt = StringEscapeUtils.escapeJson(prompt);
//
//        try {
//            AsyncHttpClient client = new DefaultAsyncHttpClient();
//            String requestBody = String.format("{\"text\":\"%s\", \"additional_Command\":\"A very short title for the paragraph no more than 4 words\", \"model\":\"summarize-xlarge\"}", escapedPrompt);
//            Request request = client.prepare("POST", "https://api.cohere.ai/v1/summarize")
//                    .setHeader("accept", "application/json")
//                    .setHeader("content-type", "application/json")
//                    .setHeader("authorization", "Bearer " + API_KEY)
//                    .setBody(requestBody)
//                    .build();
//
//            Response response = client.executeRequest(request).get();
//            JsonObject jsonObject = new Gson().fromJson(response.getResponseBody(), JsonObject.class);
//
//            System.out.println(response.getResponseBody());
//
//            if (jsonObject.has("summary") && !jsonObject.get("summary").isJsonNull()) {
//                String summary = jsonObject.get("summary").getAsString();
//                return summary.replaceAll("-", "");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
    }


    private static String escapeQuotes(String text) {
        return text.replace("\"", "\\\"");
    }

    /**
     * To see detailed debugging infomration
     * @param currentIndex
     * @param totalSlides
     * @param summary
     * @param percentage
     */
    private static void displayDebugInfo(int currentIndex, int totalSlides, String summary, double percentage) {
        System.out.println("_________________________________________________");
        System.out.println("Summarized Paragraph");
        System.out.println("_________________________________________________");

//        logger.info("\n_________________________________________________");
//        logger.info("\nSummarized Paragraph");
//        logger.info("\n_________________________________________________");

//        System.out.println("Summary: " + summary);
//        System.out.println("\nPercentage of PDF summarized: " + percentage + "%");
//        System.out.println(currentIndex + " paragraphs out of " + totalSlides + "\n");
    }
}
