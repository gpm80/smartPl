package ru.micode.shopping.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import ru.micode.shopping.service.GroupService;

/**
 * Модель записи в списке покупок
 * Created by gpm on 14.07.17.
 */
public class Buy implements Serializable, Comparable<Buy> {

    /**
     * ID
     */
    private Integer id;
    /**
     * Наименование позиции
     */
    private String name;
    /**
     * Количество
     */
    private int amount;
    /**
     * Ед. измерения
     */
    private String measure;
    /**
     * Флаг того что товар куплен
     */
    private boolean done;

    /**
     * Наименование группы
     */
    private String nameGroup;
    /**
     * Позиция группы в сортировке
     */
    private int positionGroup;

    private final Group group;

    /**
     * Родительский список
     */
    private Shopping shopping;

    public Buy() {
        done = false;
        group = new Group();
    }

    public Buy(String name, String nameGroup, int positionGroup) {
        this();
        this.name = name;
        setNameGroup(nameGroup);
        setPositionGroup(positionGroup);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public Shopping getShopping() {
        return shopping;
    }

    public void setShopping(Shopping shopping) {
        this.shopping = shopping;
    }

    public String getNameGroup() {
        return nameGroup;
    }

    public void setNameGroup(String nameGroup) {
        group.setName(nameGroup);
        this.nameGroup = nameGroup;
    }

    public int getPositionGroup() {
        return positionGroup;
    }

    public void setPositionGroup(int positionGroup) {
        group.setPosition(positionGroup);
        this.positionGroup = positionGroup;
    }

    /**
     * Возвращает группу покупки
     *
     * @param doneCheck проверка отмеченного товара. Если true и товар кулен,
     *                  то возвращается {@link GroupService#DONE_GROUP}. Иначе
     *                  возвращается текущее значение группы
     * @return
     */
    public Group getGroup(boolean doneCheck) {
        if (doneCheck && isDone()) {
            return GroupService.DONE_GROUP;
        }
        return group;
    }

    /**
     * Возвращает ID родительского списка (если он установлен)
     *
     * @return
     */
    public Integer getShoppingId() {
        if (shopping != null) {
            return shopping.getId();
        }
        return null;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("name", name)
            .append("amount", amount)
            .append("nameGroup", nameGroup)
            .append("positionGroup", positionGroup)
            .append("done", done)
            .toString();
    }

    /**
     * Уникальный ключ для поиска
     *
     * @return
     */
    @Deprecated
    public String toKeyForSearch() {
        return String.valueOf(getName()).toLowerCase() + "&" + String.valueOf(getNameGroup()).toLowerCase();
    }

    @Override
    public int compareTo(Buy o) {
        return (o == null) ? -1 : new CompareToBuilder()
            .append(getGroup(true), o.getGroup(true))
            .append(name, o.getName())
            .toComparison();
    }
}
