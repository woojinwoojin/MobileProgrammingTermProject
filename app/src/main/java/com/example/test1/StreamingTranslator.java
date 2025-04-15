package com.example.test1;

import android.util.Log;
import androidx.annotation.Nullable;
import java.io.IOException;
import okhttp3.*;
import okio.BufferedSource;
import org.json.JSONObject;

public class StreamingTranslator {
    private static final String API_KEY = "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"; // 본인의 키
    private static final String TAG = "StreamingTranslator";

    public interface StreamCallback {
        void onChunk(String chunk);
        void onComplete();
        void onError(String error);
    }

    public static void translate(String inputText, StreamCallback callback) {
        OkHttpClient client = new OkHttpClient();

        JSONObject body = new JSONObject();
        try {
            body.put("model", "gpt-4o-mini");

            body.put("stream", true);
            body.put("messages", new org.json.JSONArray()
                    .put(new JSONObject()
                            .put("role", "system")
                            .put("content", "다음 텍스트를 한국어로 번역해주세요. 서술은 부드럽게, 대사는 자연스럽게."))
                    .put(new JSONObject()
                            .put("role", "user")
                            .put("content", inputText))
            );
        } catch (Exception e) {
            callback.onError("JSON 구성 오류: " + e.getMessage());
            return;
        }

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(body.toString(), MediaType.get("application/json")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("요청 실패: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("응답 실패: " + response.code());
                    return;
                }

                BufferedSource source = response.body().source();
                try {
                    while (!source.exhausted()) {
                        String line = source.readUtf8LineStrict();
                        if (line.startsWith("data: ")) {
                            String jsonStr = line.substring(6).trim();
                            if (jsonStr.equals("[DONE]")) {
                                callback.onComplete();
                                break;
                            }
                            JSONObject chunkJson = new JSONObject(jsonStr);
                            JSONObject delta = chunkJson
                                    .getJSONArray("choices")
                                    .getJSONObject(0)
                                    .optJSONObject("delta");

                            if (delta != null) {
                                String content = delta.optString("content", "");
                                if (!content.isEmpty()) {
                                    callback.onChunk(content);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    callback.onError("스트리밍 파싱 오류: " + e.getMessage());
                } finally {
                    response.close();
                }
            }
        });

    }
    public static void retranslate(String inputText, StreamCallback callback) {
        OkHttpClient client = new OkHttpClient();

        JSONObject body = new JSONObject();
        try {
            body.put("model", "gpt-4o-mini");

            body.put("stream", true);
            body.put("messages", new org.json.JSONArray()
                    .put(new JSONObject()
                            .put("role", "system")
                            .put("content", "원문을 한국어로 처음부터 다시 번역해주세요."))
                    .put(new JSONObject()
                            .put("role", "user")
                            .put("content", inputText))
            );
        } catch (Exception e) {
            callback.onError("JSON 구성 오류: " + e.getMessage());
            return;
        }

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(body.toString(), MediaType.get("application/json")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("요청 실패: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("응답 실패: " + response.code());
                    return;
                }

                BufferedSource source = response.body().source();
                try {
                    while (!source.exhausted()) {
                        String line = source.readUtf8LineStrict();
                        if (line.startsWith("data: ")) {
                            String jsonStr = line.substring(6).trim();
                            if (jsonStr.equals("[DONE]")) {
                                callback.onComplete();
                                break;
                            }
                            JSONObject chunkJson = new JSONObject(jsonStr);
                            JSONObject delta = chunkJson
                                    .getJSONArray("choices")
                                    .getJSONObject(0)
                                    .optJSONObject("delta");

                            if (delta != null) {
                                String content = delta.optString("content", "");
                                if (!content.isEmpty()) {
                                    callback.onChunk(content);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    callback.onError("스트리밍 파싱 오류: " + e.getMessage());
                } finally {
                    response.close();
                }
            }
        });
    }
}
