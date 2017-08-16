package com.pro.salman.todoproject.model;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.pro.salman.todoproject.database.DataBase;

import java.util.ArrayList;
import java.util.HashMap;

public class Todo {

    private DataBase mDataBase;
    private Context mContext;
    private static final String TAG = "Todo";
    public Todo(Context context) {
        mContext = context;
        mDataBase = new DataBase(context);
    }


    public ArrayList<HashMap<String,String>> getData(String key)
    {
        ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();

        Cursor cursor = mDataBase.getAllData(key);

        if(cursor.getCount() == 0)
            Toast.makeText(mContext,"no data",Toast.LENGTH_LONG).show();
        else
        {
            while (cursor.moveToNext())
            {
                HashMap<String,String> map = new HashMap<>();
                map.put("id",String.valueOf(cursor.getInt(0)));
                map.put("title",cursor.getString(2));
                map.put("time",cursor.getString(3));
                map.put("date",cursor.getString(4));
                map.put("isChecked",String.valueOf(cursor.getInt(5)));
                map.put("note",cursor.getString(6));
                map.put("imageAlarm",String.valueOf(cursor.getInt(7)));
                map.put("imageReminder",String.valueOf(cursor.getInt(8)));
                map.put("imageNote",String.valueOf(cursor.getInt(9)));
                map.put("imageKey",String.valueOf(cursor.getInt(10)));



                arrayList.add(map);

            }
        }

        Log.e(TAG, "getData: "+arrayList.toString());
        return arrayList;
    }

    public void addDataToDataBase(String key,String listName,String time,String date)
    {
        mDataBase.insertData(key,listName,time,date,0,"",0,0,0,-1);
    }


    public void deleteData(String key,String listName)
    {
        mDataBase.deleteData(key,listName);
    }

    public void updateData(String key,int isChecked,String id)
    {
        mDataBase.updateData(key,isChecked,id);
    }

    public void addReminder(String key,String day,String time,String title)
    {
        mDataBase.addReminder(key,time,day,title);
    }

    public void deleteReminder(String key)
    {
        mDataBase.deleteReminder(key);
    }

    public void updateReminder(String key,String time,String date)
    {
        mDataBase.updateReminder(key, time, date);
    }

    public void updateImage(String key,String databaseId,int ImageAlarm,int ImageReminder,int ImageNote,int ImageKey)
    {
        mDataBase.updateImage(key,databaseId,ImageAlarm,ImageReminder,ImageNote,ImageKey);
    }


    public ArrayList<HashMap<String,String>> getReminderData(String key)
    {
        ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
        Cursor cursor = mDataBase.getReminderData(key);

        if(cursor.getCount() == 0)
            Toast.makeText(mContext,"set reminder",Toast.LENGTH_LONG).show();
        else
        {
            while (cursor.moveToNext())
            {
                HashMap<String,String> map = new HashMap<>();
                map.put("RTime",cursor.getString(2));
                map.put("RDate",cursor.getString(3));
                map.put("title",cursor.getString(4));
                arrayList.add(map);

            }
        }

        return arrayList;
    }


    public void updateNote(String keyTitle,String id,String note)
    {
        mDataBase.updateNote(keyTitle,id,note,1,1);
    }

}
