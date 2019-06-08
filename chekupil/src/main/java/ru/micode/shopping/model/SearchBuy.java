package ru.micode.shopping.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.util.Log;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import ru.micode.shopping.R;
import ru.micode.shopping.service.GroupService;

/**
 * Created by Petr Gusarov on 29.10.18.
 */
public class SearchBuy {

    private Set<WrapperBuy> directBuy;
    private final Context context;

    public SearchBuy(Context context, List<Buy> buysInList, List<Group> allGroups) {
        this.context = context;

        this.directBuy = new TreeSet<>();
        init(buysInList, allGroups);
    }

    private void init(List<Buy> buysInList, List<Group> allGroups) {
        Map<String, Integer> inListGroupPosition = new HashMap<>();
        // Сначала добавляем группы из справочника групп
        for (Group group : allGroups) {
            if (group.getName() != null) {
                inListGroupPosition.put(group.getName().toLowerCase().trim(), group.getPosition());
            }
        }
        // Первоначально добавляем то что в списке
        for (Buy buy : buysInList) {
            directBuy.add(new WrapperBuy(buy));
            // Добавляем группы из списка (если справочные совпадут то они заменяться)
            Group group = buy.getGroup(false);
            if (group.getName() != null) {
                inListGroupPosition.put(group.getName().toLowerCase().trim(), group.getPosition());
            }
        }
        InputStreamReader isReader = null;
        try {
            isReader = new InputStreamReader(context.getResources().openRawResource(R.raw.buy_search));
            BufferedReader buf = new BufferedReader(isReader);
            String line;
            while ((line = buf.readLine()) != null) {
                String[] items = line.split(";");
                String nameGroup = items[0].trim();
                Integer position = inListGroupPosition.get(nameGroup.toLowerCase().trim());
                if (position == null) {
                    position = 0;
                }
                for (int i = 1; i < items.length; i++) {
                    if (StringUtils.isNotBlank(items[i])) {
                        directBuy.add(new WrapperBuy(items[i].trim(), nameGroup, position));
                    }
                }
            }
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "", e);
        } finally {
            try {
                if (isReader != null) {
                    isReader.close();
                }
            } catch (Exception e) {
            }
        }
    }

    public void addItem(Buy buy) {
        directBuy.add(new WrapperBuy(buy));
    }

    public List<WrapperBuy> filterBy(String search) {
        List<WrapperBuy> filtered = new ArrayList<>();
        if (StringUtils.isNotBlank(search)) {
            WrapperBuy manual = new WrapperBuy(search, GroupService.UNDEFINED_GROUP.getName(), 0);
            manual.setManual(true);
            filtered.add(manual);
            search = search.trim().toLowerCase();
            for (WrapperBuy wrap : directBuy) {
                if (wrap.contains(search)) {
                    filtered.add(wrap);
                }
            }
        }
        return filtered;
    }


    public List<WrapperBuy> allItems() {
        return new ArrayList<>(directBuy);
    }

    /**
     * Создает уникальный ключ из позиции
     *
     * @param b покупка
     * @return
     */
    static String[] buildKeys(Buy b) {
        if (b == null) {
            return new String[]{"null", "null"};
        }
        String[] keys = new String[2];
        keys[0] = String.valueOf(b.getName()).toLowerCase();
        keys[1] = keys[0] + "&" + String.valueOf(b.getNameGroup()).toLowerCase();
        return keys;
    }

    /**
     * Обертка покупки для поиска
     */
    public static class WrapperBuy implements Comparable<WrapperBuy> {
        private String key;
        private String search;
        private Buy buy;
        private int position;
        private boolean manual;

        public WrapperBuy(String name, String group, int position) {
            this(new Buy(name, group, position));
        }

        public WrapperBuy(Buy buy) {
            this.buy = buy;
            String[] keys = SearchBuy.buildKeys(buy);
            this.search = keys[0];
            this.key = keys[1];
        }

        boolean contains(String value) {
            return search.contains(value);
        }

        public Buy getBuy() {
            return this.buy;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public boolean isManual() {
            return manual;
        }

        public void setManual(boolean manual) {
            this.manual = manual;
        }

        @Override
        public int compareTo(WrapperBuy wrapperBuy) {
            return new CompareToBuilder()
                .append(this.key, wrapperBuy.key)
                .toComparison();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof WrapperBuy))
                return false;
            WrapperBuy that = (WrapperBuy) o;
            return new EqualsBuilder()
                .append(key, that.key)
                .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                .append(key)
                .toHashCode();
        }
    }
}
