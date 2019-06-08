package ru.micode.shopping.rest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import ru.micode.shopping.model.ex.ExRecipe;

/**
 * Created by Petr Gusarov on 29.03.19.
 */
public interface RecipeService {

    @GET("recipes/all")
    Call<List<ExRecipe>> getStart();

    @GET("recipes/next/{time}")
    Call<List<ExRecipe>> getNext(@Path("time") long time);

    @GET("recipes/one/{uid}")
    Call<ExRecipe> getOne(@Path("uid") String uid);

    @POST("recipes/favorite")
    Call<List<ExRecipe>> getFavorite(@Body List<String> favoriteIds);
}
