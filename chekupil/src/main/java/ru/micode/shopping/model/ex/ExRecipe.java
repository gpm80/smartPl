package ru.micode.shopping.model.ex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 08.06.19.
 */
public class ExRecipe {

    /**
     * ID в couchDb для добавления в избранное
     */
    private String id;
    /**
     * Название рецепта
     */
    private String title;
    /**
     * Описание приготовления
     */
    private String description;
    /**
     * Группа рецепта
     */
    private String group;
    /**
     * Счетчик количества загрузок списка
     */
    private int take;
    /**
     * Время изменения рецепта
     */
    private long editTime;
    /**
     * Список продуктов для приготовления
     */
    private List<ExBuy> exBuyList;
    /**
     * Список изображений прикрепленных к рецепту
     */
    private List<String> images;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public long getEditTime() {
        return editTime;
    }

    public void setEditTime(long editTime) {
        this.editTime = editTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<String> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getTake() {
        return take;
    }

    public void setTake(int take) {
        this.take = take;
    }

    public List<ExBuy> getExBuyList() {
        if (exBuyList == null) {
            exBuyList = new ArrayList<>();
        }
        return exBuyList;
    }

    public void setExBuyList(List<ExBuy> exBuyList) {
        this.exBuyList = exBuyList;
    }
}
