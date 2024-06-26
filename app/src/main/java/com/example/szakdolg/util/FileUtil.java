package com.example.szakdolg.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FileUtil {

    public static final String USER_AGENT_FIREFOX = "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.0.10) Gecko/2009042316 Firefox/3.0.10 (.NET CLR 3.5.30729)";
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

    public static void saveFileFromUri(Uri fileUri, File file) {


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(fileUri.toString())
                .header("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.0.10) Gecko/2009042316 Firefox/3.0.10 (.NET CLR 3.5.30729)")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                try (InputStream inputStream = response.body().byteStream();
                     FileOutputStream outputStream = new FileOutputStream(file)) {
                    byte[] buffer = new byte[8 * 1024];
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, read);
                    }
                    outputStream.flush();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static String getFileExtensionFromUri(Uri uri) {
        String path = uri.getPath();
        if (path == null) {
            return null;
        }

        int lastDotIndex = path.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return null;
        }

        return path.substring(lastDotIndex + 1);
    }
}