package store.anygood;


import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import store.anygood.model.dto.GenerateQuestionRequestDTO;
import store.anygood.model.dto.ProductDTO;
import store.anygood.model.dto.ProductRecommendationRequestDTO;
import store.anygood.model.dto.QuestionDTO;
import store.anygood.model.dto.QuestionWithAnswerDTO;
import store.anygood.model.dto.TelephoneInfoDTO;

public class ChatGPTClient {

    // Change this to your backend's URL
    private static final String BASE_URL = "http://10.0.2.2:8080";
    //private static final String BASE_URL = "http://ec2-3-88-237-209.compute-1.amazonaws.com:8080";//http://10.0.2.2:8080";
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    private OkHttpClient client;
    private String jwtToken;

    private static final String SEARCH_SESSION_ID_HEADER = "searchSessionId";



    public ChatGPTClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS) // Connection timeout
                .readTimeout(60, TimeUnit.SECONDS)    // Read timeout
                .writeTimeout(60, TimeUnit.SECONDS)   // Write timeout
                .build();
    }

    /**
     * Logs in the user and retrieves a JWT token.
     * The callback will receive the backend's response.
     */
    public void login(TelephoneInfoDTO telephoneInfo, String username, String password, Callback callback) {

        RequestBody body = RequestBody.create(new Gson().toJson(telephoneInfo), JSON_MEDIA_TYPE);

        HttpUrl url = HttpUrl.parse(BASE_URL + "/auth/login").newBuilder()
                .addQueryParameter("username", username)
                .addQueryParameter("password", password)
                .build();

        // POST request with an empty body
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Assume the response contains a JSON like {"token": "jwt_token"}
                    String jsonResponse = response.body().string();
                    try {
                        String token = new JSONObject(jsonResponse).getString("token");
                        setJwtToken(token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                callback.onResponse(call, response);
            }
        });
    }

    /**
     * Calls the backend to generate the next question.
     * The caller must supply a list of QuestionWithAnswer objects.
     */
    public void generateNextQuestion(
                                     String searchSessionId,
                                     String initialQuery,
                                     String selectedCountry,
                                     String selectedLanguage,
                                     QuestionWithAnswerDTO questionWithAnswers,
                                     NextQuestionCallback callback) {
        GenerateQuestionRequestDTO generateQuestionRequest = new GenerateQuestionRequestDTO();
        generateQuestionRequest.setInitialQuery(initialQuery);
        generateQuestionRequest.setSelectedCountry(selectedCountry);
        generateQuestionRequest.setSelectedLanguage(selectedLanguage);
        generateQuestionRequest.setQuestionWithAnswers(questionWithAnswers);

        RequestBody body = RequestBody.create(new Gson().toJson(generateQuestionRequest), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/generate_next_question")
                .post(body)
                .addHeader("Authorization", "Bearer " + jwtToken)
                .addHeader(SEARCH_SESSION_ID_HEADER, searchSessionId)
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
                String searchSessionId = response.header(SEARCH_SESSION_ID_HEADER);
                String responseBody = response.body().string();
                // Convert the JSON to a NextQuestionResponse object using Gson
                Gson gson = new Gson();
                QuestionDTO result = gson.fromJson(responseBody, QuestionDTO.class);
                callback.onQuestionReceived(searchSessionId, result);

            }
        });
    }


    /**
     * Calls the backend to get product recommendations.
     */
    public void getProductRecommendations(String searchSessionId,
                                          String additionalInfo,
                                          RecommendationsCallback callback) {
        ProductRecommendationRequestDTO productRecommendationRequest = new ProductRecommendationRequestDTO();
        productRecommendationRequest.setAdditionalInfo(additionalInfo);


        RequestBody body = RequestBody.create(new Gson().toJson(productRecommendationRequest), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/get_product_recommendations")
                .post(body)
                .addHeader("Authorization", "Bearer " + jwtToken)
                .addHeader(SEARCH_SESSION_ID_HEADER, searchSessionId)
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
                Gson gson = new Gson();
                Type productListType = new TypeToken<List<ProductDTO>>() {
                }.getType();
                List<ProductDTO> products = gson.fromJson(responseBody, productListType);
                callback.onRecommendationsReceived(products);
            }
        });
    }


    /**
     * Calls the backend to get product recommendations additional.
     */
    public void getProductRecommendationsAdditional(String searchSessionId,
                                                    RecommendationsCallback callback) {

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/get_product_recommendations_additional")
                .post(RequestBody.create("", JSON_MEDIA_TYPE))
                .addHeader("Authorization", "Bearer " + jwtToken)
                .addHeader(SEARCH_SESSION_ID_HEADER, searchSessionId)
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
                Gson gson = new Gson();
                Type productListType = new TypeToken<List<ProductDTO>>() {
                }.getType();
                List<ProductDTO> products = gson.fromJson(responseBody, productListType);
                callback.onRecommendationsReceived(products);
            }
        });
    }

    /**
     * Set the JWT token once you've successfully logged in.
     */
    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }


    public interface RecommendationsCallback {
        void onRecommendationsReceived(List<ProductDTO> recommendedProducts);

        void onError(String error);
    }

    public interface NextQuestionCallback {
        void onQuestionReceived(String searchSessionId, QuestionDTO question);

        void onError(String error);
    }
}
