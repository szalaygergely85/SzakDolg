package com.example.szakdolg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileHandling {

    public static void saveImageFile(String uID, Bitmap pic, Context c) throws IOException {

        double height = pic.getHeight();
        double width = pic.getWidth();
        int newWidth= (int) Math.round(width/height*200);
        int newHeight= (int) Math.round(height/width*200);

        Bitmap resized;
        if (height>width){
            resized = Bitmap.createScaledBitmap(pic,newWidth , 200, true);
        }else{
            resized = Bitmap.createScaledBitmap(pic, 200, newHeight, true);
        }



        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, 60, bytearrayoutputstream);
        File path = new File( c.getFilesDir() + "/Pictures/");
        if(!path.exists()){
            path.mkdir();
        }
        File file = new File( path +"/"+uID+".jpg");
        try
        {
            file.createNewFile();

            FileOutputStream fileoutputstream = new FileOutputStream(file);

            fileoutputstream.write(bytearrayoutputstream.toByteArray());

            fileoutputstream.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static Uri getUri(String uID, Context c){
        Uri uri;
        File file  = new File(c.getFilesDir() + "/Pictures/" + uID+".jpg");
        if(file.exists()){
            uri= Uri.fromFile(file);
        }else {
            uri = null;
        }
        return  uri;
    }



}
