package ru.micode.shopping.model;

import java.io.Serializable;

/**
 * Экземпляр избранного рецепта
 * Created by Petr Gusarov on 05.04.19.
 */
public class Favorite implements Serializable {

    private String recipeUid;
    private int time;

    public Favorite() {
    }

    public Favorite(String recipeUid) {
        this.recipeUid = recipeUid;
    }

    public String getRecipeUid() {
        return recipeUid;
    }

    public void setRecipeUid(String recipeUid) {
        this.recipeUid = recipeUid;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
