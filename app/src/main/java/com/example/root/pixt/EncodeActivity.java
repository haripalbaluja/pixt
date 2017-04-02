package com.example.root.pixt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import AlgoStego.PVDColor;
import AlgoStego.StegoPVD;

public class EncodeActivity extends AppCompatActivity {
    public Bitmap img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_encode);
        ImageView _imv= (ImageView) findViewById(R.id.user_image);
        if(getIntent().hasExtra("imgname")){
            File destination = new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "temp.png");
            Bitmap myBitmap = BitmapFactory.decodeFile(destination.getAbsolutePath());
            img = myBitmap;
            _imv.setImageBitmap(myBitmap);
        }
        else{
        if(getIntent().hasExtra("byteArray")) {

            Bitmap _bitmap = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);
            _imv.setImageBitmap(_bitmap);
            img = _bitmap;
        }}
    }
    public void cancel(View v){
        this.onBackPressed();
    }

    public void encode(View v){

    }
}
