package ru.micode.shopping.ui.common;

import java.io.Serializable;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * Created by Petr Gusarov on 27.06.18.
 */
public abstract class MyDialogAbstract<T extends Serializable> extends DialogFragment {

    protected static final String ARG_BEAN = "common.argument";

    public MyDialogAbstract() {
    }

    /**
     * Установить фрагмент для возврата данных
     *
     * @param fragment    родительский фрагмент
     * @param requestCode код запроса
     * @return
     */
    public MyDialogAbstract target(Fragment fragment, int requestCode) {
        setTargetFragment(fragment, requestCode);
        return this;
    }

    /**
     * Устанавливает экземпляр в фрагмент
     *
     * @param bean экземпляр класса
     */
    protected void setArgumentsToFragment(T bean) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_BEAN, bean);
        setArguments(args);
    }

    public abstract String getTagView();

    public abstract Dialog initDialog(T bean, Bundle bundle);

    public abstract T commit(T sourceValue) throws IllegalValueException;

    public void show(FragmentActivity activity) {
        super.show(activity.getSupportFragmentManager(), getTagView());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        T serializable = (T) getArguments().getSerializable(ARG_BEAN);
        return initDialog(serializable, savedInstanceState);

    }

    /**
     * Отправляет результат родительскому фрагменту
     *
     * @param resultCode
     * @param keyResult
     * @param value
     * @return
     */
    protected boolean sendResult(int resultCode, String keyResult, T value) {
        try {
            Fragment tf = getTargetFragment();
            if (tf != null) {
                Intent intent = new Intent();
                intent.putExtra(keyResult, value);
                tf.onActivityResult(getTargetRequestCode(), resultCode, intent);
                return true;
            }
        } catch (Exception e) {
            Log.e(getTagView(), "", e);
        }
        return false;
    }

}
