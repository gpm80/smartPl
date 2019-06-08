package ru.micode.shopping.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import ru.smart.planet.web.Manufacturer;
import ru.smart.planet.web.Product;

import java.util.List;

/**
 * Created by Petr Gusarov on 29.03.19.
 */
public interface ProductService {

    @GET("product/all")
    Call<List<Product>> getAll();

    @GET("product/cat/{category}")
    Call<List<Product>> getCategory(@Path("category") String category);

    @GET("product/group/{group}")
    Call<List<Product>> getGroup(@Path("group") String group);

    @GET("product/get/{uid}")
    Call<Product> getOne(@Path("uid") String uid);

    @GET("manufacturer/all")
    Call<Manufacturer> getAllManufacturer();
}
