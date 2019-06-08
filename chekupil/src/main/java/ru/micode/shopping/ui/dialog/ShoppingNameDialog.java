package ru.micode.shopping.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import org.apache.commons.lang3.StringUtils;
import ru.micode.shopping.R;
import ru.micode.shopping.model.Shopping;
import ru.micode.shopping.ui.common.IllegalValueException;
import ru.micode.shopping.ui.common.MyDialogAbstract;

/**
 * Created by Petr Gusarov on 19.03.18.
 */
public class ShoppingNameDialog extends MyDialogAbstract<Shopping> {

    private static final String VIEW_ID = ShoppingNameDialog.class.getSimpleName();
    public static final String RESULT_SHOP = "ShoppingNameDialog.result";
    private EditText textField;

    /**
     *
     * @param shopping
     * @return
     */
    public static ShoppingNameDialog newInstance(Shopping shopping) {
        ShoppingNameDialog fragment = new ShoppingNameDialog();
        fragment.setArgumentsToFragment(shopping);
        return fragment;
    }

    @Override
    public String getTagView() {
        return VIEW_ID;
    }

    @Override
    public Dialog initDialog(final Shopping shopping, Bundle bundle) {
        String shoppingName = shopping.getName();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.shopping_name_dialog, null);
        textField = (EditText) view.findViewById(R.id.request_text);
        if (StringUtils.isNotBlank(shoppingName)) {
            textField.setText(shoppingName);
        }
        return new AlertDialog.Builder(getActivity())
            .setTitle(R.string.shopping_name_dialog_title)
            .setView(view)
            .setPositiveButton(R.string.shopping_name_dialog_ok_caption, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        sendResult(Activity.RESULT_OK, RESULT_SHOP, commit(shopping));
                    } catch (IllegalValueException e) {
                        Log.e(VIEW_ID, "", e);
                    }
                }
            })
            .setNegativeButton(R.string.shopping_name_dialog_cancel_caption, null)
            .create();
    }

    @Override
    public Shopping commit(Shopping shopping) throws IllegalValueException {
        shopping.setName(textField.getText().toString());
        return shopping;
    }

    @Override
    public void show(FragmentActivity activity) {
        super.show(activity.getSupportFragmentManager(), VIEW_ID);
    }
}
