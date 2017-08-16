package com.pro.salman.todoproject.adapter;


import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pro.salman.todoproject.R;
import com.pro.salman.todoproject.model.Todo;

import java.util.ArrayList;
import java.util.HashMap;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.Holder>{

    private LayoutInflater mLayoutInflater;
    private Typeface mTypeface;
    private ArrayList<HashMap<String,String>> mArrayList;
    private OnClickRecycler mOnClick;
    private static final String TAG = "TodoAdapter";


    public interface OnClickRecycler
    {
        void onClickRecycler(int p);
        void onClickCheckBox(int p,CheckBox checkBox,TextView title);
    }

    public void setOnClick (OnClickRecycler ocr)
    {
        mOnClick = ocr;
    }

    public TodoAdapter(Context context, ArrayList<HashMap<String, String>> arrayList, Typeface typeface ) {
        mTypeface = typeface;
        mArrayList = arrayList;
        mLayoutInflater = LayoutInflater.from(context);
    }



    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(mLayoutInflater.inflate(R.layout.recycler_todo_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        HashMap<String,String> data = mArrayList.get(position);

        String isChecked = data.get("isChecked");

        if(isChecked!=null)
        {
            if(isChecked.equals("1"))
            {
                holder.checkBox.setChecked(true);
                holder.title.setPaintFlags(holder.title.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            }
            else
            {
                holder.checkBox.setChecked(false);
                holder.title.setPaintFlags(holder.title.getPaintFlags()&(~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }

        holder.title.setText(data.get("title"));
        holder.time.setText(data.get("time"));



        setImageState(data,holder.alarmImage,holder.reminderImage,holder.noteImage);

    }

    private void setImageState(HashMap<String, String> data,
                               ImageView alarmImage, ImageView reminderImage, ImageView noteImage) {



        String imageKey = data.get("imageKey");
        String alarm= data.get("imageAlarm");
        String reminder= data.get("imageReminder");
        String note= data.get("imageNote");


        if(imageKey!=null) {


            if (!imageKey.equals("-1")) {

                if (alarm.equals("1") && reminder.equals("1") && note.equals("1")) {
                    alarmImage.setVisibility(View.VISIBLE);
                    reminderImage.setVisibility(View.VISIBLE);
                    noteImage.setVisibility(View.VISIBLE);
                } else if (alarm.equals("1") && reminder.equals("1") && note.equals("0")) {
                    alarmImage.setVisibility(View.VISIBLE);
                    reminderImage.setVisibility(View.VISIBLE);
                    noteImage.setVisibility(View.INVISIBLE);
                } else if (alarm.equals("0") && reminder.equals("1") && note.equals("1")) {
                    alarmImage.setVisibility(View.INVISIBLE);
                    reminderImage.setVisibility(View.VISIBLE);
                    noteImage.setVisibility(View.VISIBLE);

                } else if (alarm.equals("1") && reminder.equals("0") && note.equals("1")) {
                    alarmImage.setVisibility(View.VISIBLE);
                    reminderImage.setVisibility(View.INVISIBLE);
                    noteImage.setVisibility(View.VISIBLE);
                } else if (alarm.equals("1") && reminder.equals("0") && note.equals("0")) {
                    alarmImage.setVisibility(View.VISIBLE);
                    reminderImage.setVisibility(View.INVISIBLE);
                    noteImage.setVisibility(View.INVISIBLE);
                } else if (alarm.equals("0") && reminder.equals("1") && note.equals("0")) {
                    alarmImage.setVisibility(View.INVISIBLE);
                    reminderImage.setVisibility(View.VISIBLE);
                    noteImage.setVisibility(View.INVISIBLE);
                } else if (alarm.equals("0") && reminder.equals("0") && note.equals("1")) {
                    alarmImage.setVisibility(View.INVISIBLE);
                    reminderImage.setVisibility(View.INVISIBLE);
                    noteImage.setVisibility(View.VISIBLE);
                }
            } else
                Log.d(TAG, "setImageState: Not Running");
        }

    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView title,time;
        CheckBox checkBox;
        ImageView alarmImage,reminderImage,noteImage;
        public Holder(View itemView) {
            super(itemView);

            title = (TextView)(itemView.findViewById(R.id.todoTitle));
            time = (TextView)(itemView.findViewById(R.id.timeTodo));
            checkBox = (CheckBox)(itemView.findViewById(R.id.fragmentCheckBox));
            alarmImage = (ImageView)(itemView.findViewById(R.id.recyclerAlarm));
            reminderImage = (ImageView)(itemView.findViewById(R.id.recyclerReminder));
            noteImage = (ImageView)(itemView.findViewById(R.id.recyclerNote));

            title.setTypeface(mTypeface);
            time.setTypeface(mTypeface);
            View v = itemView.findViewById(R.id.recyclerTodoLayout);
            v.setOnClickListener(this);
            checkBox.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if(v == checkBox)
                mOnClick.onClickCheckBox(getAdapterPosition(),checkBox,title);
            else
                mOnClick.onClickRecycler(getAdapterPosition());

        }
    }
}
