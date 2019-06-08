package ru.micode.shopping.ui.adapter;

import android.content.Context;
import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 * Модель элемента меню навигатора
 * Created by Petr Gusarov on 01.03.18.
 */
public class NavigatorBean implements Comparable<NavigatorBean> {

    /**
     * Позиция в списке
     */
    private final int position;
    /**
     * Id ресурса изображения
     */
    private int imageResId;
    /**
     * Заголовок
     */
    private final int captionId;
    /**
     * Описание
     */
    private final int descriptionId;
    /**
     * Доп. надпись
     */
    private String label;
    /**
     * Класс активности
     */
    private final Class<?> activityClass;


    /**
     * Конструктор
     *
     * @param captionId     id ресурса заголовка
     * @param activityClass класс
     */
    public NavigatorBean(int position, int imageResId, int captionId, int descriptionId, Class<?> activityClass) {
        this.position = position;
        this.captionId = captionId;
        this.descriptionId = descriptionId;
        this.activityClass = activityClass;
        this.imageResId = imageResId;
    }

    public String getCaption(Context context) {
        return String.valueOf(context.getResources().getText(captionId));
    }

    public String getDescription(Context context) {
        return String.valueOf(context.getResources().getText(descriptionId));
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Class<?> getActivityClass() {
        return activityClass;
    }

    @Override
    public int compareTo(NavigatorBean o) {
        if (o == null) return -1;
        if (this == o) return 0;
        return new CompareToBuilder()
            .append(position, o.position)
            .toComparison();

    }
}
