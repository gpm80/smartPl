package ru.micode.shopping.ui.common;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

/**
 * Абстрактный класс фрагмента диалога
 * Created by Petr Gusarov on 20.03.18.
 */
@SuppressLint("ValidFragment")
public abstract class AbstractDialogFragment<T> extends DialogFragment {

//    private static final String ARG_BEAN_VALUE = "arg_bean_value";
    private ResultListener<T> resultListener;
    protected T value;

    public AbstractDialogFragment(T value, ResultListener<T> resultListener) {
        this.resultListener = resultListener;
        this.value = prepareValue(value);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return this.onCreateDialog(savedInstanceState, this.value);
    }

    /**
     * Создание диалога и привязка полей
     *
     * @param savedInstanceState бандл
     * @param value              экземпляр переданного объекта
     * @return
     */
    protected abstract Dialog onCreateDialog(Bundle savedInstanceState, T value);

    /**
     * Подготавливает значение к передачи в диалог
     *
     * @param value
     * @return
     */
    protected abstract T prepareValue(T value);

    /**
     * Возвращает обработанное диалогом значение
     *
     * @return
     * @throws IllegalValueException
     */
    protected abstract T getValue() throws IllegalValueException;

    /**
     * Показать диалог
     *
     * @param activity родительская активность
     */
    public abstract void show(FragmentActivity activity);

    protected DialogInterface.OnClickListener getPositiveOnClickListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (resultListener != null) {
                    try {
                        boolean close = resultListener.apply(getValue());
                        if (close) {
                            dismiss();
                        }
                    } catch (IllegalValueException ex) {
                        resultListener.fail(ex.getMessage());
                    }
                }
            }
        };
    }

    protected DialogInterface.OnClickListener getNegativeOnClickListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (resultListener != null) {
                    if (resultListener.close()) {
                        dismiss();
                    }
                } else {
                    dismiss();
                }
            }
        };
    }

    /**
     * Интерфейс возврата результата диалога
     *
     * @param <V> тип возвращаемых данных диалога
     */
    public interface ResultListener<V> {
        boolean apply(V value);

        boolean close();

        void fail(String message);
    }
}
