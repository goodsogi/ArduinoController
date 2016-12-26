package com.hardcopy.arduinocontroller;

import com.hardcopy.arduinocontroller.R;
import com.hardcopy.arduinocontroller.Constants;
import com.hardcopy.arduinocontroller.SerialConnector;
import com.hardcopy.sqlite.DBConnector;
import com.hardcopy.sqlite.DBConstants;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import im.dacer.androidcharts.LineView;

public class ArduinoControllerActivity extends Activity {

    private Context mContext = null;
    private ActivityHandler mHandler = null;

    private SerialListener mListener = null;
    private SerialConnector mSerialConn = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // System
        mContext = getApplicationContext();

        // Layouts
        setContentView(R.layout.activity_arduino_controller);


        init();

        addButtonListener();

    }

    private void addButtonListener() {
        Button chartButton = (Button) findViewById(R.id.chartButton);
        chartButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                drawChart();
            }
        });

        chartButton.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                removeChart();
                restartActivity();

                return false;
            }
        });
    }

    private void restartActivity() {
        Toast.makeText(ArduinoControllerActivity.this, "모든 데이터를 삭제했습니다", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {
                                          Intent mStartActivity = new Intent(ArduinoControllerActivity.this, ArduinoControllerActivity.class);
                                          int mPendingIntentId = 123456;
                                          PendingIntent mPendingIntent = PendingIntent.getActivity(ArduinoControllerActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                                          AlarmManager mgr = (AlarmManager) ArduinoControllerActivity.this.getSystemService(Context.ALARM_SERVICE);
                                          mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                                          System.exit(0);
                                      }
                                  }, 3000
        );

    }

    private void removeChart() {
        DBConnector.getInstance(this).removeAllDataInDB();

    }

    private void drawChart() {

        if (hasNoData()) {
            return;
        }

        final LineView lineView = (LineView) findViewById(R.id.line_view);

        lineView.setBottomTextList(getBottomTextList());
        lineView.setDrawDotLine(true);
        lineView.setShowPopup(LineView.SHOW_POPUPS_NONE);
        lineView.setDataList(getDataList());

    }

    private boolean hasNoData() {
        Cursor c = DBConnector.getInstance(this)
                .getAllData();


        boolean hasNoData = c.getCount() == 0;
        c.close();

        return hasNoData;
    }

    private ArrayList<ArrayList<Integer>> getDataList() {

        ArrayList<ArrayList<Integer>> dataLists = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> humidities = new ArrayList<Integer>();
        ArrayList<Integer> temperatures = new ArrayList<Integer>();
        Cursor cursor = DBConnector.getInstance(this)
                .getAllData();

        cursor.moveToFirst();


        while (!cursor.isAfterLast()) {
            String humidity = cursor.getString(cursor
                    .getColumnIndex(DBConstants.DataEntry.COLUMN_NAME_HUMIDITY));
            String temperature = cursor.getString(cursor
                    .getColumnIndex(DBConstants.DataEntry.COLUMN_NAME_TEMPERATURE));

            humidities.add((int) Double.parseDouble(humidity));
            temperatures.add((int) Double.parseDouble(temperature));


            cursor.moveToNext();
        }
        cursor.close();

        dataLists.add(humidities);
        dataLists.add(temperatures);

        return dataLists;
    }

    private ArrayList<String> getBottomTextList() {
        ArrayList<String> texts = new ArrayList<String>();
        Cursor cursor = DBConnector.getInstance(this)
                .getAllData();

        cursor.moveToFirst();


        while (!cursor.isAfterLast()) {
            String hour = cursor.getString(cursor
                    .getColumnIndex(DBConstants.DataEntry.COLUMN_NAME_HOUR));
            String minute = cursor.getString(cursor
                    .getColumnIndex(DBConstants.DataEntry.COLUMN_NAME_MINUTE));


            String text = hour + ":" + minute;
            texts.add(text);

            cursor.moveToNext();
        }
        cursor.close();


        return texts;
    }

    private void init() {
        // Initialize
        mListener = new SerialListener();
        mHandler = new ActivityHandler();

        // Initialize Serial connector and starts Serial monitoring thread.
        mSerialConn = new SerialConnector(mContext, mListener, mHandler);
        mSerialConn.initialize();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSerialConn.finalize();
    }


    public class SerialListener {

        public void onReceive(int msg, int arg0, int arg1, String arg2, Object arg3) {

            switch (msg) {
                case Constants.MSG_DEVICD_INFO:
                    //mTextLog.append(arg2);
                    break;
                case Constants.MSG_DEVICE_COUNT:
                    //mTextLog.append(Integer.toString(arg0) + " device(s) found \n");
                    break;
                case Constants.MSG_READ_DATA_COUNT:
                    //mTextLog.append(Integer.toString(arg0) + " buffer received \n");
                    break;
                case Constants.MSG_READ_DATA:
                    // if (arg3 != null) {
                    //mTextInfo.setText((String)arg3);
                    //mTextLog.append((String)arg3);
                    //mTextLog.append("\n");
                    //}
                    break;
                case Constants.MSG_SERIAL_ERROR:
                    //mTextLog.append(arg2);
                    break;
                case Constants.MSG_FATAL_ERROR_FINISH_APP:
                    finish();
                    break;
            }
        }
    }

    private void handleData(String data) {

        String[] tokens = data.split("-");


        if (tokens[0].contains("data")) {
            saveInDB(tokens[1], tokens[2]);


        }
    }

    private void saveInDB(String humidity, String temperature) {

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);

        DBConnector.getInstance(this).addDataInDB(String.valueOf(hour), String.valueOf(minute), humidity, temperature);


    }


    public class ActivityHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {


            switch (msg.what) {
                case Constants.MSG_DEVICD_INFO:
                    //mTextLog.append((String)msg.obj);
                    break;
                case Constants.MSG_DEVICE_COUNT:
                    //mTextLog.append(Integer.toString(msg.arg1) + " device(s) found \n");
                    break;
                case Constants.MSG_READ_DATA_COUNT:
                    if (msg.obj != null) {
                        handleData((String) msg.obj);
                    }
                    //mTextLog.append(((String)msg.obj) + "\n");
                    break;
                case Constants.MSG_READ_DATA:
                    //if (msg.obj != null) {

                    //mTextInfo.setText((String)msg.obj);
                    //mTextLog.append((String)msg.obj);
                    //mTextLog.append("\n");
                    //}
                    break;
                case Constants.MSG_SERIAL_ERROR:
                    //mTextLog.append((String)msg.obj);
                    break;
            }
        }
    }


}
