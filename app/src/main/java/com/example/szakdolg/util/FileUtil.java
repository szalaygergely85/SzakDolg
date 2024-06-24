package com.example.szakdolg.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class FileUtil {

        public static void writeHashMapToFile(HashMap<String, String> hashMap, File file) {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);

                // Write HashMap to ObjectOutputStream
                oos.writeObject(hashMap);

                // Close streams
                oos.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public static void writeStringToFile(String data, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            // Write HashMap to ObjectOutputStream
            oos.writeObject(data);

            // Close streams
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static HashMap<String, String> readHashMapFromFile(File file) {
        HashMap<String, String> hashMap = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            // Read HashMap from ObjectInputStream
            hashMap = (HashMap<String, String>) ois.readObject();

            // Close streams
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    public static String readStringFromFile(File file) {
        String data = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            data = (String) ois.readObject();

            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void saveImageFile(String uID, Bitmap pic, Context c) throws IOException {

        double height = pic.getHeight();
        double width = pic.getWidth();
        int newWidth = (int) Math.round(width / height * 200);
        int newHeight = (int) Math.round(height / width * 200);

        Bitmap resized;
        if (height > width) {
            resized = Bitmap.createScaledBitmap(pic, newWidth, 200, true);
        } else {
            resized = Bitmap.createScaledBitmap(pic, 200, newHeight, true);
        }
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, 60, bytearrayoutputstream);
        File path = new File(c.getFilesDir() + "/Pictures/");
        if (!path.exists()) {
            path.mkdir();
        }
        File file = new File(path + "/" + uID + ".jpg");
        try {
            file.createNewFile();
            FileOutputStream fileoutputstream = new FileOutputStream(file);
            fileoutputstream.write(bytearrayoutputstream.toByteArray());
            fileoutputstream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Uri getUri(String uID, Context c) {
        Uri uri;
        File file = new File(c.getFilesDir() + "/Pictures/" + uID + ".jpg");
        if (file.exists()) {
            uri = Uri.fromFile(file);
        } else {
            uri = null;
        }
        return uri;
    }

}
