package com.example.root.pixt;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

public class MainActivity extends AppCompatActivity {
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private LinearLayout btnEncode;
    private LinearLayout btnDecode;
    private ImageView ivImage;
    private String userChoosenTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        ImageView title = (ImageView) findViewById(R.id.title_text);
        ImageView encode = (ImageView) findViewById(R.id.encode);
        ImageView decode = (ImageView) findViewById(R.id.decode);

        TextDrawable title_im = TextDrawable.builder().buildRect("Pixt", ColorGenerator.MATERIAL.getRandomColor());
        TextDrawable encode_im = TextDrawable.builder().buildRoundRect("E", ColorGenerator.MATERIAL.getRandomColor(), 100);
        TextDrawable decode_im = TextDrawable.builder().buildRoundRect("D", ColorGenerator.MATERIAL.getRandomColor(), 100);

        title.setImageDrawable(title_im);
        encode.setImageDrawable(encode_im);
        decode.setImageDrawable(decode_im);


        btnEncode = (LinearLayout) findViewById(R.id.encode_btn);
        btnDecode = (LinearLayout) findViewById(R.id.decode_btn);

        btnEncode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

    }

    private void selectImage() {
        final CharSequence[] items = { "Capture New", "Choose from Gallery",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select an Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(MainActivity.this);
                if (items[item].equals("Capture New")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Gallery")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }




}
