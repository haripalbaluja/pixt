package com.example.root.pixt;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import AlgoStego.PVDColor;
import AlgoStego.StegoPVD;

public class MainActivity extends AppCompatActivity {
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private LinearLayout btnEncode;
    private LinearLayout btnDecode;
    private ImageView title;
    public String name;
    public String path;
    private ImageView encode;
    private ImageView decode;
    public Bitmap decodeImage;
    private String userChoosenTask;
    private Bitmap resultImage=null;
    public boolean returnOnly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        title = (ImageView) findViewById(R.id.title_text);
        encode = (ImageView) findViewById(R.id.encode);
        decode = (ImageView) findViewById(R.id.decode);

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
                returnOnly=false;
                selectImage();
            }
        });

        btnDecode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                returnOnly=true;
                galleryIntent(false);
            }
        });

    }
    private void doit(){
        StegoPVD pvd = new PVDColor();
        Object obj = pvd.stego(decodeImage, "", false);
        if(obj != null){
            String secretStego = (String) obj;
            StringBuilder theLast = new StringBuilder();
            int[] strChar = new int[8];
            for (int i = 0; i < secretStego.length(); )
            {
                strChar = new int[8];
                for (int j = 0; j < 8; j++)
                {
                    if(i < secretStego.length())
                        strChar[j] = Integer.parseInt(String.valueOf(secretStego.charAt(i++)));
                }

                int b = 0;
                int bin = 1;
                for (int k= strChar.length-1; k >= 0; k--){
                    b+= strChar[k] * bin;
                    bin = bin * 2;
                }
                theLast.append(String.valueOf((char)b));
            }
            Toast.makeText(getApplicationContext(), theLast.toString(), Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), "No text in this image!!", Toast.LENGTH_SHORT).show();
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
                        galleryIntent(false);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent()
    {
        File destination = new File(path);
        Uri tempURI = Uri.fromFile(destination);
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, tempURI);
        startActivityForResult(i, REQUEST_CAMERA);

    }

    private void galleryIntent(boolean returnOnly)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Image"),SELECT_FILE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                decodeImage = onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    @SuppressWarnings("deprecation")
    private Bitmap onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                Uri uri = data.getData();
                path=getRealPathFromURI_API19(getApplicationContext(), uri);
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        resultImage = bm;
        if(resultImage!=null){
                Intent encAct = new Intent(this.getBaseContext(), EncodeActivity.class);
                ByteArrayOutputStream _bs = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 50, _bs);
                encAct.putExtra("byteArray", _bs.toByteArray());
                encAct.putExtra("path", path);
                Toast.makeText(getApplicationContext(), path, Toast.LENGTH_SHORT).show();
            decodeImage=bm;
            if(!returnOnly)
                startActivity(encAct);
            else
                doit();


        }
        else
            Toast.makeText(getApplicationContext(), "Image selection unsuccessful. Try again.", Toast.LENGTH_SHORT).show();
        return bm;
    }
    private void onCaptureImageResult(Intent data) {

            Intent encAct = new Intent(this.getBaseContext(), EncodeActivity.class);
            encAct.putExtra("camera", true);
            encAct.putExtra("imgname", "temp.png");
            path = Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "temp.png";
            Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
            encAct.putExtra("path", path);
            startActivity(encAct);

    }

}
