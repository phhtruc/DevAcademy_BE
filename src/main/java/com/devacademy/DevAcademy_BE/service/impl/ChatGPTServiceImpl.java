package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.service.AIService;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ChatGPTServiceImpl implements AIService {
    static String OPENAI_API_KEY = Dotenv.configure().filename("local.env").load().get("CHATGPT_API_KEY");
    static String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    @Override
    public String reviewCode(String content) throws IOException, InterruptedException {
        JSONObject messageObject = new JSONObject();
        messageObject.put("role", "user");
        messageObject.put("content", content);

        JSONArray messagesArray = new JSONArray();
        messagesArray.put(messageObject);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-4o");
        requestBody.put("messages", messagesArray);
        requestBody.put("temperature", 0.7);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_API_URL))
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        String response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).body();

        JSONObject jsonResponse = new JSONObject(response);
        if (!jsonResponse.has("choices")) {
            return "Review failed: Unexpected response: " + response;
        }

        JSONArray choicesArray = jsonResponse.getJSONArray("choices");
        JSONObject firstChoice = choicesArray.getJSONObject(0);
        JSONObject message = firstChoice.getJSONObject("message");
        return message.getString("content")
                .replaceAll("```html\\n", "").replaceAll("```", "");
    }

}
