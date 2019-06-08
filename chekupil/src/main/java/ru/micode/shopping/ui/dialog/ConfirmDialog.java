package ru.micode.shopping.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import ru.micode.shopping.context.ApplicationLoader;

/**
 * Построитель диалогов с подтверждением
 * Created by Petr Gusarov on 18.07.18.
 */
public class ConfirmDialog {

    private final Context context;
    private String title;
    private String message;
    private String positiveCaption;
    private String negativeCaption;

    public static ConfirmDialog builder(Context context) {
        return new ConfirmDialog(context);
    }

    private ConfirmDialog(Context context) {
        this.context = context;
        title = "Title";
        message = "Message";
        positiveCaption = "OK";
        negativeCaption = "Cancel";
    }

    public ConfirmDialog title(String title) {
        this.title = title;
        return this;
    }

    public ConfirmDialog message(String message) {
        this.message = message;
        return this;
    }

    public ConfirmDialog positiveCaption(String positiveCaption) {
        this.positiveCaption = positiveCaption;
        return this;
    }

    public ConfirmDialog negativeCaption(String negativeCaption) {
        this.negativeCaption = negativeCaption;
        return this;
    }

    public ConfirmDialog title(int resStringId) {
        return title(context.getText(resStringId).toString());
    }

    public ConfirmDialog message(int resStringId) {
        return message(context.getText(resStringId).toString());
    }

    public ConfirmDialog positiveCaption(int resStringId) {
        return positiveCaption(context.getText(resStringId).toString());
    }

    public ConfirmDialog negativeCaption(int resStringId) {
        return negativeCaption(context.getText(resStringId).toString());
    }

    public AlertDialog create(DialogInterface.OnClickListener listener) {
        return new AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveCaption, listener)
            .setNegativeButton(negativeCaption, listener)
            .create();
    }

    public void show(DialogInterface.OnClickListener listener) {
        create(listener).show();
    }

}
