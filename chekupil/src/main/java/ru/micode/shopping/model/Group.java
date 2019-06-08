package ru.micode.shopping.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import ru.micode.shopping.ui.adapter.recycler.Selectable;

/**
 * Группы записей в списке
 * Created by Petr Gusarov on 20.04.18.
 */
public class Group implements Comparable<Group>, Serializable, Selectable {

    private Integer id;
    private String name;
    private int position;
    /**
     * Статус выделеного экземпляра
     */
    private boolean selected;

    public Group() {
    }

    public Group(String name, int position) {
        this();
        this.name = name;
        this.position = position;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean negative() {
        this.selected = !this.selected;
        return selected;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("name", name)
            .append("position", position)
            .append("selected", selected)
            .toString();
    }

    @Override
    public int compareTo(Group o) {
        return (o == null) ? -1 : new CompareToBuilder()
            .append(position, o.getPosition())
            .append(name, o.getName())
            .toComparison();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group)) return false;
        Group group = (Group) o;
        return new EqualsBuilder()
            .append(StringUtils.upperCase(name), StringUtils.upperCase(group.name))
//            .append(position, group.position)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(StringUtils.upperCase(name))
//            .append(position)
            .toHashCode();
    }
}
