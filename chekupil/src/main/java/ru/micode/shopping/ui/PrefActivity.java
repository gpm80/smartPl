package ru.micode.shopping.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import ru.micode.shopping.R;
import ru.micode.shopping.context.ApplicationLoader;

/**
 * Created by Petr Gusarov on 30.08.18.
 */
public class PrefActivity extends AppCompatActivity {

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.navigator_setting);
        //
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new PrefFragment();
            fragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
        }
    }

    /**
     * Фрагмент настроек
     */
    public static class PrefFragment extends PreferenceFragmentCompat {

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            Context context = ApplicationLoader.applicationContext;
            PreferenceScreen rootScreen = getPreferenceManager().createPreferenceScreen(context);
            setPreferenceScreen(rootScreen);
            {// Основные настройки
                PreferenceCategory catCommon = new PreferenceCategory(context);
                catCommon.setKey("common");
                catCommon.setTitle(R.string.setting_cat_common_title);
                rootScreen.addPreference(catCommon);
                {// Параметры фонового процесса
                    final CheckBoxPreference notificationCheck = new CheckBoxPreference(context);
                    notificationCheck.setKey(ApplicationLoader.PrefKey.NOTIFICATION_ON);
                    // fixme Заменить на локальную
                    notificationCheck.setIcon(android.R.drawable.ic_menu_view);
                    notificationCheck.setTitle(R.string.setting_check_notification_title);
                    notificationCheck.setSummaryOn(R.string.setting_check_notification_on_summary);
                    notificationCheck.setSummaryOff(R.string.setting_check_notification_off_summary);
                    // Интервал
                    final ListPreference intervalNotifList = new ListPreference(context);
                    intervalNotifList.setKey(ApplicationLoader.PrefKey.INTERVAL_NOTIF);
                    // fixme Заменить на локальную
                    intervalNotifList.setIcon(android.R.drawable.ic_menu_recent_history);
                    intervalNotifList.setTitle(R.string.setting_interval_list_title);
                    intervalNotifList.setSummary(R.string.setting_interval_list_summary);
                    intervalNotifList.setEntries(R.array.entries);
                    intervalNotifList.setEntryValues(ApplicationLoader.PrefKey.INTERVAL_VALUES);

                    // Добавляем в контекст
                    catCommon.addPreference(notificationCheck);
                    catCommon.addPreference(intervalNotifList);
                    intervalNotifList.setDependency(ApplicationLoader.PrefKey.NOTIFICATION_ON);

                    notificationCheck.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            boolean checked = notificationCheck.isChecked();
                            intervalNotifList.setEnabled(checked);
                            if (checked) {
                                ApplicationLoader.startPushService();
                            } else {
                                ApplicationLoader.stopPushService();
                            }
                            return false;
                        }
                    });
                    intervalNotifList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            if (newValue != null && newValue instanceof String) {
                                Log.i("Pref", "setInterval=" + newValue.toString());
                                ApplicationLoader.setIntervalNotification(newValue.toString());
                            }
                            return true;
                        }
                    });
                }
            }
            {// Стиль
                PreferenceCategory catTheme = new PreferenceCategory(context);
                catTheme.setKey("theme");
                catTheme.setTitle(R.string.setting_cat_theme_title);
                rootScreen.addPreference(catTheme);
                {//
                    final CheckBoxPreference darkThemeCheck = new CheckBoxPreference(context);
                    darkThemeCheck.setKey(ApplicationLoader.PrefKey.IS_DARK_THEME);
                    // fixme Заменить на локальную
                    darkThemeCheck.setIcon(android.R.drawable.ic_menu_gallery);
                    darkThemeCheck.setTitle(R.string.setting_check_dark_theme_title);
                    darkThemeCheck.setSummaryOn(R.string.setting_check_dark_theme_on_summary);
                    darkThemeCheck.setSummaryOff(R.string.setting_check_dark_theme_off_summary);
                    catTheme.addPreference(darkThemeCheck);
                    darkThemeCheck.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            if (newValue instanceof Boolean) {
                                ApplicationLoader.changeThemeForce(getActivity(), (Boolean) newValue);
                            }
                            return true;
                        }
                    });
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

