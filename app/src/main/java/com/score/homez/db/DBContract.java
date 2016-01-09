package com.score.homez.db;

import android.provider.BaseColumns;

/**
 * Created by namal on 11/16/15.
 */
public class DBContract {
    public DBContract(){};

    public static abstract class Switch implements BaseColumns {
        public static final String TABLE_NAME = "switch";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_STATUS = "status";
    }

    public static abstract class User implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_STATUS = "status";
    }
}
