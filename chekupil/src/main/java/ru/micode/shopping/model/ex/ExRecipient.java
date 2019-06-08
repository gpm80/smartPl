package ru.micode.shopping.model.ex;

/**
 * Информация о адресатах
 * Created by Petr Gusarov on 17.10.17.
 */
public class ExRecipient {

    /**
     * Кто отправил
     */
    private String fromUid;
    /**
     * Кому отправил
     */
    private String toUid;

    public ExRecipient() {
    }

    public ExRecipient(String fromUid, String toUid) {
        this.fromUid = fromUid;
        this.toUid = toUid;
    }

    public String getFromUid() {
        return fromUid;
    }

    public void setFromUid(String fromUid) {
        this.fromUid = fromUid;
    }

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    @Override
    public String toString() {
        return "ExRecipient{" +
                "fromUid='" + fromUid + '\'' +
                ", toUid='" + toUid + '\'' +
                '}';
    }
}
