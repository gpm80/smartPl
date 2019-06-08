package ru.micode.shopping.ui.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import org.apache.commons.lang3.StringUtils;
import ru.micode.shopping.R;
import ru.micode.shopping.service.ServiceException;
import ru.micode.shopping.ui.dialog.ConfirmDialog;

/**
 * Абстрактный класс фрагмента приложения
 * Created by Petr Gusarov on 29.06.18.
 */
public abstract class MyAbstractFragment extends Fragment {

    private Menu currentMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getResIdFragment(), container, false);
        // setHasOptionsMenu делать здесь иначе глючит при переворачивании
        setHasOptionsMenu(hasMenu());
        initFragment(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (hasMenu() && currentMenu == null) {
            inflater.inflate(getResIdMenu(), menu);
            currentMenu = menu;
        }
    }

    /**
     * Возвращает текущее меню
     *
     * @return
     */
    protected Menu getCurrentMenu() {
        return currentMenu;
    }

    protected void finish() {
        getActivity().finish();
    }

    private boolean hasMenu() {
        return getResIdMenu() != 0;
    }

    /**
     * Смена вида списка при пустом содержании
     *
     * @param isEmpty      статус пустого списка
     * @param recyclerView список
     * @param emptyText    текстовый блок
     * @return алтернативное значение параметра {@code isEmpty}
     */
    protected static boolean checkNotViewEmpty(boolean isEmpty, View recyclerView, View emptyText) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
        return !isEmpty;
    }

    /**
     * Обобщенное отображение ошибки
     *
     * @param se
     */
    public void showErrorMessage(ServiceException se) {
        try {
            showErrorMessage(se.getStringResource(getContext()));
        } catch (Exception e) {
        }
    }

    public void showErrorMessage(int resId) {
        showErrorMessage(getString(resId));
    }

    /**
     * Обобщенное отображение ошибки
     *
     * @param message
     */
    public void showErrorMessage(String message) {
        ConfirmDialog confirmDialog = ConfirmDialog.builder(getContext())
            .title(R.string.common_error_title)
            .positiveCaption(R.string.common_error_positive)
            .negativeCaption(null);
        if (StringUtils.isNotBlank(message)) {
            confirmDialog.message(message);
        } else {
            confirmDialog.message(R.string.common_error_empty_message);
        }
        confirmDialog.show(null);
//        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * Обобщенное отображение информации
     *
     * @param resId строковый ресурс
     */
    public void showInfoMessage(int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
    }

    protected abstract int getResIdFragment();

    protected abstract int getResIdMenu();

    protected abstract void initFragment(View view);

    protected abstract void refresh();
}
