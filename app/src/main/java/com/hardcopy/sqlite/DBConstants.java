package com.hardcopy.sqlite;

import android.provider.BaseColumns;

/**
 * 디비 상수
 *
 * @author user
 */
public class DBConstants {
	
    public static class DataEntry implements BaseColumns {
        public static final String TABLE_NAME = "table_data";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME_HUMIDITY = "humidity";
        public static final String COLUMN_NAME_TEMPERATURE = "temperature";
        public static final String COLUMN_NAME_HOUR = "hour";
        public static final String COLUMN_NAME_MINUTE = "minute";

    }

  
}
