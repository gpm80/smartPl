package ru.micode.shopping.ui.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import ru.micode.shopping.R;
import ru.micode.shopping.context.ApplicationLoader;

/**
 * Created by Petr Gusarov on 04.05.18.
 */
public class ConfirmFragment extends DialogFragment {

    public enum TypeMessage {
        /**
         * Удаление всех отмеченных позиций
         */
        DELETE_BUY_SELECT,
        /**
         * Отметить все позиции купленными
         */
        SELECT_ALL_ITEMS,
        /**
         * Снять отметки всех купленных позиций
         */
        DESELECT_ALL_ITEMS,
        /**
         * Инверсия отметок товара
         */
        INVERSION_ALL_ITEMS,
        /**
         * Выбор варианта добавления контакта
         */
        CHOOSE_MODE_ADD_FRIEND
    }

    private DialogInterface.OnClickListener listener;
    private TypeMessage type;

    public static ConfirmFragment create(TypeMessage type, DialogInterface.OnClickListener listener) {
        ConfirmFragment confirmFragment = new ConfirmFragment();
        confirmFragment.type = type;
        confirmFragment.listener = listener;
        return confirmFragment;
    }

    public void show(FragmentActivity activity) {
        super.show(activity.getSupportFragmentManager(), type.name());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        switch (type) {
            case DELETE_BUY_SELECT:
                return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.buy_delete_dialog_title)
                    .setMessage(R.string.buy_delete_dialog_message)
                    .setPositiveButton(R.string.buy_delete_dialog_yes, listener)
                    .setNegativeButton(R.string.buy_delete_dialog_no, listener)
                    .create();
            case SELECT_ALL_ITEMS:
                return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.buy_dialog_select_all_title)
                    .setMessage(R.string.buy_dialog_select_all_message)
                    .setPositiveButton(R.string.buy_dialog_select_all_yes, listener)
                    .setNegativeButton(R.string.buy_dialog_select_all_no, listener)
                    .create();
            case DESELECT_ALL_ITEMS:
                return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.buy_dialog_deselect_all_title)
                    .setMessage(R.string.buy_dialog_deselect_all_message)
                    .setPositiveButton(R.string.buy_dialog_deselect_all_yes, listener)
                    .setNegativeButton(R.string.buy_dialog_deselect_all_no, listener)
                    .create();
            case INVERSION_ALL_ITEMS:
                return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.buy_dialog_inversion_all_title)
                    .setMessage(R.string.buy_dialog_inversion_all_message)
                    .setPositiveButton(R.string.buy_dialog_inversion_all_yes, listener)
                    .setNegativeButton(R.string.buy_dialog_inversion_all_no, listener)
                    .create();
            case CHOOSE_MODE_ADD_FRIEND:
                return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.friend_dialog_choose_title)
                    .setMessage(ApplicationLoader.replaceTags(R.string.friend_dialog_choose_message))
                    .setPositiveButton(R.string.friend_dialog_choose_yes, listener)
                    .setNegativeButton(R.string.friend_dialog_choose_no, listener)
                    .create();
            default:
                return new AlertDialog.Builder(getActivity()).create();
        }
    }
}
