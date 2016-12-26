package com.hardcopy.sqlite;

import org.json.JSONException;
import org.json.JSONObject;

import com.hardcopy.sqlite.DBConstants.DataEntry;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 디비 조작을 위한 메소드들
 *
 * @author user
 */
public class DBConnector {
	private static final String SQL_SELECT_ALL_DATA = "SELECT * FROM "
			+ DataEntry.TABLE_NAME;


	final private SQLiteDatabase mDB;

	private static DBConnector mInstance;

	private DBConnector(Context context) {
		DBHelper dbHelper = new DBHelper(context);
		mDB = dbHelper.getWritableDatabase();
	}

	public static DBConnector getInstance(Context context) {
		if (mInstance == null) {// 있는지 체크 없으면
			mInstance = new DBConnector(context); // 생성한뒤
		}

		return mInstance;// 성성자를 넘긴다.
	}

	public void addDataInDB(String hour, String minute, String humidity, String temperature) {


			
			ContentValues cv = new ContentValues();

			cv.put(DataEntry.COLUMN_NAME_HUMIDITY, humidity);
			cv.put(DataEntry.COLUMN_NAME_TEMPERATURE, temperature);
			cv.put(DataEntry.COLUMN_NAME_HOUR, hour);
			cv.put(DataEntry.COLUMN_NAME_MINUTE,
					minute);


			mDB.insert(DataEntry.TABLE_NAME, null, cv);

	}

	public void disconnectDB() {
		this.mDB.close();
	}

	public Cursor getAllData() {
		return mDB.rawQuery(SQL_SELECT_ALL_DATA, null);
	}

	public void removeAllDataInDB() {
		// TODO Auto-generated method stub
		mDB.delete(DataEntry.TABLE_NAME,
				null,
				null);
	}

}