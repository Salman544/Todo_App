package com.pro.salman.todoproject.ui;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pro.salman.todoproject.R;
import com.pro.salman.todoproject.adapter.TodoAdapter;
import com.pro.salman.todoproject.model.Todo;
import com.pro.salman.todoproject.services.ReminderReceiver;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class TodoFragment extends Fragment implements TodoAdapter.OnClickRecycler {

    private View mView;
    private RecyclerView mRecyclerView;
    private TodoAdapter mAdapter;
    private Button addBtn,addEditBtn;
    private TextView mTextView,isEmptyTextView;
    private EditText mEditText;
    private CheckBox mCheckBox;
    private ArrayList<HashMap<String,String>> mArrayList;
    public static String Key = "key";
    private String keyName;
    private Todo mTodo;
    private String filename="saveTitle";
    private static final String TAG = "TodoFragment";


    public TodoFragment() {
        // Required empty public constructor
    }



    public static TodoFragment newInstance(String key)
    {
        Bundle args = new Bundle();
        args.putString(Key,key);

        TodoFragment fragment = new TodoFragment();
        fragment.setArguments(args);
        return fragment;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_todo, container, false);
        mView = v;

        return v;
    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mRecyclerView = (RecyclerView)(mView.findViewById(R.id.recyclerTodo));
        addBtn = (Button)(mView.findViewById(R.id.addTodoBottomBtn));
        addEditBtn = (Button)(mView.findViewById(R.id.addEditTextToDoBtn));
        mTextView = (TextView)(mView.findViewById(R.id.textView2));
        isEmptyTextView = (TextView)(mView.findViewById(R.id.isEmptyTextViewFragment));
        mEditText = (EditText)(mView.findViewById(R.id.editTextAddToDo));
        mCheckBox = (CheckBox)(mView.findViewById(R.id.checkbox));




        mTodo = new Todo(getActivity());
        mArrayList = new ArrayList<>();

        Bundle args = getArguments();

        if(args!=null)
        {
            keyName = args.getString(Key);
            saveKeySharedPerfs(keyName);
            mArrayList = mTodo.getData(keyName);

            if(mArrayList.size()!=0)
            displayRecyclerAdapter();

        }

        if(mArrayList.size() == 0)
            isEmptyTextView.setVisibility(View.VISIBLE);
        else
            isEmptyTextView.setVisibility(View.INVISIBLE);



        addData();
        onEditDone();
        addEditBtnClick();
        onTextViewClick();






    }

    private void onTextViewClick() {

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addBtn.setVisibility(View.INVISIBLE);
                mTextView.setVisibility(View.INVISIBLE);
                mEditText.setVisibility(View.VISIBLE);
                addEditBtn.setVisibility(View.VISIBLE);
                mCheckBox.setVisibility(View.VISIBLE);
                mEditText.requestFocus();


            }
        });



    }

    private void addData() {

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addBtn.setVisibility(View.INVISIBLE);
                mTextView.setVisibility(View.INVISIBLE);
                mEditText.setVisibility(View.VISIBLE);
                addEditBtn.setVisibility(View.VISIBLE);
                mCheckBox.setVisibility(View.VISIBLE);
                mEditText.requestFocus();

            }
        });

    }

    private void addEditBtnClick() {

        addEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mEditText.getText().toString().isEmpty())
                    Toast.makeText(getContext(),"content should not be empty",Toast.LENGTH_LONG).show();
                else
                {
                    createCore();
                }

            }
        });

    }


    private void onEditDone()
    {
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    InputMethodManager inputMethod = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethod.toggleSoftInput(InputMethodManager.RESULT_HIDDEN,0);
                    Toast.makeText(getContext(),"working",Toast.LENGTH_LONG).show();

                    mTextView.setVisibility(View.VISIBLE);
                    addBtn.setVisibility(View.VISIBLE);
                    addEditBtn.setVisibility(View.INVISIBLE);
                    mEditText.setVisibility(View.INVISIBLE);
                    mCheckBox.setVisibility(View.INVISIBLE);

                    if(!mEditText.getText().toString().isEmpty())
                    {
                        createCore();
                    }

                }
                else
                    Toast.makeText(getContext(),"not working",Toast.LENGTH_LONG).show();

                return true;
            }
        });
    }




    private void createCore()
    {
        String getTime = getTime();
        String getDate = getDate();
        mTodo.addDataToDataBase(keyName,mEditText.getText().toString(),getTime, getDate);
        mArrayList = mTodo.getData(keyName);
        displayRecyclerAdapter();
        isEmptyTextView.setVisibility(View.INVISIBLE);
        mEditText.setText("");
        Log.d(TAG, "createCore: Running");

    }

    private String getDate() {

        String dayOftheWeek [] = DateFormatSymbols.getInstance().getShortWeekdays();
        String monthArr [] = new String[]{"Jan","Feb","Mar","Apr","May","June","July","Aug","Sep","Oct","Nov","Dec"};



        Calendar calender = Calendar.getInstance();

        int day = calender.get(Calendar.DAY_OF_WEEK);
        int month = calender.get(Calendar.MONTH);
        int date = calender.get(Calendar.DATE);


        return "Created "+dayOftheWeek[day]+", "+monthArr[month]+" "+String.valueOf(date);
    }


    private void displayRecyclerAdapter() {

        mAdapter = new TodoAdapter(getContext(),mArrayList,Typeface.createFromAsset(getActivity().getAssets(),"fonts/Roboto-Regular.ttf"));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnClick(this);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecorator);

        ItemTouchHelper helper = new ItemTouchHelper(callBack());
        helper.attachToRecyclerView(mRecyclerView);

    }

    private ItemTouchHelper.Callback callBack() {

        return new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {


                   Log.d(TAG, "onSwiped: Running");
                   final int p = viewHolder.getAdapterPosition();
                   final HashMap<String,String> map = mArrayList.get(p);
                   final String listName = map.get("title");

                   final AlertDialog.Builder builder;
                   if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                       builder = new AlertDialog.Builder(getContext(),android.R.style.Theme_Material_Dialog_Alert);
                   }
                   else
                       builder = new AlertDialog.Builder(getContext());

                   builder.setTitle("Delete "+listName);
                   builder.setMessage("Are you sure you want to delete \""+listName+"\" ?");

                   builder.setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {

                           mArrayList.remove(p);
                           mAdapter.notifyItemRemoved(p);
                           mTodo.deleteData(keyName,listName);

                           if(mArrayList.size() == 0)
                               isEmptyTextView.setVisibility(View.VISIBLE);

                           Toast.makeText(getContext(),"deleted",Toast.LENGTH_LONG).show();
                       }
                   });

                   builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                           mAdapter.notifyItemChanged(p);
                       }
                   });

                   builder.show();

                String isTime = map.get("imageAlarm");
                String isReminder = map.get("imageReminder");

                if(isTime.equals("1")&&isReminder.equals("1"))
                    stopAlarmManager(1);
                else if(isTime.equals("0")&&isReminder.equals("1"))
                    stopAlarmManager(1);
                else if(isTime.equals("1")&&isReminder.equals("0"))
                    stopAlarmManager(1);
                else
                    stopAlarmManager(0);

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {


                Bitmap icon;
                Paint p = new Paint();
                View item = viewHolder.itemView;

                float height = (float)item.getBottom() - (float)item.getTop();
                float width = height / 3;

                if(dX<0)
                {
                    Drawable d = getResources().getDrawable(R.drawable.ic_delete_black_24dp);
                    icon = Bitmap.createBitmap(d.getIntrinsicWidth(),d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(icon);
                    d.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
                    d.draw(canvas);

                    p.setColor(Color.parseColor("#D32F2F"));
                    RectF background = new RectF((float) item.getRight() + dX,
                            (float) item.getTop(),(float) item.getRight(), (float) item.getBottom());
                    c.drawRect(background,p);

                    RectF icon_dest = new RectF((float) item.getRight() - 2*width ,
                            (float) item.getTop() + width,
                            (float) item.getRight() - width,(float)item.getBottom() - width);
                    c.drawBitmap(icon,null,icon_dest,p);
                }


                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }
        };


    }

    private String getTime() {

        String time;
        Calendar c = Calendar.getInstance();

        int hours = c.get(Calendar.HOUR_OF_DAY);
        int min =   c.get(Calendar.MINUTE);
        if(hours>12)
            hours = hours-12;

        if(min<10)
            time = String.valueOf(hours)+":0"+String.valueOf(min);
        else
            time = String.valueOf(hours)+":"+String.valueOf(min);


        int am_pm = c.get(Calendar.AM_PM);
        String amPm;

        if(am_pm == 0)
            amPm="am";
        else
            amPm="pm";


        return time+" "+amPm;

    }

    @Override
    public void onClickRecycler(int p) {


        HashMap<String,String> hasMap = mArrayList.get(p);



        Intent intent = new Intent(getContext(),ReminderActivity.class);
        intent.putExtra("title",hasMap.get("title"));
        intent.putExtra("key",keyName);
        intent.putExtra("id",hasMap.get("id"));
        intent.putExtra("date",hasMap.get("date"));
        intent.putExtra("isChecked",hasMap.get("isChecked"));
        intent.putExtra("note",hasMap.get("note"));
        getActivity().startActivity(intent);

    }


    private void stopAlarmManager(int id)
    {
        if(id!=0)
        {
            Intent i = new Intent(getContext(), ReminderReceiver.class);
            AlarmManager manager = (AlarmManager)(getContext().getSystemService(Context.ALARM_SERVICE));
            PendingIntent p = PendingIntent.getBroadcast(getContext(),id,i,PendingIntent.FLAG_UPDATE_CURRENT);
            manager.cancel(p);
        }
    }


    @Override
    public void onClickCheckBox(int p,CheckBox checkBox,TextView title)
    {
        HashMap<String,String> map = mArrayList.get(p);

        if(checkBox.isChecked())
        {
            title.setPaintFlags(title.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
            MediaPlayer player = MediaPlayer.create(getContext(),R.raw.ding);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);

            if(player.isPlaying())
                player.stop();
            else
                player.start();


            mTodo.updateData(keyName,1,map.get("id"));
            mArrayList = mTodo.getData(keyName);

        }
        else
        {
            title.setPaintFlags(title.getPaintFlags()&(~Paint.STRIKE_THRU_TEXT_FLAG));
            mTodo.updateData(keyName,0,map.get("id"));
            mArrayList = mTodo.getData(keyName);

        }
    }

    private void saveKeySharedPerfs(String key)
    {
        SharedPreferences.Editor perfs = getContext().getSharedPreferences(filename,0).edit();
        perfs.putString("key",key);
        perfs.apply();
        perfs.commit();
    }

    private String getSaveKey()
    {
        SharedPreferences perfs = getContext().getSharedPreferences(filename,0);
        String key = null;
        if(perfs!=null)
        {
            key = perfs.getString("key",null);
            Log.d(TAG, "getSaveTitle: "+key);
        }
        return key;
    }


    @Override
    public void onResume() {
        super.onResume();

        String key = getSaveKey();
        if(key!=null)
        {
            mArrayList = mTodo.getData(key);
            displayRecyclerAdapter();
        }
    }







}
