package ru.micode.shopping.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import ru.smart.planet.web.Manufacturer;

import java.util.List;

/**
 * Created by Petr Gusarov on 29.03.19.
 */
public interface ManufacturerService {


    @GET("manufacturer/all")
    Call<Manufacturer> getAll();

    @GET("manufacturer/get/{uid}")
    Call<List<Manufacturer>> getOne(@Path("uid") String uid);

}
