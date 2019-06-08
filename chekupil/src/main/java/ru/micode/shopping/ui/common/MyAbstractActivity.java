package ru.micode.shopping.ui.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import ru.micode.shopping.R;
import ru.micode.shopping.context.ApplicationLoader;

/**
 * Абстрактный класс активности c фрагментами
 * Created by Petr Gusarov on 22.02.18.
 */
public abstract class MyAbstractActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ApplicationLoader.isDarkTheme()) {
            setTheme(R.style.CheTheme_Dark);
        } else {
            setTheme(R.style.CheTheme);
        }
        setContentView(R.layout.abstract_fragment_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (ApplicationLoader.isDarkTheme()) {
            toolbar.setPopupTheme(R.style.CheTheme_PopupOverlay_Dark);
        } else {
            toolbar.setPopupTheme(R.style.CheTheme_PopupOverlay_Light);
        }
        setSupportActionBar(toolbar);
        if (isViewBackButton()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = createFragment();
            fragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
        }
    }

    /**
     * Абстрактный метод получения фрагмента
     *
     * @return
     */
    protected abstract Fragment createFragment();

    protected abstract boolean isViewBackButton();

    /**
     * Запрашивается при закрытии активности
     *
     * @return true если можно закрыть активность
     */
    @Deprecated
    protected abstract boolean isExit();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment != null && currentFragment instanceof CloseFragmentListener) {
            if (((CloseFragmentListener) currentFragment).beforeClose()) {
                super.onBackPressed();
            }
        } else {
            if (isExit()) {
                super.onBackPressed();
            }
        }
    }
}
