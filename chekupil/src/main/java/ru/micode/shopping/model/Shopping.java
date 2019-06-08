package ru.micode.shopping.model;

import java.io.Serializable;

import ru.micode.shopping.ui.adapter.recycler.Selectable;


/**
 * Модель заголовка списка покупок
 * Created by gpm on 17.07.17.
 */
public class Shopping implements Serializable, Selectable {

    private Integer id;
    private String name;
    private int editDate;
    private boolean aNew;
    private String author;
    private String comment;
    /**
     * Требуется купить
     */
    private int required;
    /**
     * Всего записей
     */
    private int total;
    /**
     * Статус выделенной позиции
     */
    private boolean selected;

    public Shopping() {
        editDate = (int) (System.currentTimeMillis() / 1000);
        aNew = false;
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

    public int getEditDate() {
        return editDate;
    }

    public void setEditDate(int editDate) {
        this.editDate = editDate;
    }

    public boolean isNew() {
        return aNew;
    }

    public void setNew(boolean isNew) {
        this.aNew = isNew;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRequired() {
        return required;
    }

    public void setRequired(int required) {
        this.required = required;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "ShoppingList{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", editDate=" + editDate +
            ", aNew=" + aNew +
            ", author='" + author + '\'' +
            ", comment='" + comment + '\'' +
            '}';
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean b) {
        selected = b;
    }

    @Override
    public boolean negative() {
        selected = !selected;
        return selected;
    }
}
