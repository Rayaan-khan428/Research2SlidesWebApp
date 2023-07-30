package com.example.research2slidesweb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Gpt3TitleGenerator {

    // Replace 'YOUR_API_KEY' with your actual API key
    private static final String API_KEY = "YOUR_API_KEY";

    public static void main(String[] args) {
        String inputText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit...";

        try {
            String generatedTitle = generateTitle(inputText);
            System.out.println("Potential Title: " + generatedTitle);
        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static String generateTitle(String inputText) throws IOException, InterruptedException, URISyntaxException {
        String prompt = "Please generate a short title for the following text:\n" + inputText + "\nTitle:";

        String apiEndpoint = "https://api.openai.com/v1/engines/text-davinci-003/completions";
        String authorizationHeader = "Bearer " + API_KEY;
        String requestData = "{\"prompt\": \"" + prompt + "\", \"max_tokens\": 50}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(apiEndpoint))
                .POST(HttpRequest.BodyPublishers.ofString(requestData))
                .header("Content-Type", "application/json")
                .header("Authorization", authorizationHeader)
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            return extractTitleFromResponse(responseBody);
        } else {
            System.out.println("Request failed with status code: " + response.statusCode());
            System.out.println("Response body: " + response.body());
            return null;
        }
    }

    private static String extractTitleFromResponse(String responseBody) throws UnsupportedEncodingException {
        // The API response is in JSON format. Extract the generated title from the response.
        String decodedResponse = new String(Base64.getDecoder().decode(responseBody), StandardCharsets.UTF_8);
        int titleStartIndex = decodedResponse.indexOf("Title:") + 7;
        int titleEndIndex = decodedResponse.indexOf("\n", titleStartIndex);
        return decodedResponse.substring(titleStartIndex, titleEndIndex).trim();
    }
}
