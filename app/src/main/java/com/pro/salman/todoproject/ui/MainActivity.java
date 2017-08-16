package com.pro.salman.todoproject.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pro.salman.todoproject.R;
import com.pro.salman.todoproject.adapter.NavAdapter;
import com.pro.salman.todoproject.database.DataBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements NavAdapter.NavRecyclerOnClick {

    private DrawerLayout mDrawerLayout;
    private TextView mTextView;
    private ArrayList<String> mArrayList = new ArrayList<>();
    private NavAdapter mAdapter;
    private TodoFragment fragment;
    private DataBase database;
    final private String sharedName = "arrayList";
    final private String stringKey = "stringKey";
    final private String arrayKey = "arrayKey";
    private String value = "";
    private static final String TAG = "MainActivity";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)(findViewById(R.id.toolbar));
        setSupportActionBar(toolbar);
        database = new DataBase(this);

        mDrawerLayout = (DrawerLayout)(findViewById(R.id.drawerLayout));
        mTextView = (TextView)(findViewById(R.id.isEmptyTextView));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,
                R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        setTitle("Todo App");

        getSaveArrayData();
        setNavigationView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.addBtn)
        {
            final Dialog dialog;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                dialog = new Dialog(this,android.R.style.Theme_Material_Light_Dialog);
            }
            else
                dialog = new Dialog(this);

            dialog.setContentView(R.layout.custom_dialog);
            dialog.setCancelable(true);
            dialog.show();
            dialog.setTitle("Title");

            final EditText editText = (EditText)(dialog.findViewById(R.id.editText));
            Button addBtn = (Button)(dialog.findViewById(R.id.saveBtn));
            Button cancelBtn = (Button)(dialog.findViewById(R.id.cancelBtn));

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(editText.getText().toString().isEmpty())
                        Toast.makeText(getApplicationContext(),"content should not be empty",Toast.LENGTH_LONG).show();
                    else
                    {
                        String name = editText.getText().toString();
                        boolean check = false;
//                        String toUpper = name.substring(0, (name.length()-name.length())+1);
//                        String toLower = name.substring(1, name.length());
//                        toUpper = toUpper.toUpperCase();
//                        toLower = toLower.toLowerCase();
//                        name = toUpper+toLower;

                        for(int i =0;i<mArrayList.size();i++)
                        {
                            if(name.equals(mArrayList.get(i)))
                            {
                                check = true;
                                break;
                            }
                            else
                                check = false;
                        }

                        if(!check)
                        {
                            mArrayList.add(name);
                            showRecyclerAdapter();
                            showFragment(name);

                            dialog.dismiss();
                            value = name;
                            setTitle(value);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"To-do list name  is already avaliable",Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                }
            });


            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFragment(String name) {

        fragment = TodoFragment.newInstance(name);
        mTextView.setVisibility(View.INVISIBLE);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer,fragment)
                .commit();

        removeFragment();



        Log.d(TAG, "showFragment: Running");


        mDrawerLayout.closeDrawer(GravityCompat.START);

    }

    private void showRecyclerAdapter() {

        mAdapter = new NavAdapter(this,mArrayList, Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf"));
        RecyclerView rec = (RecyclerView)(findViewById(R.id.recyclerNav));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rec.setLayoutManager(linearLayoutManager);
        rec.setAdapter(mAdapter);
        mAdapter.onRecyclerClick(this);

        if(mArrayList.size() == 3)
            linearLayoutManager.scrollToPosition(mArrayList.size()+1);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rec.getContext(),
                DividerItemDecoration.VERTICAL);
        rec.addItemDecoration(dividerItemDecoration);


        ItemTouchHelper helper = new ItemTouchHelper(callBack());
        helper.attachToRecyclerView(rec);

    }

    private ItemTouchHelper.Callback callBack() {

        return new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                final int p = viewHolder.getAdapterPosition();
                final String value = mArrayList.get(p);
                mDrawerLayout.closeDrawer(GravityCompat.START);

                AlertDialog.Builder builder;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(MainActivity.this,android.R.style.Theme_Material_Dialog_Alert);
                }
                else
                    builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Delete "+value);
                builder.setMessage("Are you sure you want to delete\" "+value+"\" ?");


                builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.notifyItemRemoved(p);
                        database.deleteWholeList(value);


                        if(mArrayList.size() == 1)
                        {
                            mTextView.setVisibility(View.VISIBLE);
                            setTitle("To-do App");
                        }
                        else
                        {
                            if(p==0)
                            {
                                showFragment(mArrayList.get(1));
                                setTitle(mArrayList.get(1));
                            }
                            else
                            {
                                showFragment(mArrayList.get(p-1));
                                setTitle(mArrayList.get(p-1));
                            }
                        }

                        removeFragment();
                        mArrayList.remove(p);
                        Toast.makeText(getApplicationContext(),"deleted",Toast.LENGTH_LONG).show();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mAdapter.notifyItemChanged(p);
                    }
                });


                builder.show();

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {


                Bitmap icon;
                View item = viewHolder.itemView;
                Paint p = new Paint();

                float height = (float)item.getBottom() - (float)item.getTop();
                float width = height / 3;

                if(dX>0)
                {

                    Drawable d = ContextCompat.getDrawable(MainActivity.this,R.drawable.ic_delete_black_24dp);
                    icon = Bitmap.createBitmap(d.getIntrinsicWidth(),d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(icon);
                    d.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
                    d.draw(canvas);

                    p.setColor(Color.parseColor("#D32F2F"));
                    RectF f = new RectF((float)item.getLeft(),(float)item.getTop(),dX,(float)item.getBottom());
                    c.drawRect(f,p);

                    RectF icon_dest = new RectF((float) item.getLeft() + width ,
                            (float) item.getTop() + width,(float) item.getLeft()+ 2*width,(float)item.getBottom() - width);
                    c.drawBitmap(icon,null,icon_dest,p);

                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }
        };

    }

    @Override
    public void NavRecyclerOnClick(int p) {

        showFragment(mArrayList.get(p));

        removeFragment();


        value = mArrayList.get(p);
        setTitle(value);

        Log.d(TAG, "NavRecyclerOnClick: "+String.valueOf(value));
    }

    private void removeFragment()
    {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        if(fragment!=null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        saveArrayList();

    }

    private void saveArrayList() {

        SharedPreferences.Editor shared = getSharedPreferences(sharedName,0).edit();

        Set<String> set = new HashSet<>();
        set.addAll(mArrayList);
        shared.putStringSet(arrayKey,set);
        if(!value.isEmpty())
        shared.putString(stringKey,value);

        shared.apply();
        shared.commit();


        Log.d(TAG, "saveArrayList: Running");


    }

    private void getSaveArrayData()
    {
        SharedPreferences perfs = getSharedPreferences(sharedName,0);

        if(perfs!=null)
        {
            Set<String> set = perfs.getStringSet(arrayKey,null);
            String listName = perfs.getString(stringKey,null);
            Log.d(TAG, "getSaveArrayData: ListName "+listName);
            if(set!=null)
            {
                mArrayList.addAll(set);
                showRecyclerAdapter();

                if(listName!=null)
                    showFragment(listName);

                Log.d(TAG, "getSaveArrayData: Running");
                setTitle(listName);
                
            }

        }
    }

    private void setNavigationView()
    {
        NavigationView nav = (NavigationView)(findViewById(R.id.nav));
        View header = nav.getHeaderView(0);

        TextView name = (TextView)(header.findViewById(R.id.navName));
        TextView email = (TextView)(header.findViewById(R.id.navEmail));
        ImageView img = (ImageView)(header.findViewById(R.id.navImage));

        SharedPreferences preferences = getSharedPreferences(StartActivity.perfsFileName,0);

        if(preferences!=null)
        {
            String n = preferences.getString(StartActivity.nickNameKey,null);
            String e = preferences.getString(StartActivity.emailKey,null);
            String i = preferences.getString(StartActivity.ImageKey,null);

            assert n!=null;
            name.setText(n);

            assert e!=null;
            email.setText(e);

            if(i!=null)
            {
                byte [] bytes;
                bytes = Base64.decode(i.getBytes(),Base64.DEFAULT);
                Bitmap map = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                img.setImageBitmap(map);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"not running navs",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveArrayList();


    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent();
        i.putExtra("finish",true);
        setResult(RESULT_OK,i);
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        getSupportFragmentManager().putFragment(outState,stringKey,fragment);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        getSupportFragmentManager().getFragment(savedInstanceState,stringKey);

    }

}
