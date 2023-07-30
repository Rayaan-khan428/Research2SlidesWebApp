package com.example.research2slidesweb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.asynchttpclient.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import org.json.JSONObject;
import java.util.concurrent.ExecutionException;

public class TextSummarizer {

    private static final String API_KEY = "q88H9vLvPMgp25ouFSDrRSb3vLL8zwRH9y3jXBXr";

    private static final String REGEX_PAGE_NUMBER = "\\*{3}(?:END|START) OF PAGE \\d+\\*{3}";
    private static final String REGEX_QUOTATIONS = "(?<!\\\\)\"";

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    /**
     * prepare text for summarization, check if it is longer than 250 characters
     * @param presentation
     * @throws InterruptedException
     */
    public static void summarize(ArrayList<Slide> presentation) throws InterruptedException, IOException, URISyntaxException {
        Gson gson = new Gson();

        for (int i = 0; i < presentation.size(); i++) {

            Slide slide = presentation.get(i);
            String text = slide.getParagraph();
            String title = generateTitle(text);
            String summary = summarizeText(text);

            if (summary != null) {
                slide.setTitle(title);
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
        String preSummarization = text.replaceAll(REGEX_PAGE_NUMBER, "")
                .replaceAll("\\s+", " ").trim()
                .replaceAll(REGEX_QUOTATIONS, "\\\\\"");

        if (preSummarization.length() < 250) {
            System.out.println("Paragraph has less than 250 characters, summarization not run to save tokens");
            return preSummarization;
        } else {
            try {
                AsyncHttpClient client = new DefaultAsyncHttpClient();
                String requestBody = String.format("{\"text\":\"%s\", \"length\":\"short\", \"format\":\"bullets\", \"model\":\"summarize-xlarge\"}", preSummarization);
                Request request = client.prepare("POST", "https://api.cohere.ai/v1/summarize")
                        .setHeader("accept", "application/json")
                        .setHeader("content-type", "application/json")
                        .setHeader("authorization", "Bearer " + API_KEY)
                        .setBody(requestBody)
                        .build();

                Response response = client.executeRequest(request).get();
                JsonObject jsonObject = new Gson().fromJson(response.getResponseBody(), JsonObject.class);

                if (jsonObject.has("summary") && !jsonObject.get("summary").isJsonNull()) {
                    String summary = jsonObject.get("summary").getAsString();
                    return summary.replaceAll("-", "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
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
        System.out.println("Summarized Text");
        System.out.println("Summary: " + summary);
        System.out.println("\nPercentage of PDF summarized: " + percentage + "%");
        System.out.println(currentIndex + " paragraphs out of " + totalSlides + "\n");
    }
}
