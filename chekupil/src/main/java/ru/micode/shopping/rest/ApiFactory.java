package ru.micode.shopping.rest;

import java.util.concurrent.TimeUnit;

import android.support.annotation.NonNull;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.micode.shopping.BuildConfig;

/**
 * Created by gpm on 30.05.17.
 */

public class ApiFactory {

    private static final int CONNECT_TIMEOUT = 20;
    private static final int WRITE_TIMEOUT = 60;
    private static final int TIMEOUT = 60;

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .build();


    @NonNull
    public static RecipeService getRecipeService() {
        return getRetrofit().create(RecipeService.class);
    }

    @NonNull
    private static Retrofit getRetrofit() {
        return new Retrofit.Builder()
//            .baseUrl("")
            .baseUrl(BuildConfig.API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .client(CLIENT)
            .build();
    }
}
