package com.pro.salman.todoproject.ui;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import com.pro.salman.todoproject.R;
import com.pro.salman.todoproject.model.Todo;

public class AddNote extends AppCompatActivity {

    private EditText mEditText;
    private String key;
    private String id;
    private static final String TAG = "AddNote";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        Toolbar toolbar = (Toolbar)(findViewById(R.id.toolbarNoteActivity));
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();

        if(bar!=null)
        bar.setDisplayHomeAsUpEnabled(true);

        mEditText = (EditText)(findViewById(R.id.addNoteActivity));
        setTitle("");
        getDataFromReminderActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    private void getDataFromReminderActivity() {

        Bundle b = getIntent().getExtras();
        if(b!=null)
        {
            String note = b.getString("note");
            key = b.getString("keyTitle");
            id = b.getString("id");
            if(note!=null&&!note.isEmpty())
            {
                mEditText.setText(note);
                mEditText.setSelection(note.length());
            }
        }
    }

    private void saveNoteData()
    {
        Todo todo = new Todo(this);
        todo.updateNote(key,id,mEditText.getText().toString());

    }

    @Override
    public void onBackPressed() {

        saveNoteData();

        Intent i = new Intent();
        i.putExtra("note",mEditText.getText().toString());

        setResult(RESULT_OK,i);
        super.onBackPressed();

    }

    @Override
    protected void onStop() {
        super.onStop();
        saveNoteData();
    }
}
