package com.pro.salman.todoproject.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBase extends SQLiteOpenHelper {

    final static private String databaseName = "Todo.db";
    final static private String tableName = "Todo";
    final static private String id = "id";
    final static private String Key = "key";
    final static private String list = "list";
    final static private String time = "time";
    final static private String date = "date";
    final static private String note = "note";
    final static private String imageAlarm="imageAlarm";
    final static private String imageReminder="imageReminder";
    final static private String imageNote="imageNote";
    final static private String imageKey="imageKey";
    final static private String isChecked = "isChecked";
    final static private String tableName2 = "Reminder";
    final static private String reminderTime = "reminderTime";
    final static private String reminderDate = "reminderDate";
    final static private String title = "title";
    final static private int version = 1;
    private static final String TAG = "DataBase";


    public DataBase(Context context) {
        super(context, databaseName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "create table "+tableName+"("+id+" integer primary key autoincrement ,"
                +Key+" text, "+list+" text,"+time+" text, "+date+" text,"+isChecked+" integer, " +
                ""+note+" text, "+imageAlarm+" integer, "+imageReminder+" integer, " +
                ""+imageNote+" integer, "+imageKey+" integer );";

        String queryTwo = "create table "+tableName2+"("+id+" integer primary key autoincrement ,"
                +Key+" text, "+reminderTime +" text, "+reminderDate+" text, "+title+" text);";


        Log.e(TAG, "onCreateQuery: "+query);
        Log.e(TAG, "onCreateQuery: "+queryTwo);

        db.execSQL(query);
        db.execSQL(queryTwo);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table "+tableName+" if exists");
        db.execSQL("drop table "+tableName2+" if exists");
        onCreate(db);

    }

    public void insertData(String key,String list1,String Time,String date1
            ,int isChecked1,String note1,int ImageAlarm,int ImageReminder,int ImageNote,int ImageKey)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Key,key);
        values.put(list,list1);
        values.put(time,Time);
        values.put(isChecked,isChecked1);
        values.put(note,note1);
        values.put(date,date1);
        values.put(imageAlarm,ImageAlarm);
        values.put(imageReminder,ImageReminder);
        values.put(imageKey,ImageKey);
        values.put(imageNote,ImageNote);



        long value = db.insert(tableName,null,values);

        if(value!=-1)
            Log.d(TAG, "insertData: Inserted");
        else
            Log.e(TAG, "insertData: No Inserted");

    }


    public void updateData(String key,int isChecked1,String id1)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(isChecked,isChecked1);

        int value = db.update(tableName,values,Key+"=? and "+id+"=?",new String[]{key,id1});

        if(value<1)
            Log.e(TAG, "updateData: Data Not Updated");
        else
            Log.d(TAG, "updateData: Data Updated");

    }

    public void updateImage(String key,String databaseId,int ImageAlarm,int ImageReminder,int ImageNote,int ImageKey)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(imageAlarm,ImageAlarm);
        values.put(imageReminder,ImageReminder);
        values.put(imageNote,ImageNote);
        values.put(imageKey,ImageKey);

        int value = db.update(tableName,values,Key+"=? and "+id+"=?",new String[]{key,databaseId});

        if(value<1)
            Log.e(TAG, "updateData: Data Not Updated");
        else
            Log.d(TAG, "updateData: Data Updated");

    }


    public void deleteData(String key,String listName)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String clause = " "+Key + " = ? and " + list + " = ?";
        int value = db.delete(tableName, clause, new String[]{key, listName});


        if(value<1)
            Log.e(TAG, "updateData: Data Not Deleted");
        else
            Log.d(TAG, "updateData: Data Deleted");
    }

    public void deleteWholeList(String listName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int value = db.delete(tableName,list+"=?",new String[]{listName});

        if(value<1)
            Log.e(TAG, "deleteWholeList: Data Not Deleted");
        else
            Log.d(TAG, "deleteWholeList: Data Deleted");


    }

    public Cursor getAllData(String key)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery("select * from "+tableName+" where "+Key+"=?",new String[]{key});
    }


    public void addReminder(String key,String time,String date,String title1)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(reminderTime,time);
        contentValues.put(reminderDate,date);
        contentValues.put(Key,key);
        contentValues.put(title,title1);

        long value = db.insert(tableName2,null,contentValues);

        if(value!=-1)
            Log.d(TAG, "insertData: Inserted");
        else
            Log.e(TAG, "insertData: No Inserted");

    }


    public void updateReminder(String key,String time,String date)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(reminderTime,time);
        values.put(reminderDate,date);

        int value = db.update(tableName2,values,Key+"=?",new String[]{key});

        if(value<1)
            Log.e(TAG, "updateData: Data Not Updated");
        else
            Log.d(TAG, "updateData: Data Updated");

    }

    public void deleteReminder(String key)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName2,Key+"=?",new String[]{key});
    }

    public Cursor getReminderData(String key)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+tableName2+" where "+Key+"=?",new String[]{key});

    }


    public void updateNote(String key,String Id,String Note,int ImageNote,int ImageKey)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(note,Note);
        values.put(imageNote,ImageNote);
        values.put(imageKey,ImageKey);

        int value = db.update(tableName,values,list+"=? and "+id+"=?",new String[]{key,Id});

        if(value<1)
            Log.e(TAG, "updateData: Data Not Updated");
        else
            Log.d(TAG, "updateData: Data Updated");
    }


}
