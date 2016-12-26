package com.hardcopy.sqlite;


import com.hardcopy.sqlite.DBConstants.DataEntry;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



/**
 * 디비 helper
 *
 * @author user
 */
class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "myTemperatureHumidity.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ACTIVITY_DATA_TABLE = "CREATE TABLE "
            + DataEntry.TABLE_NAME + " (" + DataEntry._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT " + COMMA_SEP
            + DataEntry.COLUMN_NAME_HUMIDITY + TEXT_TYPE + COMMA_SEP
            + DataEntry.COLUMN_NAME_TEMPERATURE + TEXT_TYPE + COMMA_SEP
            + DataEntry.COLUMN_NAME_HOUR + TEXT_TYPE + COMMA_SEP
            + DataEntry.COLUMN_NAME_MINUTE + TEXT_TYPE + " )";

   

    private static final String SQL_DELETE_ACTIVITY_DATA_TABLE = "DROP TABLE IF EXISTS "
            + DataEntry.TABLE_NAME;

    

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_ACTIVITY_DATA_TABLE);
       

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 기존 테이블 삭제
        db.execSQL(SQL_DELETE_ACTIVITY_DATA_TABLE);
       
        // 새로 DB 생성
        onCreate(db);

    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}