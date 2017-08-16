package com.pro.salman.todoproject.ui;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.pro.salman.todoproject.R;
import com.pro.salman.todoproject.model.Todo;
import com.pro.salman.todoproject.services.ReminderBackground;
import com.pro.salman.todoproject.services.ReminderReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ReminderActivity extends AppCompatActivity {

    private static final String TAG = "ReminderActivity";
    private ArrayList<HashMap<String,String>> mArrayList;
    private Todo todo;
    private CheckBox mCheckbox;
    private TextView mTitle,mAddTime,mAddDate,createdTodo;
    private ImageView timeImage,dateImage,deleteImage,closeTime,closeDate;
    private EditText mAddNote;
    private Calendar calender;
    private int id = -1;
    private int p = -1;
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;
    private String key="",oldTime="",oldDate="",reminderDate="",reminderTime="";
    private boolean closeTimeb = false,closeDateb = false;
    private ColorStateList oldColor;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_activity);
        Toolbar toolbar = (Toolbar)(findViewById(R.id.toolbarReminder));
        setSupportActionBar(toolbar);
        setTitle("To-Do");
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        mCheckbox = (CheckBox)(findViewById(R.id.ReminderFragmentCheckBox));
        mTitle = (TextView)(findViewById(R.id.textTitle));
        mAddTime = (TextView)(findViewById(R.id.addTime));
        mAddDate = (TextView)(findViewById(R.id.dueDate));
        createdTodo = (TextView)(findViewById(R.id.createdTodo));
        timeImage = (ImageView)(findViewById(R.id.alarmImage));
        dateImage = (ImageView)(findViewById(R.id.dateImage));
        deleteImage = (ImageView)(findViewById(R.id.deleteImage));
        closeTime = (ImageView)(findViewById(R.id.closeImageRemind));
        closeDate = (ImageView)(findViewById(R.id.closeDateImage));
        mAddNote = (EditText)(findViewById(R.id.addNoteEditText));



        todo = new Todo(this);
        mArrayList = new ArrayList<>();
        mAlarmManager = (AlarmManager)(getSystemService(ALARM_SERVICE));
        oldColor = mAddTime.getTextColors();
        calender = Calendar.getInstance();
        oldDate = getString(R.string.add_due_date);
        oldTime = getString(R.string.remind_me);



        getDataFromFragment();
        setReminderText();
        setStatusBarColor();
        setTime();
        setDate();
        setDeleteImage();
        closeReminderImage();
        checkBoxOnClick();
        setEditText();
    }


    private boolean isServiceRunning(Class<?> Service)
    {
        ActivityManager manager = (ActivityManager)(getSystemService(ACTIVITY_SERVICE));

        for(ActivityManager.RunningServiceInfo info : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if(Service.getName().equals(info.service.getClassName()))
                return true;
        }
        return false;
    }

    public void setEditText()
    {
        mAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ReminderActivity.this,AddNote.class);
                intent.putExtra("note",mAddNote.getText().toString());
                intent.putExtra("keyTitle",mTitle.getText().toString());
                intent.putExtra("id",String.valueOf(id));
                startActivityForResult(intent,111);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: "+String.valueOf(requestCode));
        Log.d(TAG, "onActivityResult: "+String.valueOf(resultCode));


        if(requestCode == 111 && resultCode == RESULT_OK)
        {
            String note = data.getStringExtra("note");
            mAddNote.setText(note);
            Log.d(TAG, "onActivityResult: Running");
        }
        else
            Log.d(TAG, "onActivityResult: Not Running");

    }

    private void closeReminderImage()
    {
        closeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isServiceRunning(ReminderBackground.class))
                stopReminder();

                dateImage.setImageDrawable(ContextCompat.getDrawable(ReminderActivity.this,R.drawable.ic_date_range_black_24dp));
                mAddDate.setText(getString(R.string.add_due_date));
                mAddDate.setTextColor(oldColor);
                closeDate.setVisibility(View.INVISIBLE);

            }
        });

        closeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isServiceRunning(ReminderBackground.class))
                stopReminder();


                timeImage.setImageDrawable(ContextCompat.getDrawable(ReminderActivity.this,R.drawable.ic_alarm_black_24dp));
                mAddTime.setText(getString(R.string.add_due_date));
                mAddTime.setTextColor(oldColor);
                closeTime.setVisibility(View.INVISIBLE);

            }
        });

    }


    private void setDeleteImage()
    {
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(ReminderActivity.this);
                dialog.setTitle("Delete");
                dialog.setMessage("Are you sure you want to delete \""+mTitle.getText().toString()+"\" ?");

                dialog.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        todo.deleteReminder(key);
                        stopReminder();
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_LONG).show();
                    }
                });

                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });
    }

    private void setReminderText() {


        if(mArrayList.size()!=0)
        {

            for(int i = 0;i<mArrayList.size();i++)
            {
                HashMap<String,String> map = mArrayList.get(i);
                if(map.get("title").equals(mTitle.getText().toString()))
                {
                    p = i;

                    break;
                }
            }

            if(p!=-1)
            {
                HashMap<String,String> map = mArrayList.get(p);

                oldTime = map.get("RTime");
                oldDate = map.get("RDate");
                if(oldTime!=null)
                {
                    if(!oldTime.isEmpty()&&!oldTime.equals(getString(R.string.remind_me)))
                    {
                        mAddTime.setText(oldTime);
                        mAddTime.setTextColor(ContextCompat.getColor(this,R.color.purple));
                    }
                }

                if(oldDate!=null)
                {
                    if(!oldDate.isEmpty()&&!oldTime.equals(getString(R.string.add_due_date)))
                    {
                        mAddDate.setText(oldDate);
                        mAddDate.setTextColor(ContextCompat.getColor(this,R.color.purple));
                    }
                }

            }

        }
    }

    private void startReminder()
    {
        if(id!=-1)
        {
            Intent intent = new Intent(ReminderActivity.this, ReminderReceiver.class);
            Toast.makeText(getApplicationContext(),"Reminder : "+id+" set",Toast.LENGTH_LONG).show();
            intent.putExtra("id",id);
            intent.putExtra("title",mTitle.getText().toString());
            mPendingIntent = PendingIntent.getBroadcast(this,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);


            mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calender.getTimeInMillis(),AlarmManager.INTERVAL_DAY,mPendingIntent);

        }
    }

    private void stopReminder()
    {
        mAlarmManager.cancel(mPendingIntent);
    }


    private void setTime()
    {
        mAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timeAlertDialog();
                closeTime.setVisibility(View.VISIBLE);
                closeTimeb = true;

            }
        });

        timeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timeAlertDialog();
                closeTime.setVisibility(View.VISIBLE);
                closeTimeb = true;

            }
        });

    }

    private void setDate()
    {
        mAddDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dateAlertDialog();
                closeDate.setVisibility(View.VISIBLE);
                closeDateb = true;
            }
        });

        dateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateAlertDialog();
                closeDate.setVisibility(View.VISIBLE);
                closeDateb = true;
            }
        });

    }

    private void dateAlertDialog()
    {

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                String monthArr [] = new String[]{"Jan","Feb","Mar","Apr","May","June","July","Aug","Sep","Oct","Nov","Dec"};


                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
                Date date = cal.getTime();
                String dayOftheWeek = dateFormat.format(date);

                reminderDate = "Due "+dayOftheWeek+", "+monthArr[month]+" "+String.valueOf(year);
                mAddDate.setText(reminderDate);
                mAddDate.setTextColor(ContextCompat.getColor(ReminderActivity.this,R.color.purple));
                dateImage.setImageDrawable(ContextCompat.getDrawable(ReminderActivity.this,R.drawable.ic_date_range_purple_24dp));
                calender.set(year,month,dayOfMonth);
            }
        },calender.get(Calendar.YEAR),calender.get(Calendar.MONTH),calender.get(Calendar.DAY_OF_MONTH));

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mAddDate.getTextColors()==oldColor)
                    closeDate.setVisibility(View.INVISIBLE);
            }
        });

        dialog.show();

    }

    private void timeAlertDialog()
    {

        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                int h;
                if(hourOfDay>12)
                    h = hourOfDay-12;
                else
                    h = hourOfDay;

                if(minute<10)
                    reminderTime = "Remind me at "+String.valueOf(h)+":0"+minute;
                else
                    reminderTime = "Remind me at "+String.valueOf(h)+":"+minute;



                if(hourOfDay<12)
                    reminderTime+=" AM";
                else
                    reminderTime+=" PM";

                if (reminderTime.isEmpty())
                Toast.makeText(getApplicationContext(),reminderTime,Toast.LENGTH_LONG).show();

                mAddTime.setText(reminderTime);
                mAddTime.setTextColor(ContextCompat.getColor(ReminderActivity.this,R.color.purple));
                calender.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calender.set(Calendar.MINUTE,minute);
                timeImage.setImageDrawable(ContextCompat.getDrawable(ReminderActivity.this,R.drawable.ic_alarm_purple_24dp));

            }
        }, calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE), false);

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mAddDate.getTextColors()==oldColor)
                    closeTime.setVisibility(View.INVISIBLE);
            }
        });

        dialog.show();

    }

    private void checkBoxOnClick()
    {
        mCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCheckbox.isChecked())
                {
                    mTitle.setPaintFlags(mTitle.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
                    todo.updateData(key,1,String.valueOf(id));
                }
                else
                {
                    mTitle.setPaintFlags(mTitle.getPaintFlags()&~(Paint.STRIKE_THRU_TEXT_FLAG));
                    todo.updateData(key,0,String.valueOf(id));
                }
            }
        });
    }



    private void getDataFromFragment()
    {
        Bundle b = getIntent().getExtras();

        if(b!=null)
        {

            key = b.getString("key");
            mArrayList = todo.getReminderData(b.getString("key"));
            id = Integer.parseInt(b.getString("id"));

            mTitle.setText(b.getString("title"));
            createdTodo.setText(b.getString("date"));
            String isChecked = b.getString("isChecked");

            if(isChecked!=null)
            {
                if(isChecked.equals("1"))
                {
                    mCheckbox.setChecked(true);
                    mTitle.setPaintFlags(mTitle.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
                }

            }

            String note = b.getString("note");

            if(note!=null)
            {
                if(!note.isEmpty())
                    mAddNote.setText(note);

            }
        }
    }

    private void setStatusBarColor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(ReminderActivity.this,R.color.grey2));
        }
    }


    @Override
    public void onBackPressed() {

        setConditionForServices();
        updateReminderData_updateImageState();

        super.onBackPressed();
    }

    private void setConditionForServices()
    {

        boolean check = true;

        if(closeTimeb&&closeDateb)
        {
            todo.addReminder(key,reminderDate,reminderTime,mTitle.getText().toString());
            startReminder();
            check = false;
        }

        if(closeDateb&&check)
        {

            todo.addReminder(key,reminderDate,"",mTitle.getText().toString());
            startReminder();
            check = false;
        }

        if(closeTimeb&&check)
        {
            todo.addReminder(key,"",reminderTime,mTitle.getText().toString());
            startReminder();
        }

    }

    private void updateReminderData_updateImageState()
    {
        boolean date = false,time = false , note = false;

        if(!oldDate.equals(mAddDate.getText().toString()))
        {
            oldDate = mAddDate.getText().toString();
            date = true;
            Log.d(TAG, "updateReminderData_updateImageState: Running Old Date");
        }


        if(!oldTime.equals(mAddTime.getText().toString()))
        {
            oldTime= mAddTime.getText().toString();
            Log.d(TAG, "updateReminderData_updateImageState: Running Old Time");
            time = true;
        }

        if(!mAddNote.getText().toString().isEmpty())
            note = true;


        if (date && time && note)
        {
            todo.updateImage(key,String.valueOf(id),1,1,1,1);
        }
        else if(date && time && !note)
        {
            todo.updateImage(key,String.valueOf(id),1,1,0,1);
        }
        else if(!date && time && note)
        {
            todo.updateImage(key,String.valueOf(id),1,0,1,1);
        }
        else if(date && !time && note)
        {
            todo.updateImage(key,String.valueOf(id),0,1,1,1);
        }
        else if(date && !time && !note)
        {
            todo.updateImage(key,String.valueOf(id),0,1,0,1);
        }
        else if(!date && time && !note)
        {
            todo.updateImage(key,String.valueOf(id),1,0,0,1);
        }
        else if(!date && !time && note)
        {
            todo.updateImage(key,String.valueOf(id),0,0,1,1);
        }
        else
            Log.d(TAG, "updateReminderData_updateImageState: False");  


        if(date&&time)
            todo.updateReminder(key,oldTime,oldDate);
        else if(date&&!time)
            todo.updateReminder(key,oldTime,oldDate);
        else if(time&&!date)
            todo.updateReminder(key,oldTime,oldDate);
        else
            Log.d(TAG, "Update Reminder: Not Running");

    }




    @Override
    protected void onStop() {
        super.onStop();

        setConditionForServices();
        updateReminderData_updateImageState();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
