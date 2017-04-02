/**
 *  Copyright (C) 2013  : Arthur Paliden
 *  techsupport@hideinplainsight.ca
 *
 *  This file is part of the HIPS - Hide in Plain Sight  - Steganography application.
 *
 *  HIPS is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  HIPS is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with HIPS; if not, visit there web site at :
 *  http://www.fsf.org/ 
 *  or write to them at Free Software Foundation, Inc., 
 *  51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  If you feel this program is useful and beneficial then please visit our web site:
 *  
 *  http://www.hideinplainsight.ca
 *  
 *  and make a donation to help support the site and the developers in their ongoing work.
 *
 */
package com.example.root.pixt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.os.Environment;

public class Picture extends Object
{
	public static final int MAX_ANDROID_VALUE = 1000;
	
	Bitmap 	bitmap = null;
	Bitmap initialbitmap = null;

	public Picture()
	{
	}
	
	public void openForInsert( String imageFile, boolean resize )
	{
    	BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inJustDecodeBounds = true;
    	BitmapFactory.decodeFile(imageFile, options);
    	int imageHeight = options.outHeight;
    	int imageWidth = options.outWidth;
    	
    	options = new BitmapFactory.Options();
    	if( resize )
    	{
			if( imageWidth > MAX_ANDROID_VALUE || imageHeight > MAX_ANDROID_VALUE )
		    {
		        final int heightRatio = (int)Math.ceil((double) imageHeight / (double) MAX_ANDROID_VALUE);
		        final int widthRatio = (int)Math.ceil((double) imageWidth / (double) MAX_ANDROID_VALUE);
		        options.inSampleSize =  (heightRatio < widthRatio ? heightRatio : widthRatio); // + 1;
		        options.inJustDecodeBounds = false;
		    }
    	}
    	
    	options.inDither = false;
    	options.inPreferredConfig = Config.ARGB_8888;
    	
    	bitmap = null;

      	initialbitmap = BitmapFactory.decodeFile( imageFile, options);    	
    	bitmap = convertToMutable( initialbitmap );
    	
    	initialbitmap = null;
  	
	}
	
	public void openForExtract( String imageFile )
	{
    	BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inDither = false;
    	options.inPreferredConfig = Config.ARGB_8888;
      	bitmap = BitmapFactory.decodeFile( imageFile, options);
	}
	
	public void save( File alteredFile )
		throws Exception
	{
		FileOutputStream out = new FileOutputStream( alteredFile );
		bitmap.compress( Bitmap.CompressFormat.PNG, 100, out );
		out.flush();
		out.close();
	}
	
	public void setPixel( int tgtX, int tgtY, int newRgb  )
	{
		bitmap.setPixel( tgtX, tgtY, newRgb  );
	}
	
	public int getPixel( int x, int y )
	{
		return( bitmap.getPixel( x, y ) );
	}

	public int getWidth()
	{
		return( bitmap.getWidth() );
	}
	
	public int getHeight()
	{
		return( bitmap.getHeight() );
	}

	public static Bitmap convertToMutable(Bitmap imgIn)
	{
		//Do this on disk to try to prevent Out Of Memory Errors
	    try
	    {
	        //this is the file going to use temporally to save the bytes. 
	        // This file will not be a image, it will store the raw image data.
	        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

	        //Open an RandomAccessFile
	        //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
	        //into AndroidManifest.xml file
	        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

	        // get the width and height of the source bitmap.
	        int width = imgIn.getWidth();
	        int height = imgIn.getHeight();
	        Config type = imgIn.getConfig();

	        //Copy the byte to the file
	        //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
	        FileChannel channel = randomAccessFile.getChannel();
	        MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0, imgIn.getRowBytes()*height);
	        imgIn.copyPixelsToBuffer(map);
	        //recycle the source bitmap, this will be no longer used.
	        imgIn.recycle();
	        System.gc();// try to force the bytes from the imgIn to be released

	        //Create a new bitmap to load the bitmap again. Probably the memory will be available. 
	        imgIn = Bitmap.createBitmap(width, height, type);
	        map.position(0);
	        //load it back from temporary 
	        imgIn.copyPixelsFromBuffer(map);
	        //close the temporary file and channel , then delete that also
	        channel.close();
	        randomAccessFile.close();

	        // delete the temp file
	        file.delete();

	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } 

	    return imgIn;
	}


}