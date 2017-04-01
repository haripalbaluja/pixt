package com.example.root.pixt;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

public class MainActivity extends AppCompatActivity {

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


    }
}
