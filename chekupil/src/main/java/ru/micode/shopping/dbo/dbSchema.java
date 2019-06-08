package ru.micode.shopping.dbo;

/**
 * Created by Petr Gusarov on 15.03.18.
 */
public class dbSchema {

    /**
     * Таблица избранных рецептов
     */
    public static final class FavoriteTable {
        public static final String NAME = "favorite";

        public static final class Cols {
            public static final SqlCol UID = new SqlCol("uid", SqlCol.Type.TEXT).primary(true);
            public static final SqlCol TIME = new SqlCol("createDate", SqlCol.Type.INTEGER);
        }

        static String sqlCreate() {
            return "CREATE TABLE " + NAME + "("
                + Cols.UID.getSqlCreate() + ","
                + Cols.TIME.getSqlCreate() + ")";
        }
    }

    /**
     * Таблица списков
     */
    public static final class ShoppingTable {

        public static final String NAME = "shop_list";

        public static final class Cols {

            public static final SqlCol ID = new SqlCol("id", SqlCol.Type.INTEGER)
                .primary(true)
                .autoincrement(true);
            public static final SqlCol NAME = new SqlCol("name", SqlCol.Type.TEXT);
            public static final SqlCol EDIT_DATE = new SqlCol("editDate", SqlCol.Type.INTEGER);
            public static final SqlCol NEW = new SqlCol("isNew", SqlCol.Type.SMALLINT);
            public static final SqlCol AUTHOR = new SqlCol("author", SqlCol.Type.TEXT);
            public static final SqlCol COMMENT = new SqlCol("comment", SqlCol.Type.TEXT);
        }

        /**
         * Возвращает запрос на создание таблицы
         *
         * @return
         */
        static String sqlCreate() {
            return "CREATE TABLE " + NAME + "("
                + Cols.ID.getSqlCreate() + ","
                + Cols.NAME.getSqlCreate() + ","
                + Cols.COMMENT.getSqlCreate() + ","
                + Cols.AUTHOR.getSqlCreate() + ","
                + Cols.EDIT_DATE.getSqlCreate() + ","
                + Cols.NEW.getSqlCreate()
                + " )";
        }
    }

    /**
     * Таблица позиции в списке
     */
    public static final class BuyTable {
        public static final String NAME = "shop_buy";

        public static final class Cols {
            public static final SqlCol ID = new SqlCol("id", SqlCol.Type.INTEGER)
                .primary(true)
                .autoincrement(true);
            public static final SqlCol NAME = new SqlCol("name", SqlCol.Type.TEXT);
            public static final SqlCol AMOUNT = new SqlCol("amount", SqlCol.Type.INTEGER);
            public static final SqlCol MEASURE = new SqlCol("measure", SqlCol.Type.TEXT);
            public static final SqlCol DONE = new SqlCol("done", SqlCol.Type.SMALLINT);
            public static final SqlCol GROUP_NAME = new SqlCol("gr_name", SqlCol.Type.TEXT);
            public static final SqlCol GROUP_POS = new SqlCol("gr_pos", SqlCol.Type.INTEGER);
            public static final SqlCol LIST_ID = new SqlCol("list_id", SqlCol.Type.INTEGER).notNull(true);
        }

        /**
         * Возвращает запрос на создание таблицы
         *
         * @return
         */
        static String sqlCreate() {
            return "CREATE TABLE " + NAME + "("
                + Cols.ID.getSqlCreate() + ","
                + Cols.NAME.getSqlCreate() + ","
                + Cols.AMOUNT.getSqlCreate() + ","
                + Cols.MEASURE.getSqlCreate() + ","
                + Cols.DONE.getSqlCreate() + ","
                + Cols.GROUP_NAME.getSqlCreate() + ","
                + Cols.GROUP_POS.getSqlCreate() + ","
                + Cols.LIST_ID.getSqlCreate() + ","
                + " FOREIGN KEY(" + Cols.LIST_ID.getName() + ") REFERENCES " + ShoppingTable.NAME + "(" + ShoppingTable.Cols.ID.getName() + ")"
                + " )";
        }
    }

