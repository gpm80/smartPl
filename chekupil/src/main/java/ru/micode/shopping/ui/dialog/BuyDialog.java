package ru.micode.shopping.ui.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import org.apache.commons.lang3.StringUtils;
import ru.micode.shopping.R;
import ru.micode.shopping.model.Buy;
import ru.micode.shopping.model.Group;
import ru.micode.shopping.service.GroupService;
import ru.micode.shopping.ui.adapter.BeanArrayAdapter;
import ru.micode.shopping.ui.common.IllegalValueException;
import ru.micode.shopping.ui.common.MyDialogAbstract;
import ru.micode.shopping.ui.common.OptionalLite;

/**
 * Окно редактирования и создания покупки
 * Created by Petr Gusarov on 25.06.18.
 */
public class BuyDialog extends MyDialogAbstract<Buy> {

    public static final String RESULT_BUY = "BuyDialog.resultBuy";
    private static final String LOG_TAG = BuyDialog.class.getSimpleName();
    private static final String VIEW_ID = BuyDialog.class.getSimpleName();
    private EditText nameField, amountField;
    private Spinner measureSpinner;
    private Spinner groupSpinner;

    public static BuyDialog newInstance(Buy buy) {
        BuyDialog fragment = new BuyDialog();
        fragment.setArgumentsToFragment(buy);
        return fragment;
    }

    @Override
    public String getTagView() {
        return VIEW_ID;
    }

    @Override
    public Dialog initDialog(final Buy argBuy, Bundle bundle) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.buy_edit_dialog, null);
        nameField = (EditText) view.findViewById(R.id.buy_edit_name);
        amountField = (EditText) view.findViewById(R.id.buy_edit_amount);
        measureSpinner = (AppCompatSpinner) view.findViewById(R.id.buy_edit_measure);
        groupSpinner = (AppCompatSpinner) view.findViewById(R.id.buy_edit_group);
        bind(argBuy);
        nameField.requestFocus();
        return new AlertDialog.Builder(getContext())
            .setView(view)
            .setPositiveButton(R.string.buy_dialog_ok_caption, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Buy value = commit(argBuy);
                        sendResult(Activity.RESULT_OK, RESULT_BUY, value);
                    } catch (IllegalValueException e) {
                        Log.e(LOG_TAG, "", e);
                    }
                }
            })
            .setNegativeButton(R.string.buy_dialog_cancel_caption, null)
            .setCancelable(false)
            .create();
    }

    @Override
    public Buy commit(Buy currentBuy) throws IllegalValueException {
        currentBuy.setName(nameField.getText().toString());
        String sAmount = amountField.getText().toString();
        if (StringUtils.isBlank(sAmount)) {
            currentBuy.setAmount(0);
        } else {
            try {
                currentBuy.setAmount(Integer.valueOf(sAmount));
            } catch (Exception e) {
                throw new IllegalValueException(getString(R.string.buy_dialog_fail_ammount));
            }
        }
        currentBuy.setMeasure((String) measureSpinner.getSelectedItem());
        Group group = (Group) groupSpinner.getSelectedItem();
        currentBuy.setNameGroup(group.getName());
        currentBuy.setPositionGroup(group.getPosition());
        return currentBuy;
    }

    /**
     * Устанавливает данные в поля формы
     *
     * @param buy покупка
     */
    private void bind(Buy buy) {
        ArrayList<String> measures = new ArrayList<>(
            Arrays.asList("шт.", "г.", "кг.", "л.", "м", "дес.", "бут.", "уп."));
        BeanArrayAdapter<String> measureAdapter = new BeanArrayAdapter<String>(getContext(), measures) {
            @Override
            protected String getPresentValue(String bean) {
                return bean;
            }
        };
        List<Group> joinGroups = GroupService.getInstance(getContext())
            .findAllAndJoinShopping(buy.getShopping(), buy.getGroup(false));
        BeanArrayAdapter<Group> groupAdapter = new BeanArrayAdapter<Group>(getContext(), joinGroups) {
            @Override
            protected String getPresentValue(Group bean) {
                return bean.getName();
            }
        };
        measureSpinner.setAdapter(measureAdapter);
        groupSpinner.setAdapter(groupAdapter);
        // Установка данных
        // Наименование
        nameField.setText(OptionalLite.ofNullable(buy.getName()).orElse(""));
        // Количество
        if (buy.getAmount() == 0) {
            amountField.getText().clear();
        } else {
            amountField.setText(String.valueOf(buy.getAmount()));
        }
        // Ед. изм.
        String measure = buy.getMeasure();
        if (StringUtils.isNotBlank(measure) && !measureAdapter.contains(measure)) {
            measureAdapter.add(measure);
        }
        int index = measureAdapter.getIndexByBean(measure);
        if (index >= 0) {
            measureSpinner.setSelection(index);
        }
        // Группа
        Group group = buy.getGroup(false);
        int indexGroup = groupAdapter.getIndexByBean(group);
        if (indexGroup >= 0) {
            groupSpinner.setSelection(indexGroup);
        } else {
            int i = groupAdapter.getIndexByBean(GroupService.UNDEFINED_GROUP);
            if (i >= 0) {
                groupSpinner.setSelection(i);
            }
        }
    }
}
