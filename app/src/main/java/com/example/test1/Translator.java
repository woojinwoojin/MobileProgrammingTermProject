package com.example.test1;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class Translator {
    private static final String API_KEY = "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"; // 본인의 OpenAI 키로 교체

    public static String translate(String inputText) {
        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // 줄바꿈 문자 처리
            String prompt = inputText.replace("\n", "\\n").replace("\"", "\\\"");

            String body = "{\n" +
                    "  \"model\": \"gpt-4o-mini\",\n" +
                    "  \"messages\": [\n" +
                    "    {\"role\": \"system\", \"content\": \"다음 텍스트를 한국어로 번역해주세요. 서술은 부드럽게, 대사는 자연스럽게.\"},\n" +
                    "    {\"role\": \"user\", \"content\": \"" + prompt + "\"}\n" +
                    "  ]\n" +
                    "}";

            OutputStream os = connection.getOutputStream();
            os.write(body.getBytes());
            os.flush();
            os.close();

            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            // JSON 응답 파싱
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray choices = jsonResponse.getJSONArray("choices");
            JSONObject message = choices.getJSONObject(0).getJSONObject("message");
            String content = message.getString("content");

            return content;

        } catch (Exception e) {
            return "번역 중 오류 발생: " + e.getMessage();
        }
    }
    public static String retranslate(String inputText) {
        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // 줄바꿈 문자 처리
            String prompt = inputText.replace("\n", "\\n").replace("\"", "\\\"");

            String body = "{\n" +
                    "  \"model\": \"gpt-4o-mini\",\n" +
                    "  \"messages\": [\n" +
                    "    {\"role\": \"system\", \"content\": \"방금 텍스트를 한국어로 다시 정확하게 번역해주세요. 서술은 부드럽게, 대사는 자연스럽게.\"},\n" +
                    "    {\"role\": \"user\", \"content\": \"" + prompt + "\"}\n" +
                    "  ]\n" +
                    "}";

            OutputStream os = connection.getOutputStream();
            os.write(body.getBytes());
            os.flush();
            os.close();

            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            // JSON 응답 파싱
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray choices = jsonResponse.getJSONArray("choices");
            JSONObject message = choices.getJSONObject(0).getJSONObject("message");
            String content = message.getString("content");

            return content;

        } catch (Exception e) {
            return "번역 중 오류 발생: " + e.getMessage();
        }
    }
}
