package com.pro.salman.todoproject.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pro.salman.todoproject.R;

import java.util.ArrayList;


public class NavAdapter extends RecyclerView.Adapter<NavAdapter.Holder> {

    private LayoutInflater mLayoutInflater;
    private ArrayList<String> mArrayList;
    private Typeface mTypeface;
    private NavRecyclerOnClick mClick;

    public interface NavRecyclerOnClick
    {
        void NavRecyclerOnClick(int p);
    }

    public void onRecyclerClick (NavRecyclerOnClick click) {
        mClick = click;
    }

    public NavAdapter(Context context, ArrayList<String> arrayList, Typeface t) {
        mArrayList = arrayList;
        mTypeface = t;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new Holder(mLayoutInflater.inflate(R.layout.recycler_nav_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {


        holder.title.setText(mArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView title;
        public Holder(View itemView) {
            super(itemView);

            title  = (TextView)(itemView.findViewById(R.id.navTodoTitle));
            title.setTypeface(mTypeface);
            View v = itemView.findViewById(R.id.recyclerNavLayout);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClick.NavRecyclerOnClick(getAdapterPosition());
        }
    }

}
