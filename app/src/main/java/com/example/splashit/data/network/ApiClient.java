package com.example.splashit.data.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.splashit.utils.Constants.BASE_URL;

public class ApiClient {

    private static Retrofit retrofit;

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    /**
     * called to get a reference to Retrofit instance
     *
     * @return retrofit client to be used for API calls
     */
    public static Retrofit getClient() {

        if (retrofit == null) {

            httpClient.addInterceptor(loggingInterceptor);

            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .baseUrl(BASE_URL)
                    .build();
        }

        return retrofit;
    }
}
