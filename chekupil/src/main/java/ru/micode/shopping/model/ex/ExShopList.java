package ru.micode.shopping.model.ex;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель списка покупок
 * Created by Petr Gusarov on 17.10.17.
 */
public class ExShopList {

    private String name;
    private String description;
    private ExRecipient recipient;
    private List<ExBuy> buyList;

    public ExShopList() {
    }

    public ExShopList(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExRecipient getRecipient() {
        if (recipient == null) {
            recipient = new ExRecipient();
        }
        return recipient;
    }

    public void setRecipient(ExRecipient recipient) {
        this.recipient = recipient;
    }

    public List<ExBuy> getBuyList() {
        if (buyList == null) {
            buyList = new ArrayList<>();
        }
        return buyList;
    }

    public void setBuyList(List<ExBuy> buyList) {
        this.buyList = buyList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ExShopList{");
        sb.append("name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", recipient=").append(recipient);
        sb.append(", buyList=").append(buyList);
        sb.append('}');
        return sb.toString();
    }
}
