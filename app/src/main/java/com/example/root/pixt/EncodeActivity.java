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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import AlgoStego.PVDColor;
import AlgoStego.StegoPVD;

public class EncodeActivity extends AppCompatActivity {
    public Bitmap img;
    public String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_encode);
        ImageView _imv= (ImageView) findViewById(R.id.user_image);
        path = getIntent().getStringExtra("path");
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
        try
        {
            TextView message = (TextView) findViewById(R.id.message) ;
            Steganogrator ane = new Steganogrator();
            String str = message.getText().toString();
            str = ane.insert( path, str, new StegProfile(), true );
            File source = new File(path);

            String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pixt/pixt"+System.currentTimeMillis()+".png";
            InputStream in = null;
            OutputStream out = null;
            try {

                //create output directory if it doesn't exist
                File dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pixt");
                if (!dir.exists())
                {
                    dir.mkdirs();
                }


                in = new FileInputStream(str);
                out = new FileOutputStream(destinationPath);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;

                // write the output file (You have now copied the file)
                out.flush();
                out.close();
                out = null;

            }  catch (FileNotFoundException fnfe1) {
                Log.e("tag", fnfe1.getMessage());
            }
            catch (Exception e) {
                Log.e("tag", e.getMessage());
            }

        }
        catch( OutOfMemoryError oome )
        {
            Toast.makeText(getApplication(), "Selected image is too large!", Toast.LENGTH_SHORT).show();
            oome.printStackTrace();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
}
