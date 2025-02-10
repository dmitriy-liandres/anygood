package store.anygood;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatGPTClient {
    private static final String API_KEY = "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"; // Replace with your actual key
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    private static final String MODEL = "gpt-4o-mini";
    private static final OkHttpClient client = new OkHttpClient();

    private static final int MAX_QUESTIONS = 10;
    private static final int MIN_QUESTIONS = 5;

    public interface QuestionsCallback {
        void onQuestionsReceived(Question questions);

        void onError(String error);
    }

    public interface RecommendationsCallback {
        void onRecommendationsReceived(List<Product> recommendedProducts);

        void onError(String error);
    }


    // Updated recommendations method: instruct ChatGPT to return output in the language specified.
    public static void getProductRecommendations(String initialQuery,
                                                 String selectedCountry,
                                                 String selectedLanguage,
                                                 List<QuestionWithAnswer> questionsWithAnswers,
                                                 String additionalInfo,
                                                 RecommendationsCallback callback) {
        // conversation should include lines like "Language: Russian" and "Country: Russia"
        // so that ChatGPT is fully aware of both parameters.


        StringBuilder conversationSoFar = new StringBuilder();
        conversationSoFar.append("You are a sell consultant that sells all kings of goods in ").append(selectedCountry).append(".\n");
        conversationSoFar.append("The user is looking for: ").append(initialQuery).append(" to buy.\n");


        if (questionsWithAnswers.size() > 0) {
            conversationSoFar.append("Previous questions and answers:\n");
            for (int i = 0; i < questionsWithAnswers.size(); i++) {
                QuestionWithAnswer questionWithAnswer = questionsWithAnswers.get(i);
                conversationSoFar.append("Question #").append(i).append(": ").append(questionWithAnswer.getQuestion().getText()).append("\n")
                        .append("Answer: ").append(questionWithAnswer.getAnswer()).append("\n");
            }
        }
        conversationSoFar.append("Additional info from the  buyer:\n");
        conversationSoFar.append(additionalInfo).append("\n");



        conversationSoFar.append("Recommend 3 products using ").
                append(selectedLanguage).
                append(" language that best match the user's query. The user is from ").
                append(selectedCountry).
                append(".\n");
        conversationSoFar.append("Return the result in JSON format as an array of objects. ");
        conversationSoFar.append("Each object should have the following keys: 'name' (the product's name) and 'keyword' ");
        conversationSoFar.append("(a search keyword to use for generating an Amazon link). ");
        conversationSoFar.append("Do not include any extra text. Consider brand availability or shipping restrictions in the specified country.");


        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", MODEL);

            // Build messages array
            JSONArray messages = new JSONArray();

            // System role: explicitly mention that we want the output in the conversation’s specified language,
            // and that the user is from a specific country.
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content",
                    "You are an assistant that recommends products based on user input. " +
                            "Return all output in the language and for the country specified in the conversation."
            );
            messages.put(systemMessage);

            // User message: the actual prompt
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", conversationSoFar.toString());
            messages.put(userMessage);

            jsonBody.put("messages", messages);
        } catch (Exception e) {
            callback.onError(e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Error: " + response);
                    return;
                }
                String responseBody = response.body().string();
                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONArray choices = jsonResponse.getJSONArray("choices");
                    JSONObject message = choices.getJSONObject(0).getJSONObject("message");
                    String content = message.getString("content");

                    // Strip out Markdown code fences if present
                    String jsonContent = content.trim();
                    if (jsonContent.startsWith("```")) {
                        int firstNewline = jsonContent.indexOf("\n");
                        int lastBackticks = jsonContent.lastIndexOf("```");
                        if (firstNewline != -1 && lastBackticks != -1 && lastBackticks > firstNewline) {
                            jsonContent = jsonContent.substring(firstNewline, lastBackticks).trim();
                        }
                    }

                    JSONArray productsArray = new JSONArray(jsonContent);
                    List<Product> productList = new ArrayList<>();
                    for (int i = 0; i < productsArray.length(); i++) {
                        JSONObject prodObj = productsArray.getJSONObject(i);
                        String name = prodObj.getString("name");
                        String keyword = prodObj.getString("keyword");
                        // Price is ignored in this version
                        productList.add(new Product(name, "", keyword));
                    }
                    callback.onRecommendationsReceived(productList);
                } catch (Exception e) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }


    public static void generateNextQuestion(String initialQuery,
                                            String selectedCountry,
                                            String selectedLanguage,
                                            List<QuestionWithAnswer> questionsWithAnswers, NextQuestionCallback callback) {
        // conversationSoFar might look like:
        // "Initial Query: I want a new smartphone.\n" +
        // "Question #1: Do you have a preferred brand?\nAnswer: Apple\n" +
        // "Question #2: ...\nAnswer: ...\n"
        StringBuilder conversationSoFar = new StringBuilder();
        conversationSoFar.append("You are a sell consultant that sells all kings of goods in ").append(selectedCountry).append(".\n");
        conversationSoFar.append("The user is looking for: ").append(initialQuery).append(" to buy.\n");
        conversationSoFar.append("Note that the user doesn't know tech specification, so you should ask about how the user is going to use the good he is buying.\n");
        conversationSoFar.append("You will ask one question at a time in ").append(selectedLanguage).append(".\n");
        conversationSoFar.append("Ask not less than ").append(MIN_QUESTIONS)
                .append(" questions and not more than ").append(MAX_QUESTIONS).append(".\n");
        conversationSoFar.append("At the end you should give 3 recommendations.\n");
        if (questionsWithAnswers.size() > 0) {
            conversationSoFar.append("Previous questions and answers:\n");
            for (int i = 0; i < questionsWithAnswers.size(); i++) {
                QuestionWithAnswer questionWithAnswer = questionsWithAnswers.get(i);
                conversationSoFar.append("Question #").append(i).append(": ").append(questionWithAnswer.getQuestion().getText()).append("\n")
                        .append("Answer: ").append(questionWithAnswer.getAnswer()).append("\n");
            }
            ;
        }


        conversationSoFar.append("Please generate exactly one new question in JSON format with keys:\n");
        conversationSoFar.append("\"question\" (the question text),\n");
        conversationSoFar.append("\"options\" (an array of possible multiple-choice options).\n");
        conversationSoFar.append("\"allowFreeText\" set to true if you want to allow a free text answer. Do not add questions like \"Other\" if allowFreeText equals true\n");
        conversationSoFar.append("\"isLast\" set to true if you are sure that this question should be the last one and you will have enough info. If you are not 100% sure, set isLast to false.\n");
        conversationSoFar.append("Do not include anything else. Return only JSON, no additional text.");


        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", MODEL);  // or "gpt-4" or "gpt-3.5-turbo-16k"
            JSONArray messages = new JSONArray();

            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", conversationSoFar.toString());
            messages.put(systemMsg);

            jsonBody.put("messages", messages);
        } catch (Exception e) {
            callback.onError(e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Error: " + response);
                    return;
                }
                String responseBody = response.body().string();
                // Now parse with parseChatGPTResponse
                parseChatGPTResponse(responseBody, new QuestionsCallback() {
                    @Override
                    public void onQuestionsReceived(Question question) {
                        // Pass along to original callback
                        callback.onQuestionReceived(question);
                    }

                    @Override
                    public void onError(String error) {
                        // Pass errors as well
                        callback.onError(error);
                    }
                });
            }
        });
    }

    public static void parseChatGPTResponse(String responseBody, QuestionsCallback callback) {
        try {
            // 1. Parse the entire root JSON
            JSONObject jsonResponse = new JSONObject(responseBody);

            // 2. Extract the array of choices
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.length() == 0) {
                callback.onError("No choices found in response");
                return;
            }

            // 3. Get the first choice, then get "message" -> "content"
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject message = firstChoice.getJSONObject("message");

            // If there's a "refusal" key, you might handle that as well
            // e.g., if (message.has("refusal")) { ... }

            String content = message.optString("content", "").trim();
            if (content.isEmpty()) {
                callback.onError("No content in the message");
                return;
            }

            // 4. Strip out any potential markdown fences (```json ... ```)
            content = stripMarkdownFences(content);


            JSONObject qObj = new JSONObject(content);

            // The new response uses "question" as the key
            String text = qObj.optString("question", "");
            // If we previously used "allowFreeText", we can set a default
            boolean allowFreeText = qObj.optBoolean("allowFreeText", false);
            boolean isLast = qObj.optBoolean("isLast", false);

            List<String> options = new ArrayList<>();
            if (qObj.has("options")) {
                JSONArray opts = qObj.getJSONArray("options");
                for (int j = 0; j < opts.length(); j++) {
                    options.add(opts.getString(j));
                }
            } else if (qObj.has("answerOptions")) {
                JSONArray opts = qObj.getJSONArray("answerOptions");
                for (int j = 0; j < opts.length(); j++) {
                    options.add(opts.getString(j));
                }
            }

            // 7. Return the parsed questions
            callback.onQuestionsReceived(new Question(text, options, allowFreeText, isLast));
        } catch (Exception e) {
            callback.onError("Parsing error: " + e.getMessage());
        }
    }

    // Helper to remove code fences if ChatGPT returns them
    private static String stripMarkdownFences(String raw) {
        String trimmed = raw.trim();
        if (trimmed.startsWith("```")) {
            int firstNewLine = trimmed.indexOf("\n");
            int lastFence = trimmed.lastIndexOf("```");
            if (firstNewLine != -1 && lastFence != -1 && lastFence > firstNewLine) {
                trimmed = trimmed.substring(firstNewLine, lastFence).trim();
            }
        }
        return trimmed;
    }


    private static JSONObject extractJsonObject(String content) throws JSONException {
        // If ChatGPT returns JSON as a string, parse it to a JSONObject
        return new JSONObject(content.trim());
    }

    public interface NextQuestionCallback {
        void onQuestionReceived(Question question);

        void onError(String error);
    }


}
