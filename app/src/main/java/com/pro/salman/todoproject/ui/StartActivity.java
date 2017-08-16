package com.pro.salman.todoproject.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.pro.salman.todoproject.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = "StartActivity";
    private EditText nickName,email;
    private ImageView mImageView,mCircleImageView;
    private Button nextBtn,closeBtn;
    private String ImageBytes = "";
    public static final String nickNameKey = "nickName";
    public static final String emailKey = "email";
    public static final String ImageKey = "Image";
    public static final String perfsFileName = "firstTime";
    private final String firstTimeKey = "firstTimeKey";
    private final int CAMERA_REQUEST = 111;
    private final int GALLERY_REQUEST = 222;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        nickName = (EditText)(findViewById(R.id.nickNameEditText));
        email = (EditText)(findViewById(R.id.emailEditText));
        mImageView = (ImageView)(findViewById(R.id.CameraImageView));
        nextBtn = (Button)(findViewById(R.id.nextBtnStartActivity));
        closeBtn = (Button)(findViewById(R.id.closeBtnStartActivity));
        mCircleImageView = (ImageView)(findViewById(R.id.circleImageView));
        setStatusBackground();
        setNextBtn();
        setCloseBtn();
        setImageView();
        checkFirstTime();
    }

    private void setStatusBackground()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void setNextBtn()
    {
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(nickName.getText().toString().isEmpty()&&email.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(),"content is empty ",Toast.LENGTH_LONG).show();
                else
                {
                    Intent i = new Intent(StartActivity.this,MainActivity.class);
                    firstTime();
                    startActivityForResult(i,12345);
                }

            }
        });
    }


    private void setCloseBtn()
    {
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finish();
                }


            }
        });
    }

    private void firstTime()
    {
        SharedPreferences.Editor perfs = getSharedPreferences(perfsFileName,0).edit();
        perfs.putBoolean(firstTimeKey,true);
        perfs.putString(nickNameKey,nickName.getText().toString());
        perfs.putString(emailKey,email.getText().toString().toLowerCase());

        if(!ImageBytes.isEmpty())
            perfs.putString(ImageKey,ImageBytes);

        perfs.apply();
        perfs.commit();
    }

    private void checkFirstTime()
    {
        SharedPreferences perfs = getSharedPreferences(perfsFileName,0);

        if(perfs!=null)
        {
            if(perfs.getBoolean(firstTimeKey,false))
            {
                Intent i = new Intent(StartActivity.this,MainActivity.class);
                startActivity(i);
            }
        }

    }

    private void setImageView()
    {
        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                builder.setTitle("Add profile picture");

                final ArrayAdapter<String> adapter = new ArrayAdapter<>(StartActivity.this,
                        android.R.layout.select_dialog_item);

                adapter.add("\tImage");
                adapter.add("\tTake picture");

                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String item = adapter.getItem(which);

                        if (item != null) {
                            if(item.equals("\tImage"))
                            {
                                Intent i = new Intent();
                                i.setType("image/*");
                                i.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(i,"Select Photo"),GALLERY_REQUEST);
                            }
                            else
                            {
                                Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(i,CAMERA_REQUEST);
                            }
                        }
                    }
                });

                builder.show();

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Toast.makeText(getApplicationContext(),"Running Camera",Toast.LENGTH_LONG).show();

            if(data!=null)
            {
                mImageView.setVisibility(View.INVISIBLE);
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                mCircleImageView.setImageBitmap(bitmap);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
                    byte [] b = out.toByteArray();
                    ImageBytes = Base64.encodeToString(b,Base64.DEFAULT);
                }

            }
        }
        else if(requestCode==GALLERY_REQUEST && resultCode == Activity.RESULT_OK)
        {
            if (data != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    mCircleImageView.setImageBitmap(bitmap);
                    mImageView.setVisibility(View.INVISIBLE);
                    mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    byte[] bytes = out.toByteArray();
                    ImageBytes = Base64.encodeToString(bytes, Base64.DEFAULT);

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onActivityResult: " + e.getMessage());
                }
            }
        }
        else if(requestCode == 12345 && resultCode == Activity.RESULT_OK)
        {
            Bundle b = data.getExtras();

            if(b!=null)
            {
                boolean finish = b.getBoolean("finish");

                if(finish)
                    finish();

            }
        }
    }

}