    /**
     * Таблица конактов
     */
    public static final class FriendTable {
        public static final String NAME = "shop_friend";

        public static final class Cols {
            public static final SqlCol ID = new SqlCol("id", SqlCol.Type.INTEGER)
                .primary(true)
                .autoincrement(true);
            public static final SqlCol UUID = new SqlCol("uuid", SqlCol.Type.TEXT);
            public static final SqlCol NAME = new SqlCol("name", SqlCol.Type.TEXT);
            public static final SqlCol VIEW_NAME = new SqlCol("view_name", SqlCol.Type.TEXT);
        }

        static String sqlCreate() {
            return "CREATE TABLE " + NAME + "("
                + Cols.ID.getSqlCreate() + ","
                + Cols.UUID.getSqlCreate() + ","
                + Cols.NAME.getSqlCreate() + ","
                + Cols.VIEW_NAME.getSqlCreate()
                + " )";
        }
    }

    /**
     * Таблица групп товара
     */
    public static final class GroupTable {
        public static final String NAME = "shop_group";

        public static final class Cols {
            public static final SqlCol ID = new SqlCol("id", SqlCol.Type.INTEGER)
                .primary(true)
                .autoincrement(true);
            public static final SqlCol NAME = new SqlCol("name", SqlCol.Type.TEXT);
            public static final SqlCol POSITION = new SqlCol("position", SqlCol.Type.INTEGER);
        }

        static String sqlCreate() {
            return "CREATE TABLE " + NAME + "("
                + Cols.ID.getSqlCreate() + ","
                + Cols.NAME.getSqlCreate() + ","
                + Cols.POSITION.getSqlCreate()
                + " )";
        }
    }

    /**
     * Общий класс колонки
     */
    public static class SqlCol {

        private final String name;
        private final Type type;
        private boolean primary;
        private boolean autoincrement;
        private boolean notNull;

        SqlCol(String name, Type type) {
            this.name = name;
            this.type = type;
        }

        public SqlCol primary(boolean primary) {
            this.primary = primary;
            return this;
        }

        public SqlCol autoincrement(boolean autoincrement) {
            this.autoincrement = autoincrement;
            return this;
        }

        public SqlCol notNull(boolean notNull) {
            this.notNull = notNull;
            return this;
        }

        public String getName() {
            return name;
        }

        public String whereEq() {
            return " " + getName() + " =?";
        }

        public Type getType() {
            return type;
        }

        public String getSqlCreate() {
            return String.format(" %s %s%s%s"
                , name, type.getType()
                , primary ? " PRIMARY KEY" : ""
                , autoincrement ? " AUTOINCREMENT" : ""
                , notNull ? " NOT NULL" : ""
            );
        }

        @Override
        public String toString() {
            return name;
        }

        /**
         * Тип данных в поле
         */
        public enum Type {
            /**
             * Пустое значение в таблице базы.
             */
            NULL("null"),
            /**
             * Целочисленное значение, хранящееся в 1, 2, 3, 4, 6 или 8 байтах,
             * в зависимости от величины самого значения
             */
            INTEGER("INTEGER"),
            /**
             * Числовое значение с плавающей точкой. Хранится в формате
             * 8-байтного числа IEEE с плавающей точкой.
             */
            REAL("REAL"),
            /**
             * Значение строки текста. Хранится с использованием кодировки
             * базы данных (UTF-8, UTF-16BE или UTF-16LE).
             */
            TEXT("TEXT"),
            /**
             * Значение булевых переменных
             */
            SMALLINT("SMALLINT"),
            /**
             * здесь можно хранить булевы значения, а также время и дату
             */
            NUMERIC("NUMERIC"),
            /**
             * Значение бинарных данных, хранящихся точно в том же виде,
             * в каком были введены.
             */
            BLOB("BLOB");

            private final String type;

            Type(String type) {
                this.type = type;
            }

            public String getType() {
                return type;
            }
        }
    }
}
