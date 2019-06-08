package ru.micode.shopping.model.ex;

/**
 * Модель элемента покупки
 * Created by Petr Gusarov on 17.10.17.
 */
public class ExBuy {

    private String name;
    private Integer amount;
    /**
     * Единицы измерения
     */
    private String measure;
    /**
     * Позиция в сортировке групп
     */
    private Integer position;
    private String nameGroup;

    /**
     * Конструктор
     */
    public ExBuy() {
    }

    /**
     * Конструктор
     *
     * @param name      наименование позиции
     * @param amount    количество
     * @param measure   единицы измерения
     * @param position  позиция в сортировке групп
     * @param nameGroup наименование группы
     */
    public ExBuy(String name, Integer amount, String measure, Integer position, String nameGroup) {
        this.name = name;
        this.amount = amount;
        this.measure = measure;
        this.position = position;
        this.nameGroup = nameGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getNameGroup() {
        return nameGroup;
    }

    public void setNameGroup(String nameGroup) {
        this.nameGroup = nameGroup;
    }

    @Override
    public String toString() {
        return "ExBuy{" +
            "name='" + name + '\'' +
            ", amount=" + amount +
            ", measure=" + measure +
            ", position=" + position +
            ", nameGroup=" + nameGroup +
            '}';
    }
}
